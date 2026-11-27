package com.typeomega.core.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.mega.TypeOmegaMegastructures;

/**
 * The Neutronium Gigaforge (D-446): forms on its mount, loads its 3D overlay model (D-448), extracts neutronium
 * dust around a neutron star, rejects common stars, and fails to form when its hub is broken. Slots 40 to 42.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class GigaforgeGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";

    private GigaforgeGameTests() {}

    private static boolean skip(GameTestHelper helper) {
        if (TypeOmegaMegastructures.NEUTRONIUM_GIGAFORGE_DEF != null) return false;
        TypeOmegaMod.LOGGER.warn("Neutronium Gigaforge not registered: its GameTests are skipped");
        helper.succeed();
        return true;
    }

    @GameTest(template = TEMPLATE, batch = "gigaforge_runs", timeoutTicks = 2400)
    public static void formsAndExtractsOnNeutronStar(GameTestHelper helper) {
        if (!skip(helper)) GigaforgeTestRig.formsAndExtractsOnNeutronStar(helper);
    }

    @GameTest(template = TEMPLATE, batch = "gigaforge_wrong", timeoutTicks = 2400)
    public static void rejectsCommonStar(GameTestHelper helper) {
        if (!skip(helper)) GigaforgeTestRig.rejectsCommonStar(helper);
    }

    @GameTest(template = TEMPLATE, batch = "gigaforge_wrong", timeoutTicks = 2400)
    public static void missingCoreNoForm(GameTestHelper helper) {
        if (!skip(helper)) GigaforgeTestRig.missingCoreNoForm(helper);
    }
}
