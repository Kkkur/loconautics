/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.AllPartialModels
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.ryanhcode.offroad.content.blocks.borehead_bearing;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadBearingBlockEntity;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
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
import net.minecraft.world.level.block.state.properties.Property;

public class BoreheadBearingRenderer
extends KineticBlockEntityRenderer<BoreheadBearingBlockEntity> {
    public BoreheadBearingRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected void renderSafe(BoreheadBearingBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        BlockState state = be.getBlockState();
        float time = AnimationTickHolder.getRenderTime((LevelAccessor)be.getLevel());
        Direction.Axis rotationAxis = BoreheadBearingRenderer.getRotationAxisOf((KineticBlockEntity)be);
        for (Direction direction : Iterate.directionsInAxis((Direction.Axis)rotationAxis)) {
            SuperByteBuffer dirShaft = CachedBuffers.partialFacing((PartialModel)AllPartialModels.SHAFT_HALF, (BlockState)state, (Direction)direction);
            float offset = BoreheadBearingRenderer.getRotationOffsetForPosition((KineticBlockEntity)be, (BlockPos)be.getBlockPos(), (Direction.Axis)rotationAxis);
            float angle = 0.0f;
            if (be.getSpeed() != 0.0f) {
                angle = (float)direction.getAxisDirection().getStep() * (time * be.getSpeed() * 3.0f / 10.0f) % 360.0f;
            }
            angle += offset;
            angle = angle / 180.0f * (float)Math.PI;
            BoreheadBearingRenderer.kineticRotationTransform((SuperByteBuffer)dirShaft, (KineticBlockEntity)be, (Direction.Axis)rotationAxis, (float)angle, (int)light);
            dirShaft.renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }
        Direction facing = (Direction)state.getValue((Property)BlockStateProperties.FACING);
        SuperByteBuffer bearingTop = CachedBuffers.partial((PartialModel)AllPartialModels.BEARING_TOP, (BlockState)state);
        float interpolatedAngle = be.getInterpolatedAngle(partialTicks - 1.0f);
        BoreheadBearingRenderer.kineticRotationTransform((SuperByteBuffer)bearingTop, (KineticBlockEntity)be, (Direction.Axis)facing.getAxis(), (float)((float)((double)(interpolatedAngle / 180.0f) * Math.PI)), (int)light);
        if (facing.getAxis().isHorizontal()) {
            bearingTop.rotateCentered(AngleHelper.rad((double)AngleHelper.horizontalAngle((Direction)facing.getOpposite())), Direction.UP);
        }
        bearingTop.rotateCentered(AngleHelper.rad((double)(-90.0f - AngleHelper.verticalAngle((Direction)facing))), Direction.EAST);
        bearingTop.renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

    protected BlockState getRenderedBlockState(BoreheadBearingBlockEntity be) {
        return BoreheadBearingRenderer.shaft((Direction.Axis)BoreheadBearingRenderer.getRotationAxisOf((KineticBlockEntity)be));
    }
}
