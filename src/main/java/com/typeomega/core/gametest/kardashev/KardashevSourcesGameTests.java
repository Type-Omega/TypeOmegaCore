package com.typeomega.core.gametest.kardashev;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;
import com.gregtechceu.gtceu.api.recipe.ingredient.EnergyStack;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.machine.multiblock.part.EnergyHatchPartMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.kardashev.KardashevLedger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Kardashev Meter sources (D-443): the energy a GTCEu generator puts in its container counts for its owner, the
 * energy that goes through a Wireless Dynamo Hatch counts once (in the network, never twice), and what the wireless
 * networks hand out counts as spent.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class KardashevSourcesGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";
    private static final long OUTPUT = 512;

    private KardashevSourcesGameTests() {}

    /** Gives {@code container} what a generator recipe would output in one tick. */
    static void output(NotifiableEnergyContainer container, long eu) {
        List<EnergyStack> stacks = new ArrayList<>(List.of(new EnergyStack(eu)));
        container.handleRecipeInner(IO.OUT, null, stacks, false);
    }

    static MetaMachine place(GameTestHelper h, MachineDefinition definition, BlockPos relative, UUID owner) {
        BlockPos pos = h.absolutePos(relative);
        h.getLevel().setBlock(pos, definition.getBlock().defaultBlockState(), 3);
        var machine = MetaMachine.getMachine(h.getLevel(), pos);
        machine.setOwnerUUID(owner);
        return machine;
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 40)
    public static void aDynamoHatchOutputCountsForItsOwner(GameTestHelper h) {
        UUID owner = UUID.randomUUID();
        try {
            var hatch = (EnergyHatchPartMachine) place(h, GTMachines.ENERGY_OUTPUT_HATCH[GTValues.HV],
                    new BlockPos(1, 1, 1), owner);
            output(hatch.energyContainer, OUTPUT);
            h.assertTrue(hatch.energyContainer.getEnergyStored() == OUTPUT, "the hatch took the output");
            h.assertTrue(KardashevLedger.pending(owner) == OUTPUT, "a local generator's EU counts: " +
                    KardashevLedger.pending(owner));
            output(hatch.energyContainer, OUTPUT);
            h.assertTrue(KardashevLedger.pending(owner) == 2 * OUTPUT, "every tick of output adds");
            h.assertTrue(KardashevLedger.pending(owner, false) == 0, "and none of it is spent");
        } finally {
            KardashevLedger.discard(owner);
            h.getLevel().setBlock(h.absolutePos(new BlockPos(1, 1, 1)), Blocks.AIR.defaultBlockState(), 3);
        }
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 40)
    public static void aSingleBlockGeneratorCounts(GameTestHelper h) {
        UUID owner = UUID.randomUUID();
        try {
            MachineDefinition definition = null;
            for (var candidate : GTMachines.STEAM_TURBINE) {
                if (candidate != null) {
                    definition = candidate;
                    break;
                }
            }
            h.assertTrue(definition != null, "GTCEu has a single-block steam turbine");
            var generator = (com.gregtechceu.gtceu.api.machine.SimpleGeneratorMachine) place(h, definition,
                    new BlockPos(2, 1, 1), owner);
            output(generator.energyContainer, 32);
            h.assertTrue(KardashevLedger.pending(owner) == 32, "a single-block generator counts: " +
                    KardashevLedger.pending(owner));
        } finally {
            KardashevLedger.discard(owner);
            h.getLevel().setBlock(h.absolutePos(new BlockPos(2, 1, 1)), Blocks.AIR.defaultBlockState(), 3);
        }
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 40)
    public static void aMachineNobodyOwnsCountsForNobody(GameTestHelper h) {
        UUID owner = UUID.randomUUID();
        try {
            var hatch = (EnergyHatchPartMachine) place(h, GTMachines.ENERGY_OUTPUT_HATCH[GTValues.LV],
                    new BlockPos(3, 1, 1), null);
            output(hatch.energyContainer, 32);
            h.assertTrue(KardashevLedger.pending(owner) == 0, "a machine nobody owns counts for nobody");
        } finally {
            KardashevLedger.discard(owner);
            h.getLevel().setBlock(h.absolutePos(new BlockPos(3, 1, 1)), Blocks.AIR.defaultBlockState(), 3);
        }
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 60)
    public static void aWirelessDynamoHatchIsCountedOnceNotTwice(GameTestHelper h) {
        if (!ModList.get().isLoaded("gtna")) {
            h.succeed();
            return;
        }
        UUID owner = UUID.randomUUID();
        try {
            GtnaHooks.wirelessDynamo(h, owner);
        } finally {
            KardashevLedger.discard(owner);
            h.getLevel().setBlock(h.absolutePos(new BlockPos(1, 1, 3)), Blocks.AIR.defaultBlockState(), 3);
        }
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 60)
    public static void aGeneratorArrayInWirelessModeIsCountedByTheNetworkOnly(GameTestHelper h) {
        if (!ModList.get().isLoaded("gtna")) {
            h.succeed();
            return;
        }
        UUID owner = UUID.randomUUID();
        try {
            GtnaHooks.generatorArray(h, owner);
        } finally {
            KardashevLedger.discard(owner);
            h.getLevel().setBlock(h.absolutePos(new BlockPos(1, 1, 4)), Blocks.AIR.defaultBlockState(), 3);
            h.getLevel().setBlock(h.absolutePos(new BlockPos(3, 1, 4)), Blocks.AIR.defaultBlockState(), 3);
        }
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "kardashev", timeoutTicks = 60)
    public static void wirelessNetworksCountWhatTheyHandOutAsSpent(GameTestHelper h) {
        if (!ModList.get().isLoaded("gtna")) {
            h.succeed();
            return;
        }
        UUID owner = UUID.randomUUID();
        try {
            GtnaHooks.spent(h, owner);
        } finally {
            KardashevLedger.discard(owner);
        }
        h.succeed();
    }

    /** GTNA calls live in their own class so the test class loads without GTNA. */
    private static final class GtnaHooks {

        static void wirelessDynamo(GameTestHelper h, UUID owner) {
            var level = h.getLevel();
            var energy = com.raishxn.gtna.common.data.NexusEnergyNetwork.get(level);
            energy.setMatrixStats(owner, 1, 1, 1.0, new com.typeomega.lib.data.Int128(1L << 40), true);
            energy.setMatrixDimension(owner, level.dimension());
            MachineDefinition definition = null;
            for (var candidate : com.raishxn.gtna.common.data.GTNAEnergyHatches.WIRELESS_DYNAMO_HATCHES[GTValues.HV]) {
                if (candidate != null) {
                    definition = candidate;
                    break;
                }
            }
            h.assertTrue(definition != null, "GTNA has an HV Wireless Dynamo Hatch");
            var hatch = (com.raishxn.gtna.common.machine.multiblock.part.energy.WirelessDynamoHatchPartMachine) place(h,
                    definition, new BlockPos(1, 1, 3), owner);
            hatch.setNetworkOwner(owner);

            output(hatch.energyContainer, OUTPUT);
            h.assertTrue(hatch.energyContainer.getEnergyStored() == OUTPUT, "the hatch took the generator's output");
            h.assertTrue(KardashevLedger.pending(owner) == 0,
                    "negative: the hatch's own buffer is not counted, the network will count it: " +
                            KardashevLedger.pending(owner));

            long before = hatch.energyContainer.getEnergyStored();
            hatch.transferWirelessEnergy();
            long moved = before - hatch.energyContainer.getEnergyStored();
            h.assertTrue(moved > 0, "the hatch moved energy into the network");
            h.assertTrue(
                    KardashevLedger.pending(owner) >= moved && KardashevLedger.pending(owner) <= moved * 1.0001 + 1,
                    "the network counted the energy once: moved " + moved + ", counted " +
                            KardashevLedger.pending(owner));
        }

        static void generatorArray(GameTestHelper h, UUID owner) {
            var array = (com.raishxn.gtna.common.machine.multiblock.energy.GeneratorArrayMachine) place(h,
                    com.raishxn.gtna.common.data.GTNAMachines3.GENERATOR_ARRAY, new BlockPos(1, 1, 4), owner);
            var hatch = (EnergyHatchPartMachine) place(h, GTMachines.ENERGY_OUTPUT_HATCH[GTValues.HV],
                    new BlockPos(3, 1, 4), owner);
            hatch.addedToController(array);
            array.setWirelessMode(true);
            h.assertTrue(com.typeomega.core.kardashev.KardashevLocal.routedToWireless(hatch),
                    "the array's dynamo hatch is routed to the wireless network");
            output(hatch.energyContainer, OUTPUT);
            h.assertTrue(KardashevLedger.pending(owner) == 0, "negative: wireless mode, the network counts it: " +
                    KardashevLedger.pending(owner));
            array.setWirelessMode(false);
            output(hatch.energyContainer, OUTPUT);
            h.assertTrue(KardashevLedger.pending(owner) == OUTPUT, "an array on cables counts as a local generator: " +
                    KardashevLedger.pending(owner));
        }

        static void spent(GameTestHelper h, UUID owner) {
            var level = h.getLevel();
            var energy = com.raishxn.gtna.common.data.NexusEnergyNetwork.get(level);
            energy.setMatrixStats(owner, 1, 1, 1.0, new com.typeomega.lib.data.Int128(1L << 40), true);
            energy.setMatrixDimension(owner, level.dimension());
            energy.setEnergy(owner, new com.typeomega.lib.data.Int128(1_000_000));
            h.assertTrue(energy.consumeEnergy(owner, new com.typeomega.lib.data.Int128(4_000), level),
                    "the network hands out 4000 EU");
            h.assertTrue(KardashevLedger.pending(owner, false) == 4_000, "that is spent: " +
                    KardashevLedger.pending(owner, false));
            h.assertTrue(!energy.consumeEnergy(owner, new com.typeomega.lib.data.Int128(1L << 50), level),
                    "an overdraft is refused");
            h.assertTrue(KardashevLedger.pending(owner, false) == 4_000, "and spends nothing");

            var steam = com.raishxn.gtna.common.data.SteamNetworkData.get(level);
            steam.addSteam(owner, 10_000);
            double generated = KardashevLedger.pending(owner);
            h.assertTrue(steam.consumeSteam(owner, 2_000), "the steam network hands out 2000 mB");
            h.assertTrue(steam.addSteam(owner, -1_000), "and a withdrawal through addSteam");
            h.assertTrue(KardashevLedger.pending(owner, false) == 4_000 + 1_500, "steam spent at 2 mB = 1 EU: " +
                    KardashevLedger.pending(owner, false));
            h.assertTrue(KardashevLedger.pending(owner) == generated, "spending is not generation");
            h.assertTrue(!steam.consumeSteam(owner, 99_999_999L), "a refused withdrawal");
            h.assertTrue(KardashevLedger.pending(owner, false) == 5_500, "spends nothing");
        }
    }
}
