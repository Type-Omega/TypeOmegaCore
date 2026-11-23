package com.typeomega.core.mega.typezero;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import com.typeomega.core.GTIAMod;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.DISABLE_DECOMPOSITION;

/** Heavy water, the industrial source of the HV Tokamak's deuterium (GTIA-D-421). */
public final class TypeZeroMaterials {

    public static Material HEAVY_WATER;

    private TypeZeroMaterials() {}

    public static void register() {
        HEAVY_WATER = new Material.Builder(GTIAMod.id("heavy_water")).liquid().color(0x3A6EC8)
                .flags(DISABLE_DECOMPOSITION).components(GTMaterials.Deuterium, 2, GTMaterials.Oxygen, 1)
                .formula("D2O").buildAndRegister();
    }
}
