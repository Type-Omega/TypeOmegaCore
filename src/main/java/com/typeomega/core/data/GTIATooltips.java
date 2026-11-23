package com.typeomega.core.data;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import com.typeomega.core.GTIAMod;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Multiblock tooltips in the GTNA layout: an optional real fact, then "Main Function", "Auxiliary Module" (only when
 * the structure has one) and "Running Requirements". English text lives here and is registered as lang keys
 * (typeomega.machine.&lt;id&gt;.tooltip.&lt;section&gt;.&lt;n&gt;); pt_br.json translates the same keys.
 */
public final class GTIATooltips {

    private static final String[] SECTIONS = { "main", "auxiliary", "requirements" };
    private static final String OVERCLOCK = "Overclocks like a GregTech machine and supports Batch Mode";

    /** id -> section -> lines (English). "fact" is the optional real-world line on top. */
    private static final Map<String, Map<String, String[]>> TEXT = new LinkedHashMap<>();

    static {
        add("rocket_assembler",
                "The Saturn V was stacked upright in a building 160 m tall, then rolled to the pad",
                new String[] { "Builds Ad Astra rockets from their parts, GregTech components and Rocket Fuel",
                        "The tier 1 rocket takes HV power for 2 minutes" },
                null,
                new String[] { "An Ad Astra Launch Pad (3 x 3) in the middle of the platform",
                        "Hatches go on the casings of the base and the control house in front", OVERCLOCK });
        add("national_dispatch_center",
                "A national grid is balanced minute by minute from one control room",
                new String[] { "Megastructure 1 of Type 0: raises your team's Kardashev cap to 0.89",
                        "Programs the Grid Transponders that every wireless energy hatch needs" },
                null,
                new String[] { "Hatches go on the clean stainless steel casings of the control hall", OVERCLOCK });
        add("cosmodrome",
                "Mission control watches every launch from kilometres away, through its tracking dishes",
                new String[] { "Megastructure 2 of Type 0: raises your team's Kardashev cap to 0.99",
                        "Launches the Lunar Probe with Rocket Fuel and returns the Lunar Survey Data",
                        "Without the survey, no rocket lands on the Moon" },
                null,
                new String[] { "A formed Rocket Assembler within 48 blocks: the mission control links to it",
                        "Hatches go on the clean stainless steel casings of the control hall", OVERCLOCK });
        add("fragment_collector",
                "Survey rigs read the rock through cores before any shaft is sunk",
                new String[] { "Mines the veins of the dimension of the World Fragment in its input bus",
                        "Fragment alone: 4 raw ores per 8 s from a random vein of that dimension",
                        "Fragment and a Vein Card of the same dimension: that vein, 4 operations at the card's purity",
                        "The fragment and the card are never used up; each cycle has a 50% chance of a fragment copy" },
                null,
                new String[] { "Hatches go on the steel platform casings", OVERCLOCK });
        add("large_fragment_collector",
                "Offshore platforms drill several wells from one deck",
                new String[] { "The Fragment Collector, 16 operations at once on an EV platform",
                        "Accepts a Parallel Hatch for more" },
                null,
                new String[] { "Hatches go on the titanium platform casings", OVERCLOCK });
        add("large_furnace", "Steel mills ran rows of identical furnaces off one gas main and two stacks",
                new String[] { "Smelts any furnace recipe with many parallels",
                        "64 parallels at LV, doubled for every energy tier above", "Parallels multiply items, not energy; the overclock still applies" },
                null, new String[] { "Hatches go on the solid steel casings of the plinth", "Accepts one Parallel Hatch", OVERCLOCK });
        add("ore_processing_plant", "A beneficiation plant crushes, grinds, washes and separates ore in one line",
                new String[] { "Turns raw ore into final dusts and byproducts in one step, with water",
                        "64 parallels at MV, +100 for every energy tier above", "Parallels multiply items, not energy; the overclock still applies" },
                null, new String[] { "Hatches go on the aluminium casings", "No Parallel Hatch or Maintenance Hatch", OVERCLOCK });
        add("processing_plus", "One hall, many lines: the module in the middle tells the family apart",
                new String[] { "Bending, pressing, cutting, extruding, turning, wire, engraving and solidifying", "16 parallels at LV, doubled per tier, fixed at 256 from IV", "Parallels multiply items, not energy; the overclock still applies" },
                null, new String[] { "Hatches go on the aluminium casings", "Switch the recipe type in the controller", OVERCLOCK });
        add("assembling_plus", "One hall, many lines: the module in the middle tells the family apart",
                new String[] { "Assembler and circuit assembler recipes", "16 parallels at LV, doubled per tier, fixed at 256 from IV", "Parallels multiply items, not energy; the overclock still applies" },
                null, new String[] { "Hatches go on the aluminium casings", "Switch the recipe type in the controller", OVERCLOCK });
        add("separating_plus", "One hall, many lines: the module in the middle tells the family apart",
                new String[] { "Centrifuge, thermal centrifuge, electrolyzer, sifter, macerator and extractor recipes", "16 parallels at LV, doubled per tier, fixed at 256 from IV", "Parallels multiply items, not energy; the overclock still applies" },
                null, new String[] { "Hatches go on the aluminium casings", "Switch the recipe type in the controller", OVERCLOCK });
        add("mixing_plus", "One hall, many lines: the module in the middle tells the family apart",
                new String[] { "Chemical reactor, mixer, chemical bath and ore washer recipes", "16 parallels at LV, doubled per tier, fixed at 256 from IV", "Parallels multiply items, not energy; the overclock still applies" },
                null, new String[] { "Hatches go on the aluminium casings", "Switch the recipe type in the controller", OVERCLOCK });
    }

    private GTIATooltips() {}

    private static void add(String id, String fact, String[] main, String[] auxiliary, String[] requirements) {
        Map<String, String[]> sections = new LinkedHashMap<>();
        if (fact != null) sections.put("fact", new String[] { fact });
        sections.put("main", main);
        if (auxiliary != null) sections.put("auxiliary", auxiliary);
        sections.put("requirements", requirements);
        TEXT.put(id, sections);
    }

    /** The tooltip lines of a multiblock registered above. */
    public static Component[] multiblock(String id) {
        Map<String, String[]> sections = TEXT.get(id);
        if (sections == null) throw new IllegalArgumentException("no tooltip for " + id);
        List<Component> lines = new ArrayList<>();
        if (sections.containsKey("fact")) {
            lines.add(Component.translatable(key(id, "fact", 0)).withStyle(ChatFormatting.GRAY,
                    ChatFormatting.ITALIC));
        }
        for (String section : SECTIONS) {
            String[] text = sections.get(section);
            if (text == null) continue;
            lines.add(Component.literal("- ").append(Component.translatable("typeomega.tooltip.section." + section))
                    .withStyle(ChatFormatting.GOLD));
            for (int i = 0; i < text.length; i++) {
                lines.add(Component.literal("∘ ").append(Component.translatable(key(id, section, i)))
                        .withStyle(ChatFormatting.YELLOW));
            }
        }
        return lines.toArray(Component[]::new);
    }

    static void registerLang() {
        GTIAMod.REGISTRATE.addRawLang("typeomega.tooltip.section.main", "Main Function");
        GTIAMod.REGISTRATE.addRawLang("typeomega.tooltip.section.auxiliary", "Auxiliary Module");
        GTIAMod.REGISTRATE.addRawLang("typeomega.tooltip.section.requirements", "Running Requirements");
        TEXT.forEach((id, sections) -> sections.forEach((section, text) -> {
            for (int i = 0; i < text.length; i++) GTIAMod.REGISTRATE.addRawLang(key(id, section, i), text[i]);
        }));
    }

    private static String key(String id, String section, int line) {
        return "typeomega.machine." + id + ".tooltip." + section + "." + line;
    }
}
