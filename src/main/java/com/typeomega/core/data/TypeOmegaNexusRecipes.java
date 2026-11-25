package com.typeomega.core.data;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.GTCraftingComponents;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import com.typeomega.core.TypeOmegaMod;

import java.util.function.Consumer;

/** Recipes for GTNA items that ship without one in the pack. */
public final class TypeOmegaNexusRecipes {

    private TypeOmegaNexusRecipes() {}

    public static void addRecipes(Consumer<FinishedRecipe> provider) {
        steamVoidOre(provider);
        var linker = ForgeRegistries.ITEMS.getValue(new ResourceLocation("gtna", "nexus_linker"));
        if (linker == null || linker == Items.AIR) return;
        // EN: LV tool that binds wireless hatches to a Flux Matrix: sensor + emitter (it reads and sends), ender pearl.
        // PT: Ferramenta LV que liga escotilhas sem fio a uma Flux Matrix: sensor + emissor (lê e envia), pérola do
        // fim.
        // ZH: 将无线仓绑定到通量矩阵的 LV 工具：传感器 + 发射器（读取并发送）、末影珍珠。
        VanillaRecipeHelper.addShapedRecipe(provider, true, TypeOmegaMod.id("nexus_linker"), new ItemStack(linker),
                "SPE", "CHC", "RPR",
                'S', GTCraftingComponents.SENSOR.get(GTValues.LV),
                'E', GTCraftingComponents.EMITTER.get(GTValues.LV),
                'C', GTCraftingComponents.CIRCUIT.get(GTValues.LV),
                'H', new ItemStack(Items.ENDER_PEARL),
                'P', new MaterialEntry(TagPrefix.plate, GTMaterials.Steel),
                'R', new MaterialEntry(TagPrefix.wireGtSingle, GTMaterials.RedAlloy));
    }

    /**
     * Steam Void Ore (D-361): the four starter veins, one raw ore of each per 8 s, 1 800 ores per hour.
     * EN: GTNA registers the type; the pack decides the ores. PT: O GTNA registra o tipo; o pack escolhe os minérios.
     * ZH: GTNA 注册配方类型；矿石由整合包决定。
     */
    private static void steamVoidOre(Consumer<FinishedRecipe> provider) {
        var type = com.gregtechceu.gtceu.api.registry.GTRegistries.RECIPE_TYPES
                .get(new ResourceLocation("gtna", "steam_void_ore"));
        if (type == null) return;
        type.recipeBuilder(TypeOmegaMod.id("steam_void_ore_starter_veins"))
                .outputItems(TagPrefix.rawOre, GTMaterials.Iron)
                .outputItems(TagPrefix.rawOre, GTMaterials.Copper)
                .outputItems(TagPrefix.rawOre, GTMaterials.Tin)
                .outputItems(TagPrefix.rawOre, GTMaterials.Coal)
                .duration(160).EUt(GTValues.V[GTValues.ULV])
                .save(provider);
    }
}
