package com.typeomega.core.kardashev.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.typeomega.core.kardashev.hud.KardashevHudConfig;
import com.typeomega.core.kardashev.hud.KardashevHudNet;
import com.typeomega.lib.ui.hud.HudRegistry;

/**
 * Client side of the Kardashev HUD (D-443): registers it in the lib's shared HUD registry and keeps the server told.
 */
@OnlyIn(Dist.CLIENT)
public final class KardashevHudClient {

    private KardashevHudClient() {}

    @SuppressWarnings("removal")
    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, KardashevHudConfig.SPEC,
                "typeomega-kardashev-hud.toml");
        FMLJavaModLoadingContext.get().getModEventBus().addListener(KardashevHudClient::onClientSetup);
        MinecraftForge.EVENT_BUS.addListener(KardashevHudClient::onLoggedIn);
        MinecraftForge.EVENT_BUS.addListener(KardashevHudClient::onLoggedOut);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(KardashevHudClient::register);
    }

    /** Puts the HUD in the lib's registry: the editor lists it and its key moves it with the other HUDs. */
    public static void register() {
        HudRegistry.register(KardashevHudOverlay.INSTANCE);
    }

    // EN: The on/off choice is saved here, so the server has to be told again at every login.
    // PT: A escolha ligado/desligado fica salva aqui, então o servidor precisa saber de novo a cada login.
    // ZH: 开关设置保存在客户端，因此每次登录都需要重新告知服务器。
    private static void onLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
        if (KardashevHudConfig.enabled()) KardashevHudNet.sendToggle(true);
    }

    private static void onLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        KardashevHudNet.latest = null;
    }

    public static boolean enabled() {
        return KardashevHudConfig.enabled();
    }

    public static void setEnabled(boolean enabled) {
        KardashevHudConfig.set(enabled);
        if (!enabled) KardashevHudNet.latest = null;
        if (Minecraft.getInstance().getConnection() != null) KardashevHudNet.sendToggle(enabled);
    }

    public static void toggle() {
        setEnabled(!enabled());
    }

    /** Opens the lib's HUD editor, the same one the Flux Matrix screen opens. */
    public static void openEditor() {
        HudRegistry.openEditor();
    }
}
