package com.typeomega.core.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

import com.typeomega.core.GTIAMod;
import com.typeomega.core.data.GTIACreativeTabs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Every core item shows up in one of the Type Omega creative tabs, and every tab with items has an icon. */
@PrefixGameTestTemplate(false)
@GameTestHolder(GTIAMod.MOD_ID)
public final class CreativeTabGameTests {

    private static final String TEMPLATE = "qa_empty_5x5";

    private CreativeTabGameTests() {}

    @GameTest(template = TEMPLATE, timeoutTicks = 100)
    public static void everyCoreItemIsInATab(GameTestHelper h) {
        var parameters = new CreativeModeTab.ItemDisplayParameters(h.getLevel().enabledFeatures(), true,
                h.getLevel().registryAccess());
        Set<Item> shown = new HashSet<>();
        List<String> empty = new ArrayList<>();
        for (int i = 0; i < GTIACreativeTabs.ALL.size(); i++) {
            CreativeModeTab tab = GTIACreativeTabs.ALL.get(i).get();
            tab.buildContents(parameters);
            if (!tab.getDisplayItems().isEmpty() && tab.getIconItem().isEmpty()) {
                empty.add(GTIACreativeTabs.Category.values()[i].id);
            }
            for (ItemStack stack : tab.getDisplayItems()) shown.add(stack.getItem());
        }
        List<String> missing = new ArrayList<>();
        for (var entry : ForgeRegistries.ITEMS.getEntries()) {
            if (GTIAMod.MOD_ID.equals(entry.getKey().location().getNamespace()) && !shown.contains(entry.getValue())) {
                missing.add(entry.getKey().location().getPath());
            }
        }
        h.assertTrue(empty.isEmpty(), "Type Omega tabs with items but no icon: " + empty);
        h.assertTrue(missing.isEmpty(), missing.size() + " core items in no Type Omega tab: " + missing);
        h.succeed();
    }
}
