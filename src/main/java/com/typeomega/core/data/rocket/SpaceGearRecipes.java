package com.typeomega.core.data.rocket;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.chem.ZipDMaterials;

import java.util.function.Consumer;

/**
 * EN: Ad Astra's suits, oxygen gear, base blocks and desh parts through GT machines, with no ostrum, netherite or
 * etrium (D-420, D-421, D-423). {@link AdAstraRecipeFilter} drops Ad Astra's own recipes for the same items.
 * PT: Trajes, equipamento de oxigênio, blocos da base e peças de desh do Ad Astra pelas máquinas do GT, sem ostrum,
 * netherite nem etrium (D-420, D-421, D-423). {@link AdAstraRecipeFilter} tira as receitas do próprio Ad Astra
 * para os mesmos itens.
 * ZH: Ad Astra 的宇航服、氧气设备、基地方块和戴什部件改由 GT 机器制作，不用紫金、下界合金或 etrium（D-420、
 * D-421、D-423）。AdAstraRecipeFilter 移除 Ad Astra 自带的同名物品配方。
 */
public final class SpaceGearRecipes {

    private static final int MV = 120;
    private static final int HV = 480;

    private SpaceGearRecipes() {}

    public static void addRecipes(Consumer<FinishedRecipe> provider) {
        if (!ModList.get().isLoaded("ad_astra")) return;
        parts(provider);
        base(provider);
        suits(provider);
        desh(provider);
    }

    private static void parts(Consumer<FinishedRecipe> provider) {
        assembler("gas_tank", MV, 200)
                .inputItems(GTItems.FLUID_CELL_LARGE_STEEL.asStack())
                .inputItems(TagPrefix.pipeNormalFluid, GTMaterials.StainlessSteel)
                .inputItems(TagPrefix.plate, GTMaterials.Steel, 2)
                .outputItems(stack("gas_tank")).save(provider);
        assembler("fan", MV, 200)
                .inputItems(TagPrefix.rotor, GTMaterials.StainlessSteel)
                .inputItems(GTItems.ELECTRIC_MOTOR_MV.asStack())
                .inputItems(TagPrefix.plate, GTMaterials.Steel, 2)
                .outputItems(stack("fan")).save(provider);
        assembler("engine_frame", MV, 300)
                .inputItems(TagPrefix.frameGt, GTMaterials.Steel)
                .inputItems(TagPrefix.rod, GTMaterials.StainlessSteel, 4)
                .inputItems(TagPrefix.plateDouble, GTMaterials.Steel, 2)
                .outputItems(stack("engine_frame")).save(provider);
        assembler("oxygen_gear", HV, 300)
                .inputItems(GTItems.ELECTRIC_PUMP_HV.asStack())
                .inputItems(TagPrefix.rotor, GTMaterials.StainlessSteel)
                .inputItems(TagPrefix.pipeNormalFluid, GTMaterials.Polytetrafluoroethylene)
                .outputItems(stack("oxygen_gear")).save(provider);
    }

    private static void base(Consumer<FinishedRecipe> provider) {
        assembler("oxygen_loader", HV, 400)
                .inputItems(GTMachines.HULL[GTValues.HV].asStack())
                .inputItems(stack("gas_tank", 2)).inputItems(stack("fan"))
                .inputItems(GTItems.ELECTRIC_PUMP_HV.asStack())
                .inputItems(CustomTags.HV_CIRCUITS, 2)
                .outputItems(stack("oxygen_loader")).save(provider);
        // EN: The pad is a slab of concrete on a steel frame; the Rocket Assembler takes nine.
        // PT: A plataforma é uma laje de concreto sobre moldura de aço; a Montadora de Foguetes leva nove.
        // ZH: 发射台是钢框架上的混凝土板；火箭装配厂需要九块。
        assembler("launch_pad", MV, 200)
                .inputItems(TagPrefix.frameGt, GTMaterials.Steel)
                .inputItems(TagPrefix.plate, GTMaterials.Steel, 4)
                .inputFluids(GTMaterials.Concrete.getFluid(576))
                .outputItems(stack("launch_pad")).save(provider);
        assembler("airlock", MV, 300).circuitMeta(1)
                .inputItems(TagPrefix.plate, GTMaterials.StainlessSteel, 4)
                .inputItems(GTBlocks.CASING_TEMPERED_GLASS.asStack(2))
                .inputItems(GTItems.ELECTRIC_PISTON_MV.asStack())
                .outputItems(stack("airlock")).save(provider);
        assembler("steel_sliding_door", MV, 300).circuitMeta(2)
                .inputItems(TagPrefix.plate, GTMaterials.StainlessSteel, 4)
                .inputItems(GTBlocks.CASING_TEMPERED_GLASS.asStack(2))
                .inputItems(GTItems.ELECTRIC_PISTON_MV.asStack())
                .outputItems(stack("steel_sliding_door")).save(provider);
        assembler("oxygen_sensor", HV, 300)
                .inputItems(GTItems.SENSOR_HV.asStack()).inputItems(stack("fan"))
                .inputItems(TagPrefix.plate, GTMaterials.StainlessSteel, 4)
                .outputItems(stack("oxygen_sensor")).save(provider);
        assembler("gravity_normalizer", HV, 600)
                .inputItems(GTItems.FIELD_GENERATOR_MV.asStack())
                .inputItems(TagPrefix.plate, SpaceMaterials.DESH, 4)
                .inputItems(TagPrefix.block, GTMaterials.Steel)
                .inputItems(CustomTags.HV_CIRCUITS, 2)
                .outputItems(stack("gravity_normalizer")).save(provider);
    }

    private static void suits(Consumer<FinishedRecipe> provider) {
        // EN: Tier 1 suit: silicone rubber shell, PTFE thermal layer, tempered glass visor (vacuum and -173 °C).
        // PT: Traje T1: casca de borracha de silicone, camada térmica de PTFE, visor de vidro temperado (vácuo e
        // -173 °C).
        // ZH: 一级宇航服：硅橡胶外壳、PTFE 隔热层、钢化玻璃面罩（真空与 -173 °C）。
        assembler("space_helmet", HV, 300).circuitMeta(1)
                .inputItems(TagPrefix.plate, GTMaterials.SiliconeRubber, 4)
                .inputItems(TagPrefix.plate, GTMaterials.Polytetrafluoroethylene, 2)
                .inputItems(GTBlocks.CASING_TEMPERED_GLASS.asStack())
                .outputItems(stack("space_helmet")).save(provider);
        assembler("space_suit", HV, 400).circuitMeta(2)
                .inputItems(TagPrefix.plate, GTMaterials.SiliconeRubber, 6)
                .inputItems(TagPrefix.plate, GTMaterials.Polytetrafluoroethylene, 4)
                .inputItems(stack("oxygen_gear")).inputItems(stack("gas_tank", 2))
                .outputItems(stack("space_suit")).save(provider);
        assembler("space_pants", HV, 300).circuitMeta(3)
                .inputItems(TagPrefix.plate, GTMaterials.SiliconeRubber, 5)
                .inputItems(TagPrefix.plate, GTMaterials.Polytetrafluoroethylene, 3)
                .outputItems(stack("space_pants")).save(provider);
        assembler("space_boots", HV, 200).circuitMeta(4)
                .inputItems(TagPrefix.plate, GTMaterials.SiliconeRubber, 3)
                .inputItems(TagPrefix.plate, GTMaterials.Polytetrafluoroethylene, 2)
                .outputItems(stack("space_boots")).save(provider);

        // EN: Thermal suit (Ad Astra's netherite suit, renamed): the tier 1 piece wrapped in titanium and silica
        // aerogel,
        // the insulation of the real Mars rovers.
        // PT: Traje térmico (o traje de netherite do Ad Astra, renomeado): a peça T1 envolta em titânio e aerogel de
        // sílica, o isolante dos rovers reais de Marte.
        // ZH: 隔热宇航服（Ad Astra 的下界合金宇航服改名）：一级部件包裹钛与二氧化硅气凝胶，即真实火星车的隔热材料。
        thermal("helmet", 2, 2).save(provider);
        thermal("suit", 4, 4).inputItems(stack("large_gas_tank", 2)).inputItems(stack("oxygen_gear"))
                .save(provider);
        thermal("pants", 3, 3).save(provider);
        thermal("boots", 2, 2).save(provider);
    }

    private static GTRecipeBuilder thermal(String piece, int titanium, int aerogel) {
        return assembler("netherite_space_" + piece, HV, 400)
                .inputItems(stack("space_" + piece))
                .inputItems(TagPrefix.plate, GTMaterials.Titanium, titanium)
                .inputItems(TagPrefix.dust, ZipDMaterials.SILICA_AEROGEL, aerogel)
                .outputItems(stack("netherite_space_" + piece));
    }

    private static void desh(Consumer<FinishedRecipe> provider) {
        // EN: Desh only leaves the ore through GT: crushed raw desh, then the HV EBF (SpaceMaterials). Ad Astra's
        // furnace recipes are dropped.
        // PT: O desh só sai do minério pelo GT: desh bruto moído e depois o EBF HV (SpaceMaterials). As receitas de
        // fornalha do Ad Astra saem.
        // ZH: 戴什只能经 GT 从矿石中提取：粉碎粗戴什，再进入 HV 电力高炉（SpaceMaterials）。移除 Ad Astra 的熔炉配方。
        GTRecipeTypes.MACERATOR_RECIPES.recipeBuilder(TypeOmegaMod.id("rocket/raw_desh"))
                .inputItems(stack("raw_desh"))
                .outputItems(TagPrefix.dust, SpaceMaterials.DESH)
                .chancedOutput(TagPrefix.dust, SpaceMaterials.DESH, 1, 2500, 0)
                .duration(200).EUt(MV).save(provider);
        GTRecipeTypes.MACERATOR_RECIPES.recipeBuilder(TypeOmegaMod.id("rocket/desh_ore"))
                .inputItems(ItemTags.create(new ResourceLocation("forge", "ores/desh")))
                .outputItems(stack("raw_desh", 2))
                .duration(200).EUt(MV).save(provider);
        assembler("desh_tank", HV, 400)
                .inputItems(stack("steel_tank"))
                .inputItems(TagPrefix.plateDouble, SpaceMaterials.DESH, 4)
                .inputItems(TagPrefix.pipeNormalFluid, GTMaterials.Titanium)
                .outputItems(stack("desh_tank")).save(provider);
        assembler("desh_engine", HV, 600)
                .inputItems(stack("steel_engine")).inputItems(stack("fan"))
                .inputItems(GTItems.ELECTRIC_MOTOR_EV.asStack(2))
                .inputItems(TagPrefix.plate, SpaceMaterials.DESH, 4)
                .outputItems(stack("desh_engine")).save(provider);
    }

    private static GTRecipeBuilder assembler(String name, int eut, int duration) {
        return GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(TypeOmegaMod.id("rocket/" + name)).duration(duration)
                .EUt(eut);
    }

    static ItemStack stack(String path) {
        return stack(path, 1);
    }

    static ItemStack stack(String path, int count) {
        Item item = TypeOmegaRocketRecipes.item(path);
        if (item == null) throw new IllegalStateException("Ad Astra item missing: ad_astra:" + path);
        return new ItemStack(item, count);
    }
}
