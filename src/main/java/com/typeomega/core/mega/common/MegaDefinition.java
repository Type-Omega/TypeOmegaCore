package com.typeomega.core.mega.common;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import net.minecraft.resources.ResourceLocation;

import com.typeomega.core.GTIAMod;
import com.typeomega.core.data.GTIATooltips;

import java.util.function.Function;

import static com.gregtechceu.gtceu.api.pattern.Predicates.abilities;

/**
 * Registration of a megastructure controller from its shape: the station block as appearance, the five-phase pattern,
 * the shared renderer and the station's hatch slots. The English name and the tooltip lines
 * ({@code typeomega.machine.<id>.tooltip.<i>}) come from mega_lang, laid out by the lib's builder (GTIA-D-410).
 */
public final class MegaDefinition {

    /** Hatches the station takes: what, how many at most. */
    public record Hatch(PartAbility ability, int max) {}

    private MegaDefinition() {}

    public static MultiblockMachineDefinition register(String id,
                                                       Function<IMachineBlockEntity, ? extends MegaMachine> factory,
                                                       ResourceLocation casing, Hatch... hatches) {
        return register(id, factory, casing, GTRecipeTypes.DUMMY_RECIPES, hatches);
    }

    public static MultiblockMachineDefinition register(String id,
                                                       Function<IMachineBlockEntity, ? extends MegaMachine> factory,
                                                       ResourceLocation casing, GTRecipeType type,
                                                       Hatch... hatches) {
        return GTIAMod.REGISTRATE
                .multiblock(id, factory::apply)
                .langValue(MegaLang.english("block.typeomega." + id))
                .rotationState(RotationState.NON_Y_AXIS)
                // The renderer and the capture assume the pattern stands upright and unmirrored.
                .allowExtendedFacing(false)
                .allowFlip(false)
                .recipeType(type)
                .appearanceBlock(() -> MegaPattern.block(MegaShape.get(id).part(MegaShape.Part.STATION).block()))
                .pattern(definition -> MegaPattern.build(id, definition, casing0 -> {
                    var p = casing0;
                    for (var h : hatches) p = p.or(abilities(h.ability()).setMaxGlobalLimited(h.max())
                            .setPreviewCount(1));
                    return p;
                }))
                .modelProperty(com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties.RECIPE_LOGIC_STATUS,
                        com.gregtechceu.gtceu.api.machine.trait.RecipeLogic.Status.IDLE)
                .model(com.gregtechceu.gtceu.common.data.models.GTMachineModels.createWorkableCasingMachineModel(
                        casing, GTCEu.id("block/multiblock/fusion_reactor"))
                        .andThen(builder -> builder.addDynamicRenderer(
                                com.typeomega.core.mega.common.client.MegaRenderer.Render::new)))
                .tooltipBuilder(GTIATooltips.lazy(id))
                .register();
    }
}
