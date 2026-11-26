package com.typeomega.core.kardashev.hud;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * The player's choice to show the Kardashev HUD, kept in his client config; the position is the lib's (D-443). Not a
 * client-only class so the screen can read it while it is built on a server, where it is never loaded and says off.
 */
public final class KardashevHudConfig {

    public static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.BooleanValue ENABLED;

    static {
        var builder = new ForgeConfigSpec.Builder();
        ENABLED = builder.comment("Show the Kardashev Meter HUD (also toggled on the meter's screen).")
                .define("hud", false);
        SPEC = builder.build();
    }

    private KardashevHudConfig() {}

    public static boolean enabled() {
        try {
            return ENABLED.get();
        } catch (IllegalStateException notLoaded) {
            return false;
        }
    }

    public static void set(boolean enabled) {
        try {
            ENABLED.set(enabled);
            SPEC.save();
        } catch (IllegalStateException notLoaded) {
            // The config file is not loaded yet; the choice is made again at the next toggle.
        }
    }
}
