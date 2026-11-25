package com.typeomega.core;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import com.typeomega.core.data.TypeOmegaAgroRecipes;
import com.typeomega.core.data.TypeOmegaBeeRecipes;
import com.typeomega.core.data.TypeOmegaExtraFormRecipes;
import com.typeomega.core.data.TypeOmegaExtraForms;
import com.typeomega.core.data.TypeOmegaPhaseOres;
import com.typeomega.core.data.TypeOmegaRecipes;

import java.util.function.Consumer;

@GTAddon
public final class TypeOmegaAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return TypeOmegaMod.REGISTRATE;
    }

    @Override
    public void initializeAddon() {
        TypeOmegaMod.LOGGER.info("TypeOmegaCore initialized");
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        TypeOmegaMod.LOGGER.info("Registering Type Omega recipes");
        TypeOmegaRecipes.addRecipes(provider);
        com.typeomega.core.chem.ChemLineRecipes.addRecipes(provider);
        TypeOmegaExtraFormRecipes.addRecipes(provider);
        TypeOmegaBeeRecipes.addRecipes(provider);
        TypeOmegaAgroRecipes.addRecipes(provider);
        com.typeomega.core.data.rocket.TypeOmegaRocketRecipes.addRecipes(provider);
        com.typeomega.core.data.rocket.SpaceGearRecipes.addRecipes(provider);
        com.typeomega.core.chem.ZipDRecipes.addRecipes(provider);
        com.typeomega.core.data.TypeOmegaNexusRecipes.addRecipes(provider);
        com.typeomega.core.organ.OrganRecipes.addRecipes(provider);
        com.typeomega.core.chem.ChemRecipes.addRecipes(provider);
        com.typeomega.core.route.RouteRecipes.addRecipes(provider);
        com.typeomega.core.route.ProgressionGates.addRecipes(provider);
        com.typeomega.core.route.AE2Gates.addRecipes(provider);
        com.typeomega.core.integration.ae.TypeOmegaAe.addRecipes(provider);
        com.typeomega.core.route.NanoCircuits.addRecipes(provider);
        com.typeomega.core.miner.TypeOmegaMiners.addRecipes(provider);
        com.typeomega.core.processing.OrePlantRecipes.add(provider);
        com.typeomega.core.processing.ProcessingRecipes.add(provider);
        com.typeomega.core.mega.MegaRecipes.addRecipes(provider);
        com.typeomega.core.mega.typezero.TypeZeroRecipes.addRecipes(provider);
        com.typeomega.core.mega.typezero.WirelessHatchGate.addRecipes(provider);
    }

    @Override
    public void removeRecipes(Consumer<ResourceLocation> consumer) {
        com.typeomega.core.chem.ChemRouteRemovals.removeRecipes(consumer);
        com.typeomega.core.route.RouteRemovals.removeRecipes(consumer);
        com.typeomega.core.route.ProgressionGates.removeRecipes(consumer);
        com.typeomega.core.route.NanoCircuits.removeRecipes(consumer);
        com.typeomega.core.mega.typezero.WirelessHatchGate.removeRecipes(consumer);
        // EN: Steam moves through pipes: the GTNA wireless steam hatches have no recipe (D-340, D-436).
        // PT: O vapor vai por canos: os hatches de vapor sem fio do GTNA não têm receita (D-340, D-436).
        // ZH: 蒸汽通过管道输送：GTNA 无线蒸汽仓没有配方（D-340、D-436）。
        for (String id : new String[] { "gtna:wireless_steam_input_hatch", "gtna:wireless_steam_output_hatch",
                "gtceu:hydraulic_manufacturing/wireless_steam_input_hatch_steel",
                "gtceu:hydraulic_manufacturing/wireless_steam_output_hatch_steel" })
            consumer.accept(new ResourceLocation(id));
    }

    @Override
    public void registerOreVeins() {
        TypeOmegaPhaseOres.register();
        com.typeomega.core.data.TypeOmegaEndVeins.register();
    }

    @Override
    public void registerBedrockOreVeins() {
        com.typeomega.core.miner.BedrockVeins.register();
    }

    @Override
    public void registerTagPrefixes() {
        TypeOmegaExtraForms.register();
    }

    @Override
    public String addonModId() {
        return TypeOmegaMod.MOD_ID;
    }
}
