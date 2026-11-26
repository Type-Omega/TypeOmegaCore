package com.typeomega.core.kardashev.hud;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.kardashev.KardashevData;
import com.typeomega.core.kardashev.KardashevHudStats;
import com.typeomega.core.kardashev.KardashevLedger;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Network of the Kardashev HUD (D-443). The server sends a player the stats once a second, and only while he has the
 * HUD on: the client says so with a {@link Toggle} (also at login, since the setting is saved on his side).
 */
public final class KardashevHudNet {

    private static final String VERSION = "1";
    private static final int SEND_TICKS = 20;
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TypeOmegaMod.MOD_ID, "kardashev_hud"), () -> VERSION, VERSION::equals,
            VERSION::equals);
    private static final Set<UUID> SUBSCRIBERS = ConcurrentHashMap.newKeySet();

    /** What the client draws; null until the first packet and after leaving the world. */
    public static volatile KardashevHudStats latest;

    private KardashevHudNet() {}

    public static void register() {
        CHANNEL.registerMessage(0, Stats.class, Stats::write, Stats::read, Stats::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(1, Toggle.class, Toggle::write, Toggle::read, Toggle::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    /** Tells the server whether this client wants the stats. */
    public static void sendToggle(boolean enabled) {
        CHANNEL.sendToServer(new Toggle(enabled));
    }

    public static boolean subscribed(UUID player) {
        return SUBSCRIBERS.contains(player);
    }

    public static void subscribe(UUID player, boolean enabled) {
        if (enabled) SUBSCRIBERS.add(player);
        else SUBSCRIBERS.remove(player);
    }

    public static void clear() {
        SUBSCRIBERS.clear();
    }

    public static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        SUBSCRIBERS.remove(event.getEntity().getUUID());
    }

    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || SUBSCRIBERS.isEmpty()) return;
        var server = event.getServer();
        long gameTime = server.overworld().getGameTime();
        if (gameTime % SEND_TICKS != 0) return;
        KardashevData data = KardashevData.get(server);
        for (UUID id : SUBSCRIBERS) {
            ServerPlayer player = server.getPlayerList().getPlayer(id);
            if (player == null) continue;
            var stats = data.view(KardashevLedger.teamOf(id), gameTime).hud();
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new Stats(stats));
        }
    }

    record Stats(KardashevHudStats stats) {

        void write(FriendlyByteBuf buf) {
            stats.write(buf);
        }

        static Stats read(FriendlyByteBuf buf) {
            return new Stats(KardashevHudStats.read(buf));
        }

        static void handle(Stats message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> latest = message.stats);
            context.get().setPacketHandled(true);
        }
    }

    record Toggle(boolean enabled) {

        void write(FriendlyByteBuf buf) {
            buf.writeBoolean(enabled);
        }

        static Toggle read(FriendlyByteBuf buf) {
            return new Toggle(buf.readBoolean());
        }

        static void handle(Toggle message, Supplier<NetworkEvent.Context> context) {
            ServerPlayer sender = context.get().getSender();
            if (sender != null) context.get().enqueueWork(() -> subscribe(sender.getUUID(), message.enabled));
            context.get().setPacketHandled(true);
        }
    }
}
