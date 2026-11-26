package com.typeomega.core.data;

import com.typeomega.core.TypeOmegaMod;

/** English names of the organ system (D-279). */
public final class TypeOmegaOrganLang {

    private TypeOmegaOrganLang() {}

    public static void init() {
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ_type.wing", "Wing");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ_type.eye", "Eye");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ_type.spine", "Spine");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ_type.lung", "Lung");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ_type.liver", "Liver");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ_type.heart", "Heart");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ_type.left_arm", "Left Arm");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ_type.right_arm", "Right Arm");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ_type.left_leg", "Left Leg");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ_type.right_leg", "Right Leg");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.wing",
                "Gives flight while it lasts; wings are used one after the other");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.fly_speed", "Flight speed: %s");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.durability", "Flight left: %s/%s s");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.level", "Level: %s");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.speed",
                "Both legs at this tier: +%s%% movement speed");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.reach", "+%s block reach");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.liver", "Cures poison and wither");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.lung", "Breathe under water");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.set_header", "A full set of this tier gives:");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.armor", "+%s armor and armor toughness");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.saturation", "Never hungry");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ.tooltip.flight", "Unlimited flight");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ.wing_broken", "Your wing is worn out");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ.planet_warning",
                "This planet tears at your body: it needs an organ set of tier %s");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ.screen.set_tier", "Organ set tier: %s");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ_modifier.tooltip.1", "Lets you change your organs");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ_modifier.tooltip.2",
                "Each organ set gives different effects");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ_modifier.tooltip.3", "Right click to open");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ_modifier.tooltip.4",
                "Planets need an organ set of a certain tier");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ.screen.speed", "Speed +%s%%");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ.screen.armor", "Armor +%s");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.organ.screen.reach", "Reach +%s");
    }
}
