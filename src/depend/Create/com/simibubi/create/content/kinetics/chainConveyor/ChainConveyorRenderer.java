/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 */
package com.simibubi.create.content.kinetics.chainConveyor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.foundation.render.RenderTypes;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.List;
import java.util.Map;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class ChainConveyorRenderer
extends KineticBlockEntityRenderer<ChainConveyorBlockEntity> {
    public static final ResourceLocation CHAIN_LOCATION = ResourceLocation.withDefaultNamespace((String)"textures/block/chain.png");
    public static final int MIP_DISTANCE = 48;

    public ChainConveyorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(ChainConveyorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        BlockPos pos = be.getBlockPos();
        this.renderChains(be, ms, buffer, light, overlay);
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        CachedBuffers.partial((PartialModel)AllPartialModels.CHAIN_CONVEYOR_WHEEL, (BlockState)be.getBlockState()).light(light).overlay(overlay).renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
        for (ChainConveyorPackage chainConveyorPackage : be.loopingPackages) {
            this.renderBox(be, ms, buffer, overlay, pos, chainConveyorPackage, partialTicks);
        }
        for (Map.Entry entry : be.travellingPackages.entrySet()) {
            for (ChainConveyorPackage box : (List)entry.getValue()) {
                this.renderBox(be, ms, buffer, overlay, pos, box, partialTicks);
            }
        }
    }

    private void renderBox(ChainConveyorBlockEntity be, PoseStack ms, MultiBufferSource buffer, int overlay, BlockPos pos, ChainConveyorPackage box, float partialTicks) {
        if (box.worldPosition == null) {
            return;
        }
        if (box.item == null || box.item.isEmpty()) {
            return;
        }
        ChainConveyorPackage.ChainConveyorPackagePhysicsData physicsData = box.physicsData((LevelAccessor)be.getLevel());
        if (physicsData.prevPos == null) {
            return;
        }
        Vec3 position = physicsData.prevPos.lerp(physicsData.pos, (double)partialTicks);
        Vec3 targetPosition = physicsData.prevTargetPos.lerp(physicsData.targetPos, (double)partialTicks);
        float yaw = AngleHelper.angleLerp((double)partialTicks, (double)physicsData.prevYaw, (double)physicsData.yaw);
        Vec3 offset = new Vec3(targetPosition.x - (double)pos.getX(), targetPosition.y - (double)pos.getY(), targetPosition.z - (double)pos.getZ());
        BlockPos containingPos = BlockPos.containing((Position)position);
        Level level = be.getLevel();
        BlockState blockState = be.getBlockState();
        int light = LightTexture.pack((int)level.getBrightness(LightLayer.BLOCK, containingPos), (int)level.getBrightness(LightLayer.SKY, containingPos));
        if (physicsData.modelKey == null) {
            ResourceLocation key = BuiltInRegistries.ITEM.getKey((Object)box.item.getItem());
            if (key == BuiltInRegistries.ITEM.getDefaultKey()) {
                return;
            }
            physicsData.modelKey = key;
        }
        SuperByteBuffer rigBuffer = CachedBuffers.partial((PartialModel)AllPartialModels.PACKAGE_RIGGING.get(physicsData.modelKey), (BlockState)blockState);
        SuperByteBuffer boxBuffer = CachedBuffers.partial((PartialModel)AllPartialModels.PACKAGES.get(physicsData.modelKey), (BlockState)blockState);
        Vec3 dangleDiff = VecHelper.rotate((Vec3)targetPosition.add(0.0, 0.5, 0.0).subtract(position), (double)(-yaw), (Direction.Axis)Direction.Axis.Y);
        float zRot = Mth.wrapDegrees((float)((float)Mth.atan2((double)(-dangleDiff.x), (double)dangleDiff.y) * 57.295776f)) / 2.0f;
        float xRot = Mth.wrapDegrees((float)((float)Mth.atan2((double)dangleDiff.z, (double)dangleDiff.y) * 57.295776f)) / 2.0f;
        zRot = Mth.clamp((float)zRot, (float)-25.0f, (float)25.0f);
        xRot = Mth.clamp((float)xRot, (float)-25.0f, (float)25.0f);
        for (SuperByteBuffer buf : new SuperByteBuffer[]{rigBuffer, boxBuffer}) {
            buf.translate(offset);
            buf.translate(0.0f, 0.625f, 0.0f);
            buf.rotateYDegrees(yaw);
            buf.rotateZDegrees(zRot);
            buf.rotateXDegrees(xRot);
            if (physicsData.flipped && buf == rigBuffer) {
                buf.rotateYDegrees(180.0f);
            }
            buf.uncenter();
            buf.translate(0.0f, -PackageItem.getHookDistance(box.item) + 0.4375f, 0.0f);
            buf.light(light).overlay(overlay).renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
        }
    }

    private void renderChains(ChainConveyorBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        float time = AnimationTickHolder.getRenderTime((LevelAccessor)be.getLevel()) / (360.0f / Math.abs(be.getSpeed()));
        if ((time %= 1.0f) < 0.0f) {
            time += 1.0f;
        }
        float animation = time - 0.5f;
        for (BlockPos blockPos : be.connections) {
            ChainConveyorBlockEntity.ConnectionStats stats = be.connectionStats.get(blockPos);
            if (stats == null) continue;
            Vec3 diff = stats.end().subtract(stats.start());
            double yaw = 57.2957763671875 * Mth.atan2((double)diff.x, (double)diff.z);
            double pitch = 57.2957763671875 * Mth.atan2((double)diff.y, (double)diff.multiply(1.0, 0.0, 1.0).length());
            Level level = be.getLevel();
            BlockPos tilePos = be.getBlockPos();
            Vec3 startOffset = stats.start().subtract(Vec3.atCenterOf((Vec3i)tilePos));
            if (!VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
                SuperByteBuffer guard = CachedBuffers.partial((PartialModel)AllPartialModels.CHAIN_CONVEYOR_GUARD, (BlockState)be.getBlockState());
                guard.center();
                guard.rotateYDegrees((float)yaw);
                guard.uncenter();
                guard.light(light).overlay(overlay).renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
            }
            ms.pushPose();
            PoseTransformStack chain = TransformStack.of((PoseStack)ms);
            chain.center();
            chain.translate(startOffset);
            chain.rotateYDegrees((float)yaw);
            chain.rotateXDegrees(90.0f - (float)pitch);
            chain.rotateYDegrees(45.0f);
            chain.translate(0.0f, 0.5f, 0.0f);
            chain.uncenter();
            int light1 = LightTexture.pack((int)level.getBrightness(LightLayer.BLOCK, tilePos), (int)level.getBrightness(LightLayer.SKY, tilePos));
            int light2 = LightTexture.pack((int)level.getBrightness(LightLayer.BLOCK, tilePos.offset((Vec3i)blockPos)), (int)level.getBrightness(LightLayer.SKY, tilePos.offset((Vec3i)blockPos)));
            boolean far = Minecraft.getInstance().level == be.getLevel() && !Minecraft.getInstance().getBlockEntityRenderDispatcher().camera.getPosition().closerThan((Position)Vec3.atCenterOf((Vec3i)tilePos).add((double)((float)blockPos.getX() / 2.0f), (double)((float)blockPos.getY() / 2.0f), (double)((float)blockPos.getZ() / 2.0f)), 48.0);
            ChainConveyorRenderer.renderChain(ms, buffer, animation, stats.chainLength(), light1, light2, far);
            ms.popPose();
        }
    }

    public static void renderChain(PoseStack ms, MultiBufferSource buffer, float animation, float length, int light1, int light2, boolean far) {
        float radius = far ? 0.0625f : 0.09375f;
        float minV = far ? 0.0f : animation;
        float maxV = far ? 0.0625f : length + minV;
        float minU = far ? 0.1875f : 0.0f;
        float maxU = far ? 0.25f : 0.1875f;
        ms.pushPose();
        ms.translate(0.5, 0.0, 0.5);
        VertexConsumer vc = buffer.getBuffer(RenderTypes.chain(CHAIN_LOCATION));
        ChainConveyorRenderer.renderPart(ms, vc, length, 0.0f, radius, radius, 0.0f, -radius, 0.0f, 0.0f, -radius, minU, maxU, minV, maxV, light1, light2, far);
        ms.popPose();
    }

    private static void renderPart(PoseStack pPoseStack, VertexConsumer pConsumer, float pMaxY, float pX0, float pZ0, float pX1, float pZ1, float pX2, float pZ2, float pX3, float pZ3, float pMinU, float pMaxU, float pMinV, float pMaxV, int light1, int light2, boolean far) {
        PoseStack.Pose posestack$pose = pPoseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        float uO = far ? 0.0f : 0.1875f;
        ChainConveyorRenderer.renderQuad(matrix4f, posestack$pose, pConsumer, 0.0f, pMaxY, pX0, pZ0, pX3, pZ3, pMinU, pMaxU, pMinV, pMaxV, light1, light2);
        ChainConveyorRenderer.renderQuad(matrix4f, posestack$pose, pConsumer, 0.0f, pMaxY, pX3, pZ3, pX0, pZ0, pMinU, pMaxU, pMinV, pMaxV, light1, light2);
        ChainConveyorRenderer.renderQuad(matrix4f, posestack$pose, pConsumer, 0.0f, pMaxY, pX1, pZ1, pX2, pZ2, pMinU + uO, pMaxU + uO, pMinV, pMaxV, light1, light2);
        ChainConveyorRenderer.renderQuad(matrix4f, posestack$pose, pConsumer, 0.0f, pMaxY, pX2, pZ2, pX1, pZ1, pMinU + uO, pMaxU + uO, pMinV, pMaxV, light1, light2);
    }

    private static void renderQuad(Matrix4f pPose, PoseStack.Pose pNormal, VertexConsumer pConsumer, float pMinY, float pMaxY, float pMinX, float pMinZ, float pMaxX, float pMaxZ, float pMinU, float pMaxU, float pMinV, float pMaxV, int light1, int light2) {
        ChainConveyorRenderer.addVertex(pPose, pNormal, pConsumer, pMaxY, pMinX, pMinZ, pMaxU, pMinV, light2);
        ChainConveyorRenderer.addVertex(pPose, pNormal, pConsumer, pMinY, pMinX, pMinZ, pMaxU, pMaxV, light1);
        ChainConveyorRenderer.addVertex(pPose, pNormal, pConsumer, pMinY, pMaxX, pMaxZ, pMinU, pMaxV, light1);
        ChainConveyorRenderer.addVertex(pPose, pNormal, pConsumer, pMaxY, pMaxX, pMaxZ, pMinU, pMinV, light2);
    }

    private static void addVertex(Matrix4f pPose, PoseStack.Pose pNormal, VertexConsumer pConsumer, float pY, float pX, float pZ, float pU, float pV, int light) {
        pConsumer.addVertex(pPose, pX, pY, pZ).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(pU, pV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pNormal, 0.0f, 1.0f, 0.0f);
    }

    public int getViewDistance() {
        return 256;
    }

    public boolean shouldRenderOffScreen(ChainConveyorBlockEntity be) {
        return true;
    }

    @Override
    protected SuperByteBuffer getRotatedModel(ChainConveyorBlockEntity be, BlockState state) {
        return CachedBuffers.partial((PartialModel)AllPartialModels.CHAIN_CONVEYOR_SHAFT, (BlockState)state);
    }

    @Override
    protected RenderType getRenderType(ChainConveyorBlockEntity be, BlockState state) {
        return RenderType.cutoutMipped();
    }
}
