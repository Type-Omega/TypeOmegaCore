package com.typeomega.core.integration.ae.interfaces;

import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.util.ConfigInventory;

/**
 * Config or storage row of a tiered interface: the AE2 slot capacities (64 items, 4000 mB) times the tier multiplier.
 */
final class TieredInterfaceInventory extends ConfigInventory {

    private final long multiplier;
    private final boolean config;

    private TieredInterfaceInventory(GenericStackInv.Mode mode, Runnable listener, long multiplier) {
        super(null, mode, InterfaceTier.SLOTS, listener, false);
        this.multiplier = multiplier;
        this.config = mode == GenericStackInv.Mode.CONFIG_STACKS;
    }

    static TieredInterfaceInventory config(Runnable listener, long multiplier) {
        return new TieredInterfaceInventory(GenericStackInv.Mode.CONFIG_STACKS, listener, multiplier);
    }

    static TieredInterfaceInventory storage(Runnable listener, long multiplier) {
        return new TieredInterfaceInventory(GenericStackInv.Mode.STORAGE, listener, multiplier);
    }

    @Override
    public void useRegisteredCapacities() {
        GenericSlotCapacities.getMap()
                .forEach((type, capacity) -> setCapacity(type, saturatedMultiply(capacity, multiplier)));
    }

    @Override
    public long getMaxAmount(AEKey key) {
        long capacity = getCapacity(key.getType());
        // EN: AE2's own interface logic plans in int (InterfaceLogic.usePlan), so a requested amount above int range
        // EN: would be cut to garbage; the config row stops there, the storage row keeps the full capacity.
        // PT: A lógica da interface do AE2 planeja em int (InterfaceLogic.usePlan); um pedido acima do int viraria
        // PT: lixo; a linha de configuração para aí, a de armazenamento mantém a capacidade inteira.
        // ZH: AE2 接口逻辑以 int 规划（InterfaceLogic.usePlan），超出 int 的请求会被截断成乱值；
        // ZH: 配置行在此封顶，存储行保留完整容量。
        if (config) {
            if (key instanceof AEItemKey item)
                capacity = Math.min(capacity, saturatedMultiply(item.getMaxStackSize(), multiplier));
            return Math.min(capacity, Integer.MAX_VALUE);
        }
        return capacity;
    }

    static long saturatedMultiply(long a, long b) {
        long high = Math.multiplyHigh(a, b);
        long low = a * b;
        return high != 0 || low < 0 ? Long.MAX_VALUE : low;
    }
}
