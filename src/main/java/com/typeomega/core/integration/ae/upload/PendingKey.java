package com.typeomega.core.integration.ae.upload;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * Client side: the recipe the player just moved into an encoder, kept until they press Encode. It belongs to that
 * one transfer, so the encode that follows takes it and a later hand-made pattern is never sent anywhere. Each
 * terminal reads only its own entry: AE2's by the id of the open menu, the Wireless Comprehensive Work Terminal's by
 * {@link #WCWT}. An entry nobody took within a few minutes is forgotten.
 */
public final class PendingKey {

    /** The owner of the entries the Wireless Comprehensive Work Terminal reads; it has no AE2 menu id to go by. */
    public static final int WCWT = -2;

    private static final long LIFETIME_NANOS = TimeUnit.MINUTES.toNanos(5);
    private static volatile Entry pending;

    private record Entry(int owner, RecipeKey key, long madeAt) {}

    private PendingKey() {}

    public static void set(int owner, @Nullable RecipeKey key) {
        pending = key == null || key.isEmpty() ? null : new Entry(owner, key, System.nanoTime());
    }

    public static void clear() {
        pending = null;
    }

    /** The key set for this owner, removed; null when there is none or it is too old. */
    @Nullable
    public static RecipeKey take(int owner) {
        Entry entry = pending;
        if (entry == null || entry.owner != owner) return null;
        pending = null;
        return System.nanoTime() - entry.madeAt > LIFETIME_NANOS ? null : entry.key;
    }
}
