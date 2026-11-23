package com.typeomega.core.mega.typezero;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * The Moon opens with the Cosmodrome's survey (GTIA-D-420): a player lands on it only while carrying Lunar Survey
 * Data. Read by a mixin on Ad Astra's planet check; creative and spectator players are never stopped.
 */
public final class LunarAccess {

    public static final ResourceLocation MOON = new ResourceLocation("ad_astra", "moon");

    private LunarAccess() {}

    public static boolean mayLand(Player player, ResourceKey<Level> destination) {
        if (!MOON.equals(destination.location()) || player.isCreative() || player.isSpectator()) return true;
        return player.getInventory().hasAnyMatching(stack -> stack.is(TypeZeroItems.LUNAR_SURVEY_DATA.get()));
    }

    /** {@link #mayLand}, telling the player why when the answer is no. */
    public static boolean checkLanding(Player player, ResourceKey<Level> destination) {
        boolean allowed = mayLand(player, destination);
        if (!allowed && player instanceof ServerPlayer server)
            server.displayClientMessage(Component.translatable("typeomega.lunar_survey.required"), true);
        return allowed;
    }
}
