package com.typeomega.core.miner;

import com.typeomega.core.GTIAMod;

/** English text of the veins with purity and the Entangled Miner (GTIA-D-310); pt_br and zh_cn are in lang. */
public final class GTIAMinerLang {

    private GTIAMinerLang() {}

    public static void init() {
        GTIAMod.REGISTRATE.addRawLang("typeomega.entangled_miner",
                "Entangled Miner");
        GTIAMod.REGISTRATE.addRawLang("typeomega.ore_processing_plant",
                "Ore Processing Plant");
        GTIAMod.REGISTRATE.addRawLang("typeomega.fragment_collector",
                "Fragment Collector");
        GTIAMod.REGISTRATE.addRawLang("typeomega.vein_card.none",
                "No bedrock vein under this chunk");
        GTIAMod.REGISTRATE.addRawLang("typeomega.vein_card.recorded",
                "Recorded: %s (%s)");
        GTIAMod.REGISTRATE.addRawLang("typeomega.vein_card.blank",
                "Right-click inside a bedrock vein to record it");
        GTIAMod.REGISTRATE.addRawLang("typeomega.vein_card.vein",
                "%s, %s");
        GTIAMod.REGISTRATE.addRawLang("typeomega.vein_card.where",
                "%s, around X %s Z %s");
        GTIAMod.REGISTRATE.addRawLang("typeomega.vein.purity.impure",
                "impure ×1");
        GTIAMod.REGISTRATE.addRawLang("typeomega.vein.purity.normal",
                "normal ×2");
        GTIAMod.REGISTRATE.addRawLang("typeomega.vein.purity.pure",
                "pure ×4");
        GTIAMod.REGISTRATE.addRawLang("typeomega.machine.entangled_miner.tooltip.0",
                "Mines the bedrock vein on the Vein Card in its input slot, forever. Each operation takes 1, 2 or 4 raw ores by the vein's purity.");
        GTIAMod.REGISTRATE.addRawLang("typeomega.machine.entangled_miner.tooltip.1",
                "At most %s miners per vein; the next ones wait for a free slot.");
        GTIAMod.REGISTRATE.addRawLang("typeomega.machine.entangled_miner.tooltip.same_dimension",
                "Only veins in its own dimension (any dimension from EV).");
        GTIAMod.REGISTRATE.addRawLang("typeomega.machine.entangled_miner.tooltip.any_dimension",
                "Mines veins in any dimension.");
        GTIAMod.REGISTRATE.addRawLang("typeomega.vein.iron",
                "Iron Vein");
        GTIAMod.REGISTRATE.addRawLang("typeomega.vein.copper",
                "Copper Vein");
        GTIAMod.REGISTRATE.addRawLang("typeomega.vein.tin",
                "Tin Vein");
        GTIAMod.REGISTRATE.addRawLang("typeomega.vein.coal",
                "Coal Vein");
        GTIAMod.REGISTRATE.addRawLang("typeomega.vein.lead_silver",
                "Lead and Silver Vein");
        GTIAMod.REGISTRATE.addRawLang("typeomega.vein.redstone",
                "Redstone Vein");
        GTIAMod.REGISTRATE.addRawLang("typeomega.vein.nickel",
                "Nickel Vein");
        GTIAMod.REGISTRATE.addRawLang("typeomega.vein.bauxite",
                "Bauxite Vein");
        GTIAMod.REGISTRATE.addRawLang("typeomega.vein.gold",
                "Gold Vein");
        GTIAMod.REGISTRATE.addRawLang("typeomega.vein.salts",
                "Salt Vein");
        GTIAMod.REGISTRATE.addRawLang("typeomega.vein.nether_quartz",
                "Nether Quartz Vein");
        GTIAMod.REGISTRATE.addRawLang("typeomega.vein.sulfur",
                "Sulfur Vein");
        GTIAMod.REGISTRATE.addRawLang("typeomega.vein.pitchblende",
                "Pitchblende Vein");
        GTIAMod.REGISTRATE.addRawLang("typeomega.vein.scheelite",
                "Scheelite Vein");
    }
}
