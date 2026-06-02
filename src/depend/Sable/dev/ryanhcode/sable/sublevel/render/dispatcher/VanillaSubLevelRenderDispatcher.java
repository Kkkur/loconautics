/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.shaders.FogShape
 *  com.mojang.blaze3d.shaders.Uniform
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.MeshData
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  foundry.veil.api.client.render.CullFrustum
 *  foundry.veil.api.client.render.MatrixStack
 *  foundry.veil.api.client.render.VeilRenderBridge
 *  foundry.veil.api.client.render.profiler.RenderProfilerCounter
 *  foundry.veil.api.client.render.profiler.VeilRenderProfiler
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.PrioritizeChunkUpdates
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.client.renderer.block.ModelBlockRenderer
 *  net.minecraft.client.renderer.chunk.RenderRegionCache
 *  net.minecraft.client.renderer.chunk.SectionRenderDispatcher
 *  net.minecraft.client.renderer.chunk.SectionRenderDispatcher$RenderSection
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Matrix4f
 *  org.joml.Vector3d
 *  org.joml.Vector3f
 */
package dev.ryanhcode.sable.sublevel.render.dispatcher;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.index.SableTags;
import dev.ryanhcode.sable.mixinterface.BlockEntityRenderDispatcherExtension;
import dev.ryanhcode.sable.mixinterface.dynamic_directional_shading.ModelBlockRendererCacheExtension;
import dev.ryanhcode.sable.render.sky_light_shadow.SableSkyLightShadows;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.render.SubLevelRenderData;
import dev.ryanhcode.sable.sublevel.render.dispatcher.SubLevelRenderDispatcher;
import dev.ryanhcode.sable.sublevel.render.vanilla.VanillaChunkedSubLevelRenderData;
import dev.ryanhcode.sable.sublevel.render.vanilla.VanillaSingleSubLevelRenderData;
import foundry.veil.api.client.render.CullFrustum;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.VeilRenderBridge;
import foundry.veil.api.client.render.profiler.RenderProfilerCounter;
import foundry.veil.api.client.render.profiler.VeilRenderProfiler;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.SequencedSet;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.PrioritizeChunkUpdates;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class VanillaSubLevelRenderDispatcher
implements SubLevelRenderDispatcher {
    private final SequencedSet<RenderType> singleBlockLayers = new LinkedHashSet<RenderType>();

    public static void setupDynamicEffects(ShaderInstance shader, boolean onSubLevel, boolean upload) {
        Uniform sableSkyLightScale;
        Uniform sableEnableNormalLighting = shader.getUniform("SableEnableNormalLighting");
        Uniform sableEnableSkyLightShadows = shader.getUniform("SableShadowsEnabled");
        if (sableEnableNormalLighting != null) {
            sableEnableNormalLighting.set(onSubLevel ? 1.0f : 0.0f);
            if (upload) {
                sableEnableNormalLighting.upload();
            }
        }
        if (sableEnableSkyLightShadows != null) {
            sableEnableSkyLightShadows.set(onSubLevel || !SableSkyLightShadows.isEnabled() ? 0.0f : 1.0f);
            if (upload) {
                sableEnableSkyLightShadows.upload();
            }
        }
        if ((sableSkyLightScale = shader.getUniform("SableSkyLightScale")) != null) {
            sableSkyLightScale.set(1.0f);
            if (upload) {
                sableSkyLightScale.upload();
            }
        }
    }

    public static boolean isSingleBlock(ClientSubLevel subLevel) {
        boolean isSingle;
        BoundingBox3ic bounds = subLevel.getPlot().getBoundingBox();
        boolean bl = isSingle = bounds != null && bounds.minX() == bounds.maxX() && bounds.minY() == bounds.maxY() && bounds.minZ() == bounds.maxZ();
        if (!isSingle) {
            return false;
        }
        BlockState blockState = subLevel.getLevel().getBlockState(new BlockPos(bounds.minX(), bounds.minY(), bounds.minZ()));
        return !blockState.is(SableTags.ALWAYS_CHUNK_RENDERING);
    }

    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
    }

    @Override
    public SubLevelRenderData resize(ClientSubLevel subLevel, SubLevelRenderData renderData) {
        if (renderData instanceof VanillaSingleSubLevelRenderData ^ VanillaSubLevelRenderDispatcher.isSingleBlock(subLevel)) {
            renderData.close();
            SubLevelRenderData data = this.createRenderData(subLevel);
            if (data instanceof VanillaChunkedSubLevelRenderData) {
                data.compileSections(PrioritizeChunkUpdates.NEARBY, new RenderRegionCache(), Minecraft.getInstance().gameRenderer.getMainCamera());
            }
            return data;
        }
        if (renderData instanceof VanillaChunkedSubLevelRenderData) {
            VanillaChunkedSubLevelRenderData chunkedRenderData = (VanillaChunkedSubLevelRenderData)renderData;
            chunkedRenderData.resize();
            chunkedRenderData.compileSections(PrioritizeChunkUpdates.NEARBY, new RenderRegionCache(), Minecraft.getInstance().gameRenderer.getMainCamera());
        }
        return renderData;
    }

    @Override
    public SubLevelRenderData createRenderData(ClientSubLevel subLevel) {
        if (VanillaSubLevelRenderDispatcher.isSingleBlock(subLevel)) {
            return new VanillaSingleSubLevelRenderData(subLevel);
        }
        SectionRenderDispatcher sectionRenderDispatcher = Minecraft.getInstance().levelRenderer.getSectionRenderDispatcher();
        return new VanillaChunkedSubLevelRenderData(subLevel, sectionRenderDispatcher);
    }

    @Override
    public void updateCulling(Iterable<ClientSubLevel> sublevels, double cameraX, double cameraY, double cameraZ, CullFrustum cullFrustum, boolean isSpectator) {
    }

    @Override
    public void renderSectionLayer(Iterable<ClientSubLevel> sublevels, RenderType renderType, ShaderInstance shader, double cameraX, double cameraY, double cameraZ, Matrix4f modelView, Matrix4f projection, float partialTicks) {
        FogShape fogShape = RenderSystem.getShaderFogShape();
        if (shader.FOG_SHAPE != null && fogShape != FogShape.SPHERE) {
            shader.FOG_SHAPE.set(FogShape.SPHERE.getIndex());
            shader.FOG_SHAPE.upload();
        }
        VanillaSubLevelRenderDispatcher.setupDynamicEffects(shader, true, true);
        VeilRenderProfiler profiler = VeilRenderProfiler.get();
        profiler.push("sublevel_render", RenderProfilerCounter.STANDARD_GEOMETRY);
        for (ClientSubLevel sublevel : sublevels) {
            SubLevelRenderData data = sublevel.getRenderData();
            if (!(data instanceof VanillaChunkedSubLevelRenderData)) {
                this.singleBlockLayers.addLast(renderType);
                continue;
            }
            VanillaChunkedSubLevelRenderData chunkedRenderData = (VanillaChunkedSubLevelRenderData)data;
            chunkedRenderData.renderChunkedSubLevel(renderType, shader, modelView, cameraX, cameraY, cameraZ);
        }
        profiler.pop();
        if (shader.FOG_SHAPE != null && fogShape != FogShape.SPHERE) {
            shader.FOG_SHAPE.set(fogShape.getIndex());
        }
        VanillaSubLevelRenderDispatcher.setupDynamicEffects(shader, false, false);
    }

    @Override
    public void renderAfterSections(Iterable<ClientSubLevel> sublevels, double cameraX, double cameraY, double cameraZ, Matrix4f modelView, Matrix4f projection, float partialTicks) {
        if (this.singleBlockLayers.isEmpty()) {
            return;
        }
        ModelBlockRendererCacheExtension ext = (ModelBlockRendererCacheExtension)ModelBlockRenderer.CACHE.get();
        ext.sable$setOnSubLevel(true);
        VeilRenderProfiler profiler = VeilRenderProfiler.get();
        profiler.push("sublevel_render_single", RenderProfilerCounter.STANDARD_GEOMETRY);
        for (RenderType layer : this.singleBlockLayers) {
            BufferBuilder consumer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
            for (ClientSubLevel sublevel : sublevels) {
                SubLevelRenderData data = sublevel.getRenderData();
                if (!(data instanceof VanillaSingleSubLevelRenderData)) continue;
                VanillaSingleSubLevelRenderData singleRenderData = (VanillaSingleSubLevelRenderData)data;
                singleRenderData.renderSingleBlock(layer, (VertexConsumer)consumer, modelView, cameraX, cameraY, cameraZ);
            }
            MeshData meshData = consumer.build();
            if (meshData == null) continue;
            layer.setupRenderState();
            ShaderInstance shader = Objects.requireNonNull(RenderSystem.getShader());
            shader.setDefaultUniforms(VertexFormat.Mode.QUADS, modelView, projection, Minecraft.getInstance().getWindow());
            shader.apply();
            VanillaSubLevelRenderDispatcher.setupDynamicEffects(shader, true, true);
            layer.draw(meshData);
            layer.clearRenderState();
            VanillaSubLevelRenderDispatcher.setupDynamicEffects(shader, false, false);
            shader.clear();
        }
        profiler.pop();
        ext.sable$setOnSubLevel(false);
        this.singleBlockLayers.clear();
    }

    @Override
    public void renderBlockEntities(Iterable<ClientSubLevel> sublevels, SubLevelRenderDispatcher.BlockEntityRenderer blockEntityRenderer, double cameraX, double cameraY, double cameraZ, float partialTick) {
        Vector3f cameraPosition = new Vector3f();
        Vector3d chunkOffset = new Vector3d();
        Matrix4f transformation = new Matrix4f();
        Matrix4f transformationInverse = new Matrix4f();
        BlockEntityRenderDispatcherExtension dispatcher = (BlockEntityRenderDispatcherExtension)blockEntityRenderer.getBlockEntityRenderDispatcher();
        PoseStack matrices = new PoseStack();
        MatrixStack matrixStack = VeilRenderBridge.create((PoseStack)matrices);
        for (ClientSubLevel sublevel : sublevels) {
            VanillaSingleSubLevelRenderData singleRenderData;
            BlockEntity renderBlockEntity;
            SubLevelRenderData data = sublevel.getRenderData();
            sublevel.renderPose().rotationPoint().negate(chunkOffset.zero());
            data.getTransformation(cameraX, cameraY, cameraZ, transformation);
            transformation.invert(transformationInverse).transformPosition(cameraPosition.zero());
            dispatcher.sable$setCameraPosition(new Vec3((double)cameraPosition.x - chunkOffset.x(), (double)cameraPosition.y - chunkOffset.y(), (double)cameraPosition.z - chunkOffset.z()));
            matrixStack.clear();
            matrices.mulPose(transformation);
            if (data instanceof VanillaChunkedSubLevelRenderData) {
                VanillaChunkedSubLevelRenderData chunkedRenderData = (VanillaChunkedSubLevelRenderData)data;
                for (SectionRenderDispatcher.RenderSection renderSection : chunkedRenderData.allRenderSections()) {
                    List blockEntities = renderSection.getCompiled().getRenderableBlockEntities();
                    if (blockEntities.isEmpty()) continue;
                    blockEntityRenderer.renderBlockEntities(blockEntities, matrices, partialTick, -chunkOffset.x, -chunkOffset.y, -chunkOffset.z);
                }
                continue;
            }
            if (!(data instanceof VanillaSingleSubLevelRenderData) || (renderBlockEntity = (singleRenderData = (VanillaSingleSubLevelRenderData)data).getRenderBlockEntity()) == null) continue;
            blockEntityRenderer.renderSingleBE(renderBlockEntity, matrices, partialTick, -chunkOffset.x, -chunkOffset.y, -chunkOffset.z);
        }
        dispatcher.sable$setCameraPosition(null);
    }

    @Override
    public void addDebugInfo(Consumer<String> consumer) {
    }

    public void free() {
    }
}
