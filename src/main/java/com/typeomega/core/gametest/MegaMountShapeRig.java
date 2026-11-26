package com.typeomega.core.gametest;

import net.minecraft.gametest.framework.GameTestHelper;

import com.typeomega.core.mega.TypeOmegaMegastructures;
import com.typeomega.core.mega.common.MegaShape;
import com.typeomega.core.mega.common.client.MegaMotion;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Shape checks of the controller mount (D-445), from the data files alone: the controller cell and its 3x3 on a static
 * wall, no station, and the sweep of every moving track against the mount's box.
 */
final class MegaMountShapeRig {

    /** Blocks of clear space wanted between the mount's box and anything a moving piece reaches. */
    static final int MARGIN = 4;

    /** The megastructures whose controller moved into the structure (the pilot of D-445). */
    static final String[] PILOT = { TypeOmegaMegastructures.YGGDRASIL, TypeOmegaMegastructures.NIDAVELLIR,
            TypeOmegaMegastructures.STAR_LIFTER };

    private MegaMountShapeRig() {}

    static MegaShape shape(GameTestHelper helper, String name) {
        var shape = MegaShape.get(name);
        helper.assertTrue(shape != null, name + " has its shape files");
        helper.assertTrue(shape.hasMount(), name + " has a controller mount");
        return shape;
    }

    static void controllerOnStaticMount(GameTestHelper helper, String name) {
        var shape = shape(helper, name);
        helper.assertTrue(shape.letter(shape.ck, shape.cj, shape.ci) == '~', name + ": the controller cell is ~");
        helper.assertTrue(shape.part(MegaShape.Part.STATION) == null, name + " has no station letter");
        for (var part : shape.parts.values()) {
            helper.assertTrue(!part.ring().equals("station") && !part.ring().equals("bridge"),
                    name + " keeps no " + part.ring());
            if (part.mount() || part.letter() == MegaShape.Part.HATCH)
                helper.assertTrue(part.phase() == 1 && !part.moving(),
                        name + ": " + part.ring() + " is phase 1 and static");
        }
        // The 3x3 of hatch casings around the controller, all of the hatch letter.
        int hatches = 0;
        for (int dj = -1; dj <= 1; dj++)
            for (int dk = -1; dk <= 1; dk++)
                if ((dj != 0 || dk != 0) && shape.letter(shape.ck + dk, shape.cj + dj, shape.ci) ==
                        MegaShape.Part.HATCH)
                    hatches++;
        helper.assertTrue(hatches == 8, name + ": the controller has its 8 hatch casings (" + hatches + ")");
        // Nothing near the controller is a moving piece: the mount hangs from a static one.
        for (int di = -3; di <= 3; di++)
            for (int dj = -3; dj <= 3; dj++)
                for (int dk = -3; dk <= 3; dk++) {
                    int k = shape.ck + dk, j = shape.cj + dj, i = shape.ci + di;
                    if (k < 0 || j < 0 || i < 0 || k >= shape.nx || j >= shape.ny || i >= shape.nz) continue;
                    var part = shape.part(shape.letter(k, j, i));
                    helper.assertTrue(part == null || !part.moving(),
                            name + ": a moving piece (" + (part == null ? "" : part.ring()) +
                                    ") within 3 blocks of the controller");
                }
        // Phase 1 is required, so the structure forms with the mount and nothing of the later phases.
        int[] lo = { Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE }, hi = { -1, -1, -1 };
        mountBox(shape, lo, hi);
        helper.assertTrue(hi[2] - lo[2] >= 7 && hi[0] - lo[0] >= 6, name + ": the mount is at least 7 wide and 5 deep");
    }

    /** Bounding box (k, j, i) of the cells of the mount's pieces and the controller. */
    static void mountBox(MegaShape shape, int[] lo, int[] hi) {
        for (int i = 0; i < shape.nz; i++)
            for (int j = 0; j < shape.ny; j++)
                for (int k = 0; k < shape.nx; k++) {
                    char c = shape.letter(k, j, i);
                    var part = shape.part(c);
                    if (c != '~' && (part == null || !part.mount() && c != MegaShape.Part.HATCH)) continue;
                    int[] at = { k, j, i };
                    for (int a = 0; a < 3; a++) {
                        lo[a] = Math.min(lo[a], at[a]);
                        hi[a] = Math.max(hi[a], at[a]);
                    }
                }
    }

    /** Turns every cell of every moving track by the track's samples; none may come within {@link #MARGIN}. */
    static void sweepIsClear(GameTestHelper helper, String name) {
        var shape = shape(helper, name);
        var motion = MegaMotion.get(name);
        helper.assertTrue(motion != null, name + " has its tracks");
        int[] lo = { Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE }, hi = { -1, -1, -1 };
        mountBox(shape, lo, hi);
        float[] pivot = { shape.pivotX, shape.pivotY, shape.pivotZ };
        double gap = Double.MAX_VALUE;
        var q = new Quaternionf();
        var v = new Vector3f();
        for (var part : shape.parts.values()) {
            if (!part.moving()) continue;
            var track = motion.tracks.get(part.track());
            helper.assertTrue(track != null, name + ": track " + part.track() + " exists");
            int step = Math.max(1, track.samples().length / 60);
            for (int s = 0; s < track.samples().length; s += step) {
                var sample = track.samples()[s];
                q.set(sample[0], sample[1], sample[2], sample[3]);
                for (int i = 0; i < shape.nz; i++)
                    for (int j = 0; j < shape.ny; j++)
                        for (int k = 0; k < shape.nx; k++) {
                            if (shape.letter(k, j, i) != part.letter()) continue;
                            v.set(k - pivot[0], j - pivot[1], i - pivot[2]);
                            q.transform(v);
                            double x = v.x + sample[4] + pivot[0], y = v.y + sample[5] + pivot[1],
                                    z = v.z + sample[6] + pivot[2];
                            double d = Math.max(Math.max(lo[0] - x, x - hi[0]),
                                    Math.max(Math.max(lo[1] - y, y - hi[1]), Math.max(lo[2] - z, z - hi[2])));
                            gap = Math.min(gap, d);
                        }
            }
        }
        helper.assertTrue(gap >= MARGIN, name + ": a moving piece passes " + gap + " blocks from the mount (at least " +
                MARGIN + ")");
    }
}
