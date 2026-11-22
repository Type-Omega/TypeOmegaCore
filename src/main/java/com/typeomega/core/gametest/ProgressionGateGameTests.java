package com.typeomega.core.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.GTIAMod;
import com.typeomega.core.route.AE2Gates;
import com.typeomega.core.route.ProgressionGates;

import java.util.ArrayList;
import java.util.List;

/** The restructuring gates: Eye of Ender in HV, portal only from a quest, AE2 in MV/HV (GTIA-D-347, D-349, D-352). */
@PrefixGameTestTemplate(false)
@GameTestHolder(GTIAMod.MOD_ID)
public final class ProgressionGateGameTests {

    private ProgressionGateGameTests() {}

    @GameTest(template = "qa_empty_5x5", batch = "gates", timeoutTicks = 20)
    public static void gatedRecipesAreReplaced(GameTestHelper h) {
        var manager = h.getLevel().getServer().getRecipeManager();
        List<ResourceLocation> removed = new ArrayList<>(ProgressionGates.ids());
        List<String> kept = new ArrayList<>(List.of("typeomega:assembler/ender_eye", "typeomega:assembler/nexus_flux_matrix"));
        if (ModList.get().isLoaded("ae2")) {
            removed.addAll(AE2Gates.ids());
            kept.addAll(List.of("typeomega:laser_engraver/ae2/printed_logic", "typeomega:circuit_assembler/ae2/logic_processor",
                    "typeomega:assembler/ae2/controller", "typeomega:assembler/ae2/drive",
                    "typeomega:assembler/ae2/molecular_assembler",
                    "typeomega:assembler/ae2/wireless_access_point", "typeomega:assembler/ae2/cell_component_16k"));
            // A block-to-part conversion stays: it is not a way around the gate.
            kept.add("ae2:network/blocks/pattern_providers_interface_alt");
        }
        var present = removed.stream().filter(id -> manager.byKey(id).isPresent()).toList();
        var missing = kept.stream().map(ResourceLocation::new).filter(id -> manager.byKey(id).isEmpty()).toList();
        h.assertTrue(present.isEmpty(), "gated recipes still loaded: " + present);
        h.assertTrue(missing.isEmpty(), "replacement recipes missing: " + missing);
        h.succeed();
    }
}
