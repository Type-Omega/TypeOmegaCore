package com.typeomega.core.mixin;

import net.minecraft.world.item.Item;

import com.typeomega.core.data.rocket.AdAstraNames;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * EN: Gives renamed Ad Astra items the pack's translation key the first time it is built; vanilla caches it after that.
 * A lang file under ad_astra/ would depend on the resource pack order, which Forge does not fix between mods.
 * PT: Dá aos itens renomeados do Ad Astra a chave de tradução do pack na primeira vez que ela é montada; depois o
 * vanilla guarda em cache. Um arquivo de idioma em ad_astra/ dependeria da ordem das packs, que o Forge não fixa entre
 * mods.
 * ZH: 在首次生成时为被重命名的 Ad Astra 物品赋予整合包的翻译键，之后由原版缓存。若在 ad_astra/ 下放语言文件，
 * 结果取决于资源包顺序，而 Forge 不保证模组之间的顺序。
 */
@Mixin(Item.class)
public abstract class ItemDescriptionMixin {

    @Shadow
    private String descriptionId;

    @Inject(method = "getOrCreateDescriptionId", at = @At("HEAD"))
    private void typeomega$renameAdAstraItems(CallbackInfoReturnable<String> cir) {
        if (descriptionId == null) descriptionId = AdAstraNames.descriptionId((Item) (Object) this);
    }
}
