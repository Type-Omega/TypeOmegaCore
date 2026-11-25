package com.typeomega.core.integration.ae.interfaces;

import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.Locale;

/**
 * The tiers of the ME Interface above the plain AE2 one (tier 0): II to V (GTIA-D-442). A slot of tier N holds
 * {@link #multiplier} times what a plain interface slot holds, and the recipe asks for a circuit of the listed voltage.
 */
public enum InterfaceTier {

    II(1, 4_096L, CustomTags.MV_CIRCUITS),
    III(2, 262_144L, CustomTags.EV_CIRCUITS),
    IV(3, 16_777_216L, CustomTags.LuV_CIRCUITS),
    V(4, 1_073_741_824L, CustomTags.UV_CIRCUITS);

    /** Config slots and storage slots of every tier above the plain interface. */
    public static final int SLOTS = 36;

    /** 0 is the plain AE2 interface, 1 to 4 are II to V. */
    public final int level;
    public final long multiplier;
    public final TagKey<Item> circuit;

    InterfaceTier(int level, long multiplier, TagKey<Item> circuit) {
        this.level = level;
        this.multiplier = multiplier;
        this.circuit = circuit;
    }

    public String suffix() {
        return name().toLowerCase(Locale.ROOT);
    }

    public String blockId() {
        return "me_interface_" + suffix();
    }

    public String partId() {
        return "cable_me_interface_" + suffix();
    }

    public static InterfaceTier ofLevel(int level) {
        return values()[level - 1];
    }
}
