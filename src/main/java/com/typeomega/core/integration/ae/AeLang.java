package com.typeomega.core.integration.ae;

import com.typeomega.core.TypeOmegaMod;

/** Tooltip texts of the AE2 pieces (D-442): English here, pt_br and zh_cn in the lang files. */
public final class AeLang {

    public static final String SLOTS = "typeomega.me_interface.slots";
    public static final String CAPACITY = "typeomega.me_interface.capacity";
    public static final String UPGRADE = "typeomega.me_interface.upgrade";
    public static final String CARD_DESC = "typeomega.speed_card.desc";
    public static final String CARD_LIMIT = "typeomega.speed_card.limit";
    public static final String CARD_SCOPE = "typeomega.speed_card.scope";
    public static final String CARD_REPLACE = "typeomega.speed_card.replace";

    private AeLang() {}

    public static void init() {
        TypeOmegaMod.REGISTRATE.addRawLang(SLOTS, "%s config slots and %s storage slots");
        TypeOmegaMod.REGISTRATE.addRawLang(CAPACITY,
                "Each slot holds %sx a stack: up to %s items or %s B of fluid");
        TypeOmegaMod.REGISTRATE.addRawLang(UPGRADE,
                "Sneak right-click a lower-tier interface of the same form to replace it, keeping its config and contents");
        TypeOmegaMod.REGISTRATE.addRawLang(CARD_DESC, "Each card is worth %s Acceleration Cards");
        TypeOmegaMod.REGISTRATE.addRawLang(CARD_LIMIT,
                "Shares the machine's Acceleration Card limit with the other tiers; the highest tiers count first");
        TypeOmegaMod.REGISTRATE.addRawLang(CARD_SCOPE, "Speeds up AE2 and ExtendedAE import/export buses and IO ports");
        TypeOmegaMod.REGISTRATE.addRawLang(CARD_REPLACE,
                "Sneak right-click a machine at its card limit to replace its lowest-tier card");
    }
}
