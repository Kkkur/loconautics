/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  foundry.veil.api.client.render.MatrixStack
 *  foundry.veil.api.event.VeilRenderLevelStageEvent$Stage
 *  foundry.veil.platform.VeilEventPlatform
 *  net.minecraft.client.Camera
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.MouseHandler
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.client.renderer.debug.DebugRenderer
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3d
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package dev.ryanhcode.sable.debug;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.debug.GizmoScreen;
import dev.ryanhcode.sable.debug.GizmoSelection;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3d;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public class SableClientGizmoHandler {
    private Vec3 mouseDir = Vec3.ZERO;
    private boolean enabled = false;
    @Nullable
    private GizmoSelection selection;

    public void init() {
        VeilEventPlatform.INSTANCE.onVeilRenderLevelStage(this::onRenderStage);
    }

    public static Vec3 getRay(Matrix4fc projectionMatrix, float normalizedMouseX, float normalizedMouseY) {
        Vector4f clipCoords = new Vector4f(-normalizedMouseX, -normalizedMouseY, -1.0f, 0.0f);
        Vector4f eyeSpace = SableClientGizmoHandler.toEyeCoords(projectionMatrix, (Vector4fc)clipCoords);
        return new Vec3((double)eyeSpace.x, (double)eyeSpace.y, (double)eyeSpace.z).normalize();
    }

    private static Vector4f toEyeCoords(Matrix4fc projectionMatrix, Vector4fc clipCoords) {
        Matrix4f inverse = projectionMatrix.invert(new Matrix4f());
        Vector4f result = new Vector4f(clipCoords.x(), clipCoords.y(), clipCoords.z(), clipCoords.w());
        result.mul((Matrix4fc)inverse);
        result.set(result.x(), result.y(), 1.0f, 0.0f);
        return result;
    }

    @Nullable
    public GizmoSelection getSelection() {
        return this.selection;
    }

    private void onRenderStage(VeilRenderLevelStageEvent.Stage stage, LevelRenderer levelRenderer, MultiBufferSource.BufferSource bufferSource, MatrixStack matrixStack, Matrix4fc modelViewMat, Matrix4fc projMat, int renderTicks, DeltaTracker deltaTracker, Camera camera, Frustum frustum) {
        if (stage != VeilRenderLevelStageEvent.Stage.AFTER_WEATHER) {
            return;
        }
        if (!this.enabled) {
            return;
        }
        float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(false);
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        Vec3 cameraPos = camera.getPosition();
        ClientSubLevelContainer container = SubLevelContainer.getContainer(level);
        assert (container != null);
        this.updateMouseDir(minecraft, partialTicks);
        this.updateSelection();
        PoseStack poseStack = new PoseStack();
        for (SubLevel subLevel : ((SubLevelContainer)container).getAllSubLevels()) {
            ClientSubLevel clientSubLevel = (ClientSubLevel)subLevel;
            Pose3dc renderPose = clientSubLevel.renderPose();
            Vector3d renderPos = renderPose.position().sub(cameraPos.x, cameraPos.y, cameraPos.z, new Vector3d());
            poseStack.pushPose();
            poseStack.translate(renderPos.x, renderPos.y, renderPos.z);
            DebugRenderer.renderFilledBox((PoseStack)poseStack, (MultiBufferSource)bufferSource, (AABB)new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0).inflate(0.1), (float)1.0f, (float)1.0f, (float)1.0f, (float)0.4f);
            for (Direction.Axis axis : Direction.Axis.VALUES) {
                Direction dir = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis);
                Vec3i normal = dir.getNormal();
                float r = (float)(Math.max((double)normal.getX(), 0.2) * 0.8);
                float g = (float)(Math.max((double)normal.getY(), 0.2) * 0.8);
                float b = (float)(Math.max((double)normal.getZ(), 0.2) * 0.8);
                Vec3 normalD = new Vec3((double)normal.getX(), (double)normal.getY(), (double)normal.getZ());
                Vec3 expandDir = normalD.scale(2.0);
                float inflation = 0.04f;
                AABB bb = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0).inflate((double)0.04f).move(normalD.scale(0.125)).expandTowards(expandDir);
                if (this.selection != null && this.selection.subLevel().equals(clientSubLevel.getUniqueId()) && this.selection.axis() == axis) {
                    r *= 1.2f;
                    g *= 1.2f;
                    b *= 1.2f;
                }
                DebugRenderer.renderFilledBox((PoseStack)poseStack, (MultiBufferSource)bufferSource, (AABB)bb, (float)r, (float)g, (float)b, (float)0.9f);
            }
            poseStack.popPose();
        }
    }

    private void updateSelection() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        Vec3 cameraPos = minecraft.gameRenderer.getMainCamera().getPosition();
        PoseStack poseStack = new PoseStack();
        ClientSubLevelContainer container = SubLevelContainer.getContainer(level);
        assert (container != null);
        for (SubLevel subLevel : ((SubLevelContainer)container).getAllSubLevels()) {
            ClientSubLevel clientSubLevel = (ClientSubLevel)subLevel;
            Pose3dc renderPose = clientSubLevel.renderPose();
            Vector3d renderPos = renderPose.position().sub(cameraPos.x, cameraPos.y, cameraPos.z, new Vector3d());
            poseStack.pushPose();
            poseStack.translate(renderPos.x, renderPos.y, renderPos.z);
            for (Direction.Axis axis : Direction.Axis.VALUES) {
                Direction dir = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis);
                Vec3i normal = dir.getNormal();
                Vec3 normalD = new Vec3((double)normal.getX(), (double)normal.getY(), (double)normal.getZ());
                Vec3 expandDir = normalD.scale(2.0);
                float inflation = 0.04f;
                AABB bb = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0).inflate((double)0.04f).move(normalD.scale(0.125)).expandTowards(expandDir);
                if (!bb.move(renderPos.x, renderPos.y, renderPos.z).inflate((double)0.1f).clip(Vec3.ZERO, this.mouseDir.scale(100.0)).isPresent()) continue;
                this.selection = new GizmoSelection(clientSubLevel.getUniqueId(), axis);
                return;
            }
        }
        this.selection = null;
    }

    private void updateMouseDir(Minecraft minecraft, float partialTicks) {
        LocalPlayer player = minecraft.player;
        Window window = minecraft.getWindow();
        MouseHandler mouseHandler = minecraft.mouseHandler;
        double xPos = mouseHandler.xpos() / (double)window.getScreenWidth() * 2.0 - 1.0;
        double yPos = mouseHandler.ypos() / (double)window.getScreenHeight() * 2.0 - 1.0;
        GameRenderer gameRenderer = minecraft.gameRenderer;
        double fov = gameRenderer.getFov(gameRenderer.getMainCamera(), partialTicks, true);
        Matrix4f proj = gameRenderer.getProjectionMatrix(fov);
        float yaw = player.getViewYRot(partialTicks);
        float pitch = player.getViewXRot(partialTicks);
        this.mouseDir = SableClientGizmoHandler.getRay((Matrix4fc)proj, (float)xPos, (float)yPos).xRot((float)(-Math.toRadians(pitch))).yRot((float)(-Math.toRadians(yaw)));
    }

    public void start() {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen((Screen)new GizmoScreen());
        this.enabled = true;
    }

    public void stop() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof GizmoScreen) {
            minecraft.setScreen(null);
        }
        this.enabled = false;
    }

    public Vec3 getMouseDir() {
        return this.mouseDir;
    }
}
