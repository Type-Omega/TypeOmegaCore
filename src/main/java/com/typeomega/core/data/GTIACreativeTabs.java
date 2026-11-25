package com.typeomega.core.data;

import com.gregtechceu.gtceu.api.item.MaterialBlockItem;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.item.PipeBlockItem;
import com.gregtechceu.gtceu.api.item.TagPrefixItem;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
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
 * The Type Omega creative tabs, one per category, split like the GTNA tabs. Each tab lists every core item of its
 * category, read from the item
 * registry, so a new item lands in a tab the day it is added ({@code everyCoreItemIsInATab} checks it).
 */
public final class GTIACreativeTabs {

    /** The categories, in tab order: the same split and order as the GTNA tabs. */
    public enum Category {

        MATERIAL_ITEMS("material_items", "Type Omega: Material Items", "Type Omega: Itens de Material",
                "Type Omega：材料物品", "grid_transponder"),
        MATERIAL_BLOCKS("material_blocks", "Type Omega: Material Blocks", "Type Omega: Blocos de Material",
                "Type Omega：材料方块", ""),
        FLUIDS("fluids", "Type Omega: Fluids", "Type Omega: Fluidos", "Type Omega：流体", ""),
        PIPES("pipes", "Type Omega: Pipes & Wires", "Type Omega: Canos e Fios", "Type Omega：管道与导线", ""),
        ITEMS("items", "Type Omega: Items", "Type Omega: Itens", "Type Omega：物品", "grid_transponder"),
        BLOCKS("blocks", "Type Omega: Blocks", "Type Omega: Blocos", "Type Omega：方块", "neutron_shield_casing"),
        MACHINES("machines", "Type Omega: Machines", "Type Omega: Máquinas", "Type Omega：机器", "matrioshka_brain"),
        WIRELESS("wireless", "Type Omega: Wireless", "Type Omega: Sem Fio", "Type Omega：无线", "");

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
                    // EN: Hidden while empty (the core has no fluids or pipes of its own yet); the creative screen
                    // EN: skips a tab without items.
                    // PT: Fica oculta enquanto vazia (o core ainda não tem fluidos nem canos próprios); a tela do
                    // PT: criativo pula aba sem itens.
                    // ZH: 为空时隐藏（核心模组暂无自己的流体或管道）；创造界面会跳过没有物品的标签页。
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
     * Where a core item belongs, by the GTNA rules: material items, blocks, fluids and pipes by their tag prefix;
     * GregTech machines (single blocks and multiblocks) and blocks with a block entity in Machines; wireless parts
     * in Wireless; other blocks in Blocks; everything else in Items.
     */
    public static Category categoryOf(Item item) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        String path = id == null ? "" : id.getPath();
        if (path.contains("wireless")) return Category.WIRELESS;
        if (item instanceof MetaMachineItem) return Category.MACHINES;
        if (item instanceof PipeBlockItem) return Category.PIPES;
        if (item instanceof BucketItem) return Category.FLUIDS;
        String prefix = prefix(item);
        if (prefix != null) {
            if (prefix.contains("pipe") || prefix.contains("wire") || prefix.contains("cable")) return Category.PIPES;
            if (prefix.contains("cell") || prefix.contains("bucket")) return Category.FLUIDS;
            if (prefix.contains("block") || prefix.contains("ore") || prefix.contains("frame") ||
                    prefix.contains("planks") || prefix.contains("log")) {
                return Category.MATERIAL_BLOCKS;
            }
            return Category.MATERIAL_ITEMS;
        }
        if (item instanceof BlockItem block) {
            return block.getBlock() instanceof EntityBlock ? Category.MACHINES : Category.BLOCKS;
        }
        return Category.ITEMS;
    }

    private static String prefix(Item item) {
        if (item instanceof TagPrefixItem tagged) return tagged.tagPrefix.name();
        if (item instanceof MaterialBlockItem block) return block.tagPrefix.name();
        return null;
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
        Item preferred = category.icon.isEmpty() ? null :
                ForgeRegistries.ITEMS.getValue(new ResourceLocation(GTIAMod.MOD_ID, category.icon));
        if (preferred != null && preferred != Items.AIR) return new ItemStack(preferred);
        List<Item> items = items(category);
        return items.isEmpty() ? new ItemStack(Items.BARRIER) : new ItemStack(items.get(0));
    }
}
