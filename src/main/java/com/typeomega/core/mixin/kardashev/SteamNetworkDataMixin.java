package com.typeomega.core.mixin.kardashev;

import com.raishxn.gtna.common.data.SteamNetworkData;
import com.typeomega.core.kardashev.KardashevLedger;
import com.typeomega.core.kardashev.KardashevSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/** Counts steam that enters (generated) and leaves (spent) the team's GTNA wireless steam network (D-386, D-443). */
@Mixin(value = SteamNetworkData.class, remap = false)
public abstract class SteamNetworkDataMixin {

    // EN: A negative amount is a withdrawal (spent, D-443) and a false return a rejected add; neither is production.
    // PT: Valor negativo é retirada (gasto, D-443) e retorno falso é adição recusada; nenhum dos dois é produção.
    // ZH: 负值是提取（计入消耗，D-443），返回 false 是被拒绝的添加；两者都不算产出。
    @Inject(method = "addSteam", at = @At("RETURN"))
    private void typeomega$countSteam(UUID owner, long amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) return;
        if (amount > 0) KardashevLedger.recordSteam(owner, amount, KardashevSource.WIRELESS_STEAM);
        else if (amount < 0) KardashevLedger.recordSteamSpent(owner, -amount);
    }

    // EN: D-443: consumeSteam is the other way the network hands steam out; a rejected withdrawal spends nothing.
    // PT: D-443: consumeSteam é a outra forma de a rede entregar vapor; retirada recusada não gasta nada.
    // ZH: D-443：consumeSteam 是网络发放蒸汽的另一条路径；被拒绝的提取不计消耗。
    @Inject(method = "consumeSteam", at = @At("RETURN"))
    private void typeomega$countSpentSteam(UUID owner, long amount, CallbackInfoReturnable<Boolean> cir) {
        if (amount > 0 && cir.getReturnValueZ()) KardashevLedger.recordSteamSpent(owner, amount);
    }
}
