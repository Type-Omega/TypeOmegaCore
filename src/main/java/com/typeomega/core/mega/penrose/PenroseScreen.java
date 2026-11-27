package com.typeomega.core.mega.penrose;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.raishxn.gtna.common.machine.multiblock.originals.ui.OriginalScreen.Nav;
import com.typeomega.core.mega.common.MegaScreen;
import com.typeomega.core.mega.common.client.MegaArt;
import com.typeomega.core.mega.common.ui.MegaUI;
import com.typeomega.core.mega.common.ui.MegaUI.Look;

import static com.raishxn.gtna.common.machine.multiblock.originals.ui.OriginalScreen.*;

/**
 * Penrose Sphere screen: faceted mirror sphere around the black hole, spin dial, Penrose draw knob, and EU/t readout.
 */
public final class PenroseScreen {

    static final String P = "typeomega.penrose_sphere";
    static final Look L = new Look("penrose_sphere", 0xFF7CE8FF, 0xFFE8FAFF, 0xFF0C121D, 0xFF284862, 0xFF789AB8,
            MegaUI.Shape.CAPSULE);
    private static final int MANUAL_LINES = 6, PAGE_LINES = 3;

    private PenroseScreen() {}

    public static ModularUI create(PenroseMachine m, Player player) {
        Nav nav = new Nav();
        var root = MegaUI.window(L, m, 240, 210, 72, 14);
        root.addWidget(MegaUI.paint(68, 36, 104, 104, (g, x, y) -> sphereView(g, m, x + 52, y + 52)));
        root.addWidget(MegaUI.paint(12, 60, 52, 60, (g, x, y) -> MegaUI.ring(g, L, x + 26, y + 26, 18,
                v(m, 1) / PenrosePhysics.MAX_SPIN, 0xFFC78CFF, MegaScreen.tr(P, "spin"),
                String.format("%.3f", v(m, 1)))));
        root.addWidget(MegaUI.knob(L, 200, 84, 14, () -> m.penrosePct() / PenrosePhysics.SHARE_STEP, 11,
                () -> new String[] { "0", "", "", "", "", "", "", "", "", "", "100" },
                c -> MegaScreen.act(m, c.button == 1 ? "less" : "more", c), P + ".ui.draw_tip"));
        root.addWidget(MegaUI.paint(16, 146, 152, 34, (g, x, y) -> {
            if (v(m, 4) > 0) {
                text(g, MegaScreen.tr(P, "output", sci(v(m, 2))), x + 4, y + 4, L.glow(), false);
            } else {
                String idleKey = m.blocked().isEmpty() ? "idle.rings_incomplete" : "idle." + m.blocked();
                text(g, MegaScreen.tr(P, idleKey), x + 4, y + 4, RED, false);
            }
            text(g, MegaScreen.tr(P, "draw", m.penrosePct()), x + 4, y + 18, L.dim(), false);
            right(g, MegaScreen.tr(P, "phases", m.phases(), PenrosePhysics.PHASES), x + 148, y + 18, L.dim());
        }));
        root.addWidget(MegaUI.button(L, 180, 148, 44, 14, P + ".ui.manual", () -> "manual".equals(nav.open),
                c -> nav.toggle("manual")));
        root.addWidget(MegaUI.button(L, 180, 166, 44, 14, "gtna.ui.o.power", m::isWorkingEnabled, c -> {
            if (!c.isRemote) m.setWorkingEnabled(!m.isWorkingEnabled());
        }));
        root.addWidget(MegaUI.manual(L, 14, 28, 212, 160, P, MANUAL_LINES, PAGE_LINES, nav));
        return MegaUI.ui(root, m, player);
    }

    private static double v(PenroseMachine m, int i) {
        double[] v = m.view();
        return i < v.length ? v[i] : 0;
    }

    @OnlyIn(Dist.CLIENT)
    private static void sphereView(GuiGraphics g, PenroseMachine m, int cx, int cy) {
        if (!m.inPlace()) {
            centre(g, MegaScreen.tr(P, "no_hole"), cx, cy - 4, RED);
            return;
        }
        double t = MegaArt.time(), a = v(m, 1);
        boolean active = v(m, 4) > 0;
        // Black hole shadow + superradiant ergosphere glow
        disc(g, cx, cy, 10, 0xFF000000);
        ellipse(g, cx, cy, 12, 12, active ? 0xFF8A6AE0 : 0xFF4A3A70, 0);
        // Mirror sphere shell
        ellipse(g, cx, cy, 28, 28, L.accent(), 2);
        // Twin inclined rotating rings
        double rot = t * (0.6 + a);
        int rx = (int) Math.round(36 * Math.abs(Math.cos(rot * 0.3)) + 16);
        ellipse(g, cx, cy, rx, 36, active ? 0xFF7CE8FF : 0xFF3E6880, 0);
        ellipse(g, cx, cy, 36, rx, active ? 0xFFC78CFF : 0xFF563E80, 0);
    }
}
