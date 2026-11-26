package com.typeomega.core.mega.blocks;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.common.data.models.GTModels;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.mega.common.MegaLang;

/**
 * Builders of the Giga blocks (D-445): the casings of the megastructures. Each kind has its model and render layer;
 * textures live under {@code textures/block/casings/<id>.png}, and a connected variant adds the 32 x 32 {@code _ctm}
 * sheet named in the texture's .mcmeta (LDLib, like the Rocket Assembly Casing).
 *
 * <p>
 * The English name comes from {@code mega_lang/giga_blocks.json}, which also carries pt_br and zh_cn.
 */
final class GigaBlockFactory {

    private GigaBlockFactory() {}

    private static ResourceLocation texture(String id) {
        return TypeOmegaMod.id("block/casings/" + id);
    }

    private static String name(String id) {
        return MegaLang.english("block.typeomega." + id);
    }

    private static BlockBehaviour.Properties common(BlockBehaviour.Properties p) {
        return p.isValidSpawn((state, level, pos, ent) -> false);
    }

    /** A solid hull: a cube with one texture (connected when its .mcmeta says so). */
    static BlockEntry<Block> hull(String id) {
        return TypeOmegaMod.REGISTRATE.block(id, Block::new)
                .lang(name(id))
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(GigaBlockFactory::common)
                .addLayer(() -> RenderType::solid)
                .exBlockstate(GTModels.cubeAllModel(texture(id)))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
    }

    /** A glass hull: translucent, and the faces between two of them are not drawn. */
    static BlockEntry<GlassBlock> glass(String id) {
        return TypeOmegaMod.REGISTRATE.block(id, GlassBlock::new)
                .lang(name(id))
                .initialProperties(() -> Blocks.GLASS)
                .properties(GigaBlockFactory::common)
                .addLayer(() -> RenderType::translucent)
                .exBlockstate(GTModels.cubeAllModel(texture(id)))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
    }

    /** A lattice: a cube whose texture has holes, so the blocks behind show through. */
    static BlockEntry<Block> lattice(String id) {
        return TypeOmegaMod.REGISTRATE.block(id, Block::new)
                .lang(name(id))
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> common(p).noOcclusion())
                .addLayer(() -> RenderType::cutoutMipped)
                .exBlockstate(GTModels.cubeAllModel(texture(id)))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
    }

    /**
     * An emissive block: the base texture plus the {@code _bloom} overlay that glows (GTCEu's two-layer cube, as in
     * GTNA's bloom casings), and the light the block gives off.
     */
    static BlockEntry<Block> glowing(String id, int light) {
        ResourceLocation base = texture(id), bloom = TypeOmegaMod.id("block/casings/" + id + "_bloom");
        return TypeOmegaMod.REGISTRATE.block(id, Block::new)
                .lang(name(id))
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> common(p).lightLevel(state -> light))
                .addLayer(() -> RenderType::cutoutMipped)
                .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models()
                        .withExistingParent(ctx.getName(), GTCEu.id("block/cube_2_layer/all"))
                        .texture("bot_all", base).texture("top_all", bloom)))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .model((ctx, prov) -> prov.withExistingParent(ctx.getName(), prov.modLoc("block/" + ctx.getName())))
                .build()
                .register();
    }
}
