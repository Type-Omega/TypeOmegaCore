/*
 * The hook points follow ExtendedAE Plus (LGPL-3.0, by GaLicn and contributors), mixin/ae2/menu: a request the client
 * reports before Encode, and the upload after encode() on the server. See THIRD_PARTY_LICENSES.md.
 */
package com.typeomega.core.mixin.ae;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.menu.slot.RestrictedInputSlot;
import com.typeomega.core.integration.ae.upload.PatternUpload;
import com.typeomega.core.integration.ae.upload.PatternUploader;
import com.typeomega.core.integration.ae.upload.RecipeKey;
import com.typeomega.core.integration.ae.upload.UploadClientHooks;
import com.typeomega.core.integration.ae.upload.UploadRequestHolder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * EN: Automatic pattern upload (D-442). AE2's Encode button calls encode() on the client, which only forwards it, and
 * on the server, which puts the pattern in the output slot; neither has a hook, and the upload has to run right after
 * the server's encode, once the pattern exists. Also carries the recipe key the client sent. Target:
 * PatternEncodingTermMenu#encode.
 * PT: Upload automático de padrões (D-442). O botão Encode do AE2 chama encode() no cliente, que só repassa, e no
 * servidor, que põe o padrão no slot de saída; nenhum dos dois tem gancho, e o upload precisa rodar logo depois do
 * encode do servidor, com o padrão já criado. Também guarda a chave de receita enviada pelo cliente. Alvo:
 * PatternEncodingTermMenu#encode.
 * ZH: 样板自动上传（D-442）。AE2 的“编码”按钮在客户端调用 encode()（仅转发），在服务端调用时把样板放进输出槽；
 * 两处都没有扩展点，而上传必须在服务端 encode 之后、样板生成时立即执行。同时保存客户端发来的配方键。
 * 目标：PatternEncodingTermMenu#encode。
 */
@Pseudo
@Mixin(value = PatternEncodingTermMenu.class, remap = false)
public abstract class PatternEncodingTermMenuMixin implements UploadRequestHolder {

    @Shadow
    @Final
    private RestrictedInputSlot encodedPatternSlot;

    @Unique
    private RecipeKey typeomega$uploadKey;
    // EN: The stack in the output slot before encode(): a different one afterwards means a pattern was really encoded.
    // PT: A pilha no slot de saída antes do encode(): outra depois significa que um padrão foi mesmo codificado.
    // ZH: encode() 之前输出槽中的物品栈：之后若不同，说明确实编码出了新样板。
    @Unique
    private ItemStack typeomega$before = ItemStack.EMPTY;

    @Unique
    @Override
    public void typeomega$setUploadKey(@Nullable RecipeKey key) {
        typeomega$uploadKey = key;
    }

    @Unique
    @Override
    @Nullable
    public RecipeKey typeomega$takeUploadKey() {
        RecipeKey key = typeomega$uploadKey;
        typeomega$uploadKey = null;
        return key;
    }

    @Inject(method = "encode", at = @At("HEAD"))
    private void typeomega$beforeEncode(CallbackInfo ci) {
        var menu = (PatternEncodingTermMenu) (Object) this;
        if (!PatternUpload.active()) return;
        if (menu.isClientSide()) UploadClientHooks.onEncode(menu);
        else typeomega$before = encodedPatternSlot.getItem();
    }

    @Inject(method = "encode", at = @At("RETURN"))
    private void typeomega$afterEncode(CallbackInfo ci) {
        var menu = (PatternEncodingTermMenu) (Object) this;
        if (menu.isClientSide() || !PatternUpload.active()) return;
        RecipeKey key = typeomega$takeUploadKey();
        ItemStack before = typeomega$before;
        typeomega$before = ItemStack.EMPTY;
        if (key == null || encodedPatternSlot.getItem() == before) return;
        if (menu.getPlayer() instanceof ServerPlayer player)
            PatternUploader.upload(player, menu, encodedPatternSlot, key);
    }
}
