package com.typeomega.core.integration.ae.interfaces;

import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Nameable;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.parts.PartModels;
import appeng.api.stacks.AEKeyType;
import appeng.api.upgrades.Upgrades;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.helpers.InterfaceLogicHost;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.parts.PartModel;
import com.glodblock.github.extendedae.container.ContainerExInterface;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.typeomega.core.GTIAMod;
import com.typeomega.core.integration.ae.AeLang;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * ME Interface (II) to (V), as block and as cable part (GTIA-D-442). Registers the blocks, block entities, part items,
 * part models, the shared menu and the cards the tiers accept; the English names are generated, pt_br and zh_cn are in
 * lang.
 */
public final class InterfaceTiers {

    /** The three states of the part model: unpowered, powered, powered with a channel. */
    public record PartModelSet(PartModel off, PartModel on, PartModel hasChannel) {}

    private static final Map<InterfaceTier, BlockEntry<TieredInterfaceBlock>> BLOCKS = new EnumMap<>(
            InterfaceTier.class);
    private static final Map<InterfaceTier, BlockEntityEntry<TieredInterfaceEntity>> TYPES = new EnumMap<>(
            InterfaceTier.class);
    private static final Map<InterfaceTier, ItemEntry<TieredInterfacePartItem>> PARTS = new EnumMap<>(
            InterfaceTier.class);
    private static final Map<InterfaceTier, PartModelSet> MODELS = new EnumMap<>(InterfaceTier.class);

    // EN: One menu type for all tiers, built through AE2's builder so it registers with AE2's own menu types.
    // PT: Um só tipo de menu para todos os tiers, montado pelo builder do AE2 para registrar com os menus do AE2.
    // ZH: 所有层级共用一个菜单类型，通过 AE2 的构建器创建，随 AE2 自己的菜单类型一起注册。
    public static final MenuType<ContainerExInterface> MENU = MenuTypeBuilder
            .create(ContainerExInterface::new, InterfaceLogicHost.class)
            .withMenuTitle(InterfaceTiers::title)
            .build("typeomega_tiered_interface");

    static {
        for (InterfaceTier tier : InterfaceTier.values()) register(tier);
    }

    private InterfaceTiers() {}

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(InterfaceTiers::onCommonSetup);
    }

    private static void register(InterfaceTier tier) {
        String roman = tier.name();
        var block = GTIAMod.REGISTRATE.block(tier.blockId(), p -> new TieredInterfaceBlock(tier))
                .lang("ME Interface (" + roman + ")")
                .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .blockstate((ctx, prov) -> {})
                .loot((tables, b) -> tables.dropSelf(b))
                .item((b, p) -> new TieredInterfaceBlockItem(b, p))
                .model((ctx, prov) -> {})
                .build()
                .register();
        BLOCKS.put(tier, block);
        TYPES.put(tier, GTIAMod.REGISTRATE.blockEntity(tier.blockId(), TieredInterfaceEntity::new)
                .validBlock(block)
                .register());
        PARTS.put(tier, GTIAMod.REGISTRATE.item(tier.partId(), p -> new TieredInterfacePartItem(p, tier))
                .lang("ME Interface (" + roman + ")")
                .model((ctx, prov) -> {})
                .register());
        ResourceLocation front = GTIAMod.id("part/" + tier.blockId());
        MODELS.put(tier, new PartModelSet(
                new PartModel(front, new ResourceLocation("ae2", "part/interface_off")),
                new PartModel(front, new ResourceLocation("ae2", "part/interface_on")),
                new PartModel(front, new ResourceLocation("ae2", "part/interface_has_channel"))));
        PartModels.registerModels(front);
    }

    public static TieredInterfaceBlock block(InterfaceTier tier) {
        return BLOCKS.get(tier).get();
    }

    public static TieredInterfacePartItem part(InterfaceTier tier) {
        return PARTS.get(tier).get();
    }

    public static ItemLike blockItem(InterfaceTier tier) {
        return BLOCKS.get(tier).get();
    }

    public static PartModelSet partModels(InterfaceTier tier) {
        return MODELS.get(tier);
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            for (InterfaceTier tier : InterfaceTier.values()) {
                var type = TYPES.get(tier).get();
                var block = block(tier);
                block.setBlockEntity(TieredInterfaceEntity.class, type, null, null);
                AEBaseBlockEntity.registerBlockEntityItem(type, block.asItem());
                for (ItemLike machine : List.of(block, part(tier))) {
                    Upgrades.add(AEItems.FUZZY_CARD, machine, 1, GuiText.Interface.getTranslationKey());
                    Upgrades.add(AEItems.CRAFTING_CARD, machine, 1, GuiText.Interface.getTranslationKey());
                }
            }
        });
    }

    private static Component title(InterfaceLogicHost host) {
        if (host instanceof Nameable nameable && nameable.hasCustomName()) return nameable.getCustomName();
        return host.getMainMenuIcon().getHoverName();
    }

    /** Slots and capacity of a tier, from the live AE2 slot capacities. */
    public static void addTooltip(InterfaceTier tier, List<Component> lines) {
        long items = GenericSlotCapacities.getMap().getOrDefault(AEKeyType.items(), 0L) * tier.multiplier;
        long buckets = GenericSlotCapacities.getMap().getOrDefault(AEKeyType.fluids(), 0L) * tier.multiplier /
                AEKeyType.fluids().getAmountPerUnit();
        lines.add(Component.translatable(AeLang.SLOTS, InterfaceTier.SLOTS, InterfaceTier.SLOTS)
                .withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable(AeLang.CAPACITY, FormattingUtil.formatNumbers(tier.multiplier),
                FormattingUtil.formatNumbers(items), FormattingUtil.formatNumbers(buckets))
                .withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable(AeLang.UPGRADE).withStyle(ChatFormatting.DARK_GRAY));
    }
}
