package com.typeomega.core.integration.ae.requestable;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.machines.GTAEMachines;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.chat.Component;

import appeng.core.definitions.AEItems;
import com.typeomega.core.TypeOmegaMod;

import java.util.function.Consumer;

/**
 * ME Requestable Input Bus and Hatch (D-442): the GTCEu ME input parts that also ask the network to craft what it
 * lacks. They sit one step above the plain ME parts (EV machine, HV assembler recipe) at the tier of the stocking
 * parts' recipe: IV machines, made in the IV Assembler from the plain part, an AE2 Crafting Card and IV motion parts.
 */
public final class RequestableParts {

    public static final String BUS_ID = "me_requestable_input_bus";
    public static final String HATCH_ID = "me_requestable_input_hatch";

    private static final String TOOLTIP_BUS = "typeomega.machine.me_requestable_input_bus.tooltip";
    private static final String TOOLTIP_HATCH = "typeomega.machine.me_requestable_input_hatch.tooltip";
    private static final String TOOLTIP_CRAFT = "typeomega.machine.me_requestable.craft";

    public static MachineDefinition bus;
    public static MachineDefinition hatch;

    private RequestableParts() {}

    public static void initLang() {
        TypeOmegaMod.REGISTRATE.addRawLang(TOOLTIP_BUS,
                "Pulls the configured items from the ME network and asks the network to craft what it lacks");
        TypeOmegaMod.REGISTRATE.addRawLang(TOOLTIP_HATCH,
                "Pulls the configured fluids from the ME network and asks the network to craft what it lacks");
        TypeOmegaMod.REGISTRATE.addRawLang(TOOLTIP_CRAFT,
                "A stack with a pattern is crafted for the missing amount, one job per slot; crafted items arrive directly");
    }

    public static void init() {
        bus = TypeOmegaMod.REGISTRATE
                .machine(BUS_ID, MERequestableInputBusMachine::new)
                .langValue("ME Requestable Input Bus")
                .tier(GTValues.IV)
                .rotationState(RotationState.ALL)
                .abilities(PartAbility.IMPORT_ITEMS)
                .colorOverlayTieredHullModel(TypeOmegaMod.id("block/overlay/machine/" + BUS_ID))
                .tooltips(Component.translatable("gtceu.machine.item_bus.import.tooltip"),
                        Component.translatable(TOOLTIP_BUS),
                        Component.translatable(TOOLTIP_CRAFT),
                        Component.translatable("gtceu.machine.me.copy_paste.tooltip"),
                        Component.translatable("gtceu.part_sharing.enabled"))
                .register();
        hatch = TypeOmegaMod.REGISTRATE
                .machine(HATCH_ID, MERequestableInputHatchMachine::new)
                .langValue("ME Requestable Input Hatch")
                .tier(GTValues.IV)
                .rotationState(RotationState.ALL)
                .abilities(PartAbility.IMPORT_FLUIDS)
                .colorOverlayTieredHullModel(TypeOmegaMod.id("block/overlay/machine/" + HATCH_ID))
                .tooltips(Component.translatable("gtceu.machine.fluid_hatch.import.tooltip"),
                        Component.translatable(TOOLTIP_HATCH),
                        Component.translatable(TOOLTIP_CRAFT),
                        Component.translatable("gtceu.machine.me.copy_paste.tooltip"),
                        Component.translatable("gtceu.part_sharing.enabled"))
                .register();
    }

    public static void addRecipes(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(TypeOmegaMod.id(BUS_ID))
                .inputItems(GTAEMachines.ITEM_IMPORT_BUS_ME)
                .inputItems(AEItems.CRAFTING_CARD.stack())
                .inputItems(GTItems.ROBOT_ARM_IV.asStack(2))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(1296))
                .outputItems(bus)
                .duration(300).EUt(GTValues.VA[GTValues.IV])
                .save(provider);
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(TypeOmegaMod.id(HATCH_ID))
                .inputItems(GTAEMachines.FLUID_IMPORT_HATCH_ME)
                .inputItems(AEItems.CRAFTING_CARD.stack())
                .inputItems(GTItems.ELECTRIC_PUMP_IV.asStack(2))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(1296))
                .outputItems(hatch)
                .duration(300).EUt(GTValues.VA[GTValues.IV])
                .save(provider);
    }
}
