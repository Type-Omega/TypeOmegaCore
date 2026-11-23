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
 * first chapter (GTIA-D-409), the Industrial Platform builder after the Bronze Hull (GTIA-D-406, D-408) and
 * Helium-3 only from heated lunar regolith (GTIA-D-420).
 */
public final class ProgressionGates {

    static final List<String> REMOVED = List.of(
            "minecraft:ender_eye",
            "gtceu:chemical_bath/eye_of_ender",
            "modernspace:personal_space_portal",
            "gtceu:assembler/nexus_flux_matrix",
            "gtceu:shaped/basic_terminal",
            "industrial_platform:platform_builder",
            "gtceu:centrifuge/helium_separation");

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
        // EN: HV emitters and sensors are the first parts that need the eye (GTIA-D-352). The Nether stays closed, so
        // sulfur and redstone stand in for the blaze powder (GTIA-D-419).
        // PT: Emissores e sensores HV são as primeiras peças que pedem o olho (GTIA-D-352). O Nether segue fechado,
        // então enxofre e redstone fazem o papel do pó de blaze (GTIA-D-419).
        // ZH: HV 发射器和传感器是最早需要末影之眼的部件（GTIA-D-352）。下界仍未开放，因此用硫和红石代替烈焰粉（GTIA-D-419）。
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTIAMod.id("ender_eye"))
                .inputItems(new ItemStack(Items.ENDER_PEARL))
                .inputItems(TagPrefix.dust, GTMaterials.Sulfur)
                .inputItems(TagPrefix.dust, GTMaterials.Redstone)
                .outputItems(new ItemStack(Items.ENDER_EYE))
                .duration(200).EUt(GTValues.VA[GTValues.HV]).save(provider);
        addLunarHelium3(provider);
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

    /** Helium-3 from heated lunar regolith, only when Ad Astra is loaded (GTIA-D-420). */
    private static void addLunarHelium3(Consumer<FinishedRecipe> provider) {
        if (!ModList.get().isLoaded("ad_astra")) return;
        var sand = ForgeRegistries.ITEMS.getValue(new ResourceLocation("ad_astra", "moon_sand"));
        if (sand == null || sand == Items.AIR) return;
        // EN: Heat frees the solar-wind volatiles at 480 EU/t; 25 mB per sand is a first value for the beta. Hydrogen
        // leaves too, but the Extractor has a single fluid output, so only the He-3 is kept.
        // PT: O calor solta os voláteis do vento solar a 480 EU/t; 25 mB por areia é um valor inicial do beta. O
        // hidrogênio também sai, mas a Extratora tem uma saída de fluido só, então fica só o He-3.
        // ZH: 加热以 480 EU/t 释放太阳风挥发物；每份月沙 25 mB 是测试版的初始值。氢也会释放，但提取机只有一个流体输出，
        // 因此只保留氦-3。
        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(GTIAMod.id("helium_3_from_regolith"))
                .inputItems(new ItemStack(sand))
                .outputFluids(GTMaterials.Helium3.getFluid(25))
                .duration(200).EUt(GTValues.VA[GTValues.HV]).save(provider);
    }
}
