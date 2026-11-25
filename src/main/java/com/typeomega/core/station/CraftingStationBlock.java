package com.typeomega.core.station;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

/**
 * Type Omega Crafting Station, after the Tinkers' Construct station of GTNH: a crafting table that keeps its grid
 * and shows every container touching it (any of the six sides) as a tab, so recipes can be filled from chests
 * without carrying everything.
 */
public class CraftingStationBlock extends Block implements EntityBlock {

    public CraftingStationBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return TypeOmegaStation.BLOCK_ENTITY.create(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (player instanceof ServerPlayer serverPlayer) {
            var sides = StationSides.find(level, pos);
            MenuProvider provider = new SimpleMenuProvider(
                    (id, inventory, p) -> new CraftingStationMenu(id, inventory, pos, sides),
                    Component.translatable("block.typeomega.crafting_station"));
            NetworkHooks.openScreen(serverPlayer, provider, buf -> StationSides.write(buf, pos, sides));
        }
        return InteractionResult.CONSUME;
    }

    /** Breaking the station drops the grid it kept. */
    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof CraftingStationBlockEntity be) {
            Containers.dropContents(level, pos, be.grid());
        }
        super.onRemove(state, level, pos, newState, moved);
    }
}
