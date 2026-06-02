/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.simibubi.create.foundation.blockEntity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class ColoredOverlayBlockEntityRenderer<T extends BlockEntity>
extends SafeBlockEntityRenderer<T> {
    public ColoredOverlayBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(T be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        SuperByteBuffer render = ColoredOverlayBlockEntityRenderer.render(this.getOverlayBuffer(be), this.getColor(be, partialTicks), light);
        render.renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

    protected abstract int getColor(T var1, float var2);

    protected abstract SuperByteBuffer getOverlayBuffer(T var1);

    public static SuperByteBuffer render(SuperByteBuffer buffer, int color, int light) {
        return buffer.color(color).light(light);
    }
}
