package com.typeomega.core.chem;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import net.minecraft.data.recipes.FinishedRecipe;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.data.rocket.SpaceMaterials;

import java.util.function.Consumer;

import static com.typeomega.core.chem.ZipDMaterials.*;

/**
 * EN: Zip-D, the tier 2 rocket fuel (D-424, COMBUSTIVEL_T2_v01 option C): borax to boric acid, boron
 * trifluoride (two routes), sodium borohydride by Brown-Schlesinger, diborane (two routes), deshaborane over a desh
 * bed and the mix with liquid oxygen. Sodium methoxide, sodium tetrafluoroborate and the salt go back to their
 * sources. Machines stay at HV or below (D-421).
 * PT: Zip-D, o combustível do foguete T2 (D-424, opção C da COMBUSTIVEL_T2_v01): bórax a ácido bórico,
 * trifluoreto de boro (duas rotas), borohidreto de sódio por Brown-Schlesinger, diborano (duas rotas), deshaborano
 * sobre leito de desh e a mistura com oxigênio líquido. Metóxido de sódio, tetrafluoroborato de sódio e o sal voltam às
 * fontes. Máquinas até HV (D-421).
 * ZH: 二级火箭燃料 Zip-D（D-424，COMBUSTIVEL_T2_v01 方案 C）：硼砂制硼酸、三氟化硼（两条路线）、
 * Brown-Schlesinger 法硼氢化钠、乙硼烷（两条路线）、戴什床上的戴什硼烷，以及与液氧混合。甲醇钠、四氟硼酸钠和盐
 * 回到各自来源。机器不超过 HV（D-421）。
 */
public final class ZipDRecipes {

    private static final int LV = 30;
    private static final int MV = 120;
    private static final int HV = 480;

    private ZipDRecipes() {}

    public static void addRecipes(Consumer<FinishedRecipe> provider) {
        boron(provider);
        borohydride(provider);
        diborane(provider);
        fuel(provider);
        aerogel(provider);
    }

    private static void boron(Consumer<FinishedRecipe> provider) {
        // Na2B4O7 + 2 HCl + 5 H2O -> 4 H3BO3 + 2 NaCl
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(TypeOmegaMod.id("zip_d/boric_acid"))
                .inputItems(TagPrefix.dust, GTMaterials.Borax)
                .inputFluids(GTMaterials.HydrochloricAcid.getFluid(2000))
                .inputFluids(GTMaterials.Water.getFluid(5000))
                .outputItems(TagPrefix.dust, BORIC_ACID, 4)
                .outputItems(TagPrefix.dust, GTMaterials.Salt, 2)
                .duration(200).EUt(LV).save(provider);
        // H3BO3 + 3 HF -> BF3 + 3 H2O
        GTRecipeTypes.LARGE_CHEMICAL_RECIPES.recipeBuilder(TypeOmegaMod.id("zip_d/boron_trifluoride"))
                .inputItems(TagPrefix.dust, BORIC_ACID)
                .inputFluids(GTMaterials.HydrofluoricAcid.getFluid(3000))
                .outputFluids(BORON_TRIFLUORIDE.getFluid(1000))
                .outputFluids(GTMaterials.Water.getFluid(3000))
                .duration(300).EUt(MV).save(provider);
        // EN: Second route: boron burnt in fluorine; dearer fluorine, no water to handle.
        // PT: Segunda rota: boro queimado em flúor; gasta mais flúor, sem água para tratar.
        // ZH: 第二条路线：硼在氟中燃烧；消耗更多氟，无需处理水。
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(TypeOmegaMod.id("zip_d/boron_trifluoride_direct"))
                .inputItems(TagPrefix.dust, GTMaterials.Boron)
                .inputFluids(GTMaterials.Fluorine.getFluid(3000))
                .outputFluids(BORON_TRIFLUORIDE.getFluid(1000))
                .duration(100).EUt(HV).save(provider);
        // B + 3 Cl -> BCl3, for the second diborane route
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(TypeOmegaMod.id("zip_d/boron_trichloride"))
                .inputItems(TagPrefix.dust, GTMaterials.Boron)
                .inputFluids(GTMaterials.Chlorine.getFluid(3000))
                .outputFluids(BORON_TRICHLORIDE.getFluid(1000))
                .duration(160).EUt(MV).save(provider);
    }

    private static void borohydride(Consumer<FinishedRecipe> provider) {
        // H3BO3 + 3 CH3OH -> B(OCH3)3 + 3 H2O
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(TypeOmegaMod.id("zip_d/trimethyl_borate"))
                .inputItems(TagPrefix.dust, BORIC_ACID)
                .inputFluids(GTMaterials.Methanol.getFluid(3000))
                .outputFluids(TRIMETHYL_BORATE.getFluid(1000))
                .outputFluids(GTMaterials.Water.getFluid(3000))
                .duration(200).EUt(MV).save(provider);
        // 2 Na + H2 -> 2 NaH
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(TypeOmegaMod.id("zip_d/sodium_hydride"))
                .inputItems(TagPrefix.dust, GTMaterials.Sodium, 2)
                .inputFluids(GTMaterials.Hydrogen.getFluid(1000))
                .outputItems(TagPrefix.dust, SODIUM_HYDRIDE, 2)
                .duration(100).EUt(MV).save(provider);
        // EN: 4 NaH + B(OCH3)3 -> NaBH4 + 3 NaOCH3, Brown-Schlesinger at ~260 °C: the heated step runs at HV.
        // PT: 4 NaH + B(OCH3)3 -> NaBH4 + 3 NaOCH3, Brown-Schlesinger a ~260 °C: o passo aquecido roda no HV.
        // ZH: 4 NaH + B(OCH3)3 -> NaBH4 + 3 NaOCH3，Brown-Schlesinger 法约 260 °C：加热步骤在 HV 运行。
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(TypeOmegaMod.id("zip_d/sodium_borohydride"))
                .inputItems(TagPrefix.dust, SODIUM_HYDRIDE, 4)
                .inputFluids(TRIMETHYL_BORATE.getFluid(1000))
                .outputItems(TagPrefix.dust, SODIUM_BOROHYDRIDE)
                .outputItems(TagPrefix.dust, SODIUM_METHOXIDE, 3)
                .duration(400).EUt(HV).save(provider);
        // Closed loop: NaOCH3 + H2O -> NaOH + CH3OH
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(TypeOmegaMod.id("zip_d/sodium_methoxide_hydrolysis"))
                .inputItems(TagPrefix.dust, SODIUM_METHOXIDE)
                .inputFluids(GTMaterials.Water.getFluid(1000))
                .outputItems(TagPrefix.dust, GTMaterials.SodiumHydroxide)
                .outputFluids(GTMaterials.Methanol.getFluid(1000))
                .duration(80).EUt(LV).save(provider);
    }

    private static void diborane(Consumer<FinishedRecipe> provider) {
        // EN: 3 NaBH4 + 4 BF3 -> 2 B2H6 + 3 NaBF4 under argon: diborane ignites in air. The argon is not used up.
        // PT: 3 NaBH4 + 4 BF3 -> 2 B2H6 + 3 NaBF4 sob argônio: o diborano pega fogo no ar. O argônio não é gasto.
        // ZH: 3 NaBH4 + 4 BF3 -> 2 B2H6 + 3 NaBF4，在氩气下进行：乙硼烷在空气中会自燃。氩气不消耗。
        GTRecipeTypes.LARGE_CHEMICAL_RECIPES.recipeBuilder(TypeOmegaMod.id("zip_d/diborane"))
                .inputItems(TagPrefix.dust, SODIUM_BOROHYDRIDE, 3)
                .inputFluids(BORON_TRIFLUORIDE.getFluid(4000))
                .notConsumableFluid(GTMaterials.Argon.getFluid(1000))
                .outputFluids(DIBORANE.getFluid(2000))
                .outputItems(TagPrefix.dust, SODIUM_TETRAFLUOROBORATE, 3)
                .duration(400).EUt(HV).save(provider);
        // EN: Second route, 2 BCl3 + 6 H2 -> B2H6 + 6 HCl. The proposal puts it in the EBF at 1200 K, but the EBF takes
        // one fluid; the Large Chemical Reactor at HV stands in for the hot step.
        // PT: Segunda rota, 2 BCl3 + 6 H2 -> B2H6 + 6 HCl. A proposta põe no EBF a 1200 K, mas o EBF aceita um fluido
        // só; o Grande Reator Químico no HV faz o passo quente.
        // ZH: 第二条路线，2 BCl3 + 6 H2 -> B2H6 + 6 HCl。提案放在 1200 K 的电力高炉，但高炉只接受一种流体；
        // 改由 HV 大型化学反应釜完成高温步骤。
        GTRecipeTypes.LARGE_CHEMICAL_RECIPES.recipeBuilder(TypeOmegaMod.id("zip_d/diborane_from_trichloride"))
                .inputFluids(BORON_TRICHLORIDE.getFluid(2000))
                .inputFluids(GTMaterials.Hydrogen.getFluid(6000))
                .outputFluids(DIBORANE.getFluid(1000))
                .outputFluids(GTMaterials.HydrochloricAcid.getFluid(6000))
                .duration(600).EUt(HV).save(provider);
        // Closed loop: NaBF4 -> Na + B + 4 F
        GTRecipeTypes.ELECTROLYZER_RECIPES.recipeBuilder(TypeOmegaMod.id("zip_d/sodium_tetrafluoroborate_electrolysis"))
                .inputItems(TagPrefix.dust, SODIUM_TETRAFLUOROBORATE)
                .outputItems(TagPrefix.dust, GTMaterials.Sodium)
                .outputItems(TagPrefix.dust, GTMaterials.Boron)
                .outputFluids(GTMaterials.Fluorine.getFluid(4000))
                .duration(240).EUt(MV).save(provider);
    }

    private static void fuel(Consumer<FinishedRecipe> provider) {
        // EN: 5 B2H6 -> 2 B5H9 + 6 H2 at ~200 °C over a bed of desh dust. Desh is a catalyst worn away slowly
        // (D-424): one dust in ten runs, so the Moon feeds the fuel without becoming its bottleneck.
        // PT: 5 B2H6 -> 2 B5H9 + 6 H2 a ~200 °C sobre leito de pó de desh. O desh é catalisador gasto devagar
        // (D-424): um pó a cada dez execuções, então a Lua alimenta o combustível sem virar o gargalo.
        // ZH: 5 B2H6 -> 2 B5H9 + 6 H2，约 200 °C 下流经戴什粉床。戴什是缓慢损耗的催化剂（D-424）：
        // 每十次消耗一份粉，月球供应燃料而不成为瓶颈。
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(TypeOmegaMod.id("zip_d/deshaborane"))
                .chancedInput(ChemicalHelper.get(TagPrefix.dust, SpaceMaterials.DESH), 1000, 0)
                .inputFluids(DIBORANE.getFluid(2500))
                .outputFluids(DESHABORANE.getFluid(1000))
                .outputFluids(GTMaterials.Hydrogen.getFluid(3000))
                .duration(400).EUt(HV).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(TypeOmegaMod.id("zip_d/zip_d_rocket_fuel"))
                .inputFluids(DESHABORANE.getFluid(1000))
                .inputFluids(GTMaterials.Oxygen.getFluid(FluidStorageKeys.LIQUID, 2000))
                .outputFluids(ZIP_D_ROCKET_FUEL.getFluid(3000))
                .duration(200).EUt(HV).save(provider);
    }

    private static void aerogel(Consumer<FinishedRecipe> provider) {
        // EN: Silica sol-gel in ethanol, then supercritical CO2 drying in the Autoclave; most of the ethanol comes
        // back.
        // PT: Sol-gel de sílica em etanol e secagem com CO2 supercrítico na Autoclave; quase todo o etanol volta.
        // ZH: 二氧化硅在乙醇中溶胶-凝胶，再于高压釜中用超临界 CO2 干燥；大部分乙醇可回收。
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(TypeOmegaMod.id("silica_alcogel"))
                .inputItems(TagPrefix.dust, GTMaterials.SiliconDioxide)
                .inputFluids(GTMaterials.Ethanol.getFluid(1000))
                .outputItems(TagPrefix.dust, SILICA_ALCOGEL)
                .duration(200).EUt(MV).save(provider);
        GTRecipeTypes.AUTOCLAVE_RECIPES.recipeBuilder(TypeOmegaMod.id("silica_aerogel"))
                .inputItems(TagPrefix.dust, SILICA_ALCOGEL)
                .inputFluids(GTMaterials.CarbonDioxide.getFluid(1000))
                .outputItems(TagPrefix.dust, SILICA_AEROGEL)
                .outputFluids(GTMaterials.Ethanol.getFluid(900))
                .duration(400).EUt(HV).save(provider);
    }
}
