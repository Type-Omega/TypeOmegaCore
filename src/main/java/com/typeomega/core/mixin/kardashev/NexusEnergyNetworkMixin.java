package com.typeomega.core.mixin.kardashev;

import net.minecraft.server.level.ServerLevel;

import com.raishxn.gtna.common.data.NexusEnergyNetwork;
import com.typeomega.core.kardashev.KardashevLedger;
import com.typeomega.lib.data.Int128;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 * EN: The Kardashev meter reads the team's GTNA wireless energy network (GTIA-D-386). Every EU entering it goes
 * through these two methods, so counting here needs no scan and no change to GTNA.
 * PT: O medidor Kardashev lê a rede sem fio de energia do GTNA do time (GTIA-D-386). Todo EU que entra passa por
 * estes dois métodos; contar aqui dispensa varredura e mudança no GTNA.
 * ZH: Kardashev 计量器读取队伍的 GTNA 无线能量网络（GTIA-D-386）。所有进入网络的 EU 都经过这两个方法，在此计数
 * 无需扫描世界，也无需修改 GTNA。
 */
@Mixin(value = NexusEnergyNetwork.class, remap = false)
public abstract class NexusEnergyNetworkMixin {

    // EN: The gross amount is the EU the source gave up; the loss of the matrix is part of what was generated.
    // PT: O valor bruto é o EU que a fonte entregou; a perda da matriz faz parte do que foi gerado.
    // ZH: 毛值是来源交出的 EU；矩阵损耗也属于已发出的电量。
    @Inject(method = "addEnergy", at = @At("RETURN"))
    private void typeomega$countEnergy(UUID owner, Int128 amount, ServerLevel level,
                                       CallbackInfoReturnable<Int128> cir) {
        Int128 gross = cir.getReturnValue();
        if (gross != null && gross.isPositive()) KardashevLedger.recordEu(owner, gross.doubleValue());
    }

    @Inject(method = "recordLegacySourceLoss", at = @At("HEAD"))
    private void typeomega$countLegacyLoss(UUID owner, Int128 lost, ServerLevel level, CallbackInfo ci) {
        if (lost != null && lost.isPositive()) KardashevLedger.recordEu(owner, lost.doubleValue());
    }
}
