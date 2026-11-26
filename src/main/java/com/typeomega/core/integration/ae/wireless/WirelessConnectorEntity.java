package com.typeomega.core.integration.ae.wireless;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IManagedGridNode;
import appeng.api.util.AECableType;
import appeng.blockentity.grid.AENetworkBlockEntity;

/**
 * The ME Wireless Connector: an AE2 node on the cabled network that asks for no channel and is dense, so its link to
 * the network's hub can carry as many as a dense cable. The binding itself is kept in {@link WirelessData}.
 */
public final class WirelessConnectorEntity extends AENetworkBlockEntity {

    public WirelessConnectorEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // EN: Called from the AE2 constructor, before this class has any field: only the node's flags are touched.
    // PT: Chamado pelo construtor do AE2, antes de esta classe ter qualquer campo: só as flags do nó são mexidas.
    // ZH: 由 AE2 构造函数调用，此时本类尚无字段：只设置节点标志。
    @Override
    protected IManagedGridNode createMainNode() {
        return super.createMainNode().setFlags(GridFlags.DENSE_CAPACITY);
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.DENSE_SMART;
    }
}
