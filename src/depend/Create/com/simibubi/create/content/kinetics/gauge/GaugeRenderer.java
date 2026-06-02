/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.gauge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.content.kinetics.gauge.GaugeBlock;
import com.simibubi.create.content.kinetics.gauge.GaugeBlockEntity;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class GaugeRenderer
extends ShaftRenderer<GaugeBlockEntity> {
    protected GaugeBlock.Type type;

    public static GaugeRenderer speed(BlockEntityRendererProvider.Context context) {
        return new GaugeRenderer(context, GaugeBlock.Type.SPEED);
    }

    public static GaugeRenderer stress(BlockEntityRendererProvider.Context context) {
        return new GaugeRenderer(context, GaugeBlock.Type.STRESS);
    }

    protected GaugeRenderer(BlockEntityRendererProvider.Context context, GaugeBlock.Type type) {
        super(context);
        this.type = type;
    }

    @Override
    protected void renderSafe(GaugeBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        BlockState gaugeState = be.getBlockState();
        PartialModel partialModel = this.type == GaugeBlock.Type.SPEED ? AllPartialModels.GAUGE_HEAD_SPEED : AllPartialModels.GAUGE_HEAD_STRESS;
        SuperByteBuffer headBuffer = CachedBuffers.partial((PartialModel)partialModel, (BlockState)gaugeState);
        SuperByteBuffer dialBuffer = CachedBuffers.partial((PartialModel)AllPartialModels.GAUGE_DIAL, (BlockState)gaugeState);
        float dialPivot = 0.359375f;
        float progress = Mth.lerp((float)partialTicks, (float)be.prevDialState, (float)be.dialState);
        for (Direction facing : Iterate.directions) {
            if (!((GaugeBlock)gaugeState.getBlock()).shouldRenderHeadOnFace(be.getLevel(), be.getBlockPos(), gaugeState, facing)) continue;
            VertexConsumer vb = buffer.getBuffer(RenderType.solid());
            ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)this.rotateBufferTowards(dialBuffer, facing).translate(0.0f, dialPivot, dialPivot)).rotate((float)(1.5707963267948966 * (double)(-progress)), Direction.EAST)).translate(0.0f, -dialPivot, -dialPivot)).light(light).renderInto(ms, vb);
            this.rotateBufferTowards(headBuffer, facing).light(light).renderInto(ms, vb);
        }
    }

    protected SuperByteBuffer rotateBufferTowards(SuperByteBuffer buffer, Direction target) {
        return (SuperByteBuffer)buffer.rotateCentered((float)((double)((-target.toYRot() - 90.0f) / 180.0f) * Math.PI), Direction.UP);
    }
}
