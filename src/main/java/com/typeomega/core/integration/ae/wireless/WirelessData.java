package com.typeomega.core.integration.ae.wireless;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * What the wireless ME networks keep in the world save: the networks, and which blocks are bound to which. A binding
 * is a position and the block it was made for, so it outlives a chunk unload and a world reload without any change to
 * the machine's own data.
 */
public final class WirelessData extends SavedData {

    private static final String NAME = "typeomega_me_wireless";

    /** A block in a dimension. */
    public record Site(ResourceLocation dimension, long pos) {

        public BlockPos blockPos() {
            return BlockPos.of(pos);
        }
    }

    /** The network a block is bound to, and the registry id of the block, to notice when it was replaced. */
    public record Binding(String network, String block) {}

    private final Map<String, WirelessNetwork> networks = new LinkedHashMap<>();
    private final Map<Site, Binding> bindings = new LinkedHashMap<>();

    public static WirelessData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(WirelessData::load, WirelessData::new, NAME);
    }

    // ---- networks ----

    public List<WirelessNetwork> of(UUID team) {
        List<WirelessNetwork> list = new ArrayList<>();
        for (WirelessNetwork network : networks.values()) if (network.team().equals(team)) list.add(network);
        list.sort(Comparator.comparing(WirelessNetwork::name, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    public @Nullable WirelessNetwork network(String id) {
        return networks.get(id);
    }

    /** A new network for the team; a name the team already uses gets a number, an empty one a default. */
    public WirelessNetwork create(UUID team, String requested) {
        List<WirelessNetwork> own = of(team);
        String base = requested.isBlank() ? "Network " + (own.size() + 1) : requested.strip();
        String name = base;
        for (int n = 2; nameTaken(own, name); n++) name = base + " (" + n + ")";
        WirelessNetwork network = new WirelessNetwork(UUID.randomUUID().toString(), name, team);
        networks.put(network.id(), network);
        setDirty();
        return network;
    }

    private static boolean nameTaken(List<WirelessNetwork> own, String name) {
        for (WirelessNetwork network : own) if (network.name().equalsIgnoreCase(name)) return true;
        return false;
    }

    // ---- bindings ----

    public @Nullable Binding at(Site site) {
        return bindings.get(site);
    }

    public void bind(Site site, Binding binding) {
        if (binding.equals(bindings.put(site, binding))) return;
        setDirty();
    }

    public void unbind(Site site) {
        if (bindings.remove(site) != null) setDirty();
    }

    /** A copy, so a sweep may unbind while it walks. */
    public Map<Site, Binding> bindings() {
        return new LinkedHashMap<>(bindings);
    }

    /** The other block of this kind bound to the network, if any: a network has one connector. */
    public @Nullable Site otherWithBlock(String network, String block, Site except) {
        for (var entry : bindings.entrySet()) {
            Binding binding = entry.getValue();
            if (binding.network().equals(network) && binding.block().equals(block) && !entry.getKey().equals(except)) {
                return entry.getKey();
            }
        }
        return null;
    }

    // ---- save ----

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag nets = new ListTag();
        for (WirelessNetwork network : networks.values()) nets.add(network.toTag());
        tag.put("networks", nets);
        ListTag binds = new ListTag();
        for (var entry : bindings.entrySet()) {
            CompoundTag bound = new CompoundTag();
            bound.putString("dimension", entry.getKey().dimension().toString());
            bound.putLong("pos", entry.getKey().pos());
            bound.putString("network", entry.getValue().network());
            bound.putString("block", entry.getValue().block());
            binds.add(bound);
        }
        tag.put("bindings", binds);
        return tag;
    }

    public static WirelessData load(CompoundTag tag) {
        WirelessData data = new WirelessData();
        for (Tag raw : tag.getList("networks", Tag.TAG_COMPOUND)) {
            WirelessNetwork network = WirelessNetwork.fromTag((CompoundTag) raw);
            data.networks.put(network.id(), network);
        }
        for (Tag raw : tag.getList("bindings", Tag.TAG_COMPOUND)) {
            CompoundTag bound = (CompoundTag) raw;
            data.bindings.put(new Site(new ResourceLocation(bound.getString("dimension")), bound.getLong("pos")),
                    new Binding(bound.getString("network"), bound.getString("block")));
        }
        return data;
    }
}
