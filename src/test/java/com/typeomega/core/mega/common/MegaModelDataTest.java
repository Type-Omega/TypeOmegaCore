package com.typeomega.core.mega.common;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the TOMM v1 megastructure 3D overlay loader ({@link MegaModelData}) and client config default.
 */
class MegaModelDataTest {

    private static byte[] buildSampleModel() throws IOException {
        var raw = new ByteArrayOutputStream();
        try (var out = new DataOutputStream(new GZIPOutputStream(raw))) {
            out.write("TOMM".getBytes(StandardCharsets.US_ASCII));
            out.writeInt(1);
            // global min/max
            out.writeFloat(-10f);
            out.writeFloat(-10f);
            out.writeFloat(-5f);
            out.writeFloat(10f);
            out.writeFloat(10f);
            out.writeFloat(5f);
            out.writeInt(2); // 2 parts

            // Part 1: static emissive light
            out.writeUTF("equator_lights");
            out.writeInt(1);
            out.writeUTF("orchid_window_rim");
            out.writeInt(MegaModelData.FLAG_EMISSIVE);
            out.writeUTF("");
            out.writeFloat(-2f);
            out.writeFloat(-2f);
            out.writeFloat(-1f);
            out.writeFloat(2f);
            out.writeFloat(2f);
            out.writeFloat(1f);
            out.writeInt(3); // 3 vertices
            float[] v1 = {
                    0, 0, 0, 0, 0, 1, 0, 0,
                    1, 0, 0, 0, 0, 1, 1, 0,
                    0, 1, 0, 0, 0, 1, 0, 1
            };
            for (float f : v1) out.writeFloat(f);
            out.writeInt(3); // 1 tri LOD0
            out.writeInt(0);
            out.writeInt(1);
            out.writeInt(2);
            out.writeInt(0); // no separate LOD1

            // Part 2: rotating translucent glass with separate LOD1
            out.writeUTF("disc_glass");
            out.writeInt(5);
            out.writeUTF("orchid_cell_glass");
            out.writeInt(MegaModelData.FLAG_TRANSLUCENT | MegaModelData.FLAG_DOUBLE_SIDED);
            out.writeUTF("shell_spin");
            out.writeFloat(-10f);
            out.writeFloat(-10f);
            out.writeFloat(-0.5f);
            out.writeFloat(10f);
            out.writeFloat(10f);
            out.writeFloat(0.5f);
            out.writeInt(4); // 4 vertices
            float[] v2 = {
                    -1, -1, 0, 0, 0, 1, 0, 0,
                    1, -1, 0, 0, 0, 1, 1, 0,
                    1, 1, 0, 0, 0, 1, 1, 1,
                    -1, 1, 0, 0, 0, 1, 0, 1
            };
            for (float f : v2) out.writeFloat(f);
            out.writeInt(6); // 2 tris LOD0
            out.writeInt(0);
            out.writeInt(1);
            out.writeInt(2);
            out.writeInt(0);
            out.writeInt(2);
            out.writeInt(3);
            out.writeInt(3); // 1 tri LOD1
            out.writeInt(0);
            out.writeInt(1);
            out.writeInt(2);
        }
        return raw.toByteArray();
    }

    @Test
    void readsVerticesPartsFlagsAndLods() throws IOException {
        byte[] bytes = buildSampleModel();
        MegaModelData model = MegaModelData.read("sample", new ByteArrayInputStream(bytes));
        assertNotNull(model);
        assertEquals("sample", model.name);
        assertArrayEquals(new float[] { -10f, -10f, -5f }, model.boundsMin);
        assertArrayEquals(new float[] { 10f, 10f, 5f }, model.boundsMax);
        assertEquals(2, model.parts.size());
        assertEquals(7, model.totalVertices());
        assertEquals(3, model.totalLod0Triangles());
        assertEquals(2, model.totalLod1Triangles());

        var p0 = model.parts.get(0);
        assertEquals("equator_lights", p0.name());
        assertEquals(1, p0.phase());
        assertEquals("orchid_window_rim", p0.material());
        assertTrue(p0.isEmissive());
        assertFalse(p0.isTranslucent());
        assertFalse(p0.isMoving());
        assertEquals(3, p0.vertexCount());
        assertEquals(1, p0.lod0Triangles());
        assertEquals(1, p0.lod1Triangles());

        var p1 = model.parts.get(1);
        assertEquals("disc_glass", p1.name());
        assertEquals(5, p1.phase());
        assertTrue(p1.isTranslucent());
        assertTrue(p1.isDoubleSided());
        assertTrue(p1.isMoving());
        assertEquals("shell_spin", p1.track());
        assertEquals(4, p1.vertexCount());
        assertEquals(2, p1.lod0Triangles());
        assertEquals(1, p1.lod1Triangles());
    }

    @Test
    void fallsBackToNullWhenModelFileIsMissing() throws IOException {
        assertNull(MegaModelData.read("missing", null));
        assertNull(MegaModelData.get("nonexistent_mega_overlay_test"));
    }

    @Test
    void rejectsInvalidMagic() throws IOException {
        var raw = new ByteArrayOutputStream();
        try (var out = new DataOutputStream(new GZIPOutputStream(raw))) {
            out.write("NOPE".getBytes(StandardCharsets.US_ASCII));
            out.writeInt(1);
        }
        assertThrows(IOException.class,
                () -> MegaModelData.read("bad", new ByteArrayInputStream(raw.toByteArray())));
    }

    @Test
    void loadsLocalYggdrasilModelWhenPresent() {
        // EN: typeomega_mega/ is local-only (.gitignore); skip cleanly in public CI when absent.
        // PT: typeomega_mega/ é só local (.gitignore); pula limpo no CI público quando ausente.
        // ZH: typeomega_mega/ 仅限本地（.gitignore）；在公共 CI 中缺失时安全跳过。
        MegaModelData ygg = MegaModelData.get("yggdrasil");
        if (ygg == null) return;
        assertEquals(8, ygg.parts.size());
        assertEquals(19_434, ygg.totalVertices());
        assertEquals(14_435, ygg.totalLod0Triangles());
        assertEquals(6_135, ygg.totalLod1Triangles());
        assertTrue(ygg.parts.stream().anyMatch(p -> p.isMoving() && "shell_spin".equals(p.track())));
        assertTrue(ygg.parts.stream().anyMatch(MegaModelData.Part::isEmissive));
        assertTrue(ygg.parts.stream().anyMatch(MegaModelData.Part::isTranslucent));
    }

    @Test
    void loadsLocalStellarHoopModelWhenPresent() {
        // EN: typeomega_mega/ is local-only (.gitignore); skip cleanly in public CI when absent.
        // PT: typeomega_mega/ é só local (.gitignore); pula limpo no CI público quando ausente.
        // ZH: typeomega_mega/ 仅限本地（.gitignore）；在公共 CI 中缺失时安全跳过。
        MegaModelData hoop = MegaModelData.get("stellar_hoop");
        if (hoop == null) return;
        assertEquals(6, hoop.parts.size());
        assertEquals(41_072, hoop.totalVertices());
        assertEquals(32_088, hoop.totalLod0Triangles());
        assertEquals(3_608, hoop.totalLod1Triangles());
        assertTrue(hoop.parts.stream().allMatch(MegaModelData.Part::isMoving));
        assertTrue(hoop.parts.stream().anyMatch(p -> "hoop_spin".equals(p.track())));
        assertTrue(hoop.parts.stream().anyMatch(p -> "elevators_spin".equals(p.track())));
        assertTrue(hoop.parts.stream().anyMatch(MegaModelData.Part::isEmissive));
    }
}
