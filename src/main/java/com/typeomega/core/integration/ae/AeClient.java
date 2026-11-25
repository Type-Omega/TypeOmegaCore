package com.typeomega.core.integration.ae;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import appeng.init.client.InitScreens;
import com.typeomega.core.integration.ae.interfaces.InterfaceTiers;
import com.typeomega.core.integration.ae.interfaces.TieredInterfaceScreen;

/** Client side of the AE2 pieces: the screen of the tiered interfaces, on the style of ExtendedAE's interface. */
final class AeClient {

    private AeClient() {}

    static void init(IEventBus bus) {
        bus.addListener(AeClient::onClientSetup);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> InitScreens.register(InterfaceTiers.MENU, TieredInterfaceScreen::new,
                "/screens/ex_interface.json"));
    }
}
