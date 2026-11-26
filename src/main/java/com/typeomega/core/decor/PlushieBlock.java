package com.typeomega.core.decor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A decorative seated plush, after the fishbig of ExtendedAE (LGPL-3.0): it faces the player who places it, can be
 * waterlogged, and squeaks softly with a few hearts when right-clicked. It has no other behaviour.
 */
public class PlushieBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    // EN: Boxes are written for the plush facing north (legs towards -Z) and rotated for the other facings. The
    // EN: outline stops at y = 16 although the head model reaches 19, so it never leaves the block's own cell.
    // PT: As caixas são escritas para a pelúcia virada ao norte (pernas para -Z) e giradas nas outras direções. O
    // PT: contorno para em y = 16, embora o modelo da cabeça chegue a 19, para não sair da célula do bloco.
    // ZH: 方块盒按朝北（腿朝 -Z）编写，其他朝向时旋转。轮廓止于 y = 16，虽然头部模型高达 19，以免超出方块自身格子。
    private static final double[] BODY = { 1, 0, 1, 15, 8.5, 12 };
    private static final double[] HEAD = { 2.5, 8.5, 3.5, 13.5, 16, 14.5 };
    // EN: Collision is only the sitting body, 8 px high, so walking past or stepping onto it is comfortable.
    // PT: A colisão é só o corpo sentado, com 8 px de altura, para passar ou subir nela sem esforço.
    // ZH: 碰撞箱只是坐着的身体，高 8 像素，方便走过或踏上。
    private static final double[] SEAT = { 3, 0, 1, 13, 8, 12 };

    private final VoxelShape[] outline = new VoxelShape[4];
    private final VoxelShape[] collision = new VoxelShape[4];

    public PlushieBlock(Properties properties) {
        super(properties.sound(SoundType.WOOL).noOcclusion());
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            outline[facing.get2DDataValue()] = Shapes.or(box(BODY, facing), box(HEAD, facing));
            collision[facing.get2DDataValue()] = box(SEAT, facing);
        }
    }

    private static VoxelShape box(double[] b, Direction facing) {
        double x1 = b[0], z1 = b[2], x2 = b[3], z2 = b[5];
        double[] r = switch (facing) {
            case EAST -> new double[] { 16 - z2, x1, 16 - z1, x2 };
            case SOUTH -> new double[] { 16 - x2, 16 - z2, 16 - x1, 16 - z1 };
            case WEST -> new double[] { z1, 16 - x2, z2, 16 - x1 };
            default -> new double[] { x1, z1, x2, z2 };
        };
        return Block.box(r[0], b[1], r[1], r[2], b[4], r[3]);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return outline[state.getValue(FACING).get2DDataValue()];
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
                                        CollisionContext context) {
        return collision[state.getValue(FACING).get2DDataValue()];
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return !state.getValue(WATERLOGGED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor level,
                                  BlockPos pos, BlockPos neighbourPos) {
        if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, direction, neighbour, level, pos, neighbourPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hit) {
        if (level.isClientSide) {
            for (int i = 0; i < 3; i++) {
                level.addParticle(ParticleTypes.HEART, pos.getX() + 0.3 + level.random.nextDouble() * 0.4,
                        pos.getY() + 1.1, pos.getZ() + 0.3 + level.random.nextDouble() * 0.4, 0, 0.05, 0);
            }
        } else {
            level.playSound(null, pos, SoundType.WOOL.getPlaceSound(), SoundSource.BLOCKS, 0.8F,
                    1.3F + level.random.nextFloat() * 0.3F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
