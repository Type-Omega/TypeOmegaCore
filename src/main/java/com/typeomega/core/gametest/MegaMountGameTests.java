package com.typeomega.core.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.mega.TypeOmegaMegastructures;

/**
 * The controller mount (D-445) of the megastructures that moved the controller into the structure: Yggdrasil Orchid,
 * Nidavellir Hyperforge and Star Lifter. The shape tests need no machine; the formation tests build phase 1 and need
 * GTNA. Slots 34 to 39.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class MegaMountGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";

    private MegaMountGameTests() {}

    private static boolean skip(GameTestHelper helper, Object definition, String name) {
        if (definition != null) return false;
        TypeOmegaMod.LOGGER.warn("{} not registered (no GTNA or no shape file): its mount GameTest is skipped", name);
        helper.succeed();
        return true;
    }

    // ------------------------------------------------------------------ the shape: no machine needed

    /** The controller cell, its 3x3 and the wall behind are static; there is no station and no bridge. */
    @GameTest(template = TEMPLATE, batch = "mega_mount_shape", timeoutTicks = 100)
    public static void controllerSitsOnAStaticMount(GameTestHelper helper) {
        for (String name : MegaMountShapeRig.PILOT) MegaMountShapeRig.controllerOnStaticMount(helper, name);
        helper.succeed();
    }

    /** Each pilot shape is built of its own casings, registered, with the controller in the first of them. */
    @GameTest(template = TEMPLATE, batch = "mega_mount_shape", timeoutTicks = 100)
    public static void pilotShapesUseTheirOwnCasings(GameTestHelper helper) {
        for (String name : MegaMountShapeRig.PILOT) MegaMountShapeRig.usesItsOwnCasings(helper, name);
        helper.succeed();
    }

    /** No moving piece comes within the margin of the mount while its track turns. */
    @GameTest(template = TEMPLATE, batch = "mega_mount_shape", timeoutTicks = 200)
    public static void noMovingPieceReachesTheMount(GameTestHelper helper) {
        for (String name : MegaMountShapeRig.PILOT) MegaMountShapeRig.sweepIsClear(helper, name);
        helper.succeed();
    }

    // ------------------------------------------------------------------ the machine

    @GameTest(template = TEMPLATE, batch = "yggdrasil_mount", timeoutTicks = 1200)
    public static void yggdrasilFormsOnTheMount(GameTestHelper helper) {
        if (!skip(helper, TypeOmegaMegastructures.YGGDRASIL_DEF, "Yggdrasil"))
            MegaMountTestRig.forms(helper, TypeOmegaMegastructures.YGGDRASIL, TypeOmegaMegastructures.YGGDRASIL_DEF,
                    38);
    }

    @GameTest(template = TEMPLATE, batch = "yggdrasil_mount", timeoutTicks = 1200)
    public static void yggdrasilNeedsTheMount(GameTestHelper helper) {
        if (!skip(helper, TypeOmegaMegastructures.YGGDRASIL_DEF, "Yggdrasil"))
            MegaMountTestRig.needsTheMount(helper, TypeOmegaMegastructures.YGGDRASIL,
                    TypeOmegaMegastructures.YGGDRASIL_DEF, 39);
    }

    @GameTest(template = TEMPLATE, batch = "nidavellir_mount", timeoutTicks = 1200)
    public static void nidavellirFormsOnTheMount(GameTestHelper helper) {
        if (!skip(helper, TypeOmegaMegastructures.NIDAVELLIR_DEF, "Nidavellir"))
            MegaMountTestRig.forms(helper, TypeOmegaMegastructures.NIDAVELLIR,
                    TypeOmegaMegastructures.NIDAVELLIR_DEF, 36);
    }

    @GameTest(template = TEMPLATE, batch = "nidavellir_mount", timeoutTicks = 1200)
    public static void nidavellirNeedsTheMount(GameTestHelper helper) {
        if (!skip(helper, TypeOmegaMegastructures.NIDAVELLIR_DEF, "Nidavellir"))
            MegaMountTestRig.needsTheMount(helper, TypeOmegaMegastructures.NIDAVELLIR,
                    TypeOmegaMegastructures.NIDAVELLIR_DEF, 37);
    }

    @GameTest(template = TEMPLATE, batch = "star_lifter_mount", timeoutTicks = 1200)
    public static void starLifterFormsOnTheMount(GameTestHelper helper) {
        if (!skip(helper, TypeOmegaMegastructures.STAR_LIFTER_DEF, "Star Lifter"))
            MegaMountTestRig.forms(helper, TypeOmegaMegastructures.STAR_LIFTER,
                    TypeOmegaMegastructures.STAR_LIFTER_DEF, 34);
    }

    @GameTest(template = TEMPLATE, batch = "star_lifter_mount", timeoutTicks = 1200)
    public static void starLifterNeedsTheMount(GameTestHelper helper) {
        if (!skip(helper, TypeOmegaMegastructures.STAR_LIFTER_DEF, "Star Lifter"))
            MegaMountTestRig.needsTheMount(helper, TypeOmegaMegastructures.STAR_LIFTER,
                    TypeOmegaMegastructures.STAR_LIFTER_DEF, 35);
    }
}
