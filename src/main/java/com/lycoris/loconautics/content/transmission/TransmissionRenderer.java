package com.lycoris.loconautics.content.transmission;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class TransmissionRenderer extends KineticBlockEntityRenderer<TransmissionBlockEntity> {

    public TransmissionRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(TransmissionBlockEntity be, float partialTicks, PoseStack ms,
                              MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor) be.getLevel())) return;

        BlockState state = be.getBlockState();
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
        BlockPos pos = be.getBlockPos();

        // Input shaft — rendered the same way AnalogTransmissionRenderer does its shaft
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        KineticBlockEntityRenderer.renderRotatingKineticBlock(
                (KineticBlockEntity) be,
                shaft(axis),
                ms, vb, light);

        // Output shaft half — spins at generated speed
        float time = AnimationTickHolder.getRenderTime((LevelAccessor) be.getLevel());
        float offset = getRotationOffsetForPosition(be, pos, axis);
        float angle = (time * be.getGeneratedSpeed() * 3.0f / 10.0f + offset) % 360.0f / 180.0f * (float) Math.PI;

        Direction outputFace = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        SuperByteBuffer outputShaft = CachedBuffers.partialFacing(
                (PartialModel) AllPartialModels.SHAFT_HALF, state, outputFace);
        kineticRotationTransform(outputShaft, be, axis, angle, light);
        outputShaft.renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

    @Override
    protected BlockState getRenderedBlockState(TransmissionBlockEntity be) {
        return shaft(be.getBlockState().getValue(BlockStateProperties.AXIS));
    }
}