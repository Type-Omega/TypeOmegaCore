package com.typeomega.core.kardashev.ui;

import com.lowdragmc.lowdraglib.gui.factory.UIFactory;
import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.kardashev.KardashevMeter;

/** Opens the Kardashev Meter screen of a block (D-443); the meter has no block entity, so the holder is a position. */
public final class KardashevUIFactory extends UIFactory<KardashevUIFactory.Holder> {

    public static final KardashevUIFactory INSTANCE = new KardashevUIFactory();

    private KardashevUIFactory() {
        super(TypeOmegaMod.id("kardashev_meter"));
    }

    public static void init() {
        UIFactory.register(INSTANCE);
    }

    public boolean open(ServerPlayer player, BlockPos pos) {
        return openUI(new Holder(player.level(), pos), player);
    }

    @Override
    protected ModularUI createUITemplate(Holder holder, Player player) {
        return KardashevWindow.create(holder, player);
    }

    @Override
    protected Holder readHolderFromSyncData(FriendlyByteBuf buf) {
        return new Holder(KardashevClientLevel.get(), buf.readBlockPos());
    }

    @Override
    protected void writeHolderToSyncData(FriendlyByteBuf buf, Holder holder) {
        buf.writeBlockPos(holder.pos);
    }

    /** The meter block the screen belongs to; it closes when the block is gone. */
    public static final class Holder implements IUIHolder {

        private final Level level;
        private final BlockPos pos;

        public Holder(Level level, BlockPos pos) {
            this.level = level;
            this.pos = pos;
        }

        @Override
        public ModularUI createUI(Player player) {
            return KardashevWindow.create(this, player);
        }

        @Override
        public boolean isInvalid() {
            return level == null || !level.getBlockState(pos).is(KardashevMeter.BLOCK.get());
        }

        @Override
        public boolean isRemote() {
            return level == null || level.isClientSide;
        }

        @Override
        public void markAsDirty() {}
    }
}
