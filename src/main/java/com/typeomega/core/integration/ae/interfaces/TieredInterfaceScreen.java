package com.typeomega.core.integration.ae.interfaces;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import com.glodblock.github.extendedae.client.gui.GuiExInterface;
import com.glodblock.github.extendedae.container.ContainerExInterface;

/** The Extended Interface screen, titled with the name of the tier. */
public final class TieredInterfaceScreen extends GuiExInterface {

    public TieredInterfaceScreen(ContainerExInterface menu, Inventory playerInventory, Component title,
                                 ScreenStyle style) {
        super(menu, playerInventory, title, style);
        setTextContent(AEBaseScreen.TEXT_ID_DIALOG_TITLE, title);
    }
}
