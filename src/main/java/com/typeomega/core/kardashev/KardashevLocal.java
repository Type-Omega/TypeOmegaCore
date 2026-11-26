package com.typeomega.core.kardashev;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;

import net.minecraftforge.fml.ModList;

import java.util.UUID;

/**
 * Energy the GTCEu generators put in their own buffers (D-443): the single-block generators, the large turbines and
 * engines, the Tokamak. The energy container reports what a recipe output into it; this decides whose it is and
 * whether it counts here or in the wireless network.
 */
public final class KardashevLocal {

    private static final boolean GTNA = ModList.get() != null && ModList.get().isLoaded("gtna");

    private KardashevLocal() {}

    /**
     * Counts {@code eu} a recipe just put into a container of {@code machine}, unless that energy goes on to the
     * team's wireless network, which counts it itself when it takes it (so it is never counted twice).
     */
    public static void generated(MetaMachine machine, long eu) {
        if (eu <= 0) return;
        UUID owner = ownerOf(machine);
        if (owner == null || routedToWireless(machine)) return;
        KardashevLedger.record(owner, KardashevSource.GENERATOR, eu);
    }

    /** Whether the energy that reaches {@code machine} is moved on into the wireless energy network. */
    public static boolean routedToWireless(MetaMachine machine) {
        return GTNA && KardashevGtna.routedToWireless(machine);
    }

    /** The part's own owner, or the one of its controller when the part was placed without one (a built structure). */
    static UUID ownerOf(MetaMachine machine) {
        UUID owner = machine.getOwnerUUID();
        if (owner != null || !(machine instanceof IMultiPart part)) return owner;
        for (var controller : part.getControllers()) {
            UUID fromController = controller.self().getOwnerUUID();
            if (fromController != null) return fromController;
        }
        return null;
    }
}
