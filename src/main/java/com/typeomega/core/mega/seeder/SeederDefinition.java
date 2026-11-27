package com.typeomega.core.mega.seeder;

import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.mega.common.MegaDefinition;
import com.typeomega.core.mega.common.MegaDefinition.Hatch;

/** Registration of the Planetary Seeder Nexus; loaded only with GTNA (see TypeOmegaMegastructures). */
public final class SeederDefinition {

    private SeederDefinition() {}

    /** The controller's hatches take seeds and water and export the harvest and Biomass. */
    public static MultiblockMachineDefinition register() {
        return MegaDefinition.register(SeederMachine.SHAPE, SeederMachine::new,
                TypeOmegaMod.id("block/casings/orchid_window_rim"), SeederMachine::types,
                new Hatch(PartAbility.IMPORT_ITEMS, 2), new Hatch(PartAbility.IMPORT_FLUIDS, 2),
                new Hatch(PartAbility.EXPORT_ITEMS, 2), new Hatch(PartAbility.EXPORT_FLUIDS, 1));
    }
}
