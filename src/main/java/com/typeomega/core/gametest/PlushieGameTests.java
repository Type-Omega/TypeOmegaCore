package com.typeomega.core.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.decor.PlushieBlock;
import com.typeomega.core.decor.TypeOmegaPlushies;

import java.util.List;

/**
 * The Raishxn and Nyra plushies are plain decoration: they place in the four directions, survive, drop themselves,
 * waterlog, do nothing on right-click, and are not full cubes.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class PlushieGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";
    private static final BlockPos POS = new BlockPos(2, 2, 2);
    private static final List<BlockEntry<PlushieBlock>> PLUSHIES = List.of(TypeOmegaPlushies.RAISHXN,
            TypeOmegaPlushies.NYRA);

    private PlushieGameTests() {}

    @GameTest(template = TEMPLATE, timeoutTicks = 40)
    public static void plushiesPlaceInEveryFacingAndSurvive(GameTestHelper h) {
        for (var entry : PLUSHIES) {
            for (Direction facing : Direction.Plane.HORIZONTAL) {
                h.setBlock(POS, entry.get().defaultBlockState().setValue(PlushieBlock.FACING, facing));
                BlockState state = h.getBlockState(POS);
                h.assertTrue(state.is(entry.get()) && state.getValue(PlushieBlock.FACING) == facing,
                        entry.getId() + " did not keep facing " + facing);
            }
        }
        h.setBlock(POS, TypeOmegaPlushies.NYRA.get().defaultBlockState());
        h.runAfterDelay(10, () -> {
            h.assertBlock(POS, b -> b == TypeOmegaPlushies.NYRA.get(), () -> "the plush vanished by itself");
            h.succeed();
        });
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 20)
    public static void plushiesDropThemselves(GameTestHelper h) {
        for (var entry : PLUSHIES) {
            h.setBlock(POS, entry.get().defaultBlockState());
            List<ItemStack> drops = Block.getDrops(h.getBlockState(POS), h.getLevel(), h.absolutePos(POS), null);
            h.assertTrue(drops.size() == 1 && drops.get(0).is(entry.get().asItem()),
                    entry.getId() + " drops " + drops + " instead of itself");
        }
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 20)
    public static void plushiesWaterlog(GameTestHelper h) {
        FakePlayer player = FakePlayerFactory.getMinecraft(h.getLevel());
        for (var entry : PLUSHIES) {
            h.setBlock(POS, Blocks.WATER.defaultBlockState());
            BlockPos abs = h.absolutePos(POS);
            var hit = new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false);
            var context = new BlockPlaceContext(h.getLevel(), player, InteractionHand.MAIN_HAND,
                    new ItemStack(entry.get()), hit);
            BlockState placed = entry.get().getStateForPlacement(context);
            h.assertTrue(placed != null && placed.getValue(PlushieBlock.WATERLOGGED),
                    entry.getId() + " placed in water is not waterlogged");
            h.setBlock(POS, placed);
            h.assertTrue(h.getLevel().getFluidState(abs).getType() == Fluids.WATER,
                    entry.getId() + " waterlogged holds no water");
            h.setBlock(POS, entry.get().defaultBlockState());
            h.assertTrue(h.getLevel().getFluidState(abs).isEmpty(), entry.getId() + " dry holds a fluid");
        }
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 20)
    public static void rightClickChangesNothing(GameTestHelper h) {
        FakePlayer player = FakePlayerFactory.getMinecraft(h.getLevel());
        for (var entry : PLUSHIES) {
            for (Direction facing : Direction.Plane.HORIZONTAL) {
                h.setBlock(POS, entry.get().defaultBlockState().setValue(PlushieBlock.FACING, facing));
                BlockPos abs = h.absolutePos(POS);
                BlockState before = h.getBlockState(POS);
                var hit = new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false);
                InteractionResult result = before.use(h.getLevel(), player, InteractionHand.MAIN_HAND, hit);
                h.assertTrue(result.consumesAction(), entry.getId() + " ignored the right-click: " + result);
                h.assertTrue(h.getBlockState(POS) == before, entry.getId() + " changed state on right-click");
            }
        }
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 20)
    public static void plushiesAreNotFullCubes(GameTestHelper h) {
        for (var entry : PLUSHIES) {
            for (Direction facing : Direction.Plane.HORIZONTAL) {
                h.setBlock(POS, entry.get().defaultBlockState().setValue(PlushieBlock.FACING, facing));
                BlockState state = h.getBlockState(POS);
                BlockPos abs = h.absolutePos(POS);
                var collision = state.getCollisionShape(h.getLevel(), abs);
                var outline = state.getShape(h.getLevel(), abs);
                h.assertTrue(!state.isCollisionShapeFullBlock(h.getLevel(), abs) && collision != Shapes.block(),
                        entry.getId() + " collides as a full cube");
                h.assertTrue(!collision.isEmpty() && collision.max(Direction.Axis.Y) < 1.0,
                        entry.getId() + " collision should be a low seat, not " + collision);
                h.assertTrue(!outline.isEmpty() && outline.max(Direction.Axis.Y) <= 1.0 &&
                        outline.max(Direction.Axis.Y) > collision.max(Direction.Axis.Y),
                        entry.getId() + " outline should cover the head too");
                h.assertTrue(!state.canOcclude(), entry.getId() + " occludes its neighbours");
            }
        }
        h.succeed();
    }
}
