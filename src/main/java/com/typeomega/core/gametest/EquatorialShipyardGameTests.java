package com.typeomega.core.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.mega.TypeOmegaMegastructures;

/**
 * Equatorial Shipyard (D-446 A6, D-448): smaller twin-ring sibling of the Assembly Yard (inner ring harvests, outer
 * ring assembles); formation, recipe run, and negative missing-mount/scaffold tests. Slots 47, 48, 49.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class EquatorialShipyardGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";

    private EquatorialShipyardGameTests() {}

    private static boolean skip(GameTestHelper helper) {
        if (TypeOmegaMegastructures.EQUATORIAL_SHIPYARD_DEF != null) return false;
        TypeOmegaMod.LOGGER
                .warn("Equatorial Shipyard not registered (no GTNA or no shape file): its GameTests are skipped");
        helper.succeed();
        return true;
    }

    @GameTest(template = TEMPLATE, batch = "shipyard_math", timeoutTicks = 100)
    public static void shipyardMath(GameTestHelper helper) {
        if (!skip(helper)) EquatorialShipyardTestRig.shipyardMath(helper);
    }

    @GameTest(template = TEMPLATE, batch = "shipyard_runs", timeoutTicks = 2400)
    public static void formsAndAssembles(GameTestHelper helper) {
        if (!skip(helper)) EquatorialShipyardTestRig.formsAndAssembles(helper);
    }

    @GameTest(template = TEMPLATE, batch = "shipyard_wrong", timeoutTicks = 2400)
    public static void missingScaffoldNoForm(GameTestHelper helper) {
        if (!skip(helper)) EquatorialShipyardTestRig.missingScaffoldNoForm(helper);
    }

    @GameTest(template = TEMPLATE, batch = "shipyard_wrong", timeoutTicks = 2400)
    public static void mountFormsAndRejectsCut(GameTestHelper helper) {
        if (!skip(helper)) EquatorialShipyardTestRig.mountFormsAndRejectsCut(helper);
    }
}
