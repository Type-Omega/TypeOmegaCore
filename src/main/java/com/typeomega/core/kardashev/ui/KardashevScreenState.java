package com.typeomega.core.kardashev.ui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import com.typeomega.core.kardashev.KardashevData;
import com.typeomega.core.kardashev.KardashevHistory;
import com.typeomega.core.kardashev.KardashevLedger;
import com.typeomega.core.kardashev.KardashevView;

/**
 * What one open screen reads from the server: the view of the viewer's team, kept for a second so the widgets of the
 * page share one reading. A client copy of the screen holds nothing; its widgets only draw what the server synced.
 */
final class KardashevScreenState {

    private static final int REFRESH_TICKS = 20;

    private final ServerPlayer player;
    private KardashevView view = KardashevView.empty();
    private long viewTime = Long.MIN_VALUE;

    KardashevScreenState(Player player) {
        this.player = player instanceof ServerPlayer server ? server : null;
    }

    boolean server() {
        return player != null;
    }

    long gameTime() {
        return player == null ? 0 : player.server.overworld().getGameTime();
    }

    KardashevView view() {
        if (player == null) return view;
        long now = gameTime();
        if (now - viewTime >= REFRESH_TICKS || now < viewTime) {
            viewTime = now;
            view = KardashevData.get(player.server).view(KardashevLedger.teamOf(player.getUUID()), now);
        }
        return view;
    }

    KardashevHistory.Snapshot history() {
        if (player == null) return KardashevHistory.Snapshot.EMPTY;
        return KardashevData.get(player.server).history(KardashevLedger.teamOf(player.getUUID()), gameTime());
    }
}
