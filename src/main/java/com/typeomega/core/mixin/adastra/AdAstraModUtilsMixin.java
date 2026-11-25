package com.typeomega.core.mixin.adastra;

import net.minecraft.world.entity.player.Player;

import com.typeomega.core.mega.typezero.LunarAccess;
import earth.terrarium.adastra.api.planets.Planet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * EN: The Moon needs the Cosmodrome's survey (D-420). Target: Ad Astra's server check before a rocket lands;
 * pseudo, so worlds without Ad Astra skip it.
 * PT: A Lua pede o levantamento do Cosmódromo (D-420). Alvo: a checagem do servidor do Ad Astra antes de o
 * foguete pousar; pseudo, então mundos sem Ad Astra a ignoram.
 * ZH: 登月需要航天发射场的勘测数据（D-420）。目标：Ad Astra 在火箭着陆前的服务端检查；
 * 使用 pseudo，没有 Ad Astra 的世界会跳过。
 */
@Pseudo
@Mixin(targets = "earth.terrarium.adastra.common.utils.ModUtils", remap = false)
public abstract class AdAstraModUtilsMixin {

    @Inject(method = "canTeleportToPlanet", at = @At("HEAD"), cancellable = true, require = 0)
    private static void typeomega$needsLunarSurvey(Player player, Planet planet,
                                                   CallbackInfoReturnable<Boolean> cir) {
        if (!LunarAccess.checkLanding(player, planet.dimension())) cir.setReturnValue(false);
    }
}
