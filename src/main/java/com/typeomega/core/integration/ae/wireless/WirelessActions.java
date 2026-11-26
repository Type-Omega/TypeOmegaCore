package com.typeomega.core.integration.ae.wireless;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import appeng.api.networking.IGridNode;
import com.typeomega.core.integration.ae.wireless.WirelessData.Binding;
import com.typeomega.core.integration.ae.wireless.WirelessData.Site;
import org.jetbrains.annotations.Nullable;

/**
 * What the configurator does to a block: bind it to a network, unbind it, or read its network. Every action checks the
 * player's team against the network, and GregTech's ownership rules against the machine, before it changes anything.
 */
public final class WirelessActions {

    /** The outcome, with the line to show the player and, for a read, the network found. */
    public record Result(boolean ok, Component message, @Nullable WirelessNetwork network) {

        static Result of(boolean ok, String key, Object... args) {
            return new Result(ok, Component.translatable(key, args), null);
        }
    }

    private WirelessActions() {}

    private static Site site(ServerLevel level, BlockPos pos) {
        return new Site(level.dimension().location(), pos.asLong());
    }

    public static Result bind(ServerPlayer player, ServerLevel level, BlockPos pos, WirelessNetwork network) {
        if (!WirelessNodes.bindable(level, pos)) return Result.of(false, WirelessLang.NOT_BINDABLE);
        if (!WirelessTeams.canUse(player.getUUID(), network)) return Result.of(false, WirelessLang.NETWORK_DENIED);
        if (!WirelessTeams.canConfigure(player, level, pos)) return Result.of(false, WirelessLang.MACHINE_DENIED);
        if (!WirelessTeams.ownerMayJoin(level, pos, network)) return Result.of(false, WirelessLang.OWNER_OTHER_TEAM);
        var server = level.getServer();
        WirelessData data = WirelessData.get(server);
        Site site = site(level, pos);
        String block = WirelessNodes.blockId(level, pos);
        // EN: A network has one connector: two on different ME networks would merge them through the hub.
        // PT: Uma rede tem um conector: dois em redes ME diferentes as fundiriam pelo hub.
        // ZH: 一个网络只有一个连接器：位于不同 ME 网络的两个连接器会通过中枢把它们合并。
        if (block.equals(AeWireless.CONNECTOR_ID) && data.otherWithBlock(network.id(), block, site) != null) {
            return Result.of(false, WirelessLang.CONNECTOR_TAKEN, network.name());
        }
        IGridNode node = WirelessNodes.node(level, pos);
        Binding old = data.at(site);
        if (old != null && !old.network().equals(network.id()) && node != null) {
            WirelessLinker.unlink(server, node, old.network());
        }
        data.bind(site, new Binding(network.id(), block));
        if (node != null) WirelessLinker.link(server, node, network.id());
        return Result.of(true, WirelessLang.BOUND, network.name());
    }

    public static Result unbind(ServerPlayer player, ServerLevel level, BlockPos pos) {
        if (!WirelessNodes.bindable(level, pos)) return Result.of(false, WirelessLang.NOT_BINDABLE);
        if (!WirelessTeams.canConfigure(player, level, pos)) return Result.of(false, WirelessLang.MACHINE_DENIED);
        var server = level.getServer();
        WirelessData data = WirelessData.get(server);
        Site site = site(level, pos);
        Binding old = data.at(site);
        if (old == null) return Result.of(false, WirelessLang.NOT_BOUND);
        IGridNode node = WirelessNodes.node(level, pos);
        if (node != null) WirelessLinker.unlink(server, node, old.network());
        data.unbind(site);
        return Result.of(true, WirelessLang.UNBOUND);
    }

    /** The network a block is bound to, when the player's team may use it. */
    public static @Nullable WirelessNetwork networkOf(ServerPlayer player, ServerLevel level, BlockPos pos) {
        WirelessData data = WirelessData.get(level.getServer());
        Binding binding = data.at(site(level, pos));
        WirelessNetwork network = binding == null ? null : data.network(binding.network());
        return network != null && WirelessTeams.canUse(player.getUUID(), network) ? network : null;
    }

    public static Result read(ServerPlayer player, ServerLevel level, BlockPos pos) {
        if (!WirelessNodes.bindable(level, pos)) return Result.of(false, WirelessLang.NOT_BINDABLE);
        WirelessData data = WirelessData.get(level.getServer());
        Binding binding = data.at(site(level, pos));
        if (binding == null) return Result.of(false, WirelessLang.NOT_BOUND);
        WirelessNetwork network = data.network(binding.network());
        if (network == null) return Result.of(false, WirelessLang.NETWORK_GONE);
        if (!WirelessTeams.canUse(player.getUUID(), network)) return Result.of(false, WirelessLang.NETWORK_DENIED);
        return new Result(true, Component.translatable(WirelessLang.READ, network.name()), network);
    }
}
