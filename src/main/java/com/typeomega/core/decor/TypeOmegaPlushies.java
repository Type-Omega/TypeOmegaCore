package com.typeomega.core.decor;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.typeomega.core.TypeOmegaMod;

/** Registration of the decorative plushies (the creative tab picks them up as plain blocks). */
public final class TypeOmegaPlushies {

    public static final BlockEntry<PlushieBlock> RAISHXN = plush("plush_raishxn", "Raishxn Plush");
    public static final BlockEntry<PlushieBlock> NYRA = plush("plush_nyra", "Nyra Plush");

    private TypeOmegaPlushies() {}

    private static BlockEntry<PlushieBlock> plush(String id, String name) {
        return TypeOmegaMod.REGISTRATE.block(id, PlushieBlock::new)
                .initialProperties(() -> Blocks.WHITE_WOOL)
                .properties(p -> p.mapColor(MapColor.NONE).strength(0.5F).noOcclusion())
                .lang(name)
                // Blockstate, models and recipe are hand-written resources (cut from the skins, design/arte).
                .blockstate((ctx, prov) -> {})
                .loot((tables, block) -> tables.dropSelf(block))
                .item()
                .model((ctx, prov) -> {})
                .build()
                .register();
    }

    public static void init() {}
}
