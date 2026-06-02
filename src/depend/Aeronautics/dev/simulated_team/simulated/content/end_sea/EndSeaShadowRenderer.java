/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.MeshData
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.sublevel.ClientSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  foundry.veil.api.client.render.MatrixStack
 *  foundry.veil.api.client.render.VeilRenderSystem
 *  foundry.veil.api.client.render.framebuffer.AdvancedFbo
 *  foundry.veil.api.client.render.post.PostPipeline
 *  foundry.veil.api.client.render.post.PostProcessingManager
 *  foundry.veil.api.event.VeilRenderLevelStageEvent$Stage
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.client.Camera
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.end_sea;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.void_anchor.VoidAnchorBlockEntity;
import dev.simulated_team.simulated.content.end_sea.EndSeaPhysics;
import dev.simulated_team.simulated.content.end_sea.EndSeaPhysicsData;
import dev.simulated_team.simulated.util.SimpleSubLevelGroupRenderer;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class EndSeaShadowRenderer {
    public static final float SHADOW_VOLUME_RADIUS = 128.0f;
    private static final Matrix4f PROJECTION_MAT = new Matrix4f();
    private static final Vector3d SHADOW_CAMERA_POSITION = new Vector3d();
    private static boolean isRenderingShadowMap = false;
    private static final ObjectArrayList<Vector3dc> voidAnchors = new ObjectArrayList();

    public static boolean isEnabled() {
        return true;
    }

    public static void renderShadowMap(VeilRenderLevelStageEvent.Stage stage, LevelRenderer levelRenderer, MultiBufferSource.BufferSource bufferSource, MatrixStack matrixStack, Matrix4fc frustumMatrix, Matrix4fc projectionMatrix, int renderTick, DeltaTracker deltaTracker, Camera camera, Frustum frustum) {
        if (!EndSeaShadowRenderer.isEnabled() || stage != VeilRenderLevelStageEvent.Stage.AFTER_LEVEL) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        EndSeaPhysics physics = EndSeaPhysicsData.of((Level)level);
        if (physics == null) {
            return;
        }
        AdvancedFbo fbo = EndSeaShadowRenderer.getShadowsFramebuffer();
        if (fbo == null) {
            return;
        }
        float zNear = 0.5f;
        Matrix4f modelView = new Matrix4f();
        PROJECTION_MAT.identity().ortho(-128.0f, 128.0f, -128.0f, 128.0f, 0.5f, 128.0f);
        Vec3 cameraPosition = camera.getPosition();
        Vec3 shadowCameraPosition = new Vec3(cameraPosition.x, physics.startY() - 128.0, cameraPosition.z);
        SHADOW_CAMERA_POSITION.set((Vector3dc)JOMLConversion.toJOML((Position)shadowCameraPosition));
        SHADOW_CAMERA_POSITION.set(Math.floor(EndSeaShadowRenderer.SHADOW_CAMERA_POSITION.x), EndSeaShadowRenderer.SHADOW_CAMERA_POSITION.y, Math.floor(EndSeaShadowRenderer.SHADOW_CAMERA_POSITION.z));
        isRenderingShadowMap = true;
        Quaternionf orientation = new Quaternionf().rotateX(-1.5707964f);
        BoundingBox3d bounds = new BoundingBox3d(-3.0E7, -10000.0, -3.0E7, 3.0E7, 10000.0, 3.0E7);
        ObjectArrayList clientSubLevelGroup = new ObjectArrayList();
        Iterable intersecting = Sable.HELPER.getAllIntersecting((Level)level, (BoundingBox3dc)bounds);
        for (SubLevel subLevel : intersecting) {
            clientSubLevelGroup.add((ClientSubLevel)subLevel);
        }
        fbo.bind(true);
        fbo.clear();
        SimpleSubLevelGroupRenderer.renderGroup(level, (Collection<ClientSubLevel>)clientSubLevelGroup, fbo, modelView, PROJECTION_MAT, SHADOW_CAMERA_POSITION, orientation, 8.0f, false);
        isRenderingShadowMap = false;
        PostProcessingManager post = VeilRenderSystem.renderer().getPostProcessingManager();
        PostPipeline pipeline = post.getPipeline(Simulated.path("spread_end_sea"));
        if (pipeline != null) {
            for (int i = 0; i < 5; ++i) {
                post.runPipeline(pipeline, false);
            }
        }
    }

    public static void renderVoidAnchors(Camera camera) {
        if (voidAnchors.isEmpty()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)Simulated.path("textures/effects/cracks.png"));
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        ShaderInstance shader = RenderSystem.getShader();
        if (shader == null) {
            return;
        }
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.depthMask((boolean)true);
        RenderSystem.enableDepthTest();
        shader.setDefaultUniforms(VertexFormat.Mode.QUADS, RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), minecraft.getWindow());
        shader.apply();
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        Vector3d pos = new Vector3d();
        Vec3 cameraPos = camera.getPosition();
        for (Vector3dc voidAnchor : voidAnchors) {
            float size = 60.0f;
            voidAnchor.sub(cameraPos.x, cameraPos.y, cameraPos.z, pos);
            Matrix4f pose = new Matrix4f().translate((float)pos.x, (float)pos.y, (float)pos.z);
            builder.addVertex(pose, -60.0f, 0.0f, -60.0f).setUv(0.0f, 0.0f).setColor(0.5f, 0.0f, 0.0f, 1.0f);
            builder.addVertex(pose, 60.0f, 0.0f, -60.0f).setUv(1.0f, 0.0f).setColor(0.5f, 0.0f, 0.0f, 1.0f);
            builder.addVertex(pose, 60.0f, 0.0f, 60.0f).setUv(1.0f, 1.0f).setColor(0.5f, 0.0f, 0.0f, 1.0f);
            builder.addVertex(pose, -60.0f, 0.0f, 60.0f).setUv(0.0f, 1.0f).setColor(0.5f, 0.0f, 0.0f, 1.0f);
        }
        BufferUploader.drawWithShader((MeshData)builder.buildOrThrow());
        RenderSystem.disableDepthTest();
        shader.clear();
        voidAnchors.clear();
    }

    @Nullable
    public static AdvancedFbo getShadowsFramebuffer() {
        return VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(Simulated.path("end_sea_shadows"));
    }

    public static boolean renderingShadowMap() {
        return isRenderingShadowMap;
    }

    public static Vector3dc getLastRenderOrigin() {
        return SHADOW_CAMERA_POSITION;
    }

    public static void addVoidAnchor(VoidAnchorBlockEntity voidAnchor) {
        voidAnchors.add((Object)Sable.HELPER.projectOutOfSubLevel(voidAnchor.getLevel(), JOMLConversion.atCenterOf((Vec3i)voidAnchor.getBlockPos())));
    }
}
