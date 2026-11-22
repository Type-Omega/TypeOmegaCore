package com.raishxn.gtia.miner;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.worldgen.bedrockore.BedrockOreDefinition;
import com.gregtechceu.gtceu.api.data.worldgen.bedrockore.WeightedMaterial;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import com.raishxn.gtia.GTIAMod;
import com.raishxn.gtia.data.GTIAItems;
import com.raishxn.gtia.data.GTIARecipeTypes;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragment Collectors (GTIA-D-362, D-363). A World Fragment alone mines a random GTIA vein of its dimension; with a
 * Vein Card of that dimension it mines the card's vein at the card's purity. Neither item is consumed, and each cycle
 * may give a copy of the fragment back (GTL's rule, D-362). 4 operations per 8 s cycle at LV; the Large Collector
 * runs 16 at once.
 */
public enum FragmentCollectorLogic implements GTRecipeType.ICustomRecipeLogic {

    INSTANCE;

    public static final int DURATION = 160, OPERATIONS = 4, LARGE_PARALLEL = 16;
    /** Chance (out of 10 000) of a fragment copy per cycle, as in GTL Enhanced. */
    public static final int DUPLICATE_CHANCE = 5000;

    /** The input a collector would use now: fragment dimension, optional card. */
    public record Input(ResourceKey<Level> dimension, ItemStack fragment, @Nullable VeinCardItem.Record card) {}

    @Nullable
    public static ResourceKey<Level> dimensionOf(ItemStack stack) {
        if (stack.is(GTIAItems.WORLD_FRAGMENT_OVERWORLD.get())) return Level.OVERWORLD;
        if (stack.is(GTIAItems.WORLD_FRAGMENT_NETHER.get())) return Level.NETHER;
        if (stack.is(GTIAItems.WORLD_FRAGMENT_END.get())) return Level.END;
        return null;
    }

    @Nullable
    public static Input input(IRecipeCapabilityHolder holder) {
        ItemStack fragment = null;
        ResourceKey<Level> dimension = null;
        List<VeinCardItem.Record> cards = new ArrayList<>();
        for (var list : holder.getCapabilitiesForIO(IO.IN)) {
            for (var handler : list.getCapability(ItemRecipeCapability.CAP)) {
                for (var content : handler.getContents()) {
                    if (!(content instanceof ItemStack stack) || stack.isEmpty()) continue;
                    var dim = dimensionOf(stack);
                    if (dim != null && fragment == null) {
                        fragment = stack;
                        dimension = dim;
                    }
                    var card = VeinCardItem.read(stack);
                    if (card != null) cards.add(card);
                }
            }
        }
        if (fragment == null) return null;
        for (var card : cards)
            if (card.dimension().equals(dimension.location())) return new Input(dimension, fragment, card);
        return new Input(dimension, fragment, null);
    }

    /** GTIA veins (D-310 table) of one dimension. */
    public static List<BedrockOreDefinition> veins(ResourceKey<Level> dimension) {
        List<BedrockOreDefinition> out = new ArrayList<>();
        for (var id : GTRegistries.BEDROCK_ORE_DEFINITIONS.keys()) {
            if (!id.getNamespace().equals(GTIAMod.MOD_ID)) continue;
            var def = GTRegistries.BEDROCK_ORE_DEFINITIONS.get(id);
            if (def != null && def.dimensionFilter() != null && def.dimensionFilter().contains(dimension)) out.add(def);
        }
        return out;
    }

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var input = input(holder);
        if (input == null) return null;
        int parallel = holder instanceof FragmentCollectors.Large ? LARGE_PARALLEL : 1;
        var random = holder instanceof com.gregtechceu.gtceu.api.machine.MetaMachine m && m.getLevel() != null ?
                m.getLevel().getRandom() : RandomSource.create();
        return build(input, parallel, random);
    }

    @Nullable
    public static GTRecipe build(Input input, int parallel, RandomSource random) {
        Map<Material, Integer> drawn = new LinkedHashMap<>();
        if (input.card() != null) {
            var vein = GTRegistries.BEDROCK_ORE_DEFINITIONS.get(input.card().vein());
            if (vein == null) return null;
            draw(vein, OPERATIONS * input.card().purity().ores * parallel, random, drawn);
        } else {
            var veins = veins(input.dimension());
            if (veins.isEmpty()) return null;
            // EN: Each operation picks a vein by its weight, then one ore of it: random mode is always ×1.
            // PT: Cada operação sorteia um veio pelo peso e depois um minério dele: o modo aleatório é sempre ×1.
            // ZH: 每次运行按权重抽取一条矿脉，再从中抽取一个矿石：随机模式始终为 ×1。
            int total = veins.stream().mapToInt(BedrockOreDefinition::weight).sum();
            for (int i = 0; i < OPERATIONS * parallel; i++) {
                int roll = random.nextInt(total);
                for (var v : veins) {
                    roll -= v.weight();
                    if (roll < 0) {
                        draw(v, 1, random, drawn);
                        break;
                    }
                }
            }
        }
        var builder = GTIARecipeTypes.FRAGMENT_COLLECTOR
                .recipeBuilder("fragment_" + Long.toHexString(random.nextLong()))
                .duration(DURATION).EUt(parallel > 1 ? GTValues.VA[GTValues.EV] : GTValues.VA[GTValues.LV]);
        for (var e : drawn.entrySet()) builder.outputItems(EntangledMinerLogic.raw(e.getKey(), e.getValue()));
        builder.chancedOutput(input.fragment().copyWithCount(1), DUPLICATE_CHANCE, 0);
        return builder.buildRawRecipe();
    }

    private static void draw(BedrockOreDefinition vein, int count, RandomSource random, Map<Material, Integer> into) {
        int total = vein.materials().stream().mapToInt(WeightedMaterial::weight).sum();
        for (int i = 0; i < count; i++) {
            int roll = random.nextInt(total);
            for (var m : vein.materials()) {
                roll -= m.weight();
                if (roll < 0) {
                    into.merge(m.material(), 1, Integer::sum);
                    break;
                }
            }
        }
    }
}
