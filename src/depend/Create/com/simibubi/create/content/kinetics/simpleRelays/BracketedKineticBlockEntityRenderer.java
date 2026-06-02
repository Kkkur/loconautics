/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.simpleRelays;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class BracketedKineticBlockEntityRenderer
extends KineticBlockEntityRenderer<BracketedKineticBlockEntity> {
    public BracketedKineticBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(BracketedKineticBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        if (!AllBlocks.LARGE_COGWHEEL.has(be.getBlockState())) {
            super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
            return;
        }
        VertexConsumer vc = buffer.getBuffer(RenderType.solid());
        Direction.Axis axis = BracketedKineticBlockEntityRenderer.getRotationAxisOf(be);
        Direction facing = Direction.fromAxisAndDirection((Direction.Axis)axis, (Direction.AxisDirection)Direction.AxisDirection.POSITIVE);
        BracketedKineticBlockEntityRenderer.renderRotatingBuffer(be, CachedBuffers.partialFacingVertical((PartialModel)AllPartialModels.SHAFTLESS_LARGE_COGWHEEL, (BlockState)be.getBlockState(), (Direction)facing), ms, vc, light);
        float angle = BracketedKineticBlockEntityRenderer.getAngleForLargeCogShaft(be, axis);
        SuperByteBuffer shaft = CachedBuffers.partialFacingVertical((PartialModel)AllPartialModels.COGWHEEL_SHAFT, (BlockState)be.getBlockState(), (Direction)facing);
        BracketedKineticBlockEntityRenderer.kineticRotationTransform(shaft, be, axis, angle, light);
        shaft.renderInto(ms, vc);
    }

    public static float getAngleForLargeCogShaft(SimpleKineticBlockEntity be, Direction.Axis axis) {
        BlockPos pos = be.getBlockPos();
        float offset = BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, pos);
        float time = AnimationTickHolder.getRenderTime((LevelAccessor)be.getLevel());
        float angle = (time * be.getSpeed() * 3.0f / 10.0f + offset) % 360.0f / 180.0f * (float)Math.PI;
        return angle;
    }

    public static float getShaftAngleOffset(Direction.Axis axis, BlockPos pos) {
        if (KineticBlockEntityVisual.shouldOffset(axis, (Vec3i)pos)) {
            return 22.5f;
        }
        return 0.0f;
    }
}
