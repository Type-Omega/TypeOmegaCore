package com.typeomega.core.integration.ae.upload;

import net.minecraftforge.fml.ModList;

/**
 * Entry point of the automatic pattern upload (D-442): the pattern encoded from a recipe goes into the pattern
 * provider named for that recipe, instead of waiting in the terminal. It was ExtendedAE Plus's feature; this one needs
 * only AE2, and stands aside when ExtendedAE Plus is installed so the pattern is not sent twice.
 */
public final class PatternUpload {

    private PatternUpload() {}

    public static boolean active() {
        ModList mods = ModList.get();
        return mods.isLoaded("ae2") && !mods.isLoaded("extendedae_plus");
    }

    public static void init() {
        UploadLang.init();
        // EN: Registered even when the upload is off (ExtendedAE Plus installed), so both sides always list the
        // channel.
        // PT: Registrado mesmo com o upload desligado (ExtendedAE Plus instalado), para os dois lados sempre terem o
        // canal.
        // ZH: 即使上传功能关闭（已安装 ExtendedAE Plus）也注册，使两端始终都有该通道。
        UploadNetwork.register();
    }
}
