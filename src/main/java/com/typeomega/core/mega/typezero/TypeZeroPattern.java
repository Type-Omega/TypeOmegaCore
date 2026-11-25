package com.typeomega.core.mega.typezero;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * The block layout of a Type 0 megastructure, read from {@code data/typeomega/multiblock/typezero/<id>.txt}
 * (D-425). The file holds the aisles from the back to the front (controller), separated by a blank line; in each
 * aisle, one line per layer from the ground up, as in {@code FactoryBlockPattern.start()}. Lines starting with
 * {@code #} are comments.
 */
final class TypeZeroPattern {

    private TypeZeroPattern() {}

    static String[][] read(String id) {
        String path = "data/typeomega/multiblock/typezero/" + id + ".txt";
        var stream = TypeZeroPattern.class.getClassLoader().getResourceAsStream(path);
        if (stream == null) throw new IllegalStateException("missing pattern " + path);
        List<String[]> aisles = new ArrayList<>();
        List<String> layers = new ArrayList<>();
        try (var in = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                if (line.startsWith("#")) continue;
                if (line.isBlank()) {
                    if (!layers.isEmpty()) aisles.add(layers.toArray(String[]::new));
                    layers.clear();
                } else layers.add(line.strip());
            }
        } catch (IOException e) {
            throw new UncheckedIOException(path, e);
        }
        if (!layers.isEmpty()) aisles.add(layers.toArray(String[]::new));
        int height = aisles.get(0).length, width = aisles.get(0)[0].length();
        for (String[] aisle : aisles) {
            if (aisle.length != height) throw new IllegalStateException(path + ": aisles of different heights");
            for (String layer : aisle)
                if (layer.length() != width) throw new IllegalStateException(path + ": layers of different widths");
        }
        return aisles.toArray(String[][]::new);
    }
}
