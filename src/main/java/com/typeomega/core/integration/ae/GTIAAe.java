package com.typeomega.core.integration.ae;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import com.typeomega.core.integration.ae.cards.AccelerationCards;
import com.typeomega.core.integration.ae.interfaces.InterfaceRecipes;
import com.typeomega.core.integration.ae.interfaces.InterfaceTiers;

import java.util.function.Consumer;

/**
 * Entry point of the AE2 pieces written for the pack (GTIA-D-442): ME Interface (II) to (V) and Acceleration Card
 * (II) to (V). They need AE2 and ExtendedAE, so nothing here loads without both.
 */
public final class GTIAAe {

    private GTIAAe() {}

    public static boolean available() {
        ModList mods = ModList.get();
        return mods.isLoaded("ae2") && mods.isLoaded("expatternprovider");
    }

    public static void init() {
        if (!available()) return;
        InterfaceTiers.init();
        AccelerationCards.init();
        if (FMLEnvironment.dist.isClient()) AeClient.init(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static void addRecipes(Consumer<FinishedRecipe> provider) {
        if (!available()) return;
        InterfaceRecipes.addRecipes(provider);
        AccelerationCards.addRecipes(provider);
    }
}
