package com.typeomega.core.mega.typezero;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;

import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.lib.lang.Tr;
import com.typeomega.lib.ui.DisplaySlots;
import com.typeomega.lib.ui.IllustratedPanel;
import com.typeomega.lib.ui.IllustratedRecipeUI;
import com.typeomega.lib.ui.IllustratedTab;
import com.typeomega.lib.ui.InfoLines;
import com.typeomega.lib.ui.InfoLinesWidget;
import com.typeomega.lib.ui.PanelLayout;
import com.typeomega.lib.ui.ProgressBarWidget;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The illustrated screen of the HV Tokamak (D-435, U5): the cutaway torus with the plasma, the fuel tanks on
 * the left feeding it, the exhaust on the right, and under it Total, Output, Time, the start-up charge and the
 * ignition temperature. The EMI page ({@code setRecipeUI}) and the machine tab share the layout and the art.
 */
public final class TokamakScreen {

    public static final ResourceLocation ART = TypeOmegaMod.id("textures/gui/recipe/tokamak_fusion.png");

    /** Fuel tanks on the left, lithium under them, exhaust on the right; the pipes of the art end at these slots. */
    public static final PanelLayout LAYOUT = PanelLayout.builder(160, 80).art(ART)
            .fluid(PanelLayout.Role.INPUT, 4, 4)
            .fluid(PanelLayout.Role.INPUT, 4, 23)
            .fluid(PanelLayout.Role.INPUT, 4, 42)
            .item(PanelLayout.Role.INPUT, 4, 61)
            .fluid(PanelLayout.Role.OUTPUT, 138, 13)
            .fluid(PanelLayout.Role.OUTPUT, 138, 32)
            .fluid(PanelLayout.Role.OUTPUT, 138, 51)
            // EN: The pipes of the art fill like GTNH progress arrows; no separate GTCEu fusion bar.
            // PT: Os canos da arte enchem como as setas de progresso do GTNH; sem a barra de fusão do GTCEu à parte.
            // ZH: 插图中的管道像 GTNH 的进度箭头一样填充；不再另加 GTCEu 聚变进度条。
            .mark("flow_right", 22, 9, 19, 7)
            .mark("flow_right", 22, 28, 19, 7)
            .mark("flow_right", 22, 47, 19, 7)
            .mark("flow_right", 22, 66, 25, 7)
            .mark("flow_right", 118, 18, 20, 7)
            .mark("flow_right", 118, 37, 20, 7)
            .mark("flow_right", 118, 56, 20, 7)
            .mark("charge", 118, 2, 40, 6)
            .build();

    public static final Tr START = text("start", "Start-up: %s EU", "Partida: %s EU", "启动能量：%s EU");
    public static final Tr IGNITION = text("ignition", "Ignition: %s K", "Ignição: %s K", "点火温度：%s K");
    public static final Tr TAB = text("tab", "Fusion", "Fusão", "聚变");

    /** Lines the recipe type adds after Total, Output and Time. */
    public static final int EXTRA_LINES = 2;

    private TokamakScreen() {}

    private static Tr text(String id, String en, String pt, String zh) {
        var tr = Tr.of("typeomega.ui.tokamak." + id, en, pt, zh);
        TypeOmegaMod.REGISTRATE.addRawLang(tr.key(), en);
        return tr;
    }

    /** Ignition temperature of a Tokamak recipe, from its data ({@link TypeZeroRecipes#IGNITION_KEY}). */
    public static long ignitionKelvin(GTRecipe recipe) {
        return recipe.data.getLong(TypeZeroRecipes.IGNITION_KEY);
    }

    static void extraLines(GTRecipe recipe, InfoLines lines) {
        lines.line(START, InfoLines.format(TokamakMachine.START_EU));
        long kelvin = ignitionKelvin(recipe);
        if (kelvin > 0) lines.line(IGNITION, InfoLines.format(kelvin));
    }

    public static IllustratedRecipeUI recipeUI(GTRecipeType type) {
        var ui = new IllustratedRecipeUI(type, LAYOUT, EXTRA_LINES, TokamakScreen::extraLines);
        ui.setProgressBar(GuiTextures.PROGRESS_BAR_FUSION, ProgressTexture.FillDirection.LEFT_TO_RIGHT);
        return ui;
    }

    // ------------------------------------------------------------------ machine tab (U6)

    public static IllustratedTab tab(TokamakMachine machine) {
        return new IllustratedTab(new ItemStackTexture(Tokamak.DEFINITION.asStack()), TAB.component(),
                () -> machinePanel(machine));
    }

    /**
     * The same panel on the machine: the fuel of the recipe it runs (or the D-T recipe before the first run), the
     * start-up charge as a bar, and the lines read on the server every tick.
     */
    public static Widget machinePanel(TokamakMachine machine) {
        @Nullable
        GTRecipe recipe = machine.getRecipeLogic().getLastRecipe();
        if (recipe == null) recipe = machine.defaultRecipe();
        var panel = new IllustratedPanel(LAYOUT, 3 + EXTRA_LINES + 1);
        final GTRecipe shown = recipe;
        panel.slots(slot -> switch (slot.kind()) {
            case ITEM -> DisplaySlots.item(items(shown, slot), slot.role());
            case FLUID -> DisplaySlots.fluid(fluids(shown, slot), slot.role());
        });
        panel.atMark("charge", mark -> new ProgressBarWidget(0, 0, mark.width(), mark.height(),
                () -> machine.lit() ? 1 : (double) machine.startCharge() / TokamakMachine.START_EU, 0xFF7AE0));
        panel.lines(new InfoLinesWidget(() -> machineLines(machine, shown), 3 + EXTRA_LINES + 1));
        return panel;
    }

    static List<Component> machineLines(TokamakMachine machine, @Nullable GTRecipe recipe) {
        var lines = recipe == null ? InfoLines.create() :
                IllustratedRecipeUI.standardLines(recipe, TokamakScreen::extraLines);
        if (!machine.isFormed()) return lines.build();
        lines.line(machine.lit() ?
                Component.translatable("typeomega.machine.tokamak_hv.lit").withStyle(ChatFormatting.DARK_GREEN) :
                Component.translatable("typeomega.machine.tokamak_hv.charge",
                        InfoLines.format(machine.startCharge()), InfoLines.format(TokamakMachine.START_EU)));
        return lines.build();
    }

    private static List<ItemStack> items(@Nullable GTRecipe recipe, PanelLayout.Slot slot) {
        if (recipe == null) return List.of();
        var io = slot.role() == PanelLayout.Role.OUTPUT ? IO.OUT : IO.IN;
        var contents = (io == IO.IN ? recipe.inputs : recipe.outputs).getOrDefault(ItemRecipeCapability.CAP,
                List.of());
        if (slot.index() >= contents.size()) return List.of();
        var ingredient = ItemRecipeCapability.CAP.of(contents.get(slot.index()).content);
        return Arrays.asList(ingredient.getItems());
    }

    private static List<FluidStack> fluids(@Nullable GTRecipe recipe, PanelLayout.Slot slot) {
        if (recipe == null) return List.of();
        var io = slot.role() == PanelLayout.Role.OUTPUT ? IO.OUT : IO.IN;
        var contents = (io == IO.IN ? recipe.inputs : recipe.outputs).getOrDefault(FluidRecipeCapability.CAP,
                List.of());
        if (slot.index() >= contents.size()) return List.of();
        FluidIngredient ingredient = FluidRecipeCapability.CAP.of(contents.get(slot.index()).content);
        List<FluidStack> out = new ArrayList<>();
        for (var stack : ingredient.getStacks()) out.add(FluidStack.create(stack.getFluid(), stack.getAmount()));
        return out;
    }

    /** The machine lines with the recipe the tab would show, for the GameTests. */
    public static List<Component> machineLinesForTests(TokamakMachine machine) {
        var recipe = machine.getRecipeLogic().getLastRecipe();
        return machineLines(machine, recipe == null ? machine.defaultRecipe() : recipe);
    }

    /** EU/t a recipe gives, for the GameTests. */
    public static long outputEUt(GTRecipe recipe) {
        var eu = RecipeHelper.getRealEUtWithIO(recipe);
        return eu.isInput() ? 0 : eu.voltage() * eu.amperage();
    }

    public static void init() {}
}
