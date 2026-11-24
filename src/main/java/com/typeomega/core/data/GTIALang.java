package com.typeomega.core.data;

import com.typeomega.core.GTIAMod;

public final class GTIALang {

    private GTIALang() {}

    public static void init() {
        GTIAOrganLang.init();
        com.typeomega.core.data.rocket.AdAstraNames.registerLang();
        com.typeomega.core.miner.GTIAMinerLang.init();
        GTIAMod.REGISTRATE.addRawLang("typeomega.crafting_station.tab",
                "%s (%s)");
        GTIAMod.REGISTRATE.addRawLang("typeomega.crafting_station.empty",
                "Place chests or other containers on the sides or below the station to craft from them.");
        GTIAMod.REGISTRATE.addRawLang("typeomega.crafting_station.side.north",
                "north");
        GTIAMod.REGISTRATE.addRawLang("typeomega.crafting_station.side.east",
                "east");
        GTIAMod.REGISTRATE.addRawLang("typeomega.crafting_station.side.south",
                "south");
        GTIAMod.REGISTRATE.addRawLang("typeomega.crafting_station.side.west",
                "west");
        GTIAMod.REGISTRATE.addRawLang("typeomega.crafting_station.side.down",
                "below");
        GTIAMod.REGISTRATE.addRawLang("tagprefix.gtia_double_ingot", "Double %s Ingot");
        GTIAMod.REGISTRATE.addRawLang("tagprefix.gtia_triple_ingot", "Triple %s Ingot");
        GTIAMod.REGISTRATE.addRawLang("tagprefix.gtia_quadruple_ingot", "Quadruple %s Ingot");
        GTIAMod.REGISTRATE.addRawLang("tagprefix.gtia_quintuple_ingot", "Quintuple %s Ingot");
        GTIAMod.REGISTRATE.addRawLang("tagprefix.gtia_triple_plate", "Triple %s Plate");
        GTIAMod.REGISTRATE.addRawLang("tagprefix.gtia_quadruple_plate", "Quadruple %s Plate");
        GTIAMod.REGISTRATE.addRawLang("tagprefix.gtia_quintuple_plate", "Quintuple %s Plate");
        GTIAMod.REGISTRATE.addRawLang("tagprefix.gtia_superdense_plate", "Superdense %s Plate");
        GTIAMod.REGISTRATE.addRawLang("typeomega:phase_matrix_deposit", "Phase Matrix Deposit");
        GTIAMod.REGISTRATE.addRawLang("typeomega.industrial_apiary", "Industrial Apiary");
        GTIAMod.REGISTRATE.addRawLang("typeomega.seed_analyzer", "Seed Analyzer");
        GTIAMod.REGISTRATE.addRawLang("typeomega.rocket_assembly", "Rocket Assembly");
        GTIAMod.REGISTRATE.addRawLang("typeomega.mutation_chamber", "Mutation Chamber");
        GTIAMod.REGISTRATE.addRawLang("typeomega.machine.mutation_chamber.tooltip",
                "Two caged parent bees (kept) turn a caged wild bee into a new species. No luck involved.");
        GTIAMod.REGISTRATE.addRawLang("typeomega.machine.seed_analyzer.tooltip",
                "Reveals the Growth, Gain and Resistance of Agronomica seeds.");
        GTIAMod.REGISTRATE.addRawLang("typeomega.machine.industrial_apiary.tooltip.purpose",
                "Keeps a caged bee as an item and produces its combs.");
        GTIAMod.REGISTRATE.addRawLang("typeomega.machine.industrial_apiary.tooltip.bees",
                "No bees fly around: the species comes from the cage, frames and flowers are recipe inputs.");
    }
}
