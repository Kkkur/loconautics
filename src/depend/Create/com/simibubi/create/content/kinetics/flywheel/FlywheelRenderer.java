/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.flywheel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.flywheel.FlywheelBlockEntity;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class FlywheelRenderer
extends KineticBlockEntityRenderer<FlywheelBlockEntity> {
    public FlywheelRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(FlywheelBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        BlockState blockState = be.getBlockState();
        float speed = be.visualSpeed.getValue(partialTicks) * 3.0f / 10.0f;
        float angle = be.angle + speed * partialTicks;
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        this.renderFlywheel(be, ms, light, blockState, angle, vb);
    }

    private void renderFlywheel(FlywheelBlockEntity be, PoseStack ms, int light, BlockState blockState, float angle, VertexConsumer vb) {
        SuperByteBuffer wheel = CachedBuffers.block((BlockState)blockState);
        FlywheelRenderer.kineticRotationTransform(wheel, be, FlywheelRenderer.getRotationAxisOf(be), AngleHelper.rad((double)angle), light);
        wheel.renderInto(ms, vb);
    }

    @Override
    protected BlockState getRenderedBlockState(FlywheelBlockEntity be) {
        return FlywheelRenderer.shaft(FlywheelRenderer.getRotationAxisOf(be));
    }
}
