package com.typeomega.core.route;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import com.typeomega.core.GTIAMod;

import java.util.List;
import java.util.function.Consumer;

/**
 * Recipes moved for the progression of the restructuring: the Eye of Ender only in the HV Assembler (GTIA-D-352),
 * the personal dimension portal only as a quest reward (GTIA-D-347), the Nexus Flux Matrix in HV (GTIA-D-350; its
 * losses are in the pack's config/gtna/balance/nexus_flux_matrix.json), the GTCEu Terminal with redstone in the
 * first chapter (GTIA-D-409) and the Industrial Platform builder after the Bronze Hull (GTIA-D-406, D-408).
 */
public final class ProgressionGates {

    static final List<String> REMOVED = List.of(
            "minecraft:ender_eye",
            "gtceu:chemical_bath/eye_of_ender",
            "modernspace:personal_space_portal",
            "gtceu:assembler/nexus_flux_matrix",
            "gtceu:shaped/basic_terminal",
            "industrial_platform:platform_builder");

    private ProgressionGates() {}

    public static void removeRecipes(Consumer<ResourceLocation> consumer) {
        REMOVED.forEach(id -> consumer.accept(new ResourceLocation(id)));
    }

    public static List<ResourceLocation> ids() {
        return REMOVED.stream().map(ResourceLocation::new).toList();
    }

    public static void addRecipes(Consumer<FinishedRecipe> provider) {
        // EN: Red alloy needs the alloy smelter, so the Terminal of the first chapter takes plain redstone (D-409).
        // PT: Liga vermelha pede o alloy smelter, então o Terminal do primeiro capítulo usa redstone puro (D-409).
        // ZH: 红色合金需要合金炉，因此第一章的终端改用普通红石（D-409）。
        VanillaRecipeHelper.addShapedRecipe(provider, true, GTIAMod.id("basic_terminal"), GTItems.TERMINAL.asStack(),
                "SGS", "PBP", "PWP", 'S', new MaterialEntry(TagPrefix.screw, GTMaterials.WroughtIron),
                'G', Tags.Items.GLASS_PANES, 'B', new ItemStack(Items.BOOK),
                'P', new MaterialEntry(TagPrefix.plate, GTMaterials.WroughtIron), 'W', new ItemStack(Items.REDSTONE));
        if (ModList.get().isLoaded("industrial_platform")) {
            VanillaRecipeHelper.addShapedRecipe(provider, GTIAMod.id("platform_builder"),
                    new ItemStack(ForgeRegistries.ITEMS.getValue(
                            new ResourceLocation("industrial_platform", "platform_builder"))),
                    "PSP", "SCS", "PSP", 'P', new MaterialEntry(TagPrefix.plate, GTMaterials.Bronze),
                    'S', new ItemStack(Items.SMOOTH_STONE), 'C', GTBlocks.BRONZE_HULL.asStack());
        }
        // EN: HV emitters and sensors are the first parts that need the eye, so the End opens with them (GTIA-D-352).
        // PT: Emissores e sensores HV são as primeiras peças que pedem o olho, então o End abre junto (GTIA-D-352).
        // ZH: HV 发射器和传感器是最早需要末影之眼的部件，所以末地随之开放（GTIA-D-352）。
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTIAMod.id("ender_eye"))
                .inputItems(new ItemStack(Items.ENDER_PEARL))
                .inputItems(new ItemStack(Items.BLAZE_POWDER, 2))
                .outputItems(new ItemStack(Items.ENDER_EYE))
                .duration(200).EUt(GTValues.VA[GTValues.HV]).save(provider);
        if (!ModList.get().isLoaded("gtna")) return;
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTIAMod.id("nexus_flux_matrix"))
                .inputItems(TagPrefix.plateDouble, GTMaterials.StainlessSteel, 4)
                .inputItems(CustomTags.HV_CIRCUITS, 2)
                .inputItems(TagPrefix.plate, GTMaterials.EnderEye, 2)
                .inputItems(TagPrefix.frameGt, GTMaterials.StainlessSteel)
                .outputItems(new ItemStack(
                        ForgeRegistries.ITEMS.getValue(new ResourceLocation("gtna", "nexus_flux_matrix"))))
                .duration(400).EUt(GTValues.VA[GTValues.HV]).save(provider);
    }
}
