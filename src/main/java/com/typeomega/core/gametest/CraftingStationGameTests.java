package com.typeomega.core.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.items.IItemHandler;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.station.CraftingStationMenu;
import com.typeomega.core.station.TypeOmegaStation;
import com.typeomega.core.station.StationSides;

/**
 * The Crafting Station behaves like the Tinkers' Construct station of GTNH: it finds the containers on its six sides,
 * keeps its grid, and shift-click moves between the player and those containers.
 */
@GameTestHolder(TypeOmegaMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class CraftingStationGameTests {

    private static final BlockPos STATION = new BlockPos(2, 2, 2);

    private CraftingStationGameTests() {}

    private static BlockPos place(GameTestHelper helper) {
        helper.setBlock(STATION, TypeOmegaStation.BLOCK.get());
        return helper.absolutePos(STATION);
    }

    private static FakePlayer player(GameTestHelper helper, BlockPos pos) {
        var player = FakePlayerFactory.getMinecraft(helper.getLevel());
        player.getInventory().clearContent();
        player.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
        return player;
    }

    private static CraftingStationMenu open(GameTestHelper helper, FakePlayer player, BlockPos pos) {
        return new CraftingStationMenu(1, player.getInventory(), pos, StationSides.find(helper.getLevel(), pos));
    }

    private static int count(IItemHandler handler, Item item) {
        int n = 0;
        for (int i = 0; i < handler.getSlots(); i++) {
            if (handler.getStackInSlot(i).is(item)) n += handler.getStackInSlot(i).getCount();
        }
        return n;
    }

    private static int count(FakePlayer player, Item item) {
        return player.getInventory().countItem(item);
    }

    /** A chest north, one above, a double chest west touching with one half, and another station south. */
    @GameTest(template = "qa_empty_5x5", batch = "station", timeoutTicks = 20)
    public static void stationFindsNeighbourContainersOnce(GameTestHelper helper) {
        BlockPos pos = place(helper);
        helper.setBlock(STATION.north(), Blocks.CHEST);
        helper.setBlock(STATION.above(), Blocks.CHEST);
        helper.setBlock(STATION.south(), TypeOmegaStation.BLOCK.get());
        BlockPos west = STATION.west();
        helper.setBlock(west, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.TYPE, ChestType.RIGHT)
                .setValue(ChestBlock.FACING, Direction.EAST));
        helper.setBlock(west.north(), Blocks.CHEST.defaultBlockState().setValue(ChestBlock.TYPE, ChestType.LEFT)
                .setValue(ChestBlock.FACING, Direction.EAST));
        var sides = StationSides.find(helper.getLevel(), pos);
        helper.assertTrue(sides.size() == 3, "expected the north, top and west double chests, got " +
                sides.stream().map(s -> s.direction() + ":" + s.handler().getSlots()).toList());
        helper.assertTrue(sides.stream().anyMatch(s -> s.direction() == Direction.UP), "the chest above counts");
        helper.assertTrue(sides.stream().noneMatch(s -> s.direction() == Direction.SOUTH),
                "another station is never a source");
        helper.assertTrue(sides.stream().anyMatch(s -> s.handler().getSlots() == 54), "double chest has 54 slots");
        var menu = open(helper, player(helper, pos), pos);
        helper.assertTrue(menu.slots.size() == menu.firstSideSlot() + 27 + 27 + 54,
                "every container slot is a menu slot, got " + menu.slots.size());
        helper.succeed();
    }

    /** Closing the screen keeps the grid in the station; opening it again shows it and its result. */
    @GameTest(template = "qa_empty_5x5", batch = "station", timeoutTicks = 20)
    public static void stationKeepsItsGrid(GameTestHelper helper) {
        BlockPos pos = place(helper);
        var player = player(helper, pos);
        var menu = open(helper, player, pos);
        for (int slot : new int[] { 1, 2, 4, 5 }) menu.getSlot(slot).set(new ItemStack(Items.OAK_PLANKS));
        menu.removed(player);
        helper.assertTrue(count(player, Items.OAK_PLANKS) == 0, "closing must not hand the grid back");
        var again = open(helper, player, pos);
        int planks = 0;
        for (int i = 1; i < 10; i++) planks += again.getSlot(i).getItem().getCount();
        helper.assertTrue(planks == 4, "the grid should still hold 4 planks, holds " + planks);
        helper.assertTrue(again.getSlot(0).getItem().is(Items.CRAFTING_TABLE),
                "2x2 planks should give a crafting table, got " + again.getSlot(0).getItem());
        helper.succeed();
    }

    /** Shift-clicking the result fills the matching stack in the neighbouring chest before the player. */
    @GameTest(template = "qa_empty_5x5", batch = "station", timeoutTicks = 20)
    public static void shiftClickResultRefillsTheNeighbour(GameTestHelper helper) {
        BlockPos pos = place(helper);
        helper.setBlock(STATION.east(), Blocks.CHEST);
        var player = player(helper, pos);
        var menu = open(helper, player, pos);
        var chest = menu.sides().get(0).handler();
        chest.insertItem(0, new ItemStack(Items.OAK_PLANKS, 10), false);
        menu.getSlot(5).set(new ItemStack(Items.OAK_LOG));
        helper.assertTrue(menu.getSlot(0).getItem().is(Items.OAK_PLANKS), "a log should give planks");
        menu.quickMoveStack(player, 0);
        helper.assertTrue(count(chest, Items.OAK_PLANKS) == 14,
                "the 4 planks should join the chest's stack, chest has " + count(chest, Items.OAK_PLANKS));
        helper.assertTrue(count(player, Items.OAK_PLANKS) == 0, "nothing should reach the player");
        helper.assertTrue(menu.getSlot(5).getItem().isEmpty(), "the log is used up");
        helper.succeed();
    }

    /** Shift-click pulls from the neighbour to the player, and sends the player's items into the neighbour. */
    @GameTest(template = "qa_empty_5x5", batch = "station", timeoutTicks = 20)
    public static void shiftClickUsesTheNeighbour(GameTestHelper helper) {
        BlockPos pos = place(helper);
        helper.setBlock(STATION.below(), Blocks.CHEST);
        var player = player(helper, pos);
        var menu = open(helper, player, pos);
        var chest = menu.sides().get(0).handler();
        chest.insertItem(3, new ItemStack(Items.IRON_INGOT, 16), false);
        menu.quickMoveStack(player, menu.firstSideSlot() + 3);
        helper.assertTrue(count(player, Items.IRON_INGOT) == 16 && count(chest, Items.IRON_INGOT) == 0,
                "the ingots should move from the chest to the player");
        player.getInventory().setItem(9, new ItemStack(Items.COBBLESTONE, 32));
        menu.quickMoveStack(player, 10);
        helper.assertTrue(count(chest, Items.COBBLESTONE) == 32,
                "shift-click should move 32 cobblestone into the chest, moved " + count(chest, Items.COBBLESTONE));
        helper.succeed();
    }

    /** Without a container the player's items go into the grid; the dump button empties the grid into one. */
    @GameTest(template = "qa_empty_5x5", batch = "station", timeoutTicks = 20)
    public static void gridFallbackAndDump(GameTestHelper helper) {
        BlockPos pos = place(helper);
        var player = player(helper, pos);
        var alone = open(helper, player, pos);
        helper.assertTrue(alone.sides().isEmpty(), "no container next to the station yet");
        helper.assertTrue(!alone.clickMenuButton(player, CraftingStationMenu.BUTTON_DUMP),
                "dump does nothing without a container");
        player.getInventory().setItem(9, new ItemStack(Items.STICK, 8));
        alone.quickMoveStack(player, 10);
        helper.assertTrue(alone.getSlot(1).getItem().is(Items.STICK), "with no container the grid takes the sticks");
        alone.removed(player);
        helper.setBlock(STATION.north(), Blocks.BARREL);
        var menu = open(helper, player, pos);
        helper.assertTrue(menu.clickMenuButton(player, CraftingStationMenu.BUTTON_DUMP), "dump with a barrel");
        var barrel = menu.sides().get(0).handler();
        helper.assertTrue(count(barrel, Items.STICK) == 8 && menu.getSlot(1).getItem().isEmpty(),
                "dump should move the 8 sticks into the barrel");
        helper.succeed();
    }

    /** A stack carried from the neighbour into the grid goes back to the neighbour on shift-click, not the player. */
    @GameTest(template = "qa_empty_5x5", batch = "station", timeoutTicks = 20)
    public static void gridStackFromNeighbourGoesBack(GameTestHelper helper) {
        BlockPos pos = place(helper);
        helper.setBlock(STATION.east(), Blocks.CHEST);
        var player = player(helper, pos);
        var menu = open(helper, player, pos);
        var chest = menu.sides().get(0).handler();
        chest.insertItem(0, new ItemStack(Items.IRON_INGOT, 5), false);
        menu.clicked(menu.firstSideSlot(), 0, ClickType.PICKUP, player);
        menu.clicked(5, 0, ClickType.PICKUP, player);
        helper.assertTrue(menu.getSlot(5).getItem().getCount() == 5 && menu.gridFromSide(5),
                "the ingots should sit in the grid, marked as taken from the chest");
        menu.quickMoveStack(player, 5);
        helper.assertTrue(count(chest, Items.IRON_INGOT) == 5 && count(player, Items.IRON_INGOT) == 0,
                "shift-click should send the ingots back to the chest");
        helper.assertTrue(!menu.gridFromSide(5), "the emptied slot forgets where it came from");
        player.getInventory().setItem(9, new ItemStack(Items.GOLD_INGOT, 3));
        menu.clicked(10, 0, ClickType.PICKUP, player);
        menu.clicked(6, 0, ClickType.PICKUP, player);
        menu.quickMoveStack(player, 6);
        helper.assertTrue(count(player, Items.GOLD_INGOT) == 3 && count(chest, Items.GOLD_INGOT) == 0,
                "a stack the player put in the grid goes back to the player");
        helper.succeed();
    }
}
