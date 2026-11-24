package com.typeomega.core.station;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps the crafting grid of the station in the world, as the Tinkers' Construct station of GTNH does: closing the
 * screen leaves the items in the grid, breaking the block drops them. Also remembers which grid slots were filled
 * from a neighbouring container, so a shift-click sends them back there.
 */
public class CraftingStationBlockEntity extends BlockEntity {

    public static final int GRID = 9;
    private static final String FROM_SIDE = "FromSide";

    private final NonNullList<ItemStack> grid = NonNullList.withSize(GRID, ItemStack.EMPTY);
    private int fromSide;
    /** Menus open on this station, told when the grid changes so every viewer sees the new result. */
    private final List<CraftingStationMenu> viewers = new ArrayList<>();

    public CraftingStationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public NonNullList<ItemStack> grid() {
        return grid;
    }

    public boolean fromSide(int slot) {
        return slot >= 0 && slot < GRID && (fromSide & (1 << slot)) != 0;
    }

    public void setFromSide(int slot, boolean value) {
        if (slot < 0 || slot >= GRID) return;
        int next = value ? fromSide | (1 << slot) : fromSide & ~(1 << slot);
        if (next != fromSide) {
            fromSide = next;
            setChanged();
        }
    }

    void open(CraftingStationMenu menu) {
        viewers.add(menu);
    }

    void close(CraftingStationMenu menu) {
        viewers.remove(menu);
    }

    /** The grid changed through one menu: save, forget emptied slots and refresh the result of the others. */
    void gridChanged(CraftingStationMenu source) {
        for (int i = 0; i < GRID; i++) if (grid.get(i).isEmpty()) setFromSide(i, false);
        setChanged();
        for (CraftingStationMenu viewer : List.copyOf(viewers)) if (viewer != source) viewer.refreshResult();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        for (int i = 0; i < GRID; i++) grid.set(i, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, grid);
        fromSide = tag.getInt(FROM_SIDE) & ((1 << GRID) - 1);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, grid);
        tag.putInt(FROM_SIDE, fromSide);
    }
}
