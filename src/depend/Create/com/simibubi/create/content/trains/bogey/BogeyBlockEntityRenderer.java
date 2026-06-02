/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.trains.bogey;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BogeyBlockEntityRenderer<T extends AbstractBogeyBlockEntity>
extends SafeBlockEntityRenderer<T> {
    public BogeyBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(T be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = be.getBlockState();
        Block block = blockState.getBlock();
        if (!(block instanceof AbstractBogeyBlock)) {
            return;
        }
        AbstractBogeyBlock bogey = (AbstractBogeyBlock)block;
        float angle = ((AbstractBogeyBlockEntity)((Object)be)).getVirtualAngle(partialTicks);
        ms.pushPose();
        ms.translate(0.5f, 0.5f, 0.5f);
        if (blockState.getValue(AbstractBogeyBlock.AXIS) == Direction.Axis.X) {
            ms.mulPose(Axis.YP.rotationDegrees(90.0f));
        }
        ((AbstractBogeyBlockEntity)((Object)be)).getStyle().render(bogey.getSize(), partialTicks, ms, buffer, light, overlay, angle, ((AbstractBogeyBlockEntity)((Object)be)).getBogeyData(), false);
        ms.popPose();
    }
}
