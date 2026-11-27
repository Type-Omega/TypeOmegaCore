package com.typeomega.core.mega.shipyard;

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
 * Equatorial Shipyard screen: twin-ring radar (inner harvesting ring + outer assembly ring with 6 radial gantries),
 * slipway lever, build counters, starlight readout, recipe ticker, and rush-shift switch.
 */
public final class EquatorialShipyardScreen {

    static final String P = "typeomega.equatorial_shipyard";
    static final Look L = new Look("equatorial_shipyard", 0xFF7FC4FF, 0xFFE0F0FF, 0xFF0F1620, 0xFF2E4A66,
            0xFF8FA8C0, MegaUI.Shape.CHEVRON);
    private static final int MANUAL_LINES = 6, PAGE_LINES = 3;

    private EquatorialShipyardScreen() {}

    public static ModularUI create(EquatorialShipyardMachine m, Player player) {
        Nav nav = new Nav();
        var root = MegaUI.window(L, m, 312, 204, 106, 7);
        root.addWidget(MegaUI.paint(18, 32, 124, 124, (g, x, y) -> radar(g, m, x + 62, y + 62)));
        root.addWidget(MegaUI.lever(L, 152, 22, 128,
                () -> m.active() / (double) EquatorialShipyardPhysics.SLIPWAYS_PHASE_3,
                c -> MegaScreen.act(m, "open", c), c -> MegaScreen.act(m, "close", c), P + ".ui.lever.tip"));
        root.addWidget(MegaUI.paint(180, 22, 108, 56, (g, x, y) -> counters(g, m, x, y)));
        root.addWidget(MegaUI.paint(180, 84, 108, 66, (g, x, y) -> starlight(g, m, x, y)));
        root.addWidget(MegaUI.paint(14, 160, 274, 14, (g, x, y) -> MegaUI.ticker(g, L, x + 3, y + 3, 268,
                ticker(m), 0xFFBFE0FF)));
        root.addWidget(MegaUI.button(L, 14, 182, 84, 14, P + ".ui.manual", () -> "manual".equals(nav.open),
                c -> nav.toggle("manual")));
        root.addWidget(MegaUI.button(L, 108, 182, 84, 14, P + ".ui.rush", m::rush,
                c -> MegaScreen.act(m, "rush", c)));
        root.addWidget(MegaUI.button(L, 202, 182, 84, 14, "gtna.ui.o.power", m::isWorkingEnabled, c -> {
            if (!c.isRemote) m.setWorkingEnabled(!m.isWorkingEnabled());
        }));
        root.addWidget(MegaUI.manual(L, 14, 20, 284, 156, P, MANUAL_LINES, PAGE_LINES, nav));
        return MegaUI.ui(root, m, player);
    }

    private static double v(EquatorialShipyardMachine m, int i) {
        double[] v = m.view();
        return i < v.length ? v[i] : 0;
    }

    private static String ticker(EquatorialShipyardMachine m) {
        if (m.recipe().isEmpty())
            return MegaScreen.tr(P, "idle." + (m.blocked().isEmpty() ? "no_recipe" : m.blocked()));
        return m.recipe().substring(m.recipe().indexOf(':') + 1) + "  ·  " +
                MegaScreen.tr(P, "crafts", String.format("%.0f", v(m, 4)), sci(v(m, 5)));
    }

    /** Twin-ring radar: star, inner harvesting ring, 6 radial gantries, and 24 outer assembly slipways. */
    @OnlyIn(Dist.CLIENT)
    private static void radar(GuiGraphics g, EquatorialShipyardMachine m, int cx, int cy) {
        double t = MegaArt.time();
        boolean on = v(m, 8) > 0;
        ellipse(g, cx, cy, 50, 50, 0xFF2E4A66, 0);
        ellipse(g, cx, cy, 32, 32, m.phases() >= 2 ? 0xFF4AD4FF : 0xFF2E4A66, 0);
        int gantryColour = m.phases() >= 3 ? 0xFF7FA8FF : 0xFF24384E;
        for (int k = 0; k < 6; k++) {
            double a = Math.toRadians(30.0 + 60.0 * k);
            line(g, cx + (int) Math.round(Math.cos(a) * 32), cy + (int) Math.round(Math.sin(a) * 32),
                    cx + (int) Math.round(Math.cos(a) * 50), cy + (int) Math.round(Math.sin(a) * 50), gantryColour);
        }
        int open = (int) v(m, 2), built = (int) v(m, 3), total = EquatorialShipyardPhysics.SLIPWAYS_PHASE_3;
        int sweep = on && open > 0 ? (int) (t * 16) % open : -1;
        for (int i = 0; i < total; i++) {
            double a = 2 * Math.PI * i / total - Math.PI / 2;
            int px = cx + (int) Math.round(Math.cos(a) * 44), py = cy + (int) Math.round(Math.sin(a) * 44);
            int colour = i >= built ? 0xFF1A2430 : i >= open ? 0xFF3A5068 : L.accent();
            if (on && i < open && Math.abs(i - sweep) < 2) colour = 0xFFFFFFFF;
            g.fill(px - 1, py - 1, px + 2, py + 2, colour);
        }
        MegaArt.star(g, cx, cy, 7, Math.max(3000, v(m, 7)));
        centre(g, MegaScreen.tr(P, "slipways", open, built), cx, cy + 55, L.dim());
    }

    @OnlyIn(Dist.CLIENT)
    private static void counters(GuiGraphics g, EquatorialShipyardMachine m, int x, int y) {
        text(g, MegaScreen.tr(P, "parallel"), x + 4, y + 3, L.dim(), false);
        text(g, String.format("%.0f", v(m, 0)), x + 4, y + 13, L.glow(), false);
        text(g, MegaScreen.tr(P, "speed", String.format("%.1f", v(m, 1))), x + 4, y + 28, L.accent(), false);
        text(g, MegaScreen.tr(P, "crafted", m.crafted()), x + 4, y + 40, L.dim(), false);
    }

    @OnlyIn(Dist.CLIENT)
    private static void starlight(GuiGraphics g, EquatorialShipyardMachine m, int x, int y) {
        text(g, MegaScreen.tr(P, "starlight"), x + 4, y + 3, L.dim(), false);
        text(g, MegaScreen.tr(P, "luminosity", sci(v(m, 6))), x + 4, y + 16, 0xFFFFD080, false);
        text(g, MegaScreen.tr(P, "temperature", String.format("%.0f", v(m, 7))), x + 4, y + 28, L.dim(), false);
        text(g, MegaScreen.tr(P, m.rush() ? "rush_on" : "rush_off"), x + 4, y + 42,
                m.rush() ? 0xFFFFB040 : L.dim(), false);
    }
}
