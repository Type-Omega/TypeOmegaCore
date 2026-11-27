package com.typeomega.core.mega.shipyard;

/**
 * Physics of the Equatorial Shipyard (D-446 A6): a smaller twin-ring sibling of the Assembly Yard whose inner ring
 * harvests stellar energy and whose outer ring assembles hulls across six radial gantries.
 *
 * <ul>
 * <li>Phase 1: concentric scaffold rings and service mount (0 slipways).</li>
 * <li>Phase 2: inner harvesting ring opens 6 auxiliary slipways and cuts EU draw by 20%.</li>
 * <li>Phase 3: outer assembly ring and 6 gantries open 24 slipways total, 8 parallels each (192 max).</li>
 * <li>Starlight scales speed on a dampened log scale, 1 + 0.5 * log10(L/L☉), clamped to [0.5, 2.5].</li>
 * <li>Rush shift runs 1.5x faster at 2.0x EU; megastructure controller recipes cost 0.5x EU.</li>
 * </ul>
 */
public final class EquatorialShipyardPhysics {

    public static final int PHASES = 3;
    public static final int SLIPWAYS_PHASE_2 = 6;
    public static final int SLIPWAYS_PHASE_3 = 24;
    public static final int PARALLEL_PER_SLIPWAY = 8;
    public static final int STEP = 2;
    public static final double SPEED_MIN = 0.5;
    public static final double SPEED_MAX = 2.5;
    public static final double HARVEST_EU_FACTOR = 0.8;
    public static final double RUSH_SPEED = 1.5;
    public static final double RUSH_EU = 2.0;
    public static final double MEGA_DISCOUNT = 0.5;

    private EquatorialShipyardPhysics() {}

    public static int slipways(int phases) {
        if (phases >= 3) return SLIPWAYS_PHASE_3;
        if (phases >= 2) return SLIPWAYS_PHASE_2;
        return 0;
    }

    public static int parallel(int active) {
        return PARALLEL_PER_SLIPWAY * Math.max(0, active);
    }

    /** Build-rate multiplier from the star's luminosity (L☉). */
    public static double speed(double luminosity, boolean rush) {
        double s = luminosity <= 0 ? SPEED_MIN : 1.0 + 0.5 * Math.log10(luminosity);
        s = Math.max(SPEED_MIN, Math.min(SPEED_MAX, s));
        return rush ? s * RUSH_SPEED : s;
    }

    /** EU multiplier: inner harvesting ring (phase 2+) cuts draw by 20%; rush shift doubles base draw. */
    public static double euFactor(int phases, boolean rush) {
        double harvest = phases >= 2 ? HARVEST_EU_FACTOR : 1.0;
        return harvest * (rush ? RUSH_EU : 1.0);
    }
}
