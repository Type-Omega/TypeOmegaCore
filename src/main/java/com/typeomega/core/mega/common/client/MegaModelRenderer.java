package com.typeomega.core.mega.common.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.typeomega.core.TypeOmegaMod;
import com.typeomega.core.mega.common.MegaMachine;
import com.typeomega.core.mega.common.MegaModelData;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Renders a megastructure's 3D overlay model (D-448) from static GPU {@link VertexBuffer}s built once per model,
 * using Type Omega's own D-445 casing textures, translucent glass, emissive bloom pass, frustum culling, max view
 * distance and a two-level LOD.
 */
@OnlyIn(Dist.CLIENT)
public final class MegaModelRenderer {

    public static final double MAX_DISTANCE = 512.0;
    public static final double LOD1_DISTANCE = 220.0;

    private record Material(ResourceLocation base, @Nullable ResourceLocation bloom) {}

    private static final Map<String, Material> MATERIALS = Map.of(
            "orchid_cell_frame", casing("orchid_cell_frame", false),
            "orchid_cell_glass", casing("orchid_cell_glass", false),
            "orchid_window_rim", casing("orchid_window_rim", true),
            "terrace_rim_light", casing("terrace_rim_light", true),
            "giga_lamp", casing("giga_lamp", true),
            "giga_truss", casing("giga_truss", false),
            "giga_service_deck", casing("giga_service_deck", false));
    private static final Material DEFAULT_MATERIAL = casing("orchid_cell_frame", false);

    private static final Map<String, GpuModel> MODELS = new HashMap<>();

    private static final class GpuPart {

        final MegaModelData.Part spec;
        final Material material;
        final VertexBuffer lod0;
        @Nullable
        final VertexBuffer lod1;
        final float maxRadius;

        GpuPart(MegaModelData.Part spec, Material material, VertexBuffer lod0, @Nullable VertexBuffer lod1) {
            this.spec = spec;
            this.material = material;
            this.lod0 = lod0;
            this.lod1 = lod1;
            float rx = Math.max(Math.abs(spec.boundsMin()[0]), Math.abs(spec.boundsMax()[0]));
            float ry = Math.max(Math.abs(spec.boundsMin()[1]), Math.abs(spec.boundsMax()[1]));
            float rz = Math.max(Math.abs(spec.boundsMin()[2]), Math.abs(spec.boundsMax()[2]));
            this.maxRadius = (float) Math.sqrt(rx * rx + ry * ry + rz * rz);
        }

        VertexBuffer buffer(boolean useLod1) {
            return useLod1 && lod1 != null ? lod1 : lod0;
        }

        void close() {
            lod0.close();
            if (lod1 != null) lod1.close();
        }
    }

    private static final class GpuModel {

        final List<GpuPart> parts;
        final float maxRadius;

        GpuModel(List<GpuPart> parts, float maxRadius) {
            this.parts = parts;
            this.maxRadius = maxRadius;
        }

        void close() {
            parts.forEach(GpuPart::close);
        }
    }

    private MegaModelRenderer() {}

    private static Material casing(String id, boolean hasBloom) {
        return new Material(TypeOmegaMod.id("textures/block/casings/" + id + ".png"),
                hasBloom ? TypeOmegaMod.id("textures/block/casings/" + id + "_bloom.png") : null);
    }

    /** True when a 3D overlay model file exists and loads for {@code shapeName}. */
    public static boolean canRender(String shapeName) {
        return MegaModelData.get(shapeName) != null;
    }

    /** Releases all GPU buffers (e.g. on client resource reload or shutdown). */
    public static void closeAll() {
        MODELS.values().forEach(GpuModel::close);
        MODELS.clear();
    }

    /**
     * Draws the 3D overlay model for a formed megastructure in four depth-ordered passes: opaque hulls, internal
     * {@code drawFx} effects (planet/gardens), translucent glass, and emissive casing bloom overlays.
     */
    static boolean draw(MegaMachine machine, Vec3 pivot, Matrix3f axes, MegaMotion motion, PoseStack pose, Vec3 cam,
                        Matrix4f projection, @Nullable Frustum frustum, double seconds, Runnable drawFx) {
        var data = MegaModelData.get(machine.shapeName());
        if (data == null) return false;

        double dist = cam.distanceTo(pivot);
        if (dist > MAX_DISTANCE) return true;

        var gpu = MODELS.computeIfAbsent(machine.shapeName(), k -> uploadModel(data));
        // EN: Moving parts can swing anywhere around the pivot, so global culling uses the bounding sphere radius.
        // PT: Peças móveis podem girar em torno do pivô, então o culling global usa o raio da esfera envolvente.
        // ZH: 运动部件可能绕枢轴任意旋转，因此全局视锥剔除使用包围球半径。
        if (frustum != null && !frustum.isVisible(sphereBox(pivot, gpu.maxRadius))) return true;

        int builtPhases = Math.max(1, machine.phases());
        boolean useLod1 = dist > LOD1_DISTANCE;
        double animSeconds = machine.isRunning() ? seconds : 0.0;

        var basePose = new Matrix4f(pose.last().pose());
        var staticMat = new Matrix4f(basePose)
                .translate((float) (pivot.x - cam.x), (float) (pivot.y - cam.y), (float) (pivot.z - cam.z))
                .mul(new Matrix4f().set(axes));
        Map<String, Matrix4f> trackMats = new HashMap<>();
        Map<String, Matrix3f> trackRotations = new HashMap<>();

        Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
        try {
            // Pass 1: opaque structural hulls
            var cutoutShader = GameRenderer.getRendertypeCutoutMippedShader();
            if (cutoutShader != null) {
                RenderSystem.enableDepthTest();
                RenderSystem.depthMask(true);
                RenderSystem.disableBlend();
                for (var part : gpu.parts) {
                    if (part.spec.isTranslucent() || part.spec.phase() > builtPhases) continue;
                    if (!isPartVisible(part, pivot, axes, motion, animSeconds, frustum, trackRotations)) continue;
                    var mat = partMatrix(part.spec, basePose, pivot, cam, axes, motion, animSeconds, staticMat,
                            trackMats);
                    drawPart(part.buffer(useLod1), part.material.base(), mat, projection, cutoutShader,
                            part.spec.isDoubleSided());
                }
            }

            // Pass 2: internal megastructure effects (planet, floating gardens, canopy) before translucent glass
            drawFx.run();

            // Pass 3: translucent glass panels and sphere shell
            var translucentShader = GameRenderer.getRendertypeTranslucentShader();
            if (translucentShader != null) {
                RenderSystem.enableDepthTest();
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                for (var part : gpu.parts) {
                    if (!part.spec.isTranslucent() || part.spec.phase() > builtPhases) continue;
                    if (!isPartVisible(part, pivot, axes, motion, animSeconds, frustum, trackRotations)) continue;
                    var mat = partMatrix(part.spec, basePose, pivot, cam, axes, motion, animSeconds, staticMat,
                            trackMats);
                    drawPart(part.buffer(useLod1), part.material.base(), mat, projection, translucentShader,
                            part.spec.isDoubleSided());
                }
            }

            // Pass 4: emissive bloom overlays matching GigaBlockFactory's cube_2_layer _bloom casings
            if (cutoutShader != null) {
                RenderSystem.enableDepthTest();
                RenderSystem.depthFunc(GL11.GL_LEQUAL);
                RenderSystem.depthMask(false);
                RenderSystem.polygonOffset(-1.0f, -1.0f);
                RenderSystem.enablePolygonOffset();
                for (var part : gpu.parts) {
                    if (!part.spec.isEmissive() || part.material.bloom() == null || part.spec.phase() > builtPhases)
                        continue;
                    if (!isPartVisible(part, pivot, axes, motion, animSeconds, frustum, trackRotations)) continue;
                    var mat = partMatrix(part.spec, basePose, pivot, cam, axes, motion, animSeconds, staticMat,
                            trackMats);
                    var vbo = part.buffer(useLod1);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                    drawPart(vbo, part.material.bloom(), mat, projection, cutoutShader, part.spec.isDoubleSided());

                    if (!useLod1 && translucentShader != null) {
                        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.35f);
                        drawPart(vbo, part.material.bloom(), mat, projection, translucentShader,
                                part.spec.isDoubleSided());
                    }
                }
            }
        } finally {
            VertexBuffer.unbind();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.polygonOffset(0.0f, 0.0f);
            RenderSystem.disablePolygonOffset();
            RenderSystem.depthFunc(GL11.GL_LEQUAL);
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableCull();
            Minecraft.getInstance().gameRenderer.lightTexture().turnOffLightLayer();
        }
        return true;
    }

    private static void drawPart(VertexBuffer vbo, ResourceLocation texture, Matrix4f modelMat, Matrix4f projection,
                                 ShaderInstance shader, boolean doubleSided) {
        if (doubleSided) RenderSystem.disableCull();
        else RenderSystem.enableCull();
        bindRepeatingTexture(texture);
        vbo.bind();
        vbo.drawWithShader(modelMat, projection, shader);
    }

    private static void bindRepeatingTexture(ResourceLocation texture) {
        var texManager = Minecraft.getInstance().getTextureManager();
        var texObj = texManager.getTexture(texture);
        RenderSystem.bindTexture(texObj.getId());
        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        RenderSystem.setShaderTexture(0, texture);
    }

    private static Matrix4f partMatrix(MegaModelData.Part part, Matrix4f basePose, Vec3 pivot, Vec3 cam, Matrix3f axes,
                                       MegaMotion motion, double seconds, Matrix4f staticMat,
                                       Map<String, Matrix4f> cache) {
        if (!part.isMoving()) return staticMat;
        var track = motion.tracks.get(part.track());
        if (track == null) return staticMat;
        return cache.computeIfAbsent(part.track(), name -> {
            var q = new Quaternionf();
            var shift = new Vector3f();
            track.at(seconds, q, shift);
            Matrix3f worldRot = new Matrix3f(axes).mul(new Matrix3f().rotation(q));
            axes.transform(shift);
            return new Matrix4f(basePose)
                    .translate((float) (pivot.x - cam.x) + shift.x,
                            (float) (pivot.y - cam.y) + shift.y,
                            (float) (pivot.z - cam.z) + shift.z)
                    .mul(new Matrix4f().set(worldRot));
        });
    }

    private static boolean isPartVisible(GpuPart part, Vec3 pivot, Matrix3f axes, MegaMotion motion, double seconds,
                                         @Nullable Frustum frustum, Map<String, Matrix3f> trackRotations) {
        if (frustum == null) return true;
        if (!part.spec.isMoving()) {
            return frustum.isVisible(transformAabb(part.spec.boundsMin(), part.spec.boundsMax(), pivot, axes, null));
        }
        var track = motion.tracks.get(part.spec.track());
        if (track == null) {
            return frustum.isVisible(transformAabb(part.spec.boundsMin(), part.spec.boundsMax(), pivot, axes, null));
        }
        Matrix3f rot = trackRotations.computeIfAbsent(part.spec.track(), name -> {
            var q = new Quaternionf();
            var shift = new Vector3f();
            track.at(seconds, q, shift);
            return new Matrix3f(axes).mul(new Matrix3f().rotation(q));
        });
        return frustum.isVisible(transformAabb(part.spec.boundsMin(), part.spec.boundsMax(), pivot, rot, null));
    }

    private static AABB sphereBox(Vec3 center, float radius) {
        return new AABB(center.x - radius, center.y - radius, center.z - radius,
                center.x + radius, center.y + radius, center.z + radius);
    }

    private static AABB transformAabb(float[] min, float[] max, Vec3 pivot, Matrix3f rot, @Nullable Vector3f shift) {
        float sx = shift == null ? 0 : shift.x;
        float sy = shift == null ? 0 : shift.y;
        float sz = shift == null ? 0 : shift.z;
        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;
        var v = new Vector3f();
        for (int i = 0; i < 8; i++) {
            v.set((i & 1) == 0 ? min[0] : max[0], (i & 2) == 0 ? min[1] : max[1], (i & 4) == 0 ? min[2] : max[2]);
            rot.transform(v);
            double x = pivot.x + sx + v.x;
            double y = pivot.y + sy + v.y;
            double z = pivot.z + sz + v.z;
            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (z < minZ) minZ = z;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
            if (z > maxZ) maxZ = z;
        }
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    // ------------------------------------------------------------------ VBO upload (built once per model)

    private static GpuModel uploadModel(MegaModelData data) {
        long t0 = System.nanoTime();
        List<GpuPart> gpuParts = new ArrayList<>(data.parts.size());
        float maxRadius = 0;
        for (var part : data.parts) {
            var mat = MATERIALS.getOrDefault(part.material(), DEFAULT_MATERIAL);
            var lod0 = uploadBuffer(part, part.lod0Indices());
            var lod1 = part.lod1Indices().length > 0 ? uploadBuffer(part, part.lod1Indices()) : null;
            var gpuPart = new GpuPart(part, mat, lod0, lod1);
            if (gpuPart.maxRadius > maxRadius) maxRadius = gpuPart.maxRadius;
            gpuParts.add(gpuPart);
        }
        TypeOmegaMod.LOGGER.info(
                "[Type Omega perf] 3D overlay '{}' uploaded: {} parts, {} verts, {} LOD0 tris, {} LOD1 tris in {} ms",
                data.name, data.parts.size(), data.totalVertices(), data.totalLod0Triangles(),
                data.totalLod1Triangles(), (System.nanoTime() - t0) / 1_000_000);
        return new GpuModel(gpuParts, maxRadius);
    }

    private static VertexBuffer uploadBuffer(MegaModelData.Part part, int[] indices) {
        float[] verts = part.vertices();
        int light = part.isEmissive() ? LightTexture.FULL_BRIGHT : LightTexture.pack(12, 15);
        var builder = new BufferBuilder(Math.min(indices.length, 1 << 16) * DefaultVertexFormat.BLOCK.getVertexSize());
        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.BLOCK);
        for (int idx : indices) {
            int o = idx * MegaModelData.STRIDE_FLOATS;
            float x = verts[o];
            float y = verts[o + 1];
            float z = verts[o + 2];
            float nx = verts[o + 3];
            float ny = verts[o + 4];
            float nz = verts[o + 5];
            float u = verts[o + 6];
            float v = verts[o + 7];
            float shade = part.isEmissive() ? 1.0f :
                    0.65f + 0.35f * Math.max(0.0f, Math.min(1.0f, 0.5f + 0.35f * ny + 0.25f * nz + 0.15f * nx));
            builder.vertex(x, y, z)
                    .color(shade, shade, shade, 1.0f)
                    .uv(u, v)
                    .uv2(light)
                    .normal(nx, ny, nz)
                    .endVertex();
        }
        var vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
        vbo.bind();
        vbo.upload(builder.end());
        VertexBuffer.unbind();
        return vbo;
    }
}
