/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Position
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.contraptions.minecart;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.minecart.CouplingHandler;
import com.simibubi.create.content.contraptions.minecart.capability.MinecartController;
import com.simibubi.create.content.kinetics.KineticDebugger;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CouplingRenderer {
    public static void renderAll(PoseStack ms, MultiBufferSource buffer, Vec3 camera) {
        CouplingHandler.forEachLoadedCoupling((Level)Minecraft.getInstance().level, c -> {
            if (((MinecartController)c.getFirst()).hasContraptionCoupling(true)) {
                return;
            }
            CouplingRenderer.renderCoupling(ms, buffer, camera, (Couple<AbstractMinecart>)c.map(MinecartController::cart));
        });
    }

    public static void tickDebugModeRenders() {
        if (KineticDebugger.isActive()) {
            CouplingHandler.forEachLoadedCoupling((Level)Minecraft.getInstance().level, CouplingRenderer::doDebugRender);
        }
    }

    public static void renderCoupling(PoseStack ms, MultiBufferSource buffer, Vec3 camera, Couple<AbstractMinecart> carts) {
        ClientLevel world = Minecraft.getInstance().level;
        if (carts.getFirst() == null || carts.getSecond() == null) {
            return;
        }
        Couple lightValues = carts.map(c -> LevelRenderer.getLightColor((BlockAndTintGetter)world, (BlockPos)BlockPos.containing((Position)c.getBoundingBox().getCenter())));
        Vec3 center = ((AbstractMinecart)carts.getFirst()).position().add(((AbstractMinecart)carts.getSecond()).position()).scale(0.5);
        Couple transforms = carts.map(c -> CouplingRenderer.getSuitableCartEndpoint(c, center));
        BlockState renderState = Blocks.AIR.defaultBlockState();
        VertexConsumer builder = buffer.getBuffer(RenderType.solid());
        SuperByteBuffer attachment = CachedBuffers.partial((PartialModel)AllPartialModels.COUPLING_ATTACHMENT, (BlockState)renderState);
        SuperByteBuffer ring = CachedBuffers.partial((PartialModel)AllPartialModels.COUPLING_RING, (BlockState)renderState);
        SuperByteBuffer connector = CachedBuffers.partial((PartialModel)AllPartialModels.COUPLING_CONNECTOR, (BlockState)renderState);
        Vec3 zero = Vec3.ZERO;
        Vec3 firstEndpoint = ((CartEndpoint)transforms.getFirst()).apply(zero);
        Vec3 secondEndpoint = ((CartEndpoint)transforms.getSecond()).apply(zero);
        Vec3 endPointDiff = secondEndpoint.subtract(firstEndpoint);
        double connectorYaw = -Math.atan2(endPointDiff.z, endPointDiff.x) * 180.0 / Math.PI;
        double connectorPitch = Math.atan2(endPointDiff.y, endPointDiff.multiply(1.0, 0.0, 1.0).length()) * 180.0 / Math.PI;
        PoseTransformStack msr = TransformStack.of((PoseStack)ms);
        carts.forEachWithContext((cart, isFirst) -> {
            CartEndpoint cartTransform = (CartEndpoint)transforms.get(isFirst.booleanValue());
            ms.pushPose();
            cartTransform.apply(ms, camera);
            attachment.light(((Integer)lightValues.get(isFirst.booleanValue())).intValue()).renderInto(ms, builder);
            msr.rotateYDegrees((float)connectorYaw - cartTransform.yaw);
            ring.light(((Integer)lightValues.get(isFirst.booleanValue())).intValue()).renderInto(ms, builder);
            ms.popPose();
        });
        int l1 = (Integer)lightValues.getFirst();
        int l2 = (Integer)lightValues.getSecond();
        int meanBlockLight = ((l1 >> 4 & 0xF) + (l2 >> 4 & 0xF)) / 2;
        int meanSkyLight = ((l1 >> 20 & 0xF) + (l2 >> 20 & 0xF)) / 2;
        ms.pushPose();
        ((PoseTransformStack)((PoseTransformStack)msr.translate(firstEndpoint.subtract(camera))).rotateYDegrees((float)connectorYaw)).rotateZDegrees((float)connectorPitch);
        ms.scale((float)endPointDiff.length(), 1.0f, 1.0f);
        connector.light(meanSkyLight << 20 | meanBlockLight << 4).renderInto(ms, builder);
        ms.popPose();
    }

    private static CartEndpoint getSuitableCartEndpoint(AbstractMinecart cart, Vec3 centerOfCoupling) {
        boolean isBackFaceCloser;
        long i = (long)cart.getId() * 493286711L;
        i = i * i * 4392167121L + i * 98761L;
        double x = (((float)(i >> 16 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        double y = (((float)(i >> 20 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f + 0.375f;
        double z = (((float)(i >> 24 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        float pt = AnimationTickHolder.getPartialTicks();
        double xIn = Mth.lerp((double)pt, (double)cart.xOld, (double)cart.getX());
        double yIn = Mth.lerp((double)pt, (double)cart.yOld, (double)cart.getY());
        double zIn = Mth.lerp((double)pt, (double)cart.zOld, (double)cart.getZ());
        float yaw = Mth.lerp((float)pt, (float)cart.yRotO, (float)cart.getYRot());
        float pitch = Mth.lerp((float)pt, (float)cart.xRotO, (float)cart.getXRot());
        float roll = (float)cart.getHurtTime() - pt;
        float rollAmplifier = cart.getDamage() - pt;
        if (rollAmplifier < 0.0f) {
            rollAmplifier = 0.0f;
        }
        roll = roll > 0.0f ? Mth.sin((float)roll) * roll * rollAmplifier / 10.0f * (float)cart.getHurtDir() : 0.0f;
        Vec3 positionVec = new Vec3(xIn, yIn, zIn);
        Vec3 frontVec = positionVec.add(VecHelper.rotate((Vec3)new Vec3(0.5, 0.0, 0.0), (double)(180.0f - yaw), (Direction.Axis)Direction.Axis.Y));
        Vec3 backVec = positionVec.add(VecHelper.rotate((Vec3)new Vec3(-0.5, 0.0, 0.0), (double)(180.0f - yaw), (Direction.Axis)Direction.Axis.Y));
        Vec3 railVecOfPos = cart.getPos(xIn, yIn, zIn);
        boolean flip = false;
        if (railVecOfPos != null) {
            frontVec = cart.getPosOffs(xIn, yIn, zIn, (double)0.3f);
            backVec = cart.getPosOffs(xIn, yIn, zIn, (double)-0.3f);
            if (frontVec == null) {
                frontVec = railVecOfPos;
            }
            if (backVec == null) {
                backVec = railVecOfPos;
            }
            x += railVecOfPos.x;
            y += (frontVec.y + backVec.y) / 2.0;
            z += railVecOfPos.z;
            Vec3 endPointDiff = backVec.add(-frontVec.x, -frontVec.y, -frontVec.z);
            if (endPointDiff.length() != 0.0) {
                endPointDiff = endPointDiff.normalize();
                yaw = (float)(Math.atan2(endPointDiff.z, endPointDiff.x) * 180.0 / Math.PI);
                pitch = (float)(Math.atan(endPointDiff.y) * 73.0);
            }
        } else {
            x += xIn;
            y += yIn;
            z += zIn;
        }
        float offsetMagnitude = 0.8125f;
        flip = isBackFaceCloser = frontVec.distanceToSqr(centerOfCoupling) > backVec.distanceToSqr(centerOfCoupling);
        float offset = isBackFaceCloser ? -0.8125f : 0.8125f;
        return new CartEndpoint(x, y + 0.125, z, 180.0f - yaw, -pitch, roll, offset, flip);
    }

    public static void doDebugRender(Couple<MinecartController> c) {
        boolean yOffset = true;
        MinecartController first = (MinecartController)c.getFirst();
        AbstractMinecart mainCart = first.cart();
        Vec3 mainCenter = mainCart.position().add(0.0, (double)yOffset, 0.0);
        Vec3 connectedCenter = ((MinecartController)c.getSecond()).cart().position().add(0.0, (double)yOffset, 0.0);
        int color = Color.mixColors((int)11268329, (int)15631730, (float)((float)Mth.clamp((double)(Math.abs((double)first.getCouplingLength(true) - connectedCenter.distanceTo(mainCenter)) * 8.0), (double)0.0, (double)1.0)));
        Outliner.getInstance().showLine((Object)("" + mainCart.getId()), mainCenter, connectedCenter).colored(color).lineWidth(0.125f);
        Vec3 point = mainCart.position().add(0.0, (double)yOffset, 0.0);
        Outliner.getInstance().showLine((Object)(mainCart.getId() + "_dot"), point, point.add(0.0, 0.0078125, 0.0)).colored(0xFFFFFF).lineWidth(0.25f);
    }

    static class CartEndpoint {
        double x;
        double y;
        double z;
        float yaw;
        float pitch;
        float roll;
        float offset;
        boolean flip;

        public CartEndpoint(double x, double y, double z, float yaw, float pitch, float roll, float offset, boolean flip) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
            this.roll = roll;
            this.offset = offset;
            this.flip = flip;
        }

        public Vec3 apply(Vec3 vec) {
            vec = vec.add((double)this.offset, 0.0, 0.0);
            vec = VecHelper.rotate((Vec3)vec, (double)this.roll, (Direction.Axis)Direction.Axis.X);
            vec = VecHelper.rotate((Vec3)vec, (double)this.pitch, (Direction.Axis)Direction.Axis.Z);
            vec = VecHelper.rotate((Vec3)vec, (double)this.yaw, (Direction.Axis)Direction.Axis.Y);
            return vec.add(this.x, this.y, this.z);
        }

        public void apply(PoseStack ms, Vec3 camera) {
            ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)ms).translate(camera.scale(-1.0).add(this.x, this.y, this.z))).rotateYDegrees(this.yaw)).rotateZDegrees(this.pitch)).rotateXDegrees(this.roll)).translate(this.offset, 0.0f, 0.0f).rotateYDegrees(this.flip ? 180.0f : 0.0f);
        }
    }
}
