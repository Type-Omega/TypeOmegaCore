package com.typeomega.core.kardashev;

import com.gregtechceu.gtceu.GTCEu;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;

import com.raishxn.gtna.research.KnowledgeScope;
import com.typeomega.core.TypeOmegaMod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.DoubleAdder;

/**
 * Collects what the sources report and moves it into {@link KardashevData} once a second.
 * <p>
 * Sources report themselves (mixins on the GTNA wireless energy and steam networks, on the GTCEu boilers and on the
 * energy container that takes a generator's output), so the meter never scans the world: the hot path is one map
 * lookup and one add per report.
 */
public final class KardashevLedger {

    // EN: Flush once a second: owner-to-team lookups and the saved data are touched 20 times less than the
    // sources report, and a 20-second bucket still gets 20 flushes.
    // PT: Esvazia uma vez por segundo: a busca dono→time e os dados salvos são tocados 20 vezes menos que os
    // relatos das fontes, e cada balde de 20 s ainda recebe 20 esvaziamentos.
    // ZH: 每秒汇总一次：所有者到队伍的查询和存档数据的访问次数比来源上报少 20 倍，每个 20 秒的桶仍有 20 次汇总。
    static final int FLUSH_TICKS = 20;

    private static final Map<UUID, DoubleAdder[]> PENDING = new ConcurrentHashMap<>();

    private KardashevLedger() {}

    /** Adds {@code amount} EU (steam already converted) the {@code source} reported for {@code owner}. */
    public static void record(UUID owner, KardashevSource source, double amount) {
        if (owner == null || !(amount > 0)) return;
        DoubleAdder[] adders = PENDING.get(owner);
        if (adders == null) adders = PENDING.computeIfAbsent(owner, ignored -> newAdders());
        adders[source.ordinal()].add(amount);
    }

    private static DoubleAdder[] newAdders() {
        DoubleAdder[] adders = new DoubleAdder[KardashevSource.COUNT];
        for (int i = 0; i < adders.length; i++) adders[i] = new DoubleAdder();
        return adders;
    }

    /** EU that entered the owner's grid (the GTNA wireless network counts the gross amount, before losses). */
    public static void recordEu(UUID owner, double eu) {
        record(owner, KardashevSource.WIRELESS_ENERGY, eu);
    }

    /** Steam in mB, counted at 2 mB = 1 EU (D-389), from a boiler or the wireless steam network. */
    public static void recordSteam(UUID owner, double steamMb, KardashevSource source) {
        record(owner, source, KardashevScale.steamToEu(steamMb));
    }

    /** Steam in mB the owner's wireless steam network handed out. */
    public static void recordSteamSpent(UUID owner, double steamMb) {
        record(owner, KardashevSource.SPENT_STEAM, KardashevScale.steamToEu(steamMb));
    }

    /** What {@code owner} generated since the last flush; for the GameTests. */
    public static double pending(UUID owner) {
        return pending(owner, true);
    }

    /** What {@code owner} reported since the last flush, generated or spent; for the GameTests. */
    public static double pending(UUID owner, boolean generated) {
        DoubleAdder[] adders = PENDING.get(owner);
        if (adders == null) return 0;
        double sum = 0;
        for (KardashevSource source : KardashevSource.values()) {
            if (source.generated() == generated) sum += adders[source.ordinal()].sum();
        }
        return sum;
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
            double[] perSource = new double[KardashevSource.COUNT];
            boolean any = false;
            for (int i = 0; i < perSource.length; i++) {
                perSource[i] = entry.getValue()[i].sumThenReset();
                any |= perSource[i] > 0;
            }
            if (any) data.record(teamOf(entry.getKey()), perSource, gameTime);
        }
    }

    /**
     * Team that owns what {@code owner} produced: the player's effective FTB team (the party, not the personal
     * team) when FTB Teams is loaded, otherwise the player. Same scope as the GTNA wireless networks (D-386).
     */
    public static UUID teamOf(UUID owner) {
        // EN: GTCEu's FTBOwner.getUUID answers with the personal team, so every party member had his own K;
        // KnowledgeScope resolves Team.getTeamId, the party. Without FTB Teams the player is the scope.
        // PT: O FTBOwner.getUUID do GTCEu devolve o time pessoal, e cada membro do grupo tinha o próprio K;
        // o KnowledgeScope resolve Team.getTeamId, o grupo. Sem FTB Teams o escopo é o jogador.
        // ZH: GTCEu 的 FTBOwner.getUUID 返回个人队伍，导致每个队员各算各的 K；KnowledgeScope 解析 Team.getTeamId，
        // 即所在队伍。没有 FTB Teams 时按玩家计算。
        if (owner == null || !GTCEu.Mods.isFTBTeamsLoaded()) return owner;
        try {
            UUID team = KnowledgeScope.of(owner);
            return team == null ? owner : team;
        } catch (RuntimeException | LinkageError e) {
            TypeOmegaMod.LOGGER.debug("Kardashev meter: no team for {}", owner, e);
            return owner;
        }
    }
}
