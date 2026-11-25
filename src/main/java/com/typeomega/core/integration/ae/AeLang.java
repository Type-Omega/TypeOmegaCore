package com.typeomega.core.integration.ae;

import com.typeomega.core.GTIAMod;

/** Tooltip texts of the AE2 pieces (GTIA-D-442): English here, pt_br and zh_cn in the lang files. */
public final class AeLang {

    public static final String SLOTS = "typeomega.me_interface.slots";
    public static final String CAPACITY = "typeomega.me_interface.capacity";
    public static final String UPGRADE = "typeomega.me_interface.upgrade";

    private AeLang() {}

    public static void init() {
        GTIAMod.REGISTRATE.addRawLang(SLOTS, "%s config slots and %s storage slots");
        GTIAMod.REGISTRATE.addRawLang(CAPACITY,
                "Each slot holds %sx a stack: up to %s items or %s B of fluid");
        GTIAMod.REGISTRATE.addRawLang(UPGRADE,
                "Sneak right-click a lower-tier interface of the same form to replace it, keeping its config and contents");
    }
}
