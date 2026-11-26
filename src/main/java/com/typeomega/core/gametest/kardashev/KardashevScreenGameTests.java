package com.typeomega.core.gametest.kardashev;

import com.lowdragmc.lowdraglib.gui.widget.TabButton;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.gametest.TestPlayers;
import com.typeomega.core.kardashev.KardashevData;
import com.typeomega.core.kardashev.KardashevHudStats;
import com.typeomega.core.kardashev.KardashevLedger;
import com.typeomega.core.kardashev.KardashevMeter;
import com.typeomega.core.kardashev.KardashevNext;
import com.typeomega.core.kardashev.KardashevScale;
import com.typeomega.core.kardashev.KardashevSource;
import com.typeomega.core.kardashev.KardashevUiText;
import com.typeomega.core.kardashev.hud.KardashevHudNet;
import com.typeomega.core.kardashev.ui.KardashevGraphWidget;
import com.typeomega.core.kardashev.ui.KardashevUIFactory;
import com.typeomega.core.kardashev.ui.KardashevWindow;
import com.typeomega.lib.ui.InfoLinesWidget;
import com.typeomega.lib.ui.ProgressBarWidget;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Kardashev Meter screen and HUD (D-443): the window with its three tabs reads the viewer's team on the server and
 * syncs it, the HUD is wired to the lib's shared registry, and the next-type arithmetic and the watts equivalence
 * follow the D-380 table.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class KardashevScreenGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";
    private static final int SECOND = 20;
    private static final double ONE_AMP_EV = 2048;

    private KardashevScreenGameTests() {}

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 40)
    public static void theScreenHasThreeTabsAndSyncsTheTeamReading(GameTestHelper h) {
        var player = TestPlayers.survival(h);
        var server = h.getLevel().getServer();
        UUID team = KardashevLedger.teamOf(player.getUUID());
        KardashevData data = KardashevData.get(server);
        long now = server.overworld().getGameTime() / SECOND * SECOND;
        double[] perSource = new double[KardashevSource.COUNT];
        perSource[KardashevSource.WIRELESS_ENERGY.ordinal()] = 30_000;
        perSource[KardashevSource.GENERATOR.ordinal()] = 12_000;
        perSource[KardashevSource.SPENT_ENERGY.ordinal()] = 7_000;
        data.record(team, perSource, now);

        BlockPos pos = h.absolutePos(new BlockPos(1, 1, 1));
        h.getLevel().setBlock(pos, KardashevMeter.BLOCK.get().defaultBlockState(), 3);
        try {
            var holder = new KardashevUIFactory.Holder(h.getLevel(), pos);
            h.assertTrue(!holder.isInvalid() && !holder.isRemote(), "the holder follows the meter block");
            var ui = KardashevWindow.create(holder, player);
            ui.initWidgets();
            h.assertTrue(ui.getFlatWidgetCollection().stream().filter(w -> w instanceof TabButton).count() == 3,
                    "Summary, History and Sources");
            var graphs = ui.getFlatWidgetCollection().stream().filter(w -> w instanceof KardashevGraphWidget).toList();
            h.assertTrue(graphs.size() == 1, "one graph");
            int bars = (int) ui.getFlatWidgetCollection().stream().filter(w -> w instanceof ProgressBarWidget).count();
            h.assertTrue(bars == 1 + KardashevSource.GENERATED, "the K bar and one bar per generation source: " + bars);

            var buffer = new FriendlyByteBuf(Unpooled.buffer());
            ui.mainGroup.writeInitialData(buffer);
            h.assertTrue(buffer.readableBytes() < 12_000, "the first sync is small: " + buffer.readableBytes() +
                    " bytes");
            h.assertTrue(buffer.readableBytes() >= 1_296, "and carries the three windows of the graph");

            var copy = KardashevWindow.create(holder, player);
            copy.initWidgets();
            copy.mainGroup.readInitialData(buffer);
            var lines = copy.getFlatWidgetCollection().stream().filter(w -> w instanceof InfoLinesWidget)
                    .map(w -> (InfoLinesWidget) w).toList();
            h.assertTrue(lines.size() >= 8 && lines.stream().allMatch(w -> w.lines().size() > 0),
                    "the text of every page reached the client copy: " + lines.size());
            var graph = (KardashevGraphWidget) copy.getFlatWidgetCollection().stream()
                    .filter(w -> w instanceof KardashevGraphWidget).findFirst().orElseThrow();
            float[] sixHours = graph.snapshot().generated()[2];
            h.assertTrue(sixHours[sixHours.length - 1] > 0, "the graph data reached the client copy");
            var view = data.view(team, now + 1);
            h.assertTrue(Math.abs(view.generatedPerSecond() - 42_000) < 1e-6 &&
                    Math.abs(view.spentPerSecond() - 7_000) < 1e-6, "the screen reads the viewer's team");
        } finally {
            h.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 20)
    public static void theHudIsRegisteredInTheLibRegistry(GameTestHelper h) throws IOException {
        String overlay = classBytes("com/typeomega/core/kardashev/client/KardashevHudOverlay");
        String client = classBytes("com/typeomega/core/kardashev/client/KardashevHudClient");
        String meter = classBytes("com/typeomega/core/kardashev/KardashevMeter");
        h.assertTrue(overlay.contains("com/typeomega/lib/ui/hud/MoveableHud"), "the overlay is a lib MoveableHud");
        h.assertTrue(overlay.contains("typeomega:kardashev"), "with the id typeomega:kardashev");
        h.assertTrue(client.contains("com/typeomega/lib/ui/hud/HudRegistry") && client.contains("register"),
                "the client setup registers it in the lib's HudRegistry");
        h.assertTrue(client.contains("openEditor"), "and the screen button opens the lib's editor");
        h.assertTrue(meter.contains("KardashevHudClient"), "the mod calls that client setup");
        h.assertTrue(classBytes("com/typeomega/lib/ui/hud/HudRegistry").contains("register"),
                "the lib on the classpath (0.8.4) has the registry");
        // EN: A dedicated server cannot load client classes, so the live check runs only on a physical client.
        // PT: Um servidor dedicado não carrega classes de cliente, então a checagem real só roda em cliente físico.
        // ZH: 专用服务器无法加载客户端类，因此实时检查仅在实体客户端上运行。
        if (FMLEnvironment.dist.isClient()) {
            try {
                var registry = Class.forName("com.typeomega.lib.ui.hud.HudRegistry");
                var present = (java.util.Optional<?>) registry.getMethod("get", String.class).invoke(null,
                        "typeomega:kardashev");
                h.assertTrue(present.isPresent(), "the HUD is in the registry at runtime");
            } catch (ReflectiveOperationException e) {
                h.fail("HudRegistry is not readable: " + e);
            }
        }
        h.succeed();
    }

    private static String classBytes(String path) throws IOException {
        try (InputStream in = KardashevScreenGameTests.class.getClassLoader().getResourceAsStream(path + ".class")) {
            if (in == null) throw new IOException("no class file " + path);
            return new String(in.readAllBytes(), StandardCharsets.ISO_8859_1);
        }
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 20)
    public static void hudStatsTravelInTwentyBytesAndFormThreeLines(GameTestHelper h) {
        var stats = new KardashevHudStats(84, 89, true, 1, 62_000f, 49_000f);
        var buffer = new FriendlyByteBuf(Unpooled.buffer());
        stats.write(buffer);
        h.assertTrue(buffer.readableBytes() <= 20, "one second of HUD is " + buffer.readableBytes() + " bytes");
        h.assertTrue(KardashevHudStats.read(buffer).equals(stats), "the stats survive the packet");
        h.assertTrue(KardashevUiText.hud(stats).size() == 3, "three lines: K, flow and the next type");
        h.assertTrue(KardashevUiText.hud(new KardashevHudStats(500, 599, false, 15, 0, 0)).size() == 3,
                "the last type still draws three lines");

        UUID player = UUID.randomUUID();
        h.assertTrue(!KardashevHudNet.subscribed(player), "nobody gets packets by default");
        KardashevHudNet.subscribe(player, true);
        h.assertTrue(KardashevHudNet.subscribed(player), "the client turned the HUD on");
        KardashevHudNet.subscribe(player, false);
        h.assertTrue(!KardashevHudNet.subscribed(player), "and off again");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 20)
    public static void nextTypeFollowsTheTierTable(GameTestHelper h) {
        var next = KardashevNext.of(84, 1, 1_024);
        h.assertTrue(next.type() == 0 && next.nextType() == 1 && !next.last(), "K 0.84 is Type 0, next Type I");
        h.assertTrue(next.gapHundredths() == 16, "0.16 K to go");
        h.assertTrue(Math.abs(next.euPerTickNeeded() - ONE_AMP_EV) < 1e-6, "Type I is 1 A EV (D-380)");
        h.assertTrue(Math.abs(next.multiplier() - 2) < 1e-9, "twice the average: " + next.multiplier());
        h.assertTrue(next.megasInType() == 1, "one megastructure of Type 0");
        var second = KardashevNext.of(100, 3, 0);
        h.assertTrue(second.nextType() == 2 && second.multiplier() == 0 && second.megasInType() == 0 &&
                Math.abs(second.euPerTickNeeded() - 131_072) < 1e-3, "Type II is 1 A ZPM");
        h.assertTrue(KardashevNext.of(500, 15, 1e30).last(), "Omega is the last type");
        for (double k : new double[] { 0.25, 0.9, 1, 1.5, 2, 3.3, 4, 4.7 }) {
            double eut = KardashevScale.euPerTickForK(k);
            h.assertTrue(Math.abs(KardashevScale.rawK(eut) - k) < 1e-9, "the inverse of the table at K " + k);
        }
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 20)
    public static void wattsAndSaganAreOnlyAnEquivalenceNextToTheOfficialK(GameTestHelper h) {
        h.assertTrue(KardashevScale.watts(1) == 1e12, "1 EU/t = 1 TW");
        h.assertTrue(Math.abs(KardashevScale.saganK(1e4) - 1.0) < 1e-9, "10 000 EU/t is Sagan K 1.00 (the proposal)");
        h.assertTrue(KardashevScale.saganK(0) == 0 && KardashevScale.saganK(1e-7) == 0, "floored at 0");
        h.assertTrue(KardashevScale.saganK(1e14) > 1.9 && KardashevScale.saganK(1e14) < 2.1, "10^14 EU/t is about II");
        // The official K stays the tier table, whatever Sagan says.
        h.assertTrue(KardashevScale.kHundredths(ONE_AMP_EV, 3, 0) == 100, "1 A EV is official K 1.00");
        h.assertTrue(KardashevScale.kHundredths(1e4, 3, 0) != 100, "10 000 EU/t is not, although Sagan says 1.00");
        h.succeed();
    }
}
