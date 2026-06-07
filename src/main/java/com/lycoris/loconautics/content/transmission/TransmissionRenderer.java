package com.lycoris.loconautics.content.transmission;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBuffers;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.createmod.catnip.render.CatnipClient;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Renderer for the Transmission block.
 *
 * Follows the SplitShaftRenderer pattern: two {@code SHAFT_HALF} partial models,
 * one per face on the block's axis, are rendered independently so they can spin
 * at different speeds (input side tracks its network, output side tracks generated speed).
 *
 * Using {@code partialFacing} per direction ensures each half points toward its own face,
 * so they meet in the middle without z-fighting.
 */
public class TransmissionRenderer extends KineticBlockEntityRenderer<TransmissionBlockEntity> {

    public TransmissionRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(TransmissionBlockEntity be, float partialTicks, PoseStack ms,
                              MultiBufferSource buffer, int light, int overlay) {

        BlockState state = be.getBlockState();
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);

        // Determine which direction is "front" (output) using AXIS.
        // We pick the positive-axis direction as the output face by convention;
        // the blockstate and model are axis-symmetric so it doesn't matter visually.
        Direction outputFace = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);

        float time = CatnipClient.getGameTime() + partialTicks;

        for (Direction dir : Direction.values()) {
            if (dir.getAxis() != axis) continue;

            boolean isOutput = (dir == outputFace);

            // Output side uses generated (target) speed; input side uses network speed.
            float rpm = isOutput ? be.getGeneratedSpeed() : be.getSpeed();

            // Angle in radians — same formula used by Create's own shaft renderers.
            float angle = time * rpm * 3.0f / 10.0f % 360.0f;
            angle += getRotationOffsetForPosition(be, be.getBlockPos(), axis);
            float angleRad = angle / 180.0f * (float) Math.PI;

            SuperByteBuffer shaft = CachedBuffers.partialFacing(
                    AllPartialModels.SHAFT_HALF, state, dir);
            kineticRotationTransform(shaft, be, axis, angleRad, light);
            shaft.renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }
    }

    @Override
    protected BlockState getRenderedBlockState(TransmissionBlockEntity be) {
        return shaft(be.getBlockState().getValue(BlockStateProperties.AXIS));
    }
}