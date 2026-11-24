package com.typeomega.core.data;

import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.typeomega.core.GTIAMod;

import java.util.ArrayList;
import java.util.List;

/**
 * The Type Omega creative tabs, one per category. Each tab lists every core item of its category, read from the item
 * registry, so a new item lands in a tab the day it is added ({@code everyCoreItemIsInATab} checks it).
 */
public final class GTIACreativeTabs {

    /** The categories, in tab order. */
    public enum Category {

        MACHINES("machines", "Type Omega: Machines", "Type Omega: Máquinas", "Type Omega：机器",
                "crafting_station"),
        MULTIBLOCKS("multiblocks", "Type Omega: Multiblocks & Megastructures",
                "Type Omega: Multiblocos e Megaestruturas", "Type Omega：多方块与巨构", "matrioshka_brain"),
        MATERIALS("materials", "Type Omega: Materials & Items", "Type Omega: Materiais e Itens",
                "Type Omega：材料与物品", "grid_transponder"),
        BLOCKS("blocks", "Type Omega: Blocks & Casings", "Type Omega: Blocos e Carcaças", "Type Omega：方块与机壳",
                "neutron_shield_casing");

        public final String id, en, pt, zh;
        /** Preferred icon item; the first item of the tab when it is missing. */
        final String icon;

        Category(String id, String en, String pt, String zh, String icon) {
            this.id = id;
            this.en = en;
            this.pt = pt;
            this.zh = zh;
            this.icon = icon;
        }

        public String langKey() {
            return "itemGroup." + GTIAMod.MOD_ID + "." + id;
        }
    }

    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,
            GTIAMod.MOD_ID);
    public static final List<RegistryObject<CreativeModeTab>> ALL = new ArrayList<>();

    static {
        for (Category category : Category.values()) {
            ALL.add(TABS.register(category.id, () -> CreativeModeTab.builder()
                    .title(Component.translatable(category.langKey()))
                    .icon(() -> icon(category))
                    .displayItems((parameters, output) -> items(category).forEach(output::accept))
                    .build()));
        }
    }

    private GTIACreativeTabs() {}

    public static void register(IEventBus modBus) {
        for (Category category : Category.values()) {
            GTIAMod.REGISTRATE.addRawLang(category.langKey(), category.en);
        }
        TABS.register(modBus);
    }

    /**
     * Where a core item belongs: multiblock controllers; machines (single-block GregTech machines and blocks with a
     * block entity, such as the Crafting Station); other blocks; and everything else.
     */
    public static Category categoryOf(Item item) {
        if (item instanceof MetaMachineItem machine) {
            return machine.getDefinition() instanceof MultiblockMachineDefinition ? Category.MULTIBLOCKS :
                    Category.MACHINES;
        }
        if (item instanceof BlockItem block) {
            return block.getBlock() instanceof EntityBlock ? Category.MACHINES : Category.BLOCKS;
        }
        return Category.MATERIALS;
    }

    /** Every core item of a category, in registry order. */
    public static List<Item> items(Category category) {
        List<Item> out = new ArrayList<>();
        for (var entry : ForgeRegistries.ITEMS.getEntries()) {
            if (!GTIAMod.MOD_ID.equals(entry.getKey().location().getNamespace())) continue;
            Item item = entry.getValue();
            if (item != Items.AIR && categoryOf(item) == category) out.add(item);
        }
        return out;
    }

    private static ItemStack icon(Category category) {
        Item preferred = ForgeRegistries.ITEMS.getValue(new ResourceLocation(GTIAMod.MOD_ID, category.icon));
        if (preferred != null && preferred != Items.AIR) return new ItemStack(preferred);
        List<Item> items = items(category);
        return items.isEmpty() ? new ItemStack(Items.BARRIER) : new ItemStack(items.get(0));
    }
}
