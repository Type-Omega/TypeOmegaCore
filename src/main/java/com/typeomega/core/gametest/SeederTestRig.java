package com.typeomega.core.gametest;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.raishxn.gtna.common.data.GTNARecipeType;
import com.typeomega.core.mega.TypeOmegaMegastructures;
import com.typeomega.core.mega.common.MegaMachine;
import com.typeomega.core.mega.common.MegaModelData;
import com.typeomega.core.mega.common.MegaShape;
import com.typeomega.core.mega.seeder.SeederMachine;
import com.typeomega.core.mega.seeder.SeederPhysics;
import com.typeomega.core.mega.system.StarPhysics.Kind;
import com.typeomega.core.mega.system.StarSystems;

import java.util.UUID;

/** Bodies of {@link SeederGameTests}, apart so GTNA classes load only when registered. */
final class SeederTestRig {

    private SeederTestRig() {}

    static void seederMath(GameTestHelper helper) {
        helper.assertTrue(SeederPhysics.parallel(1) == 64 && SeederPhysics.parallel(5) == 320,
                "parallel scales 64..320 across 5 phases");
        double low = SeederPhysics.speed(10, 1.0, 5);
        double high = SeederPhysics.speed(100, 1.0, 5);
        helper.assertTrue(high > low && high <= SeederPhysics.SPEED_MAX,
                "higher seeding flux increases speed within bounds");

        var model = MegaModelData.get(SeederMachine.SHAPE);
        helper.assertTrue(model != null && model.parts.size() == 7,
                "Planetary Seeder Nexus 3D overlay model loads (D-448)");
        helper.assertTrue(model.parts.stream().anyMatch(MegaModelData.Part::isEmissive),
                "Seeder overlay has emissive bio-lights");
        helper.assertTrue(model.parts.stream().anyMatch(MegaModelData.Part::isTranslucent),
                "Seeder overlay has translucent bio-spire glass");
        helper.assertTrue(model.parts.stream().anyMatch(MegaModelData.Part::isMoving),
                "Seeder overlay has rotating rings and canopy");
        helper.succeed();
    }

    static void formsAndSeedsGreenhouse(GameTestHelper helper) {
        GTRecipe recipe = someGreenhouse(helper);
        if (recipe == null) {
            helper.succeed();
            return;
        }
        var star = StarSystems.get(helper.getLevel().getServer()).star(StarSystems.NEXUS);
        var kind = star.kind;
        double mass = star.mass;
        try {
            MegaMachine.anyDimensionForTests = true;
            star.kind = Kind.YELLOW_DWARF;
            star.mass = 1;
            SeederMachine m = MegaTestRig.build(helper, SeederMachine.SHAPE,
                    TypeOmegaMegastructures.PLANETARY_SEEDER_NEXUS_DEF, 5, 47);
            var bus = (ItemBusPartMachine) MegaTestRig.hatch(helper, m, 0, GTMachines.ITEM_IMPORT_BUS[GTValues.LuV]);
            var water = (FluidHatchPartMachine) MegaTestRig.hatch(helper, m, 1,
                    GTMachines.FLUID_IMPORT_HATCH[GTValues.LuV]);
            var out = (ItemBusPartMachine) MegaTestRig.hatch(helper, m, 2, GTMachines.ITEM_EXPORT_BUS[GTValues.LuV]);
            var bio = (FluidHatchPartMachine) MegaTestRig.hatch(helper, m, 3,
                    GTMachines.FLUID_EXPORT_HATCH[GTValues.LuV]);
            helper.assertTrue(m.checkPatternWithLock(),
                    "Planetary Seeder Nexus forms: " + MegaTestRig.error(m));
            m.onStructureFormed();
            UUID owner = UUID.randomUUID();
            StellarEngineTestRig.power(helper, owner);

            for (var stack : RecipeHelper.getInputItems(recipe)) {
                var big = stack.copyWithCount(64);
                for (int s = 0; s < bus.getInventory().getSlots() && !big.isEmpty(); s++)
                    big = bus.getInventory().storage.insertItem(s, big, false);
            }
            for (var f : RecipeHelper.getInputFluids(recipe)) {
                var lots = f.copy();
                lots.setAmount(Math.max(64_000, f.getAmount() * 64));
                water.tank.fillInternal(lots, IFluidHandler.FluidAction.EXECUTE);
            }
            m.setFlux(100);
            for (int i = 0; i < Math.max(2, recipe.duration * 4 / 20 + 4) && m.crafted() == 0; i++) {
                m.testSecond(owner);
            }
            helper.assertTrue(m.crafted() > 1, "Seeder harvests " + recipe.id + " in parallel: " + m.crafted() +
                    " (" + m.blocked() + ")");
            boolean any = false;
            for (int s = 0; s < out.getInventory().getSlots(); s++)
                any |= !out.getInventory().storage.getStackInSlot(s).isEmpty();
            helper.assertTrue(any, "harvest reaches the output bus");
            helper.assertTrue(m.biomass() > 0 && bio.tank.getFluidInTank(0).getAmount() > 0,
                    "Biomass exported: " + m.biomass() + " mB");
            helper.assertTrue(SeederMachine.hasActiveSeeder(helper.getLevel()),
                    "Seeder registers active state in system");
            MegaTestRig.lit(helper, m, "seeding_halo");
            MegaTestRig.capturesAndReleases(helper, m);
            helper.succeed();
        } finally {
            MegaMachine.anyDimensionForTests = false;
            star.kind = kind;
            star.mass = mass;
        }
    }

    static void missingCoreNoForm(GameTestHelper helper) {
        SeederMachine m = MegaTestRig.build(helper, SeederMachine.SHAPE,
                TypeOmegaMegastructures.PLANETARY_SEEDER_NEXUS_DEF, 5, 48);
        var shape = MegaShape.get(SeederMachine.SHAPE);
        int[] c = MegaTestRig.firstCell(shape, MegaTestRig.part(shape, "core", "body"));
        helper.getLevel().setBlock(shape.world(m.getPos(), MegaTestRig.FRONT, c[0], c[1], c[2]),
                Blocks.AIR.defaultBlockState(), 2 | 16);
        helper.assertTrue(!m.checkPatternWithLock(),
                "missing core block prevents Planetary Seeder Nexus formation");
        helper.succeed();
    }

    static void rejectsNonSystemDimension(GameTestHelper helper) {
        MegaMachine.anyDimensionForTests = false;
        SeederMachine m = MegaTestRig.build(helper, SeederMachine.SHAPE,
                TypeOmegaMegastructures.PLANETARY_SEEDER_NEXUS_DEF, 1, 49);
        helper.assertTrue(!m.inPlace(), "Planetary Seeder Nexus rejects overworld/non-system dimension");
        helper.succeed();
    }

    private static GTRecipe someGreenhouse(GameTestHelper helper) {
        for (var r : helper.getLevel().getRecipeManager().getAllRecipesFor(GTNARecipeType.GREENHOUSE_RECIPES))
            if (!RecipeHelper.getInputItems(r).isEmpty() && !RecipeHelper.getOutputItems(r).isEmpty()) return r;
        return null;
    }
}
