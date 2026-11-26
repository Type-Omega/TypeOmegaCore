package com.typeomega.core.kardashev.ui;

import com.gregtechceu.gtceu.api.gui.GuiTextures;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Size;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import com.typeomega.core.kardashev.KardashevHistory;
import com.typeomega.core.kardashev.KardashevSource;
import com.typeomega.core.kardashev.KardashevUiText;
import com.typeomega.core.kardashev.KardashevView;
import com.typeomega.core.kardashev.client.KardashevHudClient;
import com.typeomega.core.kardashev.hud.KardashevHudConfig;
import com.typeomega.lib.ui.InfoLinesWidget;
import com.typeomega.lib.ui.ProgressBarWidget;
import com.typeomega.lib.ui.tool.ToolWindow;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The Kardashev Meter screen (D-443): a lib {@link ToolWindow} with the Summary, History and Sources tabs and, beside
 * the tabs, the HUD button. The text and the bars are the lib's {@link InfoLinesWidget} and {@link ProgressBarWidget},
 * read on the server from {@link KardashevScreenState} and synced when they change; the graph syncs its own data.
 */
public final class KardashevWindow {

    public static final int WIDTH = 320, HEIGHT = 214;
    private static final int LINE = 10;
    private static final int BLUE = 0x4A90D9, GREEN = 0x3FA34D;

    private final KardashevScreenState state;

    private KardashevWindow(Player player) {
        this.state = new KardashevScreenState(player);
    }

    public static ModularUI create(IUIHolder holder, Player player) {
        var window = new KardashevWindow(player).build();
        return new ModularUI(new Size(WIDTH, window.totalHeight()), holder, player).widget(window.build());
    }

    private ToolWindow build() {
        var window = new ToolWindow(WIDTH, HEIGHT);
        window.tab(KardashevUiText.tr("tab.summary"), summary(window.pageWidth(), window.pageHeight()));
        window.tab(KardashevUiText.tr("tab.history"), history(window.pageWidth(), window.pageHeight()));
        window.tab(KardashevUiText.tr("tab.sources"), sources(window.pageWidth(), window.pageHeight()));
        window.build().addWidget(hudButton(WIDTH - 82, 0, 82, ToolWindow.TAB_HEIGHT));
        return window;
    }

    // ------------------------------------------------------------------------------------------ summary

    private WidgetGroup summary(int w, int h) {
        var page = new WidgetGroup(0, 0, w, h);
        page.addWidget(lines(0, 0, w, 1, memo(view -> List.of(KardashevUiText.headline(view)))));
        page.addWidget(new ProgressBarWidget(0, 12, w, 9, () -> KardashevUiText.typeProgress(state.view()), BLUE));
        page.addWidget(lines(0, 23, w, 1, memo(view -> List.of(KardashevUiText.progressLabel(view)))));
        page.addWidget(lines(0, 38, w, 12, memo(KardashevUiText::summary)));
        return page;
    }

    // ------------------------------------------------------------------------------------------- sources

    private WidgetGroup sources(int w, int h) {
        var page = new WidgetGroup(0, 0, w, h);
        page.addWidget(lines(0, 0, w, 1, () -> List.of(KardashevUiText.tr("sources.generated"))));
        for (int i = 0; i < KardashevSource.GENERATED; i++) {
            var source = KardashevSource.values()[i];
            int y = 12 + i * 21;
            page.addWidget(lines(0, y, w, 1, memo(view -> List.of(KardashevUiText.sourceRow(view, source)))));
            page.addWidget(new ProgressBarWidget(0, y + 10, w, 7, () -> KardashevUiText.share(state.view(), source),
                    GREEN));
        }
        page.addWidget(lines(0, 100, w, 3, memo(KardashevUiText::sourcesSpent)));
        page.addWidget(lines(0, 134, w, 7, memo(KardashevUiText::sourcesNotes)));
        return page;
    }

    // ------------------------------------------------------------------------------------------- history

    private WidgetGroup history(int w, int h) {
        var page = new WidgetGroup(0, 0, w, h);
        var graph = new KardashevGraphWidget(0, 20, w, state::history, state::gameTime);
        int buttonWidth = 48;
        for (var window : KardashevHistory.Window.values()) {
            int index = window.ordinal();
            var button = ToolWindow.button(index * (buttonWidth + 2), 0, buttonWidth, 16,
                    KardashevUiText.tr("graph.window." + index), cd -> graph.setWindow(index));
            button.setClientSideWidget();
            page.addWidget(button);
        }
        page.addWidget(new ImageWidget(w - 168, 4, 8, 8, new com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture(
                0xFF2F7D3A)));
        page.addWidget(ToolWindow.label(w - 156, 4, KardashevUiText.tr("graph.generated")));
        page.addWidget(new ImageWidget(w - 88, 4, 8, 8, new com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture(
                0xFFE05555)));
        page.addWidget(ToolWindow.label(w - 76, 4, KardashevUiText.tr("graph.spent")));
        page.addWidget(graph);
        return page;
    }

    // ------------------------------------------------------------------------------------------ HUD button

    // EN: Left click turns the HUD on or off, right click opens the shared HUD editor, like the Flux Matrix screen.
    // The choice lives on the client, so the button is a client-side widget with no packet of its own.
    // PT: Clique esquerdo liga ou desliga o HUD, clique direito abre o editor de HUD compartilhado, como na tela do
    // Flux Matrix. A escolha vive no cliente, então o botão é um widget só do cliente, sem pacote próprio.
    // ZH: 左键开关 HUD，右键打开共享的 HUD 编辑器，与 Flux Matrix 界面一致。该选项保存在客户端，
    // 因此按钮是纯客户端控件，不需要自己的数据包。
    private static WidgetGroup hudButton(int x, int y, int w, int h) {
        var group = new WidgetGroup(x, y, w, h);
        var face = new ImageWidget(0, 0, w, h, () -> new GuiTextureGroup(GuiTextures.BUTTON,
                new TextTexture(KardashevUiText.tr(KardashevHudConfig.enabled() ? "hud.on" : "hud.off").getString())
                        .setColor(0xFFFFFFFF)));
        face.setClientSideWidget();
        group.addWidget(face);
        var click = new ButtonWidget(0, 0, w, h, com.lowdragmc.lowdraglib.gui.texture.IGuiTexture.EMPTY, cd -> {
            if (cd.button == 1) KardashevHudClient.openEditor();
            else KardashevHudClient.toggle();
        });
        click.setClientSideWidget();
        click.setHoverTooltips(KardashevUiText.tr("hud.tip"));
        group.addWidget(click);
        return group;
    }

    // EN: The text of a page is built again only when the view is (once a second), not every tick the widget asks.
    // PT: O texto de uma página é refeito só quando a view é (uma vez por segundo), não a cada tick que o widget pede.
    // ZH: 页面文本仅在视图更新时（每秒一次）重建，而不是控件每 tick 请求时都重建。
    private <T> Supplier<T> memo(Function<KardashevView, T> build) {
        var seen = new KardashevView[1];
        var value = new Object[1];
        return () -> {
            KardashevView view = state.view();
            if (view != seen[0]) {
                seen[0] = view;
                value[0] = build.apply(view);
            }
            @SuppressWarnings("unchecked")
            T cached = (T) value[0];
            return cached;
        };
    }

    private static InfoLinesWidget lines(int x, int y, int w, int max, Supplier<List<Component>> text) {
        var widget = new InfoLinesWidget(text, max);
        widget.setSelfPosition(x, y);
        widget.setSize(w, max * LINE);
        return widget;
    }
}
