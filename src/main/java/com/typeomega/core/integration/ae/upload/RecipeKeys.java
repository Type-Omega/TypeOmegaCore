package com.typeomega.core.integration.ae.upload;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fml.ModList;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Builds the {@link RecipeKey} of a recipe the player moved into a pattern encoder. A GregTech recipe is named after
 * its recipe type, the name of the machine; the recipe viewer's category title and the plain recipe type come after,
 * for other mods' machines.
 */
public final class RecipeKeys {

    private RecipeKeys() {}

    /** The key of a GregTech recipe type, "Assembler" for {@code gtceu:assembler}; null when it has no name. */
    @Nullable
    public static RecipeKey ofGtType(GTRecipeType type) {
        var out = new ArrayList<RecipeKey.Candidate>(1);
        addGtType(out, type);
        return out.isEmpty() ? null : new RecipeKey(out);
    }

    /**
     * @param recipeBase what the viewer handed over: an EMI recipe, a JEI recipe or the recipe itself
     * @param recipe     the Minecraft recipe behind it, when there is one
     */
    @Nullable
    public static RecipeKey of(@Nullable Object recipeBase, @Nullable Recipe<?> recipe) {
        var out = new ArrayList<RecipeKey.Candidate>(MAX);
        GTRecipe gt = recipe instanceof GTRecipe g ? g : recipeBase instanceof GTRecipe g2 ? g2 : null;
        if (gt != null) addGtType(out, gt.recipeType);
        if (recipeBase != null && ModList.get().isLoaded("emi")) EmiKeys.addCategory(out, recipeBase);
        if (out.isEmpty() && recipe != null) addPlainType(out, recipe);
        return out.isEmpty() ? null : new RecipeKey(out.size() > MAX ? List.copyOf(out.subList(0, MAX)) : out);
    }

    private static final int MAX = RecipeKey.MAX_CANDIDATES;

    private static void addGtType(List<RecipeKey.Candidate> out, GTRecipeType type) {
        if (type == null || type.registryName == null) return;
        String lang = type.registryName.toLanguageKey();
        if (Language.getInstance().has(lang)) add(out, Component.translatable(lang));
    }

    private static void addPlainType(List<RecipeKey.Candidate> out, Recipe<?> recipe) {
        var id = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
        if (id != null) add(out, Component.literal(id.getPath().replace('_', ' ').toLowerCase(Locale.ROOT)));
    }

    static void add(List<RecipeKey.Candidate> out, Component title) {
        var candidate = RecipeKey.Candidate.of(title);
        if (candidate.text().isEmpty()) return;
        for (var seen : out) if (seen.text().equalsIgnoreCase(candidate.text())) return;
        out.add(candidate);
    }
}
