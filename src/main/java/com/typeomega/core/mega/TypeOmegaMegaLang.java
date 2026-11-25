package com.typeomega.core.mega;

import com.typeomega.core.TypeOmegaMod;

/** English text of the megastructures and the Ψ era (D-311, D-312); pt_br and zh_cn are in lang. */
public final class TypeOmegaMegaLang {

    private TypeOmegaMegaLang() {}

    public static void init() {
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.needs_star",
                "No star here: the Matrioshka Brain only works in the Nexus System.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.phases",
                "Phases built: %s of %s");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.shell",
                "Shell %s: %s K, %s CWU/t");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.cwut",
                "Computation in use: %s of %s CWU/t");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.psi",
                "Ψ of the owner: %s thought cycles");
        TypeOmegaMod.REGISTRATE.addRawLang("dimension.typeomega.nexus_system",
                "Nexus System");
        TypeOmegaMod.REGISTRATE.addRawLang("planet.typeomega.nexus_system",
                "Nexus System");
        TypeOmegaMod.REGISTRATE.addRawLang("solar_system.typeomega.nexus_system",
                "Nexus System");
        // Matrioshka screen
        TypeOmegaMod.REGISTRATE.addRawLang("gtna.ui.o.mb_clock_down",
                "Clock -");
        TypeOmegaMod.REGISTRATE.addRawLang("gtna.ui.o.mb_clock_down.tip",
                "Lower the clock by 25% (less CWU, cooler shells).");
        TypeOmegaMod.REGISTRATE.addRawLang("gtna.ui.o.mb_clock_up",
                "Clock +");
        TypeOmegaMod.REGISTRATE.addRawLang("gtna.ui.o.mb_clock_up.tip",
                "Raise the clock by 25% (more CWU, hotter shells).");
        TypeOmegaMod.REGISTRATE.addRawLang("gtna.ui.o.mb_shells",
                "Shells");
        TypeOmegaMod.REGISTRATE.addRawLang("gtna.ui.o.mb_shells.tip",
                "Table of the five shells: radius, temperature, CWU, state.");
        TypeOmegaMod.REGISTRATE.addRawLang("gtna.ui.o.mb_manual",
                "Manual");
        TypeOmegaMod.REGISTRATE.addRawLang("gtna.ui.o.mb_manual.tip",
                "How the brain works, with its formulas.");
        TypeOmegaMod.REGISTRATE.addRawLang("gtna.ui.o.mb_shells_title",
                "Shells");
        TypeOmegaMod.REGISTRATE.addRawLang("gtna.ui.o.mb_manual_title",
                "Field manual - Matrioshka Brain");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.star",
                "STAR");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.cool",
                "COOL");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.phases",
                "Phases %s / %s");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.clock",
                "clock %s%%");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.no_star",
                "No star here: build it in the Nexus System.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.cwu",
                "Computation");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.coolant",
                "Coolant");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.hottest",
                "Hottest shell");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.psi",
                "Ψ of the owner: %s");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.psi_rate",
                "+%s Ψ/s from idle CWU");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.overheat_warning",
                "%s shell(s) overheated: lower the clock or add coolant");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.col_shell",
                "Shell");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.col_radius",
                "Radius");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.col_temp",
                "Temp.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.col_cwu",
                "CWU/t");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.col_state",
                "State");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.not_built",
                "not built");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.overheat",
                "overheated");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.ok",
                "computing");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.shells_hint",
                "Unbuilt shells show what they would give at this clock and coolant.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.manual.0",
                "The Matrioshka Brain wraps the Nexus star in shells of computers. Each finished phase is one shell, computing with the starlight that reaches it.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.manual.1",
                "= T = 278.6 K / sqrt(r / 100 blocks) × clock^(1/4)");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.manual.2",
                "A shell at radius r sits in the star's light. Radiating more power (a higher clock) needs a hotter shell.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.manual.3",
                "= CWU/t of shell n = 2^(21+n) × clock × 300 K / T");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.manual.4",
                "Landauer's limit: erasing a bit costs k·T·ln 2, so a colder shell computes more with the same energy. The cold outer shells give the most.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.manual.5",
                "= T' = Tc + (T - Tc) × (1 - η × supply)");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.manual.6",
                "A coolant pulls the shells towards its own temperature Tc. The best one in the fluid hatches is used:");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.manual.7",
                "= He 4 K, η 0.40, 250 mB/s · He-II 2 K, η 0.60, 100 mB/s · DSS 0.1 K, η 0.85, 10 mB/s (per phase, × clock)");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.manual.8",
                "Superfluid helium comes from liquid helium in a Vacuum Freezer; the Dimensionally Shifted Superfluid from the Forge of the Gods' chain.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.manual.9",
                "= T(next s) = T + (T' - T) × 0.1");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.manual.10",
                "Temperatures move slowly (10 s time constant): a short clock peak can be held before the shells heat up.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.manual.11",
                "= T > 500 K (540 K with phase 4) → overheated: no CWU, -2% integrity a second");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.manual.12",
                "An overheated shell computes nothing and wears; the outer field of phase 5 halves the wear. At 0% the shell fractures and stays off.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.manual.13",
                "Repair: put the shell's own blocks in an input bus on the station. Each block gives back 5%, one per shell a second, while it is not overheated.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.coolant_short.liquid_helium",
                "He");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.coolant_short.superfluid_helium",
                "He-II");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.coolant_short.shifted_superfluid",
                "DSS");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.coolant_name.liquid_helium",
                "Liquid helium");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.coolant_name.superfluid_helium",
                "Superfluid helium");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.coolant_name.shifted_superfluid",
                "Shifted superfluid");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.fracture_warning",
                "%s shell(s) fractured: put their blocks in an input bus");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.col_integrity",
                "Integr.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.fractured",
                "fractured");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.limit_hint",
                "Limit now: %s K (the main rings of phase 4 add 40 K).");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.manual.14",
                "= Ψ/s = (max CWU/t × 20 - CWU used in the second) / 2^20");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.manual.15",
                "CWU nobody asks for becomes Ψ (thought cycles), kept for the owner like the Nexus network's EU.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.manual.16",
                "Send CWU through up to 2 Optical Data Transmission Hatches on the station; coolant and repair blocks enter through fluid hatches and input buses there too.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.manual.17",
                "The brain only works in the Nexus System, around its star.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.manual.18",
                "Phases 2 to 5 can come later: a phase counts once all its blocks and those of the phases before it are in place.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.matrioshka.ui.manual.19",
                "While it runs, every ring but the polar one leaves the world and turns; switching off puts them back.");
        // Stellar Engine
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.needs_star",
                "No star here: the Stellar Engine only works in the Nexus System.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.thrust",
                "Thrust: %s N");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.dv",
                "Δv gathered: %s km/s");
        TypeOmegaMod.REGISTRATE.addRawLang("gtna.ui.o.eng_beta_down",
                "Jet -");
        TypeOmegaMod.REGISTRATE.addRawLang("gtna.ui.o.eng_beta_down.tip",
                "Send less of the lifted mass out as the main jet (less thrust, less tension).");
        TypeOmegaMod.REGISTRATE.addRawLang("gtna.ui.o.eng_beta_up",
                "Jet +");
        TypeOmegaMod.REGISTRATE.addRawLang("gtna.ui.o.eng_beta_up.tip",
                "Send more of the lifted mass out as the main jet (more thrust; above 70% the cage strains).");
        TypeOmegaMod.REGISTRATE.addRawLang("gtna.ui.o.eng_caplan",
                "Caplan");
        TypeOmegaMod.REGISTRATE.addRawLang("gtna.ui.o.eng_caplan.tip",
                "Switch the Caplan engine (EU from the Nexus network); without it only the mirror pushes.");
        TypeOmegaMod.REGISTRATE.addRawLang("gtna.ui.o.eng_map",
                "Galaxy");
        TypeOmegaMod.REGISTRATE.addRawLang("gtna.ui.o.eng_map.tip",
                "Map of the galaxy: the route, the regions and where the system is.");
        TypeOmegaMod.REGISTRATE.addRawLang("gtna.ui.o.eng_manual",
                "Manual");
        TypeOmegaMod.REGISTRATE.addRawLang("gtna.ui.o.eng_manual.tip",
                "How the engine works, with its formulas.");
        TypeOmegaMod.REGISTRATE.addRawLang("gtna.ui.o.eng_map_title",
                "Galaxy");
        TypeOmegaMod.REGISTRATE.addRawLang("gtna.ui.o.eng_manual_title",
                "Field manual - Stellar Engine");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.star",
                "STAR");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.caplan",
                "CAPLAN");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.beta",
                "jet %s%%");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.no_star",
                "No star here: build it in the Nexus System.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.thrust",
                "Thrust");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.to_next",
                "To %s");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.tension",
                "Tension");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.cage",
                "cage");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.dv_rate",
                "+%s km/s each second");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.plasma",
                "Plasma: H %s · He %s mB/s");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.no_power",
                "Caplan engine without EU: only the mirror pushes");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.cage_broken",
                "Cage broken: put rib blocks in an input bus");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.region.local_cloud",
                "Local Cloud");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.region.orion_edge",
                "Orion Arm Edge");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.region.interarm_void",
                "Interarm Void");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.region.emission_nebula",
                "Emission Nebula");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.region.pulsar_vicinity",
                "Pulsar Vicinity");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.region.galactic_core",
                "Galactic Core");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.bonus.local_cloud",
                "Where the system starts.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.bonus.orion_edge",
                "+25% lifted matter (Stellar era I).");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.bonus.interarm_void",
                "Matrioshka shells 10% colder (era II).");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.bonus.emission_nebula",
                "Twice the hydrogen plasma.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.bonus.pulsar_vicinity",
                "Neutronium dust from the engine.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.bonus.galactic_core",
                "Stellar era III.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.manual.0",
                "The Stellar Engine moves the whole Nexus System. A Shkadov mirror and a Caplan engine work together around its star.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.manual.1",
                "= F(Shkadov) = f × L / c, f = 0.25 (a 60° cap of mirror)");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.manual.2",
                "The mirror sends part of the starlight back: the light leaves only on one side and the system moves (Shkadov, 1987).");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.manual.3",
                "= F(Caplan) = β × ṁ × v_e, v_e = 0.01 c, ṁ = 10^12 kg/s");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.manual.4",
                "The Caplan engine lifts hydrogen and helium from the star with magnetic fields, fuses the helium and fires two jets (Caplan, 2019).");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.manual.5",
                "β is the share of the lifted mass in the main jet; the rest goes back into the star so it follows the engine.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.manual.6",
                "= Tension = (β - 70%) / 20%, at least 50% without the guides of phase 5");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.manual.7",
                "Above 70% the counter-jet cannot hold the star and the cage strains: -2% integrity a second at full tension.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.manual.8",
                "Repair the cage with rib and hoop blocks in an input bus on the station, +5% a block, while there is no tension.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.manual.9",
                "= Δv per second = F / M_sun × 10^6 years");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.manual.10",
                "One second of play is a million years of engine. The Δv gathered is kept for the owner and never goes back.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.manual.11",
                "= Power = 2^36 EU/t from the Nexus network");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.manual.12",
                "Without power only the mirror pushes. The engine also gives hydrogen and helium plasma to the output hatches.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.manual.13",
                "Regions: Orion edge 20 km/s (+25% matter), interarm void 60 (colder Matrioshka shells), emission nebula 120 (more hydrogen), pulsar vicinity 250 (neutronium), galactic core 500.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.manual.14",
                "The galaxy map shows the route, the regions reached and where the system is now.");
        TypeOmegaMod.REGISTRATE.addRawLang("typeomega.stellar_engine.ui.manual.15",
                "Phases: 1 cage and station, 2 mirror, 3 collector ring, 4 Caplan engine, 5 nozzle and counter-jet guides.");
    }
}
