/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  com.simibubi.create.foundation.item.render.CustomRenderedItemModel
 *  com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer
 *  com.simibubi.create.foundation.item.render.PartialItemModelRenderer
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.sublevel.ClientSubLevel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.core.Position
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaterniondc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 */
package dev.simulated_team.simulated.content.physics_staff;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.simulated_team.simulated.SimulatedClient;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffClientHandler;
import dev.simulated_team.simulated.index.SimPartialModels;
import dev.simulated_team.simulated.util.SimDistUtil;
import dev.simulated_team.simulated.util.SimMathUtils;
import java.util.UUID;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class PhysicsStaffItemRenderer
extends CustomRenderedItemModelRenderer {
    private static final Vector3d focusPos = new Vector3d();
    private static final Matrix4f itemProjMat = new Matrix4f();

    public static Vec3 getFirstPersonFocusPos(float pt) {
        GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        Camera camera = gameRenderer.getMainCamera();
        Vector3d focusPoint = new Vector3d((Vector3dc)focusPos);
        Quaternionf orientation = camera.rotation();
        orientation.transformInverse(focusPoint);
        Vector4f v4 = new Vector4f((float)focusPoint.x, (float)focusPoint.y, (float)focusPoint.z, 1.0f);
        Matrix4f actualProjMat = gameRenderer.getProjectionMatrix(gameRenderer.getFov(camera, AnimationTickHolder.getPartialTicks(), true));
        actualProjMat.invert(new Matrix4f()).transform(v4);
        itemProjMat.transform(v4);
        focusPoint.set((double)v4.x, (double)v4.y, (double)v4.z);
        orientation.transform(focusPoint);
        double fov = gameRenderer.getFov(camera, pt, true);
        focusPoint.mul(100.0 / fov);
        return JOMLConversion.toMojang((Vector3dc)focusPoint);
    }

    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext context, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        float openAmount = 0.0f;
        float cubeScale = 0.0f;
        PhysicsStaffClientHandler clientHandler = SimulatedClient.PHYSICS_STAFF_CLIENT_HANDLER;
        Minecraft minecraft = Minecraft.getInstance();
        float partialTicks = AnimationTickHolder.getPartialTicks();
        Player player = SimDistUtil.getClientPlayer();
        if (player != null && (context.firstPerson() || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)) {
            if (player.getMainHandItem() == stack || player.getOffhandItem() == stack) {
                openAmount = Mth.lerp((float)partialTicks, (float)clientHandler.previousExtension, (float)clientHandler.extension);
                cubeScale = Mth.lerp((float)partialTicks, (float)clientHandler.previousCubeScale, (float)clientHandler.cubeScale);
            } else {
                for (UUID playerUUID : clientHandler.beams.keySet()) {
                    Player otherPlayer = minecraft.level.getPlayerByUUID(playerUUID);
                    if (otherPlayer == null || otherPlayer.getMainHandItem() != stack && otherPlayer.getOffhandItem() != stack) continue;
                    openAmount = Mth.lerp((float)partialTicks, (float)((PhysicsStaffClientHandler.PhysicsBeam)clientHandler.beams.get((Object)playerUUID)).previousExtension, (float)((PhysicsStaffClientHandler.PhysicsBeam)clientHandler.beams.get((Object)playerUUID)).extension);
                    cubeScale = Mth.lerp((float)partialTicks, (float)((PhysicsStaffClientHandler.PhysicsBeam)clientHandler.beams.get((Object)playerUUID)).previousCubeScale, (float)((PhysicsStaffClientHandler.PhysicsBeam)clientHandler.beams.get((Object)playerUUID)).cubeScale);
                    break;
                }
            }
        }
        float tiltAmount = Mth.lerp((float)partialTicks, (float)clientHandler.previousTilt, (float)clientHandler.tilt);
        Quaternionf utilQuat = new Quaternionf();
        if (context.firstPerson()) {
            if (clientHandler.getDragSession() != null) {
                PhysicsStaffClientHandler.ClientDragSession dragSession = clientHandler.getDragSession();
                Quaternionf rotation = minecraft.gameRenderer.getMainCamera().rotation();
                Vector3d globalAnchor = ((ClientSubLevel)dragSession.dragSubLevel()).renderPose().transformPosition(new Vector3d(dragSession.dragLocalAnchor()));
                Vector3d dirToAnchor = globalAnchor.sub((Vector3dc)JOMLConversion.toJOML((Position)player.getEyePosition(partialTicks))).normalize();
                rotation.transformInverse(dirToAnchor);
                Quaternionf quat = SimMathUtils.getQuaternionfFromVectorRotation((Vector3dc)new Vector3d(0.0, 0.0, -1.0), (Vector3dc)dirToAnchor);
                ms.mulPose(utilQuat.identity().rotateY(-1.5707964f));
                ms.mulPose(quat.slerp((Quaternionfc)utilQuat.identity(), 0.6f));
                ms.mulPose(utilQuat.identity().rotateY(1.5707964f));
            }
            float tiltMultiplier = context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? -1.0f : 1.0f;
            ms.mulPose(utilQuat.identity().rotateZ((float)Math.toRadians(((double)tiltAmount * 0.5 + 0.5) * -61.0) * tiltMultiplier));
        }
        renderer.render(model.getOriginalModel(), light);
        renderer.renderSolidGlowing(SimPartialModels.PHYSICS_STAFF_CORE.get(), 0xF000F0);
        renderer.renderGlowing(SimPartialModels.PHYSICS_STAFF_CORE_GLOW.get(), 0xF000F0);
        float worldTime = AnimationTickHolder.getRenderTime() / 20.0f;
        ms.pushPose();
        ms.translate(0.0, 0.40625, 0.0);
        renderer.render(SimPartialModels.PHYSICS_STAFF_RING.get(), light);
        ms.popPose();
        ms.translate(0.0, 0.5625, 0.0);
        for (int i = 0; i < 2; ++i) {
            ms.pushPose();
            ms.mulPose(Axis.YP.rotationDegrees((float)(i * 180)));
            ms.translate(-0.1875, 0.0, 0.0);
            ms.mulPose(Axis.ZP.rotationDegrees(openAmount * 20.0f));
            renderer.render(SimPartialModels.PHYSICS_STAFF_SIGMA.get(), light);
            ms.popPose();
        }
        ms.translate(0.0, 0.375, 0.0);
        if (context.firstPerson()) {
            if (clientHandler.getDragSession() != null) {
                clientHandler.lastCubeOrientation.set((Quaterniondc)clientHandler.getDragSession().dragOrientation());
            }
            Matrix4f m = new Matrix4f((Matrix4fc)ms.last().pose());
            m.m30(0.0f).m31(0.0f).m32(0.0f);
            m.invert();
            m.rotate((Quaternionfc)clientHandler.lastCubeOrientation);
            ms.mulPose(m);
        }
        cubeScale = Mth.lerp((float)cubeScale, (float)-0.05f, (float)1.0f);
        cubeScale = Mth.clamp((float)cubeScale, (float)0.0f, (float)1.0f);
        ms.scale(cubeScale *= 0.8f, cubeScale, cubeScale);
        renderer.renderSolidGlowing(SimPartialModels.PHYSICS_STAFF_INNER_CUBE.get(), 0xF000F0);
        if (context.firstPerson()) {
            Vector3f focusPoint = new Vector3f();
            ms.last().pose().transformPosition(focusPoint);
            itemProjMat.set((Matrix4fc)RenderSystem.getProjectionMatrix());
            focusPos.set((double)focusPoint.x, (double)focusPoint.y, (double)focusPoint.z);
        }
        ms.scale(1.2f, 1.2f, 1.2f);
        renderer.renderGlowing(SimPartialModels.PHYSICS_STAFF_OUTER_CUBE.get(), 0xF000F0);
    }
}
