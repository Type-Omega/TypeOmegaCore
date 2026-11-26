package com.typeomega.core.kardashev;

import net.minecraft.network.FriendlyByteBuf;

/** The few numbers the HUD draws, sent once a second to the players who turned it on (D-443); 20 bytes. */
public record KardashevHudStats(int kHundredths, int capHundredths, boolean capped, int megastructures,
                                float generatedPerSecond, float spentPerSecond) {

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(kHundredths);
        buf.writeVarInt(capHundredths);
        buf.writeBoolean(capped);
        buf.writeVarInt(megastructures);
        buf.writeFloat(generatedPerSecond);
        buf.writeFloat(spentPerSecond);
    }

    public static KardashevHudStats read(FriendlyByteBuf buf) {
        return new KardashevHudStats(buf.readVarInt(), buf.readVarInt(), buf.readBoolean(), buf.readVarInt(),
                buf.readFloat(), buf.readFloat());
    }
}
