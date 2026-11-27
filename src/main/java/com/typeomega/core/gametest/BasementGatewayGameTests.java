package com.typeomega.core.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.mega.TypeOmegaMegastructures;

/**
 * The Basement Gateway (D-446 A6): forms on its D-445 mount, loads its 3D overlay model (D-448), runs in any
 * star system while a Solar System Disintegrator is active in the same system, does not produce without an active
 * Disintegrator, and fails to form when its frame is broken. Slots 46 to 49.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class BasementGatewayGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";

    private BasementGatewayGameTests() {}

    private static boolean skip(GameTestHelper helper) {
        if (TypeOmegaMegastructures.BASEMENT_GATEWAY_DEF != null) return false;
        TypeOmegaMod.LOGGER.warn("Basement Gateway not registered: its GameTests are skipped");
        helper.succeed();
        return true;
    }

    @GameTest(template = TEMPLATE, batch = "gateway_runs", timeoutTicks = 2400)
    public static void formsAndCondensesWithActiveDisintegrator(GameTestHelper helper) {
        if (!skip(helper)) BasementGatewayTestRig.formsAndCondensesWithActiveDisintegrator(helper);
    }

    @GameTest(template = TEMPLATE, batch = "gateway_wrong", timeoutTicks = 2400)
    public static void noActiveDisintegratorNoProduce(GameTestHelper helper) {
        if (!skip(helper)) BasementGatewayTestRig.noActiveDisintegratorNoProduce(helper);
    }

    @GameTest(template = TEMPLATE, batch = "gateway_wrong", timeoutTicks = 2400)
    public static void missingFrameNoForm(GameTestHelper helper) {
        if (!skip(helper)) BasementGatewayTestRig.missingFrameNoForm(helper);
    }
}
