package com.typeomega.core.mega.gateway;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.common.data.GTMaterials;

/**
 * Physics of the Basement Gateway (D-446 A6): companion megastructure to the Solar System Disintegrator,
 * channeling a neutron star's polar gamma bursts to sustain a micro-wormhole into a basement universe and
 * condense primordial gases and heavy r-process dusts.
 */
public final class BasementGatewayPhysics {

    public static final int PHASES = 5;
    public static final int APERTURE_MIN = 10, APERTURE_MAX = 100, APERTURE_STEP = 10;
    /** EU/t per built phase at 100% wormhole aperture. */
    public static final double EUT_PER_PHASE = Math.pow(2, 34);
    /** Hydrogen (mB/s) and Helium (mB/s) condensed per built phase at 100% aperture. */
    public static final int HYDROGEN_MB_PER_PHASE = 4000, HELIUM_MB_PER_PHASE = 1000;
    /** Total heavy r-process dust items/s condensed per built phase at 100% aperture. */
    public static final int DUST_PER_PHASE = 6;
    public static final Material[] DUST_YIELDS = {
            GTMaterials.Platinum, GTMaterials.Osmium, GTMaterials.Iridium
    };

    private BasementGatewayPhysics() {}

    public static int clampAperture(int pct) {
        return Math.max(APERTURE_MIN, Math.min(APERTURE_MAX, pct));
    }

    public static double eut(int phases, int aperturePct) {
        int p = Math.max(1, Math.min(PHASES, phases));
        return EUT_PER_PHASE * p * (clampAperture(aperturePct) / 100.0);
    }

    public static double euPerSecond(int phases, int aperturePct) {
        return eut(phases, aperturePct) * 20.0;
    }

    public static int hydrogenMb(int phases, int aperturePct) {
        int p = Math.max(1, Math.min(PHASES, phases));
        return (int) Math.round(HYDROGEN_MB_PER_PHASE * p * (clampAperture(aperturePct) / 100.0));
    }

    public static int heliumMb(int phases, int aperturePct) {
        int p = Math.max(1, Math.min(PHASES, phases));
        return (int) Math.round(HELIUM_MB_PER_PHASE * p * (clampAperture(aperturePct) / 100.0));
    }

    public static int dustPerSecond(int phases, int aperturePct) {
        int p = Math.max(1, Math.min(PHASES, phases));
        return Math.max(0, (int) Math.round(DUST_PER_PHASE * p * (clampAperture(aperturePct) / 100.0)));
    }
}
