package com.lycoris.loconautics.content.bearingaxle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class BearingAxleRenderer extends KineticBlockEntityRenderer<BearingAxleBlockEntity> {

    public BearingAxleRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }
// test
    @Override
    protected void renderSafe(BearingAxleBlockEntity be, float partialTicks, PoseStack ms,
                              MultiBufferSource buffer, int light, int overlay) {
        Direction.Axis axis = getRotationAxisOf(be);
        BlockState shaftState = shaft(axis);
        SuperByteBuffer shaftBuffer = CachedBuffers.block(KINETIC_BLOCK, shaftState);
        renderRotatingBuffer(be, shaftBuffer, ms, buffer.getBuffer(RenderType.cutoutMipped()), LightTexture.FULL_BRIGHT);
    }
}