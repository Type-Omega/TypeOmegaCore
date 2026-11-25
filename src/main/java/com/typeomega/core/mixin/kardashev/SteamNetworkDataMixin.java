package com.typeomega.core.mixin.kardashev;

import com.raishxn.gtna.common.data.SteamNetworkData;
import com.typeomega.core.kardashev.KardashevLedger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/** Counts steam that enters the team's GTNA wireless steam network (D-386, D-389). */
@Mixin(value = SteamNetworkData.class, remap = false)
public abstract class SteamNetworkDataMixin {

    // EN: A negative amount is a withdrawal and a false return a rejected add; neither is production.
    // PT: Valor negativo é retirada e retorno falso é adição recusada; nenhum dos dois é produção.
    // ZH: 负值是提取，返回 false 是被拒绝的添加；两者都不算产出。
    @Inject(method = "addSteam", at = @At("RETURN"))
    private void typeomega$countSteam(UUID owner, long amount, CallbackInfoReturnable<Boolean> cir) {
        if (amount > 0 && cir.getReturnValueZ()) KardashevLedger.recordSteam(owner, amount);
    }
}
