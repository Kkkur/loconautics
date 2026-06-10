package com.lycoris.loconautics.content.transmission;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import com.simibubi.create.content.kinetics.transmission.SplitShaftRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TransmissionRenderer extends SplitShaftRenderer {

    public TransmissionRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(SplitShaftBlockEntity be, float partialTicks, PoseStack ms,
                              MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor) be.getLevel())) {
            return;
        }

        Block block = be.getBlockState().getBlock();
        Direction.Axis boxAxis = ((IRotate) block).getRotationAxis(be.getBlockState());
        BlockPos pos = be.getBlockPos();
        float time = AnimationTickHolder.getRenderTime((LevelAccessor) be.getLevel());

        for (Direction direction : Iterate.directions) {
            if (boxAxis != direction.getAxis()) continue;

            float offset = KineticBlockEntityRenderer.getRotationOffsetForPosition(be, pos, boxAxis);
            float angle = time * be.getSpeed() * 3.0f / 10.0f % 360.0f;
            angle *= be.getRotationSpeedModifier(direction);
            angle += offset;
            angle = angle / 180.0f * (float) Math.PI;

            SuperByteBuffer superByteBuffer = CachedBuffers.partialFacing(
                    (PartialModel) AllPartialModels.SHAFT_HALF,
                    (BlockState) be.getBlockState(),
                    direction);

            // Apply rotation + flat lighting sampled at the block's own position.
            // light is the packed (skyLight << 16 | blockLight) value provided by the
            // BlockEntityRenderer contract; kineticRotationTransform writes it into every
            // vertex via SuperByteBuffer.light(), which is the same flat-lighting approach
            // used by SplitShaftRenderer and all other KineticBlockEntityRenderers.
            KineticBlockEntityRenderer.kineticRotationTransform(superByteBuffer, be, boxAxis, angle, light);
            superByteBuffer.renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }
    }
}