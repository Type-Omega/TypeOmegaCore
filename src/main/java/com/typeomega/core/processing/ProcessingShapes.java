package com.typeomega.core.processing;

/**
 * Shapes approved on 2026-11-11 (D-369), generated from design/estudos/fusao_f3/designs.py (lf_c, op_b, pf_c).
 * Back aisle to front aisle; one string per layer from the ground up; '@' controller, '.' anything.
 */
final class ProcessingShapes {

    private ProcessingShapes() {}

    /**
     * Large Furnace "furnace hall": A solid steel casing (hatches), E firebricks, H lava, V tempered glass, P steel
     * pipe,
     * K coke oven bricks (chimneys), I iron bars, F steel frame, J steel block, R steel gearbox, O orange lamp (ember
     * band), G grating, T steel hull (feet).
     */
    static final String[][] LARGE_FURNACE = {
            { "T...T.........T...T.", "JJJJJJJJJJJJJJJJJJJ.", "....................", "....................",
                    "....................", "....................", "....................", "....................",
                    "....................", "....................", "....................", "....................",
                    "....................", "....................", "....................", "....................",
                    "...................." },
            { "....................", "JJJJJJJJJJJJJJJJJJJ.", ".AAAAAAAAAAAAAAAAA..", "....................",
                    "....................", "....................", "....................", "....................",
                    "....................", "....................", "....................", "....................",
                    "....................", "....................", "....................", "....................",
                    "...................." },
            { "....................", "JJJJJJJJJJJJJJJJJJJ.", ".AAAAAAAAAAAAAAAAA..", "....F...F...F...F...",
                    "....F...F...F...F...", "....F...F...F...F...", "....F...F...F...F...", "....F...F...F...F...",
                    "....F...F...F...F...", "....F...F...F...F...", "....................", "....................",
                    "....................", "....................", "....................", "....................",
                    "...................." },
            { "....................", "JJJJJJJJJJJJJJJJJJJ.", ".AAAAAAAAAAAAAAAAA..", "..E...E...E...E...E.",
                    "..E...E...E...E...E.", "..E...E...E...E...E.", "..E...E...E...E...E.", "..E...E...E...E...E.",
                    "..E...E...E...E...E.", "....................", ".RRRRRRRRRRRRRRRRR..", "....................",
                    "....................", "....................", "....................", "....................",
                    "...................." },
            { "....................", "JJJJJJJJJJJJJJJJJJJ.", ".AAAAAAAAAAAAAAAAA..", ".EHE.EHE.EHE.EHE.EHE",
                    ".E.E.E.E.E.E.E.E.E.E", ".E.E.E.E.E.E.E.E.E.E", ".E.E.E.E.E.E.E.E.E.E", ".E.E.E.E.E.E.E.E.E.E",
                    ".EEE.EEE.EEE.EEE.EEE", ".AAA.AAA.AAA.AAA.AAA", "....................", "....................",
                    "....................", "....................", "....................", "....................",
                    "...................." },
            { "....................", "JJJJJJJJJJJJJJJJJJJ.", "AAAAAAAAAAAAAAAAAAA.", "FEHE.EHE.EHE.EHE.EFE",
                    "FE.E.E.E.E.E.E.E.EFE", "FE.E.E.E.E.E.E.E.EFE", "FE.E.E.E.E.E.E.E.EFE", "FE.E.E.E.E.E.E.E.EFE",
                    "FEEE.EEE.EEE.EEE.EFE", "FAAA.AAA.AAA.AAA.AFA", "F.R...R...R...R...F.", "F.PPPPPPPPPPPPPPP.F.",
                    "PPPPPPPPPPPPPPPPPPP.", "..K.............K...", "..K.............K...", "..K.............K...",
                    "..I.............I..." },
            { "....................", "JJJJJJJJJJJJJJJJJJJ.", ".AAAAAAAAAAAAAAAAA..", ".EHE.EHE.EHE.EHE.EHE",
                    ".E.E.E.E.E.E.E.E.E.E", ".E.E.E.E.E.E.E.E.E.E", ".E.E.E.E.E.E.E.E.E.E", ".E.E.E.E.E.E.E.E.E.E",
                    ".EEE.EEE.EEE.EEE.EEE", ".AAA.AAA.AAA.AAA.AAA", "....................", "....................",
                    "....................", "....................", "....................", "....................",
                    "...................." },
            { "....................", "JJJJJJJJJJJJJJJJJJJ.", ".AAAAAAAAAAAAAAAAA..", "..E...E...E...E...E.",
                    "..E...E...E...E...E.", "..V...V...V...V...V.", "..V...V...V...V...V.", "..E...E...E...E...E.",
                    "..E...E...E...E...E.", "....................", ".RRRRRRRRRRRRRRRRR..", "....................",
                    "....................", "....................", "....................", "....................",
                    "...................." },
            { "....................", "JJJJJJJJJJJJJJJJJJJ.", ".AAAAAAAAAAAAAAAAA..", "....F...F...F...F...",
                    ".OOOFOOOFOOOFOOOFOO.", "....G...G...G...G...", "....F...F...F...F...", "....F...F...F...F...",
                    "....F...F...F...F...", "....F...F...F...F...", "....................", "....................",
                    "....................", "....................", "....................", "....................",
                    "...................." },
            { "....................", "JJJJJJJJJJJJJJJJJJJ.", ".AAAAAAAAAAAAAAAAA..", "........A@A.........",
                    "....................", "....................", "....................", "....................",
                    "....................", "....................", "....................", "....................",
                    "....................", "....................", "....................", "....................",
                    "...................." },
            { "T...T.........T...T.", "JJJJJJJJJJJJJJJJJJJ.", "...A....AAA....A....", "....................",
                    "....................", "....................", "....................", "....................",
                    "....................", "....................", "....................", "....................",
                    "....................", "....................", "....................", "....................",
                    "...................." },
    };

    /**
     * Ore Processing Plant "beneficiation line": A frostproof aluminium casing (hatches), S clean stainless casing,
     * G steel gearbox, W water, V tempered glass, T steel pipe, R aluminium frame, M MV casing, Y yellow stripes.
     */
    static final String[][] ORE_PLANT = {
            { "YSYSYSYSYSYSY", "A.....R.....A", "R.....R.....R", "R.....R.....R", "R.....R.....R", "R.....R.....R",
                    "R.....R.....R", "RRRRRRRRRRRRR", "............." },
            { "SSSSSSSSSSSSS", ".............", ".............", ".............", ".............", ".............",
                    ".............", ".............", "............." },
            { "YSSSSSSSSSSSY", "AAA.AAAAAAAAA", "AAA.AAAVVV...", "AAA.AAAVVV...", "AAA..........", "AAA..........",
                    "AAA..........", ".............", "............." },
            { "SSSSSSSSSSSSS", "A.A.AAAAAAAAA", "AGA.AAAVWV.M.", "AGA.AAAVWV.M.", "AGA..G.....M.", "AGA.......AAA",
                    "A.A..........", "RRR..........", "............." },
            { "YSSSSSSSSSSSY", "A.A.AAAAAAAAA", "AGA.AAAVWV...", "AGA.AAAVWV...", "AGAT.GT...T..", "AGA.......AAA",
                    "A.A..........", "RRR..........", "TTTTTTTTTTTTT" },
            { "SSSSSSSSSSSSS", "A.A.AAAAAAAAA", "AGA.AAAVWV.M.", "AGA.AAAVWV.M.", "AGA..G.....M.", "AGA.......AAA",
                    "A.A..........", "RRR..........", "............." },
            { "YSSSSSSSSSSSY", "AAA.AAAAAAAAA", "AAA.AAAVVV...", "AAA.AAAVVV...", "AAA..........", "AAA..........",
                    "AAA..........", ".............", "............." },
            { "SSSSSSSSSSSSS", ".A..AAA.A..A.", ".............", ".............", ".............", ".............",
                    ".............", ".............", "............." },
            { "YSYSYSYSYSYSY", "R...AAA.....R", "R...A@A.....R", "R.....R.....R", "R.....R.....R", "R.....R.....R",
                    "R.....R.....R", "RRRRRRRRRRRRR", "............." },
    };

    /**
     * Plus hall: A frostproof aluminium casing (hatches), S clean stainless casing, Q family module, M MV casing,
     * V tempered glass, T steel pipe, R aluminium frame, Y yellow stripes, O family lamp, H steel hull (feet).
     */
    static final String[][] PLUS_HALL = {
            { "...............", "YSYSYSYSYSYSYSY", "..AAAAAAAAAAA..", "..AAAAAAAAAAA..", "..AAAAAAAAAAA..",
                    "..AAAAAAAAAAA..", "..AAAAAAAAAAA..", "..AAAAAAAAAAA..", "...............", "...............",
                    "..............." },
            { ".H.....H.....H.", "SSSSSSSSSSSSSSS", ".AAAAAAAAAAAAA.", ".A...........A.", ".A...........A.",
                    ".A...........A.", ".A...........A.", ".AAAAAAAAAAAAA.", "...............", "...............",
                    "..............." },
            { "...............", "YSSSSSSSSSSSSSY", "AAAAAAAAAAAAAAA", "A.............A", "A.............A",
                    "A.............A", "A.............A", "AAAAAAAAAAAAAAA", "..R.R.R.R.R.R..", "...............",
                    "..............." },
            { "...............", "SSSSSSSSSSSSSSS", "AAAMAAAAAAAMAAA", "A..M.......M..A", "A.............A",
                    "A.............A", "A.............A", "AAAAAAAAAAAAAAA", "..R.R.R.R.R.R..", "...............",
                    "..............." },
            { "...............", "YSSSSSSSSSSSSSY", "AAAAAAAQAAAAAAA", "A......Q......A", "A......Q......A",
                    "A......Q......A", "A......Q......A", "AAAAAAAAAAAAAAA", "..R.RVVVVVR.R..", ".....VVVVV.....",
                    "..............." },
            { "...............", "SSSSSSSSSSSSSSS", "AAAMAAAQAAAMAAA", "A..M...Q...M..A", "A......Q......A",
                    "A......Q......A", "A......Q......A", "AAAAAAAAAAAAAAA", "R.R.RVVVVVR.R.R", "R....VVVVV....R",
                    "TTTTTAAAAATTTTT" },
            { "...............", "YSSSSSSSSSSSSSY", "AAAAAAAQAAAAAAA", "A......Q......A", "A......Q......A",
                    "A......Q......A", "A......Q......A", "AAAAAAAAAAAAAAA", "..R.RVVVVVR.R..", ".....VVVVV.....",
                    "..............." },
            { "...............", "SSSSSSSSSSSSSSS", "AAAMAAAAAAAMAAA", "A..M.......M..A", "A.............A",
                    "A.............A", "A.............A", "AAAAAAAAAAAAAAA", "..R.R.R.R.R.R..", "...............",
                    "..............." },
            { "...............", "YSSSSSSSSSSSSSY", "AAAAAAAAAAAAAAA", "A.............A", "A.............A",
                    "A.............A", "A.............A", "AAAAAAAAAAAAAAA", "..R.R.R.R.R.R..", "...............",
                    "..............." },
            { ".H.....H.....H.", "SSSSSSSSSSSSSSS", ".AAAAAAAAAAAAA.", ".A...........A.", ".A...........A.",
                    ".A...........A.", ".A...........A.", ".AAAAAAAAAAAAA.", "...............", "...............",
                    "..............." },
            { "...............", "YSYSYSYSYSYSYSY", "..AAAAAAAAAAA..", "..VVVVA@AVVVV..", "..VVVVVVVVVVV..",
                    "..VVVVVVVVVVV..", "..OOOOOOOOOOO..", "..AAAAAAAAAAA..", "...............", "...............",
                    "..............." },
    };
}
