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

    /** Megastructures that run the recipes of other types through their own engine, with those types. */
    public static final java.util.Map<MultiblockMachineDefinition, java.util.function.Supplier<java.util.List<GTRecipeType>>> WORKSTATIONS = new java.util.LinkedHashMap<>();

    private MegaDefinition() {}

    /**
     * As {@link #register(String, Function, ResourceLocation, Hatch...)} for a megastructure that runs the recipes of
     * {@code runs} through {@code MegaRecipeEngine}.
     */
    // EN: The definition keeps the dummy type, because GTCEu's own recipe logic would otherwise run those recipes a
    // EN: second time beside the mega's engine; EMI lists the mega as a workstation of each type instead.
    // PT: A definição fica com o tipo dummy, porque senão a lógica de receitas do próprio GTCEu rodaria essas receitas
    // PT: uma segunda vez ao lado do motor do mega; em vez disso o EMI mostra o mega como workstation de cada tipo.
    // ZH: 定义保留 dummy 类型，否则 GTCEu 自身的配方逻辑会在巨构引擎之外再运行一遍这些配方；
    // ZH: 改为在 EMI 中把巨构列为各类型的工作站。
    public static MultiblockMachineDefinition register(String id,
                                                       Function<IMachineBlockEntity, ? extends MegaMachine> factory,
                                                       ResourceLocation casing,
                                                       java.util.function.Supplier<java.util.List<GTRecipeType>> runs,
                                                       Hatch... hatches) {
        var definition = register(id, factory, casing, GTRecipeTypes.DUMMY_RECIPES, hatches);
        WORKSTATIONS.put(definition, runs);
        return definition;
    }

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
