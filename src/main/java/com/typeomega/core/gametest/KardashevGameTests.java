package com.typeomega.core.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.GTIAMod;
import com.typeomega.core.kardashev.KardashevData;
import com.typeomega.core.kardashev.KardashevLedger;
import com.typeomega.core.kardashev.KardashevMeter;
import com.typeomega.core.kardashev.KardashevScale;
import com.typeomega.core.kardashev.KardashevText;

import java.util.UUID;

/** Kardashev Meter (GTIA-D-372, D-380, D-386, D-389, D-406): the tier table, the caps, the window and the sources. */
@PrefixGameTestTemplate(false)
@GameTestHolder(GTIAMod.MOD_ID)
public final class KardashevGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";
    private static final double ONE_AMP_EV = 2048;

    private KardashevGameTests() {}

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 20)
    public static void tableAnchorsAreTheIntegers(GameTestHelper h) {
        expect(h, 0, KardashevScale.rawK(8), "8 EU/t");
        expect(h, 1, KardashevScale.rawK(ONE_AMP_EV), "1 A EV");
        expect(h, 2, KardashevScale.rawK(131_072), "1 A ZPM");
        expect(h, 3, KardashevScale.rawK(8_388_608), "1 A UEV");
        expect(h, 4, KardashevScale.rawK(536_870_912), "1 A OpV");
        expect(h, 0, KardashevScale.rawK(0), "no power");
        expect(h, 0, KardashevScale.rawK(5), "below 8 EU/t");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 20)
    public static void kIsLinearInLog4BetweenAnchors(GameTestHelper h) {
        expect(h, 0.25, KardashevScale.rawK(32), "1 A LV");
        expect(h, 0.75, KardashevScale.rawK(512), "1 A HV");
        expect(h, 4.0 / 3, KardashevScale.rawK(8192), "1 A IV");
        expect(h, 5.0 / 3, KardashevScale.rawK(32_768), "1 A LuV");
        expect(h, 2.5, KardashevScale.rawK(1_048_576), "half way ZPM to UEV in log4");
        expect(h, 13.0 / 3, KardashevScale.rawK(2_147_483_648.0), "1 A MAX, past OpV on the last slope");
        // The 0,1 reading quest: about 28 mB/t of steam is K 0.10 (GTIA-D-389).
        h.assertTrue(KardashevScale.rawHundredths(KardashevScale.steamToEu(28)) == 10, "28 mB/t of steam is K 0.10");
        h.assertTrue(KardashevScale.rawK(600) < KardashevScale.rawK(601), "K grows with power");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 20)
    public static void megastructuresRaiseTheCap(GameTestHelper h) {
        int[] caps = { 79, 89, 99, 179, 189, 199, 279 };
        for (int megas = 0; megas < caps.length; megas++) {
            h.assertTrue(KardashevScale.capHundredths(megas) == caps[megas], "cap with " + megas + " megas");
        }
        h.assertTrue(KardashevScale.kHundredths(ONE_AMP_EV, 0, 0) == 79, "1 A EV without megas stops at 0.79");
        h.assertTrue(KardashevScale.capped(ONE_AMP_EV, 0), "the meter says the cap holds K");
        h.assertTrue(KardashevScale.kHundredths(ONE_AMP_EV, 2, 0) == 99, "two megas: 0.99");
        h.assertTrue(KardashevScale.reachedType(1e12, 2) == 0, "no power reaches Type I before the third mega");
        h.assertTrue(KardashevScale.kHundredths(ONE_AMP_EV, 3, 0) == 100, "third mega opens Type I");
        h.assertTrue(KardashevScale.reachedType(ONE_AMP_EV, 3) == 1, "1 A EV after the third mega is Type I");
        h.assertTrue(!KardashevScale.capped(512, 0), "1 A HV is under the first cap");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 20)
    public static void reachedTypeNeverDrops(GameTestHelper h) {
        h.assertTrue(KardashevScale.kHundredths(0, 3, 1) == 100, "only the decimal drops");
        h.assertTrue(KardashevScale.kHundredths(512, 3, 1) == 100, "K never shows below the type reached");
        h.assertTrue(KardashevScale.kHundredths(0, 0, 0) == 0, "a new team starts at 0.00");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 20)
    public static void tenMinuteWindowAveragesCompleteBuckets(GameTestHelper h) {
        KardashevData data = new KardashevData();
        UUID team = UUID.randomUUID();
        int bucket = KardashevData.BUCKET_TICKS;
        for (int epoch = 0; epoch < KardashevData.BUCKETS; epoch++) {
            data.add(team, ONE_AMP_EV * bucket, (long) epoch * bucket + 7);
        }
        long now = (long) KardashevData.BUCKETS * bucket;
        expect(h, ONE_AMP_EV, data.averageEuPerTick(team, now), "ten full minutes at 1 A EV");
        expect(h, ONE_AMP_EV, data.recentEuPerTick(team, now), "last 20 s at 1 A EV");
        var capped = data.read(team, now);
        h.assertTrue(capped.kHundredths() == 79 && capped.capped(), "no megas: K 0.79, capped");
        h.assertTrue(KardashevText.reading(capped).size() == 6, "capped reading adds the cap hint");

        data.setMegastructures(team, 3, now);
        var open = data.read(team, now);
        h.assertTrue(open.kHundredths() == 100 && open.reachedType() == 1, "third mega: K 1.00, Type I reached");
        h.assertTrue(KardashevText.reading(open).size() == 5, "uncapped reading has five lines");

        long later = now + KardashevData.WINDOW_TICKS + bucket;
        expect(h, 0, data.averageEuPerTick(team, later), "the window forgets old power");
        h.assertTrue(data.read(team, later).kHundredths() == 100, "Type I stays after the power is gone");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 20)
    public static void windowIgnoresTheOpenBucketAndYoungTeamsCountTheWholeWindow(GameTestHelper h) {
        KardashevData data = new KardashevData();
        UUID team = UUID.randomUUID();
        int bucket = KardashevData.BUCKET_TICKS;
        data.add(team, ONE_AMP_EV * bucket, 0);
        expect(h, 0, data.averageEuPerTick(team, bucket - 1), "the bucket being filled is not counted yet");
        expect(h, ONE_AMP_EV / KardashevData.BUCKETS, data.averageEuPerTick(team, bucket),
                "20 s of power in a new team is divided by the whole 10 minutes");
        data.add(team, -5, bucket);
        data.add(team, Double.NaN, bucket);
        expect(h, ONE_AMP_EV / KardashevData.BUCKETS, data.averageEuPerTick(team, bucket),
                "negative and NaN reports are ignored");
        expect(h, 0, data.averageEuPerTick(UUID.randomUUID(), bucket), "an unknown team has no power");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 20)
    public static void wirelessNetworksReportToTheLedger(GameTestHelper h) {
        h.assertTrue(KardashevMeter.BLOCK.get().asItem() != net.minecraft.world.item.Items.AIR, "meter has an item");
        h.assertTrue(h.getLevel().getServer().getRecipeManager().byKey(GTIAMod.id("kardashev_meter")).isPresent(),
                "meter has its crafting recipe (GTIA-D-406)");
        if (!ModList.get().isLoaded("gtna")) {
            h.succeed();
            return;
        }
        UUID owner = UUID.randomUUID();
        try {
            GtnaSources.check(h, owner);
        } finally {
            KardashevLedger.discard(owner);
        }
        h.succeed();
    }

    /** GTNA calls live in their own class so the test class loads without GTNA. */
    private static final class GtnaSources {

        static void check(GameTestHelper h, UUID owner) {
            var level = h.getLevel();
            var steam = com.raishxn.gtna.common.data.SteamNetworkData.get(level);
            h.assertTrue(steam.addSteam(owner, 2_000), "steam add accepted");
            expect(h, 1_000, KardashevLedger.pending(owner), "2000 mB of steam is 1000 EU");
            h.assertTrue(!steam.addSteam(owner, -5_000), "overdraft rejected");
            h.assertTrue(steam.addSteam(owner, -500), "withdrawal accepted");
            expect(h, 1_000, KardashevLedger.pending(owner), "withdrawals and rejected adds are not production");

            var energy = com.raishxn.gtna.common.data.NexusEnergyNetwork.get(level);
            energy.addEnergy(owner, new com.typeomega.lib.data.Int128(-4_096), level);
            energy.addEnergy(owner, com.typeomega.lib.data.Int128.ZERO(), level);
            expect(h, 1_000, KardashevLedger.pending(owner), "zero and negative EU are not production");
            energy.setMatrixStats(owner, 1, 1, 1.0, new com.typeomega.lib.data.Int128(1L << 40), true);
            energy.setMatrixDimension(owner, level.dimension());
            var gross = energy.addEnergy(owner, new com.typeomega.lib.data.Int128(4_096), level);
            expect(h, 1_000 + gross.doubleValue(), KardashevLedger.pending(owner), "the gross EU the network took");
            h.assertTrue(gross.isPositive(), "the network took the EU");
        }
    }

    private static void expect(GameTestHelper h, double expected, double actual, String what) {
        h.assertTrue(Math.abs(expected - actual) < 1e-9 * Math.max(1, Math.abs(expected)),
                what + ": expected " + expected + ", got " + actual);
    }
}
