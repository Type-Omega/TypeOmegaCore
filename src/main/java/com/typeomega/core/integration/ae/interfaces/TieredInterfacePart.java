package com.typeomega.core.integration.ae.interfaces;

import net.minecraft.world.entity.player.Player;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import com.glodblock.github.extendedae.common.parts.PartExInterface;

/** Cable part of ME Interface (II) to (V): the 36-slot Extended Interface part with the rows of its tier. */
public final class TieredInterfacePart extends PartExInterface {

    private final InterfaceTier tier;

    public TieredInterfacePart(IPartItem<?> partItem) {
        super(partItem);
        this.tier = ((TieredInterfacePartItem) partItem).tier();
        InterfaceLogicTier.apply(getInterfaceLogic(), tier);
    }

    public InterfaceTier tier() {
        return tier;
    }

    @Override
    public void openMenu(Player player, MenuLocator locator) {
        MenuOpener.open(InterfaceTiers.MENU, player, locator);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(InterfaceTiers.MENU, player, subMenu.getLocator());
    }

    @Override
    public IPartModel getStaticModels() {
        var models = InterfaceTiers.partModels(tier);
        if (isActive() && isPowered()) return models.hasChannel();
        return isPowered() ? models.on() : models.off();
    }
}
