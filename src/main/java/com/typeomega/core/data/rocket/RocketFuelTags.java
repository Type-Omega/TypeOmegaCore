package com.typeomega.core.data.rocket;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagLoader;
import net.minecraftforge.fml.ModList;

import com.typeomega.core.GTIAMod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * EN: Ad Astra's rocket fuel tags, set by the pack (GTIA-D-420, D-424): the tier 1 rocket (and anything reading
 * ad_astra:fuel) burns only GT rocket fuel, the tier 2 rocket only Zip-D. Done after the tag files load: a
 * {@code "replace": true} file in this jar lost to Ad Astra's own in dev, since Forge does not fix the order of mod
 * data inside its single mod pack.
 * PT: As tags de combustível de foguete do Ad Astra, definidas pelo pack (GTIA-D-420, D-424): o foguete T1 (e tudo que
 * lê ad_astra:fuel) queima só o combustível de foguete do GT, o T2 só Zip-D. Feito depois de carregar os arquivos de
 * tag: um arquivo {@code "replace": true} neste jar perdeu para o do Ad Astra em dev, porque o Forge não fixa a ordem
 * dos dados dos mods dentro da pack única de mods.
 * ZH: 由整合包设定的 Ad Astra 火箭燃料标签（GTIA-D-420、D-424）：一级火箭（以及读取 ad_astra:fuel 的一切）只烧
 * GT 火箭燃料，二级火箭只烧 Zip-D。在标签文件加载之后设置：本 jar 中 "replace": true 的文件在开发环境中被 Ad Astra
 * 自身的文件覆盖，因为 Forge 不保证其单一模组资源包内各模组数据的顺序。
 */
public final class RocketFuelTags {

    private static final ResourceLocation ROCKET_FUEL = new ResourceLocation("gtceu", "rocket_fuel");
    private static final Map<ResourceLocation, ResourceLocation> TAGS = Map.of(
            new ResourceLocation("ad_astra", "fuel"), ROCKET_FUEL,
            new ResourceLocation("ad_astra", "tier_1_rocket_fuel"), ROCKET_FUEL,
            new ResourceLocation("ad_astra", "tier_2_rocket_fuel"), GTIAMod.id("zip_d_rocket_fuel"));

    private RocketFuelTags() {}

    public static void apply(Map<ResourceLocation, List<TagLoader.EntryWithSource>> fluidTags) {
        if (!ModList.get().isLoaded("ad_astra")) return;
        TAGS.forEach((tag, fluid) -> fluidTags.put(tag,
                new ArrayList<>(List.of(new TagLoader.EntryWithSource(TagEntry.element(fluid), GTIAMod.MOD_ID)))));
    }
}
