package com.typeomega.core.chem;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import com.typeomega.core.TypeOmegaMod;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.DISABLE_DECOMPOSITION;

/**
 * EN: The borane line of the tier 2 rocket fuel (D-421, D-424): boric acid, boron trifluoride, the
 * Brown-Schlesinger sodium borohydride, diborane, the fictional deshaborane and Zip-D; plus the silica aerogel of the
 * thermal space suit.
 * PT: A linha dos boranos do combustível do foguete T2 (D-421, D-424): ácido bórico, trifluoreto de boro, o
 * borohidreto de sódio de Brown-Schlesinger, diborano, o deshaborano ficcional e o Zip-D; e o aerogel de sílica do
 * traje térmico.
 * ZH: 二级火箭燃料的硼烷线（D-421、D-424）：硼酸、三氟化硼、Brown-Schlesinger 法硼氢化钠、乙硼烷、虚构的
 * 戴什硼烷和 Zip-D；以及隔热宇航服的二氧化硅气凝胶。
 */
public final class ZipDMaterials {

    public static Material BORIC_ACID;
    public static Material BORON_TRIFLUORIDE;
    public static Material BORON_TRICHLORIDE;
    public static Material TRIMETHYL_BORATE;
    public static Material SODIUM_HYDRIDE;
    public static Material SODIUM_BOROHYDRIDE;
    public static Material SODIUM_METHOXIDE;
    public static Material SODIUM_TETRAFLUOROBORATE;
    public static Material DIBORANE;
    public static Material DESHABORANE;
    public static Material ZIP_D_ROCKET_FUEL;
    public static Material SILICA_ALCOGEL;
    public static Material SILICA_AEROGEL;

    private ZipDMaterials() {}

    public static void register() {
        BORIC_ACID = dust("boric_acid", 0xF2F2EA, GTMaterials.Hydrogen, 3, GTMaterials.Boron, 1, GTMaterials.Oxygen,
                3);
        BORON_TRIFLUORIDE = new Material.Builder(TypeOmegaMod.id("boron_trifluoride")).gas().color(0xDDE8F0)
                .flags(DISABLE_DECOMPOSITION).components(GTMaterials.Boron, 1, GTMaterials.Fluorine, 3)
                .buildAndRegister();
        BORON_TRICHLORIDE = new Material.Builder(TypeOmegaMod.id("boron_trichloride")).gas().color(0xC9D3BE)
                .flags(DISABLE_DECOMPOSITION).components(GTMaterials.Boron, 1, GTMaterials.Chlorine, 3)
                .buildAndRegister();
        TRIMETHYL_BORATE = new Material.Builder(TypeOmegaMod.id("trimethyl_borate")).liquid().color(0xD6E4CC)
                .flags(DISABLE_DECOMPOSITION).components(GTMaterials.Boron, 1, GTMaterials.Carbon, 3,
                        GTMaterials.Hydrogen, 9, GTMaterials.Oxygen, 3)
                .buildAndRegister();
        SODIUM_HYDRIDE = dust("sodium_hydride", 0xB4B4AA, GTMaterials.Sodium, 1, GTMaterials.Hydrogen, 1);
        SODIUM_BOROHYDRIDE = dust("sodium_borohydride", 0xF4F4F0, GTMaterials.Sodium, 1, GTMaterials.Boron, 1,
                GTMaterials.Hydrogen, 4);
        SODIUM_METHOXIDE = dust("sodium_methoxide", 0xE6E0D2, GTMaterials.Sodium, 1, GTMaterials.Carbon, 1,
                GTMaterials.Hydrogen, 3, GTMaterials.Oxygen, 1);
        SODIUM_TETRAFLUOROBORATE = dust("sodium_tetrafluoroborate", 0xD8E2E8, GTMaterials.Sodium, 1,
                GTMaterials.Boron, 1, GTMaterials.Fluorine, 4);
        DIBORANE = new Material.Builder(TypeOmegaMod.id("diborane")).gas().color(0xBFE0C6)
                .flags(DISABLE_DECOMPOSITION).components(GTMaterials.Boron, 2, GTMaterials.Hydrogen, 6)
                .buildAndRegister();
        // EN: Pentaborane (B5H9) whose boron cage the lunar desh closes: the fictional step of Zip-D.
        // PT: Pentaborano (B5H9) cuja gaiola de boro o desh lunar fecha: o passo ficcional do Zip-D.
        // ZH: 由月球戴什封闭硼笼的戊硼烷（B5H9）：Zip-D 的虚构步骤。
        DESHABORANE = new Material.Builder(TypeOmegaMod.id("deshaborane")).liquid().color(0x93CC6E)
                .flags(DISABLE_DECOMPOSITION).components(GTMaterials.Boron, 5, GTMaterials.Hydrogen, 9)
                .formula("B5H9(Ds)").buildAndRegister();
        ZIP_D_ROCKET_FUEL = new Material.Builder(TypeOmegaMod.id("zip_d_rocket_fuel")).liquid().color(0x5FE05A)
                .flags(DISABLE_DECOMPOSITION).langValue("Zip-D Rocket Fuel").buildAndRegister();
        SILICA_ALCOGEL = new Material.Builder(TypeOmegaMod.id("silica_alcogel")).dust().color(0xC4D4DC)
                .iconSet(MaterialIconSet.ROUGH).flags(DISABLE_DECOMPOSITION)
                .components(GTMaterials.SiliconDioxide, 1).buildAndRegister();
        SILICA_AEROGEL = new Material.Builder(TypeOmegaMod.id("silica_aerogel")).dust().color(0xA9CBEA)
                .iconSet(MaterialIconSet.FINE).flags(DISABLE_DECOMPOSITION)
                .components(GTMaterials.SiliconDioxide, 1).buildAndRegister();
    }

    private static Material dust(String name, int color, Object... components) {
        return new Material.Builder(TypeOmegaMod.id(name)).dust().color(color).flags(DISABLE_DECOMPOSITION)
                .components(components).buildAndRegister();
    }
}
