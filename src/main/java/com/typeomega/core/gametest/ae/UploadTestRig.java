package com.typeomega.core.gametest.ae;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.api.util.AEColor;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.helpers.patternprovider.PatternContainer;
import appeng.menu.SlotSemantics;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.parts.encoding.EncodingMode;
import appeng.parts.encoding.PatternEncodingTerminalPart;
import com.typeomega.core.integration.ae.upload.RecipeKey;
import com.typeomega.core.integration.ae.upload.UploadRequestHolder;

/**
 * A small powered network for the automatic upload tests: two cable buses, an encoding terminal on the first, a
 * Creative Energy Cell and up to five pattern providers, named by the test. Providers join the grid a few ticks
 * after they are placed, so the tests wait for {@link #ready} before they encode.
 */
final class UploadTestRig {

    static final BlockPos TERMINAL_BUS = new BlockPos(2, 2, 2);
    static final BlockPos SECOND_BUS = new BlockPos(3, 2, 2);
    /** Where the providers go, in the order the names are given. */
    static final BlockPos[] PROVIDERS = { new BlockPos(2, 2, 1), new BlockPos(2, 2, 3), new BlockPos(3, 2, 1),
            new BlockPos(3, 2, 3), new BlockPos(4, 2, 2) };

    final GameTestHelper h;
    final ServerPlayer player;
    final PatternEncodingTerminalPart terminal;
    final PatternEncodingTermMenu menu;
    final PatternProviderBlockEntity[] providers;

    UploadTestRig(GameTestHelper h, String... providerNames) {
        this.h = h;
        for (BlockPos pos : new BlockPos[] { TERMINAL_BUS, SECOND_BUS }) {
            h.setBlock(pos, AEBlocks.CABLE_BUS.block());
            ((appeng.api.parts.IPartHost) h.getBlockEntity(pos)).addPart(AEParts.GLASS_CABLE.item(AEColor.TRANSPARENT),
                    null, null);
        }
        h.setBlock(new BlockPos(1, 2, 2), AEBlocks.CREATIVE_ENERGY_CELL.block());
        var host = (appeng.api.parts.IPartHost) h.getBlockEntity(TERMINAL_BUS);
        terminal = (PatternEncodingTerminalPart) host.addPart(AEParts.PATTERN_ENCODING_TERMINAL.asItem(), Direction.UP,
                null);
        h.assertTrue(terminal != null, "the cable bus refused the encoding terminal");
        providers = new PatternProviderBlockEntity[providerNames.length];
        for (int i = 0; i < providerNames.length; i++) {
            h.setBlock(PROVIDERS[i], AEBlocks.PATTERN_PROVIDER.block());
            providers[i] = (PatternProviderBlockEntity) h.getBlockEntity(PROVIDERS[i]);
            if (!providerNames[i].isEmpty()) providers[i].setName(providerNames[i]);
        }
        player = AeTestRig.player(h);
        menu = new PatternEncodingTermMenu(1, player.getInventory(), terminal);
    }

    /** Throws until the terminal and every provider are on a powered grid. */
    void ready() {
        h.assertTrue(terminal.getActionableNode() != null && terminal.getActionableNode().isActive(),
                "the encoding terminal is not on an active grid yet");
        for (var provider : providers)
            h.assertTrue(provider.getMainNode().isActive(), "a pattern provider is not on the grid yet");
    }

    /** Sets a processing pattern of one item into one other, and the blank pattern it is written on. */
    void arm(Item in, Item out) {
        var logic = terminal.getLogic();
        logic.setMode(EncodingMode.PROCESSING);
        menu.setMode(EncodingMode.PROCESSING);
        logic.getEncodedInputInv().clear();
        logic.getEncodedOutputInv().clear();
        logic.getEncodedInputInv().setStack(0, new GenericStack(AEItemKey.of(in), 1));
        logic.getEncodedOutputInv().setStack(0, new GenericStack(AEItemKey.of(out), 1));
        if (logic.getBlankPatternInv().getStackInSlot(0).isEmpty())
            logic.getBlankPatternInv().setItemDirect(0, AEItems.BLANK_PATTERN.stack(32));
    }

    /** Presses Encode on the server, with the key the client would have sent (none when null). */
    void encode(RecipeKey key) {
        ((UploadRequestHolder) menu).typeomega$setUploadKey(key);
        menu.encode();
    }

    ItemStack encodedSlot() {
        return menu.getSlots(SlotSemantics.ENCODED_PATTERN).get(0).getItem();
    }

    int blanks() {
        return terminal.getLogic().getBlankPatternInv().getStackInSlot(0).getCount();
    }

    /** How many patterns provider {@code i} holds. */
    int patternsIn(int i) {
        var inv = ((PatternContainer) providers[i]).getTerminalPatternInventory();
        int n = 0;
        for (int slot = 0; slot < inv.size(); slot++) if (!inv.getStackInSlot(slot).isEmpty()) n++;
        return n;
    }

    int capacityOf(int i) {
        return ((PatternContainer) providers[i]).getTerminalPatternInventory().size();
    }
}
