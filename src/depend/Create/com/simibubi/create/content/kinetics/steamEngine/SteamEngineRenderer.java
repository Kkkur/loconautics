/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
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
 */
package com.simibubi.create.content.kinetics.steamEngine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlockEntity;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlock;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
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

public class SteamEngineRenderer
extends SafeBlockEntityRenderer<SteamEngineBlockEntity> {
    public SteamEngineRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(SteamEngineBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        Float angle = be.getTargetAngle();
        if (angle == null) {
            return;
        }
        BlockState blockState = be.getBlockState();
        Direction facing = SteamEngineBlock.getFacing(blockState);
        Direction.Axis facingAxis = facing.getAxis();
        Direction.Axis axis = Direction.Axis.Y;
        PoweredShaftBlockEntity shaft = be.getShaft();
        if (shaft != null) {
            axis = KineticBlockEntityRenderer.getRotationAxisOf(shaft);
        }
        boolean roll90 = facingAxis.isHorizontal() && axis == Direction.Axis.Y || facingAxis.isVertical() && axis == Direction.Axis.Z;
        float piston = 0.375f * Mth.sin((float)angle.floatValue()) - Mth.sqrt((float)(Mth.square((float)0.875f) - Mth.square((float)0.375f) * Mth.square((float)Mth.cos((float)angle.floatValue()))));
        float distance = Mth.sqrt((float)Mth.square((float)(piston - 0.375f * Mth.sin((float)angle.floatValue()))));
        float angle2 = (float)Math.acos(distance / 0.875f) * (Mth.cos((float)angle.floatValue()) >= 0.0f ? 1.0f : -1.0f);
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        ((SuperByteBuffer)this.transformed(AllPartialModels.ENGINE_PISTON, blockState, facing, roll90).translate(0.0f, piston + 1.25f, 0.0f)).light(light).renderInto(ms, vb);
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)this.transformed(AllPartialModels.ENGINE_LINKAGE, blockState, facing, roll90).center()).translate(0.0f, 1.0f, 0.0f)).uncenter()).translate(0.0f, piston + 1.25f, 0.0f)).translate(0.0f, 0.25f, 0.5f)).rotateX(angle2)).translate(0.0f, -0.25f, -0.5f)).light(light).renderInto(ms, vb);
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)this.transformed(AllPartialModels.ENGINE_CONNECTOR, blockState, facing, roll90).translate(0.0f, 2.0f, 0.0f)).center()).rotateX(-(angle.floatValue() + 1.5707964f))).uncenter()).light(light).renderInto(ms, vb);
    }

    private SuperByteBuffer transformed(PartialModel model, BlockState blockState, Direction facing, boolean roll90) {
        return (SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial((PartialModel)model, (BlockState)blockState).center()).rotateYDegrees(AngleHelper.horizontalAngle((Direction)facing))).rotateXDegrees(AngleHelper.verticalAngle((Direction)facing) + 90.0f)).rotateYDegrees(roll90 ? -90.0f : 0.0f)).uncenter();
    }

    public int getViewDistance() {
        return 128;
    }
}
