package com.typeomega.core.gametest;

import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.registry.GTRegistries;

import net.minecraft.ChatFormatting;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.data.ModNameLine;
import com.typeomega.core.data.TypeOmegaTooltips;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** What the player reads carries the Type Omega name, never the old GTIA one. */
@PrefixGameTestTemplate(false)
@GameTestHolder(TypeOmegaMod.MOD_ID)
public final class BrandingGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";
    private static final Pattern OLD_NAME = Pattern.compile("GTIA|GT:IA|gtia_|Infinity Ascension");

    private BrandingGameTests() {}

    @GameTest(template = TEMPLATE, timeoutTicks = 20)
    public static void noVisibleTextUsesTheOldPackName(GameTestHelper h) {
        List<String> found = new ArrayList<>();
        for (String language : List.of("en_us", "pt_br", "zh_cn")) {
            String path = "/assets/" + TypeOmegaMod.MOD_ID + "/lang/" + language + ".json";
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

    /** The tooltip names the mod as the mod list does (mods.toml displayName). */
    @GameTest(template = TEMPLATE, timeoutTicks = 20)
    public static void tooltipModNameIsTheDisplayName(GameTestHelper h) {
        String display = ModList.get().getModContainerById(TypeOmegaMod.MOD_ID).orElseThrow().getModInfo()
                .getDisplayName();
        h.assertTrue(TypeOmegaTooltips.MOD_NAME.equals(display),
                "tooltip says " + TypeOmegaTooltips.MOD_NAME + ", mods.toml says " + display);
        h.succeed();
    }

    /** With EMI appending the mod name to every item tooltip, a multiblock tooltip still names the mod once. */
    @GameTest(template = TEMPLATE, timeoutTicks = 20)
    public static void multiblockTooltipNamesTheModOnce(GameTestHelper h) {
        int checked = 0;
        for (var definition : GTRegistries.MACHINES) {
            if (!TypeOmegaMod.MOD_ID.equals(definition.getId().getNamespace()) ||
                    !(definition instanceof MultiblockMachineDefinition) || definition.getTooltipBuilder() == null) {
                continue;
            }
            List<Component> lines = new ArrayList<>();
            definition.getTooltipBuilder().accept(ItemStack.EMPTY, lines);
            if (lines.stream().noneMatch(l -> TypeOmegaTooltips.MOD_NAME.equals(l.getString()))) continue;
            // EN: What EMI's ItemStackMixin appends at the end of every item tooltip.
            // PT: O que o ItemStackMixin do EMI acrescenta no fim de toda tooltip de item.
            // ZH: EMI 的 ItemStackMixin 在每个物品提示末尾添加的内容。
            lines.add(Component.literal(TypeOmegaTooltips.MOD_NAME).withStyle(ChatFormatting.BLUE,
                    ChatFormatting.ITALIC));
            ModNameLine.keepOnce(lines, TypeOmegaTooltips.MOD_NAME);
            long names = lines.stream().filter(l -> TypeOmegaTooltips.MOD_NAME.equals(l.getString())).count();
            h.assertTrue(names == 1, definition.getId() + " names the mod " + names + " times");
            h.assertTrue(TypeOmegaTooltips.MOD_NAME.equals(lines.get(lines.size() - 1).getString()),
                    definition.getId() + " does not end with the mod name");
            checked++;
        }
        h.assertTrue(checked >= 12, "only " + checked + " multiblock tooltips checked");
        h.succeed();
    }
}
