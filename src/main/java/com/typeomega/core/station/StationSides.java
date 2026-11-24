package com.typeomega.core.station;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * The containers next to a Crafting Station. As in the Tinkers' Construct station of GTNH every one of the six sides
 * counts, a double chest is one container, and another station is never a source.
 */
public final class StationSides {

    /** Sides checked, in tab order (Forge's direction order, like the GTNH station). */
    public static final Direction[] SIDES = Direction.values();
    /** Large storages show their first slots only; keeps the menu small enough to sync. */
    public static final int MAX_SLOTS_PER_SIDE = CraftingStationMenu.PAGE_SIZE * 10;

    public record Side(Direction direction, IItemHandler handler, ItemStack icon, Component name) {

        public int slots() {
            return Math.min(handler.getSlots(), MAX_SLOTS_PER_SIDE);
        }
    }

    private StationSides() {}

    public static List<Side> find(Level level, BlockPos pos) {
        List<Side> found = new ArrayList<>();
        List<BlockPos> seenChests = new ArrayList<>();
        for (Direction direction : SIDES) {
            BlockPos at = pos.relative(direction);
            BlockEntity entity = level.getBlockEntity(at);
            if (entity == null || entity instanceof CraftingStationBlockEntity) continue;
            var state = level.getBlockState(at);
            if (state.getBlock() instanceof ChestBlock && state.hasProperty(ChestBlock.TYPE) &&
                    state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
                BlockPos other = at.relative(ChestBlock.getConnectedDirection(state));
                if (seenChests.contains(other)) continue;
                seenChests.add(at);
            }
            entity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite()).resolve()
                    .filter(handler -> handler.getSlots() > 0)
                    .ifPresent(handler -> found.add(new Side(direction, handler,
                            new ItemStack(state.getBlock().asItem()), state.getBlock().getName())));
        }
        return found;
    }

    public static void write(FriendlyByteBuf buf, BlockPos pos, List<Side> sides) {
        buf.writeBlockPos(pos);
        buf.writeVarInt(sides.size());
        for (Side side : sides) {
            buf.writeEnum(side.direction());
            buf.writeVarInt(side.slots());
            buf.writeItem(side.icon());
            buf.writeComponent(side.name());
        }
    }

    /** The client copy: same sizes, names and icons, empty handlers that the menu sync fills. */
    public static List<Side> read(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        List<Side> sides = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Direction direction = buf.readEnum(Direction.class);
            int slots = buf.readVarInt();
            ItemStack icon = buf.readItem();
            Component name = buf.readComponent();
            sides.add(new Side(direction, new ItemStackHandler(slots), icon, name));
        }
        return sides;
    }
}
