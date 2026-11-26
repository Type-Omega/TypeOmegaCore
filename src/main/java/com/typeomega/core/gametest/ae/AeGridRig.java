package com.typeomega.core.gametest.ae;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;

import appeng.api.config.Actionable;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionSource;
import appeng.api.parts.IPartHost;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.util.AEColor;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.blockentity.storage.DriveBlockEntity;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;

/**
 * A powered AE2 network for the GameTests: a cable bus with a Creative Energy Cell to its west and a Drive holding a
 * 64k cell to its east. {@link #withCrafting} adds a pattern provider (north) and a crafting CPU (above). The south
 * side of the cable is left for the part under test, facing north. AE2 nodes join the grid a few ticks after the
 * blocks are placed, so tests wait for {@link #ready} first.
 */
final class AeGridRig {

    static final BlockPos CABLE = new BlockPos(2, 2, 2);
    static final BlockPos ENERGY = new BlockPos(1, 2, 2);
    static final BlockPos DRIVE = new BlockPos(3, 2, 2);
    static final BlockPos PROVIDER = new BlockPos(2, 2, 1);
    static final BlockPos CPU = new BlockPos(2, 3, 2);
    /** Where the part under test goes: it touches the cable with its front, which faces north by default. */
    static final BlockPos PART = new BlockPos(2, 2, 3);

    final GameTestHelper h;
    private boolean crafting;

    AeGridRig(GameTestHelper h) {
        this.h = h;
        h.setBlock(CABLE, AEBlocks.CABLE_BUS.block());
        ((IPartHost) h.getBlockEntity(CABLE)).addPart(AEParts.GLASS_CABLE.item(AEColor.TRANSPARENT), null, null);
        h.setBlock(ENERGY, AEBlocks.CREATIVE_ENERGY_CELL.block());
        h.setBlock(DRIVE, AEBlocks.DRIVE.block());
        drive().getInternalInventory().setItemDirect(0, AEItems.ITEM_CELL_64K.stack());
    }

    AeGridRig withCrafting() {
        crafting = true;
        h.setBlock(PROVIDER, AEBlocks.PATTERN_PROVIDER.block());
        h.setBlock(CPU, AEBlocks.CRAFTING_STORAGE_1K.block());
        return this;
    }

    DriveBlockEntity drive() {
        return (DriveBlockEntity) h.getBlockEntity(DRIVE);
    }

    IGrid grid() {
        IGrid grid = drive().getMainNode().getGrid();
        h.assertTrue(grid != null, "the drive is not on a grid yet");
        return grid;
    }

    /** Throws until the cell is in the network's storage and, with crafting, the CPU and the provider are on it. */
    void ready() {
        h.assertTrue(drive().getMainNode().isActive(), "the drive is not on a powered grid yet");
        // EN: The probe goes in and out of the network: the 64k cell accepts it only once the drive has mounted it.
        // PT: A sonda entra e sai da rede: a célula de 64k só a aceita depois que o drive a monta.
        // ZH: 探针存入后又取出：驱动器挂载 64k 存储元件后才会接受它。
        AEKey probe = appeng.api.stacks.AEItemKey.of(net.minecraft.world.item.Items.STICK);
        long in = grid().getStorageService().getInventory().insert(probe, 1, Actionable.MODULATE,
                IActionSource.empty());
        h.assertTrue(in == 1, "the cell is not mounted yet");
        grid().getStorageService().getInventory().extract(probe, 1, Actionable.MODULATE, IActionSource.empty());
        if (!crafting) return;
        h.assertTrue(!grid().getCraftingService().getCpus().isEmpty(), "no crafting CPU has formed yet");
        h.assertTrue(((PatternProviderBlockEntity) h.getBlockEntity(PROVIDER)).getMainNode().isActive(),
                "the pattern provider is not on the grid yet");
    }

    /** Whether a crafting CPU is running a job. */
    boolean cpuBusy() {
        return grid().getCraftingService().getCpus().stream().anyMatch(cpu -> cpu.isBusy());
    }

    void stock(AEKey what, long amount) {
        long put = grid().getStorageService().getInventory().insert(what, amount, Actionable.MODULATE,
                IActionSource.empty());
        h.assertTrue(put == amount, "the network took only " + put + " of " + amount + " " + what);
    }

    long stored(AEKey what) {
        return grid().getStorageService().getInventory().getAvailableStacks().get(what);
    }

    /** One processing pattern {@code in -> out} in the provider. */
    void pattern(GenericStack in, GenericStack out) {
        var pattern = PatternDetailsHelper.encodeProcessingPattern(new GenericStack[] { in },
                new GenericStack[] { out });
        ((PatternProviderBlockEntity) h.getBlockEntity(PROVIDER)).getLogic().getPatternInv().addItems(pattern);
    }
}
