package com.typeomega.core.data.rocket;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.BlastProperty;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import com.typeomega.core.TypeOmegaMod;

import java.util.function.Supplier;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;

/**
 * EN: Desh as a GT material with its original name (D-393). With Ad Astra loaded, GT's ingot, nugget, block and
 * plate are Ad Astra's own items, so there is one desh in the game.
 * PT: Desh como material GT com o nome original (D-393). Com o Ad Astra carregado, o lingote, a pepita, o bloco
 * e a placa do GT são os itens do próprio Ad Astra, então só existe um desh no jogo.
 * ZH: 以原名作为 GT 材料的戴什（D-393）。加载 Ad Astra 时，GT 的锭、粒、块和板即为 Ad Astra 自身的物品，
 * 游戏中只有一种戴什。
 */
public final class SpaceMaterials {

    public static Material DESH;

    private SpaceMaterials() {}

    public static void register() {
        // EN: 1700 K stays below GT's hot-ingot threshold (1750 K): desh comes out of an HV EBF ready to press.
        // PT: 1700 K fica abaixo do limite de lingote quente do GT (1750 K): o desh sai do EBF HV pronto para prensar.
        // ZH: 1700 K 低于 GT 热锭阈值（1750 K）：戴什从 HV 电力高炉出炉即可压制。
        DESH = new Material.Builder(TypeOmegaMod.id("desh"))
                .ingot()
                .color(0xD7843E)
                .iconSet(MaterialIconSet.METALLIC)
                .flags(DISABLE_DECOMPOSITION, GENERATE_PLATE, GENERATE_DENSE)
                .blastTemp(1700, BlastProperty.GasTier.LOW, GTValues.VA[GTValues.HV], 600)
                .buildAndRegister();
        if (!ModList.get().isLoaded("ad_astra")) return;
        TagPrefix.ingot.setIgnored(DESH, adAstra("desh_ingot"));
        TagPrefix.nugget.setIgnored(DESH, adAstra("desh_nugget"));
        TagPrefix.block.setIgnored(DESH, adAstra("desh_block"));
        TagPrefix.plate.setIgnored(DESH, adAstra("desh_plate"));
    }

    @SuppressWarnings("unchecked")
    private static Supplier<? extends ItemLike>[] adAstra(String path) {
        Supplier<ItemLike> item = () -> ForgeRegistries.ITEMS.getValue(new ResourceLocation("ad_astra", path));
        return new Supplier[] { item };
    }
}
