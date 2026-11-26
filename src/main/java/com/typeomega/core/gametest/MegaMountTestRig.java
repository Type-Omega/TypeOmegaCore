package com.typeomega.core.gametest;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.common.data.GTMachines;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

import com.typeomega.core.mega.common.MegaMachine;

/**
 * Formation tests of the controller mount (D-445): phase 1 alone forms on it, and without a mount block it does not.
 */
final class MegaMountTestRig {

    private MegaMountTestRig() {}

    /** The structure with phase 1 and two hatches on the controller's 3x3 forms, the controller on the mount. */
    static void forms(GameTestHelper helper, String name, MultiblockMachineDefinition definition, int slot) {
        MegaMachine m = MegaTestRig.build(helper, name, definition, 1, slot);
        MegaTestRig.hatch(helper, m, 0, GTMachines.ITEM_IMPORT_BUS[GTValues.LuV]);
        MegaTestRig.hatch(helper, m, 1, GTMachines.ITEM_EXPORT_BUS[GTValues.LuV]);
        helper.assertTrue(m.checkPatternWithLock(), name + " forms on its mount: " + MegaTestRig.error(m));
        helper.assertTrue(m.shape().letter(m.shape().ck, m.shape().cj, m.shape().ci) == '~',
                "the controller is the mount's centre cell");
        helper.succeed();
    }

    /** One deck block, one wall block, or one casing around the controller missing: it does not form. */
    static void needsTheMount(GameTestHelper helper, String name, MultiblockMachineDefinition definition, int slot) {
        MegaMachine m = MegaTestRig.build(helper, name, definition, 1, slot);
        helper.assertTrue(m.checkPatternWithLock(), name + " forms before the cut: " + MegaTestRig.error(m));
        var shape = m.shape();
        for (String ring : new String[] { "mount_deck", "mount_wall" }) {
            int[] c = MegaTestRig.firstCell(shape, MegaTestRig.part(shape, ring, "body"));
            var pos = shape.world(m.getPos(), MegaTestRig.FRONT, c[0], c[1], c[2]);
            var old = helper.getLevel().getBlockState(pos);
            helper.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 2 | 16);
            helper.assertTrue(!m.checkPatternWithLock(),
                    "a missing " + ring + " block keeps " + name + " from forming");
            helper.getLevel().setBlock(pos, old, 2 | 16);
            helper.assertTrue(m.checkPatternWithLock(), name + " forms again once the " + ring + " block is back");
        }
        helper.succeed();
    }
}
