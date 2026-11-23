package com.typeomega.core.gametest;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import com.typeomega.core.GTIAMod;

import java.util.List;

/** End veins without ilmenite, platinum and palladium (GTIA-D-391, D-420, D-424). */
@PrefixGameTestTemplate(false)
@GameTestHolder(GTIAMod.MOD_ID)
public final class EndVeinGameTests {

    private EndVeinGameTests() {}

    private static List<Material> materials(GameTestHelper h, String vein) {
        var definition = GTRegistries.ORE_VEINS.get(new ResourceLocation("gtceu", vein));
        h.assertTrue(definition != null, "gtceu:" + vein + " is registered");
        return definition.veinGenerator().getAllMaterials();
    }

    @GameTest(template = "qa_empty_5x5", batch = "veins", timeoutTicks = 20)
    public static void bauxiteVeinHasNoIlmenite(GameTestHelper h) {
        var materials = materials(h, "bauxite_vein_end");
        h.assertTrue(!materials.contains(GTMaterials.Ilmenite), "titanium comes from the Moon, not the End");
        h.assertTrue(materials.contains(GTMaterials.Bauxite) && materials.contains(GTMaterials.Aluminium),
                "the bauxite vein keeps bauxite and aluminium: " + materials);
        h.succeed();
    }

    @GameTest(template = "qa_empty_5x5", batch = "veins", timeoutTicks = 20)
    public static void sheldoniteVeinHasNoRawPlatinum(GameTestHelper h) {
        var materials = materials(h, "sheldonite_vein");
        h.assertTrue(!materials.contains(GTMaterials.Platinum) && !materials.contains(GTMaterials.Palladium),
                "platinum and palladium come from the platinum line: " + materials);
        h.assertTrue(materials.contains(GTMaterials.Bornite) && materials.contains(GTMaterials.Cooperite),
                "the sheldonite vein keeps bornite and cooperite: " + materials);
        h.succeed();
    }

    /** Negative: the rewrite touches only those two veins; the End magnetite vein keeps its gold. */
    @GameTest(template = "qa_empty_5x5", batch = "veins", timeoutTicks = 20)
    public static void otherVeinsKeepTheirOres(GameTestHelper h) {
        var materials = materials(h, "magnetite_vein_end");
        h.assertTrue(materials.contains(GTMaterials.Magnetite) && materials.contains(GTMaterials.Gold),
                "the End magnetite vein is untouched: " + materials);
        h.succeed();
    }
}
