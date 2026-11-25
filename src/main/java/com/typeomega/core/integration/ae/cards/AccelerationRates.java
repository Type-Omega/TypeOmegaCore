package com.typeomega.core.integration.ae.cards;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.core.definitions.AEItems;

import java.util.function.IntToLongFunction;

/**
 * What Acceleration Cards (II) to (V) do (D-442). They speed up the transfer machines of AE2 and ExtendedAE: the
 * import and export buses (items per operation round, per tick) and the IO ports (items moved per tick). Every card
 * counts as its multiplier of an AE2 card, and the machine's own table is read for the number of cards installed:
 * {@code table(cards) * (sum of multipliers) / cards}. The machine's card limit is shared by all tiers, highest first.
 */
public final class AccelerationRates {

    private AccelerationRates() {}

    /**
     * Operations per tick of an item bus with this many AE2 cards (AE2 IOBusPart, and ExtendedAE before its own
     * factor).
     */
    public static long busOperations(int cards) {
        return switch (cards) {
            case 1 -> 8;
            case 2 -> 32;
            case 3 -> 64;
            case 4 -> 96;
            default -> 1;
        };
    }

    /** Items per tick of the AE2 IO Port (3 card slots). */
    public static long ioPortOperations(int cards) {
        return 256L * switch (cards) {
            case 1 -> 2;
            case 2 -> 4;
            case 3 -> 8;
            default -> 1;
        };
    }

    /** Items per tick of ExtendedAE's Extended IO Port (5 card slots). */
    public static long extendedIoPortOperations(int cards) {
        return 2048L * switch (cards) {
            case 1 -> 2;
            case 2 -> 8;
            case 3 -> 32;
            case 4 -> 128;
            case 5 -> 512;
            default -> 1;
        };
    }

    /** True when the machine holds at least one card above the AE2 one, the only case that changes anything. */
    public static boolean hasTieredCards(IUpgradeInventory upgrades) {
        for (AccelerationTier tier : AccelerationTier.values()) {
            if (upgrades.getInstalledUpgrades(AccelerationCards.card(tier)) > 0) return true;
        }
        return false;
    }

    public static long scale(IntToLongFunction table, IUpgradeInventory upgrades) {
        int remaining = upgrades.getMaxInstalled(AEItems.SPEED_CARD);
        int cards = 0;
        long weight = 0;
        for (int level = AccelerationTier.values().length; level >= 0 && remaining > 0; level--) {
            var item = level == 0 ? AEItems.SPEED_CARD.asItem() :
                    AccelerationCards.card(AccelerationTier.ofLevel(level));
            int count = Math.min(remaining, upgrades.getInstalledUpgrades(item));
            remaining -= count;
            cards += count;
            weight += count * AccelerationTier.multiplierOf(level);
        }
        long base = table.applyAsLong(cards);
        return cards == 0 ? base : base * weight / cards;
    }

    public static int busOperationsPerTick(IUpgradeInventory upgrades) {
        return (int) Math.min(Integer.MAX_VALUE, scale(c -> busOperations(c), upgrades));
    }

    public static long ioPortItemsPerTick(IUpgradeInventory upgrades) {
        return scale(AccelerationRates::ioPortOperations, upgrades);
    }

    public static long extendedIoPortItemsPerTick(IUpgradeInventory upgrades) {
        return scale(AccelerationRates::extendedIoPortOperations, upgrades);
    }
}
