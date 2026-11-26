package com.typeomega.core.mega.common;

import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;

/**
 * Compact binary 3D model overlay of a megastructure, read from {@code typeomega_mega/<name>/model.bin.gz} (TOMM v1).
 * Independent of Minecraft rendering classes so it can be unit-tested cleanly and returns {@code null} when absent
 * (falling back to the block renderer).
 */
public final class MegaModelData {

    public static final String MAGIC = "TOMM";
    public static final int VERSION = 1;
    public static final int STRIDE_FLOATS = 8;

    public static final int FLAG_EMISSIVE = 1;
    public static final int FLAG_TRANSLUCENT = 1 << 1;
    public static final int FLAG_DOUBLE_SIDED = 1 << 2;

    /** One renderable part of the model, in grid-aligned block units relative to the megastructure pivot. */
    public record Part(
                       String name,
                       int phase,
                       String material,
                       int flags,
                       @Nullable String track,
                       float[] boundsMin,
                       float[] boundsMax,
                       float[] vertices,
                       int[] lod0Indices,
                       int[] lod1Indices) {

        public int vertexCount() {
            return vertices.length / STRIDE_FLOATS;
        }

        public int lod0Triangles() {
            return lod0Indices.length / 3;
        }

        public int lod1Triangles() {
            return (lod1Indices.length > 0 ? lod1Indices.length : lod0Indices.length) / 3;
        }

        public boolean isEmissive() {
            return (flags & FLAG_EMISSIVE) != 0;
        }

        public boolean isTranslucent() {
            return (flags & FLAG_TRANSLUCENT) != 0;
        }

        public boolean isDoubleSided() {
            return (flags & FLAG_DOUBLE_SIDED) != 0;
        }

        public boolean isMoving() {
            return track != null && !track.isEmpty();
        }
    }

    public final String name;
    public final float[] boundsMin;
    public final float[] boundsMax;
    public final List<Part> parts;

    private static final Map<String, Optional<MegaModelData>> CACHE = new ConcurrentHashMap<>();

    private MegaModelData(String name, float[] boundsMin, float[] boundsMax, List<Part> parts) {
        this.name = name;
        this.boundsMin = boundsMin;
        this.boundsMax = boundsMax;
        this.parts = List.copyOf(parts);
    }

    /** Loads and caches {@code typeomega_mega/<name>/model.bin.gz}, or {@code null} when absent or unreadable. */
    @Nullable
    public static MegaModelData get(String name) {
        if (name == null || name.isEmpty()) return null;
        return CACHE.computeIfAbsent(name, n -> {
            try (var in = MegaModelData.class.getClassLoader()
                    .getResourceAsStream(MegaShape.dir(n) + "model.bin.gz")) {
                return Optional.ofNullable(read(n, in));
            } catch (IOException | RuntimeException e) {
                com.typeomega.core.TypeOmegaMod.LOGGER.error("Failed to load 3D model overlay for {}", n, e);
                return Optional.empty();
            }
        }).orElse(null);
    }

    /** Reads a TOMM v1 gzipped stream, or returns {@code null} when {@code raw} is {@code null}. */
    @Nullable
    public static MegaModelData read(String name, @Nullable InputStream raw) throws IOException {
        if (raw == null) return null;
        try (var in = new DataInputStream(new GZIPInputStream(raw))) {
            byte[] magic = new byte[4];
            in.readFully(magic);
            if (!MAGIC.equals(new String(magic, StandardCharsets.US_ASCII))) {
                throw new IOException("Not a TypeOmega mega model (invalid magic)");
            }
            int version = in.readInt();
            if (version != VERSION) {
                throw new IOException("Unsupported mega model version: " + version);
            }
            float[] globalMin = readVec3(in);
            float[] globalMax = readVec3(in);
            int partCount = in.readInt();
            if (partCount < 0 || partCount > 1024) {
                throw new IOException("Invalid part count: " + partCount);
            }
            List<Part> parts = new ArrayList<>(partCount);
            for (int i = 0; i < partCount; i++) {
                parts.add(readPart(in));
            }
            return new MegaModelData(name, globalMin, globalMax, parts);
        }
    }

    private static Part readPart(DataInputStream in) throws IOException {
        String partName = in.readUTF();
        int phase = in.readInt();
        String material = in.readUTF();
        int flags = in.readInt();
        String trackRaw = in.readUTF();
        String track = trackRaw.isEmpty() ? null : trackRaw;
        float[] min = readVec3(in);
        float[] max = readVec3(in);

        int vertexCount = in.readInt();
        if (vertexCount < 0 || vertexCount > 2_000_000) {
            throw new IOException("Invalid vertex count in part " + partName + ": " + vertexCount);
        }
        float[] vertices = new float[vertexCount * STRIDE_FLOATS];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = in.readFloat();
        }

        int lod0Count = in.readInt();
        int[] lod0 = readIndices(in, lod0Count, vertexCount, partName);
        int lod1Count = in.readInt();
        int[] lod1 = lod1Count == 0 ? new int[0] : readIndices(in, lod1Count, vertexCount, partName);
        return new Part(partName, phase, material, flags, track, min, max, vertices, lod0, lod1);
    }

    private static int[] readIndices(DataInputStream in, int count, int vertexCount,
                                     String partName) throws IOException {
        if (count < 0 || count % 3 != 0 || count > 6_000_000) {
            throw new IOException("Invalid index count in part " + partName + ": " + count);
        }
        int[] indices = new int[count];
        for (int i = 0; i < count; i++) {
            int idx = in.readInt();
            if (idx < 0 || idx >= vertexCount) {
                throw new IOException("Out-of-bounds vertex index in part " + partName + ": " + idx);
            }
            indices[i] = idx;
        }
        return indices;
    }

    private static float[] readVec3(DataInputStream in) throws IOException {
        return new float[] { in.readFloat(), in.readFloat(), in.readFloat() };
    }

    public int totalVertices() {
        int sum = 0;
        for (var p : parts) sum += p.vertexCount();
        return sum;
    }

    public int totalLod0Triangles() {
        int sum = 0;
        for (var p : parts) sum += p.lod0Triangles();
        return sum;
    }

    public int totalLod1Triangles() {
        int sum = 0;
        for (var p : parts) sum += p.lod1Triangles();
        return sum;
    }
}
