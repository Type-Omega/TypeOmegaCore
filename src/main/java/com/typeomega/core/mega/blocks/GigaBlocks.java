package com.typeomega.core.mega.blocks;

import net.minecraft.world.level.block.Block;

import com.tterrag.registrate.util.entry.BlockEntry;

/**
 * The blocks the megastructures share (D-445, M2): the controller mount's deck, its rail and its lamp. The textures
 * are drafts for the author to redraw (palette of the megas v02 study).
 */
public final class GigaBlocks {

    /** The floor of the controller mount: a 7 x 5 deck the player stands on. */
    public static final BlockEntry<Block> SERVICE_DECK = GigaBlockFactory.hull("giga_service_deck");
    /** The rail of the mount and the thorns of the rings: a charcoal lattice. */
    public static final BlockEntry<Block> TRUSS = GigaBlockFactory.lattice("giga_truss");
    /** A point of mint light on charcoal. */
    public static final BlockEntry<Block> LAMP = GigaBlockFactory.glowing("giga_lamp", 15);

    private GigaBlocks() {}

    public static void init() {}
}
