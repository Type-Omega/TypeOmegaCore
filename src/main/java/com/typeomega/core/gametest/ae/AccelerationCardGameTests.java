package com.typeomega.core.gametest.ae;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.Upgrades;
import appeng.blockentity.storage.IOPortBlockEntity;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.parts.automation.IOBusPart;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.common.tileentities.TileExIOPort;
import com.typeomega.core.GTIAMod;
import com.typeomega.core.integration.ae.cards.AccelerationCards;
import com.typeomega.core.integration.ae.cards.AccelerationRates;
import com.typeomega.core.integration.ae.cards.AccelerationTier;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Acceleration Card (II) to (V) (GTIA-D-442): x8 per tier over the AE2 card, applied to the import/export buses and the
 * IO ports of AE2 and ExtendedAE, with the card limit shared by all tiers, highest first.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(GTIAMod.MOD_ID)
public final class AccelerationCardGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";

    private AccelerationCardGameTests() {}

    private static void install(GameTestHelper h, IUpgradeInventory upgrades, int level, int count) {
        if (count == 0) return;
        h.assertTrue(upgrades.addItems(new ItemStack(AccelerationCards.card(level), count)).isEmpty(),
                "the machine refused " + count + " cards of level " + level);
    }

    private static int perTick(IOBusPart bus) throws ReflectiveOperationException {
        for (Class<?> type = bus.getClass(); type != null; type = type.getSuperclass()) {
            if (Arrays.stream(type.getDeclaredMethods()).anyMatch(m -> m.getName().equals("getOperationsPerTick"))) {
                Method method = type.getDeclaredMethod("getOperationsPerTick");
                method.setAccessible(true);
                return (int) method.invoke(bus);
            }
        }
        throw new NoSuchMethodException("getOperationsPerTick");
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 20)
    public static void eachTierIsEightTimesTheLast(GameTestHelper h) {
        long expected = 1;
        for (int level = 0; level <= 4; level++) {
            h.assertTrue(AccelerationTier.multiplierOf(level) == expected, "level " + level + " is not x" + expected);
            expected *= 8;
        }
        h.assertTrue(AccelerationTier.V.multiplier == 4096 && AccelerationTier.II.multiplier == 8,
                "the multipliers of II and V");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 60)
    public static void importBusAppliesTheTiers(GameTestHelper h) throws ReflectiveOperationException {
        // {AE2 cards, II, III, IV, V} -> operations per tick: the AE2 table of the card count times the average
        // multiplier
        long[][] cases = {
                { 0, 0, 0, 0, 0, 1 }, { 4, 0, 0, 0, 0, 96 },
                { 0, 1, 0, 0, 0, 8 * 8 }, { 0, 4, 0, 0, 0, 96 * 8 }, { 0, 0, 0, 0, 1, 8 * 4096 },
                { 0, 0, 0, 0, 4, 96 * 4096 }, { 0, 2, 0, 0, 2, 96 * (2 * 8 + 2 * 4096) / 4 },
                { 1, 0, 3, 0, 0, 96 * (1 + 3 * 64) / 4 }, { 0, 0, 0, 2, 0, 32 * 512 },
        };
        for (long[] c : cases) {
            var host = AeTestRig.bus(h);
            var bus = (IOBusPart) AeTestRig.addPart(h, host, AEParts.IMPORT_BUS.asItem());
            for (int level = 0; level <= 4; level++) install(h, bus.getUpgrades(), level, (int) c[level]);
            int got = perTick(bus);
            h.assertTrue(got == c[5],
                    "import bus with " + Arrays.toString(Arrays.copyOf(c, 5)) + " does " + got + ", not " + c[5]);
        }
        var host = AeTestRig.bus(h);
        var export = (IOBusPart) AeTestRig.addPart(h, host, AEParts.EXPORT_BUS.asItem());
        install(h, export.getUpgrades(), 3, 4); // tier IV, x512
        h.assertTrue(perTick(export) == 96 * 512, "the export bus does not follow the tiers: " + perTick(export));
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 60)
    public static void extendedBusesShareTheLimitHighestFirst(GameTestHelper h) throws ReflectiveOperationException {
        var host = AeTestRig.bus(h);
        var plain = (IOBusPart) AeTestRig.addPart(h, host, EPPItemAndBlock.EX_IMPORT_BUS);
        long factor = perTick(plain);
        h.assertTrue(factor >= 1, "ExtendedAE's bus factor is " + factor);
        install(h, plain.getUpgrades(), 0, 4);
        h.assertTrue(perTick(plain) == 96 * factor, "ExtendedAE's bus with four AE2 cards: " + perTick(plain));
        install(h, plain.getUpgrades(), 4, 4);
        h.assertTrue(perTick(plain) == 96 * 4096 * factor, "four tier V cards must count first: " + perTick(plain));

        var other = AeTestRig.bus(h);
        var bus = (IOBusPart) AeTestRig.addPart(h, other, EPPItemAndBlock.EX_EXPORT_BUS);
        install(h, bus.getUpgrades(), 4, 3);
        install(h, bus.getUpgrades(), 1, 4);
        h.assertTrue(perTick(bus) == 96 * (3 * 4096 + 8) / 4 * factor,
                "three V and the best of four II: " + perTick(bus));
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 60)
    public static void ioPortsApplyTheTiers(GameTestHelper h) {
        AeTestRig.place(h, AEBlocks.IO_PORT);
        var port = (IOPortBlockEntity) h.getBlockEntity(AeTestRig.POS);
        h.assertTrue(
                !AccelerationRates.hasTieredCards(port.getUpgrades()) &&
                        AccelerationRates.ioPortItemsPerTick(port.getUpgrades()) == 256,
                "an IO port with no card moves 256 per tick");
        install(h, port.getUpgrades(), 0, 3);
        h.assertTrue(AccelerationRates.ioPortItemsPerTick(port.getUpgrades()) == 2048,
                "three AE2 cards: 2048 per tick");

        AeTestRig.place(h, AEBlocks.IO_PORT);
        var mixed = (IOPortBlockEntity) h.getBlockEntity(AeTestRig.POS);
        install(h, mixed.getUpgrades(), 0, 1);
        install(h, mixed.getUpgrades(), 2, 1);
        install(h, mixed.getUpgrades(), 3, 1);
        h.assertTrue(AccelerationRates.ioPortItemsPerTick(mixed.getUpgrades()) == 256 * 8 * (512 + 64 + 1) / 3,
                "the mix of III, IV and AE2: " + AccelerationRates.ioPortItemsPerTick(mixed.getUpgrades()));

        AeTestRig.place(h, AEBlocks.IO_PORT);
        var full = (IOPortBlockEntity) h.getBlockEntity(AeTestRig.POS);
        install(h, full.getUpgrades(), 4, 3);
        h.assertTrue(AccelerationRates.ioPortItemsPerTick(full.getUpgrades()) == 256 * 8 * 4096,
                "three tier V cards: " + AccelerationRates.ioPortItemsPerTick(full.getUpgrades()));

        AeTestRig.place(h, EPPItemAndBlock.EX_IO_PORT);
        var extended = (TileExIOPort) h.getBlockEntity(AeTestRig.POS);
        install(h, extended.getUpgrades(), 0, 5);
        h.assertTrue(AccelerationRates.extendedIoPortItemsPerTick(extended.getUpgrades()) == 2048 * 512,
                "five AE2 cards on the Extended IO Port");
        AeTestRig.place(h, EPPItemAndBlock.EX_IO_PORT);
        extended = (TileExIOPort) h.getBlockEntity(AeTestRig.POS);
        install(h, extended.getUpgrades(), 4, 5);
        h.assertTrue(AccelerationRates.extendedIoPortItemsPerTick(extended.getUpgrades()) == 2048 * 512 * 4096L,
                "five tier V cards on the Extended IO Port: " +
                        AccelerationRates.extendedIoPortItemsPerTick(extended.getUpgrades()));
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 20)
    public static void mixinsAreOnTheIoPorts(GameTestHelper h) {
        for (Class<?> type : List.of(IOPortBlockEntity.class, TileExIOPort.class)) {
            h.assertTrue(
                    Arrays.stream(type.getDeclaredMethods())
                            .anyMatch(m -> m.getName().contains("typeomega$tieredCards")),
                    "the acceleration mixin is not applied to " + type.getSimpleName());
        }
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 20)
    public static void onlyTransferMachinesTakeTheCards(GameTestHelper h) {
        List<ItemLike> transfer = List.of(AEParts.IMPORT_BUS, AEParts.EXPORT_BUS, EPPItemAndBlock.EX_IMPORT_BUS,
                EPPItemAndBlock.EX_EXPORT_BUS, EPPItemAndBlock.TAG_EXPORT_BUS, EPPItemAndBlock.MOD_EXPORT_BUS,
                EPPItemAndBlock.THRESHOLD_EXPORT_BUS, AEBlocks.IO_PORT, EPPItemAndBlock.EX_IO_PORT);
        for (ItemLike machine : transfer) {
            int limit = Upgrades.getMaxInstallable(AEItems.SPEED_CARD, machine);
            h.assertTrue(limit > 0, machine.asItem() + " has no AE2 card limit");
            for (AccelerationTier tier : AccelerationTier.values()) {
                h.assertTrue(Upgrades.getMaxInstallable(AccelerationCards.card(tier), machine) == limit,
                        machine.asItem() + " does not take " + tier + " up to its limit of " + limit);
            }
        }
        for (ItemLike machine : List.of(AEBlocks.MOLECULAR_ASSEMBLER, AEBlocks.INSCRIBER, AEBlocks.VIBRATION_CHAMBER,
                EPPItemAndBlock.EX_ASSEMBLER)) {
            for (AccelerationTier tier : AccelerationTier.values()) {
                h.assertTrue(Upgrades.getMaxInstallable(AccelerationCards.card(tier), machine) == 0,
                        machine.asItem() + " takes " + tier + ", which is only for transfer machines");
            }
        }
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 60)
    public static void sneakingSwapsTheLowestCardAtTheLimit(GameTestHelper h) {
        var player = AeTestRig.player(h);
        var host = AeTestRig.bus(h);
        var bus = (IOBusPart) AeTestRig.addPart(h, host, AEParts.IMPORT_BUS.asItem());
        install(h, bus.getUpgrades(), 0, 4);
        var card = AccelerationCards.card(AccelerationTier.II);

        var held = new ItemStack(card, 2);
        AeTestRig.use(h, player, held, false);
        h.assertTrue(bus.getUpgrades().getInstalledUpgrades(card) == 0 && held.getCount() == 2,
                "swapped without sneaking");

        var result = AeTestRig.use(h, player, held, true);
        h.assertTrue(result.consumesAction(), "the swap at the limit was not taken: " + result);
        h.assertTrue(
                bus.getUpgrades().getInstalledUpgrades(card) == 1 &&
                        bus.getUpgrades().getInstalledUpgrades(AEItems.SPEED_CARD) == 3,
                "the swap did not replace one AE2 card");
        h.assertTrue(held.getCount() == 1 && AeTestRig.count(player, AEItems.SPEED_CARD.asItem()) == 1,
                "the swapped card did not come back");

        // Nothing lower to replace: a tier II card on a bus full of tier V cards is refused and kept.
        var topped = AeTestRig.bus(h);
        var full = (IOBusPart) AeTestRig.addPart(h, topped, AEParts.IMPORT_BUS.asItem());
        install(h, full.getUpgrades(), 4, 4);
        var kept = new ItemStack(card);
        InteractionResult refused = AeTestRig.use(h, player, kept, true);
        h.assertTrue(
                refused == InteractionResult.FAIL && kept.getCount() == 1 &&
                        full.getUpgrades().getInstalledUpgrades(card) == 0,
                "a lower card replaced a higher one, or was spent: " + refused);
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 40)
    public static void recipesAreFourAe2CardsAroundACircuit(GameTestHelper h) {
        var manager = h.getLevel().getServer().getRecipeManager();
        for (AccelerationTier tier : AccelerationTier.values()) {
            var recipe = manager.byKey(GTIAMod.id("shaped/" + tier.id()));
            h.assertTrue(recipe.isPresent(), "no recipe for " + tier.id());
            h.assertTrue(recipe.get().getResultItem(h.getLevel().registryAccess()).is(AccelerationCards.card(tier)),
                    "wrong output of " + tier);
            var ingredients = recipe.get().getIngredients().stream().filter(i -> !i.isEmpty()).toList();
            h.assertTrue(ingredients.size() == 5, tier + " is not four cards and a circuit");
            h.assertTrue(ingredients.stream().filter(i -> i.test(AEItems.SPEED_CARD.stack())).count() == 4,
                    tier + " does not use four AE2 cards");
            var circuit = ForgeRegistries.ITEMS.tags().getTag(tier.circuit).stream().findFirst().orElseThrow();
            h.assertTrue(ingredients.stream().anyMatch(i -> i.test(new ItemStack(circuit))),
                    tier + " does not take its circuit");
        }
        h.succeed();
    }
}
