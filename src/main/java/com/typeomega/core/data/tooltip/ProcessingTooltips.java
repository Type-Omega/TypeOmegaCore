package com.typeomega.core.data.tooltip;

import com.gregtechceu.gtceu.api.GTValues;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import com.typeomega.core.data.TypeOmegaTooltips;
import com.typeomega.core.data.rocket.TypeOmegaRocketRecipes;
import com.typeomega.core.miner.FragmentCollectorLogic;
import com.typeomega.core.processing.TierParallel;
import com.typeomega.lib.lang.Tr;
import com.typeomega.lib.tooltip.MultiblockTooltipBuilder;
import com.typeomega.lib.tooltip.TooltipRow;

import java.util.List;

import static com.typeomega.core.data.TypeOmegaTooltips.finish;
import static com.typeomega.core.data.TypeOmegaTooltips.own;
import static com.typeomega.core.data.TypeOmegaTooltips.shared;
import static com.typeomega.core.data.TypeOmegaTooltips.start;
import static com.typeomega.lib.tooltip.TooltipArg.emphasis;
import static com.typeomega.lib.tooltip.TooltipArg.highlight;
import static com.typeomega.lib.tooltip.TooltipArg.value;

/** Large Furnace, Ore Processing Plant, the four Plus factories, Rocket Assembler and the Fragment Collectors. */
public final class ProcessingTooltips {

    private ProcessingTooltips() {}

    public static void register() {
        TypeOmegaTooltips.register("large_furnace", ProcessingTooltips::largeFurnace);
        TypeOmegaTooltips.register("ore_processing_plant", ProcessingTooltips::orePlant);
        for (String id : List.of("processing_plus", "assembling_plus", "separating_plus", "mixing_plus")) {
            TypeOmegaTooltips.register(id, () -> plus(id));
        }
        TypeOmegaTooltips.register("rocket_assembler", ProcessingTooltips::rocketAssembler);
        TypeOmegaTooltips.register("fragment_collector", ProcessingTooltips::fragmentCollector);
        TypeOmegaTooltips.register("large_fragment_collector", ProcessingTooltips::largeFragmentCollector);
    }

    static Component parallels(int amount) {
        return shared("parallels").component(value(amount));
    }

    /** Parallel rules and the GTCEu overclock every electric multiblock of the core shares. */
    static MultiblockTooltipBuilder electric(MultiblockTooltipBuilder b) {
        return b.line(shared("parallels_items"), emphasis(shared("not")))
                .line(shared("overclock"))
                .line(shared("overclock.rule"), value("x2"), value("x4"));
    }

    private static List<Component> largeFurnace() {
        String id = "large_furnace";
        var b = start("furnace")
                .line(own(id, "purpose"), highlight(own(id, "purpose.parallels")))
                .separator()
                .line(own(id, "parallels"), parallels(TierParallel.largeFurnace(GTValues.LV)),
                        value(GTValues.VN[GTValues.LV]))
                .line(own(id, "cap"), parallels(TierParallel.largeFurnace(GTValues.UV)),
                        value(GTValues.VN[GTValues.UV]))
                .line(shared("parallel_hatch"), highlight(shared("parallel_hatch.name")));
        electric(b);
        return finish(b, id);
    }

    private static List<Component> orePlant() {
        String id = "ore_processing_plant";
        int mv = TierParallel.orePlant(GTValues.MV);
        var b = start("ore_processor")
                .line(own(id, "purpose"), highlight(own(id, "purpose.water")))
                .separator()
                .line(own(id, "parallels"), parallels(mv), value(GTValues.VN[GTValues.MV]),
                        value(TierParallel.orePlant(GTValues.HV) - mv))
                .warning(own(id, "no_hatches"), emphasis(own(id, "no")));
        electric(b);
        return finish(b, id);
    }

    private static List<Component> plus(String id) {
        var b = start(id)
                .line(own(id, "purpose"))
                .separator()
                .line(shared("plus.parallels"), parallels(TierParallel.plus(GTValues.LV)),
                        value(GTValues.VN[GTValues.LV]), value(TierParallel.plus(GTValues.IV)),
                        value(GTValues.VN[GTValues.IV]))
                .line(shared("parallel_hatch"), highlight(shared("parallel_hatch.name")))
                .line(shared("switch_type"));
        electric(b);
        return finish(b, id);
    }

    private static List<Component> rocketAssembler() {
        String id = "rocket_assembler";
        var b = start("rocket_assembler")
                .line(own(id, "purpose"))
                .separator()
                .table(rocket(own(id, "tier_1"), TypeOmegaRocketRecipes.TIER_1_EUT,
                        TypeOmegaRocketRecipes.TIER_1_TICKS),
                        rocket(own(id, "tier_2"), TypeOmegaRocketRecipes.TIER_2_EUT,
                                TypeOmegaRocketRecipes.TIER_2_TICKS))
                .separator()
                .line(own(id, "pad"), highlight("3 x 3"))
                .line(shared("overclock"))
                .line(shared("overclock.rule"), value("x2"), value("x4"));
        return finish(b, id);
    }

    private static TooltipRow rocket(Tr name, int eut, int ticks) {
        return TooltipRow.of(name, ChatFormatting.GOLD, shared("eut").component(eut),
                shared("seconds").component(ticks / 20));
    }

    private static List<Component> fragmentCollector() {
        String id = "fragment_collector";
        int seconds = FragmentCollectorLogic.DURATION / 20;
        var b = start("miner")
                .line(own(id, "purpose"), highlight(own(id, "fragment")))
                .separator()
                .table(TooltipRow.of(own(id, "alone"), ChatFormatting.GOLD, FragmentCollectorLogic.OPERATIONS,
                        shared("seconds").component(seconds)).effect(own(id, "alone.effect")),
                        TooltipRow.of(own(id, "card"), ChatFormatting.GOLD, FragmentCollectorLogic.OPERATIONS,
                                shared("seconds").component(seconds)).effect(own(id, "card.effect")))
                .line(own(id, "energy"), value(GTValues.VA[GTValues.LV]), value(GTValues.VN[GTValues.LV]))
                .separator()
                .line(own(id, "keep"), emphasis(own(id, "never")),
                        value(Fmt.pct(FragmentCollectorLogic.DUPLICATE_CHANCE / 10000.0)))
                .line(shared("overclock"))
                .line(shared("overclock.rule"), value("x2"), value("x4"));
        return finish(b, id);
    }

    private static List<Component> largeFragmentCollector() {
        String id = "large_fragment_collector";
        var b = start("miner")
                .line(own(id, "purpose"), value(FragmentCollectorLogic.LARGE_PARALLEL),
                        value(GTValues.VN[GTValues.EV]))
                .separator()
                .line(own(id, "energy"), value(GTValues.VA[GTValues.EV]),
                        value(FragmentCollectorLogic.OPERATIONS * FragmentCollectorLogic.LARGE_PARALLEL))
                .line(own(id, "parallel"), highlight(shared("parallel_hatch.name")))
                .line(own(id, "keep"), value(Fmt.pct(FragmentCollectorLogic.DUPLICATE_CHANCE / 10000.0)))
                .line(shared("overclock"))
                .line(shared("overclock.rule"), value("x2"), value("x4"));
        return finish(b, id);
    }
}
