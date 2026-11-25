package com.typeomega.core.mixin.ae;

import appeng.api.upgrades.IUpgradeableObject;
import appeng.blockentity.storage.IOPortBlockEntity;
import com.typeomega.core.integration.ae.cards.AccelerationRates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * EN: Acceleration Cards (II) to (V) on the AE2 IO Port (GTIA-D-442). The items moved per tick is a local of
 * tickingRequest, set by a switch on the AE2 card count; it is replaced right before the grid is fetched, once the
 * switch is done (local 4, the only long). Untouched when no tiered card is installed.
 * PT: Cartões de Aceleração (II) a (V) na IO Port do AE2 (GTIA-D-442). Os itens movidos por tick são uma variável
 * local de tickingRequest, definida por um switch na quantidade de cartões do AE2; é trocada logo antes de buscar a
 * rede, depois do switch (local 4, o único long). Intacto quando não há cartão escalonado.
 * ZH: 加速卡 (II) 至 (V) 作用于 AE2 IO 端口（GTIA-D-442）。每刻搬运的物品数是 tickingRequest 的局部变量，由针对 AE2
 * 卡数的 switch 赋值；在 switch 结束后、获取网络之前替换（局部变量 4，唯一的 long）。未安装分级卡时保持原样。
 */
@Pseudo
@Mixin(value = IOPortBlockEntity.class, remap = false)
public abstract class IOPortBlockEntityMixin {

    @ModifyVariable(method = "tickingRequest",
                    index = 4,
                    at = @At(value = "INVOKE",
                             target = "Lappeng/api/networking/IManagedGridNode;getGrid()Lappeng/api/networking/IGrid;"))
    private long typeomega$tieredCards(long itemsToMove) {
        var upgrades = ((IUpgradeableObject) this).getUpgrades();
        return AccelerationRates.hasTieredCards(upgrades) ? AccelerationRates.ioPortItemsPerTick(upgrades) :
                itemsToMove;
    }
}
