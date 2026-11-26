package com.typeomega.core.gametest;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.TypeOmegaMod;

/** Agronomica biomass in GT machines (D-271, D-426). Needs -PagronomicaDevJar; skipped without it. */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class AgroRecipeGameTests {

    private AgroRecipeGameTests() {}

    @GameTest(template = "qa_empty_5x5", batch = "agro_recipes", timeoutTicks = 20)
    public static void lvFermenterMakesLessBiomassThanTheBrewery(GameTestHelper h) {
        if (!ModList.get().isLoaded("agronomica")) {
            TypeOmegaMod.LOGGER
                    .warn("Agronomica not loaded (run with -PagronomicaDevJar): fermenter biomass test skipped");
            h.succeed();
            return;
        }
        GTRecipe fermenter = recipe(h, "typeomega:fermenter/agro/biomass_bundle_fermenter");
        GTRecipe brewery = recipe(h, "typeomega:brewery/agro/biomass_bundle_biomass");
        h.assertTrue(fermenter.recipeType == GTRecipeTypes.FERMENTING_RECIPES, "not a Fermenter recipe");
        h.assertTrue(fermenter.getInputEUt().getTotalEU() == 30, "Fermenter biomass must be 30 EU/t (LV)");
        long fermented = RecipeHelper.getOutputFluids(fermenter).get(0).getAmount();
        long brewed = RecipeHelper.getOutputFluids(brewery).get(0).getAmount();
        h.assertTrue(fermented > 0 && fermented < brewed,
                "Fermenter yield " + fermented + " mB must be below the Brewery's " + brewed + " mB");
        h.succeed();
    }

    private static GTRecipe recipe(GameTestHelper h, String id) {
        var r = h.getLevel().getServer().getRecipeManager().byKey(new ResourceLocation(id));
        h.assertTrue(r.isPresent() && r.get() instanceof GTRecipe, "missing recipe " + id);
        return (GTRecipe) r.get();
    }
}
