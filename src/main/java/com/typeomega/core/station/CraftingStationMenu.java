package com.typeomega.core.station;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.List;

/**
 * Menu of the Crafting Station, after the Tinkers' Construct station of GTNH (1.7.10): the grid stays in the block,
 * every container touching the station is a source with its own tab, and shift-click moves between the player and
 * those containers. Every container slot is a real menu slot, so shift-click, EMI recipe filling and dragging work;
 * only the selected tab and page are active on screen.
 * <p>
 * Slots: 0 result, 1-9 grid, 10-45 player, then the containers in tab order.
 */
public class CraftingStationMenu extends AbstractContainerMenu {

    public static final int COLUMNS = 9;
    public static final int ROWS = 6;
    public static final int PAGE_SIZE = COLUMNS * ROWS;
    /** Width of the container panel on the left; the crafting table sits right of it. */
    public static final int PANEL_WIDTH = 180;

    public static final int BUTTON_PREVIOUS_PAGE = 10;
    public static final int BUTTON_NEXT_PAGE = 11;
    public static final int BUTTON_DUMP = 12;

    static final int RESULT = 0, GRID_START = 1, GRID_END = 10, PLAYER_END = 46;

    private final ContainerLevelAccess access;
    private final BlockPos pos;
    private final Player player;
    private final CraftingStationBlockEntity station;
    private final StationGrid craftSlots;
    private final ResultContainer resultSlots = new ResultContainer();
    private final List<StationSides.Side> sides;
    private final DataSlot tab = DataSlot.standalone();
    private final DataSlot page = DataSlot.standalone();
    private final int firstSideSlot;
    private final int craftX;
    /** The stack on the cursor was picked up from a neighbouring container. */
    private boolean carriedFromSide;

    /** Server side. */
    public CraftingStationMenu(int id, Inventory inventory, BlockPos pos, List<StationSides.Side> sides) {
        this(GTIAStation.MENU.get(), id, inventory, pos, sides,
                inventory.player.level().getBlockEntity(pos) instanceof CraftingStationBlockEntity be ? be : null);
    }

    protected CraftingStationMenu(MenuType<?> type, int id, Inventory inventory, BlockPos pos,
                                  List<StationSides.Side> sides, CraftingStationBlockEntity station) {
        super(type, id);
        this.player = inventory.player;
        this.pos = pos;
        this.sides = sides;
        this.station = station;
        this.access = ContainerLevelAccess.create(inventory.player.level(), pos);
        NonNullList<ItemStack> items = station != null ? station.grid() :
                NonNullList.withSize(CraftingStationBlockEntity.GRID, ItemStack.EMPTY);
        this.craftSlots = new StationGrid(items, this::gridChanged);
        this.craftX = PANEL_WIDTH;
        addSlot(new ResultSlot(inventory.player, craftSlots, resultSlots, 0, craftX + 124, 35));
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new Slot(craftSlots, col + row * 3, craftX + 30 + col * 18, 17 + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, craftX + 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, craftX + 8 + col * 18, 142));
        firstSideSlot = slots.size();
        for (int s = 0; s < sides.size(); s++) {
            IItemHandler handler = sides.get(s).handler();
            for (int i = 0; i < sides.get(s).slots(); i++) {
                int inPage = i % PAGE_SIZE;
                addSlot(new SideSlot(handler, i, 8 + (inPage % COLUMNS) * 18, 18 + (inPage / COLUMNS) * 18, s,
                        i / PAGE_SIZE));
            }
        }
        addDataSlot(tab);
        addDataSlot(page);
        if (station != null && !player.level().isClientSide) station.open(this);
        refreshResult();
    }

    /** Client side, from the open-screen packet. */
    public static CraftingStationMenu fromNetwork(MenuType<CraftingStationMenu> type, int id, Inventory inventory,
                                                  FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        return new CraftingStationMenu(type, id, inventory, pos, StationSides.read(buf), null);
    }

    public List<StationSides.Side> sides() {
        return sides;
    }

    public int selectedTab() {
        return tab.get();
    }

    public int selectedPage() {
        return page.get();
    }

    /** X of the crafting table part, right of the container panel. */
    public int craftX() {
        return craftX;
    }

    public int pageCount() {
        if (sides.isEmpty()) return 0;
        int slotsOnTab = sides.get(Math.min(tab.get(), sides.size() - 1)).slots();
        return Math.max(1, (slotsOnTab + PAGE_SIZE - 1) / PAGE_SIZE);
    }

    /** First menu index of the container slots (grid and player inventory come before). */
    public int firstSideSlot() {
        return firstSideSlot;
    }

    /** Whether grid slot {@code index} (menu index 1-9) was filled from a neighbouring container. */
    public boolean gridFromSide(int index) {
        return station != null && station.fromSide(index - GRID_START);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= 0 && id < sides.size()) {
            tab.set(id);
            page.set(0);
            return true;
        }
        if (id == BUTTON_PREVIOUS_PAGE && page.get() > 0) {
            page.set(page.get() - 1);
            return true;
        }
        if (id == BUTTON_NEXT_PAGE && page.get() + 1 < pageCount()) {
            page.set(page.get() + 1);
            return true;
        }
        if (id == BUTTON_DUMP && !sides.isEmpty()) {
            dumpGrid();
            return true;
        }
        return false;
    }

    private void gridChanged() {
        slotsChanged(craftSlots);
        if (station != null && !player.level().isClientSide) station.gridChanged(this);
    }

    @Override
    public void slotsChanged(net.minecraft.world.Container container) {
        refreshResult();
    }

    /** Recomputes the crafting result from the grid (server side). */
    void refreshResult() {
        access.execute((level, at) -> updateResult(level));
    }

    private void updateResult(Level level) {
        if (level.isClientSide || !(player instanceof ServerPlayer serverPlayer)) return;
        ItemStack result = ItemStack.EMPTY;
        var recipe = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftSlots, level);
        if (recipe.isPresent()) {
            CraftingRecipe crafting = recipe.get();
            if (resultSlots.setRecipeUsed(level, serverPlayer, crafting)) {
                ItemStack assembled = crafting.assemble(craftSlots, level.registryAccess());
                if (assembled.isItemEnabled(level.enabledFeatures())) result = assembled;
            }
        }
        resultSlots.setItem(0, result);
        setRemoteSlot(0, result);
        if (serverPlayer.connection != null) {
            serverPlayer.connection.send(
                    new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, result));
        }
    }

    /**
     * Shift-click as in the GTNH station: the result refills matching stacks in the containers first, then goes to
     * the player; a grid stack goes back where it came from; the player's items go into the container on screen (the
     * grid when there is no container); a container's items go to the player.
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        boolean moved;
        if (index == RESULT) {
            access.execute((level, at) -> stack.getItem().onCraftedBy(stack, level, player));
            moved = refill(stack, firstSideSlot, slots.size());
            moved |= moveItemStackTo(stack, GRID_END, PLAYER_END, true);
            if (moved) slot.onQuickCraft(stack, copy);
        } else if (index < GRID_END) {
            moved = gridFromSide(index) && toSides(stack);
            moved |= moveItemStackTo(stack, GRID_END, PLAYER_END, false);
        } else if (index < PLAYER_END) {
            int[] range = activeSideRange();
            moved = range != null ? moveItemStackTo(stack, range[0], range[1], false) :
                    moveItemStackTo(stack, GRID_START, GRID_END, false);
        } else {
            moved = moveItemStackTo(stack, GRID_END, PLAYER_END, false);
        }
        if (!moved) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        if (stack.getCount() == copy.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        if (index == RESULT && !stack.isEmpty()) player.drop(stack, false);
        return copy;
    }

    /** Only into stacks of the same item that are already there (the GTNH station's chest refill). */
    private boolean refill(ItemStack stack, int start, int end) {
        boolean moved = false;
        for (int i = start; i < end && !stack.isEmpty(); i++) {
            Slot target = slots.get(i);
            ItemStack there = target.getItem();
            if (there.isEmpty() || !ItemStack.isSameItemSameTags(there, stack)) continue;
            int room = Math.min(target.getMaxStackSize(there), there.getMaxStackSize()) - there.getCount();
            if (room <= 0) continue;
            int amount = Math.min(room, stack.getCount());
            there.grow(amount);
            stack.shrink(amount);
            target.setChanged();
            moved = true;
        }
        return moved;
    }

    /** Into the container on screen first, then any other one. */
    private boolean toSides(ItemStack stack) {
        int[] active = activeSideRange();
        if (active == null) return false;
        boolean moved = moveItemStackTo(stack, active[0], active[1], false);
        if (!stack.isEmpty()) moved |= moveItemStackTo(stack, firstSideSlot, slots.size(), false);
        return moved;
    }

    /** The dump button: the grid goes into the containers, what does not fit to the player. */
    public void dumpGrid() {
        for (int i = GRID_START; i < GRID_END; i++) {
            Slot slot = slots.get(i);
            if (!slot.hasItem()) continue;
            ItemStack stack = slot.getItem().copy();
            refill(stack, firstSideSlot, slots.size());
            if (!stack.isEmpty()) moveItemStackTo(stack, firstSideSlot, slots.size(), false);
            if (!stack.isEmpty()) moveItemStackTo(stack, GRID_END, PLAYER_END, false);
            slot.set(stack);
        }
    }

    /** Keeps track of stacks carried from a container to the grid, so a shift-click sends them back. */
    @Override
    public void clicked(int slotId, int button, ClickType type, Player player) {
        boolean carriedBefore = !getCarried().isEmpty();
        ItemStack gridBefore = slotId >= GRID_START && slotId < GRID_END ? slots.get(slotId).getItem().copy() :
                ItemStack.EMPTY;
        super.clicked(slotId, button, type, player);
        if (type != ClickType.PICKUP || slotId < 0 || slotId >= slots.size()) return;
        if (slotId >= firstSideSlot && !carriedBefore && !getCarried().isEmpty()) carriedFromSide = true;
        if (slotId >= GRID_START && slotId < GRID_END && station != null && carriedBefore && carriedFromSide) {
            ItemStack now = slots.get(slotId).getItem();
            if (now.getCount() > gridBefore.getCount()) station.setFromSide(slotId - GRID_START, true);
        }
        if (getCarried().isEmpty()) carriedFromSide = false;
    }

    /** Menu slot range of the selected container, or null when there is none. */
    private int[] activeSideRange() {
        if (sides.isEmpty()) return null;
        int start = firstSideSlot;
        int selected = Math.min(tab.get(), sides.size() - 1);
        for (int s = 0; s < selected; s++) start += sides.get(s).slots();
        return new int[] { start, start + sides.get(selected).slots() };
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != resultSlots && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (station != null) {
            station.close(this);
        } else {
            access.execute((level, at) -> clearContainer(player, craftSlots));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, GTIAStation.BLOCK.get());
    }

    public BlockPos pos() {
        return pos;
    }

    /** A slot of a neighbouring container, shown only on its tab and page. */
    public final class SideSlot extends SlotItemHandler {

        private final int sideIndex;
        private final int pageIndex;

        SideSlot(IItemHandler handler, int index, int x, int y, int sideIndex, int pageIndex) {
            super(handler, index, x, y);
            this.sideIndex = sideIndex;
            this.pageIndex = pageIndex;
        }

        @Override
        public boolean isActive() {
            return tab.get() == sideIndex && page.get() == pageIndex;
        }
    }
}
