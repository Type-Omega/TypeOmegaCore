package com.typeomega.core.mega.lifter;

import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.mega.common.MegaDefinition;
import com.typeomega.core.mega.common.MegaDefinition.Hatch;

/** Registration of the Star Lifter; loaded only with GTNA (see TypeOmegaMegastructures). */
public final class StarLifterDefinition {

    private StarLifterDefinition() {}

    /** The controller's hatches take repair blocks and gives plasma, gases and dusts. */
    public static MultiblockMachineDefinition register() {
        return MegaDefinition.register(StarLifterMachine.SHAPE, StarLifterMachine::new,
                TypeOmegaMod.id("block/casings/lifter_girdle_hull"),
                new Hatch(PartAbility.IMPORT_ITEMS, 2), new Hatch(PartAbility.EXPORT_FLUIDS, 4),
                new Hatch(PartAbility.EXPORT_ITEMS, 2));
    }
}
