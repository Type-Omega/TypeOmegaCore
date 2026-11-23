package com.typeomega.core.mega.typezero;

import com.typeomega.core.GTIAMod;

/** Registration of the Type 0 megastructures (GTIA-D-390) and their texts. */
public final class TypeZeroMegas {

    private TypeZeroMegas() {}

    /** Blocks, items and texts, at mod construction. */
    public static void init() {
        TypeZeroItems.init();
        lang("typeomega.grid_dispatch", "Grid Dispatch");
    }

    /** The controllers, in GTCEu's machine registration event. */
    public static void registerMachines() {
        NationalDispatchCenter.init();
    }

    private static void lang(String key, String english) {
        GTIAMod.REGISTRATE.addRawLang(key, english);
    }
}
