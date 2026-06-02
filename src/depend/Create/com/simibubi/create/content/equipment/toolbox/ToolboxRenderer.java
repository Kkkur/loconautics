/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.equipment.toolbox;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class ToolboxRenderer
extends SmartBlockEntityRenderer<ToolboxBlockEntity> {
    public ToolboxRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(ToolboxBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = blockEntity.getBlockState();
        Direction facing = ((Direction)blockState.getValue((Property)ToolboxBlock.FACING)).getOpposite();
        SuperByteBuffer lid = CachedBuffers.partial((PartialModel)AllPartialModels.TOOLBOX_LIDS.get(blockEntity.getColor()), (BlockState)blockState);
        SuperByteBuffer drawer = CachedBuffers.partial((PartialModel)AllPartialModels.TOOLBOX_DRAWER, (BlockState)blockState);
        float lidAngle = blockEntity.lid.getValue(partialTicks);
        float drawerOffset = blockEntity.drawers.getValue(partialTicks);
        VertexConsumer builder = buffer.getBuffer(RenderType.cutoutMipped());
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)lid.center()).rotateYDegrees(-facing.toYRot())).uncenter()).translate(0.0f, 0.375f, 0.75f)).rotateXDegrees(135.0f * lidAngle)).translate(0.0f, -0.375f, -0.75f)).light(light).renderInto(ms, builder);
        for (int offset : Iterate.zeroAndOne) {
            ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)drawer.center()).rotateYDegrees(-facing.toYRot())).uncenter()).translate(0.0f, (float)(offset * 1) / 8.0f, -drawerOffset * 0.175f * (float)(2 - offset))).light(light).renderInto(ms, builder);
        }
    }
}
