package com.typeomega.core.mega.seeder;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import com.raishxn.gtna.common.data.GTNARecipeType;
import com.typeomega.core.mega.common.MegaMachine;
import com.typeomega.core.mega.common.MegaRecipeEngine;
import com.typeomega.core.mega.system.StarPhysics;
import com.typeomega.core.mega.system.StarSystems;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controller of the Planetary Seeder Nexus (D-446 A6, D-448): an earlier-tier orbital bio-seeder kilostructure and
 * companion to Yggdrasil Orchid, running Greenhouse recipes and producing Biomass.
 */
public class SeederMachine extends MegaMachine {

    public static final String SHAPE = "planetary_seeder_nexus";

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(SeederMachine.class,
            MegaMachine.MANAGED_FIELD_HOLDER);

    private record ActiveState(long gameTime, int phases) {}

    private static final Map<ResourceLocation, ActiveState> ACTIVE_BY_DIM = new ConcurrentHashMap<>();

    @Persisted
    @DescSynced
    private int fluxPct = 60;
    @Persisted
    private double progressTicks;
    @Persisted
    @DescSynced
    private long crafted;
    @Persisted
    @DescSynced
    private long biomass;
    @DescSynced
    private String recipe = "";
    @DescSynced
    private String blocked = "";
    /**
     * What the screen shows: [0] parallel, [1] speed, [2] luminosity, [3] crafts this second, [4] EU this second,
     * [5] working (1/0), [6] phases.
     */
    @DescSynced
    private double[] view = new double[7];
    private final MegaRecipeEngine.Progress progress = new MegaRecipeEngine.Progress();

    public SeederMachine(IMachineBlockEntity holder, Object... args) {
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

    // EN: Planetary Seeder Nexus operates in any star system (kilostructure companion to Yggdrasil Orchid, D-446 A6).
    // PT: O Planetary Seeder Nexus opera em qualquer sistema estelar (kiloestrutura companheira da Yggdrasil, D-446 A6).
    // ZH: 行星播种枢纽可在任意恒星系中运行（世界树兰花的前置伴生千吨级巨构，D-446 A6）。
    @Override
    public boolean inPlace() {
        return anyDimensionForTests || (getLevel() != null && StarSystems.isSystem(getLevel().dimension()));
    }

    public int fluxPct() {
        return fluxPct;
    }

    public long crafted() {
        return crafted;
    }

    public long biomass() {
        return biomass;
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

    public void setFlux(int pct) {
        fluxPct = Math.max(SeederPhysics.FLUX_MIN, Math.min(SeederPhysics.FLUX_MAX, pct));
    }

    /** Returns true if an active Planetary Seeder Nexus is formed and enabled in this system's dimension. */
    public static boolean hasActiveSeeder(ServerLevel level) {
        ActiveState state = ACTIVE_BY_DIM.get(dimKey(level));
        return state != null && level.getGameTime() - state.gameTime() <= 40;
    }

    private static ResourceLocation dimKey(ServerLevel level) {
        var dim = level.dimension().location();
        return anyDimensionForTests && !StarSystems.isSystem(level.dimension()) ? StarSystems.NEXUS.location() : dim;
    }

    static List<GTRecipeType> types() {
        return List.of(GTNARecipeType.GREENHOUSE_RECIPES);
    }

    @Override
    public float fxLevel(String effect) {
        boolean on = view[5] > 0;
        if (effect.equals("seeding_halo")) return on ? 1.0f : 0.35f;
        if (effect.equals("terraforming_beam")) return on && phases >= SeederPhysics.PHASES ? 1.0f : 0.25f;
        return 1;
    }

    @Override
    protected boolean megaSecond(ServerLevel level) {
        var star = star();
        double light = star == null ? 0 : StarPhysics.luminosity(star.kind, star.mass);
        int parallel = SeederPhysics.parallel(phases);
        double speed = SeederPhysics.speed(fluxPct, light, phases);
        MegaRecipeEngine.Result result = new MegaRecipeEngine.Result(0, 0, "", "");
        if (isWorkingEnabled()) {
            progress.ticks = progressTicks;
            result = MegaRecipeEngine.second(this, level, types(),
                    new MegaRecipeEngine.Limits(parallel, speed, 1, r -> true), progress);
            progressTicks = progress.ticks;
            crafted += result.crafts();
            if (result.crafts() > 0) {
                int mb = (int) Math.min(Integer.MAX_VALUE, result.crafts() * SeederPhysics.BIOMASS_PER_CRAFT);
                biomass += mb - fillFluid(GTMaterials.Biomass.getFluid(mb));
            }
            ACTIVE_BY_DIM.put(dimKey(level), new ActiveState(level.getGameTime(), phases));
        } else {
            ACTIVE_BY_DIM.remove(dimKey(level));
        }
        recipe = result.recipe();
        blocked = result.blocked();
        boolean working = result.busy();
        view = new double[] { parallel, speed, light, result.crafts(), result.eu(), working ? 1 : 0, phases };
        return working || isWorkingEnabled();
    }

    @Override
    protected void stopped() {
        if (getLevel() instanceof ServerLevel sl) ACTIVE_BY_DIM.remove(dimKey(sl));
        view = new double[] { view[0], view[1], view[2], 0, 0, 0, phases };
    }

    @Override
    public void onStructureInvalid() {
        if (getLevel() instanceof ServerLevel sl) ACTIVE_BY_DIM.remove(dimKey(sl));
        super.onStructureInvalid();
    }

    @Override
    public ModularUI createUI(Player player) {
        return SeederScreen.create(this, player);
    }

    @Override
    public void handleDisplayClick(String data, ClickData click) {
        if (click.isRemote) return;
        switch (data) {
            case "more" -> setFlux(fluxPct + SeederPhysics.FLUX_STEP);
            case "less" -> setFlux(fluxPct - SeederPhysics.FLUX_STEP);
            default -> super.handleDisplayClick(data, click);
        }
    }

    @Override
    public void addDisplayText(List<Component> text) {
        super.addDisplayText(text);
        if (!isFormed()) return;
        if (!inPlace()) {
            text.add(Component.translatable("typeomega.planetary_seeder_nexus.needs_system")
                    .withStyle(ChatFormatting.RED));
            return;
        }
        text.add(Component.translatable("typeomega.planetary_seeder_nexus.state",
                fluxPct, String.format("%.2f", view[1])).withStyle(ChatFormatting.GREEN));
    }
}
