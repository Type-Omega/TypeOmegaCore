package com.typeomega.core.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.mega.TypeOmegaMegastructures;

/**
 * The Nidavellir Hyperforge (D-323): parallels and neutronium by compression; it forges an alloy in parallel,
 * spending neutronium per batch, four times as fast round a neutron star; without neutronium it waits; without the
 * outer ring it does not form. Slots 18, 19.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class NidavellirGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";

    private NidavellirGameTests() {}

    private static boolean skip(GameTestHelper helper) {
        if (TypeOmegaMegastructures.NIDAVELLIR_DEF != null) return false;
        TypeOmegaMod.LOGGER.warn("Nidavellir not registered (no GTNA or no shape file): its GameTests are skipped");
        helper.succeed();
        return true;
    }

    @GameTest(template = TEMPLATE, batch = "nidavellir_math", timeoutTicks = 100)
    public static void pressMath(GameTestHelper helper) {
        if (!skip(helper)) NidavellirTestRig.pressMath(helper);
    }

    @GameTest(template = TEMPLATE, batch = "nidavellir_runs", timeoutTicks = 2400)
    public static void forgesUnderNeutronium(GameTestHelper helper) {
        if (!skip(helper)) NidavellirTestRig.forgesUnderNeutronium(helper);
    }

    @GameTest(template = TEMPLATE, batch = "nidavellir_wrong", timeoutTicks = 2400)
    public static void noOuterRingNoForge(GameTestHelper helper) {
        if (!skip(helper)) NidavellirTestRig.noOuterRingNoForge(helper);
    }

    @GameTest(template = TEMPLATE, batch = "nidavellir_wrong", timeoutTicks = 2400)
    public static void rejectsCommonStar(GameTestHelper helper) {
        if (!skip(helper)) NidavellirTestRig.rejectsCommonStar(helper);
    }
}
