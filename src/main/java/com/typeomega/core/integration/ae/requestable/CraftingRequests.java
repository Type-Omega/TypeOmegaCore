package com.typeomega.core.integration.ae.requestable;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.helpers.MultiCraftingTracker;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.NotNull;

/**
 * The crafting jobs a requestable part has asked of the network, one per config slot, kept in AE2's own tracker and
 * written to the machine's tag so a job survives a chunk unload or a world reload.
 */
public final class CraftingRequests extends MachineTrait {

    private static final ManagedFieldHolder FIELDS = new ManagedFieldHolder(CraftingRequests.class);
    private static final String TAG = "TypeOmegaCraftingRequests";

    private final MultiCraftingTracker tracker;

    public CraftingRequests(MetaMachine machine, ICraftingRequester owner, int slots) {
        super(machine);
        this.tracker = new MultiCraftingTracker(owner, slots);
    }

    /**
     * Plans and submits a craft of {@code amount} of {@code what} for a slot; does nothing while the slot has a job.
     */
    public void request(int slot, AEKey what, long amount, Level level, ICraftingService crafting,
                        IActionSource source) {
        tracker.handleCrafting(slot, what, amount, level, crafting, source);
    }

    public ImmutableSet<ICraftingLink> jobs() {
        return tracker.getRequestedJobs();
    }

    public void jobStateChange(ICraftingLink link) {
        tracker.jobStateChange(link);
    }

    public void cancelAll() {
        for (ICraftingLink link : jobs()) link.cancel();
    }

    // EN: A dropped machine does not carry its jobs: the links belong to the grid it left.
    // PT: Uma máquina que vira item não leva os trabalhos: os links pertencem à rede de onde ela saiu.
    // ZH: 变成物品掉落的机器不携带任务：这些链接属于它离开的网络。
    @Override
    public void saveCustomPersistedData(@NotNull CompoundTag tag, boolean forDrop) {
        if (forDrop) return;
        CompoundTag links = new CompoundTag();
        tracker.writeToNBT(links);
        if (!links.isEmpty()) tag.put(TAG, links);
    }

    @Override
    public void loadCustomPersistedData(@NotNull CompoundTag tag) {
        if (tag.contains(TAG)) tracker.readFromNBT(tag.getCompound(TAG));
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return FIELDS;
    }
}
