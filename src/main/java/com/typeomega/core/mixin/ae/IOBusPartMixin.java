package com.typeomega.core.mixin.ae;

import appeng.api.upgrades.IUpgradeableObject;
import appeng.parts.automation.IOBusPart;
import com.typeomega.core.integration.ae.cards.AccelerationRates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * EN: Acceleration Cards (II) to (V) on the AE2 and ExtendedAE import/export buses (GTIA-D-442). AE2 turns the card
 * count into operations with a switch inside this method and offers no hook; ExtendedAE's buses call it through super.
 * Untouched when no tiered card is installed. Target: IOBusPart#getOperationsPerTick.
 * PT: Cartões de Aceleração (II) a (V) nas buses de importação/exportação do AE2 e do ExtendedAE (GTIA-D-442). O AE2
 * converte a quantidade de cartões em operações com um switch dentro deste método e não oferece gancho; as buses do
 * ExtendedAE o chamam via super. Intacto quando não há cartão escalonado. Alvo: IOBusPart#getOperationsPerTick.
 * ZH: 加速卡 (II) 至 (V) 作用于 AE2 与 ExtendedAE 的输入/输出总线（GTIA-D-442）。AE2 在该方法内用 switch 把卡数换算成
 * 操作数且没有扩展点；ExtendedAE 的总线通过 super 调用它。未安装分级卡时保持原样。目标：IOBusPart#getOperationsPerTick。
 */
@Pseudo
@Mixin(value = IOBusPart.class, remap = false)
public abstract class IOBusPartMixin {

    @Inject(method = "getOperationsPerTick", at = @At("HEAD"), cancellable = true)
    private void typeomega$tieredCards(CallbackInfoReturnable<Integer> cir) {
        var upgrades = ((IUpgradeableObject) this).getUpgrades();
        if (AccelerationRates.hasTieredCards(upgrades))
            cir.setReturnValue(AccelerationRates.busOperationsPerTick(upgrades));
    }
}
