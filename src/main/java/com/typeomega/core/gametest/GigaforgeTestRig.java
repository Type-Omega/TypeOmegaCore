package com.typeomega.core.gametest;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

import com.typeomega.core.mega.TypeOmegaMegastructures;
import com.typeomega.core.mega.common.MegaMachine;
import com.typeomega.core.mega.common.MegaModelData;
import com.typeomega.core.mega.common.MegaShape;
import com.typeomega.core.mega.gigaforge.GigaforgeMachine;
import com.typeomega.core.mega.system.StarPhysics.Kind;
import com.typeomega.core.mega.system.StarSystems;

import java.util.UUID;

/** Bodies of {@link GigaforgeGameTests}, apart so GTNA classes load only when registered. */
final class GigaforgeTestRig {

    private GigaforgeTestRig() {}

    static void formsAndExtractsOnNeutronStar(GameTestHelper helper) {
        var model = MegaModelData.get(GigaforgeMachine.SHAPE);
        helper.assertTrue(model != null && model.parts.size() == 3, "Gigaforge 3D overlay model loads (D-448)");

        var star = StarSystems.get(helper.getLevel().getServer()).star(StarSystems.NEXUS);
        var kind = star.kind;
        try {
            MegaMachine.anyDimensionForTests = true;
            GigaforgeMachine m = MegaTestRig.build(helper, GigaforgeMachine.SHAPE,
                    TypeOmegaMegastructures.NEUTRONIUM_GIGAFORGE_DEF, 3, 40);
            var outBus = (ItemBusPartMachine) MegaTestRig.hatch(helper, m, 0,
                    GTMachines.ITEM_EXPORT_BUS[GTValues.LuV]);
            helper.assertTrue(m.checkPatternWithLock(), "Gigaforge forms: " + MegaTestRig.error(m));
            m.onStructureFormed();

            UUID owner = UUID.randomUUID();
            StellarEngineTestRig.power(helper, owner);
            star.kind = Kind.NEUTRON_STAR;
            helper.assertTrue(m.inPlace(), "Gigaforge accepts neutron star");

            m.testSecond(owner);
            helper.assertTrue(m.extracted() == 64 && m.isRunning(),
                    "Gigaforge extracts 64 dust/s at phase 3, got " + m.extracted());
            var dust = ChemicalHelper.get(TagPrefix.dust, GTMaterials.Neutronium, 1).getItem();
            int inBus = 0;
            for (int s = 0; s < outBus.getInventory().getSlots(); s++) {
                var stack = outBus.getInventory().getStackInSlot(s);
                if (stack.is(dust)) inBus += stack.getCount();
            }
            helper.assertTrue(inBus == 64, "output bus holds 64 neutronium dust, got " + inBus);
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
            GigaforgeMachine m = MegaTestRig.build(helper, GigaforgeMachine.SHAPE,
                    TypeOmegaMegastructures.NEUTRONIUM_GIGAFORGE_DEF, 3, 41);
            MegaTestRig.hatch(helper, m, 0, GTMachines.ITEM_EXPORT_BUS[GTValues.LuV]);
            helper.assertTrue(m.checkPatternWithLock(), "Gigaforge forms: " + MegaTestRig.error(m));
            m.onStructureFormed();

            UUID owner = UUID.randomUUID();
            StellarEngineTestRig.power(helper, owner);
            star.kind = Kind.YELLOW_DWARF;
            helper.assertTrue(!m.inPlace(), "Gigaforge rejects common star in inPlace()");
            m.testSecond(owner);
            helper.assertTrue(m.extracted() == 0 && !m.isRunning(), "Gigaforge does not run on common star");
            helper.succeed();
        } finally {
            MegaMachine.anyDimensionForTests = false;
            star.kind = kind;
        }
    }

    static void missingCoreNoForm(GameTestHelper helper) {
        GigaforgeMachine m = MegaTestRig.build(helper, GigaforgeMachine.SHAPE,
                TypeOmegaMegastructures.NEUTRONIUM_GIGAFORGE_DEF, 3, 42);
        var shape = MegaShape.get(GigaforgeMachine.SHAPE);
        int[] c = MegaTestRig.firstCell(shape, MegaTestRig.part(shape, "core", "body"));
        helper.getLevel().setBlock(shape.world(m.getPos(), MegaTestRig.FRONT, c[0], c[1], c[2]),
                Blocks.AIR.defaultBlockState(), 2 | 16);
        helper.assertTrue(!m.checkPatternWithLock(), "missing core block prevents Gigaforge formation");
        helper.succeed();
    }
}
