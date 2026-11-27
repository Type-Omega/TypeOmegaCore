package com.typeomega.core.gametest;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

import com.typeomega.core.mega.TypeOmegaMegastructures;
import com.typeomega.core.mega.common.MegaMachine;
import com.typeomega.core.mega.common.MegaModelData;
import com.typeomega.core.mega.common.MegaShape;
import com.typeomega.core.mega.gateway.BasementGatewayMachine;
import com.typeomega.core.mega.system.StarPhysics.Kind;
import com.typeomega.core.mega.system.StarSystems;

import java.util.UUID;

/** Bodies of {@link BasementGatewayGameTests}, apart so GTNA classes load only when registered. */
final class BasementGatewayTestRig {

    private BasementGatewayTestRig() {}

    static void formsAndCondensesOnNeutronStar(GameTestHelper helper) {
        var model = MegaModelData.get(BasementGatewayMachine.SHAPE);
        helper.assertTrue(model != null && model.parts.size() == 5,
                "Basement Gateway 3D overlay model loads (D-448)");

        var star = StarSystems.get(helper.getLevel().getServer()).star(StarSystems.NEXUS);
        var kind = star.kind;
        try {
            MegaMachine.anyDimensionForTests = true;
            BasementGatewayMachine m = MegaTestRig.build(helper, BasementGatewayMachine.SHAPE,
                    TypeOmegaMegastructures.BASEMENT_GATEWAY_DEF, 5, 46);
            var outBus = (ItemBusPartMachine) MegaTestRig.hatch(helper, m, 0,
                    GTMachines.ITEM_EXPORT_BUS[GTValues.LuV]);
            var outHatch = (FluidHatchPartMachine) MegaTestRig.hatch(helper, m, 1,
                    GTMachines.FLUID_EXPORT_HATCH[GTValues.LuV]);
            helper.assertTrue(m.checkPatternWithLock(), "Basement Gateway forms: " + MegaTestRig.error(m));
            m.onStructureFormed();

            UUID owner = UUID.randomUUID();
            StellarEngineTestRig.power(helper, owner);
            star.kind = Kind.NEUTRON_STAR;
            helper.assertTrue(m.inPlace(), "Basement Gateway accepts neutron star");

            m.setAperturePct(100);
            m.testSecond(owner);
            helper.assertTrue(m.totalDust() == 30 && m.totalGasMb() == 25000 && m.isRunning(),
                    "Basement Gateway condenses 30 dust and 25000 mB gas at phase 5, 100% aperture");
            helper.assertTrue(BasementGatewayMachine.hasActiveGateway(helper.getLevel()),
                    "Basement Gateway registers active wormhole in dimension");
            MegaTestRig.lit(helper, m, "lens_beam", "wormhole");

            int inBus = 0;
            for (int s = 0; s < outBus.getInventory().getSlots(); s++) {
                inBus += outBus.getInventory().getStackInSlot(s).getCount();
            }
            helper.assertTrue(inBus == 30, "output bus holds 30 r-process dusts, got " + inBus);

            int hMb = 0;
            for (int t = 0; t < outHatch.tank.getTanks(); t++) {
                var fs = outHatch.tank.getFluidInTank(t);
                if (fs.getFluid() == GTMaterials.Hydrogen.getFluid()) hMb += fs.getAmount();
            }
            helper.assertTrue(hMb == 20000, "output hatch holds 20000 mB Hydrogen, got " + hMb);
            helper.succeed();
        } finally {
            MegaMachine.anyDimensionForTests = false;
            star.kind = kind;
        }
    }

    static void rejectsCommonStar(GameTestHelper helper) {
        var star = StarSystems.get(helper.getLevel().getServer()).star(StarSystems.NEXUS);
        var kind = star.kind;
        try {
            MegaMachine.anyDimensionForTests = true;
            BasementGatewayMachine m = MegaTestRig.build(helper, BasementGatewayMachine.SHAPE,
                    TypeOmegaMegastructures.BASEMENT_GATEWAY_DEF, 5, 47);
            MegaTestRig.hatch(helper, m, 0, GTMachines.ITEM_EXPORT_BUS[GTValues.LuV]);
            MegaTestRig.hatch(helper, m, 1, GTMachines.FLUID_EXPORT_HATCH[GTValues.LuV]);
            helper.assertTrue(m.checkPatternWithLock(), "Basement Gateway forms: " + MegaTestRig.error(m));
            m.onStructureFormed();

            UUID owner = UUID.randomUUID();
            StellarEngineTestRig.power(helper, owner);
            star.kind = Kind.YELLOW_DWARF;
            helper.assertTrue(!m.inPlace(), "Basement Gateway rejects common star in inPlace()");
            m.testSecond(owner);
            helper.assertTrue(m.totalDust() == 0 && !m.isRunning(),
                    "Basement Gateway does not run on common star");
            helper.succeed();
        } finally {
            MegaMachine.anyDimensionForTests = false;
            star.kind = kind;
        }
    }

    static void missingFrameNoForm(GameTestHelper helper) {
        BasementGatewayMachine m = MegaTestRig.build(helper, BasementGatewayMachine.SHAPE,
                TypeOmegaMegastructures.BASEMENT_GATEWAY_DEF, 5, 48);
        var shape = MegaShape.get(BasementGatewayMachine.SHAPE);
        int[] c = MegaTestRig.firstCell(shape, MegaTestRig.part(shape, "frame", "body"));
        helper.getLevel().setBlock(shape.world(m.getPos(), MegaTestRig.FRONT, c[0], c[1], c[2]),
                Blocks.AIR.defaultBlockState(), 2 | 16);
        helper.assertTrue(!m.checkPatternWithLock(), "missing frame block prevents Basement Gateway formation");
        helper.succeed();
    }
}
