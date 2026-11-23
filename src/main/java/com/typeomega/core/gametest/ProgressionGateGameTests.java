package com.typeomega.core.gametest;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.GTIAMod;
import com.typeomega.core.route.AE2Gates;
import com.typeomega.core.route.ProgressionGates;

import java.util.ArrayList;
import java.util.List;

/**
 * The restructuring gates: Eye of Ender in HV, portal only from a quest, basic AE2 in LV and the rest in MV/HV, the
 * Terminal with redstone, the Industrial Platform after the Bronze Hull (GTIA-D-347, D-349, D-352, D-406, D-408,
 * D-409).
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(GTIAMod.MOD_ID)
public final class ProgressionGateGameTests {

    private ProgressionGateGameTests() {}

    @GameTest(template = "qa_empty_5x5", batch = "gates", timeoutTicks = 20)
    public static void gatedRecipesAreReplaced(GameTestHelper h) {
        var manager = h.getLevel().getServer().getRecipeManager();
        List<ResourceLocation> removed = new ArrayList<>(ProgressionGates.ids());
        List<String> kept = new ArrayList<>(
                List.of("typeomega:assembler/ender_eye", "typeomega:assembler/nexus_flux_matrix",
                        "typeomega:shaped/basic_terminal"));
        if (ModList.get().isLoaded("industrial_platform")) kept.add("typeomega:shaped/platform_builder");
        if (ModList.get().isLoaded("ae2")) {
            removed.addAll(AE2Gates.ids());
            kept.addAll(List.of("typeomega:laser_engraver/ae2/printed_logic",
                    "typeomega:circuit_assembler/ae2/logic_processor",
                    "typeomega:assembler/ae2/controller", "typeomega:assembler/ae2/drive",
                    "typeomega:assembler/ae2/molecular_assembler",
                    "typeomega:assembler/ae2/wireless_access_point", "typeomega:assembler/ae2/cell_component_16k",
                    "typeomega:mixer/ae2/fluix_crystal"));
            // A block-to-part conversion stays: it is not a way around the gate.
            kept.add("ae2:network/blocks/pattern_providers_interface_alt");
        }
        var present = removed.stream().filter(id -> manager.byKey(id).isPresent()).toList();
        var missing = kept.stream().map(ResourceLocation::new).filter(id -> manager.byKey(id).isEmpty()).toList();
        h.assertTrue(present.isEmpty(), "gated recipes still loaded: " + present);
        h.assertTrue(missing.isEmpty(), "replacement recipes missing: " + missing);
        h.succeed();
    }

    @GameTest(template = "qa_empty_5x5", batch = "gates", timeoutTicks = 20)
    public static void terminalTakesRedstone(GameTestHelper h) {
        var manager = h.getLevel().getServer().getRecipeManager();
        var terminal = manager.byKey(GTIAMod.id("shaped/basic_terminal")).orElse(null);
        h.assertTrue(terminal != null, "the Terminal recipe is missing");
        var ingredients = terminal.getIngredients();
        h.assertTrue(ingredients.stream().anyMatch(i -> i.test(new ItemStack(Items.REDSTONE))),
                "the Terminal takes redstone (GTIA-D-409)");
        h.assertTrue(ingredients.stream().noneMatch(i -> i.test(
                ChemicalHelper.get(TagPrefix.wireGtSingle, GTMaterials.RedAlloy))),
                "the Terminal has no red alloy wire");
        var macerated = (GTRecipe) manager.byKey(new ResourceLocation("gtceu", "macerator/macerate_terminal"))
                .orElse(null);
        if (macerated != null) {
            h.assertTrue(RecipeHelper.getOutputItems(macerated).stream()
                    .noneMatch(stack -> stack.getItem().toString().contains("red_alloy")),
                    "recycling the Terminal gives no red alloy");
        }
        h.succeed();
    }

    /** The basic AE2 network runs on LV power (GTIA-D-408, D-415), with no Nether quartz in the cores or the fluix. */
    @GameTest(template = "qa_empty_5x5", batch = "gates", timeoutTicks = 20)
    public static void basicAe2IsLv(GameTestHelper h) {
        if (!ModList.get().isLoaded("ae2")) {
            h.succeed();
            return;
        }
        var manager = h.getLevel().getServer().getRecipeManager();
        for (String path : List.of("laser_engraver/ae2/printed_logic", "laser_engraver/ae2/printed_engineering",
                "laser_engraver/ae2/printed_calculation", "laser_engraver/ae2/printed_silicon",
                "circuit_assembler/ae2/logic_processor", "mixer/ae2/fluix_crystal", "assembler/ae2/annihilation_core",
                "assembler/ae2/formation_core", "assembler/ae2/controller", "assembler/ae2/drive",
                "assembler/ae2/terminal", "assembler/ae2/item_cell_housing", "assembler/ae2/cell_component_1k")) {
            var recipe = (GTRecipe) manager.byKey(GTIAMod.id(path)).orElse(null);
            h.assertTrue(recipe != null, "missing typeomega:" + path);
            h.assertTrue(recipe.getInputEUt().getTotalEU() <= GTValues.VA[GTValues.LV], path + " runs on LV");
            var quartz = new ItemStack(Items.QUARTZ);
            h.assertTrue(recipe.getInputContents(ItemRecipeCapability.CAP).stream()
                    .noneMatch(c -> ItemRecipeCapability.CAP.of(c.getContent()).test(quartz)),
                    path + " takes no Nether quartz");
        }
        h.succeed();
    }
}
