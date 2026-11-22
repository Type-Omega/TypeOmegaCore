package com.typeomega.core.processing;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.GTCraftingComponents;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;

import net.minecraft.data.recipes.FinishedRecipe;

import com.typeomega.core.GTIAMod;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.LV;
import static com.gregtechceu.gtceu.api.GTValues.MV;

/** Controller recipes of the F3 machines: the single machines they replace, a hull and the tier's parts. */
public final class ProcessingRecipes {

    private ProcessingRecipes() {}

    public static void add(Consumer<FinishedRecipe> provider) {
        VanillaRecipeHelper.addShapedRecipe(provider, true, GTIAMod.id("large_furnace"),
                GTIAProcessing.LARGE_FURNACE.asStack(), "CFC", "FHF", "WCW",
                'C', CustomTags.LV_CIRCUITS, 'F', GTMachines.ELECTRIC_FURNACE[LV].asStack(),
                'H', GTCraftingComponents.HULL.get(LV), 'W', GTCraftingComponents.CABLE.get(LV));
        VanillaRecipeHelper.addShapedRecipe(provider, true, GTIAMod.id("ore_processing_plant"),
                GTIAProcessing.ORE_PROCESSING_PLANT.asStack(), "CMC", "WHT", "PCP",
                'C', CustomTags.MV_CIRCUITS, 'M', GTMachines.MACERATOR[MV].asStack(),
                'W', GTMachines.ORE_WASHER[MV].asStack(), 'T', GTMachines.CENTRIFUGE[MV].asStack(),
                'H', GTCraftingComponents.HULL.get(MV), 'P', GTCraftingComponents.PUMP.get(MV));
        for (var plus : GTIAProcessing.PLUS) {
            VanillaRecipeHelper.addShapedRecipe(provider, true, GTIAMod.id(plus.getName()), plus.asStack(),
                    "CRC", "AHA", "WCW",
                    'C', CustomTags.MV_CIRCUITS, 'R', GTCraftingComponents.ROBOT_ARM.get(MV),
                    'A', GTCraftingComponents.CONVEYOR.get(MV), 'H', GTCraftingComponents.HULL.get(MV),
                    'W', GTCraftingComponents.CABLE.get(GTValues.MV));
        }
    }
}
