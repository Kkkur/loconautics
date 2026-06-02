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
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.redstone.analogLever;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlock;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.Property;

public class AnalogLeverRenderer
extends SafeBlockEntityRenderer<AnalogLeverBlockEntity> {
    public AnalogLeverRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(AnalogLeverBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        BlockState leverState = be.getBlockState();
        float state = be.clientState.getValue(partialTicks);
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        SuperByteBuffer handle = CachedBuffers.partial((PartialModel)AllPartialModels.ANALOG_LEVER_HANDLE, (BlockState)leverState);
        float angle = (float)((double)(state / 15.0f * 90.0f / 180.0f) * Math.PI);
        ((SuperByteBuffer)((SuperByteBuffer)this.transform(handle, leverState).translate(0.5f, 0.0625f, 0.5f)).rotate(angle, Direction.EAST)).translate(-0.5f, -0.0625f, -0.5f);
        handle.light(light).renderInto(ms, vb);
        int color = Color.mixColors((int)2884352, (int)0xCD0000, (float)(state / 15.0f));
        SuperByteBuffer indicator = this.transform(CachedBuffers.partial((PartialModel)AllPartialModels.ANALOG_LEVER_INDICATOR, (BlockState)leverState), leverState);
        indicator.light(light).color(color).renderInto(ms, vb);
    }

    private SuperByteBuffer transform(SuperByteBuffer buffer, BlockState leverState) {
        AttachFace face = (AttachFace)leverState.getValue((Property)AnalogLeverBlock.FACE);
        float rX = face == AttachFace.FLOOR ? 0.0f : (face == AttachFace.WALL ? 90.0f : 180.0f);
        float rY = AngleHelper.horizontalAngle((Direction)((Direction)leverState.getValue((Property)AnalogLeverBlock.FACING)));
        buffer.rotateCentered((float)((double)(rY / 180.0f) * Math.PI), Direction.UP);
        buffer.rotateCentered((float)((double)(rX / 180.0f) * Math.PI), Direction.EAST);
        return buffer;
    }
}
