package com.typeomega.core.integration.ae.requestable;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.integration.ae2.machine.MEInputBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEItemSlot;

import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.GenericStack;
import org.jetbrains.annotations.Nullable;

/**
 * The GTCEu ME Input Bus plus crafting requests: besides pulling its configured stacks from the network, it asks the
 * network to craft what the network lacks, and receives the crafted items directly.
 */
public class MERequestableInputBusMachine extends MEInputBusPartMachine implements RequestablePart {

    private final CraftingRequests requests = new CraftingRequests(this, this, CONFIG_SIZE);

    public MERequestableInputBusMachine(IMachineBlockEntity holder) {
        super(holder);
        getMainNode().addService(ICraftingRequester.class, this);
    }

    @Override
    protected void syncME() {
        syncRequests();
    }

    @Override
    public void jobStateChange(ICraftingLink link) {
        RequestablePart.super.jobStateChange(link);
        updateInventorySubscription();
    }

    @Override
    public void onMachineRemoved() {
        super.onMachineRemoved();
        requests.cancelAll();
    }

    @Override
    public CraftingRequests requests() {
        return requests;
    }

    @Override
    public IActionSource source() {
        return actionSource;
    }

    @Override
    public int slotCount() {
        return aeItemHandler.getInventory().length;
    }

    @Override
    public @Nullable GenericStack exceeding(int slot) {
        return aeItemHandler.getInventory()[slot].exceedStack();
    }

    @Override
    public @Nullable GenericStack missing(int slot) {
        return aeItemHandler.getInventory()[slot].requestStack();
    }

    @Override
    public void remove(int slot, long amount) {
        ExportOnlyAEItemSlot target = aeItemHandler.getInventory()[slot];
        GenericStack stock = target.getStock();
        if (stock == null) return;
        long left = stock.amount() - amount;
        target.setStock(left <= 0 ? null : new GenericStack(stock.what(), left));
    }

    @Override
    public void add(int slot, GenericStack stack) {
        aeItemHandler.getInventory()[slot].addStack(stack);
    }
}
