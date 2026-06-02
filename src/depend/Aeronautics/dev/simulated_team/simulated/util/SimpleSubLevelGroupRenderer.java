/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Lighting
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.mixinhelpers.sublevel_render.vanilla.VanillaSubLevelBlockEntityRenderer
 *  dev.ryanhcode.sable.mixinterface.BlockEntityRenderDispatcherExtension
 *  dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.flywheel.SubLevelEmbedding
 *  dev.ryanhcode.sable.sublevel.ClientSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  dev.ryanhcode.sable.sublevel.render.SubLevelRenderData
 *  dev.ryanhcode.sable.sublevel.render.dispatcher.SubLevelRenderDispatcher
 *  dev.ryanhcode.sable.sublevel.render.dispatcher.SubLevelRenderDispatcher$BlockEntityRenderer
 *  dev.ryanhcode.sable.sublevel.render.vanilla.VanillaSingleSubLevelRenderData
 *  foundry.veil.api.client.render.CameraMatrices
 *  foundry.veil.api.client.render.VeilRenderSystem
 *  foundry.veil.api.client.render.framebuffer.AdvancedFbo
 *  foundry.veil.impl.client.render.perspective.LevelPerspectiveCamera
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 */
package dev.simulated_team.simulated.util;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.mixinhelpers.sublevel_render.vanilla.VanillaSubLevelBlockEntityRenderer;
import dev.ryanhcode.sable.mixinterface.BlockEntityRenderDispatcherExtension;
import dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.flywheel.SubLevelEmbedding;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.render.SubLevelRenderData;
import dev.ryanhcode.sable.sublevel.render.dispatcher.SubLevelRenderDispatcher;
import dev.ryanhcode.sable.sublevel.render.vanilla.VanillaSingleSubLevelRenderData;
import dev.simulated_team.simulated.mixin_interface.diagram.LightTextureExtension;
import dev.simulated_team.simulated.mixin_interface.diagram.VisualManagerExtension;
import dev.simulated_team.simulated.mixin_interface.diagram.VisualizationManagerExtension;
import foundry.veil.api.client.render.CameraMatrices;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.impl.client.render.perspective.LevelPerspectiveCamera;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;

public class SimpleSubLevelGroupRenderer {
    private static final LevelPerspectiveCamera CAMERA = new LevelPerspectiveCamera();
    private static final Matrix4f TRANSFORM = new Matrix4f();
    private static final Matrix4f BACKUP_PROJECTION = new Matrix4f();
    private static final CameraMatrices BACKUP_CAMERA_MATRICES = new CameraMatrices();
    public static boolean RENDERING_SIMPLE = false;

    public static Collection<ClientSubLevel> getRenderedChain(ClientSubLevel subLevel) {
        ObjectOpenHashSet visited = new ObjectOpenHashSet();
        ObjectOpenHashSet frontier = new ObjectOpenHashSet();
        frontier.add((Object)subLevel);
        while (!frontier.isEmpty()) {
            ClientSubLevel current = (ClientSubLevel)frontier.iterator().next();
            frontier.remove((Object)current);
            visited.add((Object)current);
            Iterable intersecting = Sable.HELPER.getAllIntersecting((Level)current.getLevel(), (BoundingBox3dc)new BoundingBox3d(current.boundingBox()));
            for (SubLevel neighbor : intersecting) {
                ClientSubLevel serverNeighbor = (ClientSubLevel)neighbor;
                if (visited.contains((Object)serverNeighbor)) continue;
                frontier.add((Object)serverNeighbor);
            }
        }
        return visited;
    }

    public static void renderChain(SubLevel subLevel, AdvancedFbo fbo, Matrix4f modelView, Matrix4f projectionMat, Vector3d cameraPosition, Quaternionf orientation, float partialTicks) {
        ClientSubLevel clientSubLevel = (ClientSubLevel)subLevel;
        ClientLevel level = clientSubLevel.getLevel();
        Collection<ClientSubLevel> subLevels = SimpleSubLevelGroupRenderer.getRenderedChain(clientSubLevel);
        SimpleSubLevelGroupRenderer.renderGroup(level, subLevels, fbo, modelView, projectionMat, cameraPosition, orientation, partialTicks, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void renderGroup(ClientLevel level, Collection<ClientSubLevel> subLevels, AdvancedFbo fbo, Matrix4f modelView, Matrix4f projectionMat, Vector3d cameraPosition, Quaternionf orientation, float partialTicks, boolean renderPlayers) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        bufferSource.endBatch();
        if (subLevels.isEmpty()) {
            AdvancedFbo.unbind();
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        GameRenderer gameRenderer = minecraft.gameRenderer;
        LightTexture lightTexture = gameRenderer.lightTexture();
        VanillaSubLevelBlockEntityRenderer beRenderer = new VanillaSubLevelBlockEntityRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.renderBuffers(), (Long2ObjectMap)new Long2ObjectOpenHashMap());
        CAMERA.setup((Vector3dc)cameraPosition, null, minecraft.level, (Quaternionfc)orientation, 0.0f);
        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(TRANSFORM.set((Matrix4fc)modelView));
        poseStack.mulPose(CAMERA.rotation());
        BACKUP_PROJECTION.set((Matrix4fc)RenderSystem.getProjectionMatrix());
        gameRenderer.resetProjectionMatrix(TRANSFORM.set((Matrix4fc)projectionMat));
        CameraMatrices matrices = VeilRenderSystem.renderer().getCameraMatrices();
        matrices.backup(BACKUP_CAMERA_MATRICES);
        Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
        matrix4fstack.pushMatrix();
        matrix4fstack.identity();
        matrix4fstack.mul((Matrix4fc)poseStack.last().pose());
        RenderSystem.applyModelViewMatrix();
        AdvancedFbo drawFbo = VeilRenderSystem.renderer().getDynamicBufferManger().getDynamicFbo(fbo);
        drawFbo.bind(true);
        try {
            Object extension;
            Lighting.setupNetherLevel();
            ((LightTextureExtension)lightTexture).simulated$makeDiagramLightTexture(0.65f);
            RENDERING_SIMPLE = true;
            for (RenderType layer : RenderType.chunkBufferLayers()) {
                layer.setupRenderState();
                ShaderInstance shader = RenderSystem.getShader();
                shader.setDefaultUniforms(VertexFormat.Mode.QUADS, RenderSystem.getModelViewMatrix(), projectionMat, minecraft.getWindow());
                shader.apply();
                SubLevelRenderDispatcher.get().renderSectionLayer(subLevels, layer, shader, cameraPosition.x, cameraPosition.y, cameraPosition.z, RenderSystem.getModelViewMatrix(), projectionMat, partialTicks);
                VertexConsumer consumer = bufferSource.getBuffer(layer);
                for (ClientSubLevel sublevel : subLevels) {
                    SubLevelRenderData data = sublevel.getRenderData();
                    if (!(data instanceof VanillaSingleSubLevelRenderData)) continue;
                    VanillaSingleSubLevelRenderData singleRenderData = (VanillaSingleSubLevelRenderData)data;
                    singleRenderData.renderSingleBlock(layer, consumer, modelView, cameraPosition.x, cameraPosition.y, cameraPosition.z);
                }
                bufferSource.endBatch(layer);
                shader.clear();
                layer.clearRenderState();
            }
            ((LightTextureExtension)lightTexture).simulated$makeDiagramLightTexture(1.0f);
            RENDERING_SIMPLE = false;
            VisualizationManager visualizationManager = VisualizationManager.get((LevelAccessor)level);
            if (visualizationManager instanceof VisualizationManagerExtension) {
                extension = (VisualizationManagerExtension)visualizationManager;
                extension.sable$setDrawingDiagram(true);
                for (ClientSubLevel beSubLevel : subLevels) {
                    BlockEntityRenderDispatcherExtension dispatcher = (BlockEntityRenderDispatcherExtension)beRenderer.getBlockEntityRenderDispatcher();
                    SubLevelEmbedding embeddingInfo = ((VisualManagerExtension)visualizationManager.blockEntities()).sable$getBEEmbeddingInfo(beSubLevel);
                    if (embeddingInfo == null) continue;
                    Vector3d chunkOffset = new Vector3d();
                    Matrix4f transformation = new Matrix4f();
                    Matrix4f transformationInverse = new Matrix4f();
                    SubLevelRenderData data = beSubLevel.getRenderData();
                    beSubLevel.renderPose().rotationPoint().negate(chunkOffset.zero());
                    data.getTransformation(cameraPosition.x, cameraPosition.y, cameraPosition.z, transformation);
                    Vector3f c = transformation.invert(transformationInverse).transformPosition(new Vector3f());
                    dispatcher.sable$setCameraPosition(new Vec3((double)c.x - chunkOffset.x(), (double)c.y - chunkOffset.y(), (double)c.z - chunkOffset.z()));
                    PoseStack beMatrices = new PoseStack();
                    beMatrices.pushPose();
                    beMatrices.mulPose(transformation);
                    beRenderer.renderBlockEntities((Collection)embeddingInfo.blockEntities(), beMatrices, partialTicks, -chunkOffset.x, -chunkOffset.y, -chunkOffset.z);
                    beMatrices.popPose();
                    dispatcher.sable$setCameraPosition(null);
                }
            }
            SubLevelRenderDispatcher.get().renderBlockEntities(subLevels, (SubLevelRenderDispatcher.BlockEntityRenderer)beRenderer, cameraPosition.x, cameraPosition.y, cameraPosition.z, partialTicks);
            for (ClientSubLevel entitySubLevel : subLevels) {
                List entities = level.getEntitiesOfClass(Entity.class, entitySubLevel.getPlot().getBoundingBox().toAABB().inflate(16.0));
                PoseStack entityPoseStack = new PoseStack();
                entityPoseStack.pushPose();
                entityPoseStack.mulPose(TRANSFORM.set((Matrix4fc)modelView));
                for (Entity entity : entities) {
                    if (Sable.HELPER.getContaining(entity) != entitySubLevel && Sable.HELPER.getTrackingOrVehicleSubLevel(entity) != entitySubLevel || !renderPlayers && entity instanceof Player) continue;
                    float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(!level.tickRateManager().isEntityFrozen(entity));
                    minecraft.levelRenderer.renderEntity(entity, cameraPosition.x, cameraPosition.y, cameraPosition.z, partialTick, entityPoseStack, (MultiBufferSource)bufferSource);
                }
                entityPoseStack.popPose();
            }
            if (visualizationManager instanceof VisualizationManagerExtension) {
                extension = (VisualizationManagerExtension)visualizationManager;
                extension.sable$setDrawingDiagram(false);
            }
            bufferSource.endBatch();
        }
        finally {
            if (level.effects().constantAmbientLight()) {
                Lighting.setupNetherLevel();
            } else {
                Lighting.setupLevel();
            }
            matrices.restore(BACKUP_CAMERA_MATRICES);
            matrix4fstack.popMatrix();
            RenderSystem.applyModelViewMatrix();
            gameRenderer.resetProjectionMatrix(BACKUP_PROJECTION);
            AdvancedFbo.unbind();
            lightTexture.updateLightTexture(partialTicks);
        }
    }
}
