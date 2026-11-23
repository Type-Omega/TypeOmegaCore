package com.typeomega.core;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import com.typeomega.core.data.GTIAAgroRecipes;
import com.typeomega.core.data.GTIABeeRecipes;
import com.typeomega.core.data.GTIAExtraFormRecipes;
import com.typeomega.core.data.GTIAExtraForms;
import com.typeomega.core.data.GTIAPhaseOres;
import com.typeomega.core.data.GTIARecipes;

import java.util.function.Consumer;

@GTAddon
public final class GTIAAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return GTIAMod.REGISTRATE;
    }

    @Override
    public void initializeAddon() {
        GTIAMod.LOGGER.info("GTIACore initialized");
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        GTIAMod.LOGGER.info("Registering GTIA recipes");
        GTIARecipes.addRecipes(provider);
        com.typeomega.core.chem.ChemLineRecipes.addRecipes(provider);
        GTIAExtraFormRecipes.addRecipes(provider);
        GTIABeeRecipes.addRecipes(provider);
        GTIAAgroRecipes.addRecipes(provider);
        com.typeomega.core.data.rocket.GTIARocketRecipes.addRecipes(provider);
        com.typeomega.core.data.GTIANexusRecipes.addRecipes(provider);
        com.typeomega.core.organ.OrganRecipes.addRecipes(provider);
        com.typeomega.core.chem.ChemRecipes.addRecipes(provider);
        com.typeomega.core.route.RouteRecipes.addRecipes(provider);
        com.typeomega.core.route.ProgressionGates.addRecipes(provider);
        com.typeomega.core.route.AE2Gates.addRecipes(provider);
        com.typeomega.core.route.NanoCircuits.addRecipes(provider);
        com.typeomega.core.miner.GTIAMiners.addRecipes(provider);
        com.typeomega.core.processing.OrePlantRecipes.add(provider);
        com.typeomega.core.processing.ProcessingRecipes.add(provider);
        com.typeomega.core.mega.MegaRecipes.addRecipes(provider);
    }

    @Override
    public void removeRecipes(Consumer<ResourceLocation> consumer) {
        com.typeomega.core.chem.ChemRouteRemovals.removeRecipes(consumer);
        com.typeomega.core.route.RouteRemovals.removeRecipes(consumer);
        com.typeomega.core.route.ProgressionGates.removeRecipes(consumer);
        com.typeomega.core.route.NanoCircuits.removeRecipes(consumer);
        // EN: Normal worlds move steam through pipes; only Journey crafts the GTNA wireless steam hatches (GTIA-D-340).
        // PT: No Normal o vapor vai por canos; só o Journey fabrica os hatches de vapor sem fio do GTNA (GTIA-D-340).
        // ZH: 普通模式下蒸汽走管道；只有旅程模式能合成 GTNA 无线蒸汽仓（GTIA-D-340）。
        if (com.typeomega.core.rules.WorldRules.get(com.typeomega.core.rules.GTIARules.WIRELESS_STEAM) == 0) {
            for (String id : new String[] { "gtna:wireless_steam_input_hatch", "gtna:wireless_steam_output_hatch",
                    "gtceu:hydraulic_manufacturing/wireless_steam_input_hatch_steel",
                    "gtceu:hydraulic_manufacturing/wireless_steam_output_hatch_steel" })
                consumer.accept(new ResourceLocation(id));
        }
    }

    @Override
    public void registerOreVeins() {
        GTIAPhaseOres.register();
        com.typeomega.core.data.GTIAEndVeins.register();
    }

    @Override
    public void registerBedrockOreVeins() {
        com.typeomega.core.miner.BedrockVeins.register();
    }

    @Override
    public void registerTagPrefixes() {
        GTIAExtraForms.register();
    }

    @Override
    public String addonModId() {
        return GTIAMod.MOD_ID;
    }
}
