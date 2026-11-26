package com.typeomega.core.gametest.ae;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import appeng.api.networking.IGrid;
import appeng.api.networking.IManagedGridNode;
import appeng.me.helpers.IGridConnectedBlockEntity;
import com.typeomega.core.gametest.TestPlayers;
import com.typeomega.core.integration.ae.wireless.AeWireless;
import com.typeomega.core.integration.ae.wireless.WirelessData;

/**
 * A powered network (see {@link AeGridRig}) with a Wireless Connector on the cable's south side, a free spot for ME
 * parts away from every cable, and a configurator in the hands of a test player.
 */
final class WirelessTestRig {

    static final BlockPos CONNECTOR = AeGridRig.PART;
    static final BlockPos FAR = new BlockPos(4, 2, 4);
    static final BlockPos FAR_2 = new BlockPos(0, 2, 4);

    final GameTestHelper h;
    final AeGridRig grid;
    final ServerPlayer player;
    final ItemStack configurator = new ItemStack(AeWireless.CONFIGURATOR.get());

    WirelessTestRig(GameTestHelper h, boolean crafting) {
        this.h = h;
        grid = crafting ? new AeGridRig(h).withCrafting() : new AeGridRig(h);
        h.setBlock(CONNECTOR, AeWireless.CONNECTOR.get());
        player = TestPlayers.survival(h);
        player.setPos(h.absolutePos(FAR).getX() + 0.5, h.absolutePos(FAR).getY() + 1, h.absolutePos(FAR).getZ() + 0.5);
    }

    MetaMachine place(BlockPos at, MachineDefinition definition) {
        h.setBlock(at, definition.getBlock());
        return ((MetaMachineBlockEntity) h.getBlockEntity(at)).getMetaMachine();
    }

    MetaMachine machineAt(BlockPos at) {
        return MetaMachine.getMachine(h.getLevel(), h.absolutePos(at));
    }

    /** What a chunk reload does: the block entity is rebuilt from its saved tag, so machine and node are new. */
    void unloadAndReload(BlockPos at, net.minecraft.nbt.CompoundTag saved) {
        BlockPos abs = h.absolutePos(at);
        var state = h.getLevel().getBlockState(abs);
        h.getLevel().removeBlockEntity(abs);
        h.getLevel().setBlock(abs, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 2);
        h.getLevel().setBlock(abs, state, 2);
        h.getLevel().getBlockEntity(abs).load(saved);
    }

    static IManagedGridNode node(MetaMachine machine) {
        return ((IGridConnectedBlockEntity) machine).getMainNode();
    }

    /** The part has built its node: a tick or two after it is placed. */
    static boolean hasNode(MetaMachine machine) {
        return node(machine).getNode() != null;
    }

    /** Whether the part is in the same grid as the drive. */
    boolean inTheGrid(MetaMachine machine) {
        IGrid own = node(machine).getGrid();
        return own != null && own == grid.grid();
    }

    /** Sneak right-click in the air: creates a network named like the item and selects it. */
    void createNetwork(String name) {
        configurator.setHoverName(net.minecraft.network.chat.Component.literal(name));
        player.setShiftKeyDown(true);
        player.setItemInHand(InteractionHand.MAIN_HAND, configurator);
        configurator.getItem().use(h.getLevel(), player, InteractionHand.MAIN_HAND);
    }

    /** Right-click in the air: next network of the team. */
    void cycle() {
        player.setShiftKeyDown(false);
        player.setItemInHand(InteractionHand.MAIN_HAND, configurator);
        configurator.getItem().use(h.getLevel(), player, InteractionHand.MAIN_HAND);
    }

    /** Right-click a block with the configurator, sneaking or not. */
    InteractionResult click(BlockPos at, boolean sneaking) {
        return click(player, configurator, at, sneaking);
    }

    InteractionResult click(ServerPlayer by, ItemStack stack, BlockPos at, boolean sneaking) {
        by.setShiftKeyDown(sneaking);
        by.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockPos abs = h.absolutePos(at);
        var hit = new BlockHitResult(new Vec3(abs.getX() + 0.5, abs.getY() + 0.5, abs.getZ() + 0.5), Direction.UP, abs,
                false);
        return stack.getItem().onItemUseFirst(stack, new UseOnContext(h.getLevel(), by, InteractionHand.MAIN_HAND,
                stack, hit));
    }

    WirelessData.Binding bindingAt(BlockPos at) {
        BlockPos abs = h.absolutePos(at);
        return WirelessData.get(h.getLevel().getServer())
                .at(new WirelessData.Site(h.getLevel().dimension().location(), abs.asLong()));
    }
}
