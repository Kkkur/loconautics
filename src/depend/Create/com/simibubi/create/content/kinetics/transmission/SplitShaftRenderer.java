/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.transmission;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SplitShaftRenderer
extends KineticBlockEntityRenderer<SplitShaftBlockEntity> {
    public SplitShaftRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(SplitShaftBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        Block block = be.getBlockState().getBlock();
        Direction.Axis boxAxis = ((IRotate)block).getRotationAxis(be.getBlockState());
        BlockPos pos = be.getBlockPos();
        float time = AnimationTickHolder.getRenderTime((LevelAccessor)be.getLevel());
        for (Direction direction : Iterate.directions) {
            Direction.Axis axis = direction.getAxis();
            if (boxAxis != axis) continue;
            float offset = SplitShaftRenderer.getRotationOffsetForPosition(be, pos, axis);
            float angle = time * be.getSpeed() * 3.0f / 10.0f % 360.0f;
            float modifier = be.getRotationSpeedModifier(direction);
            angle *= modifier;
            angle += offset;
            angle = angle / 180.0f * (float)Math.PI;
            SuperByteBuffer superByteBuffer = CachedBuffers.partialFacing((PartialModel)AllPartialModels.SHAFT_HALF, (BlockState)be.getBlockState(), (Direction)direction);
            SplitShaftRenderer.kineticRotationTransform(superByteBuffer, be, axis, angle, light);
            superByteBuffer.renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }
    }
}
