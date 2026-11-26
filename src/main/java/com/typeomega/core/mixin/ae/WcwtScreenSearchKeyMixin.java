package com.typeomega.core.mixin.ae;

import com.typeomega.core.integration.ae.upload.PatternUpload;
import com.typeomega.core.integration.ae.upload.PendingKey;
import com.typeomega.core.integration.ae.upload.RecipeKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * EN: Automatic pattern upload in the Wireless Comprehensive Work Terminal (D-442): its Encode button asks this method
 * for the provider search text of the last recipe, which comes from ExtendedAE Plus when installed and is null
 * otherwise; here it comes from the recipe noted by {@link WcwtRecipeSearchKeyMixin}. The terminal then finds the
 * provider and uploads on its own. Target: WirelessComprehensiveWorkTerminalScreen#consumeEaepProviderSearchKey.
 * PT: Upload automático de padrões no Wireless Comprehensive Work Terminal (D-442): o botão Encode dele pergunta a
 * este método o texto de busca de provider da última receita, que vem do ExtendedAE Plus quando instalado e é nulo
 * sem ele; aqui vem da receita anotada por {@link WcwtRecipeSearchKeyMixin}. O terminal então acha o provider e faz o
 * upload sozinho. Alvo: WirelessComprehensiveWorkTerminalScreen#consumeEaepProviderSearchKey.
 * ZH: Wireless Comprehensive Work Terminal 中的样板自动上传（D-442）：其“编码”按钮向该方法询问最近一次配方的供应器
 * 搜索文本，安装 ExtendedAE Plus 时由它提供，否则为 null；这里改由 {@link WcwtRecipeSearchKeyMixin} 记录的配方提供。
 * 终端随后自行找到供应器并上传。目标：WirelessComprehensiveWorkTerminalScreen#consumeEaepProviderSearchKey。
 */
@Pseudo
@Mixin(targets = "com.lhy.wcwt.client.WirelessComprehensiveWorkTerminalScreen", remap = false)
public abstract class WcwtScreenSearchKeyMixin {

    @Inject(method = "consumeEaepProviderSearchKey", at = @At("HEAD"), cancellable = true, require = 0)
    private void typeomega$searchKey(CallbackInfoReturnable<String> cir) {
        if (!PatternUpload.active()) return;
        RecipeKey key = PendingKey.take(PendingKey.WCWT);
        cir.setReturnValue(key == null ? null : key.primaryText());
    }
}
