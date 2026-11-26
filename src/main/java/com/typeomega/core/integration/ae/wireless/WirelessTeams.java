package com.typeomega.core.integration.ae.wireless;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.common.machine.owner.MachineOwner;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

import com.typeomega.core.kardashev.KardashevLedger;

import java.util.UUID;

/** Who may use a wireless network and who may reconfigure a machine for it. */
final class WirelessTeams {

    private WirelessTeams() {}

    /** The team of a player: the FTB party when FTB Teams is loaded, the player otherwise (D-386, as the Meter). */
    static UUID teamOf(UUID player) {
        return KardashevLedger.teamOf(player);
    }

    static boolean canUse(UUID player, WirelessNetwork network) {
        return teamOf(player).equals(network.team());
    }

    /**
     * GregTech's own rule for a machine with an owner: its team, its friends and operators may open it, the setting
     * the server already chose with "only owner GUI". A machine that is not GregTech's has no owner.
     */
    static boolean canConfigure(ServerPlayer player, ServerLevel level, BlockPos pos) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (!(entity instanceof IMachineBlockEntity holder)) return true;
        MetaMachine machine = holder.getMetaMachine();
        return MachineOwner.canOpenOwnerMachine(player, machine);
    }

    /** Whether the machine's owner belongs to the network's team: another team's machine cannot join it. */
    static boolean ownerMayJoin(ServerLevel level, BlockPos pos, WirelessNetwork network) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (!(entity instanceof IMachineBlockEntity holder)) return true;
        MachineOwner owner = holder.getMetaMachine().getOwner();
        return owner == null || teamOf(owner.getPlayerUUID()).equals(network.team());
    }
}
