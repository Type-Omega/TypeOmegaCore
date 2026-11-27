package com.typeomega.core.mega.seeder;

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
 * Planetary Seeder Nexus screen: orbital bio-seeder status, seeding flux dial, Greenhouse batch readout, and field
 * manual.
 */
public final class SeederScreen {

    static final String P = "typeomega.planetary_seeder_nexus";
    static final Look L = new Look("planetary_seeder_nexus", 0xFF6EF5A8, 0xFFE8FFF2, 0xFF0A1410, 0xFF224D3A,
            0xFF6FA88A, MegaUI.Shape.CAPSULE);
    private static final int MANUAL_LINES = 6, PAGE_LINES = 3;

    private SeederScreen() {}

    public static ModularUI create(SeederMachine m, Player player) {
        Nav nav = new Nav();
        var root = MegaUI.window(L, m, 240, 210, 72, 14);
        root.addWidget(MegaUI.paint(68, 36, 104, 104, (g, x, y) -> seederView(g, m, x + 52, y + 52)));
        root.addWidget(MegaUI.paint(12, 60, 52, 60, (g, x, y) -> MegaUI.ring(g, L, x + 26, y + 26, 18,
                v(m, 1) / SeederPhysics.SPEED_MAX, 0xFF6EF5A8, MegaScreen.tr(P, "speed"),
                String.format("%.2fx", v(m, 1)))));
        root.addWidget(MegaUI.knob(L, 200, 84, 14, () -> (m.fluxPct() - SeederPhysics.FLUX_MIN) / SeederPhysics.FLUX_STEP,
                10, () -> new String[] { "10", "", "", "", "", "", "", "", "", "100" },
                c -> MegaScreen.act(m, c.button == 1 ? "less" : "more", c), P + ".ui.flux_tip"));
        root.addWidget(MegaUI.paint(16, 146, 152, 34, (g, x, y) -> {
            if (v(m, 5) > 0) {
                text(g, MegaScreen.tr(P, "crafted", (long) v(m, 3), (int) v(m, 0)), x + 4, y + 4, L.glow(), false);
            } else {
                String idleKey = m.blocked().isEmpty() ? "idle.ready" : "idle." + m.blocked();
                text(g, MegaScreen.tr(P, idleKey), x + 4, y + 4, L.dim(), false);
            }
            text(g, MegaScreen.tr(P, "flux", m.fluxPct()), x + 4, y + 18, L.dim(), false);
            right(g, MegaScreen.tr(P, "phases", m.phases(), SeederPhysics.PHASES), x + 148, y + 18, L.dim());
        }));
        root.addWidget(MegaUI.button(L, 180, 148, 44, 14, P + ".ui.manual", () -> "manual".equals(nav.open),
                c -> nav.toggle("manual")));
        root.addWidget(MegaUI.button(L, 180, 166, 44, 14, "gtna.ui.o.power", m::isWorkingEnabled, c -> {
            if (!c.isRemote) m.setWorkingEnabled(!m.isWorkingEnabled());
        }));
        root.addWidget(MegaUI.manual(L, 14, 28, 212, 160, P, MANUAL_LINES, PAGE_LINES, nav));
        return MegaUI.ui(root, m, player);
    }

    private static double v(SeederMachine m, int i) {
        double[] v = m.view();
        return i < v.length ? v[i] : 0;
    }

    @OnlyIn(Dist.CLIENT)
    private static void seederView(GuiGraphics g, SeederMachine m, int cx, int cy) {
        if (!m.inPlace()) {
            centre(g, MegaScreen.tr(P, "no_system"), cx, cy - 4, RED);
            return;
        }
        double t = MegaArt.time();
        boolean active = v(m, 5) > 0;
        // Central bio-spire & seeded core
        disc(g, cx, cy, 10, active ? 0xFF1F6B48 : 0xFF163828);
        ellipse(g, cx, cy, 16, 16, L.accent(), 2);
        // Rotating seeder petal rings
        int rx = (int) Math.round(32 * Math.abs(Math.cos(t * 0.5)) + 14);
        ellipse(g, cx, cy, rx, 32, active ? 0xFF6EF5A8 : 0xFF2E6B4A, 0);
        ellipse(g, cx, cy, 32, rx, active ? 0xFF42D6B8 : 0xFF225248, 0);
    }
}
