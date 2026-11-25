package com.typeomega.core.mega.typezero;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import com.typeomega.core.GTIAMod;
import com.typeomega.core.data.GTIARecipeTypes;
import com.typeomega.core.data.GTIATooltips;

import static com.gregtechceu.gtceu.api.pattern.Predicates.abilities;
import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

/**
 * Mega 3 of Type 0, the HV Tokamak (GTIA-D-379, D-390, D-421, D-425 option C): a 23 x 17 x 27 torus with a D-shaped
 * section. The titanium vacuum chamber is lined by the lithium blanket over a divertor; eight MgB2 toroidal field
 * coils, the central solenoid and two poloidal rings hold the plasma; two neutral beam injectors heat it; a liquid
 * helium manifold and the cryostat cage cool the magnets; the neutron shield wall in front carries the hatches.
 * Closes Type 0 and opens the Type I cap (GTIA-D-389).
 */
public final class Tokamak {

    public static final String ID = "tokamak_hv";
    // EN: One HV Dynamo Hatch carries 1 A, so the structure needs one per amp of the strongest fuel (D-He3, 4 A).
    // PT: Um Dynamo Hatch HV leva 1 A, então a estrutura pede um por ampere do combustível mais forte (D-He3, 4 A).
    // ZH: 一个 HV 动力仓承载 1 A，因此结构按最强燃料（D-He3，4 A）的安培数各需一个。
    public static final int DYNAMOS = (int) TypeZeroRecipes.DHE3_AMPS;

    /** The layout, legend in {@code data/typeomega/multiblock/typezero/tokamak_hv.txt}. */
    public static final String[][] AISLES = TypeZeroPattern.read(ID);

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
                        .where('H', blocks(TypeZeroBlocks.NEUTRON_SHIELD_CASING.get()).setMinGlobalLimited(140)
                                // EN: Exactly DYNAMOS HV dynamos, one per amp of D-He3 (GTIA-D-438): a smaller
                                // buffer could not take the EU of one tick. One Energy Hatch charges the start-up.
                                // PT: Exatamente DYNAMOS dínamos HV, um por ampere da D-He3 (GTIA-D-438): um buffer
                                // menor não comportaria o EU de um tick. Um Energy Hatch carrega a partida.
                                // ZH: 恰好 DYNAMOS 个 HV 动力仓，D-He3 每安培一个（GTIA-D-438）：更小的缓存装不下
                                // 一个 tick 的 EU。一个能源仓为启动充能。
                                .or(blocks(GTMachines.ENERGY_OUTPUT_HATCH[GTValues.HV]
                                        .getBlock())
                                        .setExactLimit(DYNAMOS).setPreviewCount(DYNAMOS))
                                .or(abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1)
                                        .setMaxGlobalLimited(2).setPreviewCount(1))
                                .or(Predicates.autoAbilities(definition.getRecipeTypes(), false, false, true, false,
                                        true, true))
                                .or(Predicates.autoAbilities(true, false, false)))
                        .where('S', blocks(GTBlocks.DARK_CONCRETE.get()))
                        .where('C', blocks(GTBlocks.CASING_TITANIUM_STABLE.get()))
                        .where('X', blocks(TypeZeroBlocks.LITHIUM_BREEDING_BLANKET.get()))
                        .where('U', blocks(GTBlocks.FIREBOX_TITANIUM.get()))
                        .where('B', blocks(TypeZeroBlocks.TOKAMAK_MAGNET_COIL.get()))
                        .where('P', blocks(GTBlocks.CASING_TITANIUM_PIPE.get()))
                        .where('A', blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get()))
                        .where('F', Predicates.frames(GTMaterials.StainlessSteel))
                        .where('N', blocks(GTBlocks.MACHINE_CASING_HV.get()))
                        .where('G', blocks(GTBlocks.CASING_LAMINATED_GLASS.get()))
                        .where('Z', blocks(GTBlocks.MAGNETIC_HAZARD_SIGN_BLOCK.get()))
                        .where('.', Predicates.any())
                        .build();
            })
            .workableCasingModel(GTIAMod.id("block/casings/neutron_shield_casing"),
                    com.gregtechceu.gtceu.GTCEu.id("block/multiblock/fusion_reactor"))
            .tooltipBuilder(GTIATooltips.lazy(ID))
            .register();

    private Tokamak() {}

    public static void init() {}
}
