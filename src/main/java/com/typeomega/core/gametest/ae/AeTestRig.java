package com.typeomega.core.gametest.ae;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.parts.IPartItem;
import appeng.core.definitions.AEBlocks;
import com.typeomega.core.gametest.TestPlayers;

/** What the AE2 GameTests share: one block position, a cable bus to hold parts, and a sneak right-click. */
final class AeTestRig {

    static final BlockPos POS = new BlockPos(2, 2, 2);

    private AeTestRig() {}

    // Cleared first: setting the block it already is would keep the old block entity and its contents.
    static void place(GameTestHelper h, ItemLike block) {
        h.setBlock(POS, net.minecraft.world.level.block.Blocks.AIR);
        h.setBlock(POS, ((net.minecraft.world.item.BlockItem) block.asItem()).getBlock());
    }

    /** A cable bus at the test position, ready for parts. */
    static IPartHost bus(GameTestHelper h) {
        h.setBlock(POS, net.minecraft.world.level.block.Blocks.AIR);
        h.setBlock(POS, AEBlocks.CABLE_BUS.block());
        return (IPartHost) h.getBlockEntity(POS);
    }

    static <T extends IPart> T addPart(GameTestHelper h, IPartHost host, IPartItem<T> item) {
        T part = host.addPart(item, Direction.UP, null);
        h.assertTrue(part != null, "the cable bus refused the part " + item);
        return part;
    }

    static ServerPlayer player(GameTestHelper h) {
        ServerPlayer player = TestPlayers.survival(h);
        BlockPos pos = h.absolutePos(POS);
        player.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
        player.getInventory().clearContent();
        return player;
    }

    /**
     * Right-click the test position on its top face, the way a player holds the item; the item's own use runs first.
     */
    static InteractionResult use(GameTestHelper h, ServerPlayer player, ItemStack stack, boolean sneaking) {
        player.setShiftKeyDown(sneaking);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockPos abs = h.absolutePos(POS);
        Vec3 click = new Vec3(abs.getX() + 0.5, abs.getY() + 0.95, abs.getZ() + 0.5);
        var context = new UseOnContext(h.getLevel(), player, InteractionHand.MAIN_HAND, stack,
                new BlockHitResult(click, Direction.UP, abs, false));
        return stack.getItem().onItemUseFirst(stack, context);
    }

    static int count(ServerPlayer player, net.minecraft.world.item.Item item) {
        int n = 0;
        for (ItemStack stack : player.getInventory().items) if (stack.is(item)) n += stack.getCount();
        return n;
    }
}
