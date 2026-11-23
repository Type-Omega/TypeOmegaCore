package com.typeomega.core.gametest;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.common.data.GTBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.GTIAMod;
import com.typeomega.core.kardashev.KardashevData;
import com.typeomega.core.mega.typezero.NationalDispatchCenter;
import com.typeomega.core.mega.typezero.TypeZeroItems;

import java.util.UUID;

/**
 * The Type 0 megastructures (GTIA-D-390): each forms from its preview and fails without a part, counts once on its
 * team's Kardashev meter, and does its job (transponder gate).
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
}
