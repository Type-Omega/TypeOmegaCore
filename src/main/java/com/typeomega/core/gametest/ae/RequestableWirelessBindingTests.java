package com.typeomega.core.gametest.ae;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

import appeng.api.networking.security.IActionHost;
import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.gametest.TestPlayers;
import com.typeomega.core.integration.ae.requestable.RequestablePart;
import com.typeomega.core.integration.ae.requestable.RequestableParts;

/**
 * The ME Requestable Input Bus is an AE2 grid node, so GTNA's Wireless ME Network Binding Tool binds it to a Wireless
 * ME Network Core (D-444). GTNA is only a test dependency of the core: the block and the item are looked up in the
 * registry, and the test passes without checking anything when the GTNA build in use has no wireless network.
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class RequestableWirelessBindingTests {

    private static final BlockPos FAR = new BlockPos(4, 2, 4);

    private RequestableWirelessBindingTests() {}

    @GameTest(template = "qa_empty_5x5", timeoutTicks = 400)
    public static void wirelessRequestablePartCanBeBoundWithTheGtnaTool(GameTestHelper h) {
        var coreBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("gtna", "wireless_network_core"));
        var tool = ForgeRegistries.ITEMS.getValue(new ResourceLocation("gtna", "wireless_network_binding_tool"));
        if (coreBlock == null || coreBlock == Blocks.AIR || tool == null || tool == Items.AIR) {
            h.succeed();
            return;
        }
        var rig = new AeGridRig(h);
        h.setBlock(AeGridRig.PART, coreBlock);
        h.setBlock(FAR, RequestableParts.bus.getBlock());
        var player = TestPlayers.survival(h);
        var stack = new ItemStack(tool);
        coreBlock.setPlacedBy(h.getLevel(), h.absolutePos(AeGridRig.PART), h.getBlockState(AeGridRig.PART), player,
                new ItemStack(coreBlock));
        var part = (RequestablePart) ((MetaMachineBlockEntity) h.getBlockEntity(FAR)).getMetaMachine();
        h.startSequence()
                .thenWaitUntil(() -> {
                    rig.ready();
                    var coreNode = ((IActionHost) h.getBlockEntity(AeGridRig.PART)).getActionableNode();
                    h.assertTrue(coreNode != null && coreNode.getGrid() == rig.grid(),
                            "the core is not on the grid yet");
                    h.assertTrue(part.getMainNode().getNode() != null, "the part has no node yet");
                })
                .thenExecute(() -> {
                    h.assertTrue(part.getMainNode().getGrid() != rig.grid(), "an unbound part is already in the grid");
                    click(h, player, stack, AeGridRig.PART, true);
                    click(h, player, stack, FAR, false);
                })
                .thenWaitUntil(() -> {
                    h.assertTrue(part.getMainNode().getGrid() == rig.grid(),
                            "the tool did not bind the requestable part to the network");
                    h.assertTrue(part.getMainNode().isOnline(), "the bound requestable part has no channel");
                })
                .thenSucceed();
    }

    private static void click(GameTestHelper h, net.minecraft.server.level.ServerPlayer player, ItemStack stack,
                              BlockPos at, boolean sneaking) {
        player.setShiftKeyDown(sneaking);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockPos abs = h.absolutePos(at);
        var hit = new BlockHitResult(new Vec3(abs.getX() + 0.5, abs.getY() + 0.5, abs.getZ() + 0.5), Direction.UP, abs,
                false);
        stack.getItem().onItemUseFirst(stack, new UseOnContext(h.getLevel(), player, InteractionHand.MAIN_HAND, stack,
                hit));
    }
}
