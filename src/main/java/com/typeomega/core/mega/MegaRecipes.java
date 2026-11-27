package com.typeomega.core.mega;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.machines.GTResearchMachines;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.data.recipes.FinishedRecipe;

import com.typeomega.core.TypeOmegaMod;

import java.util.function.Consumer;

/** Recipes of the megastructures' controllers (provisional, until the Ψ era's progression is drawn). */
public final class MegaRecipes {

    private MegaRecipes() {}

    /** Provisional (D-332): the last megastructure takes a Birch World's worth of everything. */
    private static void qso(Consumer<FinishedRecipe> provider) {
        var b = GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("qso"))
                .inputItems(GTItems.FIELD_GENERATOR_UV.asStack(64))
                .inputItems(GTItems.EMITTER_UV.asStack(64))
                .inputItems(GTItems.GRAVI_STAR.asStack(64))
                .inputItems(CustomTags.UIV_CIRCUITS, 64)
                .inputFluids(GTMaterials.Neutronium.getFluid(294912));
        if (TypeOmegaMegastructures.BIRCH_WORLD_DEF != null)
            b.inputItems(TypeOmegaMegastructures.BIRCH_WORLD_DEF.asStack(1));
        b.outputItems(TypeOmegaMegastructures.QSO_DEF.asStack()).EUt(GTValues.VA[GTValues.UIV]).duration(48000)
                .save(provider);
    }

    /** Provisional (D-331): a Suprashell's worth of shells many times over. */
    private static void birchWorld(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("birch_world"))
                .inputItems(TypeOmegaMegastructures.SUPRASHELL_DEF.asStack(4))
                .inputItems(GTItems.FIELD_GENERATOR_UV.asStack(64))
                .inputItems(GTItems.GRAVI_STAR.asStack(64))
                .inputItems(CustomTags.UIV_CIRCUITS, 64)
                .inputFluids(GTMaterials.Neutronium.getFluid(147456))
                .outputItems(TypeOmegaMegastructures.BIRCH_WORLD_DEF.asStack())
                .EUt(GTValues.VA[GTValues.UIV]).duration(24000)
                .save(provider);
    }

    /** Provisional (D-446 A6): basement-universe wormhole companion to the Disintegrator. */
    private static void basementGateway(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("basement_gateway"))
                .inputItems(GTItems.FIELD_GENERATOR_UV.asStack(48))
                .inputItems(GTItems.EMITTER_UV.asStack(48))
                .inputItems(GTItems.GRAVI_STAR.asStack(32))
                .inputItems(CustomTags.UIV_CIRCUITS, 24)
                .inputFluids(GTMaterials.Neutronium.getFluid(36864))
                .outputItems(TypeOmegaMegastructures.BASEMENT_GATEWAY_DEF.asStack())
                .EUt(GTValues.VA[GTValues.UIV]).duration(12000)
                .save(provider);
    }

    /** Provisional (D-330): field generators to switch the binding off, a Suprashell's worth of gravity stars. */
    private static void disintegrator(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("disintegrator"))
                .inputItems(GTItems.FIELD_GENERATOR_UV.asStack(64))
                .inputItems(GTItems.GRAVI_STAR.asStack(64))
                .inputItems(GTItems.EMITTER_UV.asStack(64))
                .inputItems(CustomTags.UIV_CIRCUITS, 32)
                .inputFluids(GTMaterials.Neutronium.getFluid(73728))
                .outputItems(TypeOmegaMegastructures.DISINTEGRATOR_DEF.asStack())
                .EUt(GTValues.VA[GTValues.UIV]).duration(12000)
                .save(provider);
    }

    /** Provisional (D-329): a shell needs field generators to hold it static and gravity stars to tether it. */
    private static void suprashell(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("suprashell"))
                .inputItems(GTItems.FIELD_GENERATOR_UV.asStack(64))
                .inputItems(GTItems.GRAVI_STAR.asStack(64))
                .inputItems(GTItems.SENSOR_UV.asStack(32))
                .inputItems(CustomTags.UIV_CIRCUITS, 32)
                .inputFluids(GTMaterials.Neutronium.getFluid(73728))
                .outputItems(TypeOmegaMegastructures.SUPRASHELL_DEF.asStack())
                .EUt(GTValues.VA[GTValues.UIV]).duration(12000)
                .save(provider);
    }

    /** Provisional (D-328): emitters to heat a core and the coils of the magnetic mirrors. */
    private static void stellarHarvester(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("stellar_harvester"))
                .inputItems(GTItems.EMITTER_UV.asStack(64))
                .inputItems(com.gregtechceu.gtceu.common.data.GTBlocks.SUPERCONDUCTING_COIL.asStack(64))
                .inputItems(GTItems.GRAVI_STAR.asStack(64))
                .inputItems(CustomTags.UIV_CIRCUITS, 16)
                .inputFluids(GTMaterials.Neutronium.getFluid(36864))
                .outputItems(TypeOmegaMegastructures.STELLAR_HARVESTER_DEF.asStack())
                .EUt(GTValues.VA[GTValues.UIV]).duration(12000)
                .save(provider);
    }

    /** Provisional (D-446 A6): earlier-tier orbital bio-seeder companion to Yggdrasil Orchid. */
    private static void planetarySeederNexus(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("planetary_seeder_nexus"))
                .inputItems(com.raishxn.gtna.common.data.GTNAMachines3.LARGE_GREENHOUSE.asStack(16))
                .inputItems(GTItems.FIELD_GENERATOR_UV.asStack(16))
                .inputItems(GTItems.EMITTER_UV.asStack(16))
                .inputItems(CustomTags.UIV_CIRCUITS, 8)
                .inputFluids(GTMaterials.Neutronium.getFluid(4608))
                .outputItems(TypeOmegaMegastructures.PLANETARY_SEEDER_NEXUS_DEF.asStack())
                .EUt(GTValues.VA[GTValues.UIV]).duration(8000)
                .save(provider);
    }

    /** Provisional (D-327): greenhouses by the dozen and the field generators that hold the gardens aloft. */
    private static void yggdrasil(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("yggdrasil"))
                .inputItems(com.raishxn.gtna.common.data.GTNAMachines3.LARGE_GREENHOUSE.asStack(64))
                .inputItems(GTItems.FIELD_GENERATOR_UV.asStack(32))
                .inputItems(GTItems.EMITTER_UV.asStack(32))
                .inputItems(CustomTags.UIV_CIRCUITS, 16)
                .inputFluids(GTMaterials.Neutronium.getFluid(9216))
                .outputItems(TypeOmegaMegastructures.YGGDRASIL_DEF.asStack())
                .EUt(GTValues.VA[GTValues.UIV]).duration(12000)
                .save(provider);
    }

    /** Provisional (D-446 A3): superradiant mirror sphere sharing the black hole with HRAE-MC. */
    private static void penroseSphere(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("penrose_sphere"))
                .inputItems(com.gregtechceu.gtceu.common.data.GTBlocks.SUPERCONDUCTING_COIL.asStack(64))
                .inputItems(GTItems.FIELD_GENERATOR_UV.asStack(32))
                .inputItems(GTItems.GRAVI_STAR.asStack(64))
                .inputItems(CustomTags.UIV_CIRCUITS, 12)
                .inputFluids(GTMaterials.Neutronium.getFluid(18432))
                .outputItems(TypeOmegaMegastructures.PENROSE_SPHERE_DEF.asStack())
                .EUt(GTValues.VA[GTValues.UIV]).duration(10000)
                .save(provider);
    }

    /** Provisional (D-326): the yard is assembled from assembly lines and the robot arms that run them. */
    private static void assemblyYard(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("assembly_yard"))
                .inputItems(com.gregtechceu.gtceu.common.data.machines.GTMultiMachines.ASSEMBLY_LINE.asStack(16))
                .inputItems(GTItems.ROBOT_ARM_UV.asStack(64))
                .inputItems(GTItems.FIELD_GENERATOR_UV.asStack(16))
                .inputItems(CustomTags.UIV_CIRCUITS, 16)
                .inputFluids(GTMaterials.Neutronium.getFluid(18432))
                .outputItems(TypeOmegaMegastructures.ASSEMBLY_YARD_DEF.asStack())
                .EUt(GTValues.VA[GTValues.UIV]).duration(12000)
                .save(provider);
    }

    /** Provisional (D-323): the forge is pressed out of neutronium. */
    private static void nidavellir(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("nidavellir"))
                .inputItems(com.gregtechceu.gtceu.common.data.machines.GCYMMachines.BLAST_ALLOY_SMELTER.asStack(16))
                .inputItems(GTItems.ELECTRIC_PISTON_UV.asStack(64))
                .inputItems(GTItems.GRAVI_STAR.asStack(64))
                .inputItems(CustomTags.UIV_CIRCUITS, 16)
                .inputFluids(GTMaterials.Neutronium.getFluid(36864))
                .outputItems(TypeOmegaMegastructures.NIDAVELLIR_DEF.asStack())
                .EUt(GTValues.VA[GTValues.UIV]).duration(12000)
                .save(provider);
    }

    /** Provisional (D-446): companion extractor above the neutron star. */
    private static void neutroniumGigaforge(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("neutronium_gigaforge"))
                .inputItems(GTItems.ELECTRIC_PISTON_UV.asStack(32))
                .inputItems(GTItems.FIELD_GENERATOR_UV.asStack(16))
                .inputItems(GTItems.GRAVI_STAR.asStack(32))
                .inputItems(CustomTags.UIV_CIRCUITS, 8)
                .inputFluids(GTMaterials.Neutronium.getFluid(18432))
                .outputItems(TypeOmegaMegastructures.NEUTRONIUM_GIGAFORGE_DEF.asStack())
                .EUt(GTValues.VA[GTValues.UIV]).duration(8000)
                .save(provider);
    }

    /** Provisional (D-322): superconducting coils for the Penrose ring, sensors for the antennas. */
    private static void hraemc(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("hraemc"))
                .inputItems(com.gregtechceu.gtceu.common.data.GTBlocks.SUPERCONDUCTING_COIL.asStack(64))
                .inputItems(GTItems.SENSOR_UV.asStack(32))
                .inputItems(GTItems.GRAVI_STAR.asStack(64))
                .inputItems(CustomTags.UIV_CIRCUITS, 16)
                .inputFluids(GTMaterials.Neutronium.getFluid(18432))
                .outputItems(TypeOmegaMegastructures.HRAEMC_DEF.asStack())
                .EUt(GTValues.VA[GTValues.UIV]).duration(12000)
                .save(provider);
    }

    /** Provisional (D-321): emitters to pour light in, gravity stars to hold the hole. */
    private static void kugelblitz(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("kugelblitz"))
                .inputItems(GTItems.EMITTER_UV.asStack(64))
                .inputItems(GTItems.FIELD_GENERATOR_UV.asStack(32))
                .inputItems(GTItems.GRAVI_STAR.asStack(64))
                .inputItems(CustomTags.UIV_CIRCUITS, 16)
                .inputFluids(GTMaterials.Neutronium.getFluid(18432))
                .outputItems(TypeOmegaMegastructures.KUGELBLITZ_DEF.asStack())
                .EUt(GTValues.VA[GTValues.UIV]).duration(12000)
                .save(provider);
    }

    /** Provisional (D-320): field generators to damp the core, sensors to read its neutrinos. */
    private static void fusionSuppressor(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("fusion_suppressor"))
                .inputItems(GTItems.FIELD_GENERATOR_UV.asStack(32))
                .inputItems(GTItems.SENSOR_UV.asStack(32))
                .inputItems(GTItems.GRAVI_STAR.asStack(64))
                .inputItems(CustomTags.UIV_CIRCUITS, 16)
                .inputFluids(GTMaterials.Neutronium.getFluid(18432))
                .outputItems(TypeOmegaMegastructures.FUSION_SUPPRESSOR_DEF.asStack())
                .EUt(GTValues.VA[GTValues.UIV]).duration(12000)
                .save(provider);
    }

    /** Provisional (D-319): a forge of stars from fusion reactors, field generators and gravity stars. */
    private static void systemCreator(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("system_creator"))
                .inputItems(com.gregtechceu.gtceu.common.data.machines.GTMultiMachines.FUSION_REACTOR[GTValues.UV]
                        .asStack(16))
                .inputItems(GTItems.FIELD_GENERATOR_UV.asStack(32))
                .inputItems(GTItems.GRAVI_STAR.asStack(64))
                .inputItems(CustomTags.UIV_CIRCUITS, 16)
                .inputFluids(GTMaterials.Neutronium.getFluid(18432))
                .outputItems(TypeOmegaMegastructures.SYSTEM_CREATOR_DEF.asStack())
                .EUt(GTValues.VA[GTValues.UIV]).duration(12000)
                .save(provider);
    }

    /** Provisional (D-318): fusion reactors for the crucibles, shielding for the hoop. */
    private static void stellarHoop(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("stellar_hoop"))
                .inputItems(com.gregtechceu.gtceu.common.data.machines.GTMultiMachines.FUSION_REACTOR[GTValues.UV]
                        .asStack(8))
                .inputItems(GTItems.FIELD_GENERATOR_UV.asStack(8))
                .inputItems(GTItems.GRAVI_STAR.asStack(32))
                .inputItems(CustomTags.UEV_CIRCUITS, 16)
                .inputFluids(GTMaterials.Neutronium.getFluid(4608))
                .outputItems(TypeOmegaMegastructures.STELLAR_HOOP_DEF.asStack())
                .EUt(GTValues.VA[GTValues.UEV]).duration(6000)
                .save(provider);
    }

    /** Provisional (D-317): the ring current is superconducting coils, the petals are field generators. */
    private static void starLifter(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("star_lifter"))
                .inputItems(com.gregtechceu.gtceu.common.data.GTBlocks.SUPERCONDUCTING_COIL.asStack(64))
                .inputItems(GTItems.FIELD_GENERATOR_UV.asStack(16))
                .inputItems(GTItems.GRAVI_STAR.asStack(32))
                .inputItems(CustomTags.UEV_CIRCUITS, 16)
                .inputFluids(GTMaterials.Neutronium.getFluid(4608))
                .outputItems(TypeOmegaMegastructures.STAR_LIFTER_DEF.asStack())
                .EUt(GTValues.VA[GTValues.UEV]).duration(6000)
                .save(provider);
    }

    public static void addRecipes(Consumer<FinishedRecipe> provider) {
        // Helium II: liquid helium cooled below the lambda point (2.17 K).
        GTRecipeTypes.VACUUM_RECIPES.recipeBuilder(TypeOmegaMod.id("superfluid_helium"))
                .inputFluids(GTMaterials.Helium.getFluid(
                        com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys.LIQUID, 1000))
                .outputFluids(MegaMaterials.SUPERFLUID_HELIUM.getFluid(1000))
                .EUt(GTValues.VA[GTValues.UHV]).duration(200)
                .save(provider);
        // D-445: the casings of the pilot megastructures (GTNA's blocks are among their ingredients).
        if (TypeOmegaMegastructures.gtna()) MegaCasingRecipes.add(provider);
        if (TypeOmegaMegastructures.STAR_LIFTER_DEF != null) starLifter(provider);
        if (TypeOmegaMegastructures.STELLAR_HOOP_DEF != null) stellarHoop(provider);
        if (TypeOmegaMegastructures.SYSTEM_CREATOR_DEF != null) systemCreator(provider);
        if (TypeOmegaMegastructures.FUSION_SUPPRESSOR_DEF != null) fusionSuppressor(provider);
        if (TypeOmegaMegastructures.KUGELBLITZ_DEF != null) kugelblitz(provider);
        if (TypeOmegaMegastructures.HRAEMC_DEF != null) hraemc(provider);
        if (TypeOmegaMegastructures.PENROSE_SPHERE_DEF != null) penroseSphere(provider);
        if (TypeOmegaMegastructures.NIDAVELLIR_DEF != null) nidavellir(provider);
        if (TypeOmegaMegastructures.NEUTRONIUM_GIGAFORGE_DEF != null) neutroniumGigaforge(provider);
        if (TypeOmegaMegastructures.ASSEMBLY_YARD_DEF != null) assemblyYard(provider);
        if (TypeOmegaMegastructures.PLANETARY_SEEDER_NEXUS_DEF != null) planetarySeederNexus(provider);
        if (TypeOmegaMegastructures.YGGDRASIL_DEF != null) yggdrasil(provider);
        if (TypeOmegaMegastructures.STELLAR_HARVESTER_DEF != null) stellarHarvester(provider);
        if (TypeOmegaMegastructures.SUPRASHELL_DEF != null) suprashell(provider);
        if (TypeOmegaMegastructures.DISINTEGRATOR_DEF != null) disintegrator(provider);
        if (TypeOmegaMegastructures.BASEMENT_GATEWAY_DEF != null) basementGateway(provider);
        if (TypeOmegaMegastructures.BIRCH_WORLD_DEF != null) birchWorld(provider);
        if (TypeOmegaMegastructures.QSO_DEF != null) qso(provider);
        if (TypeOmegaMegastructures.MATRIOSHKA_BRAIN == null) return;
        GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("matrioshka_brain"))
                .inputItems(GTResearchMachines.HIGH_PERFORMANCE_COMPUTING_ARRAY.asStack(16))
                .inputItems(GTItems.GRAVI_STAR.asStack(64))
                .inputItems(CustomTags.UEV_CIRCUITS, 16)
                .inputFluids(GTMaterials.Neutronium.getFluid(9216))
                .outputItems(TypeOmegaMegastructures.MATRIOSHKA_BRAIN.asStack())
                .EUt(GTValues.VA[GTValues.UEV]).duration(6000)
                .save(provider);
        if (TypeOmegaMegastructures.STELLAR_ENGINE_DEF == null) return;
        GTRecipeTypes.ASSEMBLY_LINE_RECIPES.recipeBuilder(TypeOmegaMod.id("stellar_engine"))
                .inputItems(com.gregtechceu.gtceu.common.data.machines.GTMultiMachines.FUSION_REACTOR[GTValues.UV]
                        .asStack(4))
                .inputItems(GTItems.GRAVI_STAR.asStack(64))
                .inputItems(CustomTags.UEV_CIRCUITS, 16)
                .inputFluids(GTMaterials.Neutronium.getFluid(9216))
                .outputItems(TypeOmegaMegastructures.STELLAR_ENGINE_DEF.asStack())
                .EUt(GTValues.VA[GTValues.UEV]).duration(6000)
                .save(provider);
    }
}
