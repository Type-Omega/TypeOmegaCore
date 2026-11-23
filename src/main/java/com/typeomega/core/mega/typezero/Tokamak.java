package com.typeomega.core.mega.typezero;

import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTBlocks;

import com.typeomega.core.GTIAMod;
import com.typeomega.core.data.GTIARecipeTypes;
import com.typeomega.core.data.GTIATooltips;

import static com.gregtechceu.gtceu.api.pattern.Predicates.abilities;
import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

/**
 * Mega 3 of Type 0, the HV Tokamak (GTIA-D-379, D-390, D-421): a 13 x 6 x 13 square torus. The titanium vacuum
 * chamber holds the plasma channel lined by the lithium blanket; four MgB2 toroidal field coils loop around it and a
 * central solenoid drives the current; liquid helium pipes cool the magnets; the neutron shield wall closes it and
 * carries the hatches. Closes Type 0 and opens the Type I cap (GTIA-D-389).
 */
public final class Tokamak {

    public static final String ID = "tokamak_hv";

    /**
     * Back aisle to front aisle (controller); in each aisle, one string per layer from the ground up. S solid steel
     * plinth, H neutron shield (hatches allowed), G laminated glass windows, C stable titanium chamber, X lithium
     * breeding blanket, B MgB2 magnet coil (toroidal coils and central solenoid), P titanium pipe (liquid helium
     * manifold), _ plasma channel (left open), K controller, . anything.
     */
    public static final String[][] AISLES = {
            { "SSSSSSSSSSSSS", "HHHHHHHHHHHHH", "HHHHHHHHHHHHH", "HHHHGGGGGHHHH", "HHHHHHHHHHHHH", "............." },
            { "SSSSSSSSSSSSS", "H.....B.....H", "H.....B.....H", "H.....B.....H", "H.....B.....H", "......B......" },
            { "SSSSSSSSSSSSS", "H.....B.....H", "H.CCCCCCCCC.H", "H.XXXXXXXXX.H", "H.CCCCCCCCC.H", "......B......" },
            { "SSSSSSSSSSSSS", "H.....B.....H", "H.CCCCCCCCC.H", "H.X_______X.H", "H.CCCCCCCCC.H", "...PPPBPPP..." },
            { "SSSSSSSSSSSSS", "H.....B.....H", "H.CCCCCCCCC.H", "G.X_XXXXX_X.G", "H.CCCCCCCCC.H", "...P..B..P..." },
            { "SSSSSSSSSSSSS", "H.....B.....H", "H.CCC.B.CCC.H", "G.X_X.B.X_X.G", "H.CCC.B.CCC.H", "...P..B..P..." },
            { "SSSSSSSSSSSSS", "HBBBBBBBBBBBH", "HBCCCBBBCCCBH", "GBX_XBBBX_XBG", "HBCCCBBBCCCBH", ".BBBBBBBBBBB." },
            { "SSSSSSSSSSSSS", "H.....B.....H", "H.CCC.B.CCC.H", "G.X_X.B.X_X.G", "H.CCC.B.CCC.H", "...P..B..P..." },
            { "SSSSSSSSSSSSS", "H.....B.....H", "H.CCCCCCCCC.H", "G.X_XXXXX_X.G", "H.CCCCCCCCC.H", "...P..B..P..." },
            { "SSSSSSSSSSSSS", "H.....B.....H", "H.CCCCCCCCC.H", "H.X_______X.H", "H.CCCCCCCCC.H", "...PPPBPPP..." },
            { "SSSSSSSSSSSSS", "H.....B.....H", "H.CCCCCCCCC.H", "H.XXXXXXXXX.H", "H.CCCCCCCCC.H", "......B......" },
            { "SSSSSSSSSSSSS", "H.....B.....H", "H.....B.....H", "H.....B.....H", "H.....B.....H", "......B......" },
            { "SSSSSSSSSSSSS", "HHHHHHHHHHHHH", "HHHHHHKHHHHHH", "HHHHGGGGGHHHH", "HHHHHHHHHHHHH", "............." },
    };

    public static final MultiblockMachineDefinition DEFINITION = GTIAMod.REGISTRATE
            .multiblock(ID, TokamakMachine::new)
            .langValue("HV Tokamak")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTIARecipeTypes.TOKAMAK_FUSION)
            .generator(true)
            // EN: Fixed output: the GTCEu overclock reads the dynamos as the voltage limit and would cancel fusion.
            // PT: Saída fixa: o overclock do GTCEu lê os dínamos como limite de tensão e cancelaria a fusão.
            // ZH: 输出固定：GTCEu 超频会把动力仓当作电压上限并取消聚变。
            .noRecipeModifier()
            .appearanceBlock(TypeZeroBlocks.NEUTRON_SHIELD_CASING)
            .pattern(definition -> {
                var builder = FactoryBlockPattern.start();
                for (String[] aisle : AISLES) builder.aisle(aisle);
                return builder
                        .where('K', Predicates.controller(blocks(definition.getBlock())))
                        .where('H', blocks(TypeZeroBlocks.NEUTRON_SHIELD_CASING.get()).setMinGlobalLimited(120)
                                // EN: Up to 4 dynamos: D-He3 gives 4 A of HV. One Energy Hatch charges the start-up.
                                // PT: Até 4 dínamos: D-He3 dá 4 A de HV. Um Energy Hatch carrega a partida.
                                // ZH: 最多 4 个动力仓：D-He3 输出 4 A HV。一个能源仓为启动充能。
                                .or(abilities(PartAbility.OUTPUT_ENERGY).setMinGlobalLimited(1)
                                        .setMaxGlobalLimited(4).setPreviewCount(1))
                                .or(abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1)
                                        .setMaxGlobalLimited(2).setPreviewCount(1))
                                .or(Predicates.autoAbilities(definition.getRecipeTypes(), false, false, true, false,
                                        true, true))
                                .or(Predicates.autoAbilities(true, false, false)))
                        .where('S', blocks(GTBlocks.CASING_STEEL_SOLID.get()))
                        .where('G', blocks(GTBlocks.CASING_LAMINATED_GLASS.get()))
                        .where('C', blocks(GTBlocks.CASING_TITANIUM_STABLE.get()))
                        .where('X', blocks(TypeZeroBlocks.LITHIUM_BREEDING_BLANKET.get()))
                        .where('B', blocks(TypeZeroBlocks.TOKAMAK_MAGNET_COIL.get()))
                        .where('P', blocks(GTBlocks.CASING_TITANIUM_PIPE.get()))
                        .where('_', Predicates.any())
                        .where('.', Predicates.any())
                        .build();
            })
            .workableCasingModel(GTIAMod.id("block/casings/neutron_shield_casing"),
                    com.gregtechceu.gtceu.GTCEu.id("block/multiblock/fusion_reactor"))
            .tooltips(GTIATooltips.multiblock(ID))
            .register();

    private Tokamak() {}

    public static void init() {}
}
