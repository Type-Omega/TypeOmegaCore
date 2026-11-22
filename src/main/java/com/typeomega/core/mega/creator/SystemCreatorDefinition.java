package com.typeomega.core.mega.creator;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;

import com.typeomega.core.mega.common.MegaDefinition;
import com.typeomega.core.mega.common.MegaDefinition.Hatch;

/** Registration of the Interstellar System Creator; loaded only with GTNA (see GTIAMegastructures). */
public final class SystemCreatorDefinition {

    private SystemCreatorDefinition() {}

    /** The station takes hydrogen (gas or plasma) and the iron dust that seeds the metals. */
    public static MultiblockMachineDefinition register() {
        return MegaDefinition.register(SystemCreatorMachine.SHAPE, SystemCreatorMachine::new, 3,
                GTCEu.id("block/casings/fusion/fusion_casing_mk3"),
                new Hatch(PartAbility.IMPORT_FLUIDS, 4), new Hatch(PartAbility.IMPORT_ITEMS, 4));
    }
}
