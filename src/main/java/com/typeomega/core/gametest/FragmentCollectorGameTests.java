package com.typeomega.core.gametest;

import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GTBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.data.GTIAItems;
import com.typeomega.core.miner.FragmentCollectorLogic;
import com.typeomega.core.miner.FragmentCollectors;
import com.typeomega.core.miner.VeinCardItem;
import com.typeomega.core.miner.VeinPurity;

/**
 * Fragment Collectors (GTIA-D-362, D-363): both structures form from their preview and break without a gearbox; the
 * logic gives 4 ores per cycle at random, 4 × purity with a card, 16 × at the Large Collector, never uses the fragment.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder("typeomega")
public final class FragmentCollectorGameTests {

    private FragmentCollectorGameTests() {}

    static IMultiController build(GameTestHelper h, MultiblockMachineDefinition def, Block remove) {
        var shape = def.getMatchingShapes().get(0).getBlocks();
        IMultiController controller = null;
        boolean removed = remove == null;
        for (int x = 0; x < shape.length; x++)
            for (int y = 0; y < shape[x].length; y++)
                for (int z = 0; z < shape[x][y].length; z++) {
                    var info = shape[x][y][z];
                    BlockPos pos = new BlockPos(x + 1, y + 1, z + 1);
                    var state = info == null ? Blocks.AIR.defaultBlockState() : info.getBlockState();
                    if (!removed && state.is(remove)) {
                        state = Blocks.AIR.defaultBlockState();
                        removed = true;
                    }
                    h.setBlock(pos, state);
                    if (info != null && info.getBlockState().is(def.getBlock()) &&
                            h.getBlockEntity(pos) instanceof IMachineBlockEntity be &&
                            be.getMetaMachine() instanceof IMultiController c)
                        controller = c;
                }
        return controller;
    }

    @GameTest(template = "qa_empty_24", batch = "fragment_collector", timeoutTicks = 60)
    public static void collectorForms(GameTestHelper h) {
        var c = build(h, FragmentCollectors.FRAGMENT_COLLECTOR, null);
        h.assertTrue(c != null && c.checkPattern(), "the survey tower must form from its preview");
        h.succeed();
    }

    @GameTest(template = "qa_empty_24", batch = "fragment_collector", timeoutTicks = 60)
    public static void collectorNeedsItsGearbox(GameTestHelper h) {
        var c = build(h, FragmentCollectors.FRAGMENT_COLLECTOR, GTBlocks.CASING_STEEL_GEARBOX.get());
        h.assertTrue(c != null && !c.checkPattern(), "a survey tower without a gearbox must not form");
        h.succeed();
    }

    @GameTest(template = "qa_empty_24", batch = "fragment_collector", timeoutTicks = 60)
    public static void largeCollectorForms(GameTestHelper h) {
        var c = build(h, FragmentCollectors.LARGE_FRAGMENT_COLLECTOR, null);
        h.assertTrue(c != null && c.checkPattern(), "the drilling platform must form from its preview");
        h.succeed();
    }

    @GameTest(template = "qa_empty_24", batch = "fragment_collector", timeoutTicks = 60)
    public static void largeCollectorNeedsItsGearbox(GameTestHelper h) {
        var c = build(h, FragmentCollectors.LARGE_FRAGMENT_COLLECTOR, GTBlocks.CASING_TITANIUM_GEARBOX.get());
        h.assertTrue(c != null && !c.checkPattern(), "a drilling platform without a gearbox must not form");
        h.succeed();
    }

    static int ores(GTRecipe recipe) {
        int n = 0;
        for (var content : recipe.getOutputContents(ItemRecipeCapability.CAP)) {
            if (content.chance < content.maxChance) continue;
            for (var stack : ItemRecipeCapability.CAP.of(content.content).getItems()) n += stack.getCount();
        }
        return n;
    }

    @GameTest(template = "qa_empty_5x5", batch = "fragment_collector", timeoutTicks = 20)
    public static void collectorRates(GameTestHelper h) {
        var random = RandomSource.create(7);
        ItemStack fragment = GTIAItems.WORLD_FRAGMENT_OVERWORLD.asStack();
        h.assertTrue(FragmentCollectorLogic.veins(Level.OVERWORLD).size() == 10, "10 overworld veins");
        var rnd = FragmentCollectorLogic.build(new FragmentCollectorLogic.Input(Level.OVERWORLD, fragment, null), 1,
                random);
        h.assertTrue(rnd != null && ores(rnd) == 4 && rnd.duration == 160, "random: 4 ores per 8 s");
        var card = new VeinCardItem.Record(Level.OVERWORLD.location(), 0, 0,
                com.typeomega.core.GTIAMod.id("iron"), VeinPurity.PURE);
        var pure = FragmentCollectorLogic.build(new FragmentCollectorLogic.Input(Level.OVERWORLD, fragment, card), 1,
                random);
        h.assertTrue(pure != null && ores(pure) == 16, "pure card: 16 ores per cycle, got " + ores(pure));
        var large = FragmentCollectorLogic.build(new FragmentCollectorLogic.Input(Level.OVERWORLD, fragment, null),
                16, random);
        h.assertTrue(large != null && ores(large) == 64, "large: 16 × 4 ores");
        h.assertTrue(rnd.getInputContents(ItemRecipeCapability.CAP).isEmpty(), "the fragment is never consumed");
        h.assertTrue(FragmentCollectorLogic.veins(Level.NETHER).size() == 2, "2 nether veins");
        h.succeed();
    }
}
