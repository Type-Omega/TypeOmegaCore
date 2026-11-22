package com.typeomega.core.mega.qso;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;

import com.typeomega.core.mega.common.MegaDefinition;

/** Registration of the Quasi-Stellar Obliterator; loaded only with GTNA (see GTIAMegastructures). */
public final class QsoDefinition {

    private QsoDefinition() {}

    /** No hatches: it draws from the Nexus network and its harvest is negentropy. */
    public static MultiblockMachineDefinition register() {
        return MegaDefinition.register(QsoMachine.SHAPE, QsoMachine::new, 3,
                GTCEu.id("block/casings/fusion/fusion_casing_mk3"));
    }
}
