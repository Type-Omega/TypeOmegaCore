package com.typeomega.core.integration.ae.wireless;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.me.helpers.IGridConnectedBlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Which blocks a wireless network can take, and how to reach their AE2 node: any GregTech machine on the grid (the ME
 * input, output, stocking and pattern parts, and the requestable ones) and our connector. Both expose their node
 * through AE2's own interface, so no GregTech class is changed.
 */
final class WirelessNodes {

    private WirelessNodes() {}

    /** The managed node of the block at {@code pos}, or null when it has none (not loaded, not a grid block). */
    static @Nullable IManagedGridNode managed(ServerLevel level, BlockPos pos) {
        if (!level.isLoaded(pos)) return null;
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof IMachineBlockEntity holder) {
            MetaMachine machine = holder.getMetaMachine();
            return machine instanceof IGridConnectedBlockEntity grid ? grid.getMainNode() : null;
        }
        return entity instanceof IGridConnectedBlockEntity grid ? grid.getMainNode() : null;
    }

    static boolean bindable(ServerLevel level, BlockPos pos) {
        return managed(level, pos) != null;
    }

    /** The AE2 node, once the block has created it (a tick or two after it loads). */
    static @Nullable IGridNode node(ServerLevel level, BlockPos pos) {
        IManagedGridNode managed = managed(level, pos);
        return managed == null ? null : managed.getNode();
    }

    static String blockId(ServerLevel level, BlockPos pos) {
        return String.valueOf(ForgeRegistries.BLOCKS.getKey(level.getBlockState(pos).getBlock()));
    }
}
