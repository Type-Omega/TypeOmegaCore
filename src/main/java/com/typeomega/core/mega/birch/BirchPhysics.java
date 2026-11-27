package com.typeomega.core.mega.birch;

/**
 * Physics of the Birch World (D-331, D-446 A5), the Causal era (D-312): a concentric shell habitat round a black hole
 * whose layers house inhabitants and district slots for modules, while the hole's rotating interior allows closed
 * timelike curves.
 *
 * <ul>
 * <li>Each shell layer holds {@link #POPULATION_PER_LAYER} inhabitants and {@link #MODULES_PER_LAYER} district
 * module slots (distinct from the Suprashell's time dilation).</li>
 * <li>Work sent round a time loop is done before it is paid for: recipes finish 64 times as fast and their EU becomes
 * causal debt instead of being drawn.</li>
 * <li>The debt grows by 0.05 % a second (the loop's tension) until it is paid back from the Nexus network.</li>
 * <li>Novikov's self-consistency: a debt past the credit the shells can hold is a paradox. History corrects itself:
 * the debt is gone, and so is everything in the output buses, and the loops close for a minute.</li>
 * </ul>
 */
public final class BirchPhysics {

    public static final int LAYERS_PER_PHASE = 40, MODULES_PER_LAYER = 2, PARALLEL_PER_MODULE = 256, REPAY_MAX = 16,
            PARADOX_SECONDS = 60;
    // EN: Each concentric habitat layer houses 1 trillion inhabitants alongside its industrial district slots.
    // PT: Cada camada concêntrica do habitat abriga 1 trilhão de habitantes junto aos seus espaços de distrito.
    // ZH: 每个同心栖息地壳层容纳 1 万亿居民及其工业分区模块槽位。
    public static final double POPULATION_PER_LAYER = 1e12;
    public static final double SPEED = 64, INTEREST = 0.0005, CREDIT_PER_LAYER_MASS = Math.pow(2, 44),
            REPAY_STEP = Math.pow(2, 40);

    /** Debt paid back, as powers of two of EU, for the Causal era levels I, II and III. */
    public static final int[] ERA_EXPONENTS = { 50, 55, 60 };

    private BirchPhysics() {}

    public static int layers(int phases) {
        return LAYERS_PER_PHASE * Math.max(1, Math.min(5, phases));
    }

    public static int capacity(int phases) {
        return MODULES_PER_LAYER * layers(phases);
    }

    /** Total habitat population housed across the concentric shell layers. */
    public static double population(int phases) {
        return POPULATION_PER_LAYER * layers(phases);
    }

    public static int parallel(int modules) {
        return PARALLEL_PER_MODULE * Math.max(0, modules);
    }

    /** The debt the shells can hold: more layers and a heavier hole hold more. */
    public static double credit(int phases, double holeMass) {
        return CREDIT_PER_LAYER_MASS * layers(phases) * Math.max(1, holeMass);
    }

    public static double grow(double debt) {
        return debt * (1 + INTEREST);
    }

    /** EU paid back a second at repayment level {@code level}. */
    public static double repayment(int level) {
        return REPAY_STEP * Math.max(0, Math.min(REPAY_MAX, level));
    }

    /** Level of the Causal era by debt paid back: I, II, III at 2^50, 2^55, 2^60 EU. */
    public static int eraLevel(double paid) {
        for (int level = ERA_EXPONENTS.length; level > 0; level--) {
            if (paid >= Math.pow(2, ERA_EXPONENTS[level - 1])) return level;
        }
        return 0;
    }
}
