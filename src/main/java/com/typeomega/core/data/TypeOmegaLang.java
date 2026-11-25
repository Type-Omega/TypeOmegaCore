package com.typeomega.core.data;

import com.typeomega.core.TypeOmegaMod;

public final class TypeOmegaLang {

    private TypeOmegaLang() {}

    public static void init() {
        TypeOmegaOrganLang.init();
        com.typeomega.core.data.rocket.AdAstraNames.registerLang();
        com.typeomega.core.miner.TypeOmegaMinerLang.init();
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.crafting_station.tab",
                "%s (%s)");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.crafting_station.empty",
                "Place chests or other containers against any side of the station, above and below too, to craft from them.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.crafting_station.dump",
                "Move the crafting grid into the containers");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.crafting_station.side.up",
                "above");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.crafting_station.side.north",
                "north");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.crafting_station.side.east",
                "east");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.crafting_station.side.south",
                "south");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.crafting_station.side.west",
                "west");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.crafting_station.side.down",
                "below");
        TypeOmegaMod.REGISTRATE.addRawLang("tagprefix.type_omega_double_ingot", "Double %s Ingot");
        TypeOmegaMod.REGISTRATE.addRawLang("tagprefix.type_omega_triple_ingot", "Triple %s Ingot");
        TypeOmegaMod.REGISTRATE.addRawLang("tagprefix.type_omega_quadruple_ingot", "Quadruple %s Ingot");
        TypeOmegaMod.REGISTRATE.addRawLang("tagprefix.type_omega_quintuple_ingot", "Quintuple %s Ingot");
        TypeOmegaMod.REGISTRATE.addRawLang("tagprefix.type_omega_triple_plate", "Triple %s Plate");
        TypeOmegaMod.REGISTRATE.addRawLang("tagprefix.type_omega_quadruple_plate", "Quadruple %s Plate");
        TypeOmegaMod.REGISTRATE.addRawLang("tagprefix.type_omega_quintuple_plate", "Quintuple %s Plate");
        TypeOmegaMod.REGISTRATE.addRawLang("tagprefix.type_omega_superdense_plate", "Superdense %s Plate");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega:phase_matrix_deposit", "Phase Matrix Deposit");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.industrial_apiary", "Industrial Apiary");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.seed_analyzer", "Seed Analyzer");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.rocket_assembly", "Rocket Assembly");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.mutation_chamber", "Mutation Chamber");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.machine.mutation_chamber.tooltip",
                "Two caged parent bees (kept) turn a caged wild bee into a new species. No luck involved.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.machine.seed_analyzer.tooltip",
                "Reveals the Growth, Gain and Resistance of Agronomica seeds.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.machine.industrial_apiary.tooltip.purpose",
                "Keeps a caged bee as an item and produces its combs.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.machine.industrial_apiary.tooltip.bees",
                "No bees fly around: the species comes from the cage, frames and flowers are recipe inputs.");
    }
}
