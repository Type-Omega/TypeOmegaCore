package com.typeomega.core.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.mega.TypeOmegaMegastructures;

/**
 * The Birch World (D-331, D-446 A5): concentric habitat layers, population, district module capacity, credit, interest
 * and era levels; black-hole host check, hosted work booked as causal debt, paid back, and a paradox past the credit;
 * without the inner layer it does not form. Slots 30, 31.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class BirchWorldGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";

    private BirchWorldGameTests() {}

    private static boolean skip(GameTestHelper helper) {
        if (TypeOmegaMegastructures.BIRCH_WORLD_DEF != null) return false;
        TypeOmegaMod.LOGGER.warn("Birch World not registered (no GTNA or no shape file): its GameTests are skipped");
        helper.succeed();
        return true;
    }

    @GameTest(template = TEMPLATE, batch = "birch_math", timeoutTicks = 100)
    public static void causalMath(GameTestHelper helper) {
        if (!skip(helper)) BirchWorldTestRig.causalMath(helper);
    }

    @GameTest(template = TEMPLATE, batch = "birch_runs", timeoutTicks = 2400)
    public static void debtAndParadox(GameTestHelper helper) {
        if (!skip(helper)) BirchWorldTestRig.debtAndParadox(helper);
    }

    @GameTest(template = TEMPLATE, batch = "birch_wrong", timeoutTicks = 2400)
    public static void noLayerNoWorld(GameTestHelper helper) {
        if (!skip(helper)) BirchWorldTestRig.noLayerNoWorld(helper);
    }
}
