package com.typeomega.core.mega.typezero;

import net.minecraft.world.item.Item;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.typeomega.core.TypeOmegaMod;

/** Items made by the Type 0 megastructures (D-419, D-420). First-draft textures, for the author to redraw. */
public final class TypeZeroItems {

    /** Made only by the National Dispatch Center; every wireless energy hatch needs one (D-401, D-419). */
    public static final ItemEntry<Item> GRID_TRANSPONDER = item("grid_transponder", "Grid Transponder");
    /** Launched by the Cosmodrome to map the Moon (D-420). */
    public static final ItemEntry<Item> LUNAR_PROBE = item("lunar_probe", "Lunar Probe");
    /** The probe's map: carried by a player, it lets the rockets land on the Moon (D-420). */
    public static final ItemEntry<Item> LUNAR_SURVEY_DATA = item("lunar_survey_data", "Lunar Survey Data");

    private TypeZeroItems() {}

    private static ItemEntry<Item> item(String id, String name) {
        return TypeOmegaMod.REGISTRATE.item(id, Item::new)
                .lang(name)
                .model((context, provider) -> provider.generated(context, TypeOmegaMod.id("item/" + id)))
                .register();
    }

    public static void init() {}
}
