package com.typeomega.core.kardashev;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Per-team Kardashev state, saved with the overworld: the power of the last ten minutes in 20-second buckets, the
 * megastructures built and the highest type reached.
 */
public final class KardashevData extends SavedData {

    private static final String DATA_NAME = "typeomega_kardashev";

    // EN: GTIA-D-372: K uses the average of the last 10 minutes; 30 buckets of 20 s keep it exact to 20 s and cost
    // 30 numbers per team.
    // PT: GTIA-D-372: o K usa a média dos últimos 10 min; 30 baldes de 20 s mantêm a média exata a cada 20 s com
    // 30 números por time.
    // ZH: GTIA-D-372：K 取最近 10 分钟的平均值；30 个 20 秒的桶让平均值每 20 秒精确一次，每队只存 30 个数。
    public static final int BUCKET_TICKS = 400;
    public static final int BUCKETS = 30;
    public static final long WINDOW_TICKS = (long) BUCKET_TICKS * BUCKETS;

    private final Map<UUID, Team> teams = new HashMap<>();

    public KardashevData() {}

    private KardashevData(CompoundTag tag) {
        for (Tag raw : tag.getList("Teams", Tag.TAG_COMPOUND)) {
            CompoundTag entry = (CompoundTag) raw;
            Team team = new Team();
            team.legacyMegastructures = entry.getInt("Megastructures");
            for (Tag id : entry.getList("Built", Tag.TAG_STRING)) team.built.add(id.getAsString());
            team.reachedType = entry.getInt("ReachedType");
            long[] epochs = entry.getLongArray("Epochs");
            ListTag sums = entry.getList("Sums", Tag.TAG_DOUBLE);
            for (int i = 0; i < BUCKETS && i < epochs.length && i < sums.size(); i++) {
                team.epochs[i] = epochs[i];
                team.sums[i] = sums.getDouble(i);
            }
            teams.put(entry.getUUID("Team"), team);
        }
    }

    public static KardashevData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(KardashevData::new, KardashevData::new,
                DATA_NAME);
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        ListTag list = new ListTag();
        teams.forEach((id, team) -> {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("Team", id);
            entry.putInt("Megastructures", team.legacyMegastructures);
            ListTag built = new ListTag();
            for (String mega : team.built) built.add(net.minecraft.nbt.StringTag.valueOf(mega));
            entry.put("Built", built);
            entry.putInt("ReachedType", team.reachedType);
            entry.putLongArray("Epochs", team.epochs.clone());
            ListTag sums = new ListTag();
            for (double sum : team.sums) sums.add(net.minecraft.nbt.DoubleTag.valueOf(sum));
            entry.put("Sums", sums);
            list.add(entry);
        });
        tag.put("Teams", list);
        return tag;
    }

    /** Adds EU (steam already converted) produced by {@code team} at {@code gameTime}. */
    public void add(UUID team, double eu, long gameTime) {
        if (!(eu > 0)) return;
        Team state = team(team);
        long epoch = gameTime / BUCKET_TICKS;
        int slot = (int) Math.floorMod(epoch, BUCKETS);
        if (state.epochs[slot] != epoch) {
            state.epochs[slot] = epoch;
            state.sums[slot] = 0;
        }
        state.sums[slot] += eu;
        state.update(gameTime);
        setDirty();
    }

    /**
     * Average EU/t of the last ten minutes: the 30 complete buckets before the current one, divided by the whole
     * window even when the team is younger than ten minutes, so K only grows as fast as real production.
     */
    public double averageEuPerTick(UUID team, long gameTime) {
        Team state = teams.get(team);
        return state == null ? 0 : state.windowSum(gameTime, BUCKETS) / WINDOW_TICKS;
    }

    /** Average EU/t of the last complete 20-second bucket; a quick reading next to the 10-minute mean. */
    public double recentEuPerTick(UUID team, long gameTime) {
        Team state = teams.get(team);
        return state == null ? 0 : state.windowSum(gameTime, 1) / BUCKET_TICKS;
    }

    public int megastructures(UUID team) {
        Team state = teams.get(team);
        return state == null ? 0 : state.megastructures();
    }

    /**
     * Records that {@code team} formed the megastructure {@code id}; the cap follows. A megastructure counts once,
     * the first time it forms, and stays counted if it is later broken (like the type reached, GTIA-D-372).
     *
     * @return whether this was the first time
     */
    public boolean markBuilt(UUID team, String id, long gameTime) {
        Team state = team(team);
        if (!state.built.add(id)) return false;
        state.update(gameTime);
        setDirty();
        return true;
    }

    public boolean built(UUID team, String id) {
        Team state = teams.get(team);
        return state != null && state.built.contains(id);
    }

    /** Highest integer type the team reached; it never drops (GTIA-D-372). */
    public int reachedType(UUID team, long gameTime) {
        Team state = teams.get(team);
        if (state == null) return 0;
        if (state.update(gameTime)) setDirty();
        return state.reachedType;
    }

    /** Everything the meter shows, read once. */
    public Reading read(UUID team, long gameTime) {
        double average = averageEuPerTick(team, gameTime);
        int megas = megastructures(team);
        int reached = reachedType(team, gameTime);
        return new Reading(average, recentEuPerTick(team, gameTime), megas, reached,
                KardashevScale.kHundredths(average, megas, reached), KardashevScale.capHundredths(megas),
                KardashevScale.capped(average, megas));
    }

    public record Reading(double averageEuPerTick, double recentEuPerTick, int megastructures, int reachedType,
                          int kHundredths, int capHundredths, boolean capped) {}

    private Team team(UUID id) {
        return teams.computeIfAbsent(id, ignored -> new Team());
    }

    private static final class Team {

        final long[] epochs = new long[BUCKETS];
        final double[] sums = new double[BUCKETS];
        final java.util.Set<String> built = new java.util.LinkedHashSet<>();
        // EN: Count set by the old operator command, before the megastructures reported themselves; kept as a floor.
        // PT: Contagem do antigo comando de operador, de antes das megas se reportarem; fica como piso.
        // ZH: 旧版管理员命令设定的数量（巨构自行上报之前）；保留为下限。
        int legacyMegastructures;
        int reachedType;

        Team() {
            java.util.Arrays.fill(epochs, Long.MIN_VALUE);
        }

        /** Sum of the {@code count} complete buckets before the current one. */
        double windowSum(long gameTime, int count) {
            long current = gameTime / BUCKET_TICKS;
            double sum = 0;
            for (int i = 0; i < BUCKETS; i++) {
                long age = current - epochs[i];
                if (age >= 1 && age <= count) sum += sums[i];
            }
            return sum;
        }

        int megastructures() {
            return Math.min(KardashevScale.MAX_MEGASTRUCTURES, Math.max(legacyMegastructures, built.size()));
        }

        boolean update(long gameTime) {
            int reached = KardashevScale.reachedType(windowSum(gameTime, BUCKETS) / WINDOW_TICKS, megastructures());
            if (reached <= reachedType) return false;
            reachedType = reached;
            return true;
        }
    }
}
