package com.typeomega.core.balance;

import com.gregtechceu.gtceu.config.ConfigHolder;

/**
 * The one Type Omega balance (GTIA-D-436): the GTCEu settings of the former Normal preset, fixed for every world.
 * The world rules and their presets are gone; a world's old {@code gtia_rules.json} is no longer read.
 */
public final class TypeOmegaBalance {

    private TypeOmegaBalance() {}

    /** Called from the mod constructor, before GTCEu registers its machines and items and long before recipes. */
    public static void apply() {
        // EN: Mods are constructed in parallel and GTCEu's init is idempotent, so make sure its config exists.
        // PT: Os mods são construídos em paralelo e o init do GTCEu é idempotente; garante que a config exista.
        // ZH: 模组并行构建，GTCEu 的 init 可重复调用，因此先确保其配置已存在。
        ConfigHolder.init();
        var recipes = ConfigHolder.INSTANCE.recipes;
        recipes.casingsPerCraft = 2;
        recipes.disableManualCompression = true;
        recipes.harderRods = true;
        recipes.harderBrickRecipes = false;
        recipes.nerfWoodCrafting = false;
        recipes.hardWoodRecipes = false;
        recipes.hardIronRecipes = true;
        recipes.hardRedstoneRecipes = false;
        recipes.hardToolArmorRecipes = false;
        recipes.hardMiscRecipes = false;
        recipes.hardGlassRecipes = true;
        recipes.nerfPaperCrafting = true;
        recipes.hardAdvancedIronRecipes = true;
        recipes.hardDyeRecipes = false;
        recipes.harderCharcoalRecipe = true;
        recipes.flintAndSteelRequireSteel = true;
        recipes.removeVanillaBlockRecipes = false;
        recipes.removeVanillaTNTRecipe = true;
        recipes.harderCircuitRecipes = false;
        recipes.hardMultiRecipes = false;
        recipes.enchantedTools = false;

        var machines = ConfigHolder.INSTANCE.machines;
        // EN: Multiblocks need a maintenance hatch from the first LV machine on (the Normal preset, GTIA-D-436).
        // PT: Multiblocos precisam de escotilha de manutenção desde a primeira máquina LV (o Normal, GTIA-D-436).
        // ZH: 从第一台 LV 机器起，多方块结构需要维护仓（原普通模式，GTIA-D-436）。
        machines.enableMaintenance = true;
        machines.enableCleanroom = true;
        machines.cleanMultiblocks = false;
        machines.doesExplosionDamagesTerrain = true;
        machines.shouldWeatherOrTerrainExplosion = false;
        machines.harmlessActiveTransformers = false;
        machines.orderedAssemblyLineItems = true;
        machines.orderedAssemblyLineFluids = true;
        machines.energyUsageMultiplier = 100;
        machines.prospectorEnergyUseMultiplier = 100;
        machines.requireGTToolsForBlocks = true;
        ConfigHolder.INSTANCE.tools.rngDamageElectricTools = 10;

        var worldgen = ConfigHolder.INSTANCE.worldgen;
        worldgen.oreVeins.oreVeinGridSize = 3;
        worldgen.oreVeins.infiniteBedrockOresFluids = false;
        ConfigHolder.INSTANCE.gameplay.hazardsEnabled = true;
    }
}
