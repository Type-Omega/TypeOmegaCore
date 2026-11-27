package com.typeomega.core.mega.seeder;

/**
 * Physics of the Planetary Seeder Nexus (D-446 A6): an earlier-tier orbital bio-seeder kilostructure that runs
 * Greenhouse recipes in parallel, outputs Biomass, and provides a bio-seeding boost to a companion Yggdrasil Orchid
 * in the same star system.
 */
public final class SeederPhysics {

    public static final int PHASES = 5;
    public static final int PARALLEL_PER_PHASE = 64;
    public static final int BIOMASS_PER_CRAFT = 50;
    public static final int FLUX_STEP = 10;
    public static final int FLUX_MIN = 10;
    public static final int FLUX_MAX = 100;
    public static final double SPEED_MIN = 0.5;
    public static final double SPEED_MAX = 3.0;
    public static final double YGGDRASIL_BOOST = 1.25;

    private SeederPhysics() {}

    /** Parallel batch size for the built phase count (64 per phase, up to 320 at phase 5). */
    public static int parallel(int phases) {
        return PARALLEL_PER_PHASE * Math.max(1, Math.min(PHASES, phases));
    }

    /** Seeding speed multiplier from player-selected flux (10%..100%) and stellar luminosity. */
    public static double speed(int fluxPct, double luminosity, int phases) {
        int clamped = Math.max(FLUX_MIN, Math.min(FLUX_MAX, fluxPct));
        double lightFactor = 0.65 + 0.35 * Math.min(1.5, Math.max(0.0, luminosity));
        double phaseFactor = 0.6 + 0.4 * Math.max(1, Math.min(PHASES, phases)) / (double) PHASES;
        double base = SPEED_MIN + (SPEED_MAX - SPEED_MIN) * (clamped / 100.0);
        return Math.max(SPEED_MIN, Math.min(SPEED_MAX, base * lightFactor * phaseFactor));
    }
}
