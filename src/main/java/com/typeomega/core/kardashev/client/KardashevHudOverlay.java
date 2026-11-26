package com.typeomega.core.kardashev.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.kardashev.KardashevUiText;
import com.typeomega.core.kardashev.hud.KardashevHudConfig;
import com.typeomega.core.kardashev.hud.KardashevHudNet;
import com.typeomega.lib.ui.hud.MoveableHud;

/**
 * The Kardashev HUD (D-443): three lines on the shared lib HUD editor, drawn like the Flux Matrix HUD of GTNA (same
 * panel, same colours), so the editor and its key move all of them.
 */
@OnlyIn(Dist.CLIENT)
public final class KardashevHudOverlay implements MoveableHud {

    public static final KardashevHudOverlay INSTANCE = new KardashevHudOverlay();
    public static final String HUD_ID = TypeOmegaMod.MOD_ID + ":kardashev";
    public static final int DEFAULT_X_PERCENT = 5;
    public static final int DEFAULT_Y_PERCENT = 42;

    private KardashevHudOverlay() {}

    @Override
    public String id() {
        return HUD_ID;
    }

    @Override
    public Component displayName() {
        return KardashevUiText.tr("hud.name");
    }

    @Override
    public int defaultXPercent() {
        return DEFAULT_X_PERCENT;
    }

    @Override
    public int defaultYPercent() {
        return DEFAULT_Y_PERCENT;
    }

    private static java.util.List<Component> lines() {
        var stats = KardashevHudNet.latest;
        return stats == null ? java.util.List.of() : KardashevUiText.hud(stats);
    }

    @Override
    public int width() {
        var font = Minecraft.getInstance().font;
        int width = 96;
        for (Component line : lines()) width = Math.max(width, font.width(line));
        return width + 8;
    }

    @Override
    public int height() {
        return 38;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, 0x8A404040);
        MoveableHud.drawOutline(graphics, x, y, width, height, 0xCC000000);
        var font = Minecraft.getInstance().font;
        var lines = lines();
        for (int i = 0; i < lines.size(); i++) graphics.drawString(font, lines.get(i), x + 4, y + 4 + i * 10, 0xFFFFFF);
    }

    @Override
    public boolean isEnabled() {
        return KardashevHudConfig.enabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        KardashevHudClient.setEnabled(enabled);
    }

    @Override
    public boolean shouldRenderInGame() {
        return KardashevHudNet.latest != null;
    }
}
