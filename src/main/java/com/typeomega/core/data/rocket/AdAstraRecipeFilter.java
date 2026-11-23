package com.typeomega.core.data.rocket;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.server.ServerStartedEvent;

import com.typeomega.core.GTIAMod;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * EN: Drops Ad Astra's recipes that GT now makes or that skip GT: the rocket parts and suits (GTIA-D-272, D-420), the
 * Fuel Refinery, Compressor, Etrionic Blast Furnace and Coal Generator with their recipes (shortcuts to fuel, plates
 * and steel), the NASA Workbench rockets of tiers 2 to 4, and the furnace recipes of desh (it goes through the EBF,
 * GTIA-D-393). Runs when the server is up and after each /reload, like the Productive Bees filter.
 * PT: Descarta as receitas do Ad Astra que o GT passou a fazer ou que pulam o GT: peças do foguete e trajes
 * (GTIA-D-272, D-420), a Refinaria de Combustível, o Compressor, a Etrionic Blast Furnace e o Gerador a Carvão com as
 * receitas deles (atalhos para combustível, placas e aço), os foguetes T2 a T4 da NASA Workbench e as receitas de
 * fornalha do desh (ele passa pelo EBF, GTIA-D-393). Roda com o servidor pronto e após cada /reload, como o filtro do
 * Productive Bees.
 * ZH: 移除由 GT 制作或绕过 GT 的 Ad Astra 配方：火箭部件和宇航服（GTIA-D-272、D-420），燃料精炼厂、压缩机、
 * Etrionic 高炉和燃煤发电机及其配方（燃料、板材和钢的捷径），NASA 工作台的二至四级火箭，以及戴什的熔炉配方
 * （须经电力高炉，GTIA-D-393）。在服务器启动后及每次 /reload 后执行，与 Productive Bees 过滤器相同。
 */
public final class AdAstraRecipeFilter {

    static final Set<ResourceLocation> DROPPED = build();

    private AdAstraRecipeFilter() {}

    private static Set<ResourceLocation> build() {
        List<String> ids = new ArrayList<>(List.of(
                // Rocket parts, suits, oxygen gear and base blocks, now in GT machines
                "rocket_nose_cone", "rocket_fin", "steel_tank", "steel_engine", "desh_tank", "desh_engine",
                "gas_tank", "fan", "engine_frame", "oxygen_gear", "oxygen_loader", "launch_pad", "airlock",
                "steel_sliding_door", "oxygen_sensor", "gravity_normalizer",
                "space_helmet", "space_suit", "space_pants", "space_boots",
                "netherite_space_helmet", "netherite_space_suit", "netherite_space_pants", "netherite_space_boots",
                // Shortcut machines and what they make
                "fuel_refinery", "compressor", "etrionic_blast_furnace", "coal_generator",
                "refining/fuel_from_refining_oil", "alloying/steel_ingot_from_alloying_iron_ingot_and_coals",
                "nasa_workbench/tier_1_rocket_from_nasa_workbench", "nasa_workbench/tier_2_rocket_from_nasa_workbench",
                "nasa_workbench/tier_3_rocket_from_nasa_workbench",
                "nasa_workbench/tier_4_rocket_from_nasa_workbench"));
        for (String metal : new String[] { "calorite", "desh", "ostrum", "steel" }) {
            ids.add("compressing/" + metal + "_plate_from_compressing_" + metal + "_ingots");
            ids.add("compressing/" + metal + "_plate_from_compressing_" + metal + "_blocks");
        }
        ids.add("compressing/iron_plate_from_compressing_iron_ingot");
        ids.add("compressing/iron_plate_from_compressing_iron_block");
        for (String furnace : new String[] { "smelting", "blasting" })
            for (String source : new String[] { "raw_desh", "moon_desh_ore", "deepslate_desh_ore" })
                ids.add(furnace + "/desh_ingot_from_" + furnace + "_" + source);
        return Set.copyOf(ids.stream().map(id -> new ResourceLocation("ad_astra", id)).toList());
    }

    public static void onServerStarted(ServerStartedEvent event) {
        filter(event.getServer());
    }

    public static void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) filter(event.getPlayerList().getServer());
    }

    public static int filter(MinecraftServer server) {
        var manager = server.getRecipeManager();
        var all = manager.getRecipes();
        var kept = all.stream().filter(r -> !DROPPED.contains(r.getId())).toList();
        int dropped = all.size() - kept.size();
        if (dropped > 0) {
            manager.replaceRecipes(kept);
            GTIAMod.LOGGER.info("Dropped {} Ad Astra recipes (GTIA-D-272, D-420)", dropped);
        }
        return dropped;
    }
}
