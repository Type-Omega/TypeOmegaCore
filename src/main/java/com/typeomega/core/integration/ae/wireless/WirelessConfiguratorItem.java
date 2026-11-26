package com.typeomega.core.integration.ae.wireless;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The ME Wireless Configurator: carries a network of the player's team and binds ME parts and connectors to it. The
 * network is chosen by right-clicking the air (cycling the team's networks, none included) or created by
 * sneak-right-clicking the air, named like the item. On a block it binds, or with none chosen unbinds; sneaking on a
 * bound block reads its network.
 */
public final class WirelessConfiguratorItem extends Item {

    public static final String ID_KEY = "TypeOmegaWirelessNetwork";
    static final String NAME_KEY = "TypeOmegaWirelessNetworkName";

    public WirelessConfiguratorItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static @Nullable String selectedId(ItemStack stack) {
        var tag = stack.getTag();
        return tag != null && tag.contains(ID_KEY) ? tag.getString(ID_KEY) : null;
    }

    static void select(ItemStack stack, @Nullable WirelessNetwork network) {
        if (network == null) {
            stack.removeTagKey(ID_KEY);
            stack.removeTagKey(NAME_KEY);
            return;
        }
        stack.getOrCreateTag().putString(ID_KEY, network.id());
        stack.getOrCreateTag().putString(NAME_KEY, network.name());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide() || !(player instanceof ServerPlayer server)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        WirelessData data = WirelessData.get(server.server);
        var team = WirelessTeams.teamOf(server.getUUID());
        if (player.isShiftKeyDown()) {
            WirelessNetwork made = data.create(team,
                    stack.hasCustomHoverName() ? stack.getHoverName().getString() : "");
            select(stack, made);
            server.displayClientMessage(Component.translatable(WirelessLang.CREATED, made.name()), true);
        } else {
            WirelessNetwork next = nextOf(data.of(team), selectedId(stack));
            select(stack, next);
            server.displayClientMessage(next == null ? Component.translatable(WirelessLang.SELECTED_NONE) :
                    Component.translatable(WirelessLang.SELECTED, next.name()), true);
        }
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    /** The network after the current one in the team's list, then none, then the first again. */
    static @Nullable WirelessNetwork nextOf(List<WirelessNetwork> own, @Nullable String current) {
        List<WirelessNetwork> ring = new ArrayList<>();
        ring.add(null);
        ring.addAll(own);
        int at = 0;
        for (int i = 1; i < ring.size(); i++) if (ring.get(i).id().equals(current)) at = i;
        return ring.get((at + 1) % ring.size());
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        if (!(context.getPlayer() instanceof ServerPlayer player) || !(level instanceof ServerLevel server)) {
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        var pos = context.getClickedPos();
        WirelessActions.Result result;
        if (player.isShiftKeyDown()) {
            result = WirelessActions.read(player, server, pos);
            if (result.ok()) select(stack, result.network());
        } else {
            String chosen = selectedId(stack);
            WirelessNetwork network = chosen == null ? null : WirelessData.get(server.getServer()).network(chosen);
            if (chosen == null) {
                result = WirelessActions.unbind(player, server, pos);
            } else if (network == null) {
                select(stack, null);
                result = WirelessActions.Result.of(false, WirelessLang.NETWORK_GONE);
            } else {
                result = WirelessActions.bind(player, server, pos, network);
            }
        }
        player.displayClientMessage(result.message(), true);
        return InteractionResult.sidedSuccess(false);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return selectedId(stack) != null;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag flag) {
        var tag = stack.getTag();
        lines.add((tag != null && tag.contains(NAME_KEY) ? Component.translatable(WirelessLang.SELECTED,
                tag.getString(NAME_KEY)) : Component.translatable(WirelessLang.SELECTED_NONE))
                .withStyle(ChatFormatting.AQUA));
        lines.add(Component.translatable(WirelessLang.CONFIGURATOR_USE).withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable(WirelessLang.CONFIGURATOR_BIND).withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable(WirelessLang.CONFIGURATOR_READ).withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable(WirelessLang.CONFIGURATOR_TEAM).withStyle(ChatFormatting.DARK_GRAY));
    }
}
