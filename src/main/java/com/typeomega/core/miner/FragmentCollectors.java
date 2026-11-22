package com.typeomega.core.miner;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;

import com.typeomega.core.GTIAMod;
import com.typeomega.core.data.GTIARecipeTypes;
import com.typeomega.core.data.GTIATooltips;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

/**
 * Fragment Collector (LV, "survey tower", GTIA-D-362) and Large Fragment Collector (EV, "drilling platform",
 * GTIA-D-363). Shapes from design/estudos/fusao_f2 (col_b, big_b), approved on the F2 artifact on 2026-11-11.
 */
public final class FragmentCollectors {

    /** The Large Collector's controller class, so the logic can tell it apart (16 operations at once). */
    public static final class Large extends WorkableElectricMultiblockMachine {

        public Large(IMachineBlockEntity holder) {
            super(holder);
        }
    }

    /**
     * Back aisle to front aisle; one string per layer from the ground up. A steel solid casing (hatches allowed),
     * F steel frame, P steel pipe casing, G steel gearbox, V tempered glass, T steel hull (fluid tank), L LV casing
     * (power house), Y yellow stripes, K controller, . anything.
     */
    public static final String[][] COLLECTOR = {
            { "YAYAYAY", ".......", ".......", ".......", ".......", ".......", ".......", ".......", ".......",
                    ".......", ".......", ".......", "......." },
            { "AAAAAAA", ".F...F.", ".F...F.", ".F...F.", ".FFFFF.", ".......", ".......", ".......", ".......",
                    ".......", ".......", ".......", "......." },
            { "YAAAAAY", "T..G..L", "T.VVV.L", ".......", ".F...F.", "..F.F..", "..F.F..", "..F.F..", "..FFF..",
                    "...F...", "...F...", "..AAA..", "......." },
            { "AAAAAAA", "APPPGPA", "T.VPV.L", "...P...", ".F.P.F.", "...P...", "...P...", "...P...", "..FPF..",
                    "..FPF..", "..FPF..", "..AGA..", "...G..." },
            { "YAAAAAY", "T..G..L", "T.VVV.L", ".......", ".F...F.", "..F.F..", "..F.F..", "..F.F..", "..FFF..",
                    "...F...", "...F...", "..AAA..", "......." },
            { "AAAAAAA", ".F...F.", ".F...F.", ".F...F.", ".FFFFF.", ".......", ".......", ".......", ".......",
                    ".......", ".......", ".......", "......." },
            { "YAAAAAY", "..AKA..", ".......", ".......", ".......", ".......", ".......", ".......", ".......",
                    ".......", ".......", ".......", "......." },
    };

    /**
     * A stable titanium casing (hatches allowed), F titanium frame, P titanium pipe casing, G titanium gearbox,
     * V laminated glass, E EV casing (sweep ring), T inert PTFE casing (tanks), Y yellow stripes, K controller.
     */
    public static final String[][] LARGE_COLLECTOR = {
            { ".............", ".............", ".............", "YAYAYAAAYAYAY", ".A.........A.", ".T.........T.",
                    ".T.........T.", ".............", ".............", ".............", ".............",
                    ".............", ".............", ".............", ".............", ".............",
                    ".............", "............." },
            { ".F.........F.", ".F.........F.", ".F.........F.", "AAAAAAAAAAAAA", "TTPPPPPPPPTTT", "TTT.......TTT",
                    "TTT.......TTT", ".P.........P.", ".............", ".............", ".............",
                    ".............", ".............", ".............", ".............", ".............",
                    ".............", "............." },
            { ".............", ".............", ".............", "YAAAAAAAAAAAY", ".TF.......FT.", ".TF.......FT.",
                    ".TF.......FT.", "..F..EEE..F..", "..FFFFFFFFF..", ".............", ".............",
                    ".............", ".............", ".............", ".............", ".............",
                    ".............", "............." },
            { ".....AAA.....", ".....AAA.....", ".....AAA.....", "AAAAAAAAAAAAA", ".............", ".............",
                    ".............", "....E...E....", "..F.......F..", ".............", ".............",
                    ".............", ".............", ".............", ".............", ".............",
                    ".............", "............." },
            { "....AA.AA....", "....AA.AA....", "....AA.AA....", "YAAAAAAAAAAAY", ".............", ".............",
                    ".............", "...E.....E...", "..F.......F..", "....F...F....", "....F...F....",
                    "....F...F....", "....F...F....", "....FFFFF....", ".............", ".............",
                    ".............", "............." },
            { "...AA...AA...", "...AA...AA...", "...AA...AA...", "AAAAAAAAAAAAA", "......G......", ".....VVV.....",
                    ".....VVV.....", "..E.......E..", "..F.......F..", "......G......", ".............",
                    ".............", ".............", "....F...F....", ".....F.F.....", ".....F.F.....",
                    ".....AAA.....", "............." },
            { "...A..P..A...", "...A..P..A...", "...A..P..A...", "YAAAAAPAAAAAY", ".....GPG.....", ".....VPV.....",
                    ".....VPV.....", "..E...P...E..", "..F...P...F..", ".....GPG.....", "......P......",
                    "......P......", "......P......", "....F.P.F....", "......P......", "......P......",
                    ".....AGA.....", "......G......" },
            { "...AA...AA...", "...AA...AA...", "...AA...AA...", "AAAAAAAAAAAAA", "......G......", ".....VVV.....",
                    ".....VVV.....", "..E.......E..", "..F.......F..", "......G......", ".............",
                    ".............", ".............", "....F...F....", ".....F.F.....", ".....F.F.....",
                    ".....AAA.....", "............." },
            { "....AA.AA....", "....AA.AA....", "....AA.AA....", "YAAAAAAAAAAAY", ".............", ".............",
                    ".............", "...E.....E...", "..F.......F..", "....F...F....", "....F...F....",
                    "....F...F....", "....F...F....", "....FFFFF....", ".............", ".............",
                    ".............", "............." },
            { ".....AAA.....", ".....AAA.....", ".....AAA.....", "AAAAAAAAAAAAA", ".............", ".............",
                    ".............", "....E...E....", "..F.......F..", ".............", ".............",
                    ".............", ".............", ".............", ".............", ".............",
                    ".............", "............." },
            { ".............", ".............", ".............", "YAAAAAAAAAAAY", "AAA.......AAA", "AAA.......AAA",
                    "AAA.......AAA", "..F..EEE..F..", "..FFFFFFFFF..", ".............", ".............",
                    ".............", ".............", ".............", ".............", ".............",
                    ".............", "............." },
            { ".F.........F.", ".F.........F.", ".F.........F.", "AAAAAAAAAAAAA", "AAA.......AAA", "AAV.......AAA",
                    "AAA.......AAA", ".............", ".............", ".............", ".............",
                    ".............", ".............", ".............", ".............", ".............",
                    ".............", "............." },
            { ".............", ".............", ".............", "YAYAAAYAAAYAY", "AAA..AKA..AAA", "VVV.......AAA",
                    "AAA.......AAA", ".............", ".............", ".............", ".............",
                    ".............", ".............", ".............", ".............", ".............",
                    ".............", "............." },
    };

    public static final MultiblockMachineDefinition FRAGMENT_COLLECTOR = GTIAMod.REGISTRATE
            .multiblock("fragment_collector", WorkableElectricMultiblockMachine::new)
            .langValue("Fragment Collector")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTIARecipeTypes.FRAGMENT_COLLECTOR)
            .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT_SUBTICK)
            .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
            .pattern(definition -> {
                var builder = FactoryBlockPattern.start();
                for (String[] aisle : COLLECTOR) builder.aisle(aisle);
                return builder
                        .where('K', Predicates.controller(blocks(definition.getBlock())))
                        .where('A', blocks(GTBlocks.CASING_STEEL_SOLID.get()).setMinGlobalLimited(40)
                                .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                                .or(Predicates.autoAbilities(true, false, false)))
                        .where('F', Predicates.frames(GTMaterials.Steel))
                        .where('P', blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                        .where('G', blocks(GTBlocks.CASING_STEEL_GEARBOX.get()))
                        .where('V', blocks(GTBlocks.CASING_TEMPERED_GLASS.get()))
                        .where('T', blocks(GTBlocks.STEEL_HULL.get()))
                        .where('L', blocks(GTBlocks.MACHINE_CASING_LV.get()))
                        .where('Y', blocks(GTBlocks.YELLOW_STRIPES_BLOCK_A.get()))
                        .where('.', Predicates.any())
                        .build();
            })
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/large_miner"))
            .tooltips(GTIATooltips.multiblock("fragment_collector"))
            .register();

    public static final MultiblockMachineDefinition LARGE_FRAGMENT_COLLECTOR = GTIAMod.REGISTRATE
            .multiblock("large_fragment_collector", Large::new)
            .langValue("Large Fragment Collector")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTIARecipeTypes.FRAGMENT_COLLECTOR)
            .recipeModifiers(GTRecipeModifiers.PARALLEL_HATCH, GTRecipeModifiers.OC_NON_PERFECT_SUBTICK)
            .appearanceBlock(GTBlocks.CASING_TITANIUM_STABLE)
            .pattern(definition -> {
                var builder = FactoryBlockPattern.start();
                for (String[] aisle : LARGE_COLLECTOR) builder.aisle(aisle);
                return builder
                        .where('K', Predicates.controller(blocks(definition.getBlock())))
                        .where('A', blocks(GTBlocks.CASING_TITANIUM_STABLE.get()).setMinGlobalLimited(200)
                                .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                                .or(Predicates.autoAbilities(true, false, true)))
                        .where('F', Predicates.frames(GTMaterials.Titanium))
                        .where('P', blocks(GTBlocks.CASING_TITANIUM_PIPE.get()))
                        .where('G', blocks(GTBlocks.CASING_TITANIUM_GEARBOX.get()))
                        .where('V', blocks(GTBlocks.CASING_LAMINATED_GLASS.get()))
                        .where('E', blocks(GTBlocks.MACHINE_CASING_EV.get()))
                        .where('T', blocks(GTBlocks.CASING_PTFE_INERT.get()))
                        .where('Y', blocks(GTBlocks.YELLOW_STRIPES_BLOCK_A.get()))
                        .where('.', Predicates.any())
                        .build();
            })
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_stable_titanium"),
                    GTCEu.id("block/multiblock/large_miner"))
            .tooltips(GTIATooltips.multiblock("large_fragment_collector"))
            .register();

    private FragmentCollectors() {}

    public static void init() {
        GTIARecipeTypes.FRAGMENT_COLLECTOR.addCustomRecipeLogic(FragmentCollectorLogic.INSTANCE);
    }
}
