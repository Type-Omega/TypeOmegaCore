package com.typeomega.core.gametest.ae;

import com.gregtechceu.gtceu.common.data.machines.GTAEMachines;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.gametest.TestPlayers;
import com.typeomega.core.integration.ae.requestable.MERequestableInputBusMachine;
import com.typeomega.core.integration.ae.requestable.RequestableParts;
import com.typeomega.core.integration.ae.wireless.AeWireless;
import com.typeomega.core.integration.ae.wireless.WirelessConfiguratorItem;
import com.typeomega.core.integration.ae.wireless.WirelessData;
import com.typeomega.core.integration.ae.wireless.WirelessLinker;
import com.typeomega.core.kardashev.KardashevLedger;

/**
 * The wireless ME network (D-442): a GregTech ME part bound by the configurator joins the grid of the network's
 * connector without a cable, sees its storage and uses a channel; an unbound part does not; the link comes back after
 * the part unloads and loads again; the network is the team's. The grid is a real powered network.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class WirelessGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";
    private static final AEItemKey IRON = AEItemKey.of(Items.IRON_INGOT);
    private static final AEItemKey GOLD = AEItemKey.of(Items.GOLD_INGOT);

    private WirelessGameTests() {}

    /** The connector and one ME input bus bound to a new network; the bus is returned to the caller's checks. */
    private static void bindBoth(WirelessTestRig rig) {
        rig.createNetwork("Test net");
        rig.click(WirelessTestRig.CONNECTOR, false);
        rig.click(WirelessTestRig.FAR, false);
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 300)
    public static void wirelessBoundPartJoinsTheGridAndSeesTheStorage(GameTestHelper h) {
        var rig = new WirelessTestRig(h, false);
        var part = rig.place(WirelessTestRig.FAR, GTAEMachines.ITEM_IMPORT_BUS_ME);
        h.startSequence()
                .thenWaitUntil(() -> {
                    rig.grid.ready();
                    h.assertTrue(WirelessTestRig.hasNode(part), "the part has no node yet");
                })
                .thenExecute(() -> {
                    h.assertTrue(!rig.inTheGrid(part), "an unbound part is already in the grid");
                    bindBoth(rig);
                    h.assertTrue(rig.bindingAt(WirelessTestRig.FAR) != null, "the part was not bound");
                    rig.grid.stock(IRON, 7);
                })
                .thenWaitUntil(() -> {
                    h.assertTrue(rig.inTheGrid(part), "the bound part did not join the grid");
                    h.assertTrue(WirelessTestRig.node(part).isOnline(), "the bound part has no channel");
                })
                .thenExecute(() -> {
                    var storage = WirelessTestRig.node(part).getGrid().getStorageService().getInventory();
                    h.assertTrue(storage.getAvailableStacks().get(IRON) == 7, "the part does not see the storage");
                    h.assertTrue(WirelessTestRig.node(part).getNode().getUsedChannels() == 1,
                            "the part should take one channel, like a cabled part");
                })
                .thenSucceed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 200)
    public static void wirelessUnboundPartDoesNotJoinTheGrid(GameTestHelper h) {
        var rig = new WirelessTestRig(h, false);
        var bound = rig.place(WirelessTestRig.FAR, GTAEMachines.ITEM_IMPORT_BUS_ME);
        var unbound = rig.place(WirelessTestRig.FAR_2, GTAEMachines.ITEM_IMPORT_BUS_ME);
        h.startSequence()
                .thenWaitUntil(() -> {
                    rig.grid.ready();
                    h.assertTrue(WirelessTestRig.hasNode(bound) && WirelessTestRig.hasNode(unbound), "no nodes yet");
                })
                .thenExecute(() -> bindBoth(rig))
                .thenWaitUntil(() -> h.assertTrue(rig.inTheGrid(bound), "the bound part did not join"))
                .thenExecute(() -> {
                    WirelessLinker.sweep(h.getLevel().getServer());
                    h.assertTrue(!rig.inTheGrid(unbound), "the unbound part joined the grid");
                    h.assertTrue(rig.bindingAt(WirelessTestRig.FAR_2) == null, "the unbound part has a binding");
                    rig.grid.stock(IRON, 3);
                    var own = WirelessTestRig.node(unbound).getGrid();
                    h.assertTrue(own == null || own.getStorageService().getInventory().getAvailableStacks()
                            .get(IRON) == 0, "the unbound part sees the storage");
                })
                .thenSucceed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 500)
    public static void wirelessLinkSurvivesAnUnloadAndReload(GameTestHelper h) {
        var rig = new WirelessTestRig(h, false);
        var first = rig.place(WirelessTestRig.FAR, GTAEMachines.ITEM_IMPORT_BUS_ME);
        h.startSequence()
                .thenWaitUntil(() -> {
                    rig.grid.ready();
                    h.assertTrue(WirelessTestRig.hasNode(first), "the part has no node yet");
                })
                .thenExecute(() -> bindBoth(rig))
                .thenWaitUntil(() -> h.assertTrue(rig.inTheGrid(first), "the part did not join"))
                .thenExecute(() -> {
                    // EN: A reload builds a new machine from its saved tag: its node and the old link are gone.
                    // PT: Um recarregamento cria uma máquina nova a partir da tag salva: o nó e o link antigo somem.
                    // ZH: 重新加载会根据保存的标签创建新机器：节点和旧链接都已消失。
                    CompoundTag saved = h.getBlockEntity(WirelessTestRig.FAR).saveWithFullMetadata();
                    rig.unloadAndReload(WirelessTestRig.FAR, saved);
                })
                .thenWaitUntil(() -> {
                    var again = rig.machineAt(WirelessTestRig.FAR);
                    h.assertTrue(again != first && WirelessTestRig.hasNode(again), "the new machine has no node yet");
                    h.assertTrue(rig.inTheGrid(again), "the link did not come back after the reload");
                })
                .thenExecute(() -> {
                    var server = h.getLevel().getServer();
                    var before = WirelessData.get(server);
                    var after = WirelessData.load(before.save(new CompoundTag()));
                    h.assertTrue(after.bindings().equals(before.bindings()) && !after.bindings().isEmpty(),
                            "the bindings did not survive a save and load of the world data");
                    h.assertTrue(after.of(KardashevLedger.teamOf(rig.player.getUUID())).size() == before
                            .of(KardashevLedger.teamOf(rig.player.getUUID())).size(), "the networks did not survive");
                })
                .thenSucceed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 500)
    public static void wirelessRequestablePartCraftsOverTheLink(GameTestHelper h) {
        var rig = new WirelessTestRig(h, true);
        var part = (MERequestableInputBusMachine) rig.place(WirelessTestRig.FAR, RequestableParts.bus);
        var slot = ((com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEItemList) part.getInventory())
                .getInventory()[0];
        h.startSequence()
                .thenWaitUntil(() -> {
                    rig.grid.ready();
                    h.assertTrue(WirelessTestRig.hasNode(part), "no node yet");
                })
                .thenExecute(() -> {
                    bindBoth(rig);
                    rig.grid.stock(IRON, 8);
                    rig.grid.pattern(new GenericStack(IRON, 1), new GenericStack(GOLD, 1));
                    slot.setConfig(new GenericStack(GOLD, 4));
                })
                .thenWaitUntil(() -> {
                    h.assertTrue(rig.inTheGrid(part) && WirelessTestRig.node(part).isOnline(), "not online yet");
                    part.syncRequests();
                    h.assertTrue(!part.getRequestedJobs().isEmpty(), "the wireless requestable part did not ask");
                })
                .thenSucceed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 100)
    public static void wirelessNetworksBelongToTheTeam(GameTestHelper h) {
        var rig = new WirelessTestRig(h, false);
        var part = rig.place(WirelessTestRig.FAR, GTAEMachines.ITEM_IMPORT_BUS_ME);
        rig.createNetwork("Mine");
        var stranger = TestPlayers.survival(h);
        var strangerConfigurator = new ItemStack(AeWireless.CONFIGURATOR.get());
        h.assertTrue(!rig.player.getUUID().equals(stranger.getUUID()), "the two players are the same");
        // The stranger cannot pick up the first player's network by cycling, so it is selected by hand.
        var id = WirelessConfiguratorItem.selectedId(rig.configurator);
        h.assertTrue(id != null, "no network was created");
        strangerConfigurator.getOrCreateTag().putString(WirelessConfiguratorItem.ID_KEY, id);
        rig.click(stranger, strangerConfigurator, WirelessTestRig.FAR, false);
        h.assertTrue(rig.bindingAt(WirelessTestRig.FAR) == null, "another team's player bound a part to the network");
        rig.click(WirelessTestRig.FAR, false);
        h.assertTrue(rig.bindingAt(WirelessTestRig.FAR) != null, "the owner could not bind a part to the network");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 100)
    public static void wirelessNetworkHasOneConnector(GameTestHelper h) {
        var rig = new WirelessTestRig(h, false);
        BlockPos second = new BlockPos(4, 2, 2);
        h.setBlock(second, AeWireless.CONNECTOR.get());
        rig.createNetwork("Hub");
        rig.click(WirelessTestRig.CONNECTOR, false);
        h.assertTrue(rig.bindingAt(WirelessTestRig.CONNECTOR) != null, "the first connector was not bound");
        rig.click(second, false);
        h.assertTrue(rig.bindingAt(second) == null, "a second connector was bound to the same network");
        rig.createNetwork("Other hub");
        rig.click(second, false);
        h.assertTrue(rig.bindingAt(second) != null, "a connector could not take another network");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 100)
    public static void wirelessBindingOfARemovedBlockIsForgotten(GameTestHelper h) {
        var rig = new WirelessTestRig(h, false);
        rig.place(WirelessTestRig.FAR, GTAEMachines.ITEM_IMPORT_BUS_ME);
        rig.createNetwork("Gone");
        rig.click(WirelessTestRig.FAR, false);
        h.assertTrue(rig.bindingAt(WirelessTestRig.FAR) != null, "not bound");
        h.setBlock(WirelessTestRig.FAR, net.minecraft.world.level.block.Blocks.AIR);
        WirelessLinker.sweep(h.getLevel().getServer());
        h.assertTrue(rig.bindingAt(WirelessTestRig.FAR) == null, "the binding of a removed block stayed");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 100)
    public static void wirelessConfiguratorUnbindsWhenNoNetworkIsChosen(GameTestHelper h) {
        var rig = new WirelessTestRig(h, false);
        rig.place(WirelessTestRig.FAR, GTAEMachines.ITEM_IMPORT_BUS_ME);
        rig.createNetwork("Chosen");
        rig.click(WirelessTestRig.FAR, false);
        h.assertTrue(rig.bindingAt(WirelessTestRig.FAR) != null, "not bound");
        rig.cycle();
        h.assertTrue(WirelessConfiguratorItem
                .selectedId(rig.configurator) == null, "cycling past the last network should leave none chosen");
        rig.click(WirelessTestRig.FAR, false);
        h.assertTrue(rig.bindingAt(WirelessTestRig.FAR) == null, "with none chosen the click should unbind");
        // Sneaking on a bound block reads its network into the configurator.
        rig.createNetwork("Second");
        rig.click(WirelessTestRig.FAR, false);
        rig.cycle();
        rig.click(WirelessTestRig.FAR, true);
        h.assertTrue(WirelessConfiguratorItem
                .selectedId(rig.configurator) != null, "sneaking on a bound block should select its network");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 100)
    public static void wirelessPartyMembersShareNetworks(GameTestHelper h) throws ReflectiveOperationException {
        if (!com.gregtechceu.gtceu.GTCEu.Mods.isFTBTeamsLoaded()) {
            // Without FTB Teams the player is the team: the test above covers the stranger, this one the solo path.
            var rig = new WirelessTestRig(h, false);
            rig.createNetwork("Solo");
            h.assertTrue(WirelessConfiguratorItem.selectedId(rig.configurator) != null, "no network was created");
            h.succeed();
            return;
        }
        FTBWirelessPartyTests.run(h);
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 100)
    public static void wirelessItemsHaveCraftingRecipes(GameTestHelper h) {
        var manager = h.getLevel().getRecipeManager();
        for (var item : new net.minecraft.world.level.ItemLike[] { AeWireless.CONNECTOR.get(),
                AeWireless.CONFIGURATOR.get() }) {
            boolean found = manager.getRecipes().stream()
                    .anyMatch(r -> r.getResultItem(h.getLevel().registryAccess()).is(item.asItem()));
            h.assertTrue(found, "no recipe makes " + item.asItem());
        }
        h.succeed();
    }
}
