package com.typeomega.core.gametest.kardashev;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.kardashev.KardashevData;
import com.typeomega.core.kardashev.KardashevHistory;
import com.typeomega.core.kardashev.KardashevSource;

import java.util.UUID;

/**
 * Kardashev Meter history (D-443): generated and spent in 20 s, 1 min and 5 min buckets over 6 hours, driven by a
 * clock the test moves; the K of the D-380 table does not change, and the saved size stays under 5 KB per team.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class KardashevHistoryGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";
    private static final int SECOND = 20;
    private static final double ONE_AMP_EV = 2048;

    private KardashevHistoryGameTests() {}

    /** One flush per second from second {@code from} (inclusive) to {@code to} (exclusive). */
    private static void run(KardashevData data, UUID team, int from, int to, double generated, double spent) {
        double[] perSource = new double[KardashevSource.COUNT];
        for (int second = from; second < to; second++) {
            perSource[KardashevSource.WIRELESS_ENERGY.ordinal()] = generated;
            perSource[KardashevSource.SPENT_ENERGY.ordinal()] = spent;
            data.record(team, perSource, (long) second * SECOND);
        }
    }

    private static int nonZero(float[] series) {
        int count = 0;
        for (float value : series) if (value > 0) count++;
        return count;
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 20)
    public static void bucketsRollOverSixHours(GameTestHelper h) {
        KardashevData data = new KardashevData();
        UUID team = UUID.randomUUID();
        // EN: One hour of 1000 EU/s, then two hours of silence: 12 five-minute buckets are full, the rest are empty.
        // PT: Uma hora de 1000 EU/s e depois duas horas de silêncio: 12 baldes de 5 min cheios, o resto vazio.
        // ZH: 先运行一小时（1000 EU/s），再静默两小时：12 个 5 分钟桶有数据，其余为空。
        run(data, team, 0, 3600, 1000, 400);
        long threeHours = 3L * 3600 * SECOND;
        var snapshot = data.history(team, threeHours);
        int six = KardashevHistory.Window.SIX_HOURS.ordinal();
        h.assertTrue(snapshot.generated()[six].length == 72, "72 five-minute points");
        h.assertTrue(nonZero(snapshot.generated()[six]) == 12, "twelve 5-minute buckets hold the first hour: " +
                nonZero(snapshot.generated()[six]));
        h.assertTrue(nonZero(snapshot.spent()[six]) == 12, "spent rolls the same way");
        for (float value : snapshot.generated()[six]) {
            h.assertTrue(value == 0 || Math.abs(value - 1000) < 0.01, "a full bucket is 1000 EU/s, got " + value);
        }
        h.assertTrue(nonZero(snapshot.generated()[KardashevHistory.Window.ONE_HOUR.ordinal()]) == 0,
                "the 1-hour window (1 min buckets) is empty two hours later");
        h.assertTrue(nonZero(snapshot.generated()[KardashevHistory.Window.TEN_MINUTES.ordinal()]) == 0,
                "the 10-minute window is empty too");

        // Seven hours of steady power: the oldest bucket is overwritten and every point of the 6 h window is full.
        run(data, team, 3600, 7 * 3600, 1000, 0);
        long sevenHours = (7L * 3600 - 1) * SECOND + 1;
        var full = data.history(team, sevenHours).generated()[six];
        for (float value : full) h.assertTrue(Math.abs(value - 1000) < 0.01, "steady 1000 EU/s everywhere: " + value);
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 20)
    public static void theBucketBeingFilledShowsItsOwnRate(GameTestHelper h) {
        KardashevData data = new KardashevData();
        UUID team = UUID.randomUUID();
        // Five minutes of silence, then three seconds of 600 EU/s inside the next 20 s bucket.
        run(data, team, 300, 303, 600, 0);
        var ten = data.history(team, 302L * SECOND).generated()[KardashevHistory.Window.TEN_MINUTES.ordinal()];
        float newest = ten[ten.length - 1];
        h.assertTrue(Math.abs(newest - 600) < 0.5, "the open bucket divides by the seconds so far: " + newest);
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 20)
    public static void generatedAndSpentAreAccountedAndKIgnoresSpending(GameTestHelper h) {
        KardashevData data = new KardashevData();
        UUID team = UUID.randomUUID();
        double[] perSource = new double[KardashevSource.COUNT];
        int bucket = KardashevData.BUCKET_TICKS;
        // Ten minutes at 1 A EV generated, with a heavy spending next to it.
        for (int second = 0; second < 600; second++) {
            perSource[KardashevSource.WIRELESS_ENERGY.ordinal()] = ONE_AMP_EV * 20 * 0.5;
            perSource[KardashevSource.GENERATOR.ordinal()] = ONE_AMP_EV * 20 * 0.25;
            perSource[KardashevSource.BOILER.ordinal()] = ONE_AMP_EV * 20 * 0.25;
            perSource[KardashevSource.SPENT_ENERGY.ordinal()] = 30_000;
            perSource[KardashevSource.SPENT_STEAM.ordinal()] = 5_000;
            data.record(team, perSource, (long) second * SECOND);
        }
        long now = (long) KardashevData.BUCKETS * bucket;
        h.assertTrue(Math.abs(data.averageEuPerTick(team, now) - ONE_AMP_EV) < 1e-6,
                "K averages generation only: " + data.averageEuPerTick(team, now));
        var reading = data.read(team, now);
        h.assertTrue(reading.kHundredths() == 79 && reading.capped(), "1 A EV without megas is K 0.79, capped");
        var view = data.view(team, now);
        h.assertTrue(Math.abs(view.generatedPerSecond() - ONE_AMP_EV * 20) < 1e-6,
                "generated per second is the sum of " + "the sources: " + view.generatedPerSecond());
        h.assertTrue(Math.abs(view.spentPerSecond() - 35_000) < 1e-6,
                "spent per second is the sum of both networks: " + view.spentPerSecond());
        h.assertTrue(Math.abs(view.average20()[KardashevSource.GENERATOR.ordinal()] - ONE_AMP_EV * 5) < 1e-6,
                "the 20 s average of a source");
        var six = data.history(team, now - 1).spent()[KardashevHistory.Window.SIX_HOURS.ordinal()];
        float last = six[six.length - 1];
        h.assertTrue(Math.abs(last - 35_000) < 1, "the graph holds spent too: " + last);
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 20)
    public static void lastSecondReadsTheSameAroundAFlush(GameTestHelper h) {
        KardashevData data = new KardashevData();
        UUID team = UUID.randomUUID();
        run(data, team, 0, 5, 100, 0);
        long beforeFlush = 5L * SECOND;
        double before = data.view(team, beforeFlush).generatedPerSecond();
        run(data, team, 5, 6, 700, 0);
        double after = data.view(team, beforeFlush).generatedPerSecond();
        h.assertTrue(before == 100 && after == 100, "a read in the tick of a flush sees the last complete second");
        h.assertTrue(data.view(team, beforeFlush + 1).generatedPerSecond() == 700, "one tick later it is the new one");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 40)
    public static void savedSizeStaysUnderFiveKilobytes(GameTestHelper h) {
        KardashevData data = new KardashevData();
        UUID team = UUID.randomUUID();
        int seven = 7 * 3600;
        run(data, team, 0, seven, 123_456.789, 98_765.4321);
        for (int i = 0; i < 15; i++) data.markBuilt(team, "megastructure_number_" + i, (long) seven * SECOND);
        int size = data.savedSize(team);
        h.assertTrue(size < 5_000, "a full team saves in " + size + " bytes");

        var tag = data.save(new CompoundTag());
        KardashevData loaded = KardashevData.fromTag(tag);
        long now = (long) seven * SECOND;
        for (var window : KardashevHistory.Window.values()) {
            h.assertTrue(java.util.Arrays.equals(data.history(team, now).generated()[window.ordinal()],
                    loaded.history(team, now).generated()[window.ordinal()]), window + " generated survives a save");
            h.assertTrue(java.util.Arrays.equals(data.history(team, now).spent()[window.ordinal()],
                    loaded.history(team, now).spent()[window.ordinal()]), window + " spent survives a save");
        }
        h.assertTrue(loaded.megastructures(team) == 15, "the megastructures survive too");
        h.assertTrue(loaded.averageEuPerTick(team, now) == data.averageEuPerTick(team, now), "and the K window");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 20)
    public static void oldSavesLoadWithTheirTenMinutes(GameTestHelper h) {
        // The v0 format: Epochs and Sums of the 20 s ring, no Spent and no longer rings.
        UUID team = UUID.randomUUID();
        CompoundTag entry = new CompoundTag();
        entry.putUUID("Team", team);
        entry.putInt("Megastructures", 1);
        entry.put("Built", new ListTag());
        entry.putInt("ReachedType", 0);
        long[] epochs = new long[KardashevData.BUCKETS];
        ListTag sums = new ListTag();
        for (int i = 0; i < KardashevData.BUCKETS; i++) {
            epochs[i] = i;
            sums.add(DoubleTag.valueOf(ONE_AMP_EV * KardashevData.BUCKET_TICKS));
        }
        entry.putLongArray("Epochs", epochs);
        entry.put("Sums", sums);
        ListTag teams = new ListTag();
        teams.add(entry);
        CompoundTag tag = new CompoundTag();
        tag.put("Teams", teams);
        KardashevData loaded = KardashevData.fromTag(tag);
        long now = (long) KardashevData.BUCKETS * KardashevData.BUCKET_TICKS;
        h.assertTrue(Math.abs(loaded.averageEuPerTick(team, now) - ONE_AMP_EV) < 1e-6, "the old window still averages");
        h.assertTrue(loaded.megastructures(team) == 1, "and the old count is kept as a floor");
        h.assertTrue(loaded.history(team, now).spent()[0].length == 30, "the new rings start empty, not broken");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 20)
    public static void builtIdsAreStoredOnceAndKeyedByTeam(GameTestHelper h) {
        KardashevData data = new KardashevData();
        UUID a = UUID.randomUUID(), b = UUID.randomUUID();
        h.assertTrue(data.markBuilt(a, "x", 0) && !data.markBuilt(a, "x", 0), "a megastructure counts once");
        h.assertTrue(data.built(a, "x") && !data.built(b, "x"), "and only for its team");
        var tag = data.save(new CompoundTag());
        ListTag list = tag.getList("Teams", Tag.TAG_COMPOUND);
        h.assertTrue(list.size() == 1 && list.getCompound(0).getList("Built", Tag.TAG_STRING).size() == 1 &&
                list.getCompound(0).getList("Built", Tag.TAG_STRING).get(0) instanceof StringTag, "saved once");
        h.succeed();
    }
}
