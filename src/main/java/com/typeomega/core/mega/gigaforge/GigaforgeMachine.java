package com.typeomega.core.mega.gigaforge;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import com.typeomega.core.mega.common.MegaMachine;
import com.typeomega.core.mega.system.StarPhysics.Kind;
import com.typeomega.core.mega.system.StarSystems;

import java.util.List;

/**
 * Controller of the Neutronium Gigaforge (D-446): sits above a neutron star, pulsar or magnetar and extracts
 * neutronium dust for the Nidavellir Hyperforge sharing the same star.
 */
public class GigaforgeMachine extends MegaMachine {

    public static final String SHAPE = "neutronium_gigaforge";

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(GigaforgeMachine.class,
            MegaMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    private int ratePct = 100;
    @Persisted
    @DescSynced
    private long extracted;
    @DescSynced
    private String blocked = "";
    /**
     * What the screen shows: [0] neutron star (1/0), [1] phases, [2] dust/s, [3] EU/t, [4] total extracted,
     * [5] working (1/0).
     */
    @DescSynced
    private double[] view = new double[6];

    public GigaforgeMachine(IMachineBlockEntity holder, Object... args) {
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

    // EN: Both the Gigaforge and the Nidavellir require a neutron star, pulsar or magnetar (D-446 A2).
    // PT: Tanto a Gigaforge quanto a Nidavellir exigem estrela de nêutrons, pulsar ou magnetar (D-446 A2).
    // ZH: 巨型中子锻炉和尼达维勒都要求系统恒星为中子星、脉冲星或磁星（D-446 A2）。
    @Override
    public boolean inPlace() {
        if (getLevel() == null) return false;
        if (!anyDimensionForTests && !StarSystems.isSystem(getLevel().dimension())) return false;
        if (getLevel().isClientSide) return view[0] > 0;
        var star = star();
        return star != null && star.kind == Kind.NEUTRON_STAR;
    }

    public int ratePct() {
        return ratePct;
    }

    public long extracted() {
        return extracted;
    }

    public String blocked() {
        return blocked;
    }

    public double[] view() {
        return view;
    }

    @Override
    public float fxLevel(String effect) {
        boolean on = view[5] > 0 && phases >= 3;
        if (effect.startsWith("drill")) return on ? Math.max(0.25f, ratePct / 100f) : 0;
        return 1;
    }

    @Override
    protected boolean megaSecond(ServerLevel level) {
        var star = star();
        if (star == null || star.kind != Kind.NEUTRON_STAR) {
            blocked = "no_neutron_star";
            view = new double[6];
            return false;
        }
        if (!isWorkingEnabled()) {
            blocked = "disabled";
            view = new double[] { 1, phases, 0, 0, extracted, 0 };
            return false;
        }
        int dust = GigaforgePhysics.dustPerSecond(phases, ratePct);
        double eu = GigaforgePhysics.euPerSecond(dust);
        if (dust <= 0 || !pay(level, eu)) {
            blocked = "power";
            view = new double[] { 1, phases, 0, eu / 20.0, extracted, 0 };
            return false;
        }
        output(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Neutronium, 1), dust);
        extracted += dust;
        blocked = "";
        view = new double[] { 1, phases, dust, eu / 20.0, extracted, 1 };
        return true;
    }

    @Override
    protected void stopped() {
        view = new double[] { view[0], phases, 0, 0, extracted, 0 };
    }

    public void setRatePct(int pct) {
        ratePct = Math.max(GigaforgePhysics.RATE_MIN, Math.min(GigaforgePhysics.RATE_MAX, pct));
    }

    @Override
    public ModularUI createUI(Player player) {
        return GigaforgeScreen.create(this, player);
    }

    @Override
    public void handleDisplayClick(String data, ClickData click) {
        if (click.isRemote) return;
        switch (data) {
            case "up" -> setRatePct(ratePct + GigaforgePhysics.RATE_STEP);
            case "down" -> setRatePct(ratePct - GigaforgePhysics.RATE_STEP);
            default -> super.handleDisplayClick(data, click);
        }
    }

    @Override
    public void addDisplayText(List<Component> text) {
        super.addDisplayText(text);
        if (!isFormed()) return;
        text.add(Component.translatable("typeomega.neutronium_gigaforge.state", (int) view[2], extracted)
                .withStyle(ChatFormatting.AQUA));
    }
}
