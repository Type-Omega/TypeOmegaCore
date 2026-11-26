package com.typeomega.core.kardashev;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import com.typeomega.core.TypeOmegaMod;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Texts of the Kardashev Meter: English here, pt_br and zh_cn in the lang files. */
public final class KardashevText {

    static final String PREFIX = "typeomega.kardashev.";
    private static final String[] TYPES = { "0", "I", "II", "III", "IV", "Ω" };

    private KardashevText() {}

    public static void registerLang() {
        KardashevUiText.registerLang();
        lang("decimal", "%s.%s");
        lang("k", "K %s · Type %s");
        lang("average", "Average power (10 min): %s EU/t");
        lang("cap", "Cap: K %s · megastructures of Type %s: %s/3");
        lang("capped", "Your power is above the cap: the next megastructure raises it.");
        lang("tooltip.reads", "Shows the K of your team: the average power of the last %s.");
        lang("tooltip.sources", "Counts the %s, the %s, the steam boilers and the local generators.");
        lang("tooltip.wireless_energy", "wireless energy network");
        lang("tooltip.wireless_steam", "wireless steam network");
        lang("tooltip.steam", "Steam counts at %s.");
        lang("tooltip.cap", "Each type caps K at %s, %s and %s with 0, 1 and 2 megastructures.");
        lang("tooltip.use", "Right-click to open the screen: summary, history and sources, with an optional HUD.");
    }

    private static void lang(String key, String english) {
        TypeOmegaMod.REGISTRATE.addRawLang(PREFIX + key, english);
    }

    static MutableComponent tr(String key, Object... args) {
        return Component.translatable(PREFIX + key, args);
    }

    /** K in hundredths as "0.42" (en, zh) or "0,42" (pt), always two decimals. */
    public static MutableComponent decimal(int hundredths) {
        return tr("decimal", hundredths / 100, String.format(Locale.ROOT, "%02d", hundredths % 100));
    }

    static String type(int type) {
        return TYPES[Math.max(0, Math.min(TYPES.length - 1, type))];
    }

    static String eu(double euPerTick) {
        if (euPerTick < 1e15) return NumberFormat.getIntegerInstance(Locale.ROOT).format(Math.round(euPerTick));
        return String.format(Locale.ROOT, "%.3e", euPerTick);
    }

    public static List<Component> tooltip() {
        List<Component> lines = new ArrayList<>();
        lines.add(tr("tooltip.reads", value("10 min")).withStyle(ChatFormatting.GRAY));
        lines.add(tr("tooltip.sources", highlight(tr("tooltip.wireless_energy")),
                highlight(tr("tooltip.wireless_steam"))).withStyle(ChatFormatting.GRAY));
        lines.add(tr("tooltip.steam", value((int) KardashevScale.STEAM_MB_PER_EU + " mB = 1 EU"))
                .withStyle(ChatFormatting.GRAY));
        lines.add(tr("tooltip.cap", value(decimal(KardashevScale.capHundredths(0))),
                value(decimal(KardashevScale.capHundredths(1))), value(decimal(KardashevScale.capHundredths(2))))
                .withStyle(ChatFormatting.GRAY));
        lines.add(tr("tooltip.use").withStyle(ChatFormatting.DARK_GRAY));
        lines.add(Component.literal(com.typeomega.core.data.TypeOmegaTooltips.MOD_NAME).withStyle(ChatFormatting.BLUE,
                ChatFormatting.ITALIC));
        return lines;
    }

    private static MutableComponent value(Object value) {
        return (value instanceof Component c ? c.copy() : Component.literal(String.valueOf(value)))
                .withStyle(ChatFormatting.AQUA);
    }

    private static MutableComponent highlight(Component text) {
        return text.copy().withStyle(ChatFormatting.WHITE);
    }
}
