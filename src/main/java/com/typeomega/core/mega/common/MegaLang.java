package com.typeomega.core.mega.common;

import com.google.gson.JsonParser;
import com.typeomega.core.GTIAMod;
import com.typeomega.lib.lang.Tr;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Text of the megastructures and of the multiblock tooltips, in {@code assets/typeomega/mega_lang/<name>.json}: each
 * key maps to [en_us, pt_br, zh_cn]. English goes to the generated en_us through Registrate; tools/mega_lang.py
 * copies the other two into lang/pt_br.json and lang/zh_cn.json.
 */
public final class MegaLang {

    /** Files under mega_lang, without the extension. */
    public static final String[] FILES = { "common", "star_lifter", "stellar_hoop", "system_creator",
            "fusion_suppressor", "kugelblitz", "hraemc", "nidavellir", "assembly_yard", "yggdrasil",
            "stellar_harvester", "suprashell", "disintegrator", "birch_world", "qso", "multiblock_tooltips",
            "mega_tooltips" };

    /** The three texts of every key, read once on first use. */
    private static Map<String, String[]> texts;

    private MegaLang() {}

    public static String english(String key) {
        String[] text = texts().get(key);
        return text == null ? key : text[0];
    }

    /** The key as a {@link Tr} with its three texts, for the tooltip builder of the lib (GTIA-D-410). */
    public static Tr tr(String key) {
        String[] text = texts().get(key);
        if (text == null) throw new IllegalArgumentException("no mega_lang text for " + key);
        return Tr.of(key, text[0], text[1], text[2]);
    }

    public static void init() {
        // Controller names go to Registrate as each machine's lang value (see MegaDefinition).
        texts().forEach((key, text) -> {
            if (!key.startsWith("block.typeomega.")) GTIAMod.REGISTRATE.addRawLang(key, text[0]);
        });
    }

    private static synchronized Map<String, String[]> texts() {
        if (texts != null) return texts;
        Map<String, String[]> read = new LinkedHashMap<>();
        for (String name : FILES) {
            try (var in = MegaLang.class.getClassLoader()
                    .getResourceAsStream("assets/typeomega/mega_lang/" + name + ".json")) {
                if (in == null) continue;
                var root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
                for (var e : root.entrySet()) {
                    var array = e.getValue().getAsJsonArray();
                    read.put(e.getKey(), new String[] { array.get(0).getAsString(), array.get(1).getAsString(),
                            array.get(2).getAsString() });
                }
            } catch (Exception e) {
                throw new IllegalStateException("Broken mega_lang/" + name + ".json", e);
            }
        }
        texts = read;
        return texts;
    }
}
