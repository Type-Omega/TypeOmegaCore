package com.typeomega.core.mega.yggdrasil;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;

import com.typeomega.core.mega.common.MegaDefinition;
import com.typeomega.core.mega.common.MegaDefinition.Hatch;

/** Registration of the Yggdrasil Orchid; loaded only with GTNA (see GTIAMegastructures). */
public final class YggdrasilDefinition {

    private YggdrasilDefinition() {}

    /** The station takes seeds and water and gives the harvest and the biomass. */
    public static MultiblockMachineDefinition register() {
        return MegaDefinition.register(YggdrasilMachine.SHAPE, YggdrasilMachine::new,
                GTCEu.id("block/casings/fusion/fusion_casing_mk3"), YggdrasilMachine::types,
                new Hatch(PartAbility.IMPORT_ITEMS, 2), new Hatch(PartAbility.IMPORT_FLUIDS, 2),
                new Hatch(PartAbility.EXPORT_ITEMS, 2), new Hatch(PartAbility.EXPORT_FLUIDS, 1));
    }
}
