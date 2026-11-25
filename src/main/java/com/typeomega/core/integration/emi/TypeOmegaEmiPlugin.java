package com.typeomega.core.integration.emi;

import net.minecraft.world.inventory.Slot;

import com.typeomega.core.station.CraftingStationMenu;
import com.typeomega.core.station.TypeOmegaStation;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.stack.EmiStack;

import java.util.ArrayList;
import java.util.List;

/** EMI: the Crafting Station fills recipes from the player and from the containers next to it. */
@EmiEntrypoint
public class TypeOmegaEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(TypeOmegaStation.BLOCK.get()));
        registry.addRecipeHandler(TypeOmegaStation.MENU.get(), new StationHandler());
        for (var entry : com.typeomega.core.mega.common.MegaDefinition.WORKSTATIONS.entrySet()) {
            var stack = EmiStack.of(entry.getKey().asStack());
            for (var type : entry.getValue().get()) {
                for (var category : type.getCategories()) {
                    registry.addWorkstation(
                            com.gregtechceu.gtceu.integration.emi.recipe.GTRecipeEMICategory.machineCategory(category),
                            stack);
                }
            }
        }
    }

    /** The station's recipe handler; also used by the client self-test to press "+" the same way EMI does. */
    public static class StationHandler implements StandardRecipeHandler<CraftingStationMenu> {

        @Override
        public List<Slot> getInputSources(CraftingStationMenu menu) {
            List<Slot> sources = new ArrayList<>(menu.slots.subList(10, 46));
            sources.addAll(menu.slots.subList(menu.firstSideSlot(), menu.slots.size()));
            return sources;
        }

        @Override
        public List<Slot> getCraftingSlots(CraftingStationMenu menu) {
            return menu.slots.subList(1, 10);
        }

        @Override
        public Slot getOutputSlot(CraftingStationMenu menu) {
            return menu.slots.get(0);
        }

        @Override
        public boolean supportsRecipe(EmiRecipe recipe) {
            return recipe.getCategory() == VanillaEmiRecipeCategories.CRAFTING && recipe.supportsRecipeTree();
        }
    }
}
