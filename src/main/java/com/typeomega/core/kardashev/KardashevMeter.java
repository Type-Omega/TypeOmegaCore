package com.typeomega.core.kardashev;

import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.typeomega.core.GTIAMod;

/** Registration of the Kardashev Meter (GTIA-D-372, D-380, D-389, D-406) and of the team K bookkeeping. */
public final class KardashevMeter {

    public static final BlockEntry<KardashevMeterBlock> BLOCK = GTIAMod.REGISTRATE
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
        MinecraftForge.EVENT_BUS.addListener(KardashevMeter::registerCommands);
    }

    /**
     * {@code /typeomega kardashev megastructures <player> <count>} (operators): sets the megastructures of the
     * player's team until the Tipo 0 megastructures (GTIA-D-390) exist and report themselves.
     */
    private static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("typeomega").requires(s -> s.hasPermission(2))
                .then(Commands.literal("kardashev").then(Commands.literal("megastructures")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("count", IntegerArgumentType.integer(0,
                                        KardashevScale.MAX_MEGASTRUCTURES)).executes(ctx -> {
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            int count = IntegerArgumentType.getInteger(ctx, "count");
                                            var server = ctx.getSource().getServer();
                                            KardashevData.get(server).setMegastructures(
                                                    KardashevLedger.teamOf(player.getUUID()), count,
                                                    server.overworld().getGameTime());
                                            ctx.getSource().sendSuccess(() -> KardashevText.tr(
                                                    "command.megastructures", player.getDisplayName(), count,
                                                    KardashevText.decimal(KardashevScale.capHundredths(count))),
                                                    true);
                                            return count;
                                        }))))));
    }
}
