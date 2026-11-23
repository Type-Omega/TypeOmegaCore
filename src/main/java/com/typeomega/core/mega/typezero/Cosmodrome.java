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
 * Mega 2 of Type 0, the Cosmodrome (GTIA-D-390, D-420): an 11 x 9 x 9 mission control hall with a tracking dish,
 * linked to a Rocket Assembler within {@link CosmodromeMachine#LINK_RANGE} blocks. Raises the team's cap to 0.99
 * (GTIA-D-389) and launches the Lunar Probe whose survey opens the Moon.
 */
public final class Cosmodrome {

    public static final String ID = "cosmodrome";

    /**
     * Back aisle (tracking dish) to front aisle (controller); in each aisle, one string per layer from the ground up.
     * S solid steel plinth, C clean stainless casing (hatches allowed), W mission control casing (console rows),
     * L lamps (screen wall), G laminated glass, F stainless steel frame (dish mast and feed), D frost-proof aluminium
     * (dish), K controller, . anything.
     */
    public static final String[][] AISLES = {
            { "...SSSSS...", "...........", "...........", "...........", "...........", "...........",
                    ".....D.....", "....DDD....", "..........." },
            { "...SSSSS...", ".....F.....", ".....F.....", ".....F.....", ".....F.....", ".....F.....",
                    "....DDD....", "...D.F.D...", ".....F....." },
            { "...SSSSS...", "...........", "...........", "...........", "...........", "...........",
                    ".....D.....", "....DDD....", "..........." },
            { "SSSSSSSSSSS", ".CCCCCCCCC.", ".CLLLLLLLC.", ".CLLLLLLLC.", ".CCCCCCCCC.", "..CCCCCCC..",
                    "...........", "...........", "..........." },
            { "SSSSSSSSSSS", ".C.......C.", ".G.......G.", ".C.......C.", ".CCCCCCCCC.", "..CCCCCCC..",
                    "...........", "...........", "..........." },
            { "SSSSSSSSSSS", ".C.WWWWW.C.", ".G.......G.", ".C.......C.", ".CCCGGGCCC.", "..CCCCCCC..",
                    "...........", "...........", "..........." },
            { "SSSSSSSSSSS", ".C.......C.", ".G.......G.", ".C.......C.", ".CCCCCCCCC.", "..CCCCCCC..",
                    "...........", "...........", "..........." },
            { "SSSSSSSSSSS", ".C.WWWWW.C.", ".G.......G.", ".C.......C.", ".CCCGGGCCC.", "..CCCCCCC..",
                    "...........", "...........", "..........." },
            { "SSSSSSSSSSS", ".CCCCKCCCC.", ".CGGGGGGGC.", ".CCCCCCCCC.", ".CCCCCCCCC.", "..CCCCCCC..",
                    "...........", "...........", "..........." },
    };

    public static final MultiblockMachineDefinition DEFINITION = GTIAMod.REGISTRATE
            .multiblock(ID, CosmodromeMachine::new)
            .langValue("Cosmodrome")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTIARecipeTypes.MISSION_CONTROL)
            .recipeModifiers(CosmodromeMachine::requireLink, GTRecipeModifiers.OC_NON_PERFECT_SUBTICK)
            .appearanceBlock(GTBlocks.CASING_STAINLESS_CLEAN)
            .pattern(definition -> {
                var builder = FactoryBlockPattern.start();
                for (String[] aisle : AISLES) builder.aisle(aisle);
                return builder
                        .where('K', Predicates.controller(blocks(definition.getBlock())))
                        .where('C', blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()).setMinGlobalLimited(90)
                                .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                                .or(Predicates.autoAbilities(true, false, false)))
                        .where('W', blocks(TypeZeroBlocks.MISSION_CONTROL_CASING.get()))
                        .where('S', blocks(GTBlocks.CASING_STEEL_SOLID.get()))
                        .where('L', Predicates.anyLamp())
                        .where('G', blocks(GTBlocks.CASING_LAMINATED_GLASS.get()))
                        .where('F', Predicates.frames(GTMaterials.StainlessSteel))
                        .where('D', blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get()))
                        .where('.', Predicates.any())
                        .build();
            })
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_clean_stainless_steel"),
                    GTCEu.id("block/multiblock/research_station"))
            .tooltips(GTIATooltips.multiblock(ID))
            .register();

    private Cosmodrome() {}

    public static void init() {}
}
