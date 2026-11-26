/*
 * The flow follows ExtendedAE Plus (LGPL-3.0, by GaLicn and contributors), PatternEncodingTermMenu mixins and
 * ProviderUploadUtil: after Encode, a processing pattern goes into the provider named for its recipe, and a copy the
 * provider already holds is not added twice. Rewritten against the grid directly; see THIRD_PARTY_LICENSES.md.
 */
package com.typeomega.core.integration.ae.upload;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionHost;
import appeng.crafting.pattern.AEProcessingPattern;
import appeng.helpers.patternprovider.PatternContainer;
import appeng.menu.me.items.PatternEncodingTermMenu;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;

/** Server side: moves a freshly encoded processing pattern from the terminal into the provider of its recipe. */
public final class PatternUploader {

    public enum Outcome {
        UPLOADED,
        /** The pattern is not a processing pattern, so it has no recipe type to follow. */
        NOT_PROCESSING,
        NO_GRID,
        NO_PROVIDER,
        AMBIGUOUS,
        DUPLICATE,
        FULL
    }

    private PatternUploader() {}

    /**
     * @param slot the terminal's encoded pattern slot; emptied when the pattern is uploaded
     */
    public static Outcome upload(ServerPlayer player, PatternEncodingTermMenu menu, Slot slot, RecipeKey key) {
        ItemStack pattern = slot.getItem();
        if (pattern.isEmpty() ||
                !(PatternDetailsHelper.decodePattern(pattern, player.level()) instanceof AEProcessingPattern))
            return Outcome.NOT_PROCESSING;
        IGrid grid = gridOf(menu);
        if (grid == null) return Outcome.NO_GRID;

        var selection = ProviderMatcher.select(providers(grid), p -> p.getTerminalGroup().name().getString(), key);
        Outcome outcome = switch (selection.kind()) {
            case NONE -> Outcome.NO_PROVIDER;
            case AMBIGUOUS -> Outcome.AMBIGUOUS;
            case UNIQUE -> insert(selection.items(), pattern);
        };
        if (outcome == Outcome.UPLOADED) slot.set(ItemStack.EMPTY);
        tell(player, outcome, selection, key);
        return outcome;
    }

    @Nullable
    private static IGrid gridOf(PatternEncodingTermMenu menu) {
        if (menu.getTarget() instanceof IActionHost host) {
            var node = host.getActionableNode();
            if (node != null && node.isActive()) return node.getGrid();
        }
        return null;
    }

    /** The providers the pattern access terminal would list, each once. */
    private static List<PatternContainer> providers(IGrid grid) {
        var seen = Collections.newSetFromMap(new IdentityHashMap<PatternContainer, Boolean>());
        var out = new ArrayList<PatternContainer>();
        for (Class<?> machineClass : grid.getMachineClasses()) {
            if (!PatternContainer.class.isAssignableFrom(machineClass)) continue;
            for (Object machine : grid.getActiveMachines(machineClass.asSubclass(PatternContainer.class))) {
                var container = (PatternContainer) machine;
                if (container.isVisibleInTerminal() && seen.add(container)) out.add(container);
            }
        }
        return out;
    }

    private static Outcome insert(List<PatternContainer> providers, ItemStack pattern) {
        for (PatternContainer provider : providers) {
            var inv = provider.getTerminalPatternInventory();
            for (int i = 0; i < inv.size(); i++)
                if (ItemStack.isSameItemSameTags(inv.getStackInSlot(i), pattern)) return Outcome.DUPLICATE;
        }
        for (PatternContainer provider : providers) {
            if (provider.getTerminalPatternInventory().addItems(pattern.copy()).isEmpty()) return Outcome.UPLOADED;
        }
        return Outcome.FULL;
    }

    private static void tell(ServerPlayer player, Outcome outcome,
                             ProviderMatcher.Selection<PatternContainer> selection,
                             RecipeKey key) {
        Component message = switch (outcome) {
            case UPLOADED -> Component.translatable(UploadLang.DONE, selection.name());
            case DUPLICATE -> Component.translatable(UploadLang.DUPLICATE, selection.name());
            case FULL -> Component.translatable(UploadLang.FULL, key.primaryText());
            case AMBIGUOUS -> Component.translatable(UploadLang.AMBIGUOUS, selection.name(),
                    String.join(", ", selection.names()));
            default -> null;
        };
        if (message != null) player.displayClientMessage(message, true);
    }
}
