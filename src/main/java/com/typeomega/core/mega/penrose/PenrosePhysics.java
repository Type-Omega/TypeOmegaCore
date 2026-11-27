package com.typeomega.core.mega.penrose;

/**
 * Physics of the Penrose Sphere (D-446 A3): superradiant scattering and Penrose extraction from a black hole's
 * ergosphere, sharing the black hole with the HRAE-MC.
 */
public final class PenrosePhysics {

    public static final int PHASES = 3;
    public static final int SHARE_STEP = 10;
    public static final double MAX_SPIN = 0.998;
    /** Penrose extraction at full share and full spin, EU/t per M☉. */
    public static final double PENROSE_EUT = Math.pow(2, 38);
    /** The hole's mass-energy in play, EU per M☉ (only its rotational share can be taken). */
    public static final double EU_PER_SOLAR_MASS = Math.pow(2, 62);

    private PenrosePhysics() {}

    /** Share of the mass held as rotation: 1 - M_irr / M. */
    public static double rotationalShare(double a) {
        return 1 - Math.sqrt((1 + Math.sqrt(1 - a * a)) / 2);
    }

    /** Penrose power extracted per tick, EU/t (requires phase 3 Penrose rings). */
    public static double penroseEut(int sharePct, double a, double massSun, int phases) {
        if (phases < PHASES) return 0;
        int clamped = Math.max(0, Math.min(100, sharePct));
        return PENROSE_EUT * massSun * (clamped / 100.0) * Math.max(0, a);
    }

    /** Spin lost in one second due to Penrose extraction. */
    public static double spinDrain(double penroseEut, double a, double massSun) {
        if (penroseEut <= 0 || a <= 0) return 0;
        double share = rotationalShare(a), next = rotationalShare(Math.max(0, a - 1e-3));
        double perSpin = Math.max(1e-9, (share - next) / 1e-3) * massSun * EU_PER_SOLAR_MASS;
        return penroseEut * 20.0 / perSpin;
    }
}
