package com.typeomega.core.integration.ae.upload;

import dev.emi.emi.api.recipe.EmiRecipe;

import java.util.List;

/** The EMI side of {@link RecipeKeys}; only loaded when EMI is, because it names EMI's classes. */
final class EmiKeys {

    private EmiKeys() {}

    /** The title the player reads over the recipe in EMI. */
    static void addCategory(List<RecipeKey.Candidate> out, Object recipeBase) {
        if (recipeBase instanceof EmiRecipe emi && emi.getCategory() != null)
            RecipeKeys.add(out, emi.getCategory().getName());
    }
}
