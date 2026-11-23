package com.typeomega.core.gametest;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

import com.typeomega.core.GTIAMod;
import com.typeomega.core.chem.ZipDMaterials;
import com.typeomega.core.data.rocket.SpaceMaterials;

import java.util.List;
import java.util.Optional;

/**
 * EN: Ad Astra through GT (GTIA-D-420, D-421, D-424): fuel tags, dropped shortcuts, GT recipes for the space gear,
 * desh as one material, the Zip-D line at HV and the tier 2 rocket. Each test passes trivially without Ad Astra,
 * except the Zip-D line, which is the pack's own.
 * PT: Ad Astra pelo GT (GTIA-D-420, D-421, D-424): tags de combustível, atalhos removidos, receitas GT do equipamento
 * espacial, desh como um material só, a linha do Zip-D no HV e o foguete T2. Sem o Ad Astra os testes passam direto,
 * menos a linha do Zip-D, que é do próprio pack.
 * ZH: 通过 GT 使用 Ad Astra（GTIA-D-420、D-421、D-424）：燃料标签、移除的捷径、太空装备的 GT 配方、唯一的戴什材料、
 * HV 的 Zip-D 生产线以及二级火箭。未加载 Ad Astra 时测试直接通过，Zip-D 生产线除外，它属于整合包自身。
 */
@PrefixGameTestTemplate(false)
@GameTestHolder("typeomega")
public final class SpaceGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";
    private static final BlockPos MACHINE_POS = new BlockPos(2, 1, 2);

    /** GT recipes that replace Ad Astra's crafting, by item name (typeomega:rocket/<name> makes ad_astra:<name>). */
    private static final List<String> GT_GEAR = List.of("gas_tank", "fan", "engine_frame", "oxygen_gear",
            "oxygen_loader", "launch_pad", "airlock", "steel_sliding_door", "oxygen_sensor", "gravity_normalizer",
            "space_helmet", "space_suit", "space_pants", "space_boots", "netherite_space_helmet",
            "netherite_space_suit", "netherite_space_pants", "netherite_space_boots", "desh_tank", "desh_engine");

    private static final List<String> ZIP_D_LINE = List.of("boric_acid", "boron_trifluoride",
            "boron_trifluoride_direct", "boron_trichloride", "trimethyl_borate", "sodium_hydride", "sodium_borohydride",
            "sodium_methoxide_hydrolysis", "diborane", "diborane_from_trichloride",
            "sodium_tetrafluoroborate_electrolysis", "deshaborane", "zip_d_rocket_fuel");

    private SpaceGameTests() {}

    @GameTest(template = TEMPLATE, batch = "space", timeoutTicks = 20)
    public static void rocketFuelTagsHoldOnlyThePackFuels(GameTestHelper h) {
        if (!adAstra(h)) return;
        var tier1 = FluidTags.create(new ResourceLocation("ad_astra", "tier_1_rocket_fuel"));
        var fuel = FluidTags.create(new ResourceLocation("ad_astra", "fuel"));
        var tier2 = FluidTags.create(new ResourceLocation("ad_astra", "tier_2_rocket_fuel"));
        Fluid rocketFuel = GTMaterials.RocketFuel.getFluid(), zipD = ZipDMaterials.ZIP_D_ROCKET_FUEL.getFluid();
        h.assertTrue(rocketFuel.is(tier1) && rocketFuel.is(fuel), "GT rocket fuel fuels the tier 1 rocket");
        h.assertFalse(GTMaterials.Diesel.getFluid().is(tier1) || GTMaterials.Diesel.getFluid().is(fuel),
                "diesel no longer fuels a rocket");
        var adAstraFuel = ForgeRegistries.FLUIDS.getValue(new ResourceLocation("ad_astra", "fuel"));
        h.assertFalse(adAstraFuel != null && adAstraFuel.is(tier1), "Ad Astra's own fuel is out of the tier 1 tag");
        h.assertTrue(zipD.is(tier2), "Zip-D fuels the tier 2 rocket");
        h.assertFalse(rocketFuel.is(tier2) || zipD.is(tier1), "each rocket tier takes only its own fuel");
        h.assertTrue(BuiltInRegistries.FLUID.getTag(tier2).map(t -> t.size()).orElse(0) == 1,
                "the tier 2 tag holds Zip-D alone");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "space", timeoutTicks = 20)
    public static void adAstraShortcutsAreGone(GameTestHelper h) {
        if (!adAstra(h)) return;
        for (String id : List.of("fuel_refinery", "compressor", "etrionic_blast_furnace", "coal_generator",
                "refining/fuel_from_refining_oil", "alloying/steel_ingot_from_alloying_iron_ingot_and_coals",
                "compressing/steel_plate_from_compressing_steel_ingots",
                "nasa_workbench/tier_2_rocket_from_nasa_workbench", "nasa_workbench/tier_3_rocket_from_nasa_workbench",
                "nasa_workbench/tier_4_rocket_from_nasa_workbench", "space_suit", "netherite_space_suit",
                "oxygen_sensor", "gravity_normalizer", "smelting/desh_ingot_from_smelting_raw_desh",
                "blasting/desh_ingot_from_blasting_moon_desh_ore"))
            h.assertTrue(recipe(h, new ResourceLocation("ad_astra", id)).isEmpty(), "ad_astra:" + id + " is dropped");
        h.assertTrue(recipe(h, new ResourceLocation("ad_astra", "steel_block")).isPresent(),
                "unrelated Ad Astra recipes stay");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "space", timeoutTicks = 20)
    public static void spaceGearIsMadeInGtWithoutOstrumOrNetherite(GameTestHelper h) {
        if (!adAstra(h)) return;
        for (String name : GT_GEAR) {
            var gt = gtRecipe(h, "rocket/" + name);
            Item item = adAstraItem(name);
            h.assertTrue(gt.getOutputContents(ItemRecipeCapability.CAP).stream()
                    .anyMatch(c -> ItemRecipeCapability.CAP.of(c.getContent()).test(new ItemStack(item))),
                    "typeomega:rocket/" + name + " makes ad_astra:" + name);
            for (var input : gt.getInputContents(ItemRecipeCapability.CAP))
                for (var stack : ItemRecipeCapability.CAP.of(input.getContent()).getItems()) {
                    String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
                    h.assertFalse(path.contains("ostrum") || path.contains("calorite") ||
                            path.contains("etrionic") || path.startsWith("netherite_ingot") ||
                            path.equals("netherite_chestplate"),
                            name + " takes no ostrum, calorite, etrium or netherite: " + path);
                }
            h.assertTrue(gt.getInputEUt().getTotalEU() <= GTValues.VA[GTValues.HV], name + " stays at HV or below");
        }
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "space", timeoutTicks = 20)
    public static void deshAndSteelAreOneMaterialEach(GameTestHelper h) {
        if (!adAstra(h)) return;
        h.assertTrue(ChemicalHelper.get(TagPrefix.ingot, SpaceMaterials.DESH).is(adAstraItem("desh_ingot")),
                "GT's desh ingot is Ad Astra's");
        ItemStack plate = new ItemStack(adAstraItem("desh_plate"));
        h.assertTrue(ChemicalHelper.get(TagPrefix.plate, SpaceMaterials.DESH).is(plate.getItem()),
                "GT's desh plate is Ad Astra's");
        var manager = h.getLevel().getServer().getRecipeManager();
        h.assertTrue(manager.getAllRecipesFor(GTRecipeTypes.BENDER_RECIPES).stream()
                .anyMatch(r -> r.getOutputContents(ItemRecipeCapability.CAP).stream()
                        .anyMatch(c -> ItemRecipeCapability.CAP.of(c.getContent()).test(plate))),
                "the Bender makes the desh plate");
        h.assertTrue(manager.getAllRecipesFor(GTRecipeTypes.BLAST_RECIPES).stream()
                .anyMatch(r -> r.data.getInt("ebf_temp") == 1700 && r.getOutputContents(ItemRecipeCapability.CAP)
                        .stream().anyMatch(c -> ItemRecipeCapability.CAP.of(c.getContent())
                                .test(new ItemStack(adAstraItem("desh_ingot"))))),
                "desh is smelted in the EBF at 1700 K");
        gtRecipe(h, "rocket/raw_desh");
        h.assertTrue(new ItemStack(adAstraItem("steel_plate"))
                .is(ItemTags.create(new ResourceLocation("forge", "plates/steel"))), "Ad Astra steel is GT steel");
        h.assertTrue(ChemicalHelper.get(TagPrefix.plate, GTMaterials.Steel)
                .is(ItemTags.create(new ResourceLocation("ad_astra", "steel_plates"))), "GT steel is Ad Astra steel");
        h.succeed();
    }

    /** Zip-D line: every step exists and runs at HV or below; nothing in it is EV (GTIA-D-421). */
    @GameTest(template = TEMPLATE, batch = "space", timeoutTicks = 20)
    public static void zipDLineStaysAtHv(GameTestHelper h) {
        for (String step : ZIP_D_LINE) {
            var recipe = gtRecipe(h, "zip_d/" + step);
            h.assertTrue(recipe.getInputEUt().getTotalEU() <= GTValues.VA[GTValues.HV], step + " stays at HV");
        }
        var mix = gtRecipe(h, "zip_d/zip_d_rocket_fuel");
        h.assertTrue(mix.getOutputContents(FluidRecipeCapability.CAP).stream().anyMatch(c -> FluidRecipeCapability.CAP
                .of(c.getContent()).test(ZipDMaterials.ZIP_D_ROCKET_FUEL.getFluid(1))), "the mixer makes Zip-D");
        gtRecipe(h, "silica_aerogel");
        h.succeed();
    }

    /** Desh is a catalyst worn away slowly (GTIA-D-424): ten runs of the HV reactor spend about one dust. */
    @GameTest(template = TEMPLATE, batch = "space", timeoutTicks = 20)
    public static void deshCatalystWearsSlowly(GameTestHelper h) {
        var reactor = reactor(h);
        ItemStack desh = ChemicalHelper.get(TagPrefix.dust, SpaceMaterials.DESH, 64);
        reactor.importItems.setStackInSlot(0, desh);
        int runs = 20;
        for (int i = 0; i < runs; i++) {
            reactor.importFluids.setFluidInTank(0, ZipDMaterials.DIBORANE.getFluid(2500));
            for (int tank = 0; tank < reactor.exportFluids.getTanks(); tank++)
                reactor.exportFluids.setFluidInTank(tank, FluidStack.EMPTY);
            reactor.energyContainer.setEnergyStored(reactor.energyContainer.getEnergyCapacity());
            reactor.recipeLogic.findAndHandleRecipe();
            h.assertTrue(reactor.recipeLogic.getLastRecipe() != null &&
                    reactor.recipeLogic.getLastRecipe().getId().getPath().endsWith("/zip_d/deshaborane"),
                    "the HV reactor turns diborane into deshaborane");
            reactor.recipeLogic.onRecipeFinish();
        }
        int spent = 64 - reactor.importItems.getStackInSlot(0).getCount();
        h.assertTrue(spent < runs / 2, "desh is not an ingredient: " + spent + " dust in " + runs + " runs");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "space", timeoutTicks = 20)
    public static void deshaboraneNeedsDesh(GameTestHelper h) {
        var reactor = reactor(h);
        reactor.importFluids.setFluidInTank(0, ZipDMaterials.DIBORANE.getFluid(2500));
        reactor.recipeLogic.findAndHandleRecipe();
        h.assertFalse(reactor.recipeLogic.isWorking(), "no desh bed, no deshaborane");
        h.succeed();
    }

    /** The tier 2 rocket: desh parts and Zip-D at 1920 EU/t, two HV Energy Hatches and not one (GTIA-D-421). */
    @GameTest(template = TEMPLATE, batch = "space", timeoutTicks = 20)
    public static void tier2RocketNeedsDeshZipDAndTwoHvHatches(GameTestHelper h) {
        if (!adAstra(h)) return;
        var rocket = gtRecipe(h, "rocket/tier_2_rocket");
        long eut = rocket.getInputEUt().getTotalEU();
        h.assertTrue(eut <= 2 * 2 * GTValues.V[GTValues.HV] && eut > 2 * GTValues.V[GTValues.HV],
                "two HV hatches power the tier 2 rocket, one does not");
        h.assertTrue(rocket.getInputContents(FluidRecipeCapability.CAP).stream().anyMatch(c -> FluidRecipeCapability.CAP
                .of(c.getContent()).test(ZipDMaterials.ZIP_D_ROCKET_FUEL.getFluid(1))),
                "the rocket is fuelled with Zip-D");
        for (String part : List.of("desh_tank", "desh_engine"))
            h.assertTrue(
                    rocket.getInputContents(ItemRecipeCapability.CAP).stream().anyMatch(c -> ItemRecipeCapability.CAP
                            .of(c.getContent()).test(new ItemStack(adAstraItem(part)))),
                    "the rocket takes the " + part);
        h.assertTrue(rocket.getOutputContents(ItemRecipeCapability.CAP).stream().anyMatch(c -> ItemRecipeCapability.CAP
                .of(c.getContent()).test(new ItemStack(adAstraItem("tier_2_rocket")))), "it makes the tier 2 rocket");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "space", timeoutTicks = 20)
    public static void netheriteSuitIsTheThermalSuit(GameTestHelper h) {
        if (!adAstra(h)) return;
        h.assertTrue(new ItemStack(adAstraItem("netherite_space_suit")).getDescriptionId()
                .equals("item.typeomega.thermal_space_suit"), "the netherite suit is named Thermal Space Suit");
        h.assertTrue(new ItemStack(adAstraItem("space_suit")).getDescriptionId().equals("item.ad_astra.space_suit"),
                "the tier 1 suit keeps its name");
        h.succeed();
    }

    private static boolean adAstra(GameTestHelper h) {
        if (ModList.get().isLoaded("ad_astra")) return true;
        h.succeed();
        return false;
    }

    private static Item adAstraItem(String path) {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation("ad_astra", path));
    }

    private static Optional<? extends Recipe<?>> recipe(GameTestHelper h, ResourceLocation id) {
        return h.getLevel().getServer().getRecipeManager().byKey(id);
    }

    /** A typeomega GT recipe by its builder path; GTCEu puts the recipe type in front (typeomega:mixer/...). */
    private static GTRecipe gtRecipe(GameTestHelper h, String path) {
        var recipe = h.getLevel().getServer().getRecipeManager().getRecipes().stream()
                .filter(r -> r instanceof GTRecipe && r.getId().getNamespace().equals(GTIAMod.MOD_ID) &&
                        r.getId().getPath().endsWith("/" + path))
                .findFirst();
        h.assertTrue(recipe.isPresent(), "missing recipe typeomega:<type>/" + path);
        return (GTRecipe) recipe.get();
    }

    private static SimpleTieredMachine reactor(GameTestHelper h) {
        h.setBlock(MACHINE_POS, GTMachines.CHEMICAL_REACTOR[GTValues.HV].getBlock());
        var machine = (SimpleTieredMachine) ((MetaMachineBlockEntity) h.getBlockEntity(MACHINE_POS)).getMetaMachine();
        machine.energyContainer.setEnergyStored(machine.energyContainer.getEnergyCapacity());
        return machine;
    }
}
