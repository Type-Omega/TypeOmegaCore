package com.typeomega.core.mega.nidavellir;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;

import com.typeomega.core.mega.common.MegaDefinition;
import com.typeomega.core.mega.common.MegaDefinition.Hatch;

/** Registration of the Nidavellir Hyperforge; loaded only with GTNA (see TypeOmegaMegastructures). */
public final class NidavellirDefinition {

    private NidavellirDefinition() {}

    /** The controller's hatches take the alloys' inputs and the neutronium, and gives the alloys. */
    public static MultiblockMachineDefinition register() {
        return MegaDefinition.register(NidavellirMachine.SHAPE, NidavellirMachine::new,
                GTCEu.id("block/casings/fusion/fusion_casing_mk3"), NidavellirMachine::types,
                new Hatch(PartAbility.IMPORT_ITEMS, 2), new Hatch(PartAbility.IMPORT_FLUIDS, 2),
                new Hatch(PartAbility.EXPORT_ITEMS, 2), new Hatch(PartAbility.EXPORT_FLUIDS, 2));
    }
}
