package com.typeomega.core.integration.ae.interfaces;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import appeng.block.AEBaseBlockItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Item of the ME Interface (II) to (V) block. */
public final class TieredInterfaceBlockItem extends AEBaseBlockItem {

    private final TieredInterfaceBlock block;

    TieredInterfaceBlockItem(TieredInterfaceBlock block, Properties properties) {
        super(block, properties);
        this.block = block;
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        InteractionResult upgraded = InterfaceUpgrade.block(stack, context, block);
        return upgraded != null ? upgraded : super.onItemUseFirst(stack, context);
    }

    @Override
    public void addCheckedInformation(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag flag) {
        super.addCheckedInformation(stack, level, lines, flag);
        InterfaceTiers.addTooltip(block.tier(), lines);
    }
}
