package com.typeomega.core.gametest.ae;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.helpers.InterfaceLogicHost;
import com.typeomega.core.GTIAMod;
import com.typeomega.core.integration.ae.interfaces.InterfaceRecipes;
import com.typeomega.core.integration.ae.interfaces.InterfaceTier;
import com.typeomega.core.integration.ae.interfaces.InterfaceTiers;
import com.typeomega.core.integration.ae.interfaces.TieredInterfaceEntity;

/**
 * ME Interface (II) to (V) (GTIA-D-442): 36 config and 36 storage slots, each tier's capacity in block and part form,
 * the Fuzzy and Crafting cards, and the recipes.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(GTIAMod.MOD_ID)
public final class TieredInterfaceGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";
    private static final AEKey STONE = AEItemKey.of(Items.COBBLESTONE);
    private static final AEKey PICKAXE = AEItemKey.of(Items.DIAMOND_PICKAXE);
    private static final AEKey WATER = AEFluidKey.of(Fluids.WATER);

    private TieredInterfaceGameTests() {}

    private static void checkCapacity(GameTestHelper h, InterfaceTier tier, InterfaceLogicHost host, String form) {
        String what = tier + " " + form;
        var logic = host.getInterfaceLogic();
        h.assertTrue(logic.getConfig().size() == 36 && logic.getStorage().size() == 36,
                what + " does not have 36+36 slots");
        long m = tier.multiplier;
        long intMax = Integer.MAX_VALUE;
        h.assertTrue(logic.getStorage().getMaxAmount(STONE) == 64 * m,
                what + " storage items: " + logic.getStorage().getMaxAmount(STONE));
        h.assertTrue(logic.getStorage().getMaxAmount(WATER) == 4000 * m,
                what + " storage fluid: " + logic.getStorage().getMaxAmount(WATER));
        h.assertTrue(logic.getStorage().getMaxAmount(PICKAXE) == 64 * m,
                what + " storage holds unstackables like a stack of 64");
        h.assertTrue(logic.getConfig().getMaxAmount(STONE) == Math.min(64 * m, intMax), what + " config items");
        h.assertTrue(logic.getConfig().getMaxAmount(PICKAXE) == Math.min(m, intMax),
                what + " config of a unstackable item");
        h.assertTrue(logic.getConfig().getMaxAmount(WATER) == Math.min(4000 * m, intMax), what + " config fluid");
        long inserted = logic.getStorage().insert(0, STONE, Long.MAX_VALUE, Actionable.MODULATE);
        h.assertTrue(inserted == 64 * m && logic.getStorage().getAmount(0) == 64 * m,
                what + " took " + inserted + " items");
        long water = logic.getStorage().insert(1, WATER, Long.MAX_VALUE, Actionable.MODULATE);
        h.assertTrue(water == 4000 * m, what + " took " + water + " mB");
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 40)
    public static void blockCapacityPerTier(GameTestHelper h) {
        for (InterfaceTier tier : InterfaceTier.values()) {
            AeTestRig.place(h, InterfaceTiers.blockItem(tier));
            var entity = (TieredInterfaceEntity) h.getBlockEntity(AeTestRig.POS);
            h.assertTrue(entity.tier() == tier, "the block entity is of tier " + entity.tier() + ", not " + tier);
            checkCapacity(h, tier, entity, "block");
        }
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 40)
    public static void partCapacityPerTier(GameTestHelper h) {
        for (InterfaceTier tier : InterfaceTier.values()) {
            var host = AeTestRig.bus(h);
            var part = AeTestRig.addPart(h, host, InterfaceTiers.part(tier));
            checkCapacity(h, tier, part, "part");
        }
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 40)
    public static void plainInterfaceStaysAsAe2Made(GameTestHelper h) {
        AeTestRig.place(h, AEBlocks.INTERFACE);
        var logic = ((InterfaceLogicHost) h.getBlockEntity(AeTestRig.POS)).getInterfaceLogic();
        h.assertTrue(logic.getConfig().size() == 9, "the plain AE2 interface has 9 slots");
        h.assertTrue(logic.getStorage().getMaxAmount(STONE) == 64, "the plain AE2 interface keeps 64 per slot");
        h.assertTrue(logic.getConfig().getMaxAmount(PICKAXE) == 1, "and the stack limit of its config");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 40)
    public static void takeFuzzyAndCraftingCards(GameTestHelper h) {
        for (InterfaceTier tier : InterfaceTier.values()) {
            for (var machine : new net.minecraft.world.level.ItemLike[] { InterfaceTiers.blockItem(tier),
                    InterfaceTiers.part(tier) }) {
                h.assertTrue(Upgrades.getMaxInstallable(AEItems.FUZZY_CARD, machine) == 1,
                        tier + " takes no Fuzzy Card");
                h.assertTrue(Upgrades.getMaxInstallable(AEItems.CRAFTING_CARD, machine) == 1,
                        tier + " takes no Crafting Card");
                h.assertTrue(Upgrades.getMaxInstallable(AEItems.SPEED_CARD, machine) == 0,
                        tier + " takes a speed card");
            }
            AeTestRig.place(h, InterfaceTiers.blockItem(tier));
            var entity = (TieredInterfaceEntity) h.getBlockEntity(AeTestRig.POS);
            h.assertTrue(entity.getUpgrades().addItems(AEItems.FUZZY_CARD.stack()).isEmpty(),
                    tier + " refused a Fuzzy Card");
            h.assertTrue(entity.getUpgrades().isInstalled(AEItems.FUZZY_CARD), tier + " did not keep the Fuzzy Card");
        }
        var host = AeTestRig.bus(h);
        var part = AeTestRig.addPart(h, host, InterfaceTiers.part(InterfaceTier.III));
        h.assertTrue(part.getUpgrades().addItems(AEItems.CRAFTING_CARD.stack()).isEmpty(),
                "the part refused a Crafting Card");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 40)
    public static void recipesTakeCircuitsAndConvertForms(GameTestHelper h) {
        var manager = h.getLevel().getServer().getRecipeManager();
        for (InterfaceTier tier : InterfaceTier.values()) {
            var block = manager.byKey(GTIAMod.id("shaped/" + tier.blockId()));
            h.assertTrue(block.isPresent(), "no recipe for " + tier.blockId());
            var shaped = block.get();
            h.assertTrue(shaped.getResultItem(h.getLevel().registryAccess()).is(InterfaceTiers.block(tier).asItem()),
                    "wrong output of " + tier);
            var ingredients = shaped.getIngredients();
            h.assertTrue(ingredients.stream().filter(i -> !i.isEmpty()).count() == 5,
                    tier + " is not four interfaces and a circuit");
            h.assertTrue(ingredients.stream().anyMatch(i -> i.test(new ItemStack(
                    net.minecraftforge.registries.ForgeRegistries.ITEMS.tags().getTag(tier.circuit).stream().findFirst()
                            .orElseThrow()))),
                    tier + " does not take its circuit " + tier.circuit.location());
            var toPart = manager.byKey(GTIAMod.id("shapeless/" + tier.partId()));
            var toBlock = manager.byKey(GTIAMod.id("shapeless/" + tier.blockId() + "_from_part"));
            h.assertTrue(toPart.isPresent() && toBlock.isPresent(), tier + " has no block/part conversion");
            h.assertTrue(toPart.get().getResultItem(h.getLevel().registryAccess()).is(InterfaceTiers.part(tier)),
                    "the part conversion of " + tier);
            h.assertTrue(
                    toBlock.get().getResultItem(h.getLevel().registryAccess()).is(InterfaceTiers.block(tier).asItem()),
                    "the block conversion of " + tier);
        }
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 40)
    public static void lowerInterfacesFeedTheNextTier(GameTestHelper h) {
        var tags = net.minecraftforge.registries.ForgeRegistries.ITEMS.tags();
        for (InterfaceTier tier : InterfaceTier.values()) {
            var below = tags.getTag(InterfaceRecipes.below(tier));
            h.assertTrue(below.contains(AEParts.INTERFACE.asItem()) && below.contains(AEBlocks.INTERFACE.asItem()),
                    tier + " does not take the AE2 interface");
            for (InterfaceTier lower : InterfaceTier.values()) {
                boolean expected = lower.level < tier.level;
                h.assertTrue(
                        below.contains(InterfaceTiers.block(lower).asItem()) == expected &&
                                below.contains(InterfaceTiers.part(lower)) == expected,
                        tier + (expected ? " refuses " : " accepts ") + lower);
            }
        }
        h.succeed();
    }
}
