package com.typeomega.core.station;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * The containers next to the station on the left, one tab per container above them, the crafting table on the right
 * (as in the GTNH Tinkers' Construct station). Without containers the panel explains how to add one.
 */
public class CraftingStationScreen extends AbstractContainerScreen<CraftingStationMenu> {

    private static final ResourceLocation CRAFTING = new ResourceLocation("textures/gui/container/crafting_table.png");
    private static final ResourceLocation CHEST = new ResourceLocation("textures/gui/container/generic_54.png");
    private static final int PANEL_TEXT_WIDTH = 160;

    private Button previous;
    private Button next;

    public CraftingStationScreen(CraftingStationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = menu.craftX() + 176;
        this.imageHeight = 166;
        this.titleLabelX = menu.craftX() + 29;
        this.inventoryLabelX = menu.craftX() + 8;
        this.inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        var sides = menu.sides();
        for (int i = 0; i < sides.size(); i++) {
            int index = i;
            var side = sides.get(i);
            addRenderableWidget(Button.builder(Component.empty(), b -> click(index))
                    .bounds(leftPos + i * 24, topPos - 22, 22, 22)
                    .tooltip(Tooltip.create(Component.translatable("typeomega.crafting_station.tab", side.name(),
                            Component.translatable("typeomega.crafting_station.side." +
                                    side.direction().getName()))))
                    .build());
        }
        previous = addRenderableWidget(Button.builder(Component.literal("<"),
                b -> click(CraftingStationMenu.BUTTON_PREVIOUS_PAGE))
                .bounds(leftPos + 120, topPos + 128, 16, 14).build());
        next = addRenderableWidget(Button.builder(Component.literal(">"),
                b -> click(CraftingStationMenu.BUTTON_NEXT_PAGE))
                .bounds(leftPos + 152, topPos + 128, 16, 14).build());
        if (!sides.isEmpty()) {
            addRenderableWidget(Button.builder(Component.literal("⇓"), b -> click(CraftingStationMenu.BUTTON_DUMP))
                    .bounds(leftPos + menu.craftX() + 158, topPos + 4, 12, 12)
                    .tooltip(Tooltip.create(Component.translatable("typeomega.crafting_station.dump")))
                    .build());
        }
    }

    private void click(int id) {
        if (minecraft != null && minecraft.gameMode != null && menu.clickMenuButton(minecraft.player, id)) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        boolean paged = menu.pageCount() > 1;
        previous.visible = paged;
        next.visible = paged;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(CRAFTING, leftPos + menu.craftX(), topPos, 0, 0, 176, 166);
        // EN: Without containers only the chest title strip is drawn, so the hint never sits on empty slots.
        // PT: Sem contêineres só a faixa do título do baú é desenhada, para a dica não ficar sobre slots vazios.
        // ZH: 没有容器时只绘制箱子标题条，提示文字不会压在空槽位上。
        int rowsEnd = menu.sides().isEmpty() ? 17 : 17 + CraftingStationMenu.ROWS * 18;
        graphics.blit(CHEST, leftPos, topPos, 0, 0, 176, rowsEnd);
        for (int y = rowsEnd; y < 159; y += 10) {
            graphics.blit(CHEST, leftPos, topPos + y, 0, 5, 176, Math.min(10, 159 - y));
        }
        graphics.blit(CHEST, leftPos, topPos + 159, 0, 215, 176, 7);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        var sides = menu.sides();
        if (sides.isEmpty()) {
            graphics.drawWordWrap(font, Component.translatable("typeomega.crafting_station.empty"), 8, 20,
                    PANEL_TEXT_WIDTH, 0x404040);
            return;
        }
        var side = sides.get(Math.min(menu.selectedTab(), sides.size() - 1));
        Component label = Component.translatable("typeomega.crafting_station.tab", side.name(),
                Component.translatable("typeomega.crafting_station.side." + side.direction().getName()));
        graphics.drawString(font, font.plainSubstrByWidth(label.getString(), PANEL_TEXT_WIDTH), 8, 6, 0x404040,
                false);
        if (menu.pageCount() > 1) {
            String pages = (menu.selectedPage() + 1) + "/" + menu.pageCount();
            graphics.drawString(font, pages, 144 - font.width(pages) / 2, 131, 0x404040, false);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        var sides = menu.sides();
        for (int i = 0; i < sides.size(); i++) {
            int tx = leftPos + i * 24 + 3;
            int ty = topPos - 19;
            graphics.renderItem(sides.get(i).icon(), tx, ty);
            if (i == menu.selectedTab()) graphics.fill(tx - 2, ty + 17, tx + 18, ty + 19, 0xFF55FF55);
        }
        renderTooltip(graphics, mouseX, mouseY);
    }
}
