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
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.logistics.funnel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.logistics.FlapStuffs;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class FunnelRenderer
extends SmartBlockEntityRenderer<FunnelBlockEntity> {
    public FunnelRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(FunnelBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        if (!be.hasFlap() || VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        BlockState blockState = be.getBlockState();
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        PartialModel partialModel = blockState.getBlock() instanceof FunnelBlock ? AllPartialModels.FUNNEL_FLAP : AllPartialModels.BELT_FUNNEL_FLAP;
        SuperByteBuffer flapBuffer = CachedBuffers.partial((PartialModel)partialModel, (BlockState)blockState);
        Direction funnelFacing = FunnelBlock.getFunnelFacing(blockState);
        float f = be.flap.getValue(partialTicks);
        FlapStuffs.renderFlaps(ms, vb, flapBuffer, FlapStuffs.FUNNEL_PIVOT, funnelFacing, f, -be.getFlapOffset(), light);
    }
}
