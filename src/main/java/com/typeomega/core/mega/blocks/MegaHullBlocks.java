package com.typeomega.core.mega.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GlassBlock;

import com.tterrag.registrate.util.entry.BlockEntry;

/**
 * The exclusive casings of the pilot megastructures (D-445, M2): Yggdrasil Orchid, Nidavellir Hyperforge and Star
 * Lifter. A hull is the structural casing of its mega, an accent carries the light. Hulls and the glass have a
 * connected texture (LDLib {@code _ctm} sheet); accents glow through a {@code _bloom} overlay. Every texture is a draft
 * for the author to redraw, in the palette of the megas v02 study.
 */
public final class MegaHullBlocks {

    // Yggdrasil Orchid: slate frame, blue-violet glass, grooves of white light.
    public static final BlockEntry<Block> ORCHID_CELL_FRAME = GigaBlockFactory.hull("orchid_cell_frame");
    public static final BlockEntry<GlassBlock> ORCHID_CELL_GLASS = GigaBlockFactory.glass("orchid_cell_glass");
    public static final BlockEntry<Block> ORCHID_WINDOW_RIM = GigaBlockFactory.glowing("orchid_window_rim", 8);

    // Nidavellir Hyperforge: cobalt plates with a white thread, a white-cyan rim light on every terrace.
    public static final BlockEntry<Block> FORGE_COBALT_PLATE = GigaBlockFactory.hull("forge_cobalt_plate");
    public static final BlockEntry<Block> TERRACE_RIM_LIGHT = GigaBlockFactory.glowing("terrace_rim_light", 12);

    // Star Lifter: charcoal girdle with heat streaks, an ember at the centre of every hexagonal cell.
    public static final BlockEntry<Block> LIFTER_GIRDLE_HULL = GigaBlockFactory.hull("lifter_girdle_hull");
    public static final BlockEntry<Block> HEATED_HEX_PLATE = GigaBlockFactory.glowing("heated_hex_plate", 10);

    private MegaHullBlocks() {}

    public static void init() {}
}
