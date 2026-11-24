package com.typeomega.core.gametest;

import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.config.ConfigHolder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.typeomega.core.GTIAMod;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Type Omega has one mode, the former Normal (GTIA-D-436): no world rules, no presets, maintenance from LV on. */
@GameTestHolder(GTIAMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SingleModeGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";

    private SingleModeGameTests() {}

    /** The rules screen, its create-world button, the command and their texts are gone. */
    @GameTest(template = TEMPLATE, timeoutTicks = 20)
    public static void noRulesCommandScreenOrText(GameTestHelper h) {
        for (String removed : List.of("com.typeomega.core.client.GTIARulesScreen",
                "com.typeomega.core.client.GTIARulesClient", "com.typeomega.core.client.GTIARestartScreen",
                "com.typeomega.core.rules.WorldRules", "com.typeomega.core.rules.GTIARules",
                "com.typeomega.core.mixin.client.CreateWorldGameTabMixin")) {
            boolean present;
            try {
                Class.forName(removed, false, SingleModeGameTests.class.getClassLoader());
                present = true;
            } catch (ClassNotFoundException e) {
                present = false;
            }
            h.assertFalse(present, removed + " still exists");
        }
        var root = h.getLevel().getServer().getCommands().getDispatcher().getRoot().getChild(GTIAMod.MOD_ID);
        h.assertTrue(root == null || root.getChild("rules") == null, "/typeomega rules is still registered");

        JsonObject mixins = json("/typeomega.mixins.json");
        h.assertFalse(mixins.toString().contains("CreateWorld") || mixins.toString().contains("WorldOpenFlows") ||
                mixins.toString().contains("ServerPacksSource"), "create-world and rules mixins are gone");

        List<String> found = new ArrayList<>();
        for (String language : List.of("en_us", "pt_br", "zh_cn")) {
            json("/assets/" + GTIAMod.MOD_ID + "/lang/" + language + ".json").entrySet().forEach(e -> {
                if (e.getKey().startsWith("typeomega.rule") || e.getValue().getAsString().contains("Journey"))
                    found.add(language + ":" + e.getKey());
            });
        }
        h.assertTrue(found.isEmpty(), "rule or mode texts left: " + found);
        h.succeed();
    }

    /** Maintenance is on, so the LV Electric Blast Furnace needs a maintenance hatch. */
    @GameTest(template = TEMPLATE, timeoutTicks = 20)
    public static void maintenanceIsRequiredAtLv(GameTestHelper h) {
        h.assertTrue(ConfigHolder.INSTANCE.machines.enableMaintenance, "maintenance is on");
        var ebf = GTRegistries.MACHINES.get(new ResourceLocation("gtceu", "electric_blast_furnace"));
        h.assertTrue(ebf instanceof MultiblockMachineDefinition, "the EBF is a multiblock");
        var shapes = ((MultiblockMachineDefinition) ebf).getMatchingShapes();
        h.assertFalse(shapes.isEmpty(), "the EBF has a preview");
        boolean hatch = false;
        var blocks = shapes.get(0).getBlocks();
        for (var plane : blocks)
            for (var row : plane)
                for (var info : row)
                    if (info != null &&
                            BuiltInRegistries.BLOCK.getKey(info.getBlockState().getBlock()).getPath()
                                    .contains("maintenance_hatch"))
                        hatch = true;
        h.assertTrue(hatch, "the LV EBF preview has a maintenance hatch");
        h.succeed();
    }

    private static JsonObject json(String path) {
        try (InputStream in = SingleModeGameTests.class.getResourceAsStream(path)) {
            if (in == null) throw new IllegalStateException("missing " + path);
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
