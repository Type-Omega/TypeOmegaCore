package com.typeomega.core.data;

import com.typeomega.core.GTIAMod;

/** English names of the organ system (GTIA-D-279). */
public final class GTIAOrganLang {

    private GTIAOrganLang() {}

    public static void init() {
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ_type.wing", "Wing");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ_type.eye", "Eye");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ_type.spine", "Spine");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ_type.lung", "Lung");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ_type.liver", "Liver");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ_type.heart", "Heart");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ_type.left_arm", "Left Arm");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ_type.right_arm", "Right Arm");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ_type.left_leg", "Left Leg");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ_type.right_leg", "Right Leg");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.wing",
                "Gives flight while it lasts; wings are used one after the other");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.fly_speed", "Flight speed: %s");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.durability", "Flight left: %s/%s s");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.level", "Level: %s");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.speed", "Both legs at this tier: +%s%% movement speed");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.reach", "+%s block reach");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.liver", "Cures poison and wither");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.lung", "Breathe under water");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.set_header", "A full set of this tier gives:");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.armor", "+%s armor and armor toughness");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.saturation", "Never hungry");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.flight", "Unlimited flight");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ.wing_broken", "Your wing is worn out");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ.planet_warning",
                "This planet tears at your body: it needs an organ set of tier %s");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ.screen.set_tier", "Organ set tier: %s");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ_modifier.tooltip.1", "Lets you change your organs");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ_modifier.tooltip.2", "Each organ set gives different effects");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ_modifier.tooltip.3", "Right click to open");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ_modifier.tooltip.4",
                "Planets need an organ set of a certain tier");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ.screen.speed", "Speed +%s%%");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ.screen.armor", "Armor +%s");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ.screen.reach", "Reach +%s");
    }
}
