package com.typeomega.core.mega.typezero;

import com.gregtechceu.gtceu.common.data.models.GTModels;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.typeomega.core.GTIAMod;

/**
 * Casings of the Type 0 megastructures (GTIA-D-420, D-421): the Cosmodrome's mission control consoles and the HV
 * Tokamak's MgB2 magnet, neutron shield and lithium breeding blanket. The textures are first drafts derived from
 * GTCEu casings, for the author to redraw.
 */
public final class TypeZeroBlocks {

    public static final BlockEntry<Block> MISSION_CONTROL_CASING = casing("mission_control_casing",
            "Mission Control Casing");
    public static final BlockEntry<Block> TOKAMAK_MAGNET_COIL = casing("tokamak_magnet_coil",
            "MgB2 Tokamak Magnet Coil");
    public static final BlockEntry<Block> NEUTRON_SHIELD_CASING = casing("neutron_shield_casing",
            "Neutron Shield Casing");
    public static final BlockEntry<Block> LITHIUM_BREEDING_BLANKET = casing("lithium_breeding_blanket",
            "Lithium Breeding Blanket");

    private TypeZeroBlocks() {}

    private static BlockEntry<Block> casing(String id, String name) {
        return GTIAMod.REGISTRATE.block(id, Block::new)
                .lang(name)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .addLayer(() -> RenderType::solid)
                .exBlockstate(GTModels.cubeAllModel(GTIAMod.id("block/casings/" + id)))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
    }

    public static void init() {}
}
