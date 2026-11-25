package com.typeomega.core.mixin.client;

import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Lets the rules self-test render a larger frame than the test window for readable screenshots. */
@Mixin(Window.class)
public interface WindowAccessor {

    @Accessor("framebufferWidth")
    void typeomega$setFramebufferWidth(int width);

    @Accessor("framebufferHeight")
    void typeomega$setFramebufferHeight(int height);
}
