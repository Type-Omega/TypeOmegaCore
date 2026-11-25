package com.typeomega.core.data;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;

import net.minecraft.data.recipes.FinishedRecipe;

import com.typeomega.core.TypeOmegaMod;

import java.util.function.Consumer;

/** Material-conserving routes for Type Omega's GTNA-style compound forms. */
public final class TypeOmegaExtraFormRecipes {

    private TypeOmegaExtraFormRecipes() {}

    public static void addRecipes(Consumer<FinishedRecipe> provider) {
        for (Material material : new Material[] { TypeOmegaAlloys.PULSATING_ALLOY, TypeOmegaAlloys.CONDUCTIVE_ALLOY }) {
            ingots(provider, material);
            plate(provider, material, TypeOmegaExtraForms.TRIPLE_PLATE, 3);
            plate(provider, material, TypeOmegaExtraForms.QUADRUPLE_PLATE, 4);
            plate(provider, material, TypeOmegaExtraForms.QUINTUPLE_PLATE, 5);
            plate(provider, material, TypeOmegaExtraForms.SUPERDENSE_PLATE, 64);
        }
    }

    private static void ingots(Consumer<FinishedRecipe> provider, Material material) {
        var ingot = new MaterialEntry(TagPrefix.ingot, material);
        var doubleIngot = new MaterialEntry(TypeOmegaExtraForms.DOUBLE_INGOT, material);
        var quadrupleIngot = new MaterialEntry(TypeOmegaExtraForms.QUADRUPLE_INGOT, material);
        String name = material.getName();
        VanillaRecipeHelper.addShapedRecipe(provider, TypeOmegaMod.id("double_" + name + "_ingot"),
                ChemicalHelper.get(TypeOmegaExtraForms.DOUBLE_INGOT, material), "P", "P", 'P', ingot);
        VanillaRecipeHelper.addShapelessRecipe(provider, TypeOmegaMod.id("triple_" + name + "_ingot"),
                ChemicalHelper.get(TypeOmegaExtraForms.TRIPLE_INGOT, material), doubleIngot, ingot);
        VanillaRecipeHelper.addShapedRecipe(provider, TypeOmegaMod.id("quadruple_" + name + "_ingot"),
                ChemicalHelper.get(TypeOmegaExtraForms.QUADRUPLE_INGOT, material), "DD", 'D', doubleIngot);
        VanillaRecipeHelper.addShapelessRecipe(provider, TypeOmegaMod.id("quintuple_" + name + "_ingot"),
                ChemicalHelper.get(TypeOmegaExtraForms.QUINTUPLE_INGOT, material), quadrupleIngot, ingot);
    }

    private static void plate(Consumer<FinishedRecipe> provider, Material material, TagPrefix output, int count) {
        GTRecipeTypes.BENDER_RECIPES.recipeBuilder(TypeOmegaMod.id(material.getName() + "_" + count + "x_plate"))
                .inputItems(TagPrefix.plate, material, count)
                .circuitMeta(count == 64 ? 1 : count)
                .outputItems(output, material)
                .duration(40 * count)
                .EUt(GTValues.VA[count == 64 ? GTValues.HV : GTValues.MV])
                .save(provider);
    }
}
