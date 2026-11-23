package com.typeomega.core.mega.typezero;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.data.recipes.FinishedRecipe;

import com.typeomega.core.GTIAMod;
import com.typeomega.core.data.GTIARecipeTypes;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.VA;

/** Recipes of the Type 0 megastructures and of what they make (GTIA-D-390, D-419). */
public final class TypeZeroRecipes {

    private static final int HV = GTValues.HV;

    private TypeZeroRecipes() {}

    public static void addRecipes(Consumer<FinishedRecipe> provider) {
        controllers(provider);
        dispatchAndMissions(provider);
    }

    private static void controllers(Consumer<FinishedRecipe> provider) {
        // GTIA-D-419: HV hull, 4 HV transformers, HV batteries, HV Energy Hatch, aluminium cable, 16 HV circuits and
        // an MV Field Generator.
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTIAMod.id(NationalDispatchCenter.ID))
                .inputItems(GTMachines.HULL[HV].asStack())
                .inputItems(GTMachines.TRANSFORMER[HV].asStack(4))
                .inputItems(CustomTags.HV_BATTERIES, 4)
                .inputItems(GTMachines.ENERGY_INPUT_HATCH[HV].asStack())
                .inputItems(TagPrefix.cableGtSingle, GTMaterials.Aluminium, 8)
                .inputItems(CustomTags.HV_CIRCUITS, 16)
                .inputItems(GTItems.FIELD_GENERATOR_MV.asStack())
                .outputItems(NationalDispatchCenter.DEFINITION.asStack())
                .duration(600).EUt(VA[HV]).save(provider);
    }

    private static void dispatchAndMissions(Consumer<FinishedRecipe> provider) {
        GTIARecipeTypes.GRID_DISPATCH.recipeBuilder(GTIAMod.id("grid_transponder"))
                .inputItems(TagPrefix.plate, GTMaterials.Aluminium, 2)
                .inputItems(CustomTags.HV_CIRCUITS, 1)
                .inputItems(TagPrefix.wireFine, GTMaterials.Gold, 4)
                .outputItems(TypeZeroItems.GRID_TRANSPONDER.asStack())
                .duration(200).EUt(VA[HV]).save(provider);
    }
}
