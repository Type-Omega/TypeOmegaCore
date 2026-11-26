package com.typeomega.core.mega;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.data.recipes.FinishedRecipe;

import com.raishxn.gtna.common.data.GTNABlocks;
import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.mega.blocks.GigaBlocks;
import com.typeomega.core.mega.blocks.MegaHullBlocks;

import java.util.function.Consumer;

/**
 * Recipes of the megastructure casings (D-445, M6). The hull of each megastructure is made at the tier of its
 * controller, in the Assembly Line, with a research (the GTNA way, {@code stationResearch}) from the hull of the
 * megastructure before it, and gives many blocks per craft, because a megastructure is tens of thousands of blocks.
 * The shared blocks and the accents are cheap Assembler recipes that eat hulls and parts of the same tier.
 *
 * <p>
 * Pilot ladder (D-316 order, only the three pilots exist yet): the Star Lifter girdle is researched from GTNA's
 * Dimensionally Transcendent Casing (the block the controller stations used), Nidavellir's cobalt plate from the
 * girdle, the Orchid frame from the cobalt plate. When the other hulls exist, each research moves to the hull before it
 * in the order the author fixes (D-373).
 */
final class MegaCasingRecipes {

    private static final int UEV = GTValues.UEV, UIV = GTValues.UIV;

    private MegaCasingRecipes() {}

    static void add(Consumer<FinishedRecipe> provider) {
        deck(provider);
        truss(provider);
        lamp(provider);
        lifterGirdleHull(provider);
        heatedHexPlate(provider);
        forgeCobaltPlate(provider);
        terraceRimLight(provider);
        orchidCellFrame(provider);
        orchidCellGlass(provider);
        orchidWindowRim(provider);
    }

    // ------------------------------------------------------------------ shared: Assembler, UEV, cheap

    /** One Dimensionally Transcendent Casing (what the old station was made of) becomes 64 decks. */
    private static void deck(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(TypeOmegaMod.id("giga_service_deck"))
                .inputItems(GTNABlocks.DIMENSIONALLY_TRANSCENDENT_CASING.asItem(), 1)
                .inputItems(TagPrefix.plateDouble, GTMaterials.Neutronium, 8)
                .inputItems(TagPrefix.frameGt, GTMaterials.Neutronium, 4)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(1152))
                .outputItems(GigaBlocks.SERVICE_DECK.asStack(64))
                .EUt(GTValues.VA[UEV]).duration(200)
                .save(provider);
    }

    private static void truss(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(TypeOmegaMod.id("giga_truss"))
                .inputItems(TagPrefix.frameGt, GTMaterials.Neutronium, 4)
                .inputItems(TagPrefix.rod, GTMaterials.Neutronium, 16)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                .outputItems(GigaBlocks.TRUSS.asStack(64))
                .EUt(GTValues.VA[UEV]).duration(200)
                .save(provider);
    }

    private static void lamp(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(TypeOmegaMod.id("giga_lamp"))
                .inputItems(GTItems.EMITTER_UEV, 1)
                .inputItems(TagPrefix.plateDouble, GTMaterials.Neutronium, 2)
                .inputItems(TagPrefix.foil, GTMaterials.Neutronium, 8)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(288))
                .outputItems(GigaBlocks.LAMP.asStack(16))
                .EUt(GTValues.VA[UEV]).duration(200)
                .save(provider);
    }

    // ------------------------------------------------------------------ Star Lifter: UEV

    private static void lifterGirdleHull(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("lifter_girdle_hull"))
                .inputItems(GTNABlocks.DIMENSIONALLY_TRANSCENDENT_CASING.asItem(), 4)
                .inputItems(GTItems.FIELD_GENERATOR_UEV, 2)
                .inputItems(GTItems.SENSOR_UEV, 2)
                .inputItems(CustomTags.UEV_CIRCUITS, 8)
                .inputItems(TagPrefix.plateDouble, GTMaterials.Neutronium, 16)
                .inputItems(TagPrefix.frameGt, GTMaterials.Neutronium, 8)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(2304))
                .inputFluids(GTMaterials.Neutronium.getFluid(1152))
                .outputItems(MegaHullBlocks.LIFTER_GIRDLE_HULL.asStack(64))
                .EUt(GTValues.VA[UEV]).duration(600)
                .stationResearch(b -> b.researchStack(GTNABlocks.DIMENSIONALLY_TRANSCENDENT_CASING.asStack())
                        .CWUt(1024).EUt(GTValues.VA[UEV]))
                .save(provider);
    }

    /** An accent: two girdle hulls, an emitter and superconducting coils make 16 ember plates. */
    private static void heatedHexPlate(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(TypeOmegaMod.id("heated_hex_plate"))
                .inputItems(MegaHullBlocks.LIFTER_GIRDLE_HULL.asItem(), 2)
                .inputItems(GTItems.EMITTER_UEV, 1)
                .inputItems(com.gregtechceu.gtceu.common.data.GTBlocks.SUPERCONDUCTING_COIL.asItem(), 2)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                .outputItems(MegaHullBlocks.HEATED_HEX_PLATE.asStack(16))
                .EUt(GTValues.VA[UEV]).duration(300)
                .save(provider);
    }

    // ------------------------------------------------------------------ Nidavellir: UIV

    private static void forgeCobaltPlate(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("forge_cobalt_plate"))
                .inputItems(MegaHullBlocks.LIFTER_GIRDLE_HULL.asItem(), 4)
                .inputItems(GTItems.FIELD_GENERATOR_UIV, 2)
                .inputItems(GTItems.ELECTRIC_PISTON_UIV, 2)
                .inputItems(CustomTags.UIV_CIRCUITS, 8)
                .inputItems(TagPrefix.plateDouble, GTMaterials.Cobalt, 32)
                .inputItems(TagPrefix.plateDouble, GTMaterials.Neutronium, 16)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(2304))
                .inputFluids(GTMaterials.Neutronium.getFluid(2304))
                .outputItems(MegaHullBlocks.FORGE_COBALT_PLATE.asStack(64))
                .EUt(GTValues.VA[UIV]).duration(800)
                .stationResearch(b -> b.researchStack(MegaHullBlocks.LIFTER_GIRDLE_HULL.asStack())
                        .CWUt(2048).EUt(GTValues.VA[UIV]))
                .save(provider);
    }

    private static void terraceRimLight(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(TypeOmegaMod.id("terrace_rim_light"))
                .inputItems(MegaHullBlocks.FORGE_COBALT_PLATE.asItem(), 2)
                .inputItems(GTItems.EMITTER_UIV, 1)
                .inputItems(TagPrefix.foil, GTMaterials.Neutronium, 8)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                .outputItems(MegaHullBlocks.TERRACE_RIM_LIGHT.asStack(16))
                .EUt(GTValues.VA[UIV]).duration(300)
                .save(provider);
    }

    // ------------------------------------------------------------------ Yggdrasil: UIV

    private static void orchidCellFrame(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("orchid_cell_frame"))
                .inputItems(MegaHullBlocks.FORGE_COBALT_PLATE.asItem(), 4)
                .inputItems(GTItems.FIELD_GENERATOR_UIV, 2)
                .inputItems(GTItems.SENSOR_UIV, 2)
                .inputItems(CustomTags.UIV_CIRCUITS, 8)
                .inputItems(TagPrefix.plateDouble, GTMaterials.Neutronium, 32)
                .inputItems(TagPrefix.foil, GTMaterials.Naquadria, 32)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(2304))
                .inputFluids(GTMaterials.Naquadria.getFluid(2304))
                .outputItems(MegaHullBlocks.ORCHID_CELL_FRAME.asStack(64))
                .EUt(GTValues.VA[UIV]).duration(800)
                .stationResearch(b -> b.researchStack(MegaHullBlocks.FORGE_COBALT_PLATE.asStack())
                        .CWUt(2048).EUt(GTValues.VA[UIV]))
                .save(provider);
    }

    /** The glass eats the glass it replaces (Force Field Glass) and frames to hold it: 16 per craft. */
    private static void orchidCellGlass(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(TypeOmegaMod.id("orchid_cell_glass"))
                .inputItems(MegaHullBlocks.ORCHID_CELL_FRAME.asItem(), 2)
                .inputItems(GTNABlocks.FORCE_FIELD_GLASS.asItem(), 4)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                .outputItems(MegaHullBlocks.ORCHID_CELL_GLASS.asStack(16))
                .EUt(GTValues.VA[UIV]).duration(300)
                .save(provider);
    }

    private static void orchidWindowRim(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(TypeOmegaMod.id("orchid_window_rim"))
                .inputItems(MegaHullBlocks.ORCHID_CELL_FRAME.asItem(), 2)
                .inputItems(GTItems.EMITTER_UIV, 1)
                .inputItems(TagPrefix.foil, GTMaterials.Neutronium, 8)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                .outputItems(MegaHullBlocks.ORCHID_WINDOW_RIM.asStack(16))
                .EUt(GTValues.VA[UIV]).duration(300)
                .save(provider);
    }
}
