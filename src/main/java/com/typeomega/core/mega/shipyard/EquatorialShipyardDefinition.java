package com.typeomega.core.mega.shipyard;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;

import com.typeomega.core.mega.common.MegaDefinition;
import com.typeomega.core.mega.common.MegaDefinition.Hatch;

/** Registration of the Equatorial Shipyard (D-446 A6); loaded only with GTNA (see TypeOmegaMegastructures). */
public final class EquatorialShipyardDefinition {

    private EquatorialShipyardDefinition() {}

    /** Smaller twin-ring sibling of the Assembly Yard: inner ring harvests, outer ring assembles. */
    public static MultiblockMachineDefinition register() {
        return MegaDefinition.register(EquatorialShipyardMachine.SHAPE, EquatorialShipyardMachine::new,
                GTCEu.id("block/casings/fusion/fusion_casing_mk2"), EquatorialShipyardMachine::types,
                new Hatch(PartAbility.IMPORT_ITEMS, 4), new Hatch(PartAbility.IMPORT_FLUIDS, 2),
                new Hatch(PartAbility.EXPORT_ITEMS, 2));
    }
}
