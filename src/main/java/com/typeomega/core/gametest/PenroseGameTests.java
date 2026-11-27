package com.typeomega.core.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.mega.TypeOmegaMegastructures;

/**
 * The Penrose Sphere (D-446 A3, D-448): superradiant spin extraction around a black hole shared with HRAE-MC;
 * rejects non-black-hole stars; without its geodesic frame it does not form. Slots 44, 45, 46.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class PenroseGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";

    private PenroseGameTests() {}

    private static boolean skip(GameTestHelper helper) {
        if (TypeOmegaMegastructures.PENROSE_SPHERE_DEF != null) return false;
        TypeOmegaMod.LOGGER.warn("Penrose Sphere not registered (no GTNA or no shape file): its GameTests are skipped");
        helper.succeed();
        return true;
    }

    @GameTest(template = TEMPLATE, batch = "penrose_math", timeoutTicks = 100)
    public static void penroseMath(GameTestHelper helper) {
        if (!skip(helper)) PenroseTestRig.penroseMath(helper);
    }

    @GameTest(template = TEMPLATE, batch = "penrose_runs", timeoutTicks = 2400)
    public static void formsAndExtractsOnBlackHole(GameTestHelper helper) {
        if (!skip(helper)) PenroseTestRig.formsAndExtractsOnBlackHole(helper);
    }

    @GameTest(template = TEMPLATE, batch = "penrose_wrong", timeoutTicks = 2400)
    public static void rejectsCommonStar(GameTestHelper helper) {
        if (!skip(helper)) PenroseTestRig.rejectsCommonStar(helper);
    }

    @GameTest(template = TEMPLATE, batch = "penrose_wrong", timeoutTicks = 2400)
    public static void missingFrameNoForm(GameTestHelper helper) {
        if (!skip(helper)) PenroseTestRig.missingFrameNoForm(helper);
    }
}
