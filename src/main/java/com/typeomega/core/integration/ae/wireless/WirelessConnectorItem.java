package com.typeomega.core.integration.ae.wireless;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import appeng.block.AEBaseBlockItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Item of the ME Wireless Connector block. */
public final class WirelessConnectorItem extends AEBaseBlockItem {

    WirelessConnectorItem(WirelessConnectorBlock block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void addCheckedInformation(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag flag) {
        super.addCheckedInformation(stack, level, lines, flag);
        lines.add(Component.translatable(WirelessLang.CONNECTOR_DESC).withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable(WirelessLang.CONNECTOR_CHANNELS).withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable(WirelessLang.CONNECTOR_ONE).withStyle(ChatFormatting.DARK_GRAY));
    }
}
