package com.typeomega.core.kardashev;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.mega.typezero.Cosmodrome;
import com.typeomega.core.mega.typezero.NationalDispatchCenter;
import com.typeomega.core.mega.typezero.Tokamak;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The texts of the Kardashev Meter screen and HUD (D-443). Every line is a translatable component with the numbers as
 * components too, so each player reads them in his own language and with his own decimal mark. The English text is
 * declared here (datagen writes en_us); pt_br and zh_cn are in the lang files with the same keys.
 */
public final class KardashevUiText {

    private static final String UI = KardashevText.PREFIX + "ui.";
    private static final String[] PREFIXES = { "", "k", "M", "G", "T", "P", "E", "Z", "Y" };
    private static final String[] MEGAS_TYPE_ZERO = { NationalDispatchCenter.ID, Cosmodrome.ID, Tokamak.ID };

    private KardashevUiText() {}

    public static void registerLang() {
        en("tab.summary", "Summary");
        en("tab.history", "History");
        en("tab.sources", "Sources");
        en("hud.on", "HUD: ON");
        en("hud.off", "HUD: OFF");
        en("hud.tip", "Left click: turn the HUD on or off. Right click: move it on the screen.");
        en("hud.name", "Kardashev Meter");
        en("hud.cap", "cap %s");
        en("hud.flow", "+%s  -%s");
        en("hud.next", "Next: Type %s (+%s K · %s/3 megas)");
        en("hud.last", "Last type reached");
        en("unit.eus", "%s %sEU/s");
        en("unit.eut", "%s EU/t");
        en("unit.watts", "%s %sW");
        en("summary.progress", "To Type %s");
        en("summary.generated", "Generated (last second): +%s");
        en("summary.spent", "Spent (last second): -%s");
        en("summary.net", "Net: %s");
        en("summary.watts", "Equivalent: %s · Sagan K %s (1 EU/t = 1 TW, only an equivalence)");
        en("summary.next", "Next: Type %s at K %s, %s");
        en("summary.gap", "Missing +%s K · %s times your 10-minute average");
        en("summary.gap_unknown", "Missing +%s K · no power measured yet");
        en("summary.megas", "Megastructures of Type %s: %s/3");
        en("summary.last", "The last type is reached.");
        en("sources.generated", "Generated per second (average of 20 s)");
        en("sources.spent", "Spent per second (average of 20 s)");
        en("sources.row", "%s: %s (%s%%)");
        en("sources.row_spent", "%s: %s");
        en("source.wireless_energy", "Wireless energy network");
        en("source.wireless_steam", "Wireless steam network");
        en("source.boiler", "Steam boilers");
        en("source.generator", "Local generators");
        en("sources.counts", "Counts: wireless networks, boilers, generators (with the Tokamak) and megastructures.");
        en("sources.once", "Energy sent through a Wireless Dynamo Hatch counts once, in the wireless network.");
        en("sources.megas", "Megastructures counted: %s/%s");
        en("sources.mega_done", "[x] %s");
        en("sources.mega_open", "[ ] %s");
        en("graph.window.0", "10 min");
        en("graph.window.1", "1 h");
        en("graph.window.2", "6 h");
        en("graph.generated", "Generated");
        en("graph.spent", "Spent");
        en("graph.now", "now");
        en("graph.average", "Average: +%s generated · -%s spent");
        en("graph.peak", "Peak: +%s generated · -%s spent");
        en("graph.empty", "No energy reported in this window yet.");
    }

    private static void en(String key, String english) {
        TypeOmegaMod.REGISTRATE.addRawLang(UI + key, english);
    }

    public static MutableComponent tr(String key, Object... args) {
        return Component.translatable(UI + key, args);
    }

    // ------------------------------------------------------------------------------------------ numbers

    /** A number with {@code decimals} digits and the decimal mark of the reader's language. */
    public static MutableComponent number(double value, int decimals) {
        if (decimals <= 0) return Component.literal(String.format(Locale.ROOT, "%,d", Math.round(value)));
        double scale = Math.pow(10, decimals);
        long scaled = Math.round(Math.abs(value) * scale);
        String fraction = String.format(Locale.ROOT, "%0" + decimals + "d", scaled % (long) scale);
        MutableComponent whole = KardashevText.tr("decimal", (value < 0 ? "-" : "") + scaled / (long) scale, fraction);
        return whole;
    }

    private static MutableComponent scientific(double value) {
        String[] parts = String.format(Locale.ROOT, "%.2e", value).split("e");
        return Component.literal(parts[0] + "×10^" + Integer.parseInt(parts[1]));
    }

    /** EU/s with an SI prefix: 62 000 is "62.0 kEU/s"; past 10^27 in scientific form. */
    public static MutableComponent power(double euPerSecond) {
        double abs = Math.abs(euPerSecond);
        if (abs < 1000) return tr("unit.eus", number(euPerSecond, abs < 10 && abs > 0 ? 1 : 0), "");
        int step = (int) Math.floor(Math.log10(abs) / 3 + 1e-12);
        if (step >= PREFIXES.length) return tr("unit.eus", scientific(euPerSecond), "");
        return tr("unit.eus", number(euPerSecond / Math.pow(10, 3 * step), 1), PREFIXES[step]);
    }

    /** Signed: "+62.0 kEU/s" or "-3.0 kEU/s". */
    public static MutableComponent signedPower(double euPerSecond) {
        MutableComponent text = power(Math.abs(euPerSecond));
        return (euPerSecond < 0 ? Component.literal("-") : Component.literal("+")).append(text);
    }

    public static MutableComponent euPerTick(double euPerTick) {
        return tr("unit.eut", euPerTick < 1e15 ? Component.literal(KardashevText.eu(euPerTick)) :
                scientific(euPerTick));
    }

    public static MutableComponent watts(double watts) {
        if (watts < 1000) return tr("unit.watts", number(watts, 0), "");
        int step = (int) Math.floor(Math.log10(watts) / 3 + 1e-12);
        if (step >= PREFIXES.length) return tr("unit.watts", scientific(watts), "");
        return tr("unit.watts", number(watts / Math.pow(10, 3 * step), 2), PREFIXES[step]);
    }

    // ----------------------------------------------------------------------------------------- summary

    /** The line above the progress bar of the Summary tab. */
    public static Component headline(KardashevView view) {
        int k = view.reading().kHundredths();
        return KardashevText.tr("k", KardashevText.decimal(k).withStyle(ChatFormatting.DARK_BLUE, ChatFormatting.BOLD),
                KardashevText.type(k / 100));
    }

    /** How far the K is inside its type, 0 to 1: the fill of the progress bar. */
    public static double typeProgress(KardashevView view) {
        return (view.reading().kHundredths() % 100) / 100.0;
    }

    public static Component progressLabel(KardashevView view) {
        var next = nextOf(view);
        return next.last() ? tr("summary.last") : tr("summary.progress", KardashevText.type(next.nextType()));
    }

    /** The lines under the bar: cap, power, flow, equivalent watts and what is left for the next type. */
    public static List<Component> summary(KardashevView view) {
        var reading = view.reading();
        var next = nextOf(view);
        List<Component> lines = new ArrayList<>();
        int capType = KardashevScale.capType(reading.megastructures());
        lines.add(KardashevText.tr("cap", KardashevText.decimal(reading.capHundredths()),
                KardashevText.type(capType), next.megasInType()));
        lines.add(reading.capped() ? KardashevText.tr("capped") : Component.empty());
        lines.add(KardashevText.tr("average", KardashevText.eu(reading.averageEuPerTick())));
        lines.add(tr("summary.generated", power(view.generatedPerSecond())));
        lines.add(tr("summary.spent", power(view.spentPerSecond())));
        lines.add(tr("summary.net", signedPower(view.generatedPerSecond() - view.spentPerSecond())));
        double watts = KardashevScale.watts(reading.averageEuPerTick());
        lines.add(tr("summary.watts", watts(watts), number(KardashevScale.saganK(reading.averageEuPerTick()), 2)));
        lines.add(Component.empty());
        if (next.last()) {
            lines.add(tr("summary.last"));
            return lines;
        }
        lines.add(tr("summary.next", KardashevText.type(next.nextType()), KardashevText.decimal(next.nextType() * 100),
                euPerTick(next.euPerTickNeeded())));
        MutableComponent gap = next.multiplier() > 0 ?
                tr("summary.gap", number(next.gapHundredths() / 100.0, 2), number(next.multiplier(), 1)) :
                tr("summary.gap_unknown", number(next.gapHundredths() / 100.0, 2));
        lines.add(gap);
        lines.add(tr("summary.megas", KardashevText.type(next.type()), next.megasInType()));
        return lines;
    }

    private static KardashevNext nextOf(KardashevView view) {
        var reading = view.reading();
        return KardashevNext.of(reading.kHundredths(), reading.megastructures(), reading.averageEuPerTick());
    }

    // ----------------------------------------------------------------------------------------- sources

    /** Share of {@code source} in the generation, 0 to 1. */
    public static double share(KardashevView view, KardashevSource source) {
        double total = view.generatedAverage();
        return total > 0 ? view.average20()[source.ordinal()] / total : 0;
    }

    public static Component sourceName(KardashevSource source) {
        return tr("source." + (source == KardashevSource.SPENT_ENERGY ? "wireless_energy" :
                source == KardashevSource.SPENT_STEAM ? "wireless_steam" : source.id()));
    }

    /** One generation row of the Sources tab. */
    public static Component sourceRow(KardashevView view, KardashevSource source) {
        double average = view.average20()[source.ordinal()];
        return tr("sources.row", sourceName(source), power(average), number(share(view, source) * 100, 0));
    }

    /** The lines of the Sources tab after the generation bars: spending, the rule and the megastructures. */
    public static List<Component> sourcesSpent(KardashevView view) {
        List<Component> lines = new ArrayList<>();
        lines.add(tr("sources.spent"));
        for (int i = KardashevSource.GENERATED; i < KardashevSource.COUNT; i++) {
            var source = KardashevSource.values()[i];
            lines.add(tr("sources.row_spent", sourceName(source), power(view.average20()[i])));
        }
        return lines;
    }

    public static List<Component> sourcesNotes(KardashevView view) {
        List<Component> lines = new ArrayList<>();
        lines.add(tr("sources.counts"));
        lines.add(tr("sources.once"));
        lines.add(tr("sources.megas", view.reading().megastructures(), KardashevScale.MAX_MEGASTRUCTURES));
        for (String id : MEGAS_TYPE_ZERO) {
            boolean done = view.built().contains(id);
            lines.add(tr(done ? "sources.mega_done" : "sources.mega_open",
                    Component.translatable("block.typeomega." + id)));
        }
        return lines;
    }

    // --------------------------------------------------------------------------------------------- HUD

    /** The three lines of the HUD: K and type, generated and spent, and the next type. */
    public static List<Component> hud(KardashevHudStats stats) {
        var next = KardashevNext.of(stats.kHundredths(), stats.megastructures(), 0);
        MutableComponent first = KardashevText.tr("k", KardashevText.decimal(stats.kHundredths())
                .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD), KardashevText.type(stats.kHundredths() / 100));
        if (stats.capped()) {
            first.append(" ").append(tr("hud.cap", KardashevText.decimal(stats.capHundredths()))
                    .withStyle(ChatFormatting.GOLD));
        }
        MutableComponent flow = tr("hud.flow", power(stats.generatedPerSecond()).withStyle(ChatFormatting.GREEN),
                power(stats.spentPerSecond()).withStyle(ChatFormatting.RED));
        Component third = next.last() ? tr("hud.last") : tr("hud.next", KardashevText.type(next.nextType()),
                number(next.gapHundredths() / 100.0, 2), next.megasInType());
        return List.of(first, flow, third);
    }
}
