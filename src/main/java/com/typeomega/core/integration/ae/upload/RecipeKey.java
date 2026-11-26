package com.typeomega.core.integration.ae.upload;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * What the player's recipe viewer said the recipe is: the names, most specific first, that a pattern provider can be
 * called to take it ("Assembler" for a GregTech assembler recipe). Each name travels twice, as the translatable
 * component (the server renders it in its own language, like the provider names it compares with) and as the text
 * the client already rendered (for providers the player named by hand).
 */
public record RecipeKey(List<Candidate> candidates) {

    /** The limit of names in one key; the packet refuses more. */
    public static final int MAX_CANDIDATES = 4;
    private static final int MAX_TEXT = 128;

    public RecipeKey {
        candidates = List.copyOf(candidates);
    }

    public record Candidate(Component title, String text) {

        public static Candidate of(Component title) {
            return new Candidate(title, plain(title.getString()));
        }
    }

    /** The name the client shows first; what Wireless Comprehensive Work Terminal searches its provider list with. */
    public String primaryText() {
        return candidates.isEmpty() ? "" : candidates.get(0).text();
    }

    public boolean isEmpty() {
        return candidates.stream().allMatch(c -> c.text().isEmpty() && c.title().getString().isBlank());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(candidates.size());
        for (Candidate c : candidates) {
            buf.writeComponent(c.title());
            buf.writeUtf(c.text(), MAX_TEXT);
        }
    }

    public static RecipeKey read(FriendlyByteBuf buf) {
        int n = buf.readVarInt();
        if (n < 0 || n > MAX_CANDIDATES)
            throw new io.netty.handler.codec.DecoderException("recipe key of " + n + " names");
        List<Candidate> out = new ArrayList<>(n);
        for (int i = 0; i < n; i++) out.add(new Candidate(buf.readComponent(), buf.readUtf(MAX_TEXT)));
        return new RecipeKey(out);
    }

    /** Text without colour codes and with the spaces trimmed, the form names are compared in. */
    public static String plain(String text) {
        String stripped = ChatFormatting.stripFormatting(text);
        return stripped == null ? "" : stripped.trim();
    }
}
