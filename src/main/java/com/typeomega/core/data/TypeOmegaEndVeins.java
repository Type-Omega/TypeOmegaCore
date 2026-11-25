package com.typeomega.core.data;

import com.gregtechceu.gtceu.api.data.worldgen.GTLayerPattern;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTOres;

import net.minecraft.world.level.block.Blocks;

/**
 * End veins without the lunar and Type I shortcuts: the bauxite vein loses its ilmenite (titanium comes from the Moon,
 * D-391, D-420) and the sheldonite vein loses its raw platinum and palladium (the platinum line, D-424).
 * The size, density, weight and height of both veins stay as in GTCEu; only the layers change.
 */
public final class TypeOmegaEndVeins {

    private TypeOmegaEndVeins() {}

    public static void register() {
        // EN: The GTCEu definitions are mutable and re-registered on every datapack reload, so replacing the layer
        // generator here also survives /reload.
        // PT: As definições do GTCEu são mutáveis e registradas de novo a cada reload, então trocar o gerador de
        // camadas aqui também sobrevive ao /reload.
        // ZH: GTCEu 的矿脉定义是可变的，每次数据包重载都会重新注册，所以在此替换分层生成器在 /reload 后依然有效。
        GTOres.BAUXITE_VEIN_END.layeredVeinGenerator(generator -> generator
                .withLayerPattern(() -> GTLayerPattern.builder(GTOres.END_RULES)
                        .layer(l -> l.weight(2).state(Blocks.END_STONE::defaultBlockState).size(1, 6))
                        .layer(l -> l.weight(2).mat(GTMaterials.Bauxite).size(1, 4))
                        .layer(l -> l.weight(1).mat(GTMaterials.Aluminium).size(1, 1))
                        .build()));
        // EN: Cooperite stays: it is the ore the platinum line refines, so the metals still cost the full chain.
        // PT: A cooperita fica: é o minério que a linha da platina refina, então os metais seguem pedindo a cadeia
        // toda.
        // ZH: 保留硫铂矿：它是铂系流程所精炼的矿石，因此这些金属仍需走完整条链。
        GTOres.SHELDONITE_VEIN.layeredVeinGenerator(generator -> generator
                .withLayerPattern(() -> GTLayerPattern.builder(GTOres.END_RULES)
                        .layer(l -> l.weight(3).mat(GTMaterials.Bornite).size(2, 4))
                        .layer(l -> l.weight(2).mat(GTMaterials.Cooperite).size(1, 1))
                        .build()))
                .surfaceIndicatorGenerator(indicator -> indicator.surfaceRock(GTMaterials.Cooperite));
    }
}
