package com.raishxn.gtia.processing;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import com.raishxn.gtia.GTIAMod;
import com.raishxn.gtia.data.GTIARecipeTypes;
import com.raishxn.gtia.data.GTIATooltips;

import java.util.function.Supplier;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;

/**
 * Massive processing from LV (GTIA-D-357, D-365 to D-369): Large Furnace (LV), Ore Processing Plant (MV) and the four
 * Plus factories (MV), with GTL Enhanced's parallels and the shapes approved on the F3 artifact.
 */
public final class GTIAProcessing {

    private GTIAProcessing() {}

    private static TraceabilityPredicate casing(MultiblockMachineDefinition def, Block block, int min) {
        return blocks(block).setMinGlobalLimited(min).or(Predicates.autoAbilities(def.getRecipeTypes()))
                .or(Predicates.autoAbilities(true, false, true));
    }

    public static final MultiblockMachineDefinition LARGE_FURNACE = GTIAMod.REGISTRATE
            .multiblock("large_furnace", WorkableElectricMultiblockMachine::new)
            .langValue("Large Furnace")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(FURNACE_RECIPES)
            .recipeModifiers(TierParallel.of(TierParallel::largeFurnace), GTRecipeModifiers.OC_NON_PERFECT_SUBTICK)
            .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
            .pattern(def -> {
                var b = FactoryBlockPattern.start();
                for (String[] a : ProcessingShapes.LARGE_FURNACE) b.aisle(a);
                return b.where('@', Predicates.controller(blocks(def.getBlock())))
                        .where('A', casing(def, GTBlocks.CASING_STEEL_SOLID.get(), 30))
                        .where('E', blocks(GTBlocks.CASING_PRIMITIVE_BRICKS.get()))
                        .where('H', blocks(Blocks.LAVA))
                        .where('V', blocks(GTBlocks.CASING_TEMPERED_GLASS.get()))
                        .where('P', blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                        .where('K', blocks(GTBlocks.CASING_COKE_BRICKS.get()))
                        .where('I', blocks(Blocks.IRON_BARS))
                        .where('F', Predicates.frames(GTMaterials.Steel))
                        .where('J', blocks(ChemicalHelper.getBlock(TagPrefix.block, GTMaterials.Steel)))
                        .where('R', blocks(GTBlocks.CASING_STEEL_GEARBOX.get()))
                        .where('O', blocks(GTBlocks.LAMPS.get(DyeColor.ORANGE).get()))
                        .where('G', blocks(GTBlocks.CASING_GRATE.get()))
                        .where('T', blocks(GTBlocks.STEEL_HULL.get()))
                        .where('.', Predicates.any())
                        .build();
            })
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/electric_blast_furnace"))
            .tooltips(GTIATooltips.multiblock("large_furnace"))
            .register();

    public static final MultiblockMachineDefinition ORE_PROCESSING_PLANT = GTIAMod.REGISTRATE
            .multiblock("ore_processing_plant", WorkableElectricMultiblockMachine::new)
            .langValue("Ore Processing Plant")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTIARecipeTypes.ORE_PROCESSING_PLANT)
            .recipeModifiers(TierParallel.of(TierParallel::orePlant), GTRecipeModifiers.OC_NON_PERFECT_SUBTICK)
            .appearanceBlock(GTBlocks.CASING_ALUMINIUM_FROSTPROOF)
            .pattern(def -> {
                var b = FactoryBlockPattern.start();
                for (String[] a : ProcessingShapes.ORE_PLANT) b.aisle(a);
                return b.where('@', Predicates.controller(blocks(def.getBlock())))
                        .where('A', blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get()).setMinGlobalLimited(30)
                                .or(Predicates.autoAbilities(def.getRecipeTypes()))
                                .or(Predicates.autoAbilities(false, false, false)))
                        .where('S', blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()))
                        .where('G', blocks(GTBlocks.CASING_STEEL_GEARBOX.get()))
                        .where('W', blocks(Blocks.WATER))
                        .where('V', blocks(GTBlocks.CASING_TEMPERED_GLASS.get()))
                        .where('T', blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                        .where('R', Predicates.frames(GTMaterials.Aluminium))
                        .where('M', blocks(GTBlocks.MACHINE_CASING_MV.get()))
                        .where('Y', blocks(GTBlocks.YELLOW_STRIPES_BLOCK_A.get()))
                        .where('.', Predicates.any())
                        .build();
            })
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_frost_proof"),
                    GTCEu.id("block/multiblock/large_miner"))
            .tooltips(GTIATooltips.multiblock("ore_processing_plant"))
            .register();

    public static final MultiblockMachineDefinition PROCESSING_PLUS = plus("processing_plus", "Processing Factory Plus",
            GTBlocks.CASING_STEEL_GEARBOX::get, DyeColor.ORANGE, BENDER_RECIPES, COMPRESSOR_RECIPES,
            FORGE_HAMMER_RECIPES, CUTTER_RECIPES, EXTRUDER_RECIPES, LATHE_RECIPES, WIREMILL_RECIPES,
            FORMING_PRESS_RECIPES, POLARIZER_RECIPES, LASER_ENGRAVER_RECIPES, FLUID_SOLIDFICATION_RECIPES);
    public static final MultiblockMachineDefinition ASSEMBLING_PLUS = plus("assembling_plus", "Assembling Factory Plus",
            GTBlocks.CASING_GRATE::get, DyeColor.CYAN, ASSEMBLER_RECIPES, CIRCUIT_ASSEMBLER_RECIPES);
    public static final MultiblockMachineDefinition SEPARATING_PLUS = plus("separating_plus", "Separating Factory Plus",
            GTBlocks.CASING_STEEL_TURBINE::get, DyeColor.LIME, CENTRIFUGE_RECIPES, THERMAL_CENTRIFUGE_RECIPES,
            ELECTROLYZER_RECIPES, SIFTER_RECIPES, MACERATOR_RECIPES, EXTRACTOR_RECIPES);
    // EN: Mixing Plus keeps the chemical reactor and bath at MV: explicit exception to D-335 (D-367).
    // PT: A Mixing Plus mantém reator e banho químico no MV: exceção explícita à D-335 (D-367).
    // ZH: 混合工厂 Plus 在 MV 保留化学反应釜与化学浸洗：D-335 的明确例外（D-367）。
    public static final MultiblockMachineDefinition MIXING_PLUS = plus("mixing_plus", "Mixing Factory Plus",
            GTBlocks.CASING_PTFE_INERT::get, DyeColor.PURPLE, CHEMICAL_RECIPES, MIXER_RECIPES, CHEMICAL_BATH_RECIPES,
            ORE_WASHER_RECIPES);

    public static final MultiblockMachineDefinition[] PLUS = { PROCESSING_PLUS, ASSEMBLING_PLUS, SEPARATING_PLUS,
            MIXING_PLUS };

    private static MultiblockMachineDefinition plus(String id, String name, Supplier<Block> module, DyeColor lamp,
                                                    GTRecipeType... types) {
        return GTIAMod.REGISTRATE
                .multiblock(id, WorkableElectricMultiblockMachine::new)
                .langValue(name)
                .rotationState(RotationState.NON_Y_AXIS)
                .recipeTypes(types)
                .recipeModifiers(TierParallel.of(TierParallel::plus), GTRecipeModifiers.OC_NON_PERFECT_SUBTICK)
                .appearanceBlock(GTBlocks.CASING_ALUMINIUM_FROSTPROOF)
                .pattern(def -> {
                    var b = FactoryBlockPattern.start();
                    for (String[] a : ProcessingShapes.PLUS_HALL) b.aisle(a);
                    return b.where('@', Predicates.controller(blocks(def.getBlock())))
                            .where('A', casing(def, GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get(), 60))
                            .where('S', blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()))
                            .where('Q', blocks(module.get()))
                            .where('M', blocks(GTBlocks.MACHINE_CASING_MV.get()))
                            .where('V', blocks(GTBlocks.CASING_TEMPERED_GLASS.get()))
                            .where('T', blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                            .where('R', Predicates.frames(GTMaterials.Aluminium))
                            .where('Y', blocks(GTBlocks.YELLOW_STRIPES_BLOCK_A.get()))
                            .where('O', blocks(GTBlocks.LAMPS.get(lamp).get()))
                            .where('H', blocks(GTBlocks.STEEL_HULL.get()))
                            .where('.', Predicates.any())
                            .build();
                })
                .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_frost_proof"),
                        GTCEu.id("block/multiblock/large_chemical_reactor"))
                .tooltips(GTIATooltips.multiblock(id))
                .register();
    }

    public static void init() {}
}
