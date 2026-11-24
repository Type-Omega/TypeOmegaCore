package com.typeomega.core.mega.hraemc;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;

import com.typeomega.core.mega.common.MegaDefinition;
import com.typeomega.core.mega.common.MegaDefinition.Hatch;

/** Registration of the HRAE-MC; loaded only with GTNA (see GTIAMegastructures). */
public final class HraeDefinition {

    private HraeDefinition() {}

    /** The station's hatches take the matter fed to the disk. */
    public static MultiblockMachineDefinition register() {
        return MegaDefinition.register(HraeMachine.SHAPE, HraeMachine::new,
                GTCEu.id("block/casings/fusion/fusion_casing_mk3"),
                new Hatch(PartAbility.IMPORT_ITEMS, 4), new Hatch(PartAbility.IMPORT_FLUIDS, 4));
    }
}
