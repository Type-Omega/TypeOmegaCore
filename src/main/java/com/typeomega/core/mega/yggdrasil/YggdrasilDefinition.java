package com.typeomega.core.mega.yggdrasil;

import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.mega.common.MegaDefinition;
import com.typeomega.core.mega.common.MegaDefinition.Hatch;

/** Registration of the Yggdrasil Orchid; loaded only with GTNA (see TypeOmegaMegastructures). */
public final class YggdrasilDefinition {

    private YggdrasilDefinition() {}

    /** The controller's hatches take seeds and water and gives the harvest and the biomass. */
    public static MultiblockMachineDefinition register() {
        return MegaDefinition.register(YggdrasilMachine.SHAPE, YggdrasilMachine::new,
                TypeOmegaMod.id("block/casings/orchid_window_rim"), YggdrasilMachine::types,
                new Hatch(PartAbility.IMPORT_ITEMS, 2), new Hatch(PartAbility.IMPORT_FLUIDS, 2),
                new Hatch(PartAbility.EXPORT_ITEMS, 2), new Hatch(PartAbility.EXPORT_FLUIDS, 1));
    }
}
