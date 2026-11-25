package com.typeomega.core.integration.ae.interfaces;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import appeng.api.parts.IPartHost;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.util.InteractionUtil;
import org.jetbrains.annotations.Nullable;

/**
 * Sneak right-click an interface of a lower tier with a higher one of the same form (block on block, part on part) and
 * it is replaced where it stands, keeping config, storage, cards, priority and name; the old one goes to the player.
 */
final class InterfaceUpgrade {

    private InterfaceUpgrade() {}

    /** The result when the click is an upgrade, null when the item should behave as usual. */
    @Nullable
    static InteractionResult block(ItemStack stack, UseOnContext context, TieredInterfaceBlock block) {
        Player player = context.getPlayer();
        if (player == null || !InteractionUtil.isInAlternateUseMode(player)) return null;
        var level = context.getLevel();
        var pos = context.getClickedPos();
        BlockEntity target = level.getBlockEntity(pos);
        if (target == null || target instanceof IPartHost || !lower(InterfaceLogicTier.levelOf(target), block.tier()))
            return null;
        if (!level.isClientSide()) {
            CompoundTag data = target.saveWithoutMetadata();
            ItemStack old = new ItemStack(target.getBlockState().getBlock());
            // EN: Emptied first, so the removal of the old block does not drop what the new one is about to load.
            // PT: Esvaziado antes, para a remoção do bloco antigo não largar o que o novo vai carregar.
            // ZH: 先清空，避免移除旧方块时掉落新方块即将载入的内容。
            if (target instanceof AEBaseBlockEntity ae) ae.clearContent();
            level.setBlock(pos, block.defaultBlockState(), Block.UPDATE_ALL);
            if (level.getBlockEntity(pos) instanceof TieredInterfaceEntity replaced) {
                replaced.load(data);
                replaced.setChanged();
                replaced.markForUpdate();
            }
            swap(player, stack, old);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Nullable
    static InteractionResult part(ItemStack stack, UseOnContext context, TieredInterfacePartItem item) {
        Player player = context.getPlayer();
        if (player == null || !InteractionUtil.isInAlternateUseMode(player)) return null;
        var level = context.getLevel();
        if (!(level.getBlockEntity(context.getClickedPos()) instanceof IPartHost host)) return null;
        var selected = host.selectPartWorld(context.getClickLocation());
        if (selected.part == null || selected.side == null ||
                !lower(InterfaceLogicTier.levelOf(selected.part), item.tier()))
            return null;
        if (!level.isClientSide()) {
            CompoundTag data = new CompoundTag();
            selected.part.writeToNBT(data);
            ItemStack old = new ItemStack(selected.part.getPartItem());
            var replaced = host.replacePart(item, selected.side, player, context.getHand());
            if (replaced != null) {
                replaced.readFromNBT(data);
                host.markForSave();
                host.markForUpdate();
                swap(player, stack, old);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private static boolean lower(int currentLevel, InterfaceTier tier) {
        return currentLevel >= 0 && currentLevel < tier.level;
    }

    private static void swap(Player player, ItemStack held, ItemStack old) {
        if (!player.getAbilities().instabuild) held.shrink(1);
        player.getInventory().placeItemBackInInventory(old);
    }
}
