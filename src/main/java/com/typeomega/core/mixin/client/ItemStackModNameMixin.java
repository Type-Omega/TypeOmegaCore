package com.typeomega.core.mixin.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.registries.ForgeRegistries;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.data.TypeOmegaTooltips;
import com.typeomega.core.data.ModNameLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

// EN: Priority above EMI's ItemStackMixin (500), so this runs after EMI appended its mod name line.
// PT: Prioridade acima do ItemStackMixin do EMI (500), para rodar depois que o EMI acrescenta o nome do mod.
// ZH: 优先级高于 EMI 的 ItemStackMixin（500），以便在 EMI 添加模组名称行之后运行。
@Mixin(value = ItemStack.class, priority = 1500)
public abstract class ItemStackModNameMixin {

    @Inject(method = "getTooltipLines", at = @At("RETURN"))
    private void typeomega$keepModNameOnce(Player player, TooltipFlag flag,
                                      CallbackInfoReturnable<List<net.minecraft.network.chat.Component>> info) {
        List<net.minecraft.network.chat.Component> lines = info.getReturnValue();
        var id = ForgeRegistries.ITEMS.getKey(((ItemStack) (Object) this).getItem());
        if (lines == null || id == null || !TypeOmegaMod.MOD_ID.equals(id.getNamespace())) return;
        ModNameLine.keepOnce(lines, TypeOmegaTooltips.MOD_NAME);
    }
}
