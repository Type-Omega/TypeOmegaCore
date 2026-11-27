package com.typeomega.core.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

import com.typeomega.core.mega.TypeOmegaMegastructures;
import com.typeomega.core.mega.common.MegaMachine;
import com.typeomega.core.mega.common.MegaModelData;
import com.typeomega.core.mega.common.MegaShape;
import com.typeomega.core.mega.penrose.PenroseMachine;
import com.typeomega.core.mega.penrose.PenrosePhysics;
import com.typeomega.core.mega.system.StarPhysics.Kind;
import com.typeomega.core.mega.system.StarSystems;

import java.util.UUID;

/** Bodies of {@link PenroseGameTests}, apart so GTNA classes load only when registered. */
final class PenroseTestRig {

    private PenroseTestRig() {}

    static void penroseMath(GameTestHelper helper) {
        helper.assertTrue(Math.abs(PenrosePhysics.rotationalShare(1.0) - 0.2929) < 0.001,
                "29% rotational mass at full spin");
        helper.assertTrue(PenrosePhysics.penroseEut(100, 0.5, 10, 2) == 0,
                "requires phase 3 Penrose rings");
        double eut = PenrosePhysics.penroseEut(100, 0.5, 10, 3);
        helper.assertTrue(eut > 0 && PenrosePhysics.spinDrain(eut, 0.5, 10) > 0,
                "Penrose extraction drains black hole spin");

        var model = MegaModelData.get(PenroseMachine.SHAPE);
        helper.assertTrue(model != null && model.parts.size() == 5, "Penrose 3D overlay model loads (D-448)");
        helper.assertTrue(model.parts.stream().anyMatch(MegaModelData.Part::isEmissive),
                "Penrose overlay has emissive parts");
        helper.assertTrue(model.parts.stream().anyMatch(MegaModelData.Part::isTranslucent),
                "Penrose overlay has translucent mirror panels");
        helper.assertTrue(model.parts.stream().anyMatch(MegaModelData.Part::isMoving),
                "Penrose overlay has rotating rings");
        helper.succeed();
    }

    static void formsAndExtractsOnBlackHole(GameTestHelper helper) {
        var star = StarSystems.get(helper.getLevel().getServer()).star(StarSystems.NEXUS);
        var kind = star.kind;
        double mass = star.mass, spin = star.spin;
        try {
            MegaMachine.anyDimensionForTests = true;
            PenroseMachine m = MegaTestRig.build(helper, PenroseMachine.SHAPE,
                    TypeOmegaMegastructures.PENROSE_SPHERE_DEF, 3, 44);
            helper.assertTrue(m.checkPatternWithLock(), "Penrose Sphere forms: " + MegaTestRig.error(m));
            m.onStructureFormed();

            UUID owner = UUID.randomUUID();
            StellarEngineTestRig.power(helper, owner);
            star.kind = Kind.BLACK_HOLE;
            star.mass = 10;
            star.spin = 0.6;
            helper.assertTrue(m.inPlace(), "Penrose Sphere accepts black hole");

            m.setPenrose(100);
            double before = star.spin;
            m.testSecond(owner);
            helper.assertTrue(m.view()[2] > 0 && m.isRunning(),
                    "Penrose Sphere extracts EU/t and runs");
            helper.assertTrue(star.spin < before, "Penrose Sphere drains black hole spin");
            helper.assertTrue(PenroseMachine.hasActiveSphere(helper.getLevel()),
                    "Penrose Sphere registers active slice for HRAE-MC");
            MegaTestRig.lit(helper, m, "ergo");
            MegaTestRig.capturesAndReleases(helper, m);
            helper.succeed();
        } finally {
            MegaMachine.anyDimensionForTests = false;
            star.kind = kind;
            star.mass = mass;
            star.spin = spin;
        }
    }

    static void rejectsCommonStar(GameTestHelper helper) {
        var star = StarSystems.get(helper.getLevel().getServer()).star(StarSystems.NEXUS);
        var kind = star.kind;
        try {
            MegaMachine.anyDimensionForTests = true;
            PenroseMachine m = MegaTestRig.build(helper, PenroseMachine.SHAPE,
                    TypeOmegaMegastructures.PENROSE_SPHERE_DEF, 3, 45);
            helper.assertTrue(m.checkPatternWithLock(), "Penrose Sphere forms: " + MegaTestRig.error(m));
            m.onStructureFormed();

            UUID owner = UUID.randomUUID();
            StellarEngineTestRig.power(helper, owner);
            star.kind = Kind.YELLOW_DWARF;
            helper.assertTrue(!m.inPlace(), "Penrose Sphere rejects common star in inPlace()");
            m.testSecond(owner);
            helper.assertTrue(m.view()[2] == 0 && !m.isRunning(), "Penrose Sphere does not run on common star");
            helper.succeed();
        } finally {
            MegaMachine.anyDimensionForTests = false;
            star.kind = kind;
        }
    }

    static void missingFrameNoForm(GameTestHelper helper) {
        PenroseMachine m = MegaTestRig.build(helper, PenroseMachine.SHAPE,
                TypeOmegaMegastructures.PENROSE_SPHERE_DEF, 3, 46);
        var shape = MegaShape.get(PenroseMachine.SHAPE);
        int[] c = MegaTestRig.firstCell(shape, MegaTestRig.part(shape, "frame", "body"));
        helper.getLevel().setBlock(shape.world(m.getPos(), MegaTestRig.FRONT, c[0], c[1], c[2]),
                Blocks.AIR.defaultBlockState(), 2 | 16);
        helper.assertTrue(!m.checkPatternWithLock(), "missing frame block prevents Penrose Sphere formation");
        helper.succeed();
    }
}
