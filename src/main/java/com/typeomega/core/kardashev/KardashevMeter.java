package com.typeomega.core.kardashev;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStoppedEvent;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.typeomega.core.TypeOmegaMod;

/** Registration of the Kardashev Meter (D-372, D-380, D-389, D-406) and of the team K bookkeeping. */
public final class KardashevMeter {

    public static final BlockEntry<KardashevMeterBlock> BLOCK = TypeOmegaMod.REGISTRATE
            .block("kardashev_meter", KardashevMeterBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.strength(2.0f, 6.0f).sound(SoundType.METAL).requiresCorrectToolForDrops())
            .lang("Kardashev Meter")
            // Model, blockstate and recipe are hand-written resources (GTCEu bronze casing and screen textures).
            .blockstate((ctx, prov) -> {})
            .loot((tables, block) -> tables.dropSelf(block))
            .tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE)
            .item()
            .model((ctx, prov) -> {})
            .build()
            .register();

    private KardashevMeter() {}

    public static void init() {
        KardashevText.registerLang();
        MinecraftForge.EVENT_BUS.addListener(KardashevLedger::onServerTick);
        MinecraftForge.EVENT_BUS.addListener((ServerStoppedEvent e) -> KardashevLedger.clear());
    }
}
