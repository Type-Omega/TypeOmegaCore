package com.typeomega.core.mega.typezero;

import net.minecraft.world.item.Item;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.typeomega.core.GTIAMod;

/** Items made by the Type 0 megastructures (GTIA-D-419, D-420). First-draft textures, for the author to redraw. */
public final class TypeZeroItems {

    /** Made only by the National Dispatch Center; every wireless energy hatch needs one (GTIA-D-401, D-419). */
    public static final ItemEntry<Item> GRID_TRANSPONDER = item("grid_transponder", "Grid Transponder");

    private TypeZeroItems() {}

    private static ItemEntry<Item> item(String id, String name) {
        return GTIAMod.REGISTRATE.item(id, Item::new)
                .lang(name)
                .model((context, provider) -> provider.generated(context, GTIAMod.id("item/" + id)))
                .register();
    }

    public static void init() {}
}
