package com.typeomega.core.gametest.ae;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.helpers.InterfaceLogicHost;
import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.integration.ae.interfaces.InterfaceTier;
import com.typeomega.core.integration.ae.interfaces.InterfaceTiers;
import com.typeomega.core.integration.ae.interfaces.TieredInterfaceEntity;
import com.typeomega.core.integration.ae.interfaces.TieredInterfacePart;

/**
 * Sneak right-click with a higher ME Interface replaces a lower one of the same form where it stands, keeping config,
 * storage and cards; and the cases where nothing happens.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class TieredInterfaceUpgradeGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";

    private TieredInterfaceUpgradeGameTests() {}

    // EN: Amounts the plain AE2 interface can hold too (64 items, 4000 mB), so every upgrade starts from valid
    // contents.
    // PT: Quantidades que a interface comum do AE2 também aguenta (64 itens, 4000 mB), para todo upgrade partir de
    // conteúdo válido.
    // ZH: 普通 AE2 接口也能容纳的数量（64 个物品、4000 mB），使每次升级都从合法内容开始。
    private static void fill(InterfaceLogicHost host) {
        var logic = host.getInterfaceLogic();
        logic.getConfig().setStack(1, new GenericStack(AEItemKey.of(Items.COBBLESTONE), 50));
        logic.getConfig().setStack(5, new GenericStack(AEFluidKey.of(Fluids.WATER), 3000));
        logic.getStorage().setStack(1, new GenericStack(AEItemKey.of(Items.COBBLESTONE), 40));
        logic.getStorage().setStack(2, new GenericStack(AEFluidKey.of(Fluids.LAVA), 2500));
        logic.setPriority(7);
        logic.getUpgrades().addItems(AEItems.FUZZY_CARD.stack());
    }

    private static void assertKept(GameTestHelper h, InterfaceLogicHost host, String what) {
        var logic = host.getInterfaceLogic();
        h.assertTrue(new GenericStack(AEItemKey.of(Items.COBBLESTONE), 50).equals(logic.getConfig().getStack(1)),
                what + " lost its item config");
        h.assertTrue(new GenericStack(AEFluidKey.of(Fluids.WATER), 3000).equals(logic.getConfig().getStack(5)),
                what + " lost its fluid config");
        h.assertTrue(new GenericStack(AEItemKey.of(Items.COBBLESTONE), 40).equals(logic.getStorage().getStack(1)),
                what + " lost its stored items");
        h.assertTrue(new GenericStack(AEFluidKey.of(Fluids.LAVA), 2500).equals(logic.getStorage().getStack(2)),
                what + " lost its stored fluid");
        h.assertTrue(logic.getPriority() == 7, what + " lost its priority");
        h.assertTrue(logic.getUpgrades().isInstalled(AEItems.FUZZY_CARD), what + " lost its Fuzzy Card");
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 60)
    public static void blockUpgradeKeepsConfigAndContents(GameTestHelper h) {
        var player = AeTestRig.player(h);
        AeTestRig.place(h, InterfaceTiers.blockItem(InterfaceTier.II));
        fill((InterfaceLogicHost) h.getBlockEntity(AeTestRig.POS));
        for (InterfaceTier next : new InterfaceTier[] { InterfaceTier.III, InterfaceTier.V }) {
            var held = new ItemStack(InterfaceTiers.blockItem(next), 2);
            var before = ((TieredInterfaceEntity) h.getBlockEntity(AeTestRig.POS)).tier();
            var result = AeTestRig.use(h, player, held, true);
            h.assertTrue(result.consumesAction(), "the upgrade to " + next + " was not taken: " + result);
            var entity = (TieredInterfaceEntity) h.getBlockEntity(AeTestRig.POS);
            h.assertTrue(entity.tier() == next, "the block is " + entity.tier() + " after the upgrade to " + next);
            assertKept(h, entity, next.toString());
            h.assertTrue(held.getCount() == 1, "the held item is not used up one by one");
            h.assertTrue(AeTestRig.count(player, InterfaceTiers.blockItem(before).asItem()) == 1,
                    "the replaced " + before + " did not come back to the player");
            h.assertTrue(
                    entity.getInterfaceLogic().getStorage().getMaxAmount(AEItemKey.of(Items.COBBLESTONE)) ==
                            64 * next.multiplier,
                    "the new block does not have the capacity of " + next);
            player.getInventory().clearContent();
        }
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 60)
    public static void plainInterfaceUpgradesToTierTwo(GameTestHelper h) {
        var player = AeTestRig.player(h);
        AeTestRig.place(h, AEBlocks.INTERFACE);
        var plain = (InterfaceLogicHost) h.getBlockEntity(AeTestRig.POS);
        fill(plain);
        var result = AeTestRig.use(h, player, new ItemStack(InterfaceTiers.blockItem(InterfaceTier.II)), true);
        h.assertTrue(result.consumesAction(), "the plain AE2 interface was not upgraded: " + result);
        var entity = (TieredInterfaceEntity) h.getBlockEntity(AeTestRig.POS);
        h.assertTrue(entity.tier() == InterfaceTier.II, "not tier II after the upgrade");
        assertKept(h, entity, "the upgraded plain interface");
        h.assertTrue(AeTestRig.count(player, AEBlocks.INTERFACE.asItem()) == 1,
                "the plain interface did not come back");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 60)
    public static void partUpgradeKeepsConfigAndContents(GameTestHelper h) {
        var player = AeTestRig.player(h);
        var host = AeTestRig.bus(h);
        AeTestRig.addPart(h, host, AEParts.INTERFACE.asItem());
        fill((InterfaceLogicHost) host.getPart(net.minecraft.core.Direction.UP));
        for (InterfaceTier next : new InterfaceTier[] { InterfaceTier.II, InterfaceTier.IV }) {
            var held = new ItemStack(InterfaceTiers.part(next));
            var result = AeTestRig.use(h, player, held, true);
            h.assertTrue(result.consumesAction(), "the part upgrade to " + next + " was not taken: " + result);
            var part = host.getPart(net.minecraft.core.Direction.UP);
            h.assertTrue(part instanceof TieredInterfacePart tiered && tiered.tier() == next,
                    "the part is not " + next);
            assertKept(h, (InterfaceLogicHost) part, "the " + next + " part");
            h.assertTrue(held.isEmpty(), "the part item was not used up");
            h.assertTrue(
                    ((InterfaceLogicHost) part).getInterfaceLogic().getStorage()
                            .getMaxAmount(AEItemKey.of(Items.COBBLESTONE)) == 64 * next.multiplier,
                    "the new part does not have the capacity of " + next);
            player.getInventory().clearContent();
        }
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 60)
    public static void lowerOrEqualTierCannotReplace(GameTestHelper h) {
        var player = AeTestRig.player(h);
        AeTestRig.place(h, InterfaceTiers.blockItem(InterfaceTier.III));
        fill((InterfaceLogicHost) h.getBlockEntity(AeTestRig.POS));
        for (InterfaceTier other : new InterfaceTier[] { InterfaceTier.II, InterfaceTier.III }) {
            var held = new ItemStack(InterfaceTiers.blockItem(other));
            AeTestRig.use(h, player, held, true);
            var entity = (TieredInterfaceEntity) h.getBlockEntity(AeTestRig.POS);
            h.assertTrue(entity.tier() == InterfaceTier.III, other + " replaced a tier III block");
            assertKept(h, entity, "the tier III block");
            // The player holds the tried item itself, so a tier III block in the inventory can only be that one.
            h.assertTrue(
                    held.getCount() == 1 &&
                            AeTestRig.count(player, InterfaceTiers.blockItem(InterfaceTier.III).asItem()) ==
                                    (other == InterfaceTier.III ? 1 : 0),
                    other + " was spent without an upgrade");
        }
        var host = AeTestRig.bus(h);
        AeTestRig.addPart(h, host, InterfaceTiers.part(InterfaceTier.IV));
        var held = new ItemStack(InterfaceTiers.part(InterfaceTier.III));
        AeTestRig.use(h, player, held, true);
        h.assertTrue(((TieredInterfacePart) host.getPart(net.minecraft.core.Direction.UP)).tier() == InterfaceTier.IV,
                "a tier III part replaced a tier IV part");
        h.assertTrue(held.getCount() == 1, "the part item was spent without an upgrade");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 60)
    public static void upgradeNeedsSneakingAndTheSameForm(GameTestHelper h) {
        var player = AeTestRig.player(h);
        AeTestRig.place(h, InterfaceTiers.blockItem(InterfaceTier.II));
        fill((InterfaceLogicHost) h.getBlockEntity(AeTestRig.POS));
        AeTestRig.use(h, player, new ItemStack(InterfaceTiers.blockItem(InterfaceTier.V)), false);
        h.assertTrue(((TieredInterfaceEntity) h.getBlockEntity(AeTestRig.POS)).tier() == InterfaceTier.II,
                "upgraded without sneaking");
        // A part does not turn a block into a part, nor a block a part into a block.
        var partItem = new ItemStack(InterfaceTiers.part(InterfaceTier.V));
        var result = AeTestRig.use(h, player, partItem, true);
        h.assertTrue(!result.consumesAction() && partItem.getCount() == 1, "a part item acted on an interface block");
        h.assertTrue(((TieredInterfaceEntity) h.getBlockEntity(AeTestRig.POS)).tier() == InterfaceTier.II,
                "a part item replaced a block");

        var host = AeTestRig.bus(h);
        AeTestRig.addPart(h, host, InterfaceTiers.part(InterfaceTier.II));
        var blockItem = new ItemStack(InterfaceTiers.blockItem(InterfaceTier.V));
        InteractionResult onPart = AeTestRig.use(h, player, blockItem, true);
        h.assertTrue(!onPart.consumesAction() || blockItem.getCount() == 1, "a block item acted on an interface part");
        h.assertTrue(((TieredInterfacePart) host.getPart(net.minecraft.core.Direction.UP)).tier() == InterfaceTier.II,
                "a block item replaced a part");
        h.succeed();
    }
}
