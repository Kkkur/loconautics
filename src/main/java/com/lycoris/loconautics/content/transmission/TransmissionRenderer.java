package com.lycoris.loconautics.content.transmission;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class TransmissionRenderer extends KineticBlockEntityRenderer<TransmissionBlockEntity> {

    public TransmissionRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(TransmissionBlockEntity be, float partialTicks, PoseStack ms,
                              MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor) be.getLevel())) return;

        BlockState state = be.getBlockState();
        // FACING is a DirectionProperty — derive the rotation axis from the facing direction
        Direction facing = state.getValue(TransmissionBlock.FACING);
        Direction.Axis axis = facing.getAxis();

        float time = AnimationTickHolder.getRenderTime((LevelAccessor) be.getLevel());
        float offset = getRotationOffsetForPosition(be, be.getBlockPos(), axis);

        // Input shaft half — negative direction along axis, spins at the network speed
        float inputAngle = (time * be.getSpeed() * 3.0f / 10.0f + offset) % 360.0f / 180.0f * (float) Math.PI;
        Direction inputDir = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);
        SuperByteBuffer inputShaft = CachedBuffers.partialFacing(
                (PartialModel) AllPartialModels.SHAFT_HALF, state, inputDir);
        kineticRotationTransform(inputShaft, be, axis, inputAngle, light);
        inputShaft.renderInto(ms, buffer.getBuffer(RenderType.solid()));

        // Output shaft half — positive direction along axis, spins at generated (redstone-set) speed
        float outputAngle = (time * be.getGeneratedSpeed() * 3.0f / 10.0f + offset) % 360.0f / 180.0f * (float) Math.PI;
        Direction outputDir = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        SuperByteBuffer outputShaft = CachedBuffers.partialFacing(
                (PartialModel) AllPartialModels.SHAFT_HALF, state, outputDir);
        kineticRotationTransform(outputShaft, be, axis, outputAngle, light);
        outputShaft.renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

    @Override
    protected BlockState getRenderedBlockState(TransmissionBlockEntity be) {
        return shaft(be.getBlockState().getValue(TransmissionBlock.FACING).getAxis());
    }
}