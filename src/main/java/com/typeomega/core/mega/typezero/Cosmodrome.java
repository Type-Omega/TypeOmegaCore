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
 * Mega 2 of Type 0, the Cosmodrome (GTIA-D-390, D-420, D-425 option C): a 23 x 18 x 23 amphitheatre mission control
 * hall with two tracking dishes, a radome and a telemetry mast, linked to a Rocket Assembler within
 * {@link CosmodromeMachine#LINK_RANGE} blocks. Raises the team's cap to 0.99 (GTIA-D-389) and launches the Lunar Probe
 * whose survey opens the Moon.
 */
public final class Cosmodrome {

    public static final String ID = "cosmodrome";

    /** The layout, legend in {@code data/typeomega/multiblock/typezero/cosmodrome.txt}. */
    public static final String[][] AISLES = TypeZeroPattern.read(ID);

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
                        // EN: 777 casings; up to ~60 become hatches anywhere on them, Energy Hatch included.
                        // PT: 777 cascos; até ~60 viram hatches em qualquer um deles, inclusive o Energy Hatch.
                        // ZH: 共 777 个外壳；其中最多约 60 个可在任意位置替换为仓口，包括能源仓。
                        .where('C', blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()).setMinGlobalLimited(717)
                                .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                                .or(Predicates.autoAbilities(true, false, false)))
                        .where('S', blocks(GTBlocks.LIGHT_CONCRETE.get()))
                        .where('W', blocks(TypeZeroBlocks.CONSOLE_CASING.get()))
                        .where('L', Predicates.lampsByColor(DyeColor.CYAN).or(Predicates.anyLamp()))
                        .where('G', blocks(GTBlocks.CASING_LAMINATED_GLASS.get()))
                        .where('F', Predicates.frames(GTMaterials.StainlessSteel))
                        .where('D', blocks(GTBlocks.LARGE_METAL_SHEETS.get(DyeColor.WHITE).get()))
                        .where('M', blocks(GTBlocks.CASING_STEEL_GEARBOX.get()))
                        .where('V', blocks(GTBlocks.MACHINE_CASING_HV.get()))
                        .where('R', blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                        .where('O', blocks(GTBlocks.METAL_SHEETS.get(DyeColor.ORANGE).get()))
                        .where('Y', Predicates.lampsByColor(DyeColor.RED).or(Predicates.anyLamp()))
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
