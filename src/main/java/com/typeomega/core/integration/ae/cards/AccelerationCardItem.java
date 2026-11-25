package com.typeomega.core.integration.ae.cards;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import appeng.api.parts.IPartHost;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.core.definitions.AEItems;
import appeng.core.localization.PlayerMessages;
import appeng.items.materials.UpgradeCardItem;
import appeng.util.InteractionUtil;
import com.typeomega.core.integration.ae.AeLang;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Acceleration Card (II) to (V). */
public final class AccelerationCardItem extends UpgradeCardItem {

    private final AccelerationTier tier;

    AccelerationCardItem(Properties properties, AccelerationTier tier) {
        super(properties);
        this.tier = tier;
    }

    public AccelerationTier tier() {
        return tier;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable(AeLang.CARD_DESC, tier.multiplier).withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable(AeLang.CARD_LIMIT).withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable(AeLang.CARD_SCOPE).withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable(AeLang.CARD_REPLACE).withStyle(ChatFormatting.DARK_GRAY));
        super.appendHoverText(stack, level, lines, flag);
    }

    /**
     * Sneak right-click on a machine whose card limit is reached swaps its lowest-tier card for this one; with nothing
     * lower to swap it says the limit is reached. Below the limit the AE2 install runs as usual.
     */
    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !InteractionUtil.isInAlternateUseMode(player))
            return super.onItemUseFirst(stack, context);
        IUpgradeInventory upgrades = upgradesAt(context);
        if (upgrades == null || upgrades.getMaxInstalled(this) <= 0 || !atLimit(upgrades)) {
            return super.onItemUseFirst(stack, context);
        }
        int slot = lowestBelow(upgrades);
        if (slot < 0) {
            player.displayClientMessage(PlayerMessages.MaxUpgradesOfTypeInstalled.text(), true);
            return InteractionResult.FAIL;
        }
        var level = context.getLevel();
        if (!level.isClientSide()) {
            ItemStack old = upgrades.getStackInSlot(slot).copy();
            upgrades.setItemDirect(slot, stack.copyWithCount(1));
            if (!player.getAbilities().instabuild) stack.shrink(1);
            player.getInventory().placeItemBackInInventory(old);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Nullable
    private static IUpgradeInventory upgradesAt(UseOnContext context) {
        var entity = context.getLevel().getBlockEntity(context.getClickedPos());
        if (entity instanceof IPartHost host) {
            return host.selectPartWorld(context.getClickLocation()).part instanceof IUpgradeableObject part ?
                    part.getUpgrades() : null;
        }
        return entity instanceof IUpgradeableObject object ? object.getUpgrades() : null;
    }

    private static boolean atLimit(IUpgradeInventory upgrades) {
        int installed = 0;
        boolean full = true;
        for (int i = 0; i < upgrades.size(); i++) {
            ItemStack stack = upgrades.getStackInSlot(i);
            if (stack.isEmpty()) full = false;
            else if (levelOf(stack) >= 0) installed += stack.getCount();
        }
        return full || installed >= upgrades.getMaxInstalled(AEItems.SPEED_CARD);
    }

    /** The slot of the lowest card that is below this tier, or -1. */
    private int lowestBelow(IUpgradeInventory upgrades) {
        int slot = -1;
        int lowest = tier.level;
        for (int i = 0; i < upgrades.size(); i++) {
            int level = levelOf(upgrades.getStackInSlot(i));
            if (level >= 0 && level < lowest) {
                lowest = level;
                slot = i;
            }
        }
        return slot;
    }

    private static int levelOf(ItemStack stack) {
        if (stack.is(AEItems.SPEED_CARD.asItem())) return 0;
        return stack.getItem() instanceof AccelerationCardItem card ? card.tier.level : -1;
    }
}
