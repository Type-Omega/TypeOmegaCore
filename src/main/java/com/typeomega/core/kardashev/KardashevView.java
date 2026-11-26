package com.typeomega.core.kardashev;

import java.util.Set;

/**
 * Everything the screen shows about one team at one moment (D-443), built on the server from {@link KardashevData}.
 * {@code lastSecond} and {@code average20} are EU/s per {@link KardashevSource}; K and the cap come from the D-380
 * tier table, the watts and Sagan's K are an informative extra.
 */
public record KardashevView(KardashevData.Reading reading, double[] lastSecond, double[] average20, Set<String> built) {

    /** A team that has reported nothing yet; what a client-side copy of the screen holds. */
    public static KardashevView empty() {
        return new KardashevView(new KardashevData.Reading(0, 0, 0, 0, 0, KardashevScale.capHundredths(0), false),
                new double[KardashevSource.COUNT], new double[KardashevSource.COUNT], Set.of());
    }

    public double generatedPerSecond() {
        double sum = 0;
        for (int i = 0; i < KardashevSource.GENERATED; i++) sum += lastSecond[i];
        return sum;
    }

    public double spentPerSecond() {
        double sum = 0;
        for (int i = KardashevSource.GENERATED; i < KardashevSource.COUNT; i++) sum += lastSecond[i];
        return sum;
    }

    public double generatedAverage() {
        double sum = 0;
        for (int i = 0; i < KardashevSource.GENERATED; i++) sum += average20[i];
        return sum;
    }

    public double spentAverage() {
        double sum = 0;
        for (int i = KardashevSource.GENERATED; i < KardashevSource.COUNT; i++) sum += average20[i];
        return sum;
    }

    public KardashevHudStats hud() {
        return new KardashevHudStats(reading.kHundredths(), reading.capHundredths(), reading.capped(),
                reading.megastructures(), (float) generatedPerSecond(), (float) spentPerSecond());
    }
}
