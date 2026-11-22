package com.typeomega.core.gametest;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.common.data.GTBlocks;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.processing.GTIAProcessing;
import com.typeomega.core.processing.TierParallel;

/**
 * F3 machines (GTIA-D-365 to D-369): each forms from its preview and breaks without a key block; GTL's parallels.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder("typeomega")
public final class ProcessingGameTests {

    private ProcessingGameTests() {}

    private static void forms(GameTestHelper h, MultiblockMachineDefinition def) {
        var c = FragmentCollectorGameTests.build(h, def, null);
        h.assertTrue(c != null && c.checkPattern(), def.getName() + " must form from its preview");
        h.succeed();
    }

    private static void breaks(GameTestHelper h, MultiblockMachineDefinition def, Block remove) {
        var c = FragmentCollectorGameTests.build(h, def, remove);
        h.assertTrue(c != null && !c.checkPattern(), def.getName() + " must not form without " + remove);
        h.succeed();
    }

    @GameTest(template = "qa_empty_24", batch = "processing", timeoutTicks = 60)
    public static void largeFurnaceForms(GameTestHelper h) { forms(h, GTIAProcessing.LARGE_FURNACE); }

    @GameTest(template = "qa_empty_24", batch = "processing", timeoutTicks = 60)
    public static void largeFurnaceNeedsChimneys(GameTestHelper h) {
        breaks(h, GTIAProcessing.LARGE_FURNACE, GTBlocks.CASING_COKE_BRICKS.get());
    }

    @GameTest(template = "qa_empty_24", batch = "processing", timeoutTicks = 60)
    public static void orePlantForms(GameTestHelper h) { forms(h, GTIAProcessing.ORE_PROCESSING_PLANT); }

    @GameTest(template = "qa_empty_24", batch = "processing", timeoutTicks = 60)
    public static void orePlantNeedsItsCrusher(GameTestHelper h) {
        breaks(h, GTIAProcessing.ORE_PROCESSING_PLANT, GTBlocks.CASING_STEEL_GEARBOX.get());
    }

    @GameTest(template = "qa_empty_24", batch = "processing", timeoutTicks = 60)
    public static void plusFactoriesForm(GameTestHelper h) {
        for (var plus : GTIAProcessing.PLUS) {
            var c = FragmentCollectorGameTests.build(h, plus, null);
            h.assertTrue(c != null && c.checkPattern(), plus.getName() + " must form from its preview");
        }
        h.succeed();
    }

    @GameTest(template = "qa_empty_24", batch = "processing", timeoutTicks = 60)
    public static void plusNeedsItsFamilyModule(GameTestHelper h) {
        breaks(h, GTIAProcessing.PROCESSING_PLUS, GTBlocks.CASING_STEEL_GEARBOX.get());
    }

    @GameTest(template = "qa_empty_5x5", batch = "processing", timeoutTicks = 20)
    public static void gtlParallels(GameTestHelper h) {
        h.assertTrue(TierParallel.largeFurnace(GTValues.LV) == 64 && TierParallel.largeFurnace(GTValues.MV) == 128,
                "large furnace 64 at LV, doubled per tier");
        h.assertTrue(TierParallel.orePlant(GTValues.MV) == 64 && TierParallel.orePlant(GTValues.HV) == 164,
                "ore plant 64 at MV, +100 per tier");
        h.assertTrue(TierParallel.plus(GTValues.LV) == 16 && TierParallel.plus(GTValues.IV) == 256 &&
                TierParallel.plus(GTValues.UV) == 256, "plus 16 at LV, 256 from IV");
        var rm = h.getLevel().getRecipeManager();
        var ids = rm.getAllRecipesFor(com.typeomega.core.data.GTIARecipeTypes.ORE_PROCESSING_PLANT).stream()
                .map(r -> r.getId().getPath()).toList();
        h.assertTrue(ids.size() > 30 && ids.stream().anyMatch(i -> i.endsWith("ore_plant_iron")) &&
                ids.stream().anyMatch(i -> i.endsWith("ore_plant_chalcopyrite")),
                "ore plant recipes for every ore, got " + ids.size());
        h.succeed();
    }
}
