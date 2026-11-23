package com.typeomega.core.kardashev;

import com.gregtechceu.gtceu.common.machine.owner.MachineOwner;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;

import com.typeomega.core.GTIAMod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.DoubleAdder;

/**
 * Collects what the sources report and moves it into {@link KardashevData} once a second.
 * <p>
 * Sources report themselves (mixins on the GTNA wireless energy and steam networks and on the GTCEu boilers), so
 * the meter never scans the world: the hot path is one map lookup and one add per report.
 */
public final class KardashevLedger {

    // EN: Flush once a second: owner-to-team lookups and the saved data are touched 20 times less than the
    // sources report, and a 20-second bucket still gets 20 flushes.
    // PT: Esvazia uma vez por segundo: a busca dono→time e os dados salvos são tocados 20 vezes menos que os
    // relatos das fontes, e cada balde de 20 s ainda recebe 20 esvaziamentos.
    // ZH: 每秒汇总一次：所有者到队伍的查询和存档数据的访问次数比来源上报少 20 倍，每个 20 秒的桶仍有 20 次汇总。
    static final int FLUSH_TICKS = 20;

    private static final Map<UUID, DoubleAdder> PENDING = new ConcurrentHashMap<>();

    private KardashevLedger() {}

    /** EU that entered the owner's grid (the GTNA wireless network counts the gross amount, before losses). */
    public static void recordEu(UUID owner, double eu) {
        if (owner == null || !(eu > 0)) return;
        PENDING.computeIfAbsent(owner, ignored -> new DoubleAdder()).add(eu);
    }

    /** Steam the owner produced, in mB; counted at 2 mB = 1 EU (GTIA-D-389). */
    public static void recordSteam(UUID owner, double steamMb) {
        recordEu(owner, KardashevScale.steamToEu(steamMb));
    }

    /** What {@code owner} reported since the last flush; for the GameTests. */
    public static double pending(UUID owner) {
        DoubleAdder adder = PENDING.get(owner);
        return adder == null ? 0 : adder.sum();
    }

    /** Drops what {@code owner} reported since the last flush; for the GameTests. */
    public static void discard(UUID owner) {
        PENDING.remove(owner);
    }

    /** A single-player client can open another world; nothing reported in this one may leak into it. */
    public static void clear() {
        PENDING.clear();
    }

    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || PENDING.isEmpty()) return;
        MinecraftServer server = event.getServer();
        long gameTime = server.overworld().getGameTime();
        if (gameTime % FLUSH_TICKS != 0) return;
        KardashevData data = KardashevData.get(server);
        for (var entry : PENDING.entrySet()) {
            double eu = entry.getValue().sumThenReset();
            if (eu > 0) data.add(teamOf(entry.getKey()), eu, gameTime);
        }
    }

    /**
     * Team that owns what {@code owner} produced: the FTB team when FTB Teams is loaded (through GTCEu's machine
     * owner), otherwise the player. An id that is already a team id resolves to itself.
     */
    public static UUID teamOf(UUID owner) {
        try {
            MachineOwner machineOwner = MachineOwner.getOwner(owner);
            UUID team = machineOwner == null ? null : machineOwner.getUUID();
            return team == null || MachineOwner.EMPTY.equals(team) ? owner : team;
        } catch (RuntimeException | LinkageError e) {
            GTIAMod.LOGGER.debug("Kardashev meter: no team for {}", owner, e);
            return owner;
        }
    }
}
