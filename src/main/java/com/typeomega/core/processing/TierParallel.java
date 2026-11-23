package com.typeomega.core.processing;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;

import java.util.function.IntUnaryOperator;

/**
 * Parallel by energy tier, as in GTL Enhanced (GTIA-D-365 to D-367).
 * EN: Parallels multiply inputs and outputs but not the energy, as in GTL Enhanced; the overclock still applies.
 * PT: Os paralelos multiplicam entradas e saídas, mas não a energia, como no GTL Enhanced; o overclock continua.
 * ZH: 并行倍增输入与输出但不倍增能耗，与 GTL Enhanced 一致；超频照常生效。
 */
public final class TierParallel {

    private TierParallel() {}

    /** Large Furnace: 64 at LV, doubled per tier above (GTL base values); a Parallel Hatch adds its own amount. */
    public static int largeFurnace(int tier) {
        return 64 << Math.max(0, Math.min(tier, GTValues.UV) - GTValues.LV);
    }

    /** Ore Processing Plant: 64 at MV, +100 per energy tier above. */
    public static int orePlant(int tier) {
        return 64 + 100 * Math.max(0, tier - GTValues.MV);
    }

    /** Plus factories: 16 at LV, doubled per tier, fixed at 256 from IV. */
    public static int plus(int tier) {
        return Math.min(256, 16 << Math.max(0, tier - GTValues.LV));
    }

    public static RecipeModifier of(IntUnaryOperator byTier) {
        return (MetaMachine machine, GTRecipe recipe) -> {
            if (!(machine instanceof WorkableElectricMultiblockMachine m) || !m.isFormed())
                return ModifierFunction.IDENTITY;
            int limit = byTier.applyAsInt(m.getTier());
            if (machine instanceof IMultiController c)
                limit += c.getParallelHatch().map(h -> h.getCurrentParallel()).orElse(0);
            int p = ParallelLogic.getParallelAmount(machine, recipe, limit);
            if (p <= 1) return p == 0 ? ModifierFunction.NULL : ModifierFunction.IDENTITY;
            return ModifierFunction.builder().modifyAllContents(ContentModifier.multiplier(p)).parallels(p).build();
        };
    }
}
