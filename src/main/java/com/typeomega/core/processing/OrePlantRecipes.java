package com.typeomega.core.processing;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.data.recipes.FinishedRecipe;

import com.typeomega.core.GTIAMod;
import com.typeomega.core.data.GTIARecipeTypes;

import java.util.function.Consumer;

/**
 * Ore Processing Plant recipes (GTIA-D-366): raw ore to the final dusts in one step, the yield of the full GTCEu
 * route (macerate, wash, macerate, centrifuge) without the intermediate items.
 */
public final class OrePlantRecipes {

    private OrePlantRecipes() {}

    public static void add(Consumer<FinishedRecipe> provider) {
        for (Material m : GTCEuAPI.materialManager.getRegisteredMaterials()) {
            if (!m.hasProperty(PropertyKey.ORE) || ChemicalHelper.get(TagPrefix.rawOre, m).isEmpty()) continue;
            var ore = m.getProperty(PropertyKey.ORE);
            Material main = m;
            var dust = ChemicalHelper.get(TagPrefix.dust, main, 2 * ore.getOreMultiplier());
            if (dust.isEmpty()) continue;
            // EN: 2 dusts per raw ore (macerator ×2), two byproducts at 33% and 14% like washer + centrifuge.
            // PT: 2 pós por minério bruto (macerador ×2), dois subprodutos a 33% e 14% como lavadora + centrífuga.
            // ZH: 每个粗矿 2 份粉（研磨 ×2），两个副产物 33% 与 14%，相当于洗矿 + 离心。
            var b = GTIARecipeTypes.ORE_PROCESSING_PLANT.recipeBuilder(GTIAMod.id("ore_plant_" + m.getName()))
                    .inputItems(TagPrefix.rawOre, m)
                    .inputFluids(GTMaterials.Water.getFluid(100))
                    .outputItems(dust)
                    .duration(200).EUt(GTValues.VA[GTValues.LV]);
            var by = ore.getOreByProducts();
            int[] chance = { 3333, 1400 };
            for (int i = 0; i < Math.min(2, by.size()); i++) {
                var s = ChemicalHelper.get(TagPrefix.dust, by.get(i));
                if (!s.isEmpty()) b.chancedOutput(s, chance[i], 0);
            }
            b.save(provider);
        }
    }
}
