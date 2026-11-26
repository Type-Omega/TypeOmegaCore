package com.typeomega.core.mega.common.client;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Client rendering configuration for megastructures (D-448). Defaults to drawing the 3D model overlay when available,
 * falling back to the block renderer when disabled or when no model file is present.
 */
public final class MegaRenderConfig {

    public static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.BooleanValue MODEL_OVERLAY;

    static {
        var builder = new ForgeConfigSpec.Builder();
        MODEL_OVERLAY = builder
                .comment("Draw 3D models over formed megastructures (pilot: Yggdrasil Orchid). " +
                        "Disable to fall back to the block renderer.")
                .translation("typeomega.config.mega_model_overlay")
                .define("modelOverlay", true);
        SPEC = builder.build();
    }

    private MegaRenderConfig() {}

    /** Whether the 3D model overlay is enabled on the client; defaults to {@code true} before config load. */
    public static boolean modelOverlayEnabled() {
        try {
            return MODEL_OVERLAY.get();
        } catch (IllegalStateException notLoaded) {
            return true;
        }
    }
}
