package com.typeomega.core.gametest;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import com.lowdragmc.lowdraglib.gui.widget.Widget;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.GTIAMod;
import com.typeomega.core.data.GTIARecipeTypes;
import com.typeomega.core.mega.typezero.Tokamak;
import com.typeomega.core.mega.typezero.TokamakMachine;
import com.typeomega.core.mega.typezero.TokamakScreen;
import com.typeomega.core.mega.typezero.TypeZeroRecipes;
import com.typeomega.lib.ui.IllustratedPanel;
import com.typeomega.lib.ui.IllustratedRecipeUI;
import com.typeomega.lib.ui.PanelLayout;

import java.util.List;
import java.util.UUID;

/** The illustrated Tokamak screen (GTIA-D-435): it mounts, its slots sit inside the picture, its lines are right. */
@PrefixGameTestTemplate(false)
@GameTestHolder(GTIAMod.MOD_ID)
public final class TokamakScreenGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";

    private TokamakScreenGameTests() {}

    private static GTRecipe recipe(GameTestHelper h, String path) {
        var recipe = h.getLevel().getServer().getRecipeManager().byKey(GTIAMod.id("tokamak_fusion/" + path))
                .orElse(null);
        h.assertTrue(recipe instanceof GTRecipe, "missing Tokamak recipe " + path);
        return (GTRecipe) recipe;
    }

    private static void assertInside(GameTestHelper h, Widget panel) {
        for (var child : ((IllustratedPanel) panel).widgets) {
            var p = child.getSelfPosition();
            h.assertTrue(p.x >= 0 && p.y >= 0 && p.x + child.getSizeWidth() <= panel.getSizeWidth() &&
                    p.y + child.getSizeHeight() <= panel.getSizeHeight(),
                    child.getClass().getSimpleName() + " at " + p + " leaves the panel");
        }
    }

    private static List<String> keys(List<Component> lines) {
        return lines.stream().map(c -> c.getContents() instanceof TranslatableContents t ? t.getKey() : "").toList();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 20)
    public static void recipeScreenMountsWithEverySlotInThePicture(GameTestHelper h) {
        h.assertTrue(TokamakScreen.LAYOUT.problems().isEmpty(), "layout: " + TokamakScreen.LAYOUT.problems());
        h.assertTrue(GTIARecipeTypes.TOKAMAK_FUSION.getRecipeUI() instanceof IllustratedRecipeUI,
                "tokamak_fusion uses the illustrated recipe UI");
        var ui = (IllustratedRecipeUI) GTIARecipeTypes.TOKAMAK_FUSION.getRecipeUI();
        var panel = ui.createPanel();
        assertInside(h, panel);
        long tanks = panel.widgets.stream()
                .filter(w -> w instanceof com.gregtechceu.gtceu.api.gui.widget.TankWidget).count();
        h.assertTrue(tanks == 6, "3 fuel and 3 exhaust tanks, got " + tanks);
        h.assertTrue(TokamakScreenGameTests.class.getResource(
                "/assets/typeomega/textures/gui/recipe/tokamak_fusion.png") != null, "the torus art ships");
        // EN: the recipe's slot counts fit the layout, so no input is hidden.
        // PT: o número de slots da receita cabe no layout, então nenhuma entrada some.
        // ZH: 配方槽位数量不超过布局，因此不会隐藏任何输入。
        for (String path : List.of("deuterium_tritium", "deuterium_helium_3")) {
            var r = recipe(h, path);
            int fluidsIn = r.inputs.getOrDefault(
                    com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability.CAP, List.of()).size();
            h.assertTrue(fluidsIn <= TokamakScreen.LAYOUT
                    .slots(PanelLayout.Kind.FLUID, PanelLayout.Role.INPUT).size(), path + " has too many fluids");
        }
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 20)
    public static void recipeLinesCarryStartUpAndIgnition(GameTestHelper h) {
        var ui = (IllustratedRecipeUI) GTIARecipeTypes.TOKAMAK_FUSION.getRecipeUI();
        var dt = recipe(h, "deuterium_tritium");
        var lines = ui.lines(dt).build();
        h.assertTrue(keys(lines).equals(List.of("typeomegalib.ui.total", "typeomegalib.ui.output",
                "typeomegalib.ui.time", TokamakScreen.START.key(), TokamakScreen.IGNITION.key())),
                "GTNH order Total, Output, Time, start-up, ignition: " + keys(lines));
        h.assertTrue(lines.size() <= ui.lineCount(), "EMI reserves room for every line");
        h.assertTrue(TokamakScreen.ignitionKelvin(dt) == TypeZeroRecipes.DT_IGNITION_K, "D-T ignition from data");
        h.assertTrue(TokamakScreen.ignitionKelvin(recipe(h, "deuterium_helium_3")) ==
                TypeZeroRecipes.DHE3_IGNITION_K, "D-He3 ignition from data");
        var start = (TranslatableContents) lines.get(3).getContents();
        h.assertTrue(start.getArgs()[0].equals(String.format(java.util.Locale.US, "%,d", TokamakMachine.START_EU)),
                "start-up EU comes from TokamakMachine.START_EU");
        h.assertTrue(TokamakScreen.outputEUt(dt) == TypeZeroRecipes.DT_EUT, "D-T gives DT_EUT");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "type_zero_megas", timeoutTicks = 60)
    public static void machineTabShowsTheSamePanel(GameTestHelper h) {
        var placed = TypeZeroTestRig.place(h, Tokamak.DEFINITION, TypeZeroTestRig.slot(h, 8), UUID.randomUUID(),
                null);
        h.assertTrue(TypeZeroTestRig.form(placed.controller()), "the Tokamak forms");
        var tokamak = (TokamakMachine) placed.controller();
        var panel = TokamakScreen.machinePanel(tokamak);
        h.assertTrue(panel instanceof IllustratedPanel p && p.layout() == TokamakScreen.LAYOUT,
                "the machine tab uses the EMI layout");
        assertInside(h, panel);
        h.assertTrue(tokamak.defaultRecipe() != null, "the tab shows D-T before the first run");
        var lines = keys(TokamakScreen.machineLinesForTests(tokamak));
        h.assertTrue(lines.contains("typeomega.machine.tokamak_hv.charge"), "a cold Tokamak shows its charge");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, timeoutTicks = 20)
    public static void layoutRejectsASlotOutsideThePicture(GameTestHelper h) {
        var broken = PanelLayout.builder(160, 80).fluid(PanelLayout.Role.INPUT, 150, 70).build();
        h.assertFalse(broken.problems().isEmpty(), "a slot past the picture is reported");
        h.succeed();
    }
}
