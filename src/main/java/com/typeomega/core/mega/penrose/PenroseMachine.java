package com.typeomega.core.mega.penrose;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

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
import com.typeomega.core.mega.system.StarPhysics.Kind;
import com.typeomega.core.mega.system.StarSystems;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controller of the Penrose Sphere (D-446 A3): a geodesic mirror sphere with twin inclined Penrose rings around a
 * natural black hole, extracting rotational energy from its ergosphere and supplying the Penrose slice to HRAE-MC.
 */
public class PenroseMachine extends MegaMachine {

    public static final String SHAPE = "penrose_sphere";

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(PenroseMachine.class,
            MegaMachine.MANAGED_FIELD_HOLDER);

    private record ActiveState(long gameTime, double eut, int pct) {}

    private static final Map<ResourceLocation, ActiveState> ACTIVE_BY_DIM = new ConcurrentHashMap<>();

    @Persisted
    @DescSynced
    private int penrosePct = 30;
    @DescSynced
    private String blocked = "";
    /**
     * What the screen shows: [0] black hole present (1/0), [1] spin, [2] Penrose EU/t, [3] phases, [4] working (1/0),
     * [5] hole mass M☉.
     */
    @DescSynced
    private double[] view = new double[6];

    public PenroseMachine(IMachineBlockEntity holder, Object... args) {
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

    // EN: Penrose Sphere requires a natural black hole, shared with HRAE-MC (D-446 A3).
    // PT: A Penrose Sphere exige um buraco negro natural, compartilhado com o HRAE-MC (D-446 A3).
    // ZH: 彭罗斯球要求系统中心为黑洞，与 HRAE-MC 共享同一个黑洞（D-446 A3）。
    @Override
    public boolean inPlace() {
        if (getLevel() == null) return false;
        if (!anyDimensionForTests && !StarSystems.isSystem(getLevel().dimension())) return false;
        if (getLevel().isClientSide) return view[0] > 0;
        var star = star();
        return star != null && star.kind == Kind.BLACK_HOLE;
    }

    public int penrosePct() {
        return penrosePct;
    }

    public String blocked() {
        return blocked;
    }

    public double[] view() {
        return view;
    }

    public void setPenrose(int pct) {
        penrosePct = Math.max(0, Math.min(100, pct));
    }

    /** Returns the active Penrose EU/t extracted in this system's dimension, or 0 if no Penrose Sphere is active. */
    public static double activePenroseEut(ServerLevel level) {
        ActiveState state = ACTIVE_BY_DIM.get(dimKey(level));
        if (state == null) return 0;
        if (level.getGameTime() - state.gameTime() > 40) return 0;
        return state.eut();
    }

    /** Returns true if an active Penrose Sphere is formed and running around this system's black hole. */
    public static boolean hasActiveSphere(ServerLevel level) {
        return activePenroseEut(level) > 0;
    }

    private static ResourceLocation dimKey(ServerLevel level) {
        var dim = level.dimension().location();
        return anyDimensionForTests && !StarSystems.isSystem(level.dimension()) ? StarSystems.NEXUS.location() : dim;
    }

    @Override
    public float fxLevel(String effect) {
        if (effect.equals("ergo")) return view[4] > 0 && view[2] > 0 ? 1 : 0.35f;
        if (effect.equals("hole")) return view[0] > 0 ? 1 : 0.35f;
        return 1;
    }

    @Override
    public float fxScale(String effect) {
        if (effect.equals("hole")) return (float) Math.max(0.6, Math.min(1.6, Math.cbrt(Math.max(1, view[5]) / 10)));
        return 1;
    }

    @Override
    protected boolean megaSecond(ServerLevel level) {
        var star = star();
        if (star == null || star.kind != Kind.BLACK_HOLE) {
            blocked = "no_hole";
            ACTIVE_BY_DIM.remove(dimKey(level));
            view = new double[6];
            return false;
        }
        if (!isWorkingEnabled()) {
            blocked = "disabled";
            ACTIVE_BY_DIM.remove(dimKey(level));
            view = new double[] { 1, star.spin, 0, phases, 0, star.mass };
            return false;
        }
        double a = Math.max(0, Math.min(PenrosePhysics.MAX_SPIN, star.spin));
        double penrose = PenrosePhysics.penroseEut(penrosePct, a, star.mass, phases);
        if (penrose > 0) {
            payOut(level, penrose * 20.0);
            star.spin = Math.max(0, Math.min(PenrosePhysics.MAX_SPIN, a - PenrosePhysics.spinDrain(penrose, a, star.mass)));
            StarSystems.get(level.getServer()).changed();
        }
        ACTIVE_BY_DIM.put(dimKey(level), new ActiveState(level.getGameTime(), penrose, penrosePct));
        blocked = phases < PenrosePhysics.PHASES ? "rings_incomplete" : "";
        view = new double[] { 1, star.spin, penrose, phases, penrose > 0 ? 1 : 0, star.mass };
        return penrose > 0;
    }

    @Override
    protected void stopped() {
        if (getLevel() instanceof ServerLevel sl) ACTIVE_BY_DIM.remove(dimKey(sl));
        view = new double[] { view[0], view[1], 0, phases, 0, view[5] };
    }

    @Override
    public void onStructureInvalid() {
        if (getLevel() instanceof ServerLevel sl) ACTIVE_BY_DIM.remove(dimKey(sl));
        super.onStructureInvalid();
    }

    @Override
    public ModularUI createUI(Player player) {
        return PenroseScreen.create(this, player);
    }

    @Override
    public void handleDisplayClick(String data, ClickData click) {
        if (click.isRemote) return;
        switch (data) {
            case "more" -> setPenrose(penrosePct + PenrosePhysics.SHARE_STEP);
            case "less" -> setPenrose(penrosePct - PenrosePhysics.SHARE_STEP);
            default -> super.handleDisplayClick(data, click);
        }
    }

    @Override
    public void addDisplayText(List<Component> text) {
        super.addDisplayText(text);
        if (!isFormed()) return;
        if (!inPlace()) {
            text.add(Component.translatable("typeomega.penrose_sphere.needs_hole").withStyle(ChatFormatting.RED));
            return;
        }
        text.add(Component.translatable("typeomega.penrose_sphere.state",
                String.format("%.3f", view[1]), String.format("%.2e", view[2]))
                .withStyle(ChatFormatting.AQUA));
    }
}
