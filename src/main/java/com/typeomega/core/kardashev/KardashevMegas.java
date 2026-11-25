package com.typeomega.core.kardashev;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

/**
 * Megastructures report to the meter when they form (D-389, D-390): the team of the controller's owner gets
 * that megastructure counted once, and the cap rises.
 */
public final class KardashevMegas {

    private KardashevMegas() {}

    /**
     * Counts {@code machine} as the megastructure {@code id} of its owner's team.
     *
     * @return whether this was the first time the team formed it
     */
    public static boolean report(MetaMachine machine, String id) {
        UUID owner = machine.getOwnerUUID();
        if (owner == null || !(machine.getLevel() instanceof ServerLevel level)) return false;
        return KardashevData.get(level.getServer()).markBuilt(KardashevLedger.teamOf(owner), id,
                level.getServer().overworld().getGameTime());
    }
}
