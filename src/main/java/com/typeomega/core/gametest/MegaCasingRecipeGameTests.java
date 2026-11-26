package com.typeomega.core.gametest;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.recipe.condition.ResearchCondition;
import com.gregtechceu.gtceu.config.ConfigHolder;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

import com.typeomega.core.TypeOmegaMod;

/**
 * The recipes of the pilot casings (D-445 M6): each hull is an Assembly Line recipe at the tier of its controller with
 * a Research Station research from the hull before it and 64 blocks per craft; the shared deck and the accents are
 * cheaper Assembler recipes. Needs GTNA (the recipes only exist with it).
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class MegaCasingRecipeGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";

    private MegaCasingRecipeGameTests() {}

    private static Item item(String id) {
        return ForgeRegistries.ITEMS.getValue(TypeOmegaMod.id(id));
    }

    private static GTRecipe recipe(GameTestHelper helper, String path) {
        var found = helper.getLevel().getServer().getRecipeManager().byKey(TypeOmegaMod.id(path)).orElse(null);
        helper.assertTrue(found instanceof GTRecipe, "missing recipe typeomega:" + path);
        return (GTRecipe) found;
    }

    private static int inputs(GTRecipe recipe) {
        int n = 0;
        for (var stack : RecipeHelper.getInputItems(recipe)) n += stack.getCount();
        return n;
    }

    private static void yields(GameTestHelper helper, GTRecipe recipe, String block, int count, int tier) {
        var out = RecipeHelper.getOutputItems(recipe);
        helper.assertTrue(out.size() == 1 && out.get(0).getItem() == item(block) && out.get(0).getCount() == count,
                block + " comes out " + count + " per craft");
        helper.assertTrue(recipe.getInputEUt().getTotalEU() == GTValues.VA[tier],
                block + " runs at the tier of its controller (" + GTValues.VN[tier] + ")");
    }

    /** True when a Research Station recipe takes {@code block} as the item to research. */
    private static boolean researchedFrom(GameTestHelper helper, String block) {
        for (var r : helper.getLevel().getRecipeManager().getAllRecipesFor(GTRecipeTypes.RESEARCH_STATION_RECIPES))
            for (var stack : RecipeHelper.getInputItems(r)) if (stack.getItem() == item(block)) return true;
        return false;
    }

    private static boolean hasResearch(GTRecipe recipe) {
        return recipe.conditions.stream().anyMatch(ResearchCondition.class::isInstance);
    }

    @GameTest(template = TEMPLATE, batch = "mega_casing_recipes", timeoutTicks = 100)
    public static void hullsAreResearchedAndYieldMany(GameTestHelper helper) {
        if (!com.typeomega.core.mega.TypeOmegaMegastructures.gtna()) {
            helper.succeed();
            return;
        }
        // Star Lifter (UEV), Nidavellir (UIV), Yggdrasil (UIV): the ladder of the pilot.
        var lifter = recipe(helper, "assembly_line/lifter_girdle_hull");
        var forge = recipe(helper, "assembly_line/forge_cobalt_plate");
        var orchid = recipe(helper, "assembly_line/orchid_cell_frame");
        yields(helper, lifter, "lifter_girdle_hull", 64, GTValues.UEV);
        yields(helper, forge, "forge_cobalt_plate", 64, GTValues.UIV);
        yields(helper, orchid, "orchid_cell_frame", 64, GTValues.UIV);
        if (ConfigHolder.INSTANCE.machines.enableResearch) {
            for (var r : new GTRecipe[] { lifter, forge, orchid })
                helper.assertTrue(hasResearch(r), r.id + " needs a research");
            // The next hull is researched from the hull before it.
            helper.assertTrue(researchedFrom(helper, "lifter_girdle_hull"),
                    "the cobalt plate is researched from the girdle hull");
            helper.assertTrue(researchedFrom(helper, "forge_cobalt_plate"),
                    "the Orchid frame is researched from the cobalt plate");
        }
        // The hull before it is also an ingredient, so the chain cannot be skipped.
        helper.assertTrue(RecipeHelper.getInputItems(forge).stream()
                .anyMatch(s -> s.getItem() == item("lifter_girdle_hull")), "the cobalt plate eats girdle hulls");
        helper.assertTrue(RecipeHelper.getInputItems(orchid).stream()
                .anyMatch(s -> s.getItem() == item("forge_cobalt_plate")), "the Orchid frame eats cobalt plates");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "mega_casing_recipes", timeoutTicks = 100)
    public static void sharedBlocksAndAccentsAreCheaper(GameTestHelper helper) {
        if (!com.typeomega.core.mega.TypeOmegaMegastructures.gtna()) {
            helper.succeed();
            return;
        }
        var hull = recipe(helper, "assembly_line/lifter_girdle_hull");
        var deck = recipe(helper, "assembler/giga_service_deck");
        var truss = recipe(helper, "assembler/giga_truss");
        yields(helper, deck, "giga_service_deck", 64, GTValues.UEV);
        yields(helper, truss, "giga_truss", 64, GTValues.UEV);
        yields(helper, recipe(helper, "assembler/giga_lamp"), "giga_lamp", 16, GTValues.UEV);
        yields(helper, recipe(helper, "assembler/heated_hex_plate"), "heated_hex_plate", 16, GTValues.UEV);
        yields(helper, recipe(helper, "assembler/terrace_rim_light"), "terrace_rim_light", 16, GTValues.UIV);
        yields(helper, recipe(helper, "assembler/orchid_cell_glass"), "orchid_cell_glass", 16, GTValues.UIV);
        yields(helper, recipe(helper, "assembler/orchid_window_rim"), "orchid_window_rim", 16, GTValues.UIV);
        helper.assertTrue(!hasResearch(deck) && !hasResearch(truss), "the shared blocks need no research");
        helper.assertTrue(inputs(deck) < inputs(hull) / 2, "the deck takes far fewer items than a hull: " +
                inputs(deck) + " against " + inputs(hull));
        helper.succeed();
    }
}
