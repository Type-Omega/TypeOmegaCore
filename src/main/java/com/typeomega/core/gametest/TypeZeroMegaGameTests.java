package com.typeomega.core.gametest;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.common.data.GTBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.GTIAMod;
import com.typeomega.core.data.rocket.GTIARocketAssembler;
import com.typeomega.core.kardashev.KardashevData;
import com.typeomega.core.mega.typezero.Cosmodrome;
import com.typeomega.core.mega.typezero.CosmodromeMachine;
import com.typeomega.core.mega.typezero.LunarAccess;
import com.typeomega.core.mega.typezero.NationalDispatchCenter;
import com.typeomega.core.mega.typezero.TypeZeroBlocks;
import com.typeomega.core.mega.typezero.TypeZeroItems;

import java.util.UUID;

/**
 * The Type 0 megastructures (GTIA-D-390): each forms from its preview and fails without a part, counts once on its
 * team's Kardashev meter, and does its job (transponder gate, Cosmodrome link and Moon gate).
 */
@PrefixGameTestTemplate(false)
@GameTestHolder(GTIAMod.MOD_ID)
public final class TypeZeroMegaGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";

    private TypeZeroMegaGameTests() {}

    private static KardashevData kardashev(GameTestHelper h) {
        return KardashevData.get(h.getLevel().getServer());
    }

    // ---------------------------------------------------------------------------------------- mega 1: Dispatch

    @GameTest(template = TEMPLATE, batch = "type_zero_megas", timeoutTicks = 60)
    public static void dispatchCenterFormsAndRaisesTheCap(GameTestHelper h) {
        UUID team = UUID.randomUUID();
        var placed = TypeZeroTestRig.place(h, NationalDispatchCenter.DEFINITION, TypeZeroTestRig.slot(h, 0), team,
                null);
        h.assertTrue(TypeZeroTestRig.form(placed.controller()), "the Dispatch Center preview must form");
        h.assertTrue(kardashev(h).built(team, NationalDispatchCenter.ID), "the team counts the Dispatch Center");
        h.assertTrue(kardashev(h).megastructures(team) == 1, "one megastructure: cap 0.89");
        placed.controller().onStructureFormed();
        h.assertTrue(kardashev(h).megastructures(team) == 1, "forming again does not count twice");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "type_zero_megas", timeoutTicks = 60)
    public static void dispatchCenterNeedsItsTransformers(GameTestHelper h) {
        UUID team = UUID.randomUUID();
        var placed = TypeZeroTestRig.place(h, NationalDispatchCenter.DEFINITION, TypeZeroTestRig.slot(h, 1), team,
                null);
        // EN: Remove one transformer tank of the substation yard; the hall alone is not a dispatch center.
        // PT: Tira um tanque de transformador do pátio; a sala sozinha não é um centro de despacho.
        // ZH: 移除变电站场区的一个变压器油箱；仅有控制大厅不构成调度中心。
        BlockPos tank = placed.find(h, GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get()).get(0);
        h.getLevel().setBlock(tank, Blocks.AIR.defaultBlockState(), 2 | 16);
        h.assertFalse(TypeZeroTestRig.form(placed.controller()), "a yard missing a transformer must not form");
        h.assertTrue(kardashev(h).megastructures(team) == 0, "an unformed Dispatch Center counts nothing");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "type_zero_megas", timeoutTicks = 20)
    public static void wirelessHatchesNeedTheTransponder(GameTestHelper h) {
        var manager = h.getLevel().getServer().getRecipeManager();
        h.assertTrue(manager.byKey(GTIAMod.id("grid_dispatch/grid_transponder")).isPresent(),
                "the Dispatch Center programs the transponder");
        if (!ModList.get().isLoaded("gtna")) {
            h.succeed();
            return;
        }
        for (String io : new String[] { "in", "out" }) {
            String path = "wireless_energy_" + io + "_1a_hv";
            h.assertTrue(manager.byKey(new ResourceLocation("gtceu", "assembler/" + path)).isEmpty(),
                    "GTNA's ungated " + path + " is removed");
            var gated = (GTRecipe) manager.byKey(GTIAMod.id("assembler/wireless/" + path)).orElse(null);
            h.assertTrue(gated != null, "the gated " + path + " exists");
            boolean transponder = RecipeHelper.getInputItems(gated).stream()
                    .anyMatch(stack -> stack.is(TypeZeroItems.GRID_TRANSPONDER.get()));
            h.assertTrue(transponder, path + " takes a Grid Transponder");
            String merged = "assembler/wireless_energy_" + io + "_64a_lv";
            h.assertTrue(manager.byKey(new ResourceLocation("gtceu", merged)).isPresent(),
                    "GTNA's 4-to-1 " + merged + " stays, gated through its inputs");
        }
        h.succeed();
    }

    // -------------------------------------------------------------------------------------- mega 2: Cosmodrome

    @GameTest(template = TEMPLATE, batch = "type_zero_megas", timeoutTicks = 100)
    public static void cosmodromeCountsOnlyLinkedToARocketAssembler(GameTestHelper h) {
        UUID team = UUID.randomUUID();
        BlockPos origin = TypeZeroTestRig.slot(h, 2);
        var cosmodrome = TypeZeroTestRig.place(h, Cosmodrome.DEFINITION, origin, team, null);
        var mission = (CosmodromeMachine) cosmodrome.controller();
        h.assertTrue(TypeZeroTestRig.form(mission), "the Cosmodrome preview must form");
        h.assertTrue(mission.linkedAssembler() == null, "no Rocket Assembler nearby: no link");
        h.assertFalse(kardashev(h).built(team, Cosmodrome.ID), "an unlinked Cosmodrome does not count");
        var survey = (GTRecipe) h.getLevel().getServer().getRecipeManager()
                .byKey(GTIAMod.id("mission_control/lunar_survey")).orElse(null);
        h.assertTrue(survey != null, "the lunar survey mission exists");
        h.assertTrue(CosmodromeMachine.requireLink(mission, survey).apply(survey) == null,
                "no mission runs without the link");

        var assembler = TypeZeroTestRig.place(h, GTIARocketAssembler.ROCKET_ASSEMBLER, origin.offset(16, 0, 0),
                team, null);
        h.assertTrue(TypeZeroTestRig.form(assembler.controller()), "the Rocket Assembler preview must form");
        mission.relink();
        h.assertTrue(mission.linkedAssembler() != null, "a formed Rocket Assembler 16 blocks away links");
        h.assertTrue(kardashev(h).built(team, Cosmodrome.ID), "the linked Cosmodrome counts");
        h.assertTrue(CosmodromeMachine.requireLink(mission, survey).apply(survey) != null,
                "missions run once linked");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "type_zero_megas", timeoutTicks = 60)
    public static void cosmodromeNeedsItsConsoles(GameTestHelper h) {
        var placed = TypeZeroTestRig.place(h, Cosmodrome.DEFINITION, TypeZeroTestRig.slot(h, 4), UUID.randomUUID(),
                null);
        BlockPos console = placed.find(h, TypeZeroBlocks.MISSION_CONTROL_CASING.get()).get(0);
        h.getLevel().setBlock(console, GTBlocks.CASING_STAINLESS_CLEAN.get().defaultBlockState(), 2 | 16);
        h.assertFalse(TypeZeroTestRig.form(placed.controller()), "a control hall missing a console must not form");
        h.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "type_zero_megas", timeoutTicks = 20)
    public static void theMoonNeedsTheSurvey(GameTestHelper h) {
        var player = TestPlayers.survival(h);
        ResourceKey<Level> moon = ResourceKey.create(Registries.DIMENSION, LunarAccess.MOON);
        h.assertFalse(LunarAccess.mayLand(player, moon), "no survey: no Moon");
        h.assertTrue(LunarAccess.mayLand(player, Level.OVERWORLD), "the Earth is always open");
        player.getInventory().add(new ItemStack(TypeZeroItems.LUNAR_SURVEY_DATA.get()));
        h.assertTrue(LunarAccess.mayLand(player, moon), "carrying the Lunar Survey Data opens the Moon");
        h.succeed();
    }
}
