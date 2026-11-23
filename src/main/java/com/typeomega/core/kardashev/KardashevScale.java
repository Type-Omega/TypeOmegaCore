package com.typeomega.core.kardashev;

/**
 * The Kardashev number K of a team (GTIA-D-372, D-380, D-389). Pure arithmetic: the meter, the commands and the
 * GameTests all read K from here.
 * <p>
 * K is a table over the GregTech tier of the average power: the tier position is {@code log4(EU/t ÷ 8)} (ULV = 0,
 * LV = 1 … OpV = 13), and K is a straight line between the points of {@link #TIER_POINTS}. The cap comes from the
 * megastructures built; the integer already reached never drops.
 */
public final class KardashevScale {

    // EN: GTIA-D-380: 8 EU/t is K 0 and one amp of the first tier of each type is the integer (EV 1, ZPM 2,
    // UEV 3, OpV 4); between these points K is linear in log4 of EU/t.
    // PT: GTIA-D-380: 8 EU/t é K 0 e 1 A do primeiro tier de cada tipo é o inteiro (EV 1, ZPM 2, UEV 3, OpV 4);
    // entre esses pontos o K é linear no log4 do EU/t.
    // ZH: GTIA-D-380：8 EU/t 为 K 0，每个类型首个电压等级的 1 安培为整数（EV 1、ZPM 2、UEV 3、OpV 4）；
    // 两点之间 K 随 EU/t 的 log4 线性变化。
    static final double[][] TIER_POINTS = { { 0, 0 }, { 4, 1 }, { 7, 2 }, { 10, 3 }, { 13, 4 } };

    // EN: GTIA-D-389: steam counts at 2 mB = 1 EU, the GTCEu steam-to-EU rate.
    // PT: GTIA-D-389: vapor conta a 2 mB = 1 EU, a taxa vapor-EU do GTCEu.
    // ZH: GTIA-D-389：蒸汽按 2 mB = 1 EU 计入，即 GTCEu 的蒸汽换算比例。
    public static final double STEAM_MB_PER_EU = 2.0;

    // EN: GTIA-D-389: decimal cap inside a type with 0, 1 and 2 of its three megastructures; the third closes the
    // type and opens the next one's 0.79.
    // PT: GTIA-D-389: teto decimal dentro do tipo com 0, 1 e 2 das três megaestruturas; a terceira fecha o tipo e
    // abre o 0,79 do seguinte.
    // ZH: GTIA-D-389：在同一类型内建成 0、1、2 座巨构时的小数上限；第三座结束该类型并开放下一类型的 0.79。
    static final int[] CAP_HUNDREDTHS = { 79, 89, 99 };

    public static final int MEGAS_PER_TYPE = 3;
    /** Types 0 to IV have three megastructures each (GTIA-D-373); Ω has none yet. */
    public static final int MAX_MEGASTRUCTURES = 5 * MEGAS_PER_TYPE;

    private static final double LOG4 = Math.log(4);

    private KardashevScale() {}

    /** Continuous tier position of a power: 8 EU/t = 0 (ULV), 32 = 1 (LV) … ; below 8 EU/t it is 0. */
    public static double tierPosition(double euPerTick) {
        if (!(euPerTick > 8)) return 0;
        return Math.log(euPerTick / 8) / LOG4;
    }

    /** K of a power before the cap: the tier table, extended past OpV with the UEV–OpV slope. */
    public static double rawK(double euPerTick) {
        double x = tierPosition(euPerTick);
        for (int i = 1; i < TIER_POINTS.length; i++) {
            double[] low = TIER_POINTS[i - 1];
            double[] high = TIER_POINTS[i];
            if (x <= high[0]) return low[1] + (x - low[0]) * (high[1] - low[1]) / (high[0] - low[0]);
        }
        double[] beforeLast = TIER_POINTS[TIER_POINTS.length - 2];
        double[] last = TIER_POINTS[TIER_POINTS.length - 1];
        return last[1] + (x - last[0]) * (last[1] - beforeLast[1]) / (last[0] - beforeLast[0]);
    }

    /** Type whose cap applies after {@code megastructures} were built (three per type). */
    public static int capType(int megastructures) {
        return clampMegas(megastructures) / MEGAS_PER_TYPE;
    }

    /** Cap in hundredths: 79, 89, 99, then 179 after the third megastructure, and so on. */
    public static int capHundredths(int megastructures) {
        int megas = clampMegas(megastructures);
        return megas / MEGAS_PER_TYPE * 100 + CAP_HUNDREDTHS[megas % MEGAS_PER_TYPE];
    }

    /**
     * K in hundredths, as shown: the raw K rounded down, limited by the cap, and never below the type already
     * reached (GTIA-D-372: only the decimal drops).
     */
    public static int kHundredths(double euPerTick, int megastructures, int reachedType) {
        int capped = Math.min(rawHundredths(euPerTick), capHundredths(megastructures));
        return Math.max(capped, Math.max(0, reachedType) * 100);
    }

    /** Integer type a power has reached under the current cap; the store keeps the highest one. */
    public static int reachedType(double euPerTick, int megastructures) {
        return Math.min(rawHundredths(euPerTick), capHundredths(megastructures)) / 100;
    }

    /** Whether the shown K is held down by the cap. */
    public static boolean capped(double euPerTick, int megastructures) {
        return rawHundredths(euPerTick) > capHundredths(megastructures);
    }

    public static int rawHundredths(double euPerTick) {
        return (int) Math.min(Integer.MAX_VALUE, Math.floor(rawK(euPerTick) * 100 + 1e-9));
    }

    /** EU/t equivalent of a steam flow in mB/t. */
    public static double steamToEu(double steamMb) {
        return steamMb / STEAM_MB_PER_EU;
    }

    private static int clampMegas(int megastructures) {
        return Math.max(0, Math.min(MAX_MEGASTRUCTURES, megastructures));
    }
}
