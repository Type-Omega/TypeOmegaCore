package com.typeomega.core.mixin.ae;

import appeng.api.upgrades.IUpgradeableObject;
import com.glodblock.github.extendedae.common.tileentities.TileExIOPort;
import com.typeomega.core.integration.ae.cards.AccelerationRates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * EN: Same as IOPortBlockEntityMixin for ExtendedAE's Extended IO Port, which overrides tickingRequest with its own
 * table (2048 items per tick, up to x512 with five AE2 cards).
 * PT: Igual ao IOPortBlockEntityMixin para a Extended IO Port do ExtendedAE, que sobrescreve tickingRequest com tabela
 * própria (2048 itens por tick, até x512 com cinco cartões do AE2).
 * ZH: 与 IOPortBlockEntityMixin 相同，作用于 ExtendedAE 的扩展 IO 端口；它用自己的表重写了 tickingRequest
 * （每刻 2048 个物品，五张 AE2 卡时最高 x512）。
 */
@Pseudo
@Mixin(value = TileExIOPort.class, remap = false)
public abstract class ExtendedIOPortMixin {

    @ModifyVariable(method = "tickingRequest",
                    index = 4,
                    at = @At(value = "INVOKE",
                             target = "Lappeng/api/networking/IManagedGridNode;getGrid()Lappeng/api/networking/IGrid;"))
    private long typeomega$tieredCards(long itemsToMove) {
        var upgrades = ((IUpgradeableObject) this).getUpgrades();
        return AccelerationRates.hasTieredCards(upgrades) ? AccelerationRates.extendedIoPortItemsPerTick(upgrades) :
                itemsToMove;
    }
}
