package com.typeomega.core.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.mega.TypeOmegaMegastructures;

/**
 * The HRAE-MC (D-322): Bardeen's orbits and efficiency, the Penrose share; around a black hole feeding gives EU
 * and spin, Penrose takes spin; around a shining star it does nothing; without a hoop it does not form. Slots 16, 17.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class HraeGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";

    private HraeGameTests() {}

    private static boolean skip(GameTestHelper helper) {
        if (TypeOmegaMegastructures.HRAEMC_DEF != null) return false;
        TypeOmegaMod.LOGGER.warn("HRAE-MC not registered (no GTNA or no shape file): its GameTests are skipped");
        helper.succeed();
        return true;
    }

    @GameTest(template = TEMPLATE, batch = "hraemc_math", timeoutTicks = 100)
    public static void kerrMath(GameTestHelper helper) {
        if (!skip(helper)) HraeTestRig.kerrMath(helper);
    }

    @GameTest(template = TEMPLATE, batch = "hraemc_runs", timeoutTicks = 2400)
    public static void powerFromAHole(GameTestHelper helper) {
        if (!skip(helper)) HraeTestRig.powerFromAHole(helper);
    }

    @GameTest(template = TEMPLATE, batch = "hraemc_wrong", timeoutTicks = 2400)
    public static void noHoopNoPlant(GameTestHelper helper) {
        if (!skip(helper)) HraeTestRig.noHoopNoPlant(helper);
    }
}
