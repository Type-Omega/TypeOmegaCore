package com.typeomega.core.integration.ae.interfaces;

import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.typeomega.core.TypeOmegaMod;

import java.util.function.Consumer;

/**
 * Crafting of ME Interface (II) to (V): four interfaces of any lower tier around a circuit (MV, EV, LuV, UV), and the
 * shapeless conversion between the block and the cable part. The lower-tier ingredient is a tag per tier, listed in
 * data/typeomega/tags/items/me_interfaces.
 */
public final class InterfaceRecipes {

    private InterfaceRecipes() {}

    /** Any interface below the given tier: the AE2 interface (and ExtendedAE's) and every lower tier of ours. */
    public static TagKey<Item> below(InterfaceTier tier) {
        return TagKey.create(Registries.ITEM, TypeOmegaMod.id("me_interfaces/below_" + tier.suffix()));
    }

    public static void addRecipes(Consumer<FinishedRecipe> provider) {
        for (InterfaceTier tier : InterfaceTier.values()) {
            ItemStack block = new ItemStack(InterfaceTiers.block(tier));
            ItemStack part = new ItemStack(InterfaceTiers.part(tier));
            VanillaRecipeHelper.addShapedRecipe(provider, true, TypeOmegaMod.id(tier.blockId()), block,
                    " A ", "ACA", " A ",
                    'A', below(tier), 'C', tier.circuit);
            VanillaRecipeHelper.addShapelessRecipe(provider, TypeOmegaMod.id(tier.partId()), part, block);
            VanillaRecipeHelper.addShapelessRecipe(provider, TypeOmegaMod.id(tier.blockId() + "_from_part"), block, part);
        }
    }
}
