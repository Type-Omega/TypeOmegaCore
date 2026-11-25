package com.typeomega.core.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.mega.TypeOmegaMegastructures;

/**
 * The Black Hole Suprashell (D-329): time dilation, room and lowering; it hosts multiblock controllers and runs
 * their recipes in parallel, only round a black hole; without the equatorial plates it does not form. Slots 26, 27.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class SuprashellGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";

    private SuprashellGameTests() {}

    private static boolean skip(GameTestHelper helper) {
        if (TypeOmegaMegastructures.SUPRASHELL_DEF != null) return false;
        TypeOmegaMod.LOGGER.warn("Suprashell not registered (no GTNA or no shape file): its GameTests are skipped");
        helper.succeed();
        return true;
    }

    @GameTest(template = TEMPLATE, batch = "suprashell_math", timeoutTicks = 100)
    public static void shellMath(GameTestHelper helper) {
        if (!skip(helper)) SuprashellTestRig.shellMath(helper);
    }

    @GameTest(template = TEMPLATE, batch = "suprashell_runs", timeoutTicks = 2400)
    public static void hostsModules(GameTestHelper helper) {
        if (!skip(helper)) SuprashellTestRig.hostsModules(helper);
    }

    @GameTest(template = TEMPLATE, batch = "suprashell_wrong", timeoutTicks = 2400)
    public static void noPlatesNoShell(GameTestHelper helper) {
        if (!skip(helper)) SuprashellTestRig.noPlatesNoShell(helper);
    }
}
