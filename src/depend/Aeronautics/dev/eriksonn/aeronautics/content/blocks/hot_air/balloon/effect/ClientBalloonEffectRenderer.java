/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.systems.RenderSystem
 *  foundry.veil.api.client.render.VeilRenderSystem
 *  foundry.veil.api.client.render.framebuffer.AdvancedFbo
 *  foundry.veil.api.client.render.post.PostPipeline
 *  foundry.veil.api.client.render.post.PostPipeline$Context
 *  foundry.veil.api.client.render.post.PostProcessingManager
 *  foundry.veil.api.client.render.shader.program.ShaderProgram
 *  foundry.veil.api.client.render.shader.uniform.ShaderUniformAccess
 *  foundry.veil.api.event.VeilRenderLevelStageEvent$Stage
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.LevelAccessor
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.lwjgl.opengl.GL30
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.effect;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.content.blocks.hot_air.BlockEntityLiftingGasProvider;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.Balloon;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.ClientBalloon;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.effect.HeatedCulledRenderRegion;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.map.BalloonMap;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.render.shader.uniform.ShaderUniformAccess;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.opengl.GL30;

public class ClientBalloonEffectRenderer {
    private static final ResourceLocation FBO_ID = Aeronautics.path("soft_light");
    private static final ResourceLocation POST_SHADER_ID = Aeronautics.path("soft_light");
    private static final ResourceLocation SIDE_TEXTURE = Aeronautics.path("textures/special/heat_overlay.png");
    private static final ResourceLocation TOP_TEXTURE = Aeronautics.path("textures/special/lava_still.png");
    private static final ResourceLocation SHADER_ID = Aeronautics.path("hot_air_overlay");
    @Nullable
    private static AdvancedFbo overlayFbo;

    public static void onRenderLevelStage(VeilRenderLevelStageEvent.Stage stage, Matrix4fc frustumMatrix, Matrix4fc projectionMatrix, int renderTick) {
        if (stage != VeilRenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            ClientBalloonEffectRenderer.freeFbo();
            return;
        }
        BalloonMap ballonMap = (BalloonMap)BalloonMap.MAP.get((LevelAccessor)level);
        if (ballonMap.isEmpty()) {
            ClientBalloonEffectRenderer.freeFbo();
            return;
        }
        Window window = minecraft.getWindow();
        if (overlayFbo == null || overlayFbo.getWidth() != window.getWidth() || overlayFbo.getHeight() != window.getHeight()) {
            ClientBalloonEffectRenderer.freeFbo();
            overlayFbo = AdvancedFbo.withSize((int)window.getWidth(), (int)window.getHeight()).addColorTextureBuffer().setDepthTextureBuffer().build(true);
        }
        ClientBalloonEffectRenderer.renderBalloonEffects(ballonMap, frustumMatrix, projectionMatrix, renderTick);
    }

    private static void renderBalloonEffects(BalloonMap balloonMap, Matrix4fc frustumMatrix, Matrix4fc projectionMatrix, int renderTick) {
        Minecraft minecraft = Minecraft.getInstance();
        float partialTicks = minecraft.getTimer().getGameTimeDeltaPartialTick(false);
        ShaderProgram shader = VeilRenderSystem.setShader((ResourceLocation)SHADER_ID);
        if (shader == null) {
            return;
        }
        overlayFbo.bind(false);
        overlayFbo.clear(0.0f, 0.0f, 0.0f, 0.0f, 16640);
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)SIDE_TEXTURE);
        RenderSystem.setShaderTexture((int)1, (ResourceLocation)TOP_TEXTURE);
        RenderSystem.enableCull();
        RenderSystem.depthMask((boolean)true);
        RenderSystem.enableDepthTest();
        GL30.glCullFace((int)1028);
        RenderSystem.polygonOffset((float)-0.5f, (float)-30.0f);
        RenderSystem.enablePolygonOffset();
        float scrollAmount = ((float)renderTick + partialTicks) / -20.0f;
        ShaderUniformAccess scrollUniform = shader.getUniformSafe((CharSequence)"Scroll");
        ShaderUniformAccess yCutoffUniform = shader.getUniformSafe((CharSequence)"CutoffY");
        scrollUniform.setFloat((float)(Math.floor(scrollAmount * 16.0f) / 16.0));
        float brightness = 0.85f;
        float alpha = 1.0f;
        RenderSystem.setShaderColor((float)0.85f, (float)0.85f, (float)0.85f, (float)1.0f);
        Matrix4f modelViewMat = new Matrix4f(frustumMatrix);
        Matrix4f projMat = new Matrix4f(projectionMatrix);
        for (Balloon balloon : balloonMap.getBalloons()) {
            ClientBalloon clientBalloon = (ClientBalloon)balloon;
            HeatedCulledRenderRegion renderRegion = clientBalloon.getRenderRegion();
            if (renderRegion == null) continue;
            float filledPercent = 0.0f;
            for (BlockEntityLiftingGasProvider heater : balloon.getHeaters()) {
                filledPercent = Math.max(filledPercent, (float)heater.getClientPredictedVolume() / (float)balloon.getCapacity());
            }
            filledPercent = Mth.clamp((float)filledPercent, (float)0.0f, (float)1.0f);
            yCutoffUniform.setFloat((1.0f - filledPercent) * (balloon.getHeight() + 1.0f));
            renderRegion.render(modelViewMat, projMat);
        }
        RenderSystem.polygonOffset((float)0.0f, (float)0.0f);
        RenderSystem.disablePolygonOffset();
        GL30.glCullFace((int)1029);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        AdvancedFbo.unbind();
        ClientBalloonEffectRenderer.applyHeatingToScreen();
    }

    private static void applyHeatingToScreen() {
        PostProcessingManager manager = VeilRenderSystem.renderer().getPostProcessingManager();
        PostPipeline pipeline = manager.getPipeline(POST_SHADER_ID);
        PostPipeline.Context context = manager.getPostPipelineContext();
        context.setFramebuffer(FBO_ID, overlayFbo);
        manager.runPipeline(pipeline);
    }

    private static void freeFbo() {
        if (overlayFbo != null) {
            overlayFbo.free();
        }
        overlayFbo = null;
    }
}
