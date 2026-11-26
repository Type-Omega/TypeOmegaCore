package com.typeomega.core.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.machines.GTMachineUtils;

import net.minecraft.network.chat.Component;

import com.typeomega.core.TypeOmegaMod;

import java.util.Locale;

/** Farm machines: bees from HV (D-259) and the Agronomica Seed Analyzer from LV (D-271). */
public final class TypeOmegaBeeMachines {

    // EN: HV is the first tier (D-259); bees must not exist before the chemistry their combs need.
    // PT: HV é o primeiro tier (D-259); abelhas não existem antes da química que os combs pedem.
    // ZH: HV 为最低等级（D-259）；在蜂巢所需化工出现之前不提供蜜蜂。
    public static final int FIRST_TIER = GTValues.HV;

    public static final MachineDefinition[] INDUSTRIAL_APIARY = registerApiaries();
    public static final MachineDefinition[] SEED_ANALYZER = registerSeedAnalyzers();
    public static final MachineDefinition[] MUTATION_CHAMBER = registerMutationChambers();

    private static MachineDefinition[] registerMutationChambers() {
        MachineDefinition[] machines = new MachineDefinition[GTValues.TIER_COUNT];
        for (int tier : GTValues.tiersBetween(FIRST_TIER, GTValues.UV)) {
            String id = GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_mutation_chamber";
            machines[tier] = TypeOmegaMod.REGISTRATE
                    .machine(id, holder -> new SimpleTieredMachine(holder, tier,
                            GTMachineUtils.defaultTankSizeFunction))
                    .tier(tier)
                    .langValue("%s Mutation Chamber %s".formatted(GTValues.VLVH[tier], GTValues.VLVT[tier]))
                    .editableUI(SimpleTieredMachine.EDITABLE_UI_CREATOR.apply(TypeOmegaMod.id("mutation_chamber"),
                            TypeOmegaRecipeTypes.MUTATION_CHAMBER))
                    .rotationState(RotationState.NON_Y_AXIS)
                    .recipeType(TypeOmegaRecipeTypes.MUTATION_CHAMBER)
                    .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
                    .workableTieredHullModel(GTCEu.id("block/machines/chemical_bath"))
                    .tooltips(Component.translatable("typeomega.machine.mutation_chamber.tooltip"))
                    .tooltips(GTMachineUtils.workableTiered(tier, GTValues.V[tier], GTValues.V[tier] * 64L,
                            TypeOmegaRecipeTypes.MUTATION_CHAMBER,
                            GTMachineUtils.defaultTankSizeFunction.applyAsInt(tier),
                            true))
                    .register();
        }
        return machines;
    }

    private static MachineDefinition[] registerSeedAnalyzers() {
        TypeOmegaRecipeTypes.SEED_ANALYZER
                .addCustomRecipeLogic(com.typeomega.core.machine.logic.SeedAnalyzerLogic.INSTANCE);
        MachineDefinition[] machines = new MachineDefinition[GTValues.TIER_COUNT];
        for (int tier : GTValues.tiersBetween(GTValues.LV, GTValues.HV)) {
            String id = GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_seed_analyzer";
            machines[tier] = TypeOmegaMod.REGISTRATE
                    .machine(id, holder -> new SimpleTieredMachine(holder, tier,
                            GTMachineUtils.defaultTankSizeFunction))
                    .tier(tier)
                    .langValue("%s Seed Analyzer %s".formatted(GTValues.VLVH[tier], GTValues.VLVT[tier]))
                    .editableUI(SimpleTieredMachine.EDITABLE_UI_CREATOR.apply(TypeOmegaMod.id("seed_analyzer"),
                            TypeOmegaRecipeTypes.SEED_ANALYZER))
                    .rotationState(RotationState.NON_Y_AXIS)
                    .recipeType(TypeOmegaRecipeTypes.SEED_ANALYZER)
                    .workableTieredHullModel(GTCEu.id("block/machines/scanner"))
                    .tooltips(Component.translatable("typeomega.machine.seed_analyzer.tooltip"))
                    .tooltips(GTMachineUtils.workableTiered(tier, GTValues.V[tier], GTValues.V[tier] * 64L,
                            TypeOmegaRecipeTypes.SEED_ANALYZER, GTMachineUtils.defaultTankSizeFunction.applyAsInt(tier),
                            true))
                    .register();
        }
        return machines;
    }

    private TypeOmegaBeeMachines() {}

    private static MachineDefinition[] registerApiaries() {
        MachineDefinition[] machines = new MachineDefinition[GTValues.TIER_COUNT];
        for (int tier : GTValues.tiersBetween(FIRST_TIER, GTValues.UV)) {
            String id = GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_industrial_apiary";
            machines[tier] = TypeOmegaMod.REGISTRATE
                    .machine(id, holder -> new SimpleTieredMachine(holder, tier,
                            GTMachineUtils.defaultTankSizeFunction))
                    .tier(tier)
                    .langValue("%s Industrial Apiary %s".formatted(GTValues.VLVH[tier], GTValues.VLVT[tier]))
                    .editableUI(SimpleTieredMachine.EDITABLE_UI_CREATOR.apply(TypeOmegaMod.id("industrial_apiary"),
                            TypeOmegaRecipeTypes.INDUSTRIAL_APIARY))
                    .rotationState(RotationState.NON_Y_AXIS)
                    .recipeType(TypeOmegaRecipeTypes.INDUSTRIAL_APIARY)
                    .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
                    .workableTieredHullModel(GTCEu.id("block/machines/brewery"))
                    .tooltips(Component.translatable("typeomega.machine.industrial_apiary.tooltip.purpose"),
                            Component.translatable("typeomega.machine.industrial_apiary.tooltip.bees"))
                    .tooltips(GTMachineUtils.workableTiered(tier, GTValues.V[tier], GTValues.V[tier] * 64L,
                            TypeOmegaRecipeTypes.INDUSTRIAL_APIARY,
                            GTMachineUtils.defaultTankSizeFunction.applyAsInt(tier),
                            true))
                    .register();
        }
        return machines;
    }

    public static void init() {}
}
