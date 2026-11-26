package com.typeomega.core.mixin.ae;

import appeng.menu.me.items.PatternEncodingTermMenu;
import com.typeomega.core.integration.ae.upload.RecipeKeys;
import com.typeomega.core.integration.ae.upload.UploadClientHooks;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * EN: Automatic pattern upload (D-442): GregTech has its own EMI "+" handler for AE2's pattern terminal and takes
 * every GregTech recipe before AE2's handler sees it, so the recipe has to be noted here too. Target:
 * Ae2PatternTerminalHandler#craft.
 * PT: Upload automático de padrões (D-442): o GregTech tem o próprio handler "+" do EMI para o terminal de padrões do
 * AE2 e fica com toda receita do GregTech antes do handler do AE2, então a receita precisa ser anotada aqui também.
 * Alvo: Ae2PatternTerminalHandler#craft.
 * ZH: 样板自动上传（D-442）：GregTech 为 AE2 样板终端自带 EMI “+”处理器，所有 GregTech 配方都先由它接管，
 * 之后才轮到 AE2 的处理器，所以这里也必须记录配方。目标：Ae2PatternTerminalHandler#craft。
 */
@Pseudo
@Mixin(targets = "com.gregtechceu.gtceu.integration.emi.recipe.Ae2PatternTerminalHandler", remap = false)
public abstract class GtPatternTerminalHandlerMixin {

    @Inject(method = "craft", at = @At("HEAD"), require = 0)
    private void typeomega$rememberRecipe(EmiRecipe recipe, EmiCraftContext<?> context,
                                          CallbackInfoReturnable<Boolean> cir) {
        if (context.getScreenHandler() instanceof PatternEncodingTermMenu menu)
            UploadClientHooks.record(menu.containerId, RecipeKeys.of(recipe, recipe.getBackingRecipe()));
    }
}
