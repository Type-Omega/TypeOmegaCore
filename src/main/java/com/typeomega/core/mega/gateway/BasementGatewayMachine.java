package com.typeomega.core.mega.gateway;

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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import com.typeomega.core.mega.common.MegaMachine;
import com.typeomega.core.mega.disintegrator.DisintegratorMachine;
import com.typeomega.core.mega.system.StarPhysics.Kind;
import com.typeomega.core.mega.system.StarSystems;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controller of the Basement Gateway (D-446 A6): built in any star system as the companion destination of a
 * Solar System Disintegrator, sustaining a basement-universe wormhole to receive dismantled matter and return
 * primordial hydrogen, helium and r-process dusts.
 */
public class BasementGatewayMachine extends MegaMachine {

    public static final String SHAPE = "basement_gateway";

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            BasementGatewayMachine.class, MegaMachine.MANAGED_FIELD_HOLDER);

    private record ControllerKey(ResourceLocation dim, long pos) {}

    private static final Map<ControllerKey, Long> ACTIVE = new ConcurrentHashMap<>();

    @Persisted
    @DescSynced
    private int aperturePct = 50;
    @Persisted
    @DescSynced
    private long totalDust;
    @Persisted
    @DescSynced
    private long totalGasMb;
    @DescSynced
    private String blocked = "";
    /**
     * What the screen shows: [0] neutron star (1/0), [1] phases, [2] aperturePct, [3] EU/t, [4] dust/s,
     * [5] gas mB/s, [6] working (1/0).
     */
    @DescSynced
    private double[] view = new double[7];

    public BasementGatewayMachine(IMachineBlockEntity holder, Object... args) {
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

    // EN: Basement Gateway works in any star system, paired with an active Disintegrator (D-446).
    // PT: O Basement Gateway funciona em qualquer sistema estelar, pareado a um Disintegrator ativo (D-446).
    // ZH: 地下传送门可在任意恒星系中运行，与正在工作的拆解器配对（D-446）。
    @Override
    public boolean inPlace() {
        if (getLevel() == null) return false;
        if (!anyDimensionForTests && !StarSystems.isSystem(getLevel().dimension())) return false;
        if (getLevel().isClientSide) return view[0] > 0;
        var star = star();
        return star != null && star.kind != Kind.NONE;
    }

    public int aperturePct() {
        return aperturePct;
    }

    public long totalDust() {
        return totalDust;
    }

    public long totalGasMb() {
        return totalGasMb;
    }

    public String blocked() {
        return blocked;
    }

    public double[] view() {
        return view;
    }

    public void setAperturePct(int pct) {
        aperturePct = BasementGatewayPhysics.clampAperture(pct);
    }

    /** Returns true if an active Basement Gateway is open in this system's dimension. */
    public static boolean hasActiveGateway(ServerLevel level) {
        ResourceLocation dim = dimKey(level);
        long now = level.getGameTime();
        var it = ACTIVE.entrySet().iterator();
        while (it.hasNext()) {
            var e = it.next();
            if (!e.getKey().dim().equals(dim)) continue;
            if (now - e.getValue() <= 40) return true;
            it.remove();
        }
        return false;
    }

    private static ResourceLocation dimKey(ServerLevel level) {
        var dim = level.dimension().location();
        return anyDimensionForTests && !StarSystems.isSystem(level.dimension()) ? StarSystems.NEXUS.location() : dim;
    }

    private ControllerKey key(ServerLevel level) {
        return new ControllerKey(dimKey(level), getPos().asLong());
    }

    @Override
    public float fxLevel(String effect) {
        boolean on = view[6] > 0;
        if (effect.equals("wormhole")) return view[0] > 0 && phases >= 5 ? (on ? 1.2f : 0.45f) : 0f;
        if (effect.equals("lens_beam")) return on && phases >= 4 ? Math.max(0.25f, aperturePct / 100f) : 0f;
        if (effect.equals("neutron_star")) return view[0] > 0 ? 1f : 0.35f;
        return 1f;
    }

    @Override
    protected boolean megaSecond(ServerLevel level) {
        var star = star();
        if (star == null || star.kind == Kind.NONE) {
            blocked = "no_star";
            ACTIVE.remove(key(level));
            view = new double[7];
            return false;
        }
        if (!isWorkingEnabled()) {
            blocked = "disabled";
            ACTIVE.remove(key(level));
            view = new double[] { 1, phases, aperturePct, 0, 0, 0, 0 };
            return false;
        }
        if (!DisintegratorMachine.hasActiveInSystem(level)) {
            blocked = "no_disintegrator";
            ACTIVE.remove(key(level));
            view = new double[] { 1, phases, aperturePct, 0, 0, 0, 0 };
            return false;
        }
        double eut = BasementGatewayPhysics.eut(phases, aperturePct);
        if (!pay(level, eut * 20.0)) {
            blocked = "power";
            ACTIVE.remove(key(level));
            view = new double[] { 1, phases, aperturePct, eut, 0, 0, 0 };
            return false;
        }

        int hMb = BasementGatewayPhysics.hydrogenMb(phases, aperturePct);
        int heMb = BasementGatewayPhysics.heliumMb(phases, aperturePct);
        int dustTotal = BasementGatewayPhysics.dustPerSecond(phases, aperturePct);
        fillFluid(GTMaterials.Hydrogen.getFluid(hMb));
        fillFluid(GTMaterials.Helium.getFluid(heMb));

        int n = BasementGatewayPhysics.DUST_YIELDS.length;
        int each = dustTotal / n;
        int rem = dustTotal % n;
        for (int i = 0; i < n; i++) {
            int count = each + (i < rem ? 1 : 0);
            if (count > 0) output(ChemicalHelper.get(TagPrefix.dust, BasementGatewayPhysics.DUST_YIELDS[i], 1), count);
        }

        totalDust += dustTotal;
        totalGasMb += hMb + heMb;
        blocked = "";
        ACTIVE.put(key(level), level.getGameTime());
        view = new double[] { 1, phases, aperturePct, eut, dustTotal, hMb + heMb, 1 };
        return true;
    }

    @Override
    protected void stopped() {
        if (getLevel() instanceof ServerLevel sl) ACTIVE.remove(key(sl));
        view = new double[] { view[0], phases, aperturePct, 0, 0, 0, 0 };
    }

    @Override
    public ModularUI createUI(Player player) {
        return BasementGatewayScreen.create(this, player);
    }

    @Override
    public void handleDisplayClick(String data, ClickData click) {
        if (click.isRemote) return;
        switch (data) {
            case "up" -> setAperturePct(aperturePct + BasementGatewayPhysics.APERTURE_STEP);
            case "down" -> setAperturePct(aperturePct - BasementGatewayPhysics.APERTURE_STEP);
            default -> super.handleDisplayClick(data, click);
        }
    }

    @Override
    public void addDisplayText(List<Component> text) {
        super.addDisplayText(text);
        if (!isFormed()) return;
        if (!inPlace()) {
            text.add(Component.translatable("typeomega.basement_gateway.needs_star").withStyle(ChatFormatting.RED));
            return;
        }
        text.add(Component.translatable("typeomega.basement_gateway.state",
                (int) view[2], (int) view[4], (int) view[5]).withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
