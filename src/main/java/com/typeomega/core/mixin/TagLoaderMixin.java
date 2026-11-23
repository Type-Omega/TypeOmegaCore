package com.typeomega.core.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;

import com.typeomega.core.data.rocket.RocketFuelTags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

/** Sets the rocket fuel tags after every fluid tag file is read ({@link RocketFuelTags}). */
@Mixin(TagLoader.class)
public abstract class TagLoaderMixin {

    @Shadow
    @Final
    private String directory;

    @Inject(method = "load", at = @At("RETURN"))
    private void typeomega$setRocketFuels(ResourceManager manager,
                                          CallbackInfoReturnable<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> cir) {
        if ("tags/fluids".equals(directory)) RocketFuelTags.apply(cir.getReturnValue());
    }
}
