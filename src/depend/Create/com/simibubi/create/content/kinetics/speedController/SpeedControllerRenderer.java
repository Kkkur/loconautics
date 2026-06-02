/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.Sheets
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.speedController;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlock;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class SpeedControllerRenderer
extends SmartBlockEntityRenderer<SpeedControllerBlockEntity> {
    public SpeedControllerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(SpeedControllerBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);
        VertexConsumer builder = buffer.getBuffer(Sheets.solidBlockSheet());
        if (!VisualizationManager.supportsVisualization((LevelAccessor)blockEntity.getLevel())) {
            KineticBlockEntityRenderer.renderRotatingBuffer(blockEntity, this.getRotatedModel(blockEntity), ms, builder, light);
        }
        if (!blockEntity.hasBracket) {
            return;
        }
        BlockPos pos = blockEntity.getBlockPos();
        Level world = blockEntity.getLevel();
        BlockState blockState = blockEntity.getBlockState();
        boolean alongX = blockState.getValue(SpeedControllerBlock.HORIZONTAL_AXIS) == Direction.Axis.X;
        SuperByteBuffer bracket = CachedBuffers.partial((PartialModel)AllPartialModels.SPEED_CONTROLLER_BRACKET, (BlockState)blockState);
        bracket.translate(0.0f, 1.0f, 0.0f);
        bracket.rotateCentered((float)(alongX ? Math.PI : 1.5707963267948966), Direction.UP);
        bracket.light(LevelRenderer.getLightColor((BlockAndTintGetter)world, (BlockPos)pos.above()));
        bracket.renderInto(ms, builder);
    }

    private SuperByteBuffer getRotatedModel(SpeedControllerBlockEntity blockEntity) {
        return CachedBuffers.block(KineticBlockEntityRenderer.KINETIC_BLOCK, (BlockState)KineticBlockEntityRenderer.shaft(KineticBlockEntityRenderer.getRotationAxisOf(blockEntity)));
    }
}
