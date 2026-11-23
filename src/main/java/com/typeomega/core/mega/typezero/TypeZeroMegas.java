package com.typeomega.core.mega.typezero;

import com.typeomega.core.GTIAMod;

/** Registration of the Type 0 megastructures (GTIA-D-390) and their texts. */
public final class TypeZeroMegas {

    private TypeZeroMegas() {}

    /** Blocks, items and texts, at mod construction. */
    public static void init() {
        TypeZeroBlocks.init();
        TypeZeroItems.init();
        lang("typeomega.grid_dispatch", "Grid Dispatch");
        lang("typeomega.mission_control", "Mission Control");
        lang("typeomega.machine.cosmodrome.linked", "Linked to the Rocket Assembler at %s, %s, %s");
        lang("typeomega.machine.cosmodrome.unlinked", "No formed Rocket Assembler within %s blocks");
        lang("typeomega.lunar_survey.required", "The Moon needs Lunar Survey Data from the Cosmodrome");
    }

    /** The controllers, in GTCEu's machine registration event. */
    public static void registerMachines() {
        NationalDispatchCenter.init();
        Cosmodrome.init();
    }

    private static void lang(String key, String english) {
        GTIAMod.REGISTRATE.addRawLang(key, english);
    }
}
