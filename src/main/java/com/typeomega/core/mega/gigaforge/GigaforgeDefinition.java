package com.typeomega.core.mega.gigaforge;

import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.mega.common.MegaDefinition;
import com.typeomega.core.mega.common.MegaDefinition.Hatch;

/** Registration of the Neutronium Gigaforge; loaded only with GTNA (see TypeOmegaMegastructures). */
public final class GigaforgeDefinition {

    private GigaforgeDefinition() {}

    public static MultiblockMachineDefinition register() {
        return MegaDefinition.register(GigaforgeMachine.SHAPE, GigaforgeMachine::new,
                TypeOmegaMod.id("block/casings/forge_cobalt_plate"),
                new Hatch(PartAbility.EXPORT_ITEMS, 4), new Hatch(PartAbility.EXPORT_FLUIDS, 2));
    }
}
