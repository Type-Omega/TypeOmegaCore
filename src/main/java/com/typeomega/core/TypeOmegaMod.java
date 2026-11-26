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

import com.typeomega.core.data.TypeOmegaAlloys;
import com.typeomega.core.data.TypeOmegaBeeMachines;
import com.typeomega.core.data.TypeOmegaItems;
import com.typeomega.core.data.TypeOmegaLang;
import com.typeomega.core.data.TypeOmegaRecipeTypes;
import com.typeomega.core.tools.RecipeExport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TypeOmegaMod.MOD_ID)
@SuppressWarnings("removal")
public final class TypeOmegaMod {

    public static final String MOD_ID = "typeomega";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final GTRegistrate REGISTRATE = GTRegistrate.create(MOD_ID);

    public TypeOmegaMod() {
        TypeOmegaLang.init();
        com.typeomega.core.mega.TypeOmegaMegaLang.init();
        com.typeomega.core.mega.common.MegaLang.init();
        com.typeomega.core.balance.TypeOmegaBalance.apply();
        TypeOmegaItems.init();
        com.typeomega.core.organ.TypeOmegaOrgans.init();
        MinecraftForge.EVENT_BUS.addListener(com.typeomega.core.balance.ExtraRegeneration::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(com.typeomega.core.mega.system.SystemWipe::onServerAboutToStart);
        com.typeomega.core.station.TypeOmegaStation.init();
        com.typeomega.core.kardashev.KardashevMeter.init();
        com.typeomega.core.decor.TypeOmegaPlushies.init();
        com.typeomega.core.data.rocket.TypeOmegaRocketBlocks.init();
        com.typeomega.core.mega.typezero.TypeZeroMegas.init();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        com.typeomega.core.miner.TypeOmegaMiners.registerSerializers(modBus);
        com.typeomega.core.data.TypeOmegaCreativeTabs.register(modBus);
        com.typeomega.core.loot.GTOreFortune.register(modBus);
        modBus.addListener(this::registerMaterialRegistry);
        modBus.addListener(this::sendCraftingSources);
        modBus.addListener(this::registerMaterials);
        modBus.addGenericListener(GTRecipeType.class, this::registerRecipeTypes);
        modBus.addGenericListener(MachineDefinition.class, this::registerMachines);
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist.isClient() &&
                com.typeomega.core.mega.TypeOmegaMegastructures.gtna())
            modBus.addListener(com.typeomega.core.mega.common.client.MegaFxShaders::register);
        com.typeomega.core.integration.ae.TypeOmegaAe.init();
        com.typeomega.core.integration.ae.AeLang.init();
        com.typeomega.core.integration.ae.upload.PatternUpload.init();
        REGISTRATE.registerRegistrate();
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
        com.typeomega.core.mega.system.SkyStarNetwork.register();
        MinecraftForge.EVENT_BUS.addListener(com.typeomega.core.mega.system.SkyStarNetwork::onServerTick);
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist.isClient()) {
            if (com.typeomega.core.mega.TypeOmegaMegastructures.available()) {
                // The dynamic render type has to exist before the machine models load.
                var ignored = com.typeomega.core.mega.common.client.MegaRenderer.Render.TYPE;
                MinecraftForge.EVENT_BUS.addListener(
                        com.typeomega.core.mega.common.client.MegaRenderer::onRenderLevelStage);
                MinecraftForge.EVENT_BUS.addListener(
                        com.typeomega.core.mega.common.client.MegaSkyStar::onRenderLevelStage);
                MinecraftForge.EVENT_BUS.addListener(
                        com.typeomega.core.mega.common.client.MegaDevCommand::register);
            }
        }
        if (System.getProperty(RecipeExport.PROPERTY) != null) {
            MinecraftForge.EVENT_BUS.addListener(RecipeExport::onServerStarted);
        }
    }

    /**
     * Crafting Chain (optional) crafts from the containers around an open Crafting Station, the same six sides the
     * station shows. Sent over IMC, so the core needs no compile dependency on it.
     */
    private void sendCraftingSources(net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent event) {
        if (!net.minecraftforge.fml.ModList.get().isLoaded("craftingchain")) return;
        net.minecraftforge.fml.InterModComms.sendTo("craftingchain", "sources",
                () -> (java.util.function.Function<net.minecraft.server.level.ServerPlayer, java.util.List<net.minecraftforge.items.IItemHandler>>) player -> player.containerMenu instanceof com.typeomega.core.station.CraftingStationMenu station ?
                        station.sides().stream()
                                .map(com.typeomega.core.station.StationSides.Side::handler)
                                .toList() :
                        java.util.List.of());
    }

    private void registerMaterialRegistry(MaterialRegistryEvent event) {
        GTCEuAPI.materialManager.createRegistry(MOD_ID);
    }

    private void registerMaterials(MaterialEvent event) {
        TypeOmegaAlloys.register();
        com.typeomega.core.chem.ChemMaterials.register();
        com.typeomega.core.route.RouteMaterials.register();
        com.typeomega.core.chem.ZipDMaterials.register();
        com.typeomega.core.data.rocket.SpaceMaterials.register();
        com.typeomega.core.mega.typezero.TypeZeroMaterials.register();
        com.typeomega.core.mega.MegaMaterials.register();
    }

    private void registerRecipeTypes(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {
        TypeOmegaRecipeTypes.init();
    }

    private void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        TypeOmegaBeeMachines.init();
        com.typeomega.core.miner.TypeOmegaMiners.init();
        com.typeomega.core.miner.FragmentCollectors.init();
        com.typeomega.core.processing.TypeOmegaProcessing.init();
        com.typeomega.core.data.rocket.TypeOmegaRocketAssembler.init();
        com.typeomega.core.mega.typezero.TypeZeroMegas.registerMachines();
        com.typeomega.core.mega.TypeOmegaMegastructures.init();
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
