package com.typeomega.core.mixin.kardashev;

import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;

import com.typeomega.core.kardashev.KardashevLocal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * EN: D-443: a GTCEu generator (single block, large turbine or engine, the Tokamak) has no event for its output; the
 * one place every recipe's EU output passes is this container, so the meter reads it there instead of watching each
 * machine. handleRecipeInner changes the stored energy by a negative amount for inputs and a positive one for
 * outputs, so a positive change is generation. Target: the changeEnergy call of handleRecipeInner.
 * PT: D-443: um gerador do GTCEu (bloco único, turbina ou motor grande, o Tokamak) não tem evento de saída; o único
 * ponto por onde passa o EU de saída de toda receita é este container, então o medidor lê ali em vez de vigiar cada
 * máquina. O handleRecipeInner muda a energia guardada com valor negativo para entradas e positivo para saídas, então
 * mudança positiva é geração. Alvo: a chamada a changeEnergy do handleRecipeInner.
 * ZH: D-443：GTCEu 发电机（单方块、大型涡轮或引擎、托卡马克）没有输出事件；每个配方的 EU 输出都会经过该容器，
 * 因此计量器在此读取，而不是监视每台机器。handleRecipeInner 对输入以负值、对输出以正值修改储能，所以正值变化
 * 即发电。目标：handleRecipeInner 中对 changeEnergy 的调用。
 */
@Mixin(value = NotifiableEnergyContainer.class, remap = false)
public abstract class NotifiableEnergyContainerMixin {

    @Redirect(method = "handleRecipeInner",
              at = @At(value = "INVOKE",
                       target = "Lcom/gregtechceu/gtceu/api/machine/trait/NotifiableEnergyContainer;changeEnergy(J)J"))
    private long typeomega$countGenerated(NotifiableEnergyContainer container, long amount) {
        long changed = container.changeEnergy(amount);
        if (amount > 0 && changed > 0) KardashevLocal.generated(container.getMachine(), changed);
        return changed;
    }
}
