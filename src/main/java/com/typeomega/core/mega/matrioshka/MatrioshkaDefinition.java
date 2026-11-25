package com.typeomega.core.mega.matrioshka;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.data.TypeOmegaTooltips;
import com.typeomega.core.mega.common.MegaPattern;
import com.typeomega.core.mega.common.MegaShape;

import static com.gregtechceu.gtceu.api.pattern.Predicates.abilities;

/** Registration and pattern of the Matrioshka Brain; loaded only with GTNA (see TypeOmegaMegastructures). */
public final class MatrioshkaDefinition {

    /** Optical Data Transmission Hatches the station takes. */
    public static final int DATA_HATCHES = 2;

    private MatrioshkaDefinition() {}

    public static MultiblockMachineDefinition register() {
        return TypeOmegaMod.REGISTRATE
                .multiblock("matrioshka_brain", MatrioshkaBrainMachine::new)
                .langValue("Matrioshka Brain")
                .rotationState(RotationState.NON_Y_AXIS)
                // The renderer and the capture assume the pattern stands upright and unmirrored.
                .allowExtendedFacing(false)
                .allowFlip(false)
                .recipeType(GTRecipeTypes.DUMMY_RECIPES)
                .appearanceBlock(() -> MegaPattern.block(
                        MegaShape.get(MatrioshkaBrainMachine.SHAPE).part(MegaShape.Part.STATION).block()))
                .pattern(MatrioshkaDefinition::pattern)
                .modelProperty(com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties.RECIPE_LOGIC_STATUS,
                        com.gregtechceu.gtceu.api.machine.trait.RecipeLogic.Status.IDLE)
                .model(com.gregtechceu.gtceu.common.data.models.GTMachineModels.createWorkableCasingMachineModel(
                        GTCEu.id("block/casings/hpca/high_power_casing"),
                        GTCEu.id("block/multiblock/hpca"))
                        .andThen(builder -> builder.addDynamicRenderer(
                                com.typeomega.core.mega.common.client.MegaRenderer.Render::new)))
                .tooltipBuilder(TypeOmegaTooltips.lazy("matrioshka_brain"))
                .register();
    }

    /** The pattern of all five phases; the station's casings take CWU, coolant and repair hatches. */
    public static BlockPattern pattern(MultiblockMachineDefinition definition) {
        return MegaPattern.build(MatrioshkaBrainMachine.SHAPE, definition, casing -> casing
                .or(abilities(PartAbility.COMPUTATION_DATA_TRANSMISSION).setMaxGlobalLimited(DATA_HATCHES)
                        .setPreviewCount(1))
                .or(abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(2).setPreviewCount(1))
                .or(abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(2).setPreviewCount(1)));
    }
}
