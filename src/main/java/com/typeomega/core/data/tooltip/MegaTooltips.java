package com.typeomega.core.data.tooltip;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import com.typeomega.core.data.TypeOmegaTooltips;
import com.typeomega.core.mega.birch.BirchPhysics;
import com.typeomega.core.mega.disintegrator.DisintegratorPhysics;
import com.typeomega.core.mega.hraemc.HraePhysics;
import com.typeomega.core.mega.kugelblitz.KugelblitzPhysics;
import com.typeomega.core.mega.gigaforge.GigaforgePhysics;
import com.typeomega.core.mega.nidavellir.NidavellirPhysics;
import com.typeomega.core.mega.qso.QsoPhysics;
import com.typeomega.core.mega.suprashell.ShellPhysics;
import com.typeomega.core.mega.yard.YardPhysics;
import com.typeomega.core.mega.yggdrasil.YggdrasilPhysics;

import java.util.List;

import static com.typeomega.core.data.TypeOmegaTooltips.finish;
import static com.typeomega.core.data.TypeOmegaTooltips.own;
import static com.typeomega.core.data.tooltip.Fmt.num;
import static com.typeomega.core.data.tooltip.Fmt.pct;
import static com.typeomega.core.data.tooltip.Fmt.pow2;
import static com.typeomega.core.data.tooltip.Fmt.sci;
import static com.typeomega.core.data.tooltip.Fmt.times;
import static com.typeomega.core.data.tooltip.StellarTooltips.begin;
import static com.typeomega.core.data.tooltip.StellarTooltips.repair;
import static com.typeomega.lib.tooltip.TooltipArg.value;

/**
 * Megastructures that run recipes, hold black holes or take systems apart: Assembly Yard, Nidavellir, Yggdrasil,
 * Kugelblitz, HRAE-MC, Suprashell, Disintegrator, Birch World and the Quasi-Stellar Obliterator.
 */
public final class MegaTooltips {

    private MegaTooltips() {}

    public static void register() {
        TypeOmegaTooltips.register("assembly_yard", MegaTooltips::yard);
        TypeOmegaTooltips.register("nidavellir", MegaTooltips::nidavellir);
        TypeOmegaTooltips.register("neutronium_gigaforge", MegaTooltips::neutroniumGigaforge);
        TypeOmegaTooltips.register("yggdrasil", MegaTooltips::yggdrasil);
        TypeOmegaTooltips.register("kugelblitz", MegaTooltips::kugelblitz);
        TypeOmegaTooltips.register("hraemc", MegaTooltips::hraemc);
        TypeOmegaTooltips.register("suprashell", MegaTooltips::suprashell);
        TypeOmegaTooltips.register("disintegrator", MegaTooltips::disintegrator);
        TypeOmegaTooltips.register("birch_world", MegaTooltips::birch);
        TypeOmegaTooltips.register("qso", MegaTooltips::qso);
    }

    private static List<Component> yard() {
        String id = "assembly_yard";
        var b = begin(id, "shipyard", "megastructure")
                .line(own(id, "recipes"))
                .line(own(id, "slipways"), value(YardPhysics.SLIPWAYS_PER_PHASE), value(YardPhysics.slipways(5)),
                        value(YardPhysics.PARALLEL_PER_SLIPWAY))
                .line(own(id, "closed"))
                .line(own(id, "light"), value(times(YardPhysics.SPEED_MIN)), value(times(YardPhysics.SPEED_MAX)))
                .line(own(id, "rush"), value(times(YardPhysics.RUSH_SPEED)), value(times(YardPhysics.RUSH_EU)))
                .line(own(id, "controllers"), value(pct(YardPhysics.MEGA_DISCOUNT)))
                .line(own(id, "energy"));
        return finish(b, id);
    }

    private static List<Component> nidavellir() {
        String id = "nidavellir";
        int max = NidavellirPhysics.COMPRESSION_MAX;
        var b = begin(id, "alloy_forge", "megastructure")
                .line(own(id, "recipes"))
                .line(own(id, "compression"), value(max), value(NidavellirPhysics.parallel(0)))
                .line(own(id, "range"), value(num(NidavellirPhysics.parallel(max))),
                        value(NidavellirPhysics.neutronium(max, false)))
                .line(own(id, "magnetosphere"), value(times(NidavellirPhysics.SPEED_NEUTRON_STAR)))
                .line(own(id, "pulsar"))
                .line(own(id, "energy"))
                .warning(own(id, "waits"));
        return finish(b, id);
    }

    private static List<Component> neutroniumGigaforge() {
        String id = "neutronium_gigaforge";
        var b = begin(id, "star_lifting", "megastructure")
                .line(own(id, "star"))
                .line(own(id, "extract"), value(GigaforgePhysics.maxDustPerSecond(1)),
                        value(GigaforgePhysics.maxDustPerSecond(GigaforgePhysics.PHASES)))
                .line(own(id, "phases"), value(GigaforgePhysics.maxDustPerSecond(2)),
                        value(GigaforgePhysics.maxDustPerSecond(3)))
                .line(own(id, "rate"), value(GigaforgePhysics.RATE_MIN + "%"),
                        value(GigaforgePhysics.RATE_MAX + "%"))
                .line(own(id, "pair"))
                .line(own(id, "output"))
                .line(own(id, "energy"), value(sci(GigaforgePhysics.EU_PER_DUST) + " EU"));
        return finish(b, id);
    }

    private static List<Component> yggdrasil() {
        String id = "yggdrasil";
        var b = begin(id, "greenhouse", "megastructure")
                .line(own(id, "recipes"))
                .line(own(id, "parallel"), value(YggdrasilPhysics.PARALLEL_PER_PHASE),
                        value(YggdrasilPhysics.BIOMASS_PER_CRAFT))
                .line(own(id, "growth"), value(num(YggdrasilPhysics.C0)), value(num(YggdrasilPhysics.K_I)),
                        value(num(YggdrasilPhysics.K_C)))
                .line(own(id, "depth"), value(num(YggdrasilPhysics.DEPTH_MAX)), value(num(YggdrasilPhysics.DEPTH_STEP)))
                .line(own(id, "rate"), value(num(YggdrasilPhysics.SPEED_PER_G)),
                        value(times(YggdrasilPhysics.SPEED_MIN)),
                        value(times(YggdrasilPhysics.SPEED_MAX)))
                .line(own(id, "best"), value(num(YggdrasilPhysics.bestDepth(1))))
                .line(own(id, "energy"));
        return finish(b, id);
    }

    private static List<Component> kugelblitz() {
        String id = "kugelblitz";
        double[] targets = KugelblitzPhysics.TARGETS;
        var b = begin(id, "generator", "megastructure")
                .line(own(id, "mass"), value(pow2(KugelblitzPhysics.EU_PER_KG) + " EU"),
                        value(sci(KugelblitzPhysics.FORM_MASS) + " kg"))
                .line(own(id, "lasers"), value(pow2(KugelblitzPhysics.RATES[KugelblitzPhysics.maxRate(1)]) + " EU/t"),
                        value(pow2(KugelblitzPhysics.RATES[KugelblitzPhysics.maxRate(2)]) + " EU/t"))
                .line(own(id, "hawking"), value(pow2(KugelblitzPhysics.hawkingEut(1e6)) + " EU/t"))
                .line(own(id, "lifetime"))
                .line(own(id, "blades"), value(pct(KugelblitzPhysics.CATCH)))
                .line(own(id, "targets"), value(sci(targets[0]) + " kg"),
                        value(sci(targets[targets.length - 1]) + " kg"))
                .line(own(id, "vault"), value(num(KugelblitzPhysics.KG_PER_ITEM * 1000) + " g"))
                .warning(own(id, "critical"), value(sci(KugelblitzPhysics.CRITICAL) + " kg"),
                        value(sci(KugelblitzPhysics.CRITICAL_STABILISED) + " kg"));
        repair(b, id, KugelblitzPhysics.REPAIR);
        return finish(b, id);
    }

    private static List<Component> hraemc() {
        String id = "hraemc";
        var b = begin(id, "generator", "research", "megastructure")
                .warning(own(id, "where"))
                .line(own(id, "accretion"), value(pow2(HraePhysics.EU_PER_UNIT) + " EU"),
                        value(pct(HraePhysics.efficiency(0))), value(pct(HraePhysics.efficiency(HraePhysics.MAX_SPIN))),
                        value(num(HraePhysics.MAX_SPIN)))
                .line(own(id, "eddington"), value(pow2(HraePhysics.EDDINGTON_EUT) + " EU/t"))
                .line(own(id, "feed"), value(sci(HraePhysics.SPIN_PER_UNIT)))
                .line(own(id, "penrose"), value(pow2(HraePhysics.PENROSE_EUT) + " EU/t"),
                        value(pct(HraePhysics.rotationalShare(1))), value(HraePhysics.SHARE_STEP + "%"))
                .line(own(id, "jets"))
                .line(own(id, "research"))
                .line(own(id, "mass"));
        return finish(b, id);
    }

    private static List<Component> suprashell() {
        String id = "suprashell";
        var b = begin(id, "module_host", "megastructure")
                .warning(own(id, "where"))
                .line(own(id, "modules"), value(ShellPhysics.PARALLEL_PER_MODULE))
                .line(own(id, "room"), value(ShellPhysics.MODULES_PER_PHASE))
                .line(own(id, "radius"), value(num(ShellPhysics.R_MIN)), value(num(ShellPhysics.R_MAX)),
                        value(num(ShellPhysics.R_STEP)))
                .line(own(id, "tau"), value(pct(ShellPhysics.tau(ShellPhysics.R_MIN))), value(num(ShellPhysics.R_MIN)),
                        value(pct(ShellPhysics.tau(ShellPhysics.R_MAX))), value(num(ShellPhysics.R_MAX)))
                .line(own(id, "lowering"))
                .line(own(id, "energy"));
        return finish(b, id);
    }

    private static List<Component> disintegrator() {
        String id = "disintegrator";
        var b = begin(id, "demolition", "megastructure")
                .line(own(id, "binding"), value(pow2(DisintegratorPhysics.EU_PER_SOLAR_MASS_SQ) + " EU"),
                        value(pow2(DisintegratorPhysics.EUT_PER_PHASE) + " EU/t"))
                .line(own(id, "share"))
                .separator()
                .line(own(id, "crust"), value(pct(DisintegratorPhysics.CRUST)),
                        materials(DisintegratorPhysics.CRUST_YIELD))
                .line(own(id, "mantle"), value(pct(DisintegratorPhysics.MANTLE)),
                        materials(DisintegratorPhysics.MANTLE_YIELD))
                .line(own(id, "core"), value(pct(DisintegratorPhysics.CORE)),
                        materials(DisintegratorPhysics.CORE_YIELD))
                .line(own(id, "dust"), value(num(DisintegratorPhysics.DUST_PER_PERCENT)))
                .line(own(id, "star"), value(pct(1 - DisintegratorPhysics.CORE)),
                        value(num(DisintegratorPhysics.GAS_PER_PERCENT)))
                .line(own(id, "stop"))
                .warning(own(id, "wipe"));
        return finish(b, id);
    }

    private static Component materials(Material[] yields) {
        MutableComponent out = Component.empty().withStyle(ChatFormatting.WHITE);
        for (int i = 0; i < yields.length; i++) {
            if (i > 0) out.append(", ");
            out.append(yields[i].getLocalizedName());
        }
        return out;
    }

    private static List<Component> birch() {
        String id = "birch_world";
        int[] era = BirchPhysics.ERA_EXPONENTS;
        var b = begin(id, "module_host", "megastructure")
                .line(own(id, "habitat"), value(Fmt.sci(BirchPhysics.POPULATION_PER_LAYER)),
                        value(BirchPhysics.LAYERS_PER_PHASE))
                .line(own(id, "modules"))
                .line(own(id, "speed"), value(times(BirchPhysics.SPEED)))
                .line(own(id, "room"), value(BirchPhysics.PARALLEL_PER_MODULE), value(BirchPhysics.MODULES_PER_LAYER),
                        value(BirchPhysics.LAYERS_PER_PHASE))
                .line(own(id, "interest"), value(pct(BirchPhysics.INTEREST)))
                .line(own(id, "repay"), value(pow2(BirchPhysics.REPAY_STEP) + " EU/s"), value(BirchPhysics.REPAY_MAX))
                .line(own(id, "credit"), value(pow2(BirchPhysics.CREDIT_PER_LAYER_MASS) + " EU"))
                .warning(own(id, "paradox"), value(BirchPhysics.PARADOX_SECONDS))
                .line(own(id, "era"), value("2^" + era[0]), value("2^" + era[1]), value("2^" + era[2]));
        return finish(b, id);
    }

    private static List<Component> qso() {
        String id = "qso";
        var b = begin(id, "weapon", "megastructure")
                .line(own(id, "input"), value(pow2(QsoPhysics.EUT_PER_SOLAR_MASS) + " EU/t"),
                        value(pow2(QsoPhysics.DRAW_STEP) + " EU/s"), value(QsoPhysics.DRAW_MAX))
                .line(own(id, "leak"), value(pct(QsoPhysics.LEAK)), value(num(QsoPhysics.LEAK)))
                .line(own(id, "shot"), value(pow2(QsoPhysics.AIM)), value(QsoPhysics.SIZE_MAX))
                .line(own(id, "negentropy"), value(pow2(QsoPhysics.AIM)), value(pow2(QsoPhysics.AIM)),
                        value(pow2(QsoPhysics.EU_PER_UNIT)))
                .line(own(id, "tradeoff"))
                .separator()
                .warning(own(id, "finite"))
                .warning(own(id, "end"));
        return finish(b, id);
    }
}
