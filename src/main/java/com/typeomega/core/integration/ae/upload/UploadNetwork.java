package com.typeomega.core.integration.ae.upload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import com.typeomega.core.TypeOmegaMod;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * The one message of the automatic upload: the client says, just before Encode, which recipe the pattern comes from.
 * The server never sees the recipe viewer, so this is its only way to know which provider the pattern belongs to.
 */
public final class UploadNetwork {

    private static final String VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TypeOmegaMod.MOD_ID, "pattern_upload"), () -> VERSION,
            NetworkRegistry.acceptMissingOr(VERSION), NetworkRegistry.acceptMissingOr(VERSION));

    private UploadNetwork() {}

    public static void register() {
        CHANNEL.registerMessage(0, EncodeKey.class, EncodeKey::write, EncodeKey::read, EncodeKey::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    /** Client only: tell the server what the next Encode of the open menu is for. */
    public static void sendToServer(RecipeKey key) {
        CHANNEL.sendToServer(new EncodeKey(key));
    }

    public record EncodeKey(RecipeKey key) {

        void write(FriendlyByteBuf buf) {
            key.write(buf);
        }

        static EncodeKey read(FriendlyByteBuf buf) {
            return new EncodeKey(RecipeKey.read(buf));
        }

        void handle(Supplier<NetworkEvent.Context> context) {
            var ctx = context.get();
            var sender = ctx.getSender();
            ctx.enqueueWork(() -> {
                if (sender != null && sender.containerMenu instanceof UploadRequestHolder holder)
                    holder.typeomega$setUploadKey(key);
            });
            ctx.setPacketHandled(true);
        }
    }
}
