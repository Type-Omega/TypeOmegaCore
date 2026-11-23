package com.typeomega.core;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialRegistryEvent;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.typeomega.core.data.GTIAAlloys;
import com.typeomega.core.data.GTIABeeMachines;
import com.typeomega.core.data.GTIAItems;
import com.typeomega.core.data.GTIALang;
import com.typeomega.core.data.GTIARecipeTypes;
import com.typeomega.core.rules.GTIARulesNetwork;
import com.typeomega.core.rules.GTIASteamRules;
import com.typeomega.core.rules.WorldRules;
import com.typeomega.core.tools.RecipeExport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(GTIAMod.MOD_ID)
@SuppressWarnings("removal")
public final class GTIAMod {

    public static final String MOD_ID = "typeomega";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final GTRegistrate REGISTRATE = GTRegistrate.create(MOD_ID);

    public GTIAMod() {
        GTIALang.init();
        com.typeomega.core.mega.GTIAMegaLang.init();
        com.typeomega.core.mega.common.MegaLang.init();
        com.typeomega.core.rules.GTIARuleEffects.applyStartupRules();
        GTIAItems.init();
        com.typeomega.core.organ.GTIAOrgans.init();
        MinecraftForge.EVENT_BUS.addListener(com.typeomega.core.rules.GTIASurvivalRules::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(com.typeomega.core.mega.system.SystemWipe::onServerAboutToStart);
        com.typeomega.core.station.GTIAStation.init();
        com.typeomega.core.kardashev.KardashevMeter.init();
        com.typeomega.core.data.rocket.GTIARocketBlocks.init();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        com.typeomega.core.miner.GTIAMiners.registerSerializers(modBus);
        com.typeomega.core.loot.GTOreFortune.register(modBus);
        modBus.addListener(this::registerMaterialRegistry);
        modBus.addListener(this::sendCraftingSources);
        modBus.addListener(this::registerMaterials);
        modBus.addGenericListener(GTRecipeType.class, this::registerRecipeTypes);
        modBus.addGenericListener(MachineDefinition.class, this::registerMachines);
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist.isClient() &&
                com.typeomega.core.mega.GTIAMegastructures.gtna())
            modBus.addListener(com.typeomega.core.mega.common.client.MegaFxShaders::register);
        REGISTRATE.registerRegistrate();
        MinecraftForge.EVENT_BUS.addListener(
                (net.minecraftforge.event.server.ServerStartedEvent e) -> WorldRules.onServerStarted(e.getServer()));
        MinecraftForge.EVENT_BUS.addListener(com.typeomega.core.data.RecipeAuditCommand::register);
        if (net.minecraftforge.fml.ModList.get().isLoaded("productivebees")) {
            MinecraftForge.EVENT_BUS.addListener(com.typeomega.core.data.ProductiveBeesRecipeFilter::onServerStarted);
            MinecraftForge.EVENT_BUS.addListener(com.typeomega.core.data.ProductiveBeesRecipeFilter::onDatapackSync);
        }
        if (net.minecraftforge.fml.ModList.get().isLoaded("ae2")) {
            MinecraftForge.EVENT_BUS.addListener(com.typeomega.core.route.AE2Gates::onServerStarted);
            MinecraftForge.EVENT_BUS.addListener(com.typeomega.core.route.AE2Gates::onDatapackSync);
        }
        if (net.minecraftforge.fml.ModList.get().isLoaded("ad_astra")) {
            MinecraftForge.EVENT_BUS
                    .addListener(com.typeomega.core.data.rocket.AdAstraRecipeFilter::onServerStarted);
            MinecraftForge.EVENT_BUS
                    .addListener(com.typeomega.core.data.rocket.AdAstraRecipeFilter::onDatapackSync);
        }
        GTIARulesNetwork.register();
        com.typeomega.core.mega.system.SkyStarNetwork.register();
        MinecraftForge.EVENT_BUS.addListener(com.typeomega.core.mega.system.SkyStarNetwork::onServerTick);
        if (net.minecraftforge.fml.ModList.get().isLoaded("gtna")) GTIASteamRules.install();
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist.isClient()) {
            MinecraftForge.EVENT_BUS.addListener(
                    (net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut e) -> WorldRules.leaveWorld());
            MinecraftForge.EVENT_BUS.addListener(com.typeomega.core.client.GTIARulesClient::onRegisterCommands);
            MinecraftForge.EVENT_BUS.addListener(com.typeomega.core.client.GTIARulesClient::onClientTick);
            if (com.typeomega.core.mega.GTIAMegastructures.available()) {
                // The dynamic render type has to exist before the machine models load.
                var ignored = com.typeomega.core.mega.common.client.MegaRenderer.Render.TYPE;
                MinecraftForge.EVENT_BUS.addListener(
                        com.typeomega.core.mega.common.client.MegaRenderer::onRenderLevelStage);
                MinecraftForge.EVENT_BUS.addListener(
                        com.typeomega.core.mega.common.client.MegaSkyStar::onRenderLevelStage);
                MinecraftForge.EVENT_BUS.addListener(
                        com.typeomega.core.mega.common.client.MegaDevCommand::register);
            }
            // Same test as GTIARulesSelfTest.ENABLED, read here: that class touches EMI, which may be absent.
            if (Boolean.getBoolean("typeomega.rulesSelfTest") || java.nio.file.Files.exists(
                    java.nio.file.Path.of("gtia_rules_selftest"))) {
                MinecraftForge.EVENT_BUS.addListener(com.typeomega.core.client.GTIARulesSelfTest::onClientTick);
            }
        }
        MinecraftForge.EVENT_BUS
                .addListener((net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent e) -> {
                    if (e.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
                        GTIARulesNetwork.sendTo(player);
                        com.typeomega.core.rules.ModeStage.apply(player);
                    }
                    String warning = WorldRules.takeWarning();
                    if (warning != null) {
                        e.getEntity().sendSystemMessage(net.minecraft.network.chat.Component.translatable(warning,
                                WorldRules.active().preset().id()).withStyle(net.minecraft.ChatFormatting.GOLD));
                    }
                });
        if (System.getProperty(RecipeExport.PROPERTY) != null) {
            MinecraftForge.EVENT_BUS.addListener(RecipeExport::onServerStarted);
        }
    }

    /**
     * Crafting Chain (optional) crafts from the containers around an open Crafting Station, the same five sides the
     * station shows. Sent over IMC, so GTIACore needs no compile dependency on it.
     */
    private void sendCraftingSources(net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent event) {
        if (!net.minecraftforge.fml.ModList.get().isLoaded("craftingchain")) return;
        net.minecraftforge.fml.InterModComms.sendTo("craftingchain", "sources",
                () -> (java.util.function.Function<net.minecraft.server.level.ServerPlayer, java.util.List<net.minecraftforge.items.IItemHandler>>) player -> player.containerMenu instanceof com.typeomega.core.station.CraftingStationMenu station ?
                        station.sides().stream()
                                .map(com.typeomega.core.station.CraftingStationMenu.Side::handler)
                                .toList() :
                        java.util.List.of());
    }

    private void registerMaterialRegistry(MaterialRegistryEvent event) {
        GTCEuAPI.materialManager.createRegistry(MOD_ID);
    }

    private void registerMaterials(MaterialEvent event) {
        GTIAAlloys.register();
        com.typeomega.core.chem.ChemMaterials.register();
        com.typeomega.core.route.RouteMaterials.register();
        com.typeomega.core.chem.ZipDMaterials.register();
        com.typeomega.core.data.rocket.SpaceMaterials.register();
        com.typeomega.core.mega.MegaMaterials.register();
    }

    private void registerRecipeTypes(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {
        GTIARecipeTypes.init();
    }

    private void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        GTIABeeMachines.init();
        com.typeomega.core.miner.GTIAMiners.init();
        com.typeomega.core.miner.FragmentCollectors.init();
        com.typeomega.core.processing.GTIAProcessing.init();
        com.typeomega.core.data.rocket.GTIARocketAssembler.init();
        com.typeomega.core.mega.GTIAMegastructures.init();
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
