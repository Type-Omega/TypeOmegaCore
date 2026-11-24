package com.typeomega.core.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.typeomega.core.GTIAMod;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** What the player reads carries the Type Omega name, never the old GTIA one. */
@PrefixGameTestTemplate(false)
@GameTestHolder(GTIAMod.MOD_ID)
public final class BrandingGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";
    private static final Pattern OLD_NAME = Pattern.compile("GTIA|GT:IA|gtia_|Infinity Ascension");

    private BrandingGameTests() {}

    @GameTest(template = TEMPLATE, timeoutTicks = 20)
    public static void noVisibleTextUsesTheOldPackName(GameTestHelper h) {
        List<String> found = new ArrayList<>();
        for (String language : List.of("en_us", "pt_br", "zh_cn")) {
            String path = "/assets/" + GTIAMod.MOD_ID + "/lang/" + language + ".json";
            try (InputStream in = BrandingGameTests.class.getResourceAsStream(path)) {
                h.assertTrue(in != null, "missing " + path);
                JsonObject lang = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                        .getAsJsonObject();
                lang.entrySet().forEach(e -> {
                    if (OLD_NAME.matcher(e.getValue().getAsString()).find()) found.add(language + ":" + e.getKey());
                });
            } catch (java.io.IOException e) {
                throw new IllegalStateException(e);
            }
        }
        h.assertTrue(found.isEmpty(), "visible text still names GTIA: " + found);
        h.succeed();
    }
}
