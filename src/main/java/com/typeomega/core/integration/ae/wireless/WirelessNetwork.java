package com.typeomega.core.integration.ae.wireless;

import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

/**
 * A named wireless network: the id the machines and the configurator carry, the name players see, and the team that
 * may use it (the FTB party when FTB Teams is loaded, otherwise the player who created it).
 */
public record WirelessNetwork(String id, String name, UUID team) {

    CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id);
        tag.putString("name", name);
        tag.putUUID("team", team);
        return tag;
    }

    static WirelessNetwork fromTag(CompoundTag tag) {
        return new WirelessNetwork(tag.getString("id"), tag.getString("name"), tag.getUUID("team"));
    }
}
