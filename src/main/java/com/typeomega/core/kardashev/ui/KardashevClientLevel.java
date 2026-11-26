package com.typeomega.core.kardashev.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** The client's level, in a class of its own so the factory verifies on a dedicated server without loading it. */
@OnlyIn(Dist.CLIENT)
final class KardashevClientLevel {

    private KardashevClientLevel() {}

    static Level get() {
        return Minecraft.getInstance().level;
    }
}
