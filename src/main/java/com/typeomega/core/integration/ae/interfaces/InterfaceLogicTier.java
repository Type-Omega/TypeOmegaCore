package com.typeomega.core.integration.ae.interfaces;

import appeng.helpers.InterfaceLogic;
import appeng.helpers.InterfaceLogicHost;
import appeng.util.ConfigInventory;
import com.typeomega.core.TypeOmegaMod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** Puts the tier's config and storage rows into an {@link InterfaceLogic}, and tells which tier a host is. */
final class InterfaceLogicTier {

    private static final Field CONFIG;
    private static final Field STORAGE;
    private static final Method ON_CONFIG_CHANGED;
    private static final Method ON_STORAGE_CHANGED;

    static {
        try {
            CONFIG = field("config");
            STORAGE = field("storage");
            ON_CONFIG_CHANGED = method("onConfigRowChanged");
            ON_STORAGE_CHANGED = method("onStorageChanged");
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("AE2 InterfaceLogic changed: the tiered interfaces need a new hook", e);
        }
    }

    private InterfaceLogicTier() {}

    // EN: InterfaceLogic keeps its two rows in final private fields built with 64-item capacity and offers no hook, so
    // EN: they are swapped by reflection right after construction (before any NBT is read). ExtendedAE does the same.
    // PT: A InterfaceLogic guarda as duas linhas em campos privados finais com capacidade de 64 itens e não oferece
    // PT: gancho; elas são trocadas por reflexão logo após a construção (antes de ler NBT). O ExtendedAE faz igual.
    // ZH: InterfaceLogic 把两行库存存放在私有 final 字段中（容量 64 个物品），且没有扩展点；因此在构造后、
    // ZH: 读取 NBT 前通过反射替换。ExtendedAE 也是这么做的。
    static void apply(InterfaceLogic logic, InterfaceTier tier) {
        try {
            ConfigInventory config = TieredInterfaceInventory.config(() -> call(ON_CONFIG_CHANGED, logic),
                    tier.multiplier);
            ConfigInventory storage = TieredInterfaceInventory.storage(() -> call(ON_STORAGE_CHANGED, logic),
                    tier.multiplier);
            CONFIG.set(logic, config);
            STORAGE.set(logic, storage);
            config.useRegisteredCapacities();
            storage.useRegisteredCapacities();
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot install the rows of " + tier, e);
        }
    }

    /**
     * 0 for the plain AE2 interface (block or part) and ExtendedAE's Extended Interface, 1 to 4 for II to V, -1
     * otherwise.
     */
    static int levelOf(Object host) {
        if (host instanceof TieredInterfaceEntity entity) return entity.tier().level;
        if (host instanceof TieredInterfacePart part) return part.tier().level;
        if (!(host instanceof InterfaceLogicHost)) return -1;
        String name = host.getClass().getName();
        return switch (name) {
            case "appeng.blockentity.misc.InterfaceBlockEntity", "appeng.parts.misc.InterfacePart", "com.glodblock.github.extendedae.common.tileentities.TileExInterface", "com.glodblock.github.extendedae.common.parts.PartExInterface" -> 0;
            default -> -1;
        };
    }

    private static void call(Method method, InterfaceLogic logic) {
        try {
            method.invoke(logic);
        } catch (ReflectiveOperationException e) {
            TypeOmegaMod.LOGGER.error("Tiered interface listener failed", e);
        }
    }

    private static Field field(String name) throws ReflectiveOperationException {
        Field field = InterfaceLogic.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    private static Method method(String name) throws ReflectiveOperationException {
        Method method = InterfaceLogic.class.getDeclaredMethod(name);
        method.setAccessible(true);
        return method;
    }
}
