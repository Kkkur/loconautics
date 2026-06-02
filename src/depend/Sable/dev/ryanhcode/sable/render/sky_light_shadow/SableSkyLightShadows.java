/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.shaders.Uniform
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  foundry.veil.api.client.render.MatrixStack
 *  foundry.veil.api.client.render.VeilLevelPerspectiveRenderer
 *  foundry.veil.api.client.render.VeilRenderSystem
 *  foundry.veil.api.client.render.framebuffer.AdvancedFbo
 *  foundry.veil.api.event.VeilRenderLevelStageEvent$Stage
 *  net.minecraft.client.Camera
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.core.Position
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.lwjgl.opengl.GL30
 */
package dev.ryanhcode.sable.render.sky_light_shadow;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.Uniform;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.VeilLevelPerspectiveRenderer;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.Position;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.lwjgl.opengl.GL30;

public class SableSkyLightShadows {
    public static final float SHADOW_VOLUME_SIZE = 128.0f;
    private static final ResourceLocation FRAMEBUFFER_NAME = Sable.sablePath("sub_level_shadow");
    private static final Matrix4f PROJECTION_MAT = new Matrix4f();
    private static final Vector3d SHADOW_CAMERA_POSITION = new Vector3d();
    private static final Quaternionf SHADOW_CAMERA_ORIENTATION = new Quaternionf();
    private static boolean isRenderingShadowMap = false;
    private static boolean isEnabled = false;

    public static boolean isEnabled() {
        return isEnabled;
    }

    public static void setIsEnabled(boolean isEnabled) {
        SableSkyLightShadows.isEnabled = isEnabled;
    }

    public static void renderShadowMap(VeilRenderLevelStageEvent.Stage stage, LevelRenderer levelRenderer, MultiBufferSource.BufferSource bufferSource, MatrixStack matrixStack, Matrix4fc frustumMatrix, Matrix4fc projectionMatrix, int renderTick, DeltaTracker deltaTracker, Camera camera, Frustum frustum) {
        if (!SableSkyLightShadows.isEnabled()) {
            return;
        }
        if (VeilLevelPerspectiveRenderer.isRenderingPerspective()) {
            return;
        }
        if (stage != VeilRenderLevelStageEvent.Stage.AFTER_LEVEL) {
            return;
        }
        AdvancedFbo fbo = SableSkyLightShadows.getShadowsFramebuffer();
        if (fbo != null) {
            fbo.bind(true);
            GL30.glClearColor((float)1.0f, (float)1.0f, (float)1.0f, (float)0.0f);
            fbo.clear();
            Minecraft client = Minecraft.getInstance();
            ClientLevel level = client.level;
            Window window = client.getWindow();
            Matrix4f modelView = new Matrix4f();
            PROJECTION_MAT.identity().ortho(-128.0f, 128.0f, -128.0f, 128.0f, 0.5f, 128.0f);
            Vec3 cameraPosition = camera.getPosition();
            Vec3 shadowCameraPosition = new Vec3(cameraPosition.x, cameraPosition.y + 64.0, cameraPosition.z);
            JOMLConversion.toJOML((Position)shadowCameraPosition, (Vector3d)SHADOW_CAMERA_POSITION);
            SHADOW_CAMERA_POSITION.set(Math.floor(SableSkyLightShadows.SHADOW_CAMERA_POSITION.x), SableSkyLightShadows.SHADOW_CAMERA_POSITION.y, Math.floor(SableSkyLightShadows.SHADOW_CAMERA_POSITION.z));
            isRenderingShadowMap = true;
            VeilLevelPerspectiveRenderer.render((AdvancedFbo)fbo, (Matrix4fc)modelView, (Matrix4fc)PROJECTION_MAT, (Vector3dc)SHADOW_CAMERA_POSITION, (Quaternionfc)SHADOW_CAMERA_ORIENTATION.identity().rotateX(1.5707964f), (float)8.0f, (DeltaTracker)deltaTracker, (boolean)false);
            isRenderingShadowMap = false;
        }
    }

    @Nullable
    public static AdvancedFbo getShadowsFramebuffer() {
        return VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(FRAMEBUFFER_NAME);
    }

    public static boolean renderingShadowMap() {
        return isRenderingShadowMap;
    }

    public static void bindShadowMapTexture(ShaderInstance shader) {
        Uniform offsetUniform;
        if (!SableSkyLightShadows.isEnabled()) {
            return;
        }
        Uniform volumeSizeUniform = shader.getUniform("SableShadowVolumeSize");
        if (volumeSizeUniform != null) {
            volumeSizeUniform.set(128.0f);
        }
        if ((offsetUniform = shader.getUniform("SableShadowOrigin")) != null) {
            Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            offsetUniform.set((float)(SableSkyLightShadows.SHADOW_CAMERA_POSITION.x - camera.x), (float)(SableSkyLightShadows.SHADOW_CAMERA_POSITION.y - camera.y), (float)(SableSkyLightShadows.SHADOW_CAMERA_POSITION.z - camera.z));
        }
        AdvancedFbo fbo = SableSkyLightShadows.getShadowsFramebuffer();
        shader.setSampler("SableShadowSampler", (Object)fbo.getDepthTextureAttachment());
    }
}
