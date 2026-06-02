/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.equipment.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.equipment.armor.BacktankBlock;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class BacktankRenderer
extends KineticBlockEntityRenderer<BacktankBlockEntity> {
    public BacktankRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(BacktankBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        BlockState blockState = be.getBlockState();
        SuperByteBuffer cogs = CachedBuffers.partial((PartialModel)BacktankRenderer.getCogsModel(blockState), (BlockState)blockState);
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)cogs.center()).rotateYDegrees(180.0f + AngleHelper.horizontalAngle((Direction)((Direction)blockState.getValue(BacktankBlock.HORIZONTAL_FACING))))).uncenter()).translate(0.0f, 0.40625f, 0.6875f)).rotate(AngleHelper.rad((double)(be.getSpeed() / 4.0f * AnimationTickHolder.getRenderTime((LevelAccessor)be.getLevel()) % 360.0f)), Direction.EAST)).translate(0.0f, -0.40625f, -0.6875f);
        cogs.light(light).renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

    @Override
    protected SuperByteBuffer getRotatedModel(BacktankBlockEntity be, BlockState state) {
        return CachedBuffers.partial((PartialModel)BacktankRenderer.getShaftModel(state), (BlockState)state);
    }

    public static PartialModel getCogsModel(BlockState state) {
        if (AllBlocks.NETHERITE_BACKTANK.has(state)) {
            return AllPartialModels.NETHERITE_BACKTANK_COGS;
        }
        return AllPartialModels.COPPER_BACKTANK_COGS;
    }

    public static PartialModel getShaftModel(BlockState state) {
        if (AllBlocks.NETHERITE_BACKTANK.has(state)) {
            return AllPartialModels.NETHERITE_BACKTANK_SHAFT;
        }
        return AllPartialModels.COPPER_BACKTANK_SHAFT;
    }
}
