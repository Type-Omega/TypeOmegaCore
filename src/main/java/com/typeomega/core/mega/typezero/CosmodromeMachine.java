package com.typeomega.core.mega.typezero;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

import com.typeomega.core.data.rocket.TypeOmegaRocketAssembler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Mission control of the Cosmodrome (D-420): a controller of its own that links to a formed Rocket Assembler
 * nearby. Only with that link does it run missions and count as the second megastructure of Type 0.
 */
public class CosmodromeMachine extends TypeZeroMegaMachine {

    // EN: 48 blocks lets the control hall stand clear of the launch complex, as real control rooms do, and keeps the
    // search to at most 7 x 7 loaded chunks, done once every 5 s.
    // PT: 48 blocos deixam a sala de controle afastada do complexo de lançamento, como nas salas reais, e limitam a
    // busca a no máximo 7 x 7 chunks carregados, feita uma vez a cada 5 s.
    // ZH: 48 格让控制大厅与发射场保持距离（与真实控制室一致），且搜索最多覆盖 7 x 7 个已加载区块，每 5 秒一次。
    public static final int LINK_RANGE = 48;
    static final int LINK_RECHECK_TICKS = 100;

    private long linkCheckedAt = Long.MIN_VALUE;
    private @Nullable BlockPos assembler;

    public CosmodromeMachine(IMachineBlockEntity holder) {
        super(holder, Cosmodrome.ID);
    }

    /** The formed Rocket Assembler this mission control is linked to, or null; cached for 5 s. */
    public @Nullable BlockPos linkedAssembler() {
        if (!(getLevel() instanceof ServerLevel level)) return assembler;
        long now = level.getGameTime();
        if (linkCheckedAt != Long.MIN_VALUE && now - linkCheckedAt < LINK_RECHECK_TICKS && now >= linkCheckedAt)
            return assembler;
        linkCheckedAt = now;
        assembler = findAssembler(level, getPos());
        if (assembler != null) reportIfBuilt();
        return assembler;
    }

    /** Forgets the cached link, so the next look searches again; for the GameTests and after a rebuild. */
    public void relink() {
        linkCheckedAt = Long.MIN_VALUE;
    }

    static @Nullable BlockPos findAssembler(ServerLevel level, BlockPos origin) {
        int range2 = LINK_RANGE * LINK_RANGE;
        for (int cx = (origin.getX() - LINK_RANGE) >> 4; cx <= (origin.getX() + LINK_RANGE) >> 4; cx++)
            for (int cz = (origin.getZ() - LINK_RANGE) >> 4; cz <= (origin.getZ() + LINK_RANGE) >> 4; cz++) {
                var chunk = level.getChunkSource().getChunkNow(cx, cz);
                if (chunk == null) continue;
                for (var entity : chunk.getBlockEntities().values()) {
                    if (!(entity instanceof IMachineBlockEntity holder)) continue;
                    MetaMachine machine = holder.getMetaMachine();
                    if (machine.getDefinition() != TypeOmegaRocketAssembler.ROCKET_ASSEMBLER ||
                            !(machine instanceof IMultiController controller) || !controller.isFormed())
                        continue;
                    if (entity.getBlockPos().distSqr(origin) <= range2) return entity.getBlockPos();
                }
            }
        return null;
    }

    @Override
    protected boolean countsAsBuilt() {
        return assembler != null;
    }

    @Override
    public void onStructureFormed() {
        relink();
        super.onStructureFormed();
        linkedAssembler();
    }

    /** Recipe modifier: no mission runs without a linked Rocket Assembler. */
    public static ModifierFunction requireLink(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (machine instanceof CosmodromeMachine cosmodrome && cosmodrome.linkedAssembler() != null)
            return ModifierFunction.IDENTITY;
        return ModifierFunction.NULL;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        BlockPos pos = linkedAssembler();
        textList.add(pos == null ?
                Component.translatable("typeomega.machine.cosmodrome.unlinked", LINK_RANGE)
                        .withStyle(ChatFormatting.RED) :
                Component.translatable("typeomega.machine.cosmodrome.linked", pos.getX(), pos.getY(), pos.getZ())
                        .withStyle(ChatFormatting.GREEN));
    }
}
