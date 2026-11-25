package com.typeomega.core.data;

import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.registry.GTRegistries;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.mojang.logging.LogUtils;
import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.data.tooltip.MegaTooltips;
import com.typeomega.core.data.tooltip.ProcessingTooltips;
import com.typeomega.core.data.tooltip.StellarTooltips;
import com.typeomega.core.data.tooltip.TypeZeroTooltips;
import com.typeomega.core.mega.common.MegaLang;
import com.typeomega.lib.lang.Tr;
import com.typeomega.lib.tooltip.MultiblockTooltip;
import com.typeomega.lib.tooltip.MultiblockTooltipBuilder;
import com.typeomega.lib.tooltip.StructureInfo;
import com.typeomega.lib.tooltip.structure.GtceuPatterns;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Multiblock tooltips in the GTNH layout of the lib (D-410) with the whole mechanic of each machine (D-432):
 * every number comes from the constant the machine logic uses. The texts, in three languages, are in
 * {@code mega_lang/multiblock_tooltips.json} and {@code mega_lang/mega_tooltips.json}. No structure section and no
 * pollution line (D-431). Families live in {@code data/tooltip/}; the tooltip is built when it is shown, so
 * the language is current.
 */
public final class TypeOmegaTooltips {

    /** Shown in blue italics on the last line: the displayName of mods.toml ({@code mod_name}). */
    public static final String MOD_NAME = "TypeOmega Core";

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<String, Supplier<List<Component>>> TOOLTIPS = new LinkedHashMap<>();
    private static final Set<String> UNREADABLE = ConcurrentHashMap.newKeySet();
    private static boolean initialised;

    private TypeOmegaTooltips() {}

    private static synchronized void init() {
        if (initialised) return;
        initialised = true;
        ProcessingTooltips.register();
        TypeZeroTooltips.register();
        StellarTooltips.register();
        MegaTooltips.register();
    }

    public static void register(String id, Supplier<List<Component>> tooltip) {
        if (TOOLTIPS.put(id, tooltip) != null) throw new IllegalStateException(id + " tooltip registered twice");
    }

    /** Registry paths of the multiblocks with a tooltip here. */
    public static List<String> ids() {
        init();
        return List.copyOf(TOOLTIPS.keySet());
    }

    public static List<Component> build(String id) {
        init();
        Supplier<List<Component>> tooltip = TOOLTIPS.get(id);
        if (tooltip == null) throw new IllegalArgumentException("no tooltip for " + id);
        return tooltip.get();
    }

    /** For {@code MultiblockMachineBuilder.tooltipBuilder}: the tooltip of {@code id}, built when it is shown. */
    public static BiConsumer<ItemStack, List<Component>> lazy(String id) {
        return (stack, lines) -> lines.addAll(build(id));
    }

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

    /** The modes and the "Supports ...!" line read from the machine, then the humour line and the mod name. */
    public static List<Component> finish(MultiblockTooltipBuilder builder, String id) {
        if (GTRegistries.MACHINES.get(TypeOmegaMod.id(id)) instanceof MultiblockMachineDefinition definition) {
            builder.modes(GtceuPatterns.modes(definition));
            StructureInfo info = structure(definition);
            // EN: Only the "Supports" line is read from the pattern; the structure section is off (D-431).
            // PT: Só a linha "Suporta" vem do padrão; a seção de estrutura está desligada (D-431).
            // ZH: 只从结构模式读取“支持”一行；结构部分已关闭（D-431）。
            if (info != null) builder.hatches(info);
        }
        return builder.humor(own(id, "humor")).mod(MOD_NAME).build();
    }

    private static StructureInfo structure(MultiblockMachineDefinition definition) {
        String id = definition.getId().toString();
        if (UNREADABLE.contains(id)) return null;
        try {
            return GtceuPatterns.of(definition);
        } catch (RuntimeException e) {
            if (UNREADABLE.add(id)) LOGGER.warn("Cannot read the structure of {} for its tooltip", id, e);
            return null;
        }
    }
}
