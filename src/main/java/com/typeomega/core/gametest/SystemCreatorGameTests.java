package com.typeomega.core.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.mega.TypeOmegaMegastructures;

/**
 * The Interstellar System Creator (D-319): the formulas; a red dwarf made from start to end (hydrogen from the
 * hatches, collapse, ignition, a new system and the Dimensional era); without Ψ it does not start; without a talon
 * it does not form. Needs GTNA and the shape files. Slots 10 and 11.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class SystemCreatorGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";

    private SystemCreatorGameTests() {}

    private static boolean skip(GameTestHelper helper) {
        if (TypeOmegaMegastructures.SYSTEM_CREATOR_DEF != null) return false;
        TypeOmegaMod.LOGGER.warn("System Creator not registered (no GTNA or no shape file): its GameTests are skipped");
        helper.succeed();
        return true;
    }

    @GameTest(template = TEMPLATE, batch = "system_creator_math", timeoutTicks = 100)
    public static void creatorMath(GameTestHelper helper) {
        if (!skip(helper)) SystemCreatorTestRig.creatorMath(helper);
    }

    @GameTest(template = TEMPLATE, batch = "system_creator_runs", timeoutTicks = 2400)
    public static void forgesARedDwarf(GameTestHelper helper) {
        if (!skip(helper)) SystemCreatorTestRig.forgesARedDwarf(helper);
    }

    @GameTest(template = TEMPLATE, batch = "system_creator_wrong", timeoutTicks = 2400)
    public static void noTalonNoCreator(GameTestHelper helper) {
        if (!skip(helper)) SystemCreatorTestRig.noTalonNoCreator(helper);
    }
}
