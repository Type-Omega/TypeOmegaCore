package com.typeomega.core.miner;

import com.typeomega.core.TypeOmegaMod;

/** English text of the veins with purity and the Entangled Miner (D-310); pt_br and zh_cn are in lang. */
public final class TypeOmegaMinerLang {

    private TypeOmegaMinerLang() {}

    public static void init() {
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.entangled_miner",
                "Entangled Miner");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.ore_processing_plant",
                "Ore Processing Plant");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.fragment_collector",
                "Fragment Collector");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.vein_card.none",
                "No bedrock vein under this chunk");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.vein_card.recorded",
                "Recorded: %s (%s)");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.vein_card.blank",
                "Right-click inside a bedrock vein to record it");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.vein_card.vein",
                "%s, %s");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.vein_card.where",
                "%s, around X %s Z %s");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.vein.purity.impure",
                "impure ×1");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.vein.purity.normal",
                "normal ×2");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.vein.purity.pure",
                "pure ×4");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.machine.entangled_miner.tooltip.0",
                "Mines the bedrock vein on the Vein Card in its input slot, forever. Each operation takes 1, 2 or 4 raw ores by the vein's purity.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.machine.entangled_miner.tooltip.1",
                "At most %s miners per vein; the next ones wait for a free slot.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.machine.entangled_miner.tooltip.same_dimension",
                "Only veins in its own dimension (any dimension from EV).");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.machine.entangled_miner.tooltip.any_dimension",
                "Mines veins in any dimension.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.vein.iron",
                "Iron Vein");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.vein.copper",
                "Copper Vein");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.vein.tin",
                "Tin Vein");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.vein.coal",
                "Coal Vein");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.vein.lead_silver",
                "Lead and Silver Vein");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.vein.redstone",
                "Redstone Vein");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.vein.nickel",
                "Nickel Vein");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.vein.bauxite",
                "Bauxite Vein");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.vein.gold",
                "Gold Vein");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.vein.salts",
                "Salt Vein");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.vein.nether_quartz",
                "Nether Quartz Vein");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.vein.sulfur",
                "Sulfur Vein");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.vein.pitchblende",
                "Pitchblende Vein");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.vein.scheelite",
                "Scheelite Vein");
    }
}
