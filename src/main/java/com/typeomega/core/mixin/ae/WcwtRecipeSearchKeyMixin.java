package com.typeomega.core.mixin.ae;

import net.minecraft.world.item.crafting.Recipe;

import appeng.parts.encoding.EncodingMode;
import com.typeomega.core.integration.ae.upload.PatternUpload;
import com.typeomega.core.integration.ae.upload.PendingKey;
import com.typeomega.core.integration.ae.upload.RecipeKeys;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * EN: Automatic pattern upload in the Wireless Comprehensive Work Terminal (D-442). WCWT matches an encoded pattern to
 * a provider by a search text, and gets that text from ExtendedAE Plus when it is installed; this method is where it
 * asks, so the recipe is noted here when that mod is not. WCWT has no event or API for it. Target:
 * WcwtRecipeSearchKeyResolver#updateEaepProviderSearchKey.
 * PT: Upload automático de padrões no Wireless Comprehensive Work Terminal (D-442). O WCWT liga um padrão codificado a
 * um provider por um texto de busca e pega esse texto do ExtendedAE Plus quando ele está instalado; este método é onde
 * ele pergunta, então a receita é anotada aqui quando esse mod não existe. O WCWT não tem evento nem API para isso.
 * Alvo: WcwtRecipeSearchKeyResolver#updateEaepProviderSearchKey.
 * ZH: Wireless Comprehensive Work Terminal 中的样板自动上传（D-442）。WCWT 用搜索文本把编码好的样板匹配到供应器，
 * 安装 ExtendedAE Plus 时从它取得该文本；该方法正是询问之处，所以没有该模组时在此记录配方。WCWT 没有为此提供事件或
 * API。目标：WcwtRecipeSearchKeyResolver#updateEaepProviderSearchKey。
 */
@Pseudo
@Mixin(targets = "com.lhy.wcwt.compat.WcwtRecipeSearchKeyResolver", remap = false)
public abstract class WcwtRecipeSearchKeyMixin {

    @Inject(method = "updateEaepProviderSearchKey", at = @At("HEAD"), require = 0)
    private static void typeomega$rememberRecipe(Object recipeBase, @Nullable Recipe<?> recipe, EncodingMode mode,
                                                 CallbackInfo ci) {
        if (!PatternUpload.active()) return;
        // EN: Crafting, smithing and stonecutting are not a machine's recipes: nothing to send, and no old key left.
        // PT: Crafting, smithing e stonecutting não são receitas de máquina: nada a enviar e nenhuma chave antiga
        // sobra.
        // ZH: 合成、锻造台与切石机不是机器配方：无需发送，也不保留旧的配方键。
        PendingKey.set(PendingKey.WCWT, mode == EncodingMode.PROCESSING ? RecipeKeys.of(recipeBase, recipe) : null);
    }
}
