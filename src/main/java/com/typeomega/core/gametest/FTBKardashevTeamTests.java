package com.typeomega.core.gametest;

import net.minecraft.gametest.framework.GameTestHelper;

import com.typeomega.core.kardashev.KardashevData;
import com.typeomega.core.kardashev.KardashevLedger;

import java.util.Map;
import java.util.UUID;

/** Optional FTB Teams body for the Kardashev Meter: party members add to one K, solo players keep their own. */
final class FTBKardashevTeamTests {

    private FTBKardashevTeamTests() {}

    @SuppressWarnings("unchecked")
    static void run(GameTestHelper h) throws ReflectiveOperationException {
        var manager = (dev.ftb.mods.ftbteams.data.TeamManagerImpl) dev.ftb.mods.ftbteams.api.FTBTeamsAPI.api()
                .getManager();
        var knownField = manager.getClass().getDeclaredField("knownPlayers");
        knownField.setAccessible(true);
        var known = (Map<UUID, dev.ftb.mods.ftbteams.api.Team>) knownField.get(manager);
        UUID first = UUID.randomUUID(), second = UUID.randomUUID(), solo = UUID.randomUUID();
        var party = new dev.ftb.mods.ftbteams.data.PartyTeam(manager, UUID.randomUUID());
        var firstTeam = new dev.ftb.mods.ftbteams.data.PlayerTeam(manager, first);
        var secondTeam = new dev.ftb.mods.ftbteams.data.PlayerTeam(manager, second);
        firstTeam.setEffectiveTeam(party);
        secondTeam.setEffectiveTeam(party);
        known.put(first, firstTeam);
        known.put(second, secondTeam);
        known.put(solo, new dev.ftb.mods.ftbteams.data.PlayerTeam(manager, solo));
        try {
            h.assertTrue(party.getId().equals(KardashevLedger.teamOf(first)) &&
                    party.getId().equals(KardashevLedger.teamOf(second)), "party members resolve to the party");
            h.assertTrue(solo.equals(KardashevLedger.teamOf(solo)), "a solo player is his own scope");

            KardashevData data = new KardashevData();
            int bucket = KardashevData.BUCKET_TICKS;
            data.add(KardashevLedger.teamOf(first), 1_024.0 * bucket, 0);
            data.add(KardashevLedger.teamOf(second), 1_024.0 * bucket, 0);
            double both = data.averageEuPerTick(party.getId(), bucket);
            h.assertTrue(Math.abs(both - 2_048.0 / KardashevData.BUCKETS) < 1e-9, "both members add to one K: " + both);
            h.assertTrue(data.read(KardashevLedger.teamOf(second), bucket).averageEuPerTick() == both,
                    "and every member reads the same K");
            h.assertTrue(data.markBuilt(KardashevLedger.teamOf(first), "test_mega", 0), "first build counts");
            h.assertTrue(!data.markBuilt(KardashevLedger.teamOf(second), "test_mega", 0) &&
                    data.megastructures(party.getId()) == 1, "a mega built by one member counts for the party once");
            h.assertTrue(data.megastructures(solo) == 0, "the solo player does not get it");
        } finally {
            known.remove(first);
            known.remove(second);
            known.remove(solo);
        }
    }
}
