package com.typeomega.core.data;

import net.minecraft.world.item.Item;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.typeomega.core.GTIAMod;

public final class GTIAItems {

    /**
     * Wears out slowly in the chlor-alkali Electrolyzer recipe (a chanced input). Placeholder texture until the author
     * draws one.
     */
    public static final ItemEntry<Item> ASBESTOS_DIAPHRAGM = GTIAMod.REGISTRATE.item("asbestos_diaphragm", Item::new)
            .lang("Asbestos Diaphragm")
            .model((context, provider) -> provider.generated(context,
                    GTIAMod.id("item/asbestos_diaphragm")))
            .register();

    /**
     * Consumed by the Hall-Heroult recipe in the Electric Blast Furnace: real anodes burn away into CO2. Placeholder
     * texture.
     */
    public static final ItemEntry<Item> CARBON_ANODE = GTIAMod.REGISTRATE.item("carbon_anode", Item::new)
            .lang("Carbon Anode")
            .model((context, provider) -> provider.generated(context,
                    GTIAMod.id("item/carbon_anode")))
            .register();

    /** Records a bedrock vein for the Entangled Miner (GTIA-D-310). Placeholder texture until the author draws one. */
    public static final ItemEntry<com.typeomega.core.miner.VeinCardItem> VEIN_CARD = GTIAMod.REGISTRATE
            .item("vein_card", com.typeomega.core.miner.VeinCardItem::new)
            .lang("Vein Card")
            .model((context, provider) -> provider.generated(context, GTIAMod.id("item/vein_card")))
            .register();

    /**
     * World Fragments (GTIA-D-354, D-362): a piece of a dimension that lets the Fragment Collectors reach its veins.
     * EN: Vanilla textures stand in until the author draws the fragments.
     * PT: Texturas vanilla ficam no lugar até o autor desenhar os fragmentos.
     * ZH: 在作者绘制碎片贴图之前，暂用原版贴图。
     */
    public static final ItemEntry<net.minecraft.world.item.Item> WORLD_FRAGMENT_OVERWORLD = fragment("overworld",
            "Overworld", "minecraft:block/grass_block_side");
    public static final ItemEntry<net.minecraft.world.item.Item> WORLD_FRAGMENT_NETHER = fragment("nether",
            "Nether", "minecraft:block/netherrack");
    public static final ItemEntry<net.minecraft.world.item.Item> WORLD_FRAGMENT_END = fragment("end", "End",
            "minecraft:block/end_stone");

    private static ItemEntry<net.minecraft.world.item.Item> fragment(String dim, String name, String texture) {
        return GTIAMod.REGISTRATE.item("world_fragment_" + dim, net.minecraft.world.item.Item::new)
                .lang("World Fragment (" + name + ")")
                .model((context, provider) -> provider.generated(context,
                        new net.minecraft.resources.ResourceLocation(texture)))
                .register();
    }

    private GTIAItems() {}

    public static void init() {}
}
