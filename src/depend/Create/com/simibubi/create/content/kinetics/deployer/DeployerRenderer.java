/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.deployer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class DeployerRenderer
extends SafeBlockEntityRenderer<DeployerBlockEntity> {
    public DeployerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(DeployerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        this.renderItem(be, partialTicks, ms, buffer, light, overlay);
        FilteringRenderer.renderOnBlockEntity(be, partialTicks, ms, buffer, light, overlay);
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        this.renderComponents(be, partialTicks, ms, buffer, light, overlay);
    }

    protected void renderItem(DeployerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        boolean isBlockItem;
        if (be.heldItem.isEmpty()) {
            return;
        }
        BlockState deployerState = be.getBlockState();
        Vec3 offset = this.getHandOffset(be, partialTicks, deployerState).add(VecHelper.getCenterOf((Vec3i)BlockPos.ZERO));
        ms.pushPose();
        ms.translate(offset.x, offset.y, offset.z);
        Direction facing = (Direction)deployerState.getValue((Property)DirectionalKineticBlock.FACING);
        boolean punching = be.mode == DeployerBlockEntity.Mode.PUNCH;
        float yRot = AngleHelper.horizontalAngle((Direction)facing) + 180.0f;
        float xRot = facing == Direction.UP ? 90.0f : (facing == Direction.DOWN ? 270.0f : 0.0f);
        boolean displayMode = facing == Direction.UP && be.getSpeed() == 0.0f && !punching;
        ms.mulPose(Axis.YP.rotationDegrees(yRot));
        if (!displayMode) {
            ms.mulPose(Axis.XP.rotationDegrees(xRot));
            ms.translate(0.0f, 0.0f, -0.6875f);
        }
        if (punching) {
            ms.translate(0.0f, 0.125f, -0.0625f);
        }
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemDisplayContext transform = ItemDisplayContext.NONE;
        BakedModel bakedModel = itemRenderer.getModel(be.heldItem, be.getLevel(), null, 0);
        boolean bl = isBlockItem = be.heldItem.getItem() instanceof BlockItem && bakedModel.isGui3d();
        if (displayMode) {
            float scale = isBlockItem ? 1.25f : 1.0f;
            ms.translate(0.0f, isBlockItem ? 0.5625f : 0.6875f, 0.0f);
            ms.scale(scale, scale, scale);
            transform = ItemDisplayContext.GROUND;
            ms.mulPose(Axis.YP.rotationDegrees(AnimationTickHolder.getRenderTime((LevelAccessor)be.getLevel())));
        } else {
            float scale = punching ? 0.75f : (isBlockItem ? 0.734375f : 0.5f);
            ms.scale(scale, scale, scale);
            transform = punching ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND : ItemDisplayContext.FIXED;
        }
        itemRenderer.render(be.heldItem, transform, false, ms, buffer, light, overlay, bakedModel);
        ms.popPose();
    }

    protected void renderComponents(DeployerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        if (!VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            KineticBlockEntityRenderer.renderRotatingKineticBlock(be, this.getRenderedBlockState(be), ms, vb, light);
        }
        BlockState blockState = be.getBlockState();
        Vec3 offset = this.getHandOffset(be, partialTicks, blockState);
        SuperByteBuffer pole = CachedBuffers.partial((PartialModel)AllPartialModels.DEPLOYER_POLE, (BlockState)blockState);
        SuperByteBuffer hand = CachedBuffers.partial((PartialModel)be.getHandPose(), (BlockState)blockState);
        DeployerRenderer.transform((SuperByteBuffer)pole.translate(offset.x, offset.y, offset.z), blockState, true).light(light).renderInto(ms, vb);
        DeployerRenderer.transform((SuperByteBuffer)hand.translate(offset.x, offset.y, offset.z), blockState, false).light(light).renderInto(ms, vb);
    }

    protected Vec3 getHandOffset(DeployerBlockEntity be, float partialTicks, BlockState blockState) {
        float distance = be.getHandOffset(partialTicks);
        return Vec3.atLowerCornerOf((Vec3i)((Direction)blockState.getValue((Property)DirectionalKineticBlock.FACING)).getNormal()).scale((double)distance);
    }

    protected BlockState getRenderedBlockState(KineticBlockEntity be) {
        return KineticBlockEntityRenderer.shaft(KineticBlockEntityRenderer.getRotationAxisOf(be));
    }

    private static SuperByteBuffer transform(SuperByteBuffer buffer, BlockState deployerState, boolean axisDirectionMatters) {
        float xRot;
        Direction facing = (Direction)deployerState.getValue((Property)DirectionalKineticBlock.FACING);
        float yRot = AngleHelper.horizontalAngle((Direction)facing);
        float f = facing == Direction.UP ? 270.0f : (xRot = facing == Direction.DOWN ? 90.0f : 0.0f);
        float zRot = axisDirectionMatters && (Boolean)deployerState.getValue((Property)DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE) ^ facing.getAxis() == Direction.Axis.Z ? 90.0f : 0.0f;
        buffer.rotateCentered((float)((double)(yRot / 180.0f) * Math.PI), Direction.UP);
        buffer.rotateCentered((float)((double)(xRot / 180.0f) * Math.PI), Direction.EAST);
        buffer.rotateCentered((float)((double)(zRot / 180.0f) * Math.PI), Direction.SOUTH);
        return buffer;
    }

    public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        double factor;
        VertexConsumer builder = buffer.getBuffer(RenderType.solid());
        BlockState blockState = context.state;
        DeployerBlockEntity.Mode mode = (DeployerBlockEntity.Mode)NBTHelper.readEnum((CompoundTag)context.blockEntityData, (String)"Mode", DeployerBlockEntity.Mode.class);
        PartialModel handPose = DeployerRenderer.getHandPose(mode);
        float speed = context.getAnimationSpeed();
        if (context.contraption.stalled) {
            speed = 0.0f;
        }
        SuperByteBuffer shaft = CachedBuffers.block((BlockState)AllBlocks.SHAFT.getDefaultState());
        SuperByteBuffer pole = CachedBuffers.partial((PartialModel)AllPartialModels.DEPLOYER_POLE, (BlockState)blockState);
        SuperByteBuffer hand = CachedBuffers.partial((PartialModel)handPose, (BlockState)blockState);
        if (context.contraption.stalled || context.position == null || context.data.contains("StationaryTimer")) {
            factor = Mth.sin((float)(AnimationTickHolder.getRenderTime() * 0.5f)) * 0.25f + 0.25f;
        } else {
            Vec3 center = VecHelper.getCenterOf((Vec3i)BlockPos.containing((Position)context.position));
            double distance = context.position.distanceTo(center);
            double nextDistance = context.position.add(context.motion).distanceTo(center);
            factor = 0.5 - Mth.clamp((double)Mth.lerp((double)AnimationTickHolder.getPartialTicks(), (double)distance, (double)nextDistance), (double)0.0, (double)1.0);
        }
        Vec3 offset = Vec3.atLowerCornerOf((Vec3i)((Direction)blockState.getValue((Property)DirectionalKineticBlock.FACING)).getNormal()).scale(factor);
        PoseStack m = matrices.getModel();
        m.pushPose();
        m.pushPose();
        Direction.Axis axis = Direction.Axis.Y;
        Block block = context.state.getBlock();
        if (block instanceof IRotate) {
            IRotate def = (IRotate)block;
            axis = def.getRotationAxis(context.state);
        }
        float time = AnimationTickHolder.getRenderTime((LevelAccessor)context.world) / 20.0f;
        float angle = time * speed % 360.0f;
        ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)m).center()).rotateYDegrees(axis == Direction.Axis.Z ? 90.0f : 0.0f)).rotateZDegrees(axis.isHorizontal() ? 90.0f : 0.0f)).uncenter();
        shaft.transform(m);
        shaft.rotateCentered(angle, Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)Direction.Axis.Y));
        m.popPose();
        if (!context.disabled) {
            m.translate(offset.x, offset.y, offset.z);
        }
        pole.transform(m);
        hand.transform(m);
        DeployerRenderer.transform(pole, blockState, true);
        DeployerRenderer.transform(hand, blockState, false);
        shaft.light(LevelRenderer.getLightColor((BlockAndTintGetter)renderWorld, (BlockPos)context.localPos)).useLevelLight((BlockAndTintGetter)context.world, matrices.getWorld()).renderInto(matrices.getViewProjection(), builder);
        pole.light(LevelRenderer.getLightColor((BlockAndTintGetter)renderWorld, (BlockPos)context.localPos)).useLevelLight((BlockAndTintGetter)context.world, matrices.getWorld()).renderInto(matrices.getViewProjection(), builder);
        hand.light(LevelRenderer.getLightColor((BlockAndTintGetter)renderWorld, (BlockPos)context.localPos)).useLevelLight((BlockAndTintGetter)context.world, matrices.getWorld()).renderInto(matrices.getViewProjection(), builder);
        m.popPose();
    }

    static PartialModel getHandPose(DeployerBlockEntity.Mode mode) {
        return mode == DeployerBlockEntity.Mode.PUNCH ? AllPartialModels.DEPLOYER_HAND_PUNCHING : AllPartialModels.DEPLOYER_HAND_POINTING;
    }
}
