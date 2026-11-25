package com.typeomega.core.mega.typezero;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.data.recipes.FinishedRecipe;

import com.typeomega.core.GTIAMod;
import com.typeomega.core.data.GTIARecipeTypes;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.VA;

/** Recipes of the three Type 0 megastructures and of what they make and burn (GTIA-D-390, D-419 to D-421, D-425). */
public final class TypeZeroRecipes {

    private static final int HV = GTValues.HV;

    // EN: GTIA-D-390: D-He3 gives twice the EU of D-T. 2 A and 4 A of HV keep the Tokamak below the GTCEu fusion
    // of LuV (GTIA-D-379) while one D-He3 Tokamak alone reaches K 1.00 (2048 EU/t, GTIA-D-380).
    // PT: GTIA-D-390: D-He3 dá o dobro de EU da D-T. 2 A e 4 A de HV deixam o Tokamak abaixo da fusão do GTCEu no
    // LuV (GTIA-D-379), e um Tokamak em D-He3 sozinho chega a K 1,00 (2048 EU/t, GTIA-D-380).
    // ZH: GTIA-D-390：D-He3 的 EU 是 D-T 的两倍。HV 的 2 A 与 4 A 让托卡马克低于 GTCEu 的 LuV 聚变（GTIA-D-379），
    // 而单座 D-He3 托卡马克即可达到 K 1.00（2048 EU/t，GTIA-D-380）。
    public static final long DT_EUT = 2 * GTValues.V[HV];
    public static final long DHE3_EUT = 4 * GTValues.V[HV];
    /** Amps of HV each fuel puts on the dynamos every tick. */
    public static final long DT_AMPS = DT_EUT / GTValues.V[HV], DHE3_AMPS = DHE3_EUT / GTValues.V[HV];
    public static final int FUSION_TICKS = 200;
    /** Tritium burnt and bred back by the lithium blanket per D-T cycle. */
    public static final int TRITIUM_BURNT = 100, TRITIUM_BRED = 90;
    // EN: Ignition temperatures shown on the Tokamak screen (GTIA-D-435): D-T ignites near 150 million K (about
    // 13 keV, the ITER target); D-He3 needs about four times that (about 50 keV).
    // PT: Temperaturas de ignição mostradas na tela do Tokamak (GTIA-D-435): a D-T acende perto de 150 milhões de K
    // (uns 13 keV, a meta do ITER); a D-He3 pede umas quatro vezes isso (uns 50 keV).
    // ZH: 托卡马克界面显示的点火温度（GTIA-D-435）：D-T 约 1.5 亿 K 点火（约 13 keV，ITER 的目标）；
    // D-He3 约需四倍（约 50 keV）。
    public static final long DT_IGNITION_K = 150_000_000L, DHE3_IGNITION_K = 580_000_000L;
    /** Recipe data key of the ignition temperature in kelvin. */
    public static final String IGNITION_KEY = "ignition_k";
    public static final net.minecraft.resources.ResourceLocation DT_RECIPE = GTIAMod
            .id("tokamak_fusion/deuterium_tritium");

    private TypeZeroRecipes() {}

    public static void addRecipes(Consumer<FinishedRecipe> provider) {
        controllers(provider);
        casings(provider);
        dispatchAndMissions(provider);
        fusion(provider);
        heavyWater(provider);
    }

    private static void controllers(Consumer<FinishedRecipe> provider) {
        // GTIA-D-419: HV hull, 4 HV transformers, HV batteries, HV Energy Hatch, aluminium cable, 16 HV circuits and
        // an MV Field Generator.
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTIAMod.id(NationalDispatchCenter.ID))
                .inputItems(GTMachines.HULL[HV].asStack())
                .inputItems(GTMachines.TRANSFORMER[HV].asStack(4))
                .inputItems(CustomTags.HV_BATTERIES, 4)
                .inputItems(GTMachines.ENERGY_INPUT_HATCH[HV].asStack())
                .inputItems(TagPrefix.cableGtSingle, GTMaterials.Aluminium, 8)
                .inputItems(CustomTags.HV_CIRCUITS, 16)
                .inputItems(GTItems.FIELD_GENERATOR_MV.asStack())
                .outputItems(NationalDispatchCenter.DEFINITION.asStack())
                .duration(600).EUt(VA[HV]).save(provider);
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTIAMod.id(Cosmodrome.ID))
                .inputItems(GTMachines.HULL[HV].asStack())
                .inputItems(TypeZeroBlocks.CONSOLE_CASING.asStack(4))
                .inputItems(CustomTags.EV_CIRCUITS, 4)
                .inputItems(GTItems.SENSOR_HV.asStack(2))
                .inputItems(GTItems.EMITTER_HV.asStack(2))
                .inputItems(TagPrefix.plate, GTMaterials.StainlessSteel, 8)
                .outputItems(Cosmodrome.DEFINITION.asStack())
                .duration(600).EUt(VA[HV]).save(provider);
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTIAMod.id(Tokamak.ID))
                .inputItems(GTMachines.HULL[GTValues.EV].asStack())
                .inputItems(TypeZeroBlocks.TOKAMAK_MAGNET_COIL.asStack(4))
                .inputItems(GTItems.ELECTRIC_PUMP_EV.asStack(4))
                .inputItems(CustomTags.EV_CIRCUITS, 4)
                .inputItems(GTItems.FIELD_GENERATOR_MV.asStack(2))
                .inputItems(TagPrefix.plateDouble, GTMaterials.Titanium, 4)
                .outputItems(Tokamak.DEFINITION.asStack())
                .duration(1200).EUt(VA[HV]).save(provider);
    }

    private static void casings(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTIAMod.id("mission_control_casing"))
                .inputItems(TagPrefix.plate, GTMaterials.Aluminium, 4)
                .inputItems(CustomTags.EV_CIRCUITS, 1)
                .inputItems(TagPrefix.frameGt, GTMaterials.StainlessSteel)
                .outputItems(TypeZeroBlocks.CONSOLE_CASING.asStack(2))
                .duration(400).EUt(VA[HV]).save(provider);
        // EN: MgB2 superconducts near 39 K, the real candidate for cheap tokamak magnets; the GTCEu superconducting
        // coil stays at LuV.
        // PT: O MgB2 supercondutor perto de 39 K é o candidato real a ímã barato de tokamak; a bobina
        // supercondutora do GTCEu fica no LuV.
        // ZH: MgB2 在约 39 K 超导，是廉价托卡马克磁体的现实候选；GTCEu 的超导线圈仍在 LuV。
        // EN: GTIA-D-425: 8 per recipe, so the 466 coils of the Tokamak (462 placed, 4 in the controller) take
        // 59 MV Field Generators instead of 233.
        // PT: GTIA-D-425: 8 por receita, para as 466 bobinas do Tokamak (462 na estrutura, 4 no controlador)
        // custarem 59 Geradores de Campo MV em vez de 233.
        // ZH: GTIA-D-425：每次配方产出 8 个，托卡马克的 466 个线圈（结构 462 个、控制器 4 个）只需 59 个 MV 力场
        // 发生器，而非 233 个。
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTIAMod.id("tokamak_magnet_coil"))
                .inputItems(TagPrefix.wireGtDouble, GTMaterials.MagnesiumDiboride, 4)
                .inputItems(GTItems.FIELD_GENERATOR_MV.asStack())
                .inputItems(TagPrefix.frameGt, GTMaterials.Titanium)
                .outputItems(TypeZeroBlocks.TOKAMAK_MAGNET_COIL.asStack(8))
                .duration(400).EUt(VA[HV]).save(provider);
        // EN: Borated polyethylene slows and catches the neutrons, lead stops the gammas.
        // PT: Polietileno borado freia e captura os nêutrons, o chumbo segura os gamas.
        // ZH: 含硼聚乙烯减速并俘获中子，铅阻挡伽马射线。
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTIAMod.id("neutron_shield_casing"))
                .inputItems(TagPrefix.plate, GTMaterials.Polyethylene, 4)
                .inputItems(TagPrefix.plate, GTMaterials.Lead, 4)
                .inputItems(TagPrefix.dust, GTMaterials.Boron)
                .inputItems(TagPrefix.frameGt, GTMaterials.Steel)
                .outputItems(TypeZeroBlocks.NEUTRON_SHIELD_CASING.asStack(2))
                .duration(200).EUt(VA[GTValues.MV]).save(provider);
        // EN: Lithium breeds tritium from the fusion neutrons; beryllium multiplies them (ITER test blanket modules).
        // PT: O lítio gera trítio com os nêutrons da fusão; o berílio os multiplica (módulos de teste de manta do
        // ITER).
        // ZH: 锂利用聚变中子增殖氚；铍使中子倍增（ITER 测试包层模块）。
        // EN: GTIA-D-425: 4 per recipe for the 444 blanket blocks of the Tokamak.
        // PT: GTIA-D-425: 4 por receita para os 444 blocos de manta do Tokamak.
        // ZH: GTIA-D-425：每次配方产出 4 个，供托卡马克的 444 块包层使用。
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTIAMod.id("lithium_breeding_blanket"))
                .inputItems(TagPrefix.plate, GTMaterials.Titanium, 4)
                .inputItems(TagPrefix.dust, GTMaterials.Lithium, 8)
                .inputItems(TagPrefix.dust, GTMaterials.Beryllium, 2)
                .outputItems(TypeZeroBlocks.LITHIUM_BREEDING_BLANKET.asStack(4))
                .duration(200).EUt(VA[HV]).save(provider);
    }

    private static void dispatchAndMissions(Consumer<FinishedRecipe> provider) {
        GTIARecipeTypes.GRID_DISPATCH.recipeBuilder(GTIAMod.id("grid_transponder"))
                .inputItems(TagPrefix.plate, GTMaterials.Aluminium, 2)
                .inputItems(CustomTags.HV_CIRCUITS, 1)
                .inputItems(TagPrefix.wireFine, GTMaterials.Gold, 4)
                .outputItems(TypeZeroItems.GRID_TRANSPONDER.asStack())
                .duration(200).EUt(VA[HV]).save(provider);
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(GTIAMod.id("lunar_probe"))
                .inputItems(GTItems.TOOL_DATA_STICK.asStack())
                .inputItems(GTItems.SENSOR_HV.asStack())
                .inputItems(GTItems.EMITTER_HV.asStack())
                .inputItems(GTItems.NOR_MEMORY_CHIP.asStack(4))
                .inputItems(TagPrefix.plate, GTMaterials.Aluminium, 4)
                .inputItems(CustomTags.HV_BATTERIES, 1)
                .outputItems(TypeZeroItems.LUNAR_PROBE.asStack())
                .cleanroom(CleanroomType.CLEANROOM)
                .duration(600).EUt(VA[HV]).save(provider);
        GTIARecipeTypes.MISSION_CONTROL.recipeBuilder(GTIAMod.id("lunar_survey"))
                .inputItems(TypeZeroItems.LUNAR_PROBE.asStack())
                .inputFluids(GTMaterials.RocketFuel.getFluid(1000))
                .outputItems(TypeZeroItems.LUNAR_SURVEY_DATA.asStack())
                .duration(600).EUt(VA[HV]).save(provider);
        // EN: Copies for the rest of the team: every player who lands carries the map.
        // PT: Cópias para o resto do time: cada jogador que pousa leva o mapa.
        // ZH: 为队伍其他成员复制：每位登月的玩家都要携带地图。
        GTIARecipeTypes.MISSION_CONTROL.recipeBuilder(GTIAMod.id("lunar_survey_copy"))
                .inputItems(TypeZeroItems.LUNAR_SURVEY_DATA.asStack())
                .inputItems(GTItems.TOOL_DATA_STICK.asStack())
                .outputItems(TypeZeroItems.LUNAR_SURVEY_DATA.asStack(2))
                .duration(200).EUt(VA[HV]).save(provider);
    }

    private static void fusion(Consumer<FinishedRecipe> provider) {
        // EN: The lithium blanket turns most of the neutrons back into tritium (90 of 100 mB); the gap is the first
        // ignition's centrifuge tritium. Liquid helium cools the magnets and leaves as gas.
        // PT: A manta de lítio devolve a maior parte dos nêutrons como trítio (90 de 100 mB); a diferença é o trítio
        // de centrífuga da primeira ignição. O hélio líquido resfria os ímãs e sai como gás.
        // ZH: 锂包层把大部分中子转回氚（100 mB 中回收 90）；差额由首次点火时离心得到的氚补足。液氦冷却磁体后以气体排出。
        GTIARecipeTypes.TOKAMAK_FUSION.recipeBuilder(GTIAMod.id("deuterium_tritium"))
                .inputItems(TagPrefix.dust, GTMaterials.Lithium)
                .inputFluids(GTMaterials.Deuterium.getFluid(100))
                .inputFluids(GTMaterials.Tritium.getFluid(TRITIUM_BURNT))
                .inputFluids(GTMaterials.Helium.getFluid(FluidStorageKeys.LIQUID, 50))
                .outputFluids(GTMaterials.Helium.getFluid(150))
                .outputFluids(GTMaterials.Tritium.getFluid(TRITIUM_BRED))
                .addData(IGNITION_KEY, DT_IGNITION_K)
                // EN: Continuous output, like the GTCEu fusion reactors: EU every tick, not once per cycle (GTIA-D-438).
                // PT: Saída contínua, como os reatores de fusão do GTCEu: EU a cada tick, não uma vez por ciclo
                // (GTIA-D-438).
                // ZH: 与 GTCEu 聚变反应堆一样持续输出：每 tick 产出 EU，而非每周期一次（GTIA-D-438）。
                .duration(FUSION_TICKS).EUt(-GTValues.V[HV], DT_AMPS).save(provider);
        GTIARecipeTypes.TOKAMAK_FUSION.recipeBuilder(GTIAMod.id("deuterium_helium_3"))
                .inputFluids(GTMaterials.Deuterium.getFluid(100))
                .inputFluids(GTMaterials.Helium3.getFluid(100))
                .inputFluids(GTMaterials.Helium.getFluid(FluidStorageKeys.LIQUID, 50))
                .outputFluids(GTMaterials.Helium.getFluid(150))
                .outputFluids(GTMaterials.Hydrogen.getFluid(100))
                .addData(IGNITION_KEY, DHE3_IGNITION_K)
                .duration(FUSION_TICKS).EUt(-GTValues.V[HV], DHE3_AMPS).save(provider);
    }

    private static void heavyWater(Consumer<FinishedRecipe> provider) {
        // EN: Girdler sulfide process: deuterium moves from hot hydrogen sulfide into cold water; most H2S returns.
        // PT: Processo Girdler: o deutério passa do sulfeto de hidrogênio quente para a água fria; quase todo o H2S
        // volta.
        // ZH: Girdler 硫化氢法：氘从热硫化氢转移到冷水中；大部分硫化氢回收。
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(GTIAMod.id("heavy_water"))
                .inputFluids(GTMaterials.Water.getFluid(1000))
                .inputFluids(GTMaterials.HydrogenSulfide.getFluid(50))
                .outputFluids(TypeZeroMaterials.HEAVY_WATER.getFluid(50))
                .outputFluids(GTMaterials.HydrogenSulfide.getFluid(45))
                .duration(200).EUt(VA[HV]).save(provider);
        GTRecipeTypes.ELECTROLYZER_RECIPES.recipeBuilder(GTIAMod.id("heavy_water_electrolysis"))
                .inputFluids(TypeZeroMaterials.HEAVY_WATER.getFluid(1000))
                .outputFluids(GTMaterials.Deuterium.getFluid(2000))
                .outputFluids(GTMaterials.Oxygen.getFluid(1000))
                .duration(1500).EUt(VA[GTValues.LV]).save(provider);
    }
}
