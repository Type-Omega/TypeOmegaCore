package com.typeomega.core.mixin.ae;

import net.minecraft.world.item.crafting.Recipe;

import appeng.integration.modules.jeirei.EncodingHelper;
import appeng.menu.me.items.PatternEncodingTermMenu;
import com.typeomega.core.integration.ae.upload.RecipeKeys;
import com.typeomega.core.integration.ae.upload.UploadClientHooks;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * EN: Automatic pattern upload (D-442): AE2's EMI "+" handler is where a recipe becomes a pattern's inputs and outputs,
 * and the only place that still knows which recipe it was; EMI and AE2 offer no event for it. Target:
 * EmiEncodePatternHandler#transferRecipe.
 * PT: Upload automático de padrões (D-442): o handler "+" do AE2 no EMI é onde a receita vira entradas e saídas do
 * padrão, e o único lugar que ainda sabe qual era a receita; EMI e AE2 não têm evento para isso. Alvo:
 * EmiEncodePatternHandler#transferRecipe.
 * ZH: 样板自动上传（D-442）：AE2 在 EMI 中的“+”处理器把配方变成样板的输入输出，也是唯一还知道原配方的位置；
 * EMI 与 AE2 都没有对应事件。目标：EmiEncodePatternHandler#transferRecipe。
 */
@Pseudo
@Mixin(targets = "appeng.integration.modules.emi.EmiEncodePatternHandler", remap = false)
public abstract class EmiEncodePatternHandlerMixin {

    @Inject(method = "transferRecipe(Lappeng/menu/me/items/PatternEncodingTermMenu;Lnet/minecraft/world/item/crafting/Recipe;" +
            "Ldev/emi/emi/api/recipe/EmiRecipe;Z)Lappeng/integration/modules/emi/AbstractRecipeHandler$Result;",
            at = @At("HEAD"),
            require = 0)
    private void typeomega$rememberRecipe(PatternEncodingTermMenu menu, @Nullable Recipe<?> recipe, EmiRecipe emiRecipe,
                                          boolean doTransfer, CallbackInfoReturnable<?> cir) {
        if (!doTransfer) return;
        boolean crafting = EncodingHelper.isSupportedCraftingRecipe(recipe) ||
                emiRecipe.getCategory().equals(VanillaEmiRecipeCategories.CRAFTING);
        // EN: A crafting recipe is not a machine's: drop whatever an earlier transfer left, so it is not sent.
        // PT: Receita de crafting não é de máquina: descarta o que uma transferência anterior deixou, para não ser
        // enviado.
        // ZH: 合成配方不属于机器：丢弃上一次转移遗留的配方键，避免被误发送。
        UploadClientHooks.record(menu.containerId, crafting ? null : RecipeKeys.of(emiRecipe, recipe));
    }
}
