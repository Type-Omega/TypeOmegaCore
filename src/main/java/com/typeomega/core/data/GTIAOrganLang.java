package com.typeomega.core.data;

import com.typeomega.core.GTIAMod;

/** English names of the organ system and of the rules added for parity with GTO Rules (GTIA-D-279, D-280). */
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
        GTIAMod.REGISTRATE.addRawLang("typeomega.rules.group.organs", "Organs");
        GTIAMod.REGISTRATE.addRawLang("typeomega.rule.resources.rubber_tree_chance", "Rubber tree chance");
        GTIAMod.REGISTRATE.addRawLang("typeomega.rule.resources.rubber_tree_chance.desc",
                "Multiplies how often rubber trees grow in new chunks.");
        GTIAMod.REGISTRATE.addRawLang("typeomega.rule.resources.infinite_bedrock_fluids", "Infinite bedrock fluids");
        GTIAMod.REGISTRATE.addRawLang("typeomega.rule.resources.infinite_bedrock_fluids.desc",
                "Bedrock fluid veins never run dry.");
        GTIAMod.REGISTRATE.addRawLang("typeomega.rule.machines.require_gt_tools", "Require GT tools");
        GTIAMod.REGISTRATE.addRawLang("typeomega.rule.machines.require_gt_tools.desc",
                "GregTech machines and pipes only drop when broken with a GregTech tool.");
        GTIAMod.REGISTRATE.addRawLang("typeomega.rule.survival.material_hazards", "Material hazards");
        GTIAMod.REGISTRATE.addRawLang("typeomega.rule.survival.material_hazards.desc",
                "Radioactive and toxic materials hurt players who carry them without protection.");
        GTIAMod.REGISTRATE.addRawLang("typeomega.rule.survival.extra_regen_food", "Extra regeneration food level");
        GTIAMod.REGISTRATE.addRawLang("typeomega.rule.survival.extra_regen_food.desc",
                "Food level from which you heal faster. 21 turns it off.");
        GTIAMod.REGISTRATE.addRawLang("typeomega.rule.survival.extra_regen_strength", "Extra regeneration strength");
        GTIAMod.REGISTRATE.addRawLang("typeomega.rule.survival.extra_regen_strength.desc",
                "Half hearts healed every 2.5 seconds.");
        GTIAMod.REGISTRATE.addRawLang("typeomega.rule.organs.planet_requirement", "Planets need organs");
        GTIAMod.REGISTRATE.addRawLang("typeomega.rule.organs.planet_requirement.desc",
                "Other planets hurt players whose organ set tier is too low: the Moon and Mars need tier 1.");
        GTIAMod.REGISTRATE.addRawLang("typeomega.rule.organs.organ_cost", "Organ cost");
        GTIAMod.REGISTRATE.addRawLang("typeomega.rule.organs.organ_cost.desc",
                "Multiplies the parts of every organ recipe.");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ.screen.speed", "Speed +%s%%");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ.screen.armor", "Armor +%s");
        GTIAMod.REGISTRATE.addRawLang("typeomega.organ.screen.reach", "Reach +%s");
        GTIAMod.REGISTRATE.addRawLang("typeomega.rules.group.chemistry", "Chemistry");
        GTIAMod.REGISTRATE.addRawLang("typeomega.rule.chemistry.catalyst_wear", "Catalyst wear");
        GTIAMod.REGISTRATE.addRawLang("typeomega.rule.chemistry.catalyst_wear.desc",
                "Chance per run, in hundredths of a percent, that the catalyst of a GTIA chemistry plant wears out. 0 keeps every catalyst forever.");
        GTIAMod.REGISTRATE.addRawLang("typeomega.rule.chemistry.heat_recovery", "Heat recovery");
        GTIAMod.REGISTRATE.addRawLang("typeomega.rule.chemistry.heat_recovery.desc",
                "Multiplies the steam the chemistry plants make from their own reaction heat.");
    }
}
