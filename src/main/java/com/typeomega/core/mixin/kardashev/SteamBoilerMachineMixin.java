package com.typeomega.core.mixin.kardashev;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.steam.SteamBoilerMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.typeomega.core.kardashev.KardashevLedger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * EN: Single-block GTCEu boilers have no wireless hatch, but the 0,0 chapter reads their steam on the meter
 * (GTIA-D-389, quest "Primeiro Vapor"). Target: the only steam fill of updateCurrentTemperature.
 * PT: As caldeiras de bloco único do GTCEu não têm hatch sem fio, mas o capítulo 0,0 lê o vapor delas no medidor
 * (GTIA-D-389, quest "Primeiro Vapor"). Alvo: o único enchimento de vapor de updateCurrentTemperature.
 * ZH: GTCEu 单方块锅炉没有无线仓，但 0,0 章节要在计量器上读取其蒸汽（GTIA-D-389，任务“第一缕蒸汽”）。
 * 目标：updateCurrentTemperature 中唯一的蒸汽注入。
 */
@Mixin(value = SteamBoilerMachine.class, remap = false)
public abstract class SteamBoilerMachineMixin {

    @Redirect(method = "updateCurrentTemperature",
              at = @At(value = "INVOKE",
                       target = "Lcom/gregtechceu/gtceu/api/machine/trait/NotifiableFluidTank;fillInternal(Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraftforge/fluids/capability/IFluidHandler$FluidAction;)I"))
    private int typeomega$countSteam(NotifiableFluidTank tank, FluidStack steam, IFluidHandler.FluidAction action) {
        int filled = tank.fillInternal(steam, action);
        if (filled > 0 && action.execute()) {
            KardashevLedger.recordSteam(((MetaMachine) (Object) this).getOwnerUUID(), filled);
        }
        return filled;
    }
}
