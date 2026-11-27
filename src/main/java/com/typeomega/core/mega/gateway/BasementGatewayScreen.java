package com.typeomega.core.mega.gateway;

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
 * Basement Gateway screen: shows the containment frame, coils, shell, polar gamma lens, spinning entangler,
 * basement-universe wormhole, aperture lever, and field manual.
 */
public final class BasementGatewayScreen {

    static final String P = "typeomega.basement_gateway";
    static final Look L = new Look("gateway", 0xFFA66BFF, 0xFFF0E4FF, 0xFF100B1C, 0xFF4A2B7A, 0xFF8A70B8,
            MegaUI.Shape.NOTCH);
    private static final int MANUAL_LINES = 6, PAGE_LINES = 4;

    private BasementGatewayScreen() {}

    public static ModularUI create(BasementGatewayMachine m, Player player) {
        Nav nav = new Nav();
        var root = MegaUI.window(L, m, 300, 208, 96, 9);
        root.addWidget(MegaUI.paint(28, 22, 178, 96, (g, x, y) -> rig(g, m, x, y)));
        root.addWidget(MegaUI.lever(L, 214, 22, 96,
                () -> (m.aperturePct() - BasementGatewayPhysics.APERTURE_MIN) /
                        (double) (BasementGatewayPhysics.APERTURE_MAX - BasementGatewayPhysics.APERTURE_MIN),
                c -> MegaScreen.act(m, "up", c), c -> MegaScreen.act(m, "down", c),
                P + ".ui.aperture_tip"));
        root.addWidget(MegaUI.paint(28, 124, 242, 14, (g, x, y) -> MegaUI.ticker(g, L, x + 3, y + 3, 236, ticker(m),
                0xFFD4B2FF)));
        root.addWidget(MegaUI.paint(28, 144, 242, 28, (g, x, y) -> stats(g, m, x, y)));
        root.addWidget(MegaUI.button(L, 28, 180, 116, 16, P + ".ui.manual", () -> "manual".equals(nav.open),
                c -> nav.toggle("manual")));
        root.addWidget(MegaUI.button(L, 154, 180, 116, 16, "gtna.ui.o.power", m::isWorkingEnabled, c -> {
            if (!c.isRemote) m.setWorkingEnabled(!m.isWorkingEnabled());
        }));
        root.addWidget(MegaUI.manual(L, 26, 20, 248, 156, P, MANUAL_LINES, PAGE_LINES, nav));
        return MegaUI.ui(root, m, player);
    }

    private static double v(BasementGatewayMachine m, int i) {
        double[] v = m.view();
        return i < v.length ? v[i] : 0;
    }

    private static String ticker(BasementGatewayMachine m) {
        if (v(m, 6) > 0) {
            return MegaScreen.tr(P, "running", String.format("%.0f", v(m, 4)), String.format("%.0f", v(m, 5)),
                    sci(v(m, 3)));
        }
        return MegaScreen.tr(P, "idle." + (m.blocked().isEmpty() ? "no_neutron_star" : m.blocked()));
    }

    @OnlyIn(Dist.CLIENT)
    private static void rig(GuiGraphics g, BasementGatewayMachine m, int x, int y) {
        int cx = x + 89, cy = y + 40;
        boolean on = v(m, 6) > 0, hasStar = v(m, 0) > 0;
        int phases = m.phases();
        if (phases >= 2) ellipse(g, cx, cy, 24, 9, 0xFF7B57C4, 0);
        if (phases >= 3) ellipse(g, cx, cy, 20, 12, 0xFF4B5B78, 0);
        g.fill(cx - 14, cy - 12, cx + 14, cy + 12, 0xFF3E4E60);
        if (hasStar) MegaArt.star(g, cx, cy, 5, 600000);
        if (phases >= 4) {
            g.fill(cx - 5, cy - 22, cx + 5, cy - 14, 0xFF7EE8FF);
            if (on) MegaArt.beam(g, cx, cy - 14, cx, y + 78, 0xFF9C5CFF);
        }
        if (phases >= 5) {
            g.fill(cx - 3, cy - 30, cx + 3, cy - 24, 0xFFC580FF);
            ellipse(g, cx, y + 80, 14, 5, on ? 0xFFB86BFF : 0xFF5A3E82, 0);
        }
        text(g, MegaScreen.tr(P, hasStar ? "star_ok" : "no_star"), x + 3, y + 3, hasStar ? 0xFF80F0FF : RED, false);
    }

    @OnlyIn(Dist.CLIENT)
    private static void stats(GuiGraphics g, BasementGatewayMachine m, int x, int y) {
        text(g, MegaScreen.tr(P, "aperture", m.aperturePct()), x + 4, y + 4, L.glow(), false);
        text(g, MegaScreen.tr(P, "totals", m.totalDust(), m.totalGasMb()), x + 4, y + 16, L.glow(), false);
        right(g, MegaScreen.tr(P, "phases", m.phases(), BasementGatewayPhysics.PHASES), x + 238, y + 4, L.dim());
    }
}
