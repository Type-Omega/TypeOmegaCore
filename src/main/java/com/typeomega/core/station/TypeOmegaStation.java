package com.typeomega.core.station;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Blocks;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.typeomega.core.TypeOmegaMod;

/** Registration of the Type Omega Crafting Station. */
public final class TypeOmegaStation {

    public static final BlockEntry<CraftingStationBlock> BLOCK = TypeOmegaMod.REGISTRATE
            .block("crafting_station", CraftingStationBlock::new)
            .initialProperties(() -> Blocks.CRAFTING_TABLE)
            .lang("Crafting Station")
            // Model, blockstate and recipe are hand-written resources (vanilla crafting table textures).
            .blockstate((ctx, prov) -> {})
            .loot((tables, block) -> tables.dropSelf(block))
            .item()
            .model((ctx, prov) -> {})
            .build()
            .register();

    public static final BlockEntityEntry<CraftingStationBlockEntity> BLOCK_ENTITY = TypeOmegaMod.REGISTRATE
            .blockEntity("crafting_station", CraftingStationBlockEntity::new)
            .validBlock(BLOCK)
            .register();

    public static final MenuEntry<CraftingStationMenu> MENU = TypeOmegaMod.REGISTRATE
            .menu("crafting_station", (MenuType<CraftingStationMenu> type, int id,
                                       net.minecraft.world.entity.player.Inventory inventory,
                                       net.minecraft.network.FriendlyByteBuf buf) -> CraftingStationMenu
                                               .fromNetwork(type, id, inventory, buf),
                    () -> CraftingStationScreen::new)
            .register();

    private TypeOmegaStation() {}

    public static void init() {}
}
