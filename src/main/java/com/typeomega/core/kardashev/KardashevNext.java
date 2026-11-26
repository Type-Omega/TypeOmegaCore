package com.typeomega.core.kardashev;

/**
 * What the team still lacks for the next type (D-443): the K and the power of the next integer on the D-380 table,
 * how many times the 10-minute average that is, and the megastructures still open in the current type.
 */
public record KardashevNext(int type, boolean last, int nextType, int gapHundredths, double euPerTickNeeded,
                            double multiplier, int megasInType) {

    /** Types 0 to IV have three megastructures each and Ω is the last (D-373). */
    private static final int LAST_TYPE = 5;

    public static KardashevNext of(int kHundredths, int megastructures, double averageEuPerTick) {
        int type = Math.max(0, Math.min(LAST_TYPE, kHundredths / 100));
        int megasInType = Math.max(0, megastructures - KardashevScale.capType(megastructures) *
                KardashevScale.MEGAS_PER_TYPE);
        if (type >= LAST_TYPE) return new KardashevNext(type, true, type, 0, 0, 0, megasInType);
        int next = type + 1;
        double needed = KardashevScale.euPerTickForK(next);
        double multiplier = averageEuPerTick > 0 ? needed / averageEuPerTick : 0;
        return new KardashevNext(type, false, next, next * 100 - kHundredths, needed, multiplier, megasInType);
    }
}
