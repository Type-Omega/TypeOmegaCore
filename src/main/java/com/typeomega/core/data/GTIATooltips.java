package com.typeomega.core.data;

import com.gregtechceu.gtceu.api.GTValues;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import com.typeomega.core.data.rocket.GTIARocketRecipes;
import com.typeomega.core.kardashev.KardashevScale;
import com.typeomega.core.mega.common.MegaLang;
import com.typeomega.core.mega.typezero.CosmodromeMachine;
import com.typeomega.core.mega.typezero.Tokamak;
import com.typeomega.core.mega.typezero.TokamakMachine;
import com.typeomega.core.mega.typezero.TypeZeroRecipes;
import com.typeomega.core.miner.FragmentCollectorLogic;
import com.typeomega.core.processing.TierParallel;
import com.typeomega.lib.lang.Tr;
import com.typeomega.lib.tooltip.Hatch;
import com.typeomega.lib.tooltip.MultiblockTooltip;
import com.typeomega.lib.tooltip.MultiblockTooltipBuilder;
import com.typeomega.lib.tooltip.TooltipRow;

import java.util.Locale;

import static com.typeomega.lib.tooltip.TooltipArg.emphasis;
import static com.typeomega.lib.tooltip.TooltipArg.highlight;
import static com.typeomega.lib.tooltip.TooltipArg.value;

/**
 * Multiblock tooltips in the GTNH layout of the lib (GTIA-D-410). The texts, in three languages, are in
 * {@code mega_lang/multiblock_tooltips.json}; every number comes from the constant the machine logic or the recipe
 * uses. The real-world fact of each machine stays as the grey italic line (GTIA-D-343).
 */
public final class GTIATooltips {

    /** Shown in blue italics on the last line, as the mod list names the mod. */
    public static final String MOD_NAME = "TypeOmegaCore";

    private GTIATooltips() {}

    /** Shared text {@code typeomega.tooltip.<key>}. */
    public static Tr shared(String key) {
        return MegaLang.tr("typeomega.tooltip." + key);
    }

    /** {@code typeomega.machine.<id>.tooltip.<key>}. */
    public static Tr own(String id, String key) {
        return MegaLang.tr("typeomega.machine." + id + ".tooltip." + key);
    }

    /** Builder with the Machine Type line already in place. */
    public static MultiblockTooltipBuilder start(String... types) {
        Tr[] trs = new Tr[types.length];
        for (int i = 0; i < types.length; i++) trs[i] = shared("type." + types[i]);
        return MultiblockTooltip.builder().machineType(trs);
    }

    /** The fact line and the mod name that close every tooltip. */
    public static Component[] finish(MultiblockTooltipBuilder builder, String id) {
        return builder.humor(own(id, "fact")).mod(MOD_NAME).toArray();
    }

    public static Component[] multiblock(String id) {
        return switch (id) {
            case "large_furnace" -> largeFurnace();
            case "ore_processing_plant" -> orePlant();
            case "processing_plus", "assembling_plus", "separating_plus", "mixing_plus" -> plus(id);
            case "rocket_assembler" -> rocketAssembler();
            case "fragment_collector" -> fragmentCollector();
            case "large_fragment_collector" -> largeFragmentCollector();
            case "national_dispatch_center" -> dispatchCenter();
            case "cosmodrome" -> cosmodrome();
            case Tokamak.ID -> tokamak();
            default -> throw new IllegalArgumentException("no tooltip for " + id);
        };
    }

    /**
     * A megastructure: the first mega_lang line describes it, the others are its rules. Their numbers are formulas
     * of the machine's physics, so they stay in the text.
     */
    public static Component[] megastructure(String id, int lines) {
        var b = start("megastructure").line(own(id, "0"));
        if (lines > 1) b.separator();
        for (int i = 1; i < lines; i++) b.line(own(id, String.valueOf(i)));
        return b.mod(MOD_NAME).toArray();
    }

    private static Component parallels(int amount) {
        return shared("parallels").component(value(amount));
    }

    private static MultiblockTooltipBuilder parallelRules(MultiblockTooltipBuilder b) {
        return b.line(shared("parallels_items"), emphasis(shared("not")));
    }

    private static Component[] largeFurnace() {
        String id = "large_furnace";
        var b = start("furnace")
                .line(own(id, "purpose"), highlight(own(id, "purpose.parallels")))
                .line(own(id, "parallels"), parallels(TierParallel.largeFurnace(GTValues.LV)),
                        value(GTValues.VN[GTValues.LV]));
        parallelRules(b).separator()
                .line(own(id, "hatches"))
                .line(shared("parallel_hatch"), highlight(shared("parallel_hatch.name")))
                .line(shared("overclock"))
                .hatches(Hatch.MULTI_AMP);
        return finish(b, id);
    }

    private static Component[] orePlant() {
        String id = "ore_processing_plant";
        int mv = TierParallel.orePlant(GTValues.MV);
        var b = start("ore_processor")
                .line(own(id, "purpose"), highlight(own(id, "purpose.water")))
                .line(own(id, "parallels"), parallels(mv), value(GTValues.VN[GTValues.MV]),
                        value(TierParallel.orePlant(GTValues.HV) - mv));
        parallelRules(b).separator()
                .line(own(id, "hatches"))
                .warning(own(id, "no_hatches"), emphasis(own(id, "no")))
                .line(shared("overclock"))
                .hatches(Hatch.MULTI_AMP);
        return finish(b, id);
    }

    private static Component[] plus(String id) {
        var b = start(id)
                .line(own(id, "purpose"))
                .line(shared("plus.parallels"), parallels(TierParallel.plus(GTValues.LV)),
                        value(GTValues.VN[GTValues.LV]), value(TierParallel.plus(GTValues.IV)),
                        value(GTValues.VN[GTValues.IV]));
        parallelRules(b).separator()
                .line(shared("plus.hatches"))
                .line(shared("switch_type"))
                .line(shared("overclock"))
                .hatches(Hatch.MULTI_AMP);
        return b.humor(shared("plus.fact")).mod(MOD_NAME).toArray();
    }

    private static Component[] rocketAssembler() {
        String id = "rocket_assembler";
        var b = start("rocket_assembler")
                .line(own(id, "purpose"))
                .separator()
                .table(rocket(own(id, "tier_1"), GTIARocketRecipes.TIER_1_EUT, GTIARocketRecipes.TIER_1_TICKS),
                        rocket(own(id, "tier_2"), GTIARocketRecipes.TIER_2_EUT, GTIARocketRecipes.TIER_2_TICKS))
                .separator()
                .line(own(id, "pad"), highlight("3 x 3"))
                .line(own(id, "hatches"))
                .line(shared("overclock"))
                .hatches(Hatch.MULTI_AMP);
        return finish(b, id);
    }

    private static TooltipRow rocket(Tr name, int eut, int ticks) {
        return TooltipRow.of(name, ChatFormatting.GOLD, shared("eut").component(eut),
                shared("seconds").component(ticks / 20));
    }

    private static Component[] fragmentCollector() {
        String id = "fragment_collector";
        int seconds = FragmentCollectorLogic.DURATION / 20;
        var b = start("miner")
                .line(own(id, "purpose"), highlight(own(id, "fragment")))
                .separator()
                .table(TooltipRow.of(own(id, "alone"), ChatFormatting.GOLD, FragmentCollectorLogic.OPERATIONS,
                        shared("seconds").component(seconds)).effect(own(id, "alone.effect")),
                        TooltipRow.of(own(id, "card"), ChatFormatting.GOLD, FragmentCollectorLogic.OPERATIONS,
                                shared("seconds").component(seconds)).effect(own(id, "card.effect")))
                .separator()
                .line(own(id, "keep"), emphasis(own(id, "never")),
                        value(FragmentCollectorLogic.DUPLICATE_CHANCE / 100 + "%"))
                .line(own(id, "hatches"))
                .line(shared("overclock"))
                .hatches(Hatch.MULTI_AMP);
        return finish(b, id);
    }

    private static Component[] largeFragmentCollector() {
        String id = "large_fragment_collector";
        var b = start("miner")
                .line(own(id, "purpose"), value(FragmentCollectorLogic.LARGE_PARALLEL),
                        value(GTValues.VN[GTValues.EV]))
                .line(own(id, "parallel"), highlight(shared("parallel_hatch.name")))
                .separator()
                .line(own(id, "hatches"))
                .line(shared("overclock"))
                .hatches(Hatch.MULTI_AMP);
        return finish(b, id);
    }

    /** "Megastructure n of Type 0: raises your team's Kardashev cap to 0.nn" for the first two. */
    private static MultiblockTooltipBuilder typeZero(String type, int number) {
        String cap = String.format(Locale.ROOT, "%.2f", KardashevScale.capHundredths(number) / 100.0);
        return start(type, "megastructure").line(shared("type_zero.mega"), value(number))
                .line(shared("type_zero.cap"), value(cap));
    }

    private static Component[] dispatchCenter() {
        String id = "national_dispatch_center";
        var b = typeZero("grid_dispatch", 1)
                .line(own(id, "purpose"), highlight(own(id, "transponder")))
                .separator()
                .line(shared("type_zero.hatches"))
                .line(shared("overclock"))
                .hatches(Hatch.MULTI_AMP);
        return finish(b, id);
    }

    private static Component[] cosmodrome() {
        String id = "cosmodrome";
        var b = typeZero("mission_control", 2)
                .line(own(id, "purpose"))
                .warning(own(id, "no_survey"))
                .separator()
                .line(own(id, "link"), value(CosmodromeMachine.LINK_RANGE))
                .line(shared("type_zero.hatches"))
                .line(shared("overclock"))
                .hatches(Hatch.MULTI_AMP);
        return finish(b, id);
    }

    private static Component[] tokamak() {
        String id = Tokamak.ID;
        var b = start("fusion_reactor", "generator", "megastructure")
                .line(shared("type_zero.mega"), value(3))
                .line(own(id, "closes"))
                .separator()
                .table(TooltipRow.of("D-T", ChatFormatting.LIGHT_PURPLE,
                        shared("eut").component(TypeZeroRecipes.DT_EUT))
                        .effect(own(id, "dt"), value(TypeZeroRecipes.TRITIUM_BRED),
                                value(TypeZeroRecipes.TRITIUM_BURNT)),
                        TooltipRow.of("D-He3", ChatFormatting.LIGHT_PURPLE,
                                shared("eut").component(TypeZeroRecipes.DHE3_EUT)).effect(own(id, "dhe3")))
                .line(own(id, "coolant"))
                .separator()
                .line(own(id, "start"), value(TokamakMachine.START_EU))
                .warning(own(id, "cools"), value(TokamakMachine.COOL_AFTER_TICKS / 20))
                .line(own(id, "dynamos"), value(1), value(Tokamak.MAX_DYNAMOS))
                .warning(own(id, "no_overclock"), emphasis(own(id, "no")));
        return finish(b, id);
    }
}
