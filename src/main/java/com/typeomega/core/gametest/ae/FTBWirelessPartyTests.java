package com.typeomega.core.gametest.ae;

import com.gregtechceu.gtceu.common.data.machines.GTAEMachines;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;

import com.typeomega.core.gametest.TestPlayers;
import com.typeomega.core.integration.ae.wireless.AeWireless;
import com.typeomega.core.integration.ae.wireless.WirelessConfiguratorItem;

import java.util.Map;
import java.util.UUID;

/** Optional FTB Teams body of the wireless tests: party members share networks, a stranger does not. */
final class FTBWirelessPartyTests {

    private FTBWirelessPartyTests() {}

    @SuppressWarnings("unchecked")
    static void run(GameTestHelper h) throws ReflectiveOperationException {
        var manager = (dev.ftb.mods.ftbteams.data.TeamManagerImpl) dev.ftb.mods.ftbteams.api.FTBTeamsAPI.api()
                .getManager();
        var knownField = manager.getClass().getDeclaredField("knownPlayers");
        knownField.setAccessible(true);
        var known = (Map<UUID, dev.ftb.mods.ftbteams.api.Team>) knownField.get(manager);
        var rig = new WirelessTestRig(h, false);
        rig.place(WirelessTestRig.FAR, GTAEMachines.ITEM_IMPORT_BUS_ME);
        var mate = TestPlayers.survival(h);
        var stranger = TestPlayers.survival(h);
        var party = new dev.ftb.mods.ftbteams.data.PartyTeam(manager, UUID.randomUUID());
        var first = new dev.ftb.mods.ftbteams.data.PlayerTeam(manager, rig.player.getUUID());
        var second = new dev.ftb.mods.ftbteams.data.PlayerTeam(manager, mate.getUUID());
        first.setEffectiveTeam(party);
        second.setEffectiveTeam(party);
        known.put(rig.player.getUUID(), first);
        known.put(mate.getUUID(), second);
        known.put(stranger.getUUID(), new dev.ftb.mods.ftbteams.data.PlayerTeam(manager, stranger.getUUID()));
        try {
            rig.createNetwork("Party net");
            String id = WirelessConfiguratorItem.selectedId(rig.configurator);
            ItemStack mateConfigurator = new ItemStack(AeWireless.CONFIGURATOR.get());
            mateConfigurator.getOrCreateTag().putString(WirelessConfiguratorItem.ID_KEY, id);
            ItemStack strangerConfigurator = mateConfigurator.copy();
            rig.click(stranger, strangerConfigurator, WirelessTestRig.FAR, false);
            h.assertTrue(rig.bindingAt(WirelessTestRig.FAR) == null, "a stranger used the party's network");
            rig.click(mate, mateConfigurator, WirelessTestRig.FAR, false);
            h.assertTrue(rig.bindingAt(WirelessTestRig.FAR) != null, "a party member could not use the network");
            // Cycling shows a party member the party's networks, and the stranger none.
            mateConfigurator.removeTagKey(WirelessConfiguratorItem.ID_KEY);
            mate.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, mateConfigurator);
            mate.setShiftKeyDown(false);
            mateConfigurator.getItem().use(h.getLevel(), mate, net.minecraft.world.InteractionHand.MAIN_HAND);
            h.assertTrue(id.equals(WirelessConfiguratorItem.selectedId(mateConfigurator)),
                    "the party member does not see the party's network when cycling");
            strangerConfigurator.removeTagKey(WirelessConfiguratorItem.ID_KEY);
            stranger.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, strangerConfigurator);
            stranger.setShiftKeyDown(false);
            strangerConfigurator.getItem().use(h.getLevel(), stranger, net.minecraft.world.InteractionHand.MAIN_HAND);
            h.assertTrue(WirelessConfiguratorItem.selectedId(strangerConfigurator) == null,
                    "the stranger sees the party's network when cycling");
        } finally {
            known.remove(rig.player.getUUID());
            known.remove(mate.getUUID());
            known.remove(stranger.getUUID());
        }
    }
}
