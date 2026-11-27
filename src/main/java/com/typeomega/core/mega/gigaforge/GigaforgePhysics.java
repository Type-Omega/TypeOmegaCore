package com.typeomega.core.mega.gigaforge;

/**
 * Physics of the Neutronium Gigaforge (D-446): extracts degenerate neutronium dust from the crust of a neutron
 * star, pulsar or magnetar.
 */
public final class GigaforgePhysics {

    public static final int PHASES = 3;
    public static final int RATE_MIN = 10, RATE_MAX = 100, RATE_STEP = 10;
    public static final double EU_PER_DUST = 5.0e8;

    private GigaforgePhysics() {}

    /** Base dust per second by built phases: 4 (core), 16 (processing rings), 64 (extractor drill). */
    public static int maxDustPerSecond(int phases) {
        return switch (Math.max(1, Math.min(PHASES, phases))) {
            case 1 -> 4;
            case 2 -> 16;
            default -> 64;
        };
    }

    public static int dustPerSecond(int phases, int ratePct) {
        int clamped = Math.max(0, Math.min(RATE_MAX, ratePct));
        return (int) Math.round(maxDustPerSecond(phases) * (clamped / 100.0));
    }

    public static double euPerSecond(int dust) {
        return dust * EU_PER_DUST;
    }
}
