package com.typeomega.core.gametest;

import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.registry.GTRegistries;

import net.minecraft.ChatFormatting;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.data.TypeOmegaTooltips;
import com.typeomega.core.data.tooltip.Fmt;
import com.typeomega.core.mega.kugelblitz.KugelblitzPhysics;
import com.typeomega.core.mega.matrioshka.MatrioshkaShells;
import com.typeomega.core.mega.yard.YardPhysics;
import com.typeomega.core.processing.TierParallel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Multiblock tooltips with the whole mechanic (D-432): every core multiblock has one, with enough mechanic lines,
 * a humour line and the mod name, without the structure section or the pollution line (D-431), and the numbers
 * shown are the ones the code uses.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class MechanicTooltipGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";
    /** Mechanic lines at least: megastructures explain more rules than the factories. */
    private static final int MIN_MEGA = 9, MIN_FACTORY = 4;

    private MechanicTooltipGameTests() {}

    /** The megastructures of the later eras share the five-phase line; Type 0 ones are single structures. */
    private static boolean mega(List<Component> lines) {
        return lines.stream().anyMatch(l -> l.getContents() instanceof TranslatableContents t &&
                t.getKey().equals("typeomega.tooltip.mega.phases"));
    }

    private static boolean unformatted(String line) {
        return line.contains("%s") || line.contains("%%") || line.contains("typeomega.machine.") ||
                line.contains("typeomega.tooltip.");
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 200)
    public static void everyMultiblockHasTheFullMechanicTooltip(GameTestHelper h) {
        Set<String> ids = Set.copyOf(TypeOmegaTooltips.ids());
        List<String> missing = new ArrayList<>(), wrong = new ArrayList<>();
        for (var definition : GTRegistries.MACHINES) {
            if (!TypeOmegaMod.MOD_ID.equals(definition.getId().getNamespace()) ||
                    !(definition instanceof MultiblockMachineDefinition)) {
                continue;
            }
            String id = definition.getId().getPath();
            if (!ids.contains(id)) {
                missing.add(id);
                continue;
            }
            List<Component> lines = TypeOmegaTooltips.build(id);
            long mechanic = lines.stream().filter(l -> l.getStyle().getColor() != null &&
                    (l.getStyle().getColor().equals(net.minecraft.network.chat.TextColor.fromLegacyFormat(
                            ChatFormatting.GRAY)) ||
                            l.getStyle().getColor().equals(net.minecraft.network.chat.TextColor.fromLegacyFormat(
                                    ChatFormatting.RED))))
                    .count() - 1;
            int min = mega(lines) ? MIN_MEGA : MIN_FACTORY;
            boolean humor = lines.stream().anyMatch(l -> l.getStyle().isItalic() &&
                    l.getContents() instanceof TranslatableContents t && t.getKey().endsWith(".humor"));
            boolean structure = lines.stream().anyMatch(l -> l.getContents() instanceof TranslatableContents t &&
                    (t.getKey().startsWith("typeomegalib.tooltip.structure") ||
                            t.getKey().equals("typeomegalib.tooltip.pollution")));
            String unformatted = lines.stream().map(Component::getString).filter(MechanicTooltipGameTests::unformatted)
                    .collect(Collectors.joining(" | "));
            if (mechanic < min || !humor || structure || !unformatted.isEmpty() ||
                    !TypeOmegaTooltips.MOD_NAME.equals(lines.get(lines.size() - 1).getString())) {
                wrong.add(id + " (" + mechanic + " lines, humor " + humor + ", structure " + structure +
                        ", unformatted [" + unformatted + "])");
            }
        }
        h.assertTrue(missing.isEmpty(), "multiblocks without a mechanic tooltip: " + missing);
        h.assertTrue(wrong.isEmpty(), "incomplete tooltips: " + wrong);
        h.succeed();
    }

    /** A few numbers the tooltips show, against the constants the logic reads. */
    @GameTest(template = TEMPLATE, timeoutTicks = 100)
    public static void tooltipNumbersComeFromTheCode(GameTestHelper h) {
        expect(h, "kugelblitz", Fmt.pow2(KugelblitzPhysics.hawkingEut(1e6)));
        expect(h, "kugelblitz", Fmt.pct(KugelblitzPhysics.CATCH));
        expect(h, "assembly_yard", String.valueOf(YardPhysics.slipways(5)));
        expect(h, "matrioshka_brain", Fmt.pow2(MatrioshkaShells.shellCwut(5, MatrioshkaShells.T_REF, 100)));
        expect(h, "large_furnace", TierParallel.largeFurnace(com.gregtechceu.gtceu.api.GTValues.LV) + " parallels");
        expect(h, "disintegrator", "Platinum");
        h.succeed();
    }

    private static void expect(GameTestHelper h, String id, String text) {
        String tooltip = TypeOmegaTooltips.build(id).stream().map(Component::getString)
                .collect(Collectors.joining("\n"));
        h.assertTrue(tooltip.contains(text), id + " tooltip does not show " + text + ":\n" + tooltip);
    }
}
