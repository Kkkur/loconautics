/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.fluids.pipes.valve;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.fluids.pipes.valve.FluidValveBlock;
import com.simibubi.create.content.fluids.pipes.valve.FluidValveBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class FluidValveRenderer
extends KineticBlockEntityRenderer<FluidValveBlockEntity> {
    public FluidValveRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(FluidValveBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        BlockState blockState = be.getBlockState();
        SuperByteBuffer pointer = CachedBuffers.partial((PartialModel)AllPartialModels.FLUID_VALVE_POINTER, (BlockState)blockState);
        Direction facing = (Direction)blockState.getValue((Property)FluidValveBlock.FACING);
        float pointerRotation = Mth.lerp((float)be.pointer.getValue(partialTicks), (float)0.0f, (float)-90.0f);
        Direction.Axis pipeAxis = FluidValveBlock.getPipeAxis(blockState);
        Direction.Axis shaftAxis = FluidValveRenderer.getRotationAxisOf(be);
        int pointerRotationOffset = 0;
        if (pipeAxis.isHorizontal() && shaftAxis == Direction.Axis.X || pipeAxis.isVertical()) {
            pointerRotationOffset = 90;
        }
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)pointer.center()).rotateYDegrees(AngleHelper.horizontalAngle((Direction)facing))).rotateXDegrees(facing == Direction.UP ? 0.0f : (facing == Direction.DOWN ? 180.0f : 90.0f))).rotateYDegrees((float)pointerRotationOffset + pointerRotation)).uncenter()).light(light).renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

    @Override
    protected BlockState getRenderedBlockState(FluidValveBlockEntity be) {
        return FluidValveRenderer.shaft(FluidValveRenderer.getRotationAxisOf(be));
    }
}
