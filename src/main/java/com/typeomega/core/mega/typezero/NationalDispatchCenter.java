package com.typeomega.core.mega.typezero;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
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
 * Mega 1 of Type 0, the National Dispatch Center (GTIA-D-390, D-419): an 11 x 6 x 9 grid control hall behind a
 * substation yard. It raises the team's Kardashev cap to 0.89 (GTIA-D-389) and programs the Grid Transponders that
 * every wireless energy hatch needs (GTIA-D-401).
 */
public final class NationalDispatchCenter {

    public static final String ID = "national_dispatch_center";

    /**
     * Back aisle (substation yard) to front aisle (controller); in each aisle, one string per layer from the ground up.
     * S solid steel plinth, C clean stainless casing (hatches allowed), G cleanroom glass, L lamps (display wall),
     * T frost-proof aluminium (transformer tanks), R steel pipe (radiators), F aluminium frame (pylons and busbars),
     * K controller, . anything.
     */
    public static final String[][] AISLES = {
            { "SSSSSSSSSSS", ".F.......F.", ".F.......F.", ".F.......F.", ".FFFFFFFFF.", "..........." },
            { "SSSSSSSSSSS", ".TTR...RTT.", ".TTR...RTT.", "..F.....F..", "..F.....F..", "..........." },
            { "SSSSSSSSSSS", ".TTR...RTT.", ".TTR...RTT.", "...........", "...........", "..........." },
            { "SSSSSSSSSSS", ".F.......F.", ".F.......F.", ".F.......F.", ".FFFFFFFFF.", "..........." },
            { "SSSSSSSSSSS", "..CCCCCCC..", "..CLLLLLC..", "..CCCCCCC..", "..CCCCCCC..", "...CCCCC..." },
            { "SSSSSSSSSSS", "..C.....C..", "..G.....G..", "..C.....C..", "..CCCCCCC..", "...CCCCC..." },
            { "SSSSSSSSSSS", "..C.....C..", "..G.....G..", "..C.....C..", "..CCGGGCC..", "...CCCCC..." },
            { "SSSSSSSSSSS", "..C.....C..", "..G.....G..", "..C.....C..", "..CCCCCCC..", "...CCCCC..." },
            { "SSSSSSSSSSS", "..CCCKCCC..", "..CGGGGGC..", "..CCCCCCC..", "..CCCCCCC..", "...CCCCC..." },
    };

    public static final MultiblockMachineDefinition DEFINITION = GTIAMod.REGISTRATE
            .multiblock(ID, holder -> new TypeZeroMegaMachine(holder, ID))
            .langValue("National Dispatch Center")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTIARecipeTypes.GRID_DISPATCH)
            .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT_SUBTICK)
            .appearanceBlock(GTBlocks.CASING_STAINLESS_CLEAN)
            .pattern(definition -> {
                var builder = FactoryBlockPattern.start();
                for (String[] aisle : AISLES) builder.aisle(aisle);
                return builder
                        .where('K', Predicates.controller(blocks(definition.getBlock())))
                        .where('C', blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()).setMinGlobalLimited(70)
                                .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                                .or(Predicates.autoAbilities(true, false, false)))
                        .where('S', blocks(GTBlocks.CASING_STEEL_SOLID.get()))
                        .where('G', blocks(GTBlocks.CLEANROOM_GLASS.get()))
                        .where('L', Predicates.anyLamp())
                        .where('T', blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get()))
                        .where('R', blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                        .where('F', Predicates.frames(GTMaterials.Aluminium))
                        .where('.', Predicates.any())
                        .build();
            })
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_clean_stainless_steel"),
                    GTCEu.id("block/multiblock/power_substation"))
            .tooltips(GTIATooltips.multiblock(ID))
            .register();

    private NationalDispatchCenter() {}

    public static void init() {}
}
