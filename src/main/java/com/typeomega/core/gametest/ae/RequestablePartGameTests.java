package com.typeomega.core.gametest.ae;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.machines.GTAEMachines;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEFluidList;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEItemList;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEItemSlot;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.core.definitions.AEItems;
import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.integration.ae.requestable.MERequestableInputBusMachine;
import com.typeomega.core.integration.ae.requestable.MERequestableInputHatchMachine;
import com.typeomega.core.integration.ae.requestable.RequestablePart;
import com.typeomega.core.integration.ae.requestable.RequestableParts;

/**
 * ME Requestable Input Bus and Hatch (D-442): like the GTCEu ME input parts they pull the configured stacks from the
 * network, and for what the network lacks they request a craft, one job per slot, until the slot is satisfied. The
 * grid is a real powered network with a pattern provider and a crafting CPU.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class RequestablePartGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";
    private static final AEItemKey IRON = AEItemKey.of(Items.IRON_INGOT);
    private static final AEItemKey GOLD = AEItemKey.of(Items.GOLD_INGOT);
    private static final AEItemKey DIAMOND = AEItemKey.of(Items.DIAMOND);

    private RequestablePartGameTests() {}

    private static MetaMachine place(GameTestHelper h, MachineDefinition definition) {
        h.setBlock(AeGridRig.PART, definition.getBlock());
        return ((MetaMachineBlockEntity) h.getBlockEntity(AeGridRig.PART)).getMetaMachine();
    }

    private static MERequestableInputBusMachine bus(GameTestHelper h) {
        h.assertTrue(RequestableParts.bus != null, "the requestable bus is not registered");
        return (MERequestableInputBusMachine) place(h, RequestableParts.bus);
    }

    private static MERequestableInputHatchMachine hatch(GameTestHelper h) {
        return (MERequestableInputHatchMachine) place(h, RequestableParts.hatch);
    }

    private static ExportOnlyAEItemSlot slot(MERequestableInputBusMachine bus, int index) {
        return ((ExportOnlyAEItemList) bus.getInventory()).getInventory()[index];
    }

    private static void configure(MERequestableInputBusMachine bus, int slot, AEItemKey what, long amount) {
        slot(bus, slot).setConfig(new GenericStack(what, amount));
    }

    private static long stocked(MERequestableInputBusMachine bus, int index) {
        GenericStack stock = slot(bus, index).getStock();
        return stock == null ? 0 : stock.amount();
    }

    /** The part is online on the grid once its node exists, a tick after it is placed. */
    private static void online(GameTestHelper h, RequestablePart part) {
        h.assertTrue(part.getMainNode().isActive(), "the part is not on the powered grid yet");
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 400)
    public static void requestableBusRequestsACraftForWhatIsMissing(GameTestHelper h) {
        var rig = new AeGridRig(h).withCrafting();
        var bus = bus(h);
        h.startSequence()
                .thenWaitUntil(rig::ready)
                .thenExecute(() -> {
                    rig.stock(IRON, 8);
                    rig.pattern(new GenericStack(IRON, 1), new GenericStack(GOLD, 1));
                    configure(bus, 0, GOLD, 4);
                })
                .thenWaitUntil(() -> {
                    online(h, bus);
                    bus.syncRequests();
                    h.assertTrue(!bus.getRequestedJobs().isEmpty(), "no craft request was issued yet");
                })
                .thenWaitUntil(() -> h.assertTrue(rig.cpuBusy(), "the crafting CPU is not running the gold job"))
                .thenExecute(() -> h.assertTrue(stocked(bus, 0) == 0, "nothing could have arrived yet"))
                .thenSucceed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 200)
    public static void requestablePartPullsFromTheNetworkWithoutCrafting(GameTestHelper h) {
        var rig = new AeGridRig(h).withCrafting();
        var bus = bus(h);
        h.startSequence()
                .thenWaitUntil(rig::ready)
                .thenExecute(() -> {
                    rig.stock(GOLD, 8);
                    rig.pattern(new GenericStack(IRON, 1), new GenericStack(GOLD, 1));
                    configure(bus, 0, GOLD, 4);
                })
                .thenWaitUntil(() -> {
                    online(h, bus);
                    bus.syncRequests();
                    h.assertTrue(stocked(bus, 0) == 4, "the slot did not fill from the network");
                })
                .thenExecute(() -> {
                    bus.syncRequests();
                    h.assertTrue(bus.getRequestedJobs().isEmpty(), "a craft was requested for what was in stock");
                    h.assertTrue(rig.stored(GOLD) == 4, "the network should have kept the other 4");
                })
                .thenSucceed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 200)
    public static void requestablePartDoesNotRequestAStackThatIsSatisfied(GameTestHelper h) {
        var rig = new AeGridRig(h).withCrafting();
        var bus = bus(h);
        h.startSequence()
                .thenWaitUntil(rig::ready)
                .thenExecute(() -> {
                    rig.stock(IRON, 8);
                    rig.pattern(new GenericStack(IRON, 1), new GenericStack(GOLD, 1));
                    configure(bus, 0, GOLD, 4);
                    slot(bus, 0).setStock(new GenericStack(GOLD, 4));
                })
                .thenWaitUntil(() -> online(h, bus))
                .thenExecute(() -> {
                    for (int i = 0; i < 5; i++) bus.syncRequests();
                    h.assertTrue(bus.getRequestedJobs().isEmpty(), "a craft was requested for a full slot");
                    h.assertTrue(!rig.cpuBusy(), "the CPU is crafting gold");
                    h.assertTrue(stocked(bus, 0) == 4, "the full slot changed");
                })
                .thenSucceed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 200)
    public static void requestablePartDoesNotRequestWhatHasNoPattern(GameTestHelper h) {
        var rig = new AeGridRig(h).withCrafting();
        var bus = bus(h);
        h.startSequence()
                .thenWaitUntil(rig::ready)
                .thenExecute(() -> {
                    rig.stock(IRON, 8);
                    rig.pattern(new GenericStack(IRON, 1), new GenericStack(GOLD, 1));
                    configure(bus, 0, DIAMOND, 2);
                })
                .thenWaitUntil(() -> online(h, bus))
                .thenExecute(() -> {
                    h.assertTrue(!rig.grid().getCraftingService().isCraftable(DIAMOND), "diamond has a pattern");
                    for (int i = 0; i < 5; i++) bus.syncRequests();
                    h.assertTrue(bus.getRequestedJobs().isEmpty(), "a craft was requested without a pattern");
                    h.assertTrue(stocked(bus, 0) == 0, "the slot got something from nowhere");
                })
                .thenSucceed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 100)
    public static void requestableBusReceivesCraftedItemsDirectlyUpToWhatIsMissing(GameTestHelper h) {
        var bus = bus(h);
        configure(bus, 0, GOLD, 4);
        slot(bus, 0).setStock(new GenericStack(GOLD, 1));
        h.assertTrue(bus.insertCraftedItems(null, GOLD, 10, Actionable.SIMULATE) == 3,
                "a simulation should accept the 3 that are missing");
        h.assertTrue(stocked(bus, 0) == 1, "a simulation changed the slot");
        h.assertTrue(bus.insertCraftedItems(null, GOLD, 10, Actionable.MODULATE) == 3, "3 missing, 3 taken");
        h.assertTrue(stocked(bus, 0) == 4, "the slot should be full");
        h.assertTrue(bus.insertCraftedItems(null, IRON, 5, Actionable.MODULATE) == 0, "another item was taken");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 400)
    public static void requestableHatchRequestsAFluidCraft(GameTestHelper h) {
        var rig = new AeGridRig(h).withCrafting();
        var hatch = hatch(h);
        AEFluidKey water = AEFluidKey.of(Fluids.WATER);
        h.startSequence()
                .thenWaitUntil(rig::ready)
                .thenExecute(() -> {
                    rig.stock(IRON, 4);
                    rig.pattern(new GenericStack(IRON, 1), new GenericStack(water, 1000));
                    ((ExportOnlyAEFluidList) hatch.tank).getInventory()[0]
                            .setConfig(new GenericStack(water, 2000));
                })
                .thenWaitUntil(() -> {
                    online(h, hatch);
                    hatch.syncRequests();
                    h.assertTrue(!hatch.getRequestedJobs().isEmpty(), "no fluid craft was requested yet");
                })
                .thenWaitUntil(() -> h.assertTrue(rig.cpuBusy(), "the crafting CPU is not running the water job"))
                .thenExecute(() -> {
                    h.assertTrue(hatch.insertCraftedItems(null, water, 5000, Actionable.MODULATE) == 2000,
                            "the hatch takes the missing 2000 mB and no more");
                })
                .thenSucceed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 400)
    public static void requestableJobSurvivesASaveAndReload(GameTestHelper h) {
        var rig = new AeGridRig(h).withCrafting();
        var bus = bus(h);
        h.startSequence()
                .thenWaitUntil(rig::ready)
                .thenExecute(() -> {
                    rig.stock(IRON, 8);
                    rig.pattern(new GenericStack(IRON, 1), new GenericStack(GOLD, 1));
                    configure(bus, 0, GOLD, 4);
                })
                .thenWaitUntil(() -> {
                    bus.syncRequests();
                    h.assertTrue(!bus.getRequestedJobs().isEmpty(), "no craft request was issued yet");
                })
                .thenExecute(() -> {
                    CompoundTag saved = new CompoundTag();
                    bus.saveCustomPersistedData(saved, false);
                    h.assertTrue(!saved.isEmpty(), "the job was not written");
                    BlockPos other = new BlockPos(4, 2, 4);
                    h.setBlock(other, RequestableParts.bus.getBlock());
                    var copy = (MERequestableInputBusMachine) ((MetaMachineBlockEntity) h.getBlockEntity(other))
                            .getMetaMachine();
                    copy.loadCustomPersistedData(saved);
                    h.assertTrue(copy.getRequestedJobs().size() == 1, "the job did not come back");
                    CompoundTag dropped = new CompoundTag();
                    bus.saveCustomPersistedData(dropped, true);
                    h.assertTrue(dropped.isEmpty(), "a dropped machine carried a job");
                })
                .thenSucceed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 100)
    public static void requestablePartsAreTheirOwnMachinesWithTheMeAbilities(GameTestHelper h) {
        h.assertTrue(RequestableParts.bus != GTAEMachines.ITEM_IMPORT_BUS_ME, "the bus is the plain one");
        h.assertTrue(RequestableParts.hatch != GTAEMachines.FLUID_IMPORT_HATCH_ME, "the hatch is the plain one");
        h.assertTrue(RequestableParts.bus.getTier() == 5 && RequestableParts.hatch.getTier() == 5,
                "both parts are IV");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 100)
    public static void requestablePartsHaveAnIvAssemblerRecipeFromThePlainParts(GameTestHelper h) {
        for (var entry : new Object[][] { { RequestableParts.bus, GTAEMachines.ITEM_IMPORT_BUS_ME },
                { RequestableParts.hatch, GTAEMachines.FLUID_IMPORT_HATCH_ME } }) {
            MachineDefinition made = (MachineDefinition) entry[0], plain = (MachineDefinition) entry[1];
            GTRecipe found = null;
            for (var recipe : h.getLevel().getRecipeManager().getAllRecipesFor(GTRecipeTypes.ASSEMBLER_RECIPES)) {
                for (var out : RecipeHelper.getOutputItems(recipe)) if (out.is(made.getItem())) found = recipe;
            }
            h.assertTrue(found != null, "no assembler recipe makes " + made.getId());
            boolean fromPlain = false, card = false;
            for (var ingredient : RecipeHelper.getInputItems(found)) {
                fromPlain |= ingredient.is(plain.getItem());
                card |= ingredient.is(AEItems.CRAFTING_CARD.asItem());
            }
            h.assertTrue(fromPlain && card, made.getId() + " needs the plain part and a Crafting Card");
            h.assertTrue(RecipeHelper.getRecipeEUtTier(found) == GTValues.IV,
                    made.getId() + " is not an IV recipe");
        }
        h.succeed();
    }
}
