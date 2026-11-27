package com.typeomega.core.mega.gateway;

import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.mega.common.MegaDefinition;
import com.typeomega.core.mega.common.MegaDefinition.Hatch;

/** Registration of the Basement Gateway; loaded only with GTNA (see TypeOmegaMegastructures). */
public final class BasementGatewayDefinition {

    private BasementGatewayDefinition() {}

    /** Exports condensed hydrogen/helium and heavy r-process dusts from the basement-universe wormhole. */
    public static MultiblockMachineDefinition register() {
        return MegaDefinition.register(BasementGatewayMachine.SHAPE, BasementGatewayMachine::new,
                TypeOmegaMod.id("block/casings/neutron_shield_casing"),
                new Hatch(PartAbility.EXPORT_ITEMS, 2), new Hatch(PartAbility.EXPORT_FLUIDS, 2));
    }
}
