package com.typeomega.core.integration.ae.cards;

import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.Locale;

/**
 * Acceleration Card (II) to (V) (D-442): each is 8 times the one before, starting from the AE2 card (tier 0, x1).
 * The recipe is four AE2 Acceleration Cards around a circuit of the listed voltage.
 */
public enum AccelerationTier {

    II(1, CustomTags.EV_CIRCUITS),
    III(2, CustomTags.LuV_CIRCUITS),
    IV(3, CustomTags.UV_CIRCUITS),
    V(4, CustomTags.UEV_CIRCUITS);

    /** 0 is the AE2 card, 1 to 4 are II to V. */
    public final int level;
    public final long multiplier;
    public final TagKey<Item> circuit;

    AccelerationTier(int level, TagKey<Item> circuit) {
        this.level = level;
        this.multiplier = multiplierOf(level);
        this.circuit = circuit;
    }

    /** How many AE2 Acceleration Cards one card of this level is worth: 1, 8, 64, 512, 4096. */
    public static long multiplierOf(int level) {
        return 1L << (3 * level);
    }

    public String id() {
        return "speed_card_" + name().toLowerCase(Locale.ROOT);
    }

    public static AccelerationTier ofLevel(int level) {
        return values()[level - 1];
    }
}
