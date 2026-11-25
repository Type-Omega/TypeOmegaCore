package com.typeomega.core.data.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import com.typeomega.core.data.TypeOmegaTooltips;
import com.typeomega.core.mega.creator.CreatorPhysics;
import com.typeomega.core.mega.engine.EnginePhysics;
import com.typeomega.core.mega.harvester.HarvesterPhysics;
import com.typeomega.core.mega.hoop.HoopPhysics;
import com.typeomega.core.mega.lifter.LifterPhysics;
import com.typeomega.core.mega.matrioshka.MatrioshkaDefinition;
import com.typeomega.core.mega.matrioshka.MatrioshkaShells;
import com.typeomega.core.mega.suppressor.SuppressorPhysics;
import com.typeomega.core.mega.system.StarPhysics;
import com.typeomega.core.mega.system.StarSystems;
import com.typeomega.lib.tooltip.MultiblockTooltipBuilder;
import com.typeomega.lib.tooltip.TooltipRow;

import java.util.List;
import java.util.StringJoiner;

import static com.typeomega.core.data.TypeOmegaTooltips.finish;
import static com.typeomega.core.data.TypeOmegaTooltips.own;
import static com.typeomega.core.data.TypeOmegaTooltips.shared;
import static com.typeomega.core.data.TypeOmegaTooltips.start;
import static com.typeomega.core.data.tooltip.Fmt.num;
import static com.typeomega.core.data.tooltip.Fmt.pct;
import static com.typeomega.core.data.tooltip.Fmt.perMille;
import static com.typeomega.core.data.tooltip.Fmt.pow2;
import static com.typeomega.core.data.tooltip.Fmt.sci;
import static com.typeomega.core.data.tooltip.Fmt.times;
import static com.typeomega.lib.tooltip.TooltipArg.highlight;
import static com.typeomega.lib.tooltip.TooltipArg.value;

/**
 * Megastructures that work on a star: Stellar Engine, Matrioshka Brain, Star Lifter, Stellar Hoop, Stellar
 * Harvester, Fusion Suppressor and Interstellar System Creator. Every number comes from their physics classes.
 */
public final class StellarTooltips {

    private StellarTooltips() {}

    public static void register() {
        TypeOmegaTooltips.register("stellar_engine", StellarTooltips::engine);
        TypeOmegaTooltips.register("matrioshka_brain", StellarTooltips::matrioshka);
        TypeOmegaTooltips.register("star_lifter", StellarTooltips::lifter);
        TypeOmegaTooltips.register("stellar_hoop", StellarTooltips::hoop);
        TypeOmegaTooltips.register("stellar_harvester", StellarTooltips::harvester);
        TypeOmegaTooltips.register("fusion_suppressor", StellarTooltips::suppressor);
        TypeOmegaTooltips.register("system_creator", StellarTooltips::creator);
    }

    /** Machine Type, the purpose line and the five-phase rule every megastructure shares. */
    static MultiblockTooltipBuilder begin(String id, String... types) {
        return start(types).line(own(id, "purpose")).line(shared("mega.phases"), value(5)).separator();
    }

    /** "Repair: idle, put its blocks in an input bus: +5% each". */
    static MultiblockTooltipBuilder repair(MultiblockTooltipBuilder b, String id, int perMille) {
        return b.line(shared("mega.repair"), highlight(own(id, "repair_blocks")), value(perMille(perMille)));
    }

    private static List<Component> engine() {
        String id = "stellar_engine";
        StringJoiner regions = new StringJoiner(" · ");
        for (var r : EnginePhysics.Region.values()) if (r.dv > 0) regions.add(num(r.dv));
        var b = begin(id, "propulsion", "megastructure")
                .line(own(id, "shkadov"), value(num(EnginePhysics.MIRROR_SHARE)))
                .line(own(id, "caplan"), value(sci(EnginePhysics.MDOT_MAX) + " kg/s"), value("0.01 c"))
                .line(own(id, "power"), value(pow2(EnginePhysics.CAPLAN_EUT) + " EU/t"))
                .line(own(id, "plasma"), value(EnginePhysics.HYDROGEN_PLASMA), value(EnginePhysics.HELIUM_PLASMA))
                .line(own(id, "dv"), value(sci(EnginePhysics.YEARS_PER_SECOND)))
                .line(own(id, "beta"), value(EnginePhysics.BETA_MIN + "%"), value(EnginePhysics.BETA_MAX + "%"),
                        value(EnginePhysics.BETA_STEP + "%"))
                .warning(own(id, "tension"), value(EnginePhysics.BETA_SAFE + "%"),
                        value(perMille(EnginePhysics.WEAR)))
                .separator()
                .line(own(id, "regions"), value(regions.toString()))
                .line(own(id, "bonuses"), value(pct(EnginePhysics.mdotBonus(EnginePhysics.Region.GALACTIC_CORE) - 1)),
                        value(pct(1 - EnginePhysics.shellCooling(EnginePhysics.Region.GALACTIC_CORE))),
                        value(times(EnginePhysics.hydrogenBonus(EnginePhysics.Region.GALACTIC_CORE))))
                .line(own(id, "era"), value(EnginePhysics.Region.GALACTIC_CORE.era));
        repair(b, id, EnginePhysics.REPAIR);
        return finish(b, id);
    }

    private static List<Component> matrioshka() {
        String id = "matrioshka_brain";
        var b = begin(id, "computer", "megastructure")
                .line(own(id, "shell"), value(num(MatrioshkaShells.T_REF) + " K"),
                        value(pow2(MatrioshkaShells.shellCwut(1, MatrioshkaShells.T_REF, 100))),
                        value(pow2(MatrioshkaShells.shellCwut(5, MatrioshkaShells.T_REF, 100))))
                .line(own(id, "temperature"), value(num(MatrioshkaShells.T_1AU) + " K"),
                        value(num(MatrioshkaShells.BLOCKS_PER_AU)))
                .line(own(id, "clock"), value(MatrioshkaShells.CLOCK_MIN + "%"),
                        value(MatrioshkaShells.CLOCK_MAX + "%"), value(MatrioshkaShells.CLOCK_STEP + "%"))
                .line(own(id, "coolant"));
        for (var c : MatrioshkaShells.Coolant.values()) {
            if (c == MatrioshkaShells.Coolant.NONE) continue;
            b.table(TooltipRow.of(own(id, "coolant." + c.name().toLowerCase(java.util.Locale.ROOT)),
                    ChatFormatting.AQUA, num(c.kelvin) + " K", "η " + num(c.eta), c.perPhase + " mB/s"));
        }
        b.line(own(id, "inertia"), value(pct(MatrioshkaShells.INERTIA)))
                .warning(own(id, "overheat"), value(num(MatrioshkaShells.limit(1)) + " K"),
                        value(num(MatrioshkaShells.limit(4)) + " K"), value(perMille(MatrioshkaShells.WEAR)))
                .warning(own(id, "fracture"))
                .separator()
                .line(own(id, "psi"), value(pow2(MatrioshkaShells.CWU_PER_PSI)))
                .line(own(id, "hatches"), value(MatrioshkaDefinition.DATA_HATCHES))
                .line(own(id, "void"), value(pct(1 - EnginePhysics.shellCooling(EnginePhysics.Region.INTERARM_VOID))));
        repair(b, id, MatrioshkaShells.REPAIR);
        return finish(b, id);
    }

    private static List<Component> lifter() {
        String id = "star_lifter";
        var b = begin(id, "star_lifting", "megastructure")
                .line(own(id, "escape"), value(sci(LifterPhysics.G_M_OVER_R_SUN) + " J/kg"),
                        value(pct(LifterPhysics.LIFT_EFFICIENCY)))
                .line(own(id, "power"), value(pow2(LifterPhysics.EUT_AT_FULL)),
                        value(sci(LifterPhysics.MDOT_RING)))
                .line(own(id, "modes"), value(LifterPhysics.Mode.THERMAL.phase),
                        value(sci(LifterPhysics.MDOT_THERMAL)), value(LifterPhysics.Mode.RING.phase),
                        value(sci(LifterPhysics.MDOT_RING)), value(LifterPhysics.Mode.DEEP.phase))
                .line(own(id, "rate"), value(LifterPhysics.RATE_MIN + "%"), value(LifterPhysics.RATE_MAX + "%"),
                        value(LifterPhysics.RATE_STEP + "%"))
                .line(own(id, "capture"), value(pct(LifterPhysics.CAPTURE_CHALICES)),
                        value(pct(LifterPhysics.CAPTURE_OPEN)))
                .line(own(id, "output"), value(sci(LifterPhysics.MB_PER_KG)), value(sci(LifterPhysics.DUST_PER_KG)))
                .line(own(id, "star"), value(sci(LifterPhysics.YEARS_PER_SECOND)))
                .line(own(id, "deep"), value(num(LifterPhysics.FUEL_MIX)))
                .line(own(id, "orion"), value(pct(EnginePhysics.mdotBonus(EnginePhysics.Region.ORION_EDGE) - 1)))
                .warning(own(id, "instability"), value(LifterPhysics.DEEP_SAFE + "%"),
                        value(perMille(LifterPhysics.FLARE_WEAR)))
                .warning(own(id, "min_mass"), value(num(LifterPhysics.MIN_MASS)));
        repair(b, id, LifterPhysics.REPAIR);
        return finish(b, id);
    }

    private static List<Component> hoop() {
        String id = "stellar_hoop";
        StringJoiner p0 = new StringJoiner(" / ");
        for (int p : HoopPhysics.PARALLEL) p0.add(String.valueOf(p));
        var b = begin(id, "stellar_forge", "megastructure")
                .line(own(id, "recipes"))
                .line(own(id, "corona"), value("10^4 K"), value("2×10^6 K"),
                        value(sci(HoopPhysics.coronaKelvin(HoopPhysics.HEIGHT_MAX)) + " K"))
                .line(own(id, "speed"), value(times(HoopPhysics.SPEED)), value(pct(HoopPhysics.EU_FACTOR)))
                .line(own(id, "height"), value(HoopPhysics.HEIGHT_MIN + "%"), value(HoopPhysics.HEIGHT_MAX + "%"),
                        value(HoopPhysics.HEIGHT_STEP + "%"))
                .line(own(id, "parallel"), value(HoopPhysics.HEIGHT_MIN + "%"), value(p0.toString()))
                .line(own(id, "load"), value(pct(1 - HoopPhysics.RADIATORS)))
                .line(own(id, "coolants"), value(num(HoopPhysics.COOLANT_UNITS[1])),
                        value(num(HoopPhysics.COOLANT_UNITS[2])), value(num(HoopPhysics.COOLANT_UNITS[3])))
                .warning(own(id, "wear"), value(perMille(HoopPhysics.WEAR)))
                .line(own(id, "second_hoop"));
        repair(b, id, HoopPhysics.REPAIR);
        return finish(b, id);
    }

    private static List<Component> harvester() {
        String id = "stellar_harvester";
        var b = begin(id, "generator", "megastructure")
                .line(own(id, "boost"), value(HarvesterPhysics.exponent(1)),
                        value(HarvesterPhysics.exponent(HarvesterPhysics.CNO_MASS)),
                        value(num(HarvesterPhysics.CNO_MASS)))
                .line(own(id, "heat"), value(pct(HarvesterPhysics.HEAT_PER_PHASE)),
                        value(pct(HarvesterPhysics.HEAT_STEP)))
                .line(own(id, "power"), value(pow2(HarvesterPhysics.EUT_PER_SUN) + " EU/t"),
                        value(sci(HarvesterPhysics.BOOST_MAX)))
                .line(own(id, "budget"), value(pow2(HarvesterPhysics.EU_PER_SOLAR_MASS) + " EU"))
                .separator()
                .warning(own(id, "end"), value(num(HarvesterPhysics.WHITE_DWARF_LIMIT)),
                        value(num(HarvesterPhysics.NEUTRON_STAR_LIMIT)), value(pct(SuppressorPhysics.HOLE_SHARE)))
                .line(own(id, "remnants"))
                .warning(own(id, "final"));
        return finish(b, id);
    }

    private static List<Component> suppressor() {
        String id = "fusion_suppressor";
        var b = begin(id, "research", "megastructure")
                .line(own(id, "suppression"), value(SuppressorPhysics.MAX + "%"), value(SuppressorPhysics.STEP + "%"))
                .line(own(id, "research"), value(num(SuppressorPhysics.RESEARCH_FACTOR)))
                .line(own(id, "field"), value(pow2(SuppressorPhysics.EUT_FULL) + " EU/t"),
                        value(num(SuppressorPhysics.SURFACE_COOLING)))
                .line(own(id, "deficit"))
                .separator()
                .line(own(id, "ready"), value(num(SuppressorPhysics.COLLAPSE_MASS)))
                .line(own(id, "supernova"), value(SuppressorPhysics.DUST_PER_SOLAR_MASS),
                        value(num(SuppressorPhysics.NEUTRON_STAR_MASS)), value(SuppressorPhysics.SUPERNOVA_RESEARCH))
                .line(own(id, "hole"), value(pct(SuppressorPhysics.HOLE_SHARE)),
                        value(SuppressorPhysics.HOLE_RESEARCH))
                .warning(own(id, "flash"), value(perMille(SuppressorPhysics.FLASH_WEAR)))
                .warning(own(id, "final"));
        repair(b, id, SuppressorPhysics.REPAIR);
        return finish(b, id);
    }

    private static List<Component> creator() {
        String id = "system_creator";
        var b = begin(id, "star_foundry", "megastructure")
                .line(own(id, "cloud"), value(num(CreatorPhysics.MB_PER_SOLAR_MASS)),
                        value(times(CreatorPhysics.PLASMA_FACTOR)), value(num(CreatorPhysics.GATHER_MB_PER_SECOND)))
                .line(own(id, "cost"), value(pow2(CreatorPhysics.PSI_PER_SOLAR_MASS)),
                        value(CreatorPhysics.DUST_PER_SOLAR_MASS))
                .line(own(id, "collapse"), value(CreatorPhysics.collapseSeconds(StarPhysics.Kind.YELLOW_DWARF)),
                        value(CreatorPhysics.COLLAPSE_MIN), value(CreatorPhysics.COLLAPSE_MAX))
                .line(own(id, "ignite"), value(pow2(CreatorPhysics.IGNITE_EU) + " EU"),
                        value(CreatorPhysics.IGNITE_SECONDS))
                .separator();
        for (var kind : CreatorPhysics.KINDS) {
            b.table(TooltipRow.of(own(id, "kind." + kind.name().toLowerCase(java.util.Locale.ROOT)),
                    ChatFormatting.GOLD, shared("mega.phase").component(CreatorPhysics.phase(kind)),
                    num(CreatorPhysics.cloudMass(kind)) + " M☉"));
        }
        StringJoiner z = new StringJoiner(", ");
        for (double m : CreatorPhysics.METALLICITY) z.add(num(m));
        b.line(own(id, "remnant_psi"), value(times(CreatorPhysics.REMNANT_PSI)))
                .line(own(id, "metallicity"), value(z.toString()))
                .line(own(id, "systems"), value(StarSystems.SLOTS))
                .line(own(id, "era"), value(CreatorPhysics.ERA_SYSTEMS[0]), value(CreatorPhysics.ERA_SYSTEMS[1]),
                        value(CreatorPhysics.ERA_SYSTEMS[2]))
                .warning(own(id, "disperse"));
        return finish(b, id);
    }
}
