package com.typeomega.core.integration.ae.requestable;

import net.minecraft.world.level.Level;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.Nullable;

/**
 * What the ME Requestable Input Bus and Hatch share: the ME sync that, after pulling what the network has, asks it
 * to craft the rest, and the delivery of the crafted stacks straight into the slots. The part supplies its slots.
 */
public interface RequestablePart extends ICraftingRequester {

    CraftingRequests requests();

    IManagedGridNode getMainNode();

    IActionSource source();

    Level getLevel();

    int slotCount();

    /** What the slot holds beyond its config, to go back to the network. */
    @Nullable
    GenericStack exceeding(int slot);

    /** What the slot lacks to reach its config. */
    @Nullable
    GenericStack missing(int slot);

    void remove(int slot, long amount);

    void add(int slot, GenericStack stack);

    @Override
    default ImmutableSet<ICraftingLink> getRequestedJobs() {
        return requests().jobs();
    }

    @Override
    default void jobStateChange(ICraftingLink link) {
        requests().jobStateChange(link);
    }

    default void syncRequests() {
        IGrid grid = getMainNode().getGrid();
        if (grid == null) return;
        MEStorage network = grid.getStorageService().getInventory();
        ICraftingService crafting = grid.getCraftingService();
        for (int slot = 0; slot < slotCount(); slot++) {
            GenericStack excess = exceeding(slot);
            if (excess != null && excess.amount() > 0) {
                long inserted = network.insert(excess.what(), excess.amount(), Actionable.MODULATE, source());
                if (inserted > 0) {
                    remove(slot, inserted);
                    continue;
                }
            }
            GenericStack wanted = missing(slot);
            if (wanted == null || wanted.amount() <= 0) continue;
            long extracted = network.extract(wanted.what(), wanted.amount(), Actionable.MODULATE, source());
            if (extracted > 0) add(slot, new GenericStack(wanted.what(), extracted));
            long lacking = wanted.amount() - extracted;
            // EN: Only what has a pattern is planned; asking for the rest would redo a doomed plan every sync.
            // PT: Só o que tem padrão é planejado; pedir o resto refaria um plano fadado ao fracasso a cada sync.
            // ZH: 只规划有样板的物品；对其余物品发起请求会在每次同步时重复一个注定失败的规划。
            if (lacking > 0 && crafting.isCraftable(wanted.what())) {
                requests().request(slot, wanted.what(), lacking, getLevel(), crafting, source());
            }
        }
    }

    @Override
    default long insertCraftedItems(ICraftingLink link, AEKey what, long amount, Actionable mode) {
        long remaining = amount;
        for (int slot = 0; slot < slotCount() && remaining > 0; slot++) {
            GenericStack wanted = missing(slot);
            if (wanted == null || !wanted.what().equals(what)) continue;
            long take = Math.min(wanted.amount(), remaining);
            if (take <= 0) continue;
            remaining -= take;
            if (mode == Actionable.MODULATE) add(slot, new GenericStack(what, take));
        }
        return amount - remaining;
    }
}
