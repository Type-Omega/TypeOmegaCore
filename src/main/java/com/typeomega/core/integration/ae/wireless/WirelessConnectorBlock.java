package com.typeomega.core.integration.ae.wireless;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseEntityBlock;
import org.jetbrains.annotations.Nullable;

/** The block of the ME Wireless Connector; right-clicking it with an empty hand tells which network it is bound to. */
public final class WirelessConnectorBlock extends AEBaseEntityBlock<WirelessConnectorEntity> {

    WirelessConnectorBlock() {
        super(AEBaseBlock.metalProps());
    }

    @Override
    public InteractionResult onActivated(Level level, BlockPos pos, Player player, InteractionHand hand,
                                         @Nullable ItemStack held, BlockHitResult hit) {
        if (!(level instanceof ServerLevel server)) return InteractionResult.sidedSuccess(true);
        WirelessData data = WirelessData.get(server.getServer());
        var binding = data.at(new WirelessData.Site(server.dimension().location(), pos.asLong()));
        WirelessNetwork network = binding == null ? null : data.network(binding.network());
        player.displayClientMessage(network == null ? Component.translatable(WirelessLang.STATUS_NONE) :
                Component.translatable(WirelessLang.STATUS, network.name()), true);
        return InteractionResult.sidedSuccess(false);
    }
}
