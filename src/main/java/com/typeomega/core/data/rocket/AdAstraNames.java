package com.typeomega.core.data.rocket;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import com.typeomega.core.GTIAMod;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * EN: Ad Astra items renamed by the pack: the netherite space suit is the Thermal Space Suit, built from titanium and
 * silica aerogel (GTIA-D-421). The item IDs stay, so worlds and tags keep working.
 * PT: Itens do Ad Astra renomeados pelo pack: o traje espacial de netherite é o Traje Espacial Térmico, feito de
 * titânio e aerogel de sílica (GTIA-D-421). Os IDs continuam, então mundos e tags seguem funcionando.
 * ZH: 整合包重命名的 Ad Astra 物品：下界合金宇航服改为隔热宇航服，由钛与二氧化硅气凝胶制成（GTIA-D-421）。物品 ID
 * 不变，存档和标签照常工作。
 */
public final class AdAstraNames {

    static final Map<String, String> RENAMED = Map.of(
            "netherite_space_helmet", "item.typeomega.thermal_space_helmet",
            "netherite_space_suit", "item.typeomega.thermal_space_suit",
            "netherite_space_pants", "item.typeomega.thermal_space_pants",
            "netherite_space_boots", "item.typeomega.thermal_space_boots");

    private AdAstraNames() {}

    public static void registerLang() {
        for (String piece : new String[] { "helmet", "suit", "pants", "boots" })
            GTIAMod.REGISTRATE.addRawLang("item.typeomega.thermal_space_" + piece,
                    "Thermal Space " + Character.toUpperCase(piece.charAt(0)) + piece.substring(1));
    }

    /** The pack's translation key for an Ad Astra item, or null to keep the item's own. */
    public static @Nullable String descriptionId(Item item) {
        var key = BuiltInRegistries.ITEM.getKey(item);
        return "ad_astra".equals(key.getNamespace()) ? RENAMED.get(key.getPath()) : null;
    }
}
