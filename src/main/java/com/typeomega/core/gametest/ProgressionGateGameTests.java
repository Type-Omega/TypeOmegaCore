package com.typeomega.core.gametest;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.recipe.condition.CleanroomCondition;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.GTIAMod;
import com.typeomega.core.route.AE2Gates;
import com.typeomega.core.route.NanoCircuits;
import com.typeomega.core.route.ProgressionGates;

import java.util.ArrayList;
import java.util.List;

/**
 * The restructuring gates: Eye of Ender in HV, portal only from a quest, basic AE2 in LV and the rest in MV/HV, the
 * Terminal with redstone, the Industrial Platform after the Bronze Hull, the eye without blaze, the nano line in HV
 * and He-3 only from the Moon, the LV silicon plate (GTIA-D-347, D-349, D-352, D-406, D-408, D-409, D-419 to D-421,
 * D-424, D-426).
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(GTIAMod.MOD_ID)
public final class ProgressionGateGameTests {

    private ProgressionGateGameTests() {}

    @GameTest(template = "qa_empty_5x5", batch = "gates", timeoutTicks = 20)
    public static void gatedRecipesAreReplaced(GameTestHelper h) {
        var manager = h.getLevel().getServer().getRecipeManager();
        List<ResourceLocation> removed = new ArrayList<>(ProgressionGates.ids());
        removed.addAll(NanoCircuits.ids());
        List<String> kept = new ArrayList<>(
                List.of("typeomega:assembler/ender_eye", "typeomega:assembler/nexus_flux_matrix",
                        "typeomega:shaped/basic_terminal"));
        NanoCircuits.IDS.forEach(path -> kept.add(GTIAMod.MOD_ID + ":" + path));
        if (ModList.get().isLoaded("industrial_platform")) kept.add("typeomega:shaped/platform_builder");
        if (ModList.get().isLoaded("ae2")) {
            removed.addAll(AE2Gates.ids());
            kept.addAll(List.of("typeomega:laser_engraver/ae2/printed_logic",
                    "typeomega:circuit_assembler/ae2/logic_processor",
                    "typeomega:assembler/ae2/controller", "typeomega:assembler/ae2/drive",
                    "typeomega:assembler/ae2/molecular_assembler",
                    "typeomega:assembler/ae2/wireless_access_point", "typeomega:assembler/ae2/cell_component_16k",
                    "typeomega:mixer/ae2/fluix_crystal"));
            // A block-to-part conversion stays: it is not a way around the gate.
            kept.add("ae2:network/blocks/pattern_providers_interface_alt");
        }
        var present = removed.stream().filter(id -> manager.byKey(id).isPresent()).toList();
        var missing = kept.stream().map(ResourceLocation::new).filter(id -> manager.byKey(id).isEmpty()).toList();
        h.assertTrue(present.isEmpty(), "gated recipes still loaded: " + present);
        h.assertTrue(missing.isEmpty(), "replacement recipes missing: " + missing);
        h.succeed();
    }

    @GameTest(template = "qa_empty_5x5", batch = "gates", timeoutTicks = 20)
    public static void terminalTakesRedstone(GameTestHelper h) {
        var manager = h.getLevel().getServer().getRecipeManager();
        var terminal = manager.byKey(GTIAMod.id("shaped/basic_terminal")).orElse(null);
        h.assertTrue(terminal != null, "the Terminal recipe is missing");
        var ingredients = terminal.getIngredients();
        h.assertTrue(ingredients.stream().anyMatch(i -> i.test(new ItemStack(Items.REDSTONE))),
                "the Terminal takes redstone (GTIA-D-409)");
        h.assertTrue(ingredients.stream().noneMatch(i -> i.test(
                ChemicalHelper.get(TagPrefix.wireGtSingle, GTMaterials.RedAlloy))),
                "the Terminal has no red alloy wire");
        var macerated = (GTRecipe) manager.byKey(new ResourceLocation("gtceu", "macerator/macerate_terminal"))
                .orElse(null);
        if (macerated != null) {
            h.assertTrue(RecipeHelper.getOutputItems(macerated).stream()
                    .noneMatch(stack -> stack.getItem().toString().contains("red_alloy")),
                    "recycling the Terminal gives no red alloy");
        }
        h.succeed();
    }

    /** The basic AE2 network runs on LV power (GTIA-D-408, D-415), with no Nether quartz in the cores or the fluix. */
    @GameTest(template = "qa_empty_5x5", batch = "gates", timeoutTicks = 20)
    public static void basicAe2IsLv(GameTestHelper h) {
        if (!ModList.get().isLoaded("ae2")) {
            h.succeed();
            return;
        }
        var manager = h.getLevel().getServer().getRecipeManager();
        for (String path : List.of("laser_engraver/ae2/printed_logic", "laser_engraver/ae2/printed_engineering",
                "laser_engraver/ae2/printed_calculation", "laser_engraver/ae2/printed_silicon",
                "circuit_assembler/ae2/logic_processor", "mixer/ae2/fluix_crystal", "assembler/ae2/annihilation_core",
                "assembler/ae2/formation_core", "assembler/ae2/controller", "assembler/ae2/drive",
                "assembler/ae2/terminal", "assembler/ae2/item_cell_housing", "assembler/ae2/cell_component_1k")) {
            var recipe = (GTRecipe) manager.byKey(GTIAMod.id(path)).orElse(null);
            h.assertTrue(recipe != null, "missing typeomega:" + path);
            h.assertTrue(recipe.getInputEUt().getTotalEU() <= GTValues.VA[GTValues.LV], path + " runs on LV");
            var quartz = new ItemStack(Items.QUARTZ);
            h.assertTrue(recipe.getInputContents(ItemRecipeCapability.CAP).stream()
                    .noneMatch(c -> ItemRecipeCapability.CAP.of(c.getContent()).test(quartz)),
                    path + " takes no Nether quartz");
        }
        h.succeed();
    }

    /**
     * The silicon plate for the AE2 print has an LV route in the Alloy Smelter at 30 EU/t, worse than the EBF one,
     * which stays (GTIA-D-426).
     */
    @GameTest(template = "qa_empty_5x5", batch = "gates", timeoutTicks = 20)
    public static void siliconPlateHasWorseLvRoute(GameTestHelper h) {
        var manager = h.getLevel().getServer().getRecipeManager();
        var lv = (GTRecipe) manager.byKey(GTIAMod.id(ProgressionGates.LV_SILICON_PLATE)).orElse(null);
        var ebf = (GTRecipe) manager.byKey(new ResourceLocation(ProgressionGates.EBF_SILICON)).orElse(null);
        h.assertTrue(lv != null, "the LV silicon plate recipe is missing");
        h.assertTrue(ebf != null, "the EBF silicon route is gone");
        h.assertTrue(lv.duration > ebf.duration, "the LV plate is slower than the EBF ingot");

        var pos = new BlockPos(2, 1, 2);
        h.setBlock(pos, GTMachines.ALLOY_SMELTER[GTValues.LV].getBlock());
        var machine = (SimpleTieredMachine) ((MetaMachineBlockEntity) h.getBlockEntity(pos)).getMetaMachine();
        machine.energyContainer.setEnergyStored(machine.energyContainer.getEnergyCapacity());
        machine.importItems.setStackInSlot(0, ChemicalHelper.get(TagPrefix.dust, GTMaterials.Silicon, 2));
        machine.importItems.setStackInSlot(1, GTItems.SHAPE_MOLD_PLATE.asStack());
        machine.recipeLogic.findAndHandleRecipe();
        var running = machine.recipeLogic.getLastRecipe();
        h.assertTrue(running != null && running.getId().equals(lv.getId()),
                "the LV Alloy Smelter runs the silicon plate: " + (running == null ? null : running.getId()));
        h.assertTrue(running.getInputEUt().getTotalEU() == GTValues.VA[GTValues.LV],
                "the silicon plate runs at 30 EU/t");
        h.assertTrue(machine.importItems.getStackInSlot(0).isEmpty(), "two silicon dusts go into one plate");
        h.assertTrue(!machine.importItems.getStackInSlot(1).isEmpty(), "the plate mold is kept");
        machine.recipeLogic.onRecipeFinish();
        var out = machine.exportItems.getStackInSlot(0);
        h.assertTrue(ItemStack.isSameItem(out, ChemicalHelper.get(TagPrefix.plate, GTMaterials.Silicon)) &&
                out.getCount() == 1, "one silicon plate comes out: " + out);
        h.succeed();
    }

    /** The eye comes from the HV Assembler with no Nether input (GTIA-D-419). */
    @GameTest(template = "qa_empty_5x5", batch = "gates", timeoutTicks = 20)
    public static void enderEyeNeedsNoNether(GameTestHelper h) {
        var recipe = (GTRecipe) h.getLevel().getServer().getRecipeManager().byKey(GTIAMod.id("assembler/ender_eye"))
                .orElse(null);
        h.assertTrue(recipe != null, "the Eye of Ender recipe is missing");
        h.assertTrue(recipe.getInputEUt().getTotalEU() == GTValues.VA[GTValues.HV], "the eye is an HV craft");
        var inputs = recipe.getInputContents(ItemRecipeCapability.CAP);
        for (var nether : List.of(new ItemStack(Items.BLAZE_POWDER), new ItemStack(Items.BLAZE_ROD),
                new ItemStack(Items.GLOWSTONE_DUST), new ItemStack(Items.QUARTZ))) {
            h.assertTrue(inputs.stream().noneMatch(c -> ItemRecipeCapability.CAP.of(c.getContent()).test(nether)),
                    "the eye takes no " + nether.getItem());
        }
        h.assertTrue(inputs.stream().anyMatch(
                c -> ItemRecipeCapability.CAP.of(c.getContent()).test(new ItemStack(Items.ENDER_PEARL))),
                "the eye still takes an ender pearl");
        h.succeed();
    }

    /**
     * The nano line runs at 480 EU/t in a Cleanroom with the GTCEu energy per craft (GTIA-D-421, D-424); the GTCEu
     * originals with the same inputs are gone (checked in {@link #gatedRecipesAreReplaced}).
     */
    @GameTest(template = "qa_empty_5x5", batch = "gates", timeoutTicks = 20)
    public static void nanoCircuitsRunInHv(GameTestHelper h) {
        var manager = h.getLevel().getServer().getRecipeManager();
        for (String path : NanoCircuits.IDS) {
            var recipe = (GTRecipe) manager.byKey(GTIAMod.id(path)).orElse(null);
            h.assertTrue(recipe != null, "missing typeomega:" + path);
            h.assertTrue(recipe.getInputEUt().getTotalEU() == GTValues.VA[GTValues.HV], path + " runs at 480 EU/t");
            h.assertTrue(recipe.conditions.stream().anyMatch(c -> c instanceof CleanroomCondition),
                    path + " needs a Cleanroom");
        }
        var mainframe = (GTRecipe) manager.byKey(GTIAMod.id("circuit_assembler/nano/mainframe")).orElseThrow();
        h.assertTrue(mainframe.duration == 800 * 4, "the HV Mainframe takes four times the GTCEu 800 ticks");
        h.succeed();
    }

    /**
     * The nano wafer (1920 EU/t) has no HV copy: it fits the Large Chemical Reactor with two HV Energy Hatches, which
     * GTCEu lets run one tier up (2 x 2 A x 512 = 2048 EU/t), and not a single HV hatch (GTIA-D-421).
     */
    @GameTest(template = "qa_empty_5x5", batch = "gates", timeoutTicks = 20)
    public static void nanoWaferFitsTwoHvHatches(GameTestHelper h) {
        var wafer = (GTRecipe) h.getLevel().getServer().getRecipeManager()
                .byKey(new ResourceLocation("gtceu", "large_chemical_reactor/nano_cpu_wafer")).orElse(null);
        h.assertTrue(wafer != null, "the Large Chemical Reactor runs the nano wafer");
        long eut = wafer.getInputEUt().getTotalEU();
        h.assertTrue(eut <= GTValues.V[GTValues.EV] && eut <= 2 * 2 * GTValues.V[GTValues.HV],
                "two HV hatches power the nano wafer");
        h.assertTrue(eut > 2 * GTValues.V[GTValues.HV], "one HV hatch (2 A) does not");
        h.succeed();
    }

    /** He-3 leaves the helium centrifuge and comes from heated lunar regolith when Ad Astra is loaded (GTIA-D-420). */
    @GameTest(template = "qa_empty_5x5", batch = "gates", timeoutTicks = 20)
    public static void helium3OnlyFromRegolith(GameTestHelper h) {
        var manager = h.getLevel().getServer().getRecipeManager();
        h.assertTrue(manager.byKey(new ResourceLocation("gtceu", "centrifuge/helium_separation")).isEmpty(),
                "the helium centrifuge no longer makes He-3");
        var regolith = (GTRecipe) manager.byKey(GTIAMod.id("extractor/helium_3_from_regolith")).orElse(null);
        if (!ModList.get().isLoaded("ad_astra")) {
            h.assertTrue(regolith == null, "no regolith recipe without Ad Astra");
            h.succeed();
            return;
        }
        h.assertTrue(regolith != null, "the regolith recipe is missing");
        h.assertTrue(regolith.getInputEUt().getTotalEU() == GTValues.VA[GTValues.HV], "the regolith heats at HV");
        h.assertTrue(regolith.getOutputContents(FluidRecipeCapability.CAP).stream()
                .anyMatch(c -> FluidRecipeCapability.CAP.of(c.getContent())
                        .test(GTMaterials.Helium3.getFluid(1))),
                "the regolith gives He-3");
        h.succeed();
    }
}
