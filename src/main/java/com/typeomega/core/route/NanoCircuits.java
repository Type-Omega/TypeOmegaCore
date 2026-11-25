package com.typeomega.core.route;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import com.typeomega.core.TypeOmegaMod;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * The nano line in the HV Circuit Assembler with a Cleanroom (D-421, D-424): the same inputs as GTCEu at
 * 480 EU/t, with the time stretched so each craft costs the same energy. The GTCEu recipes at 600, 1200 and 1920 EU/t
 * take the same parts, so GTCEu's lookup refuses one of each pair as a conflict; they leave, and an EV Circuit
 * Assembler still runs the HV copies faster by overclocking. The ASMD and SoC variants stay.
 */
public final class NanoCircuits {

    static final int EUT = GTValues.VA[GTValues.HV];

    /** Ids of the HV copies, for the GameTests. */
    public static final List<String> IDS = List.of("circuit_assembler/nano/processor",
            "circuit_assembler/nano/processor_assembly", "circuit_assembler/nano/computer",
            "circuit_assembler/nano/mainframe", "circuit_assembler/nano/data_orb");

    static final List<String> REMOVED = List.of("nano_processor_hv", "nano_processor_assembly_ev", "nano_computer_iv",
            "nano_mainframe_luv", "data_orb");

    private NanoCircuits() {}

    public static void removeRecipes(Consumer<ResourceLocation> consumer) {
        ids().forEach(consumer);
    }

    /** The GTCEu recipes the HV copies replace, with the soldering alloy variant of each. */
    public static List<ResourceLocation> ids() {
        return REMOVED.stream().flatMap(id -> Stream.of(id, id + "_soldering_alloy"))
                .map(id -> new ResourceLocation("gtceu", "circuit_assembler/" + id)).toList();
    }

    /** Ticks at 480 EU/t that spend the same energy as the GTCEu recipe. */
    static int stretched(int ticks, int eut) {
        return (int) Math.ceil((double) ticks * eut / EUT);
    }

    public static void addRecipes(Consumer<FinishedRecipe> provider) {
        int processors = ConfigHolder.INSTANCE.recipes.harderCircuitRecipes ? 1 : 2;
        GTRecipeTypes.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder(TypeOmegaMod.id("nano/processor"))
                .inputItems(GTItems.ADVANCED_CIRCUIT_BOARD).inputItems(GTItems.NANO_CENTRAL_PROCESSING_UNIT)
                .inputItems(GTItems.SMD_RESISTOR, 8).inputItems(GTItems.SMD_CAPACITOR, 8)
                .inputItems(GTItems.SMD_TRANSISTOR, 8).inputItems(TagPrefix.wireFine, GTMaterials.Electrum, 8)
                .outputItems(GTItems.NANO_PROCESSOR_HV, processors)
                .cleanroom(CleanroomType.CLEANROOM).duration(stretched(200, 600)).EUt(EUT).save(provider);
        GTRecipeTypes.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder(TypeOmegaMod.id("nano/processor_assembly"))
                .inputItems(GTItems.ADVANCED_CIRCUIT_BOARD).inputItems(GTItems.NANO_PROCESSOR_HV, 2)
                .inputItems(GTItems.SMD_INDUCTOR, 4).inputItems(GTItems.SMD_CAPACITOR, 8)
                .inputItems(GTItems.RANDOM_ACCESS_MEMORY, 8).inputItems(TagPrefix.wireFine, GTMaterials.Electrum, 16)
                .outputItems(GTItems.NANO_PROCESSOR_ASSEMBLY_EV, 2).solderMultiplier(2)
                .cleanroom(CleanroomType.CLEANROOM).duration(stretched(400, 600)).EUt(EUT).save(provider);
        // EN: The Nano Mainframe takes two supercomputers, so they also come down to HV, or the mainframe of D-424
        // would still wait for EV power.
        // PT: O Mainframe Nano pede dois supercomputadores, então eles também descem para o HV; senão o mainframe da
        // D-424 continuaria esperando energia EV.
        // ZH: 纳米主机需要两台超级计算机，因此它们也降到 HV，否则 D-424 的主机仍要等待 EV 电力。
        GTRecipeTypes.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder(TypeOmegaMod.id("nano/computer"))
                .inputItems(GTItems.ADVANCED_CIRCUIT_BOARD).inputItems(GTItems.NANO_PROCESSOR_ASSEMBLY_EV, 2)
                .inputItems(GTItems.SMD_DIODE, 8).inputItems(GTItems.NOR_MEMORY_CHIP, 4)
                .inputItems(GTItems.RANDOM_ACCESS_MEMORY, 16).inputItems(TagPrefix.wireFine, GTMaterials.Electrum, 16)
                .outputItems(GTItems.NANO_COMPUTER_IV).solderMultiplier(2)
                .cleanroom(CleanroomType.CLEANROOM).duration(stretched(400, 600)).EUt(EUT).save(provider);
        // EN: 1920 -> 480 EU/t is four times the time (D-424).
        // PT: 1920 -> 480 EU/t são quatro vezes o tempo (D-424).
        // ZH: 1920 -> 480 EU/t 即四倍时间（D-424）。
        GTRecipeTypes.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder(TypeOmegaMod.id("nano/mainframe"))
                .inputItems(TagPrefix.frameGt, GTMaterials.Aluminium, 2).inputItems(GTItems.NANO_COMPUTER_IV, 2)
                .inputItems(GTItems.SMD_INDUCTOR, 16).inputItems(GTItems.SMD_CAPACITOR, 32)
                .inputItems(GTItems.RANDOM_ACCESS_MEMORY, 16)
                .inputItems(TagPrefix.wireGtSingle, GTMaterials.AnnealedCopper, 32)
                .outputItems(GTItems.NANO_MAINFRAME_LuV).solderMultiplier(4)
                .cleanroom(CleanroomType.CLEANROOM).duration(stretched(800, GTValues.VA[GTValues.EV])).EUt(EUT)
                .save(provider);
        GTRecipeTypes.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder(TypeOmegaMod.id("nano/data_orb"))
                .inputItems(GTItems.ADVANCED_CIRCUIT_BOARD).inputItems(CustomTags.HV_CIRCUITS, 2)
                .inputItems(GTItems.RANDOM_ACCESS_MEMORY, 4).inputItems(GTItems.NOR_MEMORY_CHIP, 32)
                .inputItems(GTItems.NAND_MEMORY_CHIP, 64).inputItems(TagPrefix.wireFine, GTMaterials.Platinum, 32)
                .outputItems(GTItems.TOOL_DATA_ORB).solderMultiplier(2)
                .cleanroom(CleanroomType.CLEANROOM).duration(stretched(400, 1200)).EUt(EUT).save(provider);
    }
}
