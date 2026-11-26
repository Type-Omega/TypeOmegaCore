/*
 * The rule that picks the provider is the one of ExtendedAE Plus (LGPL-3.0, by GaLicn and contributors): a provider
 * belongs to a recipe when its name contains the recipe's name, and the pattern goes only when exactly one name
 * matches. Rewritten here for server-side names and several candidate names; see THIRD_PARTY_LICENSES.md.
 */
package com.typeomega.core.integration.ae.upload;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/** Decides which pattern providers a recipe key points at. Pure: it only reads names. */
public final class ProviderMatcher {

    private ProviderMatcher() {}

    public enum Kind {
        /** No provider carries any of the names. */
        NONE,
        /** Exactly one name matches; the pattern may go to the providers that carry it. */
        UNIQUE,
        /** More than one name matches; the player has to tell the providers apart. */
        AMBIGUOUS
    }

    /**
     * @param name  the provider name that won (UNIQUE) or the recipe name that matched too much (AMBIGUOUS)
     * @param items the providers with that name, in the order they were given
     * @param names the distinct provider names that matched, for the message
     */
    public record Selection<T>(Kind kind, String name, List<T> items, List<String> names) {

        static <T> Selection<T> none() {
            return new Selection<>(Kind.NONE, "", List.of(), List.of());
        }
    }

    /**
     * The first recipe name that matches any provider decides. Among the providers whose name contains it, an exact
     * name ("Assembler") beats a longer one ("Basic Assembler"); otherwise one distinct name must be left.
     */
    public static <T> Selection<T> select(List<T> providers, Function<T, String> nameOf, RecipeKey key) {
        for (RecipeKey.Candidate candidate : key.candidates()) {
            for (String needle : needles(candidate)) {
                Map<String, List<T>> byName = new LinkedHashMap<>();
                for (T provider : providers) {
                    String name = RecipeKey.plain(nameOf.apply(provider));
                    if (name.toLowerCase(Locale.ROOT).contains(needle))
                        byName.computeIfAbsent(name, k -> new ArrayList<>()).add(provider);
                }
                if (byName.isEmpty()) continue;
                var exact = byName.entrySet().stream().filter(e -> e.getKey().equalsIgnoreCase(needle)).toList();
                if (exact.size() == 1) return unique(exact.get(0), byName);
                if (byName.size() == 1) return unique(byName.entrySet().iterator().next(), byName);
                return new Selection<>(Kind.AMBIGUOUS, candidate.text(), List.of(), List.copyOf(byName.keySet()));
            }
        }
        return Selection.none();
    }

    private static <T> Selection<T> unique(Map.Entry<String, List<T>> winner, Map<String, List<T>> all) {
        return new Selection<>(Kind.UNIQUE, winner.getKey(), List.copyOf(winner.getValue()), List.copyOf(all.keySet()));
    }

    // EN: The server's rendering of the name first, then what the client showed; the same text twice counts once.
    // PT: Primeiro o nome renderizado pelo servidor, depois o que o cliente mostrou; o mesmo texto duas vezes conta
    // uma.
    // ZH: 先用服务端渲染的名称，再用客户端显示的文本；相同文本只算一次。
    private static List<String> needles(RecipeKey.Candidate candidate) {
        var out = new ArrayList<String>(2);
        for (String text : new String[] { RecipeKey.plain(candidate.title().getString()), candidate.text() }) {
            String lower = RecipeKey.plain(text).toLowerCase(Locale.ROOT);
            if (!lower.isEmpty() && !out.contains(lower)) out.add(lower);
        }
        return out;
    }
}
