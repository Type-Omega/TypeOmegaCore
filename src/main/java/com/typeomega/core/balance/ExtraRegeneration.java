package com.typeomega.core.balance;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;

/** Extra regeneration (GTO: Extra Regeneration), applied every 2.5 seconds. */
public final class ExtraRegeneration {

    public static final int PERIOD = 50;
    // EN: GTO's Normal values: from food 15, heal 2 half hearts every 2.5 s (GTIA-D-280, D-436).
    // PT: Valores do Normal do GTO: a partir de fome 15, cura 2 meios corações a cada 2,5 s (GTIA-D-280, D-436).
    // ZH: GTO 普通模式数值：饱食度 15 起，每 2.5 秒恢复 2 点生命（半颗心）（GTIA-D-280、D-436）。
    public static final int FOOD_THRESHOLD = 15;
    public static final float HEAL = 2;

    private ExtraRegeneration() {}

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) return;
        if (player.tickCount % PERIOD == 0) regenerate(player);
    }

    /** Heals the player when the food level reaches the threshold; returns the health healed. */
    public static float regenerate(ServerPlayer player) {
        if (player.isDeadOrDying()) return 0;
        if (player.getFoodData().getFoodLevel() < FOOD_THRESHOLD || player.getHealth() >= player.getMaxHealth())
            return 0;
        float before = player.getHealth();
        player.heal(HEAL);
        return player.getHealth() - before;
    }
}
