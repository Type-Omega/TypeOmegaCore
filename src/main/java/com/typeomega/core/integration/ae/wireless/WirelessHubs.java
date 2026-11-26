package com.typeomega.core.integration.ae.wireless;

import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;

import appeng.api.networking.GridFlags;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.IManagedGridNode;
import appeng.api.stacks.AEItemKey;
import com.typeomega.core.TypeOmegaMod;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * One virtual AE2 node per wireless network, in no block: the connector and every bound part link to it, which puts
 * them all in one grid. The node is dense so a connector's link carries up to 32 channels like a dense cable, and it
 * asks for none itself; each bound part takes a channel like any cabled one. Hubs live only while the server runs and
 * are rebuilt on first use, because the bindings are what is saved.
 */
public final class WirelessHubs {

    private static final Map<String, IManagedGridNode> HUBS = new HashMap<>();

    private enum Listener implements IGridNodeListener<String> {

        INSTANCE;

        @Override
        public void onSaveChanges(String network, IGridNode node) {}
    }

    private WirelessHubs() {}

    public static IGridNode node(MinecraftServer server, String network) {
        IManagedGridNode hub = HUBS.get(network);
        if (hub == null || hub.getNode() == null) {
            hub = GridHelper.createManagedNode(network, Listener.INSTANCE)
                    .setInWorldNode(false)
                    .setFlags(GridFlags.DENSE_CAPACITY)
                    .setIdlePowerUsage(0)
                    .setExposedOnSides(EnumSet.noneOf(Direction.class))
                    .setVisualRepresentation(AEItemKey.of(AeWireless.CONNECTOR.get().asItem()));
            hub.create(server.overworld(), null);
            HUBS.put(network, hub);
        }
        return hub.getNode();
    }

    public static boolean exists(String network) {
        IManagedGridNode hub = HUBS.get(network);
        return hub != null && hub.getNode() != null;
    }

    /** Drops every hub: the members' links go with them, and the sweep builds them again. */
    public static void clear() {
        for (IManagedGridNode hub : HUBS.values()) {
            try {
                hub.destroy();
            } catch (RuntimeException e) {
                TypeOmegaMod.LOGGER.debug("Wireless hub already gone", e);
            }
        }
        HUBS.clear();
    }
}
