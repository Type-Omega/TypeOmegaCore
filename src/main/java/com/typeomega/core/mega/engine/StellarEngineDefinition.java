package com.typeomega.core.mega.engine;

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

/** Registration and pattern of the Stellar Engine; loaded only with GTNA (see TypeOmegaMegastructures). */
public final class StellarEngineDefinition {

    private StellarEngineDefinition() {}

    public static MultiblockMachineDefinition register() {
        return TypeOmegaMod.REGISTRATE
                .multiblock("stellar_engine", StellarEngineMachine::new)
                .langValue("Stellar Engine")
                .rotationState(RotationState.NON_Y_AXIS)
                // The renderer and the capture assume the pattern stands upright and unmirrored.
                .allowExtendedFacing(false)
                .allowFlip(false)
                .recipeType(GTRecipeTypes.DUMMY_RECIPES)
                .appearanceBlock(() -> MegaPattern.block(
                        MegaShape.get(StellarEngineMachine.SHAPE).part(MegaShape.Part.STATION).block()))
                .pattern(StellarEngineDefinition::pattern)
                .modelProperty(com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties.RECIPE_LOGIC_STATUS,
                        com.gregtechceu.gtceu.api.machine.trait.RecipeLogic.Status.IDLE)
                .model(com.gregtechceu.gtceu.common.data.models.GTMachineModels.createWorkableCasingMachineModel(
                        GTCEu.id("block/casings/fusion/fusion_casing_mk3"),
                        GTCEu.id("block/multiblock/fusion_reactor"))
                        .andThen(builder -> builder.addDynamicRenderer(
                                com.typeomega.core.mega.common.client.MegaRenderer.Render::new)))
                .tooltipBuilder(TypeOmegaTooltips.lazy("stellar_engine"))
                .register();
    }

    /** All five phases; the station's casings take repair blocks and give plasma and neutronium. */
    public static BlockPattern pattern(MultiblockMachineDefinition definition) {
        return MegaPattern.build(StellarEngineMachine.SHAPE, definition, casing -> casing
                .or(abilities(PartAbility.IMPORT_ITEMS).setMaxGlobalLimited(2).setPreviewCount(1))
                .or(abilities(PartAbility.EXPORT_FLUIDS).setMaxGlobalLimited(2).setPreviewCount(1))
                .or(abilities(PartAbility.EXPORT_ITEMS).setMaxGlobalLimited(2).setPreviewCount(1)));
    }
}
