package com.typeomega.core.kardashev;

import java.util.Arrays;

/**
 * The last 20 seconds of a team, one slot per second and source: what the Sources tab and the HUD read. Memory only,
 * not saved; after a restart it fills again in 20 seconds.
 */
final class KardashevLive {

    static final int SECONDS = 20;
    private static final int PER_SECOND = KardashevLedger.FLUSH_TICKS;

    private final long[] epochs = new long[SECONDS];
    private final double[][] values = new double[SECONDS][KardashevSource.COUNT];

    KardashevLive() {
        Arrays.fill(epochs, Long.MIN_VALUE);
    }

    /** Adds what each source reported in the second that ended at {@code gameTime}. */
    void add(double[] perSource, long gameTime) {
        long epoch = gameTime / PER_SECOND;
        int slot = (int) Math.floorMod(epoch, (long) SECONDS);
        if (epochs[slot] != epoch) {
            epochs[slot] = epoch;
            Arrays.fill(values[slot], 0);
        }
        for (int i = 0; i < perSource.length; i++) values[slot][i] += perSource[i];
    }

    /**
     * Fills {@code last} with the EU/s of the last complete second and {@code average} with the mean of the last 20.
     * Reads {@code (gameTime - 1) / 20}, so a read in the very tick of a flush gives the same answer before and after
     * it.
     */
    void read(long gameTime, double[] last, double[] average) {
        long newest = Math.floorDiv(gameTime - 1, (long) PER_SECOND);
        Arrays.fill(last, 0);
        Arrays.fill(average, 0);
        for (int i = 0; i < SECONDS; i++) {
            long age = newest - epochs[i];
            if (age < 0 || age >= SECONDS) continue;
            for (int s = 0; s < KardashevSource.COUNT; s++) {
                average[s] += values[i][s] / SECONDS;
                if (age == 0) last[s] = values[i][s];
            }
        }
    }
}
