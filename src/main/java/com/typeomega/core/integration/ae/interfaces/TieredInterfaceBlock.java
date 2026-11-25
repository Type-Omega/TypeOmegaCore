package com.typeomega.core.integration.ae.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseEntityBlock;
import appeng.menu.locator.MenuLocators;
import appeng.util.InteractionUtil;
import org.jetbrains.annotations.Nullable;

/** The block of ME Interface (II) to (V). */
public final class TieredInterfaceBlock extends AEBaseEntityBlock<TieredInterfaceEntity> {

    private final InterfaceTier tier;

    TieredInterfaceBlock(InterfaceTier tier) {
        super(AEBaseBlock.metalProps());
        this.tier = tier;
    }

    public InterfaceTier tier() {
        return tier;
    }

    @Override
    public InteractionResult onActivated(Level level, BlockPos pos, Player player, InteractionHand hand,
                                         @Nullable ItemStack held, BlockHitResult hit) {
        if (InteractionUtil.isInAlternateUseMode(player)) return InteractionResult.PASS;
        TieredInterfaceEntity entity = getBlockEntity(level, pos);
        if (entity == null) return InteractionResult.PASS;
        if (!level.isClientSide()) entity.openMenu(player, MenuLocators.forBlockEntity(entity));
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
