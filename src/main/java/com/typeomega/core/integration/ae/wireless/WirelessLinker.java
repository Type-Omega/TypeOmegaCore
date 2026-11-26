package com.typeomega.core.integration.ae.wireless;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;

import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridNode;
import com.typeomega.core.integration.ae.wireless.WirelessData.Binding;
import com.typeomega.core.integration.ae.wireless.WirelessData.Site;

/**
 * Keeps every bound block linked to its network's hub. A link is an AE2 connection between the block's node and the
 * hub's, so it dies with the node when the chunk unloads; this sweep, once a second, links again every bound block
 * whose chunk is loaded and whose node exists, and forgets the bindings of blocks that were replaced or networks that
 * were removed. Nothing else has to be hooked into the machines.
 */
public final class WirelessLinker {

    static final int PERIOD_TICKS = 20;

    private WirelessLinker() {}

    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.getServer().getTickCount() % PERIOD_TICKS != 0) return;
        sweep(event.getServer());
    }

    public static void onServerStarting(ServerStartingEvent event) {
        WirelessHubs.clear();
    }

    public static void onServerStopped(ServerStoppedEvent event) {
        WirelessHubs.clear();
    }

    public static void sweep(MinecraftServer server) {
        WirelessData data = WirelessData.get(server);
        for (var entry : data.bindings().entrySet()) {
            Site site = entry.getKey();
            Binding binding = entry.getValue();
            ServerLevel level = server.getLevel(ResourceKey.create(Registries.DIMENSION, site.dimension()));
            BlockPos pos = site.blockPos();
            if (level == null || !level.isLoaded(pos)) continue;
            if (data.network(binding.network()) == null || !binding.block().equals(WirelessNodes.blockId(level, pos))) {
                data.unbind(site);
                continue;
            }
            IGridNode node = WirelessNodes.node(level, pos);
            if (node != null) link(server, node, binding.network());
        }
    }

    /** Connects the node to the hub of the network unless it already is. */
    public static void link(MinecraftServer server, IGridNode node, String network) {
        IGridNode hub = WirelessHubs.node(server, network);
        if (connection(node, hub) == null) GridHelper.createConnection(node, hub);
    }

    /** Cuts the node's link to the hub of the network, if there is one. */
    public static void unlink(MinecraftServer server, IGridNode node, String network) {
        if (!WirelessHubs.exists(network)) return;
        IGridConnection connection = connection(node, WirelessHubs.node(server, network));
        if (connection != null) connection.destroy();
    }

    static IGridConnection connection(IGridNode node, IGridNode hub) {
        for (IGridConnection connection : node.getConnections()) {
            if (connection.getOtherSide(node) == hub) return connection;
        }
        return null;
    }
}
