package com.typeomega.core.mixin.kardashev;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.steam.LargeBoilerMachine;

import com.typeomega.core.kardashev.KardashevLedger;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * EN: GTCEu large boilers export steam through normal hatches, outside the wireless network; the meter counts the
 * steam they make. Target: the second write of steamGenerated (the first one only zeroes it below 100 °C).
 * PT: As caldeiras grandes do GTCEu exportam vapor por hatches comuns, fora da rede sem fio; o medidor conta o
 * vapor que elas produzem. Alvo: a segunda escrita de steamGenerated (a primeira só zera abaixo de 100 °C).
 * ZH: GTCEu 大型锅炉通过普通仓输出蒸汽，不经过无线网络；计量器统计其产出的蒸汽。
 * 目标：steamGenerated 的第二次写入（第一次只在低于 100 °C 时归零）。
 */
@Mixin(value = LargeBoilerMachine.class, remap = false)
public abstract class LargeBoilerMachineMixin {

    @Shadow
    private int steamGenerated;

    @Inject(method = "updateCurrentTemperature",
            at = @At(value = "FIELD",
                     target = "Lcom/gregtechceu/gtceu/common/machine/multiblock/steam/LargeBoilerMachine;steamGenerated:I",
                     opcode = Opcodes.PUTFIELD,
                     ordinal = 1,
                     shift = At.Shift.AFTER))
    private void typeomega$countSteam(CallbackInfo ci) {
        if (steamGenerated > 0) {
            KardashevLedger.recordSteam(((MetaMachine) (Object) this).getOwnerUUID(), steamGenerated);
        }
    }
}
