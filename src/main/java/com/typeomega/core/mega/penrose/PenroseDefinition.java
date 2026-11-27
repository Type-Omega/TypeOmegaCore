package com.typeomega.core.mega.penrose;

import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.mega.common.MegaDefinition;

/** Registration of the Penrose Sphere; loaded only with GTNA (see TypeOmegaMegastructures). */
public final class PenroseDefinition {

    private PenroseDefinition() {}

    public static MultiblockMachineDefinition register() {
        return MegaDefinition.register(PenroseMachine.SHAPE, PenroseMachine::new,
                TypeOmegaMod.id("block/casings/lifter_girdle_hull"));
    }
}
