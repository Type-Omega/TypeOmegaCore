package com.typeomega.core.mega.typezero;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;

import com.raishxn.gtna.common.data.GTNAEnergyHatches;
import com.raishxn.gtna.common.data.GTNAMaterials;
import com.typeomega.core.TypeOmegaMod;

import java.util.Locale;
import java.util.function.Consumer;

/**
 * Wireless energy from the National Dispatch Center on (D-401, D-419): the GTNA wireless energy and dynamo
 * hatches made from a GTCEu hatch also take a Grid Transponder, which only the Dispatch Center makes. The higher
 * amperages are made from these, so the whole family is gated. GTNA's own recipes are replaced, not edited, since
 * the gate belongs to the pack (D-376).
 */
public final class WirelessHatchGate {

    private WirelessHatchGate() {}

    public static void removeRecipes(Consumer<ResourceLocation> consumer) {
        // EN: Only the recipes made from a GTCEu hatch are replaced; GTNA's 4-to-1 recipes stay, gated through
        // their inputs.
        // PT: Só as receitas feitas de um hatch do GTCEu são trocadas; as de 4 para 1 do GTNA ficam, travadas pelas
        // entradas.
        // ZH: 只替换由 GTCEu 仓制作的配方；GTNA 的 4 合 1 配方保留，通过其输入间接受限。
        if (ModList.get().isLoaded("gtna"))
            Gtna.forEach((id, recipe) -> consumer.accept(new ResourceLocation("gtceu", "assembler/" + id)));
    }

    public static void addRecipes(Consumer<FinishedRecipe> provider) {
        if (ModList.get().isLoaded("gtna")) Gtna.forEach((id, recipe) -> recipe.accept(provider));
    }

    static String id(String io, int ampExp, int tier) {
        return "wireless_energy_" + io + "_" + (1 << (2 * ampExp)) + "a_" + GTValues.VN[tier].toLowerCase(Locale.ROOT);
    }

    /** GTNA calls live here, so this class loads only with GTNA. */
    private static final class Gtna {

        /** Each gated hatch: GTNA's recipe id and the replacement recipe, saved when run. */
        static void forEach(java.util.function.BiConsumer<String, Consumer<Consumer<FinishedRecipe>>> action) {
            for (boolean output : new boolean[] { false, true }) {
                MachineDefinition[][] base = output ? new MachineDefinition[][] {
                        GTMachines.ENERGY_OUTPUT_HATCH, GTMachines.ENERGY_OUTPUT_HATCH_4A,
                        GTMachines.ENERGY_OUTPUT_HATCH_16A, GTMachines.SUBSTATION_ENERGY_OUTPUT_HATCH,
                        GTMachines.LASER_OUTPUT_HATCH_256, GTMachines.LASER_OUTPUT_HATCH_1024,
                        GTMachines.LASER_OUTPUT_HATCH_4096 } :
                        new MachineDefinition[][] {
                                GTMachines.ENERGY_INPUT_HATCH, GTMachines.ENERGY_INPUT_HATCH_4A,
                                GTMachines.ENERGY_INPUT_HATCH_16A, GTMachines.SUBSTATION_ENERGY_INPUT_HATCH,
                                GTMachines.LASER_INPUT_HATCH_256, GTMachines.LASER_INPUT_HATCH_1024,
                                GTMachines.LASER_INPUT_HATCH_4096 };
                var wireless = output ? GTNAEnergyHatches.WIRELESS_DYNAMO_HATCHES :
                        GTNAEnergyHatches.WIRELESS_ENERGY_HATCHES;
                for (int tier = GTValues.LV; tier <= GTValues.MAX; tier++) {
                    int part = Math.min(tier, GTValues.OpV);
                    Item sensor = component(part, output ? "emitter" : "sensor");
                    int t = tier;
                    var solder = tier <= GTValues.LuV ? GTMaterials.SolderingAlloy : GTNAMaterials.Indalloy140;
                    for (int ampExp = 0; ampExp < base.length && ampExp < wireless[tier].length; ampExp++) {
                        var hatch = tier < base[ampExp].length ? base[ampExp][tier] : null;
                        if (wireless[tier][ampExp] == null || hatch == null || sensor == null) continue;
                        // EN: Same inputs as GTNA's recipe (GTNAMachineRecipes.registerWirelessHatchRecipes) plus the
                        // transponder; keep them in step when GTNA changes its recipe.
                        // PT: As mesmas entradas da receita do GTNA (GTNAMachineRecipes.registerWirelessHatchRecipes)
                        // mais o transponder; manter iguais quando o GTNA mudar a receita.
                        // ZH: 与 GTNA 配方（GTNAMachineRecipes.registerWirelessHatchRecipes）相同的输入，另加应答器；
                        // GTNA 修改配方时需同步。
                        String id = id(output ? "out" : "in", ampExp, tier);
                        var out = wireless[tier][ampExp];
                        int exp = ampExp;
                        action.accept(id, provider -> GTRecipeTypes.ASSEMBLER_RECIPES
                                .recipeBuilder(TypeOmegaMod.id("wireless/" + id))
                                .inputItems(hatch.asStack(), exp <= 2 ? 2 : 1)
                                .inputItems(sensor, Math.min(16, 2 << Math.min(exp, 3)))
                                .inputItems(TagPrefix.plate, GTMaterials.EnderPearl, Math.min(16, 2 + 2 * exp))
                                .inputItems(CustomTags.CIRCUITS_ARRAY[t], 1 + exp / 2)
                                .inputItems(TypeZeroItems.GRID_TRANSPONDER.asStack())
                                .inputFluids(solder.getFluid(144 << Math.min(exp, 4)))
                                .outputItems(out.asStack())
                                .duration(200 * (1 + exp / 2)).EUt(GTValues.VA[t])
                                .save(provider));
                    }
                }
            }
        }

        private static Item component(int tier, String kind) {
            Item item = BuiltInRegistries.ITEM.get(new ResourceLocation("gtceu",
                    GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_" + kind));
            return item == Items.AIR ? null : item;
        }
    }
}
