package com.typeomega.core.mega.typezero;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.data.recipes.FinishedRecipe;

import com.typeomega.core.GTIAMod;
import com.typeomega.core.data.GTIARecipeTypes;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.VA;

/** Recipes of the Type 0 megastructures and of what they make (GTIA-D-390, D-419, D-420). */
public final class TypeZeroRecipes {

    private static final int HV = GTValues.HV;

    private TypeZeroRecipes() {}

    public static void addRecipes(Consumer<FinishedRecipe> provider) {
        controllers(provider);
        casings(provider);
        dispatchAndMissions(provider);
    }

    private static void controllers(Consumer<FinishedRecipe> provider) {
        // GTIA-D-419: HV hull, 4 HV transformers, HV batteries, HV Energy Hatch, aluminium cable, 16 HV circuits and
        // an MV Field Generator.
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTIAMod.id(NationalDispatchCenter.ID))
                .inputItems(GTMachines.HULL[HV].asStack())
                .inputItems(GTMachines.TRANSFORMER[HV].asStack(4))
                .inputItems(CustomTags.HV_BATTERIES, 4)
                .inputItems(GTMachines.ENERGY_INPUT_HATCH[HV].asStack())
                .inputItems(TagPrefix.cableGtSingle, GTMaterials.Aluminium, 8)
                .inputItems(CustomTags.HV_CIRCUITS, 16)
                .inputItems(GTItems.FIELD_GENERATOR_MV.asStack())
                .outputItems(NationalDispatchCenter.DEFINITION.asStack())
                .duration(600).EUt(VA[HV]).save(provider);
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTIAMod.id(Cosmodrome.ID))
                .inputItems(GTMachines.HULL[HV].asStack())
                .inputItems(TypeZeroBlocks.MISSION_CONTROL_CASING.asStack(4))
                .inputItems(CustomTags.EV_CIRCUITS, 4)
                .inputItems(GTItems.SENSOR_HV.asStack(2))
                .inputItems(GTItems.EMITTER_HV.asStack(2))
                .inputItems(TagPrefix.plate, GTMaterials.StainlessSteel, 8)
                .outputItems(Cosmodrome.DEFINITION.asStack())
                .duration(600).EUt(VA[HV]).save(provider);
    }

    private static void casings(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTIAMod.id("mission_control_casing"))
                .inputItems(TagPrefix.plate, GTMaterials.Aluminium, 4)
                .inputItems(CustomTags.EV_CIRCUITS, 1)
                .inputItems(TagPrefix.frameGt, GTMaterials.StainlessSteel)
                .outputItems(TypeZeroBlocks.MISSION_CONTROL_CASING.asStack(2))
                .duration(400).EUt(VA[HV]).save(provider);
    }

    private static void dispatchAndMissions(Consumer<FinishedRecipe> provider) {
        GTIARecipeTypes.GRID_DISPATCH.recipeBuilder(GTIAMod.id("grid_transponder"))
                .inputItems(TagPrefix.plate, GTMaterials.Aluminium, 2)
                .inputItems(CustomTags.HV_CIRCUITS, 1)
                .inputItems(TagPrefix.wireFine, GTMaterials.Gold, 4)
                .outputItems(TypeZeroItems.GRID_TRANSPONDER.asStack())
                .duration(200).EUt(VA[HV]).save(provider);
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTIAMod.id("lunar_probe"))
                .inputItems(GTItems.TOOL_DATA_STICK.asStack())
                .inputItems(GTItems.SENSOR_HV.asStack())
                .inputItems(GTItems.EMITTER_HV.asStack())
                .inputItems(GTItems.NOR_MEMORY_CHIP.asStack(4))
                .inputItems(TagPrefix.plate, GTMaterials.Aluminium, 4)
                .inputItems(CustomTags.HV_BATTERIES, 1)
                .outputItems(TypeZeroItems.LUNAR_PROBE.asStack())
                .cleanroom(CleanroomType.CLEANROOM)
                .duration(600).EUt(VA[HV]).save(provider);
        GTIARecipeTypes.MISSION_CONTROL.recipeBuilder(GTIAMod.id("lunar_survey"))
                .inputItems(TypeZeroItems.LUNAR_PROBE.asStack())
                .inputFluids(GTMaterials.RocketFuel.getFluid(1000))
                .outputItems(TypeZeroItems.LUNAR_SURVEY_DATA.asStack())
                .duration(600).EUt(VA[HV]).save(provider);
        // EN: Copies for the rest of the team: every player who lands carries the map.
        // PT: Cópias para o resto do time: cada jogador que pousa leva o mapa.
        // ZH: 为队伍其他成员复制：每位登月的玩家都要携带地图。
        GTIARecipeTypes.MISSION_CONTROL.recipeBuilder(GTIAMod.id("lunar_survey_copy"))
                .inputItems(TypeZeroItems.LUNAR_SURVEY_DATA.asStack())
                .inputItems(GTItems.TOOL_DATA_STICK.asStack())
                .outputItems(TypeZeroItems.LUNAR_SURVEY_DATA.asStack(2))
                .duration(200).EUt(VA[HV]).save(provider);
    }
}
