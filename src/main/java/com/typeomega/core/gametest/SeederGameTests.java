package com.typeomega.core.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.mega.TypeOmegaMegastructures;

/**
 * The Planetary Seeder Nexus (D-446 A6, D-448): orbital bio-seeder kilostructure and companion to Yggdrasil Orchid;
 * runs Greenhouse recipes in parallel with Biomass output; without its core it does not form. Slots 47, 48, 49.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class SeederGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";

    private SeederGameTests() {}

    private static boolean skip(GameTestHelper helper) {
        if (TypeOmegaMegastructures.PLANETARY_SEEDER_NEXUS_DEF != null) return false;
        TypeOmegaMod.LOGGER.warn("Planetary Seeder Nexus not registered (no GTNA or no shape file): its GameTests are skipped");
        helper.succeed();
        return true;
    }

    @GameTest(template = TEMPLATE, batch = "seeder_math", timeoutTicks = 100)
    public static void seederMath(GameTestHelper helper) {
        if (!skip(helper)) SeederTestRig.seederMath(helper);
    }

    @GameTest(template = TEMPLATE, batch = "seeder_runs", timeoutTicks = 2400)
    public static void formsAndSeedsGreenhouse(GameTestHelper helper) {
        if (!skip(helper)) SeederTestRig.formsAndSeedsGreenhouse(helper);
    }

    @GameTest(template = TEMPLATE, batch = "seeder_wrong", timeoutTicks = 2400)
    public static void missingCoreNoForm(GameTestHelper helper) {
        if (!skip(helper)) SeederTestRig.missingCoreNoForm(helper);
    }

    @GameTest(template = TEMPLATE, batch = "seeder_wrong", timeoutTicks = 2400)
    public static void rejectsNonSystemDimension(GameTestHelper helper) {
        if (!skip(helper)) SeederTestRig.rejectsNonSystemDimension(helper);
    }
}
