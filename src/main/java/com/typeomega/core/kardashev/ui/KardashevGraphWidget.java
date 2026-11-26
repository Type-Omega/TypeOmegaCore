package com.typeomega.core.kardashev.ui;

import com.lowdragmc.lowdraglib.gui.widget.Widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.typeomega.core.kardashev.KardashevHistory;
import com.typeomega.core.kardashev.KardashevUiText;

import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * The History graph (D-443): generated (green bars) and spent (red line) in EU/s over the window the player picked.
 * The server sends the three windows, 1.3 KB, when the screen opens and again every 5 seconds while the tab is
 * showing; the window buttons only change which of them is drawn, so they need no packet.
 */
public final class KardashevGraphWidget extends Widget {

    public static final int GRAPH_HEIGHT = 96;
    public static final int HEIGHT = GRAPH_HEIGHT + 44;
    private static final int AXIS_WIDTH = 52;
    private static final int SEND_TICKS = 100;
    private static final int UPDATE = 1;
    private static final int TEXT = 0xFF2B2B2B;

    private final Supplier<KardashevHistory.Snapshot> server;
    private final LongSupplier clock;
    private KardashevHistory.Snapshot snapshot = KardashevHistory.Snapshot.EMPTY;
    private long sentAt = Long.MIN_VALUE;
    private int window = KardashevHistory.Window.SIX_HOURS.ordinal();

    public KardashevGraphWidget(int x, int y, int width, Supplier<KardashevHistory.Snapshot> server,
                                LongSupplier clock) {
        super(x, y, width, HEIGHT);
        this.server = server;
        this.clock = clock;
    }

    public void setWindow(int window) {
        this.window = Math.max(0, Math.min(KardashevHistory.Window.values().length - 1, window));
    }

    public int window() {
        return window;
    }

    public KardashevHistory.Snapshot snapshot() {
        return snapshot;
    }

    @Override
    public void writeInitialData(FriendlyByteBuf buffer) {
        super.writeInitialData(buffer);
        snapshot = server.get();
        sentAt = clock.getAsLong();
        snapshot.write(buffer);
    }

    @Override
    public void readInitialData(FriendlyByteBuf buffer) {
        super.readInitialData(buffer);
        snapshot = KardashevHistory.Snapshot.read(buffer);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        long now = clock.getAsLong();
        if (sentAt != Long.MIN_VALUE && now >= sentAt && now - sentAt < SEND_TICKS) return;
        sentAt = now;
        snapshot = server.get();
        var current = snapshot;
        writeUpdateInfo(UPDATE, current::write);
    }

    @Override
    public void readUpdateInfo(int id, FriendlyByteBuf buffer) {
        if (id == UPDATE) snapshot = KardashevHistory.Snapshot.read(buffer);
        else super.readUpdateInfo(id, buffer);
    }

    /** Smallest 1, 2 or 5 times a power of ten that holds {@code value}; the top of the vertical axis. */
    static double niceMax(double value) {
        if (!(value > 0)) return 1;
        double base = Math.pow(10, Math.floor(Math.log10(value)));
        for (double step : new double[] { 1, 2, 5, 10 }) {
            if (value <= base * step) return base * step;
        }
        return base * 10;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        var font = Minecraft.getInstance().font;
        float[] generated = snapshot.generated()[window], spent = snapshot.spent()[window];
        int x = getPositionX(), y = getPositionY(), w = getSizeWidth();
        int left = x + AXIS_WIDTH, plotWidth = w - AXIS_WIDTH, bottom = y + GRAPH_HEIGHT;
        double peakGenerated = 0, peakSpent = 0, sumGenerated = 0, sumSpent = 0;
        for (int i = 0; i < generated.length; i++) {
            peakGenerated = Math.max(peakGenerated, generated[i]);
            peakSpent = Math.max(peakSpent, spent[i]);
            sumGenerated += generated[i];
            sumSpent += spent[i];
        }
        double top = niceMax(Math.max(peakGenerated, peakSpent));
        graphics.fill(left, y, left + plotWidth, bottom, 0xFF1E1E1E);
        for (int quarter = 1; quarter < 4; quarter++) {
            graphics.fill(left, bottom - GRAPH_HEIGHT * quarter / 4, left + plotWidth,
                    bottom - GRAPH_HEIGHT * quarter / 4 + 1, 0xFF3A3A3A);
        }
        int n = generated.length, previousSpentY = -1;
        for (int i = 0; i < n; i++) {
            int x0 = left + i * plotWidth / n, x1 = Math.max(x0 + 1, left + (i + 1) * plotWidth / n - 1);
            int height = (int) Math.round(generated[i] / top * (GRAPH_HEIGHT - 1));
            if (generated[i] > 0) height = Math.max(1, height);
            graphics.fill(x0, bottom - height, x1, bottom, 0xFF2F7D3A);
            if (height > 0) graphics.fill(x0, bottom - height, x1, bottom - height + 1, 0xFF6FE07C);
            int spentY = bottom - 1 - (int) Math.round(spent[i] / top * (GRAPH_HEIGHT - 2));
            if (spent[i] > 0) {
                graphics.fill(x0, spentY, x1, spentY + 2, 0xFFE05555);
                if (previousSpentY >= 0) {
                    graphics.fill(x0, Math.min(spentY, previousSpentY), x0 + 1,
                            Math.max(spentY, previousSpentY) + 2, 0xFFE05555);
                }
                previousSpentY = spentY;
            } else {
                previousSpentY = -1;
            }
        }
        graphics.drawString(font, KardashevUiText.power(top), x, y, TEXT, false);
        graphics.drawString(font, KardashevUiText.power(0), x, bottom - 8, TEXT, false);
        if (sumGenerated == 0 && sumSpent == 0) {
            Component empty = KardashevUiText.tr("graph.empty");
            graphics.drawString(font, empty, left + (plotWidth - font.width(empty)) / 2, y + GRAPH_HEIGHT / 2 - 4,
                    0xFFAAAAAA, false);
        }
        Component ago = Component.literal("-").append(KardashevUiText.tr("graph.window." + window));
        Component now = KardashevUiText.tr("graph.now");
        graphics.drawString(font, ago, left, bottom + 2, TEXT, false);
        graphics.drawString(font, now, left + plotWidth - font.width(now), bottom + 2, TEXT, false);
        graphics.drawString(font, KardashevUiText.tr("graph.average", KardashevUiText.power(sumGenerated / n),
                KardashevUiText.power(sumSpent / n)), x, bottom + 16, TEXT, false);
        graphics.drawString(font, KardashevUiText.tr("graph.peak", KardashevUiText.power(peakGenerated),
                KardashevUiText.power(peakSpent)), x, bottom + 28, TEXT, false);
    }
}
