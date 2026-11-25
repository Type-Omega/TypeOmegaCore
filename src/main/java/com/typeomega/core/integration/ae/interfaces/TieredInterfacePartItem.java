package com.typeomega.core.integration.ae.interfaces;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import appeng.items.parts.PartItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Item of the ME Interface (II) to (V) cable part. */
public final class TieredInterfacePartItem extends PartItem<TieredInterfacePart> {

    private final InterfaceTier tier;

    TieredInterfacePartItem(Properties properties, InterfaceTier tier) {
        super(properties, TieredInterfacePart.class, TieredInterfacePart::new);
        this.tier = tier;
    }

    public InterfaceTier tier() {
        return tier;
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        InteractionResult upgraded = InterfaceUpgrade.part(stack, context, this);
        return upgraded != null ? upgraded : super.onItemUseFirst(stack, context);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, level, lines, flag);
        InterfaceTiers.addTooltip(tier, lines);
    }
}
