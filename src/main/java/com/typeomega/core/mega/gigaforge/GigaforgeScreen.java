package com.typeomega.core.mega.gigaforge;

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
 * Neutronium Gigaforge screen: shows the cube hub, the three processing rings, the drill pointing down at the
 * neutron star, extraction rate control and field manual.
 */
public final class GigaforgeScreen {

    static final String P = "typeomega.neutronium_gigaforge";
    static final Look L = new Look("gigaforge", 0xFF55E0FF, 0xFFD8F8FF, 0xFF101820, 0xFF285870, 0xFF8AA8B8,
            MegaUI.Shape.NOTCH);
    private static final int MANUAL_LINES = 6, PAGE_LINES = 4;

    private GigaforgeScreen() {}

    public static ModularUI create(GigaforgeMachine m, Player player) {
        Nav nav = new Nav();
        var root = MegaUI.window(L, m, 300, 208, 96, 9);
        root.addWidget(MegaUI.paint(28, 22, 178, 96, (g, x, y) -> rig(g, m, x, y)));
        root.addWidget(MegaUI.lever(L, 214, 22, 96,
                () -> (m.ratePct() - GigaforgePhysics.RATE_MIN) /
                        (double) (GigaforgePhysics.RATE_MAX - GigaforgePhysics.RATE_MIN),
                c -> MegaScreen.act(m, "up", c), c -> MegaScreen.act(m, "down", c),
                P + ".ui.rate_tip"));
        root.addWidget(MegaUI.paint(28, 124, 242, 14, (g, x, y) -> MegaUI.ticker(g, L, x + 3, y + 3, 236, ticker(m),
                0xFF9BE8FF)));
        root.addWidget(MegaUI.paint(28, 144, 242, 28, (g, x, y) -> stats(g, m, x, y)));
        root.addWidget(MegaUI.button(L, 28, 180, 116, 16, P + ".ui.manual", () -> "manual".equals(nav.open),
                c -> nav.toggle("manual")));
        root.addWidget(MegaUI.button(L, 154, 180, 116, 16, "gtna.ui.o.power", m::isWorkingEnabled, c -> {
            if (!c.isRemote) m.setWorkingEnabled(!m.isWorkingEnabled());
        }));
        root.addWidget(MegaUI.manual(L, 26, 20, 248, 156, P, MANUAL_LINES, PAGE_LINES, nav));
        return MegaUI.ui(root, m, player);
    }

    private static double v(GigaforgeMachine m, int i) {
        double[] v = m.view();
        return i < v.length ? v[i] : 0;
    }

    private static String ticker(GigaforgeMachine m) {
        if (v(m, 5) > 0) return MegaScreen.tr(P, "extracting", String.format("%.0f", v(m, 2)), sci(v(m, 3)));
        return MegaScreen.tr(P, "idle." + (m.blocked().isEmpty() ? "no_neutron_star" : m.blocked()));
    }

    @OnlyIn(Dist.CLIENT)
    private static void rig(GuiGraphics g, GigaforgeMachine m, int x, int y) {
        int cx = x + 89, cy = y + 32;
        boolean on = v(m, 5) > 0, hasStar = v(m, 0) > 0;
        int phases = m.phases();
        g.fill(cx - 12, cy - 12, cx + 12, cy + 12, 0xFF3E4E60);
        if (phases >= 2) {
            ellipse(g, cx, cy - 4, 20, 5, 0xFF80C8E8, 0);
            ellipse(g, cx, cy, 22, 6, 0xFF80C8E8, 0);
            ellipse(g, cx, cy + 4, 20, 5, 0xFF80C8E8, 0);
        }
        if (phases >= 3) {
            g.fill(cx - 4, cy + 12, cx + 4, cy + 28, 0xFF5A6B7D);
            if (on) MegaArt.beam(g, cx, cy + 28, cx, y + 80, 0xFF80F0FF);
        }
        if (hasStar) MegaArt.star(g, cx, y + 82, 5, 600000);
        text(g, MegaScreen.tr(P, hasStar ? "star_ok" : "no_star"), x + 3, y + 3, hasStar ? 0xFF80F0FF : RED, false);
    }

    @OnlyIn(Dist.CLIENT)
    private static void stats(GuiGraphics g, GigaforgeMachine m, int x, int y) {
        text(g, MegaScreen.tr(P, "rate", m.ratePct()), x + 4, y + 4, L.glow(), false);
        text(g, MegaScreen.tr(P, "extracted", m.extracted()), x + 4, y + 16, L.glow(), false);
        right(g, MegaScreen.tr(P, "phases", m.phases(), GigaforgePhysics.PHASES), x + 238, y + 4, L.dim());
    }
}
