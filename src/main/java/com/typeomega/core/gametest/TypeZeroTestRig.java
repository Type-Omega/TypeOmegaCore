package com.typeomega.core.gametest;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMaintenanceMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Builds the Type 0 megastructures from their previews far from the test area, one box per slot. */
final class TypeZeroTestRig {

    private TypeZeroTestRig() {}

    /** A placed structure: the corner it starts at, its size and its controller. */
    record Placed(BlockPos origin, int nx, int ny, int nz, MultiblockControllerMachine controller) {

        BlockPos at(int x, int y, int z) {
            return origin.offset(x, y, z);
        }

        /** Positions holding {@code block}, in preview order. */
        List<BlockPos> find(GameTestHelper h, Block block) {
            List<BlockPos> found = new ArrayList<>();
            for (int z = 0; z < nz; z++)
                for (int y = 0; y < ny; y++)
                    for (int x = 0; x < nx; x++)
                        if (h.getLevel().getBlockState(at(x, y, z)).is(block)) found.add(at(x, y, z));
            return found;
        }
    }

    /**
     * Absolute corner of a slot: 128 blocks apart, 3600 blocks south of the test and at y 200. The tests of a batch
     * stand a few blocks apart, so the gap must hold the widest structure (the Cosmodrome and its Rocket Assembler,
     * 41 blocks) plus that shift.
     */
    static BlockPos slot(GameTestHelper h, int slot) {
        BlockPos zero = h.absolutePos(BlockPos.ZERO);
        return new BlockPos(zero.getX() + 128 * slot, 200, zero.getZ() + 3600);
    }

    /**
     * Places the first preview of {@code definition} at {@code origin}, after clearing its box (the test world is
     * kept between runs), with the given owner. Hatches the preview chose are replaced by {@code casing} when it is
     * not null, so a test can put its own.
     */
    static Placed place(GameTestHelper h, MultiblockMachineDefinition definition, BlockPos origin, UUID owner,
                        Block casing) {
        var shape = definition.getMatchingShapes().get(0).getBlocks();
        var level = h.getLevel();
        int nx = shape.length, ny = shape[0].length, nz = shape[0][0].length;
        for (int cx = origin.getX() >> 4; cx <= (origin.getX() + nx) >> 4; cx++)
            for (int cz = origin.getZ() >> 4; cz <= (origin.getZ() + nz) >> 4; cz++)
                level.setChunkForced(cx, cz, true);
        BlockState air = Blocks.AIR.defaultBlockState();
        for (int x = 0; x < nx; x++)
            for (int y = 0; y < ny; y++)
                for (int z = 0; z < nz; z++)
                    if (!level.getBlockState(origin.offset(x, y, z)).isAir())
                        level.setBlock(origin.offset(x, y, z), air, 2 | 16);
        BlockPos controllerPos = null;
        for (int x = 0; x < nx; x++)
            for (int y = 0; y < ny; y++)
                for (int z = 0; z < nz; z++) {
                    var info = shape[x][y][z];
                    if (info == null) continue;
                    BlockState state = info.getBlockState();
                    if (state.is(definition.getBlock())) controllerPos = origin.offset(x, y, z);
                    else if (casing != null &&
                            state.getBlock() instanceof com.gregtechceu.gtceu.api.block.MetaMachineBlock)
                        state = casing.defaultBlockState();
                    level.setBlock(origin.offset(x, y, z), state, 2 | 16);
                }
        if (controllerPos == null) throw new IllegalStateException("no controller in the preview of " +
                definition.getId());
        var controller = (MultiblockControllerMachine) MetaMachine.getMachine(level, controllerPos);
        controller.setOwnerUUID(owner);
        return new Placed(origin, nx, ny, nz, controller);
    }

    /**
     * Removes the controllers of {@code definition} left within {@code range} blocks of {@code center} by earlier
     * runs: the test world is kept, and when the test layout shifts an old Rocket Assembler can fall within the
     * Cosmodrome's link range.
     */
    static void removeControllers(GameTestHelper h, MultiblockMachineDefinition definition, BlockPos center,
                                  int range) {
        var level = h.getLevel();
        List<BlockPos> found = new ArrayList<>();
        for (int cx = (center.getX() - range) >> 4; cx <= (center.getX() + range) >> 4; cx++)
            for (int cz = (center.getZ() - range) >> 4; cz <= (center.getZ() + range) >> 4; cz++) {
                var chunk = level.getChunkSource().getChunkNow(cx, cz);
                if (chunk == null) continue;
                for (var entity : chunk.getBlockEntities().values())
                    if (entity instanceof com.gregtechceu.gtceu.api.machine.IMachineBlockEntity holder &&
                            holder.getMetaMachine().getDefinition() == definition)
                        found.add(entity.getBlockPos());
            }
        for (BlockPos pos : found) level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2 | 16);
    }

    /** Checks the pattern the way the world does, runs the formation hook and fixes the maintenance hatches. */
    static boolean form(MultiblockControllerMachine controller) {
        if (!controller.checkPatternWithLock()) return false;
        controller.onStructureFormed();
        // EN: The preview takes any maintenance hatch, and which one depends on hash order (it changes between runs
        // and with FTB Teams loaded). A plain one is placed with all 6 problems and makes every recipe fail.
        // PT: O preview pega qualquer hatch de manutenção, e qual depende da ordem de hash (muda entre rodadas e com
        // o FTB Teams carregado). Um hatch comum nasce com os 6 problemas e faz toda receita falhar.
        // ZH: 预览会选用任意维护仓，具体哪个取决于哈希顺序（每次运行及加载 FTB Teams 时都会变化）。普通维护仓
        // 放置时带有全部 6 个故障，会使所有配方失败。
        for (var part : controller.getParts())
            if (part instanceof IMaintenanceMachine maintenance) maintenance.setMaintenanceProblems(NO_PROBLEMS);
        return true;
    }

    /** All six maintenance bits set: nothing to fix. */
    static final byte NO_PROBLEMS = 0b111111;
}
