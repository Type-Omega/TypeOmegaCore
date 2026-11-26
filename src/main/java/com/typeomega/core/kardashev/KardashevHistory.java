package com.typeomega.core.kardashev;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Arrays;

/**
 * The energy a team generated and spent, in three rings of buckets (D-443): 30 of 20 s, 60 of 1 min and 72 of
 * 5 min, so the graph reaches back 10 minutes, 1 hour and 6 hours. A bucket is two numbers (generated, spent) and an
 * epoch; the whole history is about 3.4 KB of NBT per team. The 20 s ring is also what K averages (D-372).
 */
public final class KardashevHistory {

    /** The three windows of the graph: bucket length in ticks and number of buckets. */
    public enum Window {

        TEN_MINUTES(400, 30),
        ONE_HOUR(1200, 60),
        SIX_HOURS(6000, 72);

        public final int bucketTicks;
        public final int buckets;

        Window(int bucketTicks, int buckets) {
            this.bucketTicks = bucketTicks;
            this.buckets = buckets;
        }
    }

    // Legacy v0 keys of the 20 s ring: Epochs and Sums (generated). Spent is new.
    private static final String EPOCHS = "Epochs", SUMS = "Sums", SPENT = "Spent";

    private final Ring[] rings = new Ring[Window.values().length];

    public KardashevHistory() {
        for (Window window : Window.values()) rings[window.ordinal()] = new Ring(window);
    }

    /** Adds what was generated and spent in the second that ended at {@code gameTime} to every ring. */
    public void add(double generated, double spent, long gameTime) {
        for (Ring ring : rings) ring.add(generated, spent, gameTime);
    }

    /** Generated EU of the {@code count} complete 20 s buckets before the current one (the window of K). */
    public double generatedWindow(long gameTime, int count) {
        return rings[Window.TEN_MINUTES.ordinal()].generatedWindow(gameTime, count);
    }

    /** Points of {@code window}, oldest to newest, in EU/s; the newest bucket is the one being filled. */
    public float[] series(Window window, boolean spent, long gameTime) {
        return rings[window.ordinal()].series(spent, gameTime);
    }

    public Snapshot snapshot(long gameTime) {
        float[][] generated = new float[rings.length][];
        float[][] spent = new float[rings.length][];
        for (Window window : Window.values()) {
            generated[window.ordinal()] = series(window, false, gameTime);
            spent[window.ordinal()] = series(window, true, gameTime);
        }
        return new Snapshot(generated, spent);
    }

    public void save(CompoundTag tag) {
        Ring fine = rings[Window.TEN_MINUTES.ordinal()];
        tag.putLongArray(EPOCHS, fine.epochs.clone());
        tag.put(SUMS, doubles(fine.generated));
        tag.put(SPENT, doubles(fine.spent));
        for (Window window : Window.values()) {
            if (window == Window.TEN_MINUTES) continue;
            Ring ring = rings[window.ordinal()];
            CompoundTag entry = new CompoundTag();
            int[] epochs = new int[ring.size];
            for (int i = 0; i < ring.size; i++) {
                epochs[i] = ring.epochs[i] == Long.MIN_VALUE ? Integer.MIN_VALUE : (int) ring.epochs[i];
            }
            entry.putIntArray("E", epochs);
            entry.put("G", doubles(ring.generated));
            entry.put("S", doubles(ring.spent));
            tag.put(window.name(), entry);
        }
    }

    public void load(CompoundTag tag) {
        Ring fine = rings[Window.TEN_MINUTES.ordinal()];
        fine.load(tag.getLongArray(EPOCHS), tag.getList(SUMS, Tag.TAG_DOUBLE), tag.getList(SPENT, Tag.TAG_DOUBLE));
        for (Window window : Window.values()) {
            if (window == Window.TEN_MINUTES || !tag.contains(window.name(), Tag.TAG_COMPOUND)) continue;
            CompoundTag entry = tag.getCompound(window.name());
            int[] stored = entry.getIntArray("E");
            long[] epochs = new long[stored.length];
            for (int i = 0; i < stored.length; i++) {
                epochs[i] = stored[i] == Integer.MIN_VALUE ? Long.MIN_VALUE : stored[i];
            }
            rings[window.ordinal()].load(epochs, entry.getList("G", Tag.TAG_DOUBLE),
                    entry.getList("S", Tag.TAG_DOUBLE));
        }
    }

    private static ListTag doubles(double[] values) {
        ListTag list = new ListTag();
        for (double value : values) list.add(DoubleTag.valueOf(value));
        return list;
    }

    /** The graph data of every window, in EU/s, ready to send to a client that has the screen open. */
    public record Snapshot(float[][] generated, float[][] spent) {

        public static final Snapshot EMPTY = new Snapshot(empty(), empty());

        private static float[][] empty() {
            float[][] out = new float[Window.values().length][];
            for (Window window : Window.values()) out[window.ordinal()] = new float[window.buckets];
            return out;
        }

        public void write(FriendlyByteBuf buf) {
            for (Window window : Window.values()) {
                for (float value : generated[window.ordinal()]) buf.writeFloat(value);
                for (float value : spent[window.ordinal()]) buf.writeFloat(value);
            }
        }

        public static Snapshot read(FriendlyByteBuf buf) {
            float[][] generated = empty(), spent = empty();
            for (Window window : Window.values()) {
                for (int i = 0; i < window.buckets; i++) generated[window.ordinal()][i] = buf.readFloat();
                for (int i = 0; i < window.buckets; i++) spent[window.ordinal()][i] = buf.readFloat();
            }
            return new Snapshot(generated, spent);
        }
    }

    private static final class Ring {

        final int ticks, size;
        final long[] epochs;
        final double[] generated, spent;

        Ring(Window window) {
            ticks = window.bucketTicks;
            size = window.buckets;
            epochs = new long[size];
            generated = new double[size];
            spent = new double[size];
            Arrays.fill(epochs, Long.MIN_VALUE);
        }

        void add(double gen, double out, long gameTime) {
            long epoch = gameTime / ticks;
            int slot = (int) Math.floorMod(epoch, (long) size);
            if (epochs[slot] != epoch) {
                epochs[slot] = epoch;
                generated[slot] = 0;
                spent[slot] = 0;
            }
            generated[slot] += gen;
            spent[slot] += out;
        }

        double generatedWindow(long gameTime, int count) {
            long current = gameTime / ticks;
            double sum = 0;
            for (int i = 0; i < size; i++) {
                long age = current - epochs[i];
                if (age >= 1 && age <= count) sum += generated[i];
            }
            return sum;
        }

        float[] series(boolean wantSpent, long gameTime) {
            long current = gameTime / ticks;
            double[] source = wantSpent ? spent : generated;
            float[] out = new float[size];
            for (int i = 0; i < size; i++) {
                long epoch = current - (size - 1) + i;
                int slot = (int) Math.floorMod(epoch, (long) size);
                double sum = epochs[slot] == epoch ? source[slot] : 0;
                // EN: The bucket being filled holds only the seconds flushed so far, so its rate divides by those.
                // PT: O balde em preenchimento tem só os segundos já esvaziados, então a taxa divide por eles.
                // ZH: 正在填充的桶只含已汇总的秒数，因此其速率按这些秒数计算。
                double seconds = epoch == current ?
                        Math.min(ticks, gameTime % ticks + KardashevLedger.FLUSH_TICKS) / 20.0 : ticks / 20.0;
                out[i] = (float) Math.min(Float.MAX_VALUE, sum / seconds);
            }
            return out;
        }

        void load(long[] storedEpochs, ListTag gen, ListTag out) {
            for (int i = 0; i < size && i < storedEpochs.length; i++) {
                epochs[i] = storedEpochs[i];
                generated[i] = i < gen.size() ? gen.getDouble(i) : 0;
                spent[i] = i < out.size() ? out.getDouble(i) : 0;
            }
        }
    }
}
