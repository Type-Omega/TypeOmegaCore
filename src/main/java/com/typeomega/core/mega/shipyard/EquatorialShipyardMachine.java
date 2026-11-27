package com.typeomega.core.mega.shipyard;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import com.typeomega.core.mega.TypeOmegaMegastructures;
import com.typeomega.core.mega.common.MegaMachine;
import com.typeomega.core.mega.common.MegaRecipeEngine;
import com.typeomega.core.mega.system.StarPhysics;
import com.typeomega.core.mega.system.StarSystems;

import java.util.List;

/**
 * Controller of the Equatorial Shipyard (D-446 A6): smaller twin-ring sibling of the Assembly Yard where the inner
 * ring harvests stellar energy (20% EU reduction) and the outer ring assembles Assembler and Assembly Line recipes.
 */
public class EquatorialShipyardMachine extends MegaMachine {

    public static final String SHAPE = "equatorial_shipyard";

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            EquatorialShipyardMachine.class, MegaMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    private int active = EquatorialShipyardPhysics.SLIPWAYS_PHASE_2;
    @Persisted
    @DescSynced
    private boolean rush;
    @Persisted
    private double progressTicks;
    @Persisted
    @DescSynced
    private long crafted;
    @DescSynced
    private String recipe = "";
    @DescSynced
    private String blocked = "";
    /**
     * What the screen shows: [0] parallel, [1] speed, [2] active slipways, [3] slipways built, [4] crafts this
     * second, [5] EU this second, [6] luminosity (L☉), [7] star temperature, [8] working (1/0), [9] drawn scale.
     */
    @DescSynced
    private double[] view = new double[10];
    private final MegaRecipeEngine.Progress progress = new MegaRecipeEngine.Progress();

    public EquatorialShipyardMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public String shapeName() {
        return SHAPE;
    }

    @Override
    public boolean inPlace() {
        return anyDimensionForTests || getLevel() != null && StarSystems.isSystem(getLevel().dimension());
    }

    public int active() {
        return active;
    }

    public boolean rush() {
        return rush;
    }

    public long crafted() {
        return crafted;
    }

    public String recipe() {
        return recipe;
    }

    public String blocked() {
        return blocked;
    }

    public double[] view() {
        return view;
    }

    /** Half the EU for a recipe that makes a megastructure controller. */
    public static double discount(GTRecipe recipe) {
        for (var out : RecipeHelper.getOutputItems(recipe))
            if (TypeOmegaMegastructures.isController(out)) return EquatorialShipyardPhysics.MEGA_DISCOUNT;
        return 1;
    }

    static List<GTRecipeType> types() {
        return List.of(GTRecipeTypes.ASSEMBLY_LINE_RECIPES, GTRecipeTypes.ASSEMBLER_RECIPES);
    }

    // ------------------------------------------------------------------ effects

    @Override
    public float fxLevel(String effect) {
        boolean on = view[8] > 0;
        // EN: Inner ring harvests starlight once phase 2 is built; outer gantries weld when phase 3 is active.
        // PT: O anel interno colhe luz estelar na fase 2+; os pórticos externos soldam com a fase 3 ativa.
        // ZH: 第 2 阶段建成后内环采集星光；第 3 阶段激活后外环龙门架进行焊接。
        if (effect.startsWith("harvest")) return phases >= 2 ? (on ? 1.0f : 0.45f) : 0;
        if (effect.startsWith("weld")) return on && phases >= 3 ? (rush ? 1.4f : 1.0f) : 0;
        return 1;
    }

    @Override
    public float fxTemperature(String effect) {
        return effect.equals("star") && view[7] > 0 ? (float) view[7] : 0;
    }

    @Override
    public float fxScale(String effect) {
        return effect.equals("star") && view[9] > 0 ? (float) view[9] : 1;
    }

    // ------------------------------------------------------------------ work

    @Override
    protected boolean megaSecond(ServerLevel level) {
        var star = star();
        double light = star == null ? 0 : StarPhysics.luminosity(star.kind, star.mass);
        int built = EquatorialShipyardPhysics.slipways(phases);
        int open = Math.min(active, built);
        int parallel = EquatorialShipyardPhysics.parallel(open);
        double speed = EquatorialShipyardPhysics.speed(light, rush);
        String idleReason = built == 0 ? "inner_ring" : (open == 0 ? "slipways" : "");
        MegaRecipeEngine.Result result = new MegaRecipeEngine.Result(0, 0, "", idleReason);
        if (isWorkingEnabled() && open > 0) {
            progress.ticks = progressTicks;
            result = MegaRecipeEngine.second(this, level, types(),
                    new MegaRecipeEngine.Limits(parallel, speed, EquatorialShipyardPhysics.euFactor(phases, rush),
                            r -> true, MegaRecipeEngine.MAX_BATCHES, EquatorialShipyardMachine::discount),
                    progress);
            progressTicks = progress.ticks;
            crafted += result.crafts();
        }
        recipe = result.recipe();
        blocked = result.blocked();
        boolean working = result.busy();
        double temp = star == null ? 0 : StarPhysics.temperature(star.kind, star.mass);
        float scale = star == null ? 1 : StarPhysics.drawnScale(star.kind, star.mass);
        view = new double[] { parallel, speed, open, built, result.crafts(), result.eu(), light, temp,
                working ? 1 : 0, scale };
        return working || isWorkingEnabled();
    }

    public void setActive(int slipways) {
        active = Math.max(0, Math.min(EquatorialShipyardPhysics.SLIPWAYS_PHASE_3, slipways));
    }

    public void setRush(boolean on) {
        rush = on;
    }

    // ------------------------------------------------------------------ screen

    @Override
    public ModularUI createUI(Player player) {
        return EquatorialShipyardScreen.create(this, player);
    }

    @Override
    public void handleDisplayClick(String data, ClickData click) {
        if (click.isRemote) return;
        switch (data) {
            case "open" -> setActive(active + EquatorialShipyardPhysics.STEP);
            case "close" -> setActive(active - EquatorialShipyardPhysics.STEP);
            case "rush" -> setRush(!rush);
            default -> super.handleDisplayClick(data, click);
        }
    }

    @Override
    public void addDisplayText(List<Component> text) {
        super.addDisplayText(text);
        if (!isFormed()) return;
        text.add(Component.translatable("typeomega.equatorial_shipyard.state", (int) view[2], (int) view[3],
                String.format("%.1f", view[1])).withStyle(ChatFormatting.AQUA));
    }
}
