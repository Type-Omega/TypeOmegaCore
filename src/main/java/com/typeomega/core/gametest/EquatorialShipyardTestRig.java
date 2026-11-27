package com.typeomega.core.gametest;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.data.GTMachines;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

import com.typeomega.core.mega.TypeOmegaMegastructures;
import com.typeomega.core.mega.common.MegaMachine;
import com.typeomega.core.mega.common.MegaModelData;
import com.typeomega.core.mega.common.MegaShape;
import com.typeomega.core.mega.shipyard.EquatorialShipyardMachine;
import com.typeomega.core.mega.shipyard.EquatorialShipyardPhysics;

import java.util.UUID;

/** Bodies of {@link EquatorialShipyardGameTests}, apart so GTNA classes load only when registered. */
final class EquatorialShipyardTestRig {

    private EquatorialShipyardTestRig() {}

    static void shipyardMath(GameTestHelper helper) {
        helper.assertTrue(EquatorialShipyardPhysics.slipways(1) == 0, "phase 1 has 0 slipways");
        helper.assertTrue(EquatorialShipyardPhysics.slipways(2) == 6, "phase 2 inner ring opens 6 slipways");
        helper.assertTrue(EquatorialShipyardPhysics.slipways(3) == 24, "phase 3 outer ring opens 24 slipways");
        helper.assertTrue(EquatorialShipyardPhysics.parallel(24) == 192, "24 slipways give 192 parallel");
        helper.assertTrue(Math.abs(EquatorialShipyardPhysics.speed(1.0, false) - 1.0) < 1e-6, "solar luminosity x1.0");
        helper.assertTrue(Math.abs(EquatorialShipyardPhysics.euFactor(2, false) - 0.8) < 1e-6,
                "inner harvesting ring cuts EU by 20%");

        var model = MegaModelData.get(EquatorialShipyardMachine.SHAPE);
        helper.assertTrue(model != null && model.parts.size() == 7,
                "Equatorial Shipyard 3D overlay model loads (D-448)");
        helper.assertTrue(model.parts.stream().anyMatch(MegaModelData.Part::isEmissive),
                "Equatorial Shipyard overlay has emissive ring glow parts");
        helper.succeed();
    }

    static void formsAndAssembles(GameTestHelper helper) {
        try {
            MegaMachine.anyDimensionForTests = true;
            EquatorialShipyardMachine m = MegaTestRig.build(helper, EquatorialShipyardMachine.SHAPE,
                    TypeOmegaMegastructures.EQUATORIAL_SHIPYARD_DEF, 3, 47);
            MegaTestRig.hatch(helper, m, 0, GTMachines.ITEM_IMPORT_BUS[GTValues.LuV]);
            MegaTestRig.hatch(helper, m, 1, GTMachines.ITEM_EXPORT_BUS[GTValues.LuV]);
            helper.assertTrue(m.checkPatternWithLock(),
                    "Equatorial Shipyard forms: " + MegaTestRig.error(m));
            m.onStructureFormed();

            UUID owner = UUID.randomUUID();
            StellarEngineTestRig.power(helper, owner);
            m.setActive(24);
            m.testSecond(owner);
            helper.assertTrue(m.phases() >= 3, "all 3 phases formed (phases=" + m.phases() + ")");
            helper.assertTrue((int) m.view()[2] == 24 && (int) m.view()[0] == 192,
                    "24 slipways open give 192 parallel");
            MegaTestRig.lit(helper, m, "harvest_0");
            MegaTestRig.capturesAndReleases(helper, m);
            helper.succeed();
        } finally {
            MegaMachine.anyDimensionForTests = false;
        }
    }

    static void missingScaffoldNoForm(GameTestHelper helper) {
        EquatorialShipyardMachine m = MegaTestRig.build(helper, EquatorialShipyardMachine.SHAPE,
                TypeOmegaMegastructures.EQUATORIAL_SHIPYARD_DEF, 3, 48);
        var shape = MegaShape.get(EquatorialShipyardMachine.SHAPE);
        int[] c = MegaTestRig.firstCell(shape, MegaTestRig.part(shape, "inner_scaffold", "rib"));
        helper.getLevel().setBlock(shape.world(m.getPos(), MegaTestRig.FRONT, c[0], c[1], c[2]),
                Blocks.AIR.defaultBlockState(), 2 | 16);
        helper.assertTrue(!m.checkPatternWithLock(),
                "missing inner scaffold block prevents Equatorial Shipyard formation");
        helper.succeed();
    }

    static void mountFormsAndRejectsCut(GameTestHelper helper) {
        MegaMountTestRig.needsTheMount(helper, EquatorialShipyardMachine.SHAPE,
                TypeOmegaMegastructures.EQUATORIAL_SHIPYARD_DEF, 49);
    }
}
