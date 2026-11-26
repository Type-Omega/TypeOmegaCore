package com.typeomega.core.integration.ae.upload;

import net.minecraft.client.gui.screens.Screen;

import appeng.menu.me.items.PatternEncodingTermMenu;

/** Client side of the automatic upload. Separate from the mixin so the server never touches the client classes. */
public final class UploadClientHooks {

    private UploadClientHooks() {}

    /** The player pressed Encode: tell the server where the pattern comes from, unless Shift asks to keep it here. */
    public static void onEncode(PatternEncodingTermMenu menu) {
        RecipeKey key = PendingKey.take(menu.containerId);
        if (key != null && !Screen.hasShiftDown()) UploadNetwork.sendToServer(key);
    }

    /** The recipe was moved into an encoder this mod serves, not one that ExtendedAE Plus already serves. */
    public static void record(int containerId, RecipeKey key) {
        if (PatternUpload.active()) PendingKey.set(containerId, key);
    }
}
