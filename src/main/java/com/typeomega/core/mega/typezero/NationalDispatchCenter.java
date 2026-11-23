package com.typeomega.core.mega.typezero;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;

import net.minecraft.world.item.DyeColor;

import com.typeomega.core.GTIAMod;
import com.typeomega.core.data.GTIARecipeTypes;
import com.typeomega.core.data.GTIATooltips;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

/**
 * Mega 1 of Type 0, the National Dispatch Center (GTIA-D-390, D-419, D-425 option B): a 17 x 18 x 17 substation
 * yard, cable trench, stepped control building and microwave tower. It raises the team's Kardashev cap to 0.89
 * (GTIA-D-389) and programs the Grid Transponders that every wireless energy hatch needs (GTIA-D-401).
 */
public final class NationalDispatchCenter {

    public static final String ID = "national_dispatch_center";

    /** The layout; legend in {@code data/typeomega/multiblock/typezero/national_dispatch_center.txt}. */
    public static final String[][] AISLES = TypeZeroPattern.read(ID);

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
                        // EN: 366 casings; up to ~40 become hatches anywhere on them, Energy Hatch included.
                        // PT: 366 cascos; até ~40 viram hatches em qualquer um deles, inclusive o Energy Hatch.
                        // ZH: 共 366 个外壳；其中最多约 40 个可在任意位置替换为仓口，包括能源仓。
                        .where('C', blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()).setMinGlobalLimited(326)
                                .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                                .or(Predicates.autoAbilities(true, false, false)))
                        .where('S', blocks(GTBlocks.LIGHT_CONCRETE.get()))
                        .where('F', Predicates.frames(GTMaterials.Aluminium))
                        .where('T', blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get()))
                        .where('R', blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                        .where('P', blocks(GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))
                        .where('V', blocks(GTBlocks.MACHINE_CASING_HV.get()))
                        .where('G', blocks(GTBlocks.CLEANROOM_GLASS.get()))
                        .where('L', Predicates.lampsByColor(DyeColor.LIME).or(Predicates.anyLamp()))
                        .where('W', blocks(TypeZeroBlocks.CONSOLE_CASING.get()))
                        .where('Q', blocks(GTBlocks.FILTER_CASING.get()))
                        .where('D', blocks(GTBlocks.LARGE_METAL_SHEETS.get(DyeColor.WHITE).get()))
                        .where('Z', blocks(GTBlocks.HIGH_VOLTAGE_HAZARD_SIGN_BLOCK.get()))
                        .where('Y', Predicates.lampsByColor(DyeColor.RED).or(Predicates.anyLamp()))
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
