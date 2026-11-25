package com.typeomega.core.integration.ae.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import appeng.helpers.InterfaceLogic;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import com.glodblock.github.extendedae.common.tileentities.TileExInterface;

/** Block entity of ME Interface (II) to (V): the 36-slot Extended Interface with the rows of its tier. */
public final class TieredInterfaceEntity extends TileExInterface {

    public TieredInterfaceEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public InterfaceTier tier() {
        return ((TieredInterfaceBlock) getBlockState().getBlock()).tier();
    }

    // EN: Called from the AE2 constructor, before any field of this class exists; the tier comes from the block state.
    // PT: Chamado pelo construtor do AE2, antes de qualquer campo desta classe existir; o tier vem do estado do bloco.
    // ZH: 由 AE2 构造函数调用，此时本类的字段尚不存在；tier 取自方块状态。
    @Override
    protected InterfaceLogic createLogic() {
        InterfaceLogic logic = new InterfaceLogic(getMainNode(), this, getBlockState().getBlock().asItem(),
                InterfaceTier.SLOTS);
        InterfaceLogicTier.apply(logic, ((TieredInterfaceBlock) getBlockState().getBlock()).tier());
        return logic;
    }

    @Override
    public void openMenu(Player player, MenuLocator locator) {
        MenuOpener.open(InterfaceTiers.MENU, player, locator);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(InterfaceTiers.MENU, player, subMenu.getLocator());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(getBlockState().getBlock());
    }
}
