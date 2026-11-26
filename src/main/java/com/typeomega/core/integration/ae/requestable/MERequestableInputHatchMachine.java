package com.typeomega.core.integration.ae.requestable;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.integration.ae2.machine.MEInputHatchPartMachine;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEFluidSlot;

import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.GenericStack;
import org.jetbrains.annotations.Nullable;

/** The fluid twin of the requestable bus: the GTCEu ME Input Hatch that also asks the network to craft the rest. */
public class MERequestableInputHatchMachine extends MEInputHatchPartMachine implements RequestablePart {

    private final CraftingRequests requests = new CraftingRequests(this, this, aeFluidHandler.getConfigurableSlots());

    public MERequestableInputHatchMachine(IMachineBlockEntity holder) {
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
        updateTankSubscription();
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
        return aeFluidHandler.getInventory().length;
    }

    @Override
    public @Nullable GenericStack exceeding(int slot) {
        return aeFluidHandler.getInventory()[slot].exceedStack();
    }

    @Override
    public @Nullable GenericStack missing(int slot) {
        return aeFluidHandler.getInventory()[slot].requestStack();
    }

    @Override
    public void remove(int slot, long amount) {
        ExportOnlyAEFluidSlot target = aeFluidHandler.getInventory()[slot];
        GenericStack stock = target.getStock();
        if (stock == null) return;
        long left = stock.amount() - amount;
        target.setStock(left <= 0 ? null : new GenericStack(stock.what(), left));
    }

    @Override
    public void add(int slot, GenericStack stack) {
        aeFluidHandler.getInventory()[slot].addStack(stack);
    }
}
