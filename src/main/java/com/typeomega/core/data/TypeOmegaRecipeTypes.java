package com.typeomega.core.data;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;

import net.minecraft.core.registries.BuiltInRegistries;

import com.typeomega.core.TypeOmegaMod;

import static com.lowdragmc.lowdraglib.gui.texture.ProgressTexture.FillDirection.LEFT_TO_RIGHT;

public final class TypeOmegaRecipeTypes {

    /**
     * Industrial Apiary (D-259): a caged Productive Bees bee is a catalyst item, combs come out. The pack's
     * KubeJS adds the species; no bee entity ever exists in the world.
     */
    public static final GTRecipeType INDUSTRIAL_APIARY = register("industrial_apiary")
            .setMaxIOSize(3, 6, 1, 0)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.CHEMICAL);

    /** Seed Analyzer: reveals an Agronomica seed's stats; the recipe is built per seed (custom logic). */
    public static final GTRecipeType SEED_ANALYZER = register("seed_analyzer")
            .setMaxIOSize(1, 1, 0, 0)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.SCIENCE);

    /** Mutation Chamber (D-271): two caged parents (kept) turn a caged vanilla bee into a new species. */
    public static final GTRecipeType MUTATION_CHAMBER = register("mutation_chamber")
            .setMaxIOSize(5, 1, 0, 0)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.SCIENCE);

    /** Entangled Miner (D-310): the recipe is built per operation from the Vein Card (custom logic). */
    public static final GTRecipeType ENTANGLED_MINER = register("entangled_miner")
            .setMaxIOSize(1, 4, 0, 0)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.MINER);

    /**
     * Fragment Collectors (D-362, D-363): the recipe is built per cycle from the World Fragment (custom logic).
     */
    public static final GTRecipeType FRAGMENT_COLLECTOR = register("fragment_collector", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(2, 16, 0, 0)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.MINER);

    /** Ore Processing Plant (D-366): raw ore to final dusts in one step. */
    public static final GTRecipeType ORE_PROCESSING_PLANT = register("ore_processing_plant", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(1, 3, 1, 0)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MACERATE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.MACERATOR);

    /** Rocket Assembler (D-274): Ad Astra rocket parts and GT components become a rocket. */
    public static final GTRecipeType ROCKET_ASSEMBLY = register("rocket_assembly", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(9, 1, 1, 0)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.ASSEMBLER);

    /** National Dispatch Center (D-390, D-419): programs the grid transponders of the team's wireless hatches. */
    public static final GTRecipeType GRID_DISPATCH = register("grid_dispatch", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(4, 1, 0, 0)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.ELECTROLYZER);

    /** Cosmodrome (D-390, D-420): mission control launches probes and returns their survey data. */
    public static final GTRecipeType MISSION_CONTROL = register("mission_control", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(2, 1, 1, 0)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.SCIENCE);

    /** HV Tokamak (D-379, D-390): D-T and D-He3 fusion, EU straight out of the dynamo hatches. */
    public static final GTRecipeType TOKAMAK_FUSION = register("tokamak_fusion", GTRecipeTypes.GENERATOR)
            .setMaxIOSize(1, 0, 3, 3)
            .setEUIO(IO.OUT)
            .setProgressBar(GuiTextures.PROGRESS_BAR_FUSION, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.ARC);

    static {
        // EN: Illustrated screen (D-435): the torus with the fuel placed in the drawing.
        // PT: Tela ilustrada (D-435): o toroide com o combustível posicionado no desenho.
        // ZH: 图解界面（D-435）：环形装置，燃料槽位画在插图中。
        TOKAMAK_FUSION.setRecipeUI(com.typeomega.core.mega.typezero.TokamakScreen.recipeUI(TOKAMAK_FUSION));
    }

    private TypeOmegaRecipeTypes() {}

    public static void init() {}

    private static GTRecipeType register(String name) {
        return register(name, GTRecipeTypes.ELECTRIC);
    }

    private static GTRecipeType register(String name, String group) {
        GTRecipeType type = new GTRecipeType(TypeOmegaMod.id(name), group);
        GTRegistries.register(BuiltInRegistries.RECIPE_TYPE, type.registryName, type);
        GTRegistries.register(BuiltInRegistries.RECIPE_SERIALIZER, type.registryName, new GTRecipeSerializer());
        GTRegistries.RECIPE_TYPES.register(type.registryName, type);
        return type;
    }
}
