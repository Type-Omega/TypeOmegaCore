package com.typeomega.core.data.tooltip;

import com.gregtechceu.gtceu.api.GTValues;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import com.typeomega.core.data.GTIATooltips;
import com.typeomega.core.kardashev.KardashevScale;
import com.typeomega.core.mega.typezero.CosmodromeMachine;
import com.typeomega.core.mega.typezero.Tokamak;
import com.typeomega.core.mega.typezero.TokamakMachine;
import com.typeomega.core.mega.typezero.TypeZeroRecipes;
import com.typeomega.lib.tooltip.MultiblockTooltipBuilder;
import com.typeomega.lib.tooltip.TooltipRow;

import java.util.List;
import java.util.Locale;

import static com.typeomega.core.data.GTIATooltips.finish;
import static com.typeomega.core.data.GTIATooltips.own;
import static com.typeomega.core.data.GTIATooltips.shared;
import static com.typeomega.core.data.GTIATooltips.start;
import static com.typeomega.lib.tooltip.TooltipArg.emphasis;
import static com.typeomega.lib.tooltip.TooltipArg.highlight;
import static com.typeomega.lib.tooltip.TooltipArg.value;

/** The three megastructures of Type 0: National Dispatch Center, Cosmodrome and Tokamak. */
public final class TypeZeroTooltips {

    private TypeZeroTooltips() {}

    public static void register() {
        GTIATooltips.register("national_dispatch_center", TypeZeroTooltips::dispatchCenter);
        GTIATooltips.register("cosmodrome", TypeZeroTooltips::cosmodrome);
        GTIATooltips.register(Tokamak.ID, TypeZeroTooltips::tokamak);
    }

    /** "Megastructure n of Type 0: raises your team's Kardashev cap to 0.nn". */
    private static MultiblockTooltipBuilder typeZero(int number, String... types) {
        String cap = String.format(Locale.ROOT, "%.2f", KardashevScale.capHundredths(number) / 100.0);
        return start(types).line(shared("type_zero.mega"), value(number))
                .line(shared("type_zero.cap"), value(cap));
    }

    private static List<Component> dispatchCenter() {
        String id = "national_dispatch_center";
        var b = typeZero(1, "grid_dispatch", "megastructure")
                .line(own(id, "purpose"), highlight(own(id, "transponder")))
                .separator()
                .line(own(id, "recipe"), value(GTValues.VA[GTValues.HV]), value(GTValues.VN[GTValues.HV]))
                .warning(own(id, "gate"))
                .line(shared("overclock"))
                .line(shared("overclock.rule"), value("x2"), value("x4"));
        return finish(b, id);
    }

    private static List<Component> cosmodrome() {
        String id = "cosmodrome";
        var b = typeZero(2, "mission_control", "megastructure")
                .line(own(id, "purpose"))
                .warning(own(id, "no_survey"))
                .separator()
                .line(own(id, "link"), value(CosmodromeMachine.LINK_RANGE))
                .line(shared("overclock"))
                .line(shared("overclock.rule"), value("x2"), value("x4"));
        return finish(b, id);
    }

    private static List<Component> tokamak() {
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
                .line(own(id, "cycle"), value(TypeZeroRecipes.FUSION_TICKS / 20))
                .line(own(id, "coolant"))
                .separator()
                .line(own(id, "start"), value(Fmt.num(TokamakMachine.START_EU)))
                .warning(own(id, "cools"), value(TokamakMachine.COOL_AFTER_TICKS / 20))
                .line(own(id, "dynamos"), value(1), value(Tokamak.MAX_DYNAMOS))
                .warning(own(id, "no_overclock"), emphasis(own(id, "no")));
        return finish(b, id);
    }
}
