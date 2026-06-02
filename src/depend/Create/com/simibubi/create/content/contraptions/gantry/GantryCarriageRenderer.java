/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
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
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.contraptions.gantry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.gantry.GantryCarriageBlock;
import com.simibubi.create.content.contraptions.gantry.GantryCarriageBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
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
import net.minecraft.world.level.block.state.properties.Property;

public class GantryCarriageRenderer
extends KineticBlockEntityRenderer<GantryCarriageBlockEntity> {
    public GantryCarriageRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(GantryCarriageBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        BlockState state = be.getBlockState();
        Direction facing = (Direction)state.getValue((Property)GantryCarriageBlock.FACING);
        Boolean alongFirst = (Boolean)state.getValue((Property)GantryCarriageBlock.AXIS_ALONG_FIRST_COORDINATE);
        Direction.Axis rotationAxis = GantryCarriageRenderer.getRotationAxisOf(be);
        BlockPos visualPos = facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? be.getBlockPos() : be.getBlockPos().relative(facing.getOpposite());
        float angleForBE = GantryCarriageRenderer.getAngleForBE(be, visualPos, rotationAxis);
        Direction.Axis gantryAxis = Direction.Axis.X;
        for (Direction.Axis axis : Iterate.axes) {
            if (axis == rotationAxis || axis == facing.getAxis()) continue;
            gantryAxis = axis;
        }
        if (gantryAxis == Direction.Axis.X && facing == Direction.UP) {
            angleForBE *= -1.0f;
        }
        if (gantryAxis == Direction.Axis.Y && (facing == Direction.NORTH || facing == Direction.EAST)) {
            angleForBE *= -1.0f;
        }
        SuperByteBuffer cogs = CachedBuffers.partial((PartialModel)AllPartialModels.GANTRY_COGS, (BlockState)state);
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)cogs.center()).rotateYDegrees(AngleHelper.horizontalAngle((Direction)facing))).rotateXDegrees(facing == Direction.UP ? 0.0f : (facing == Direction.DOWN ? 180.0f : 90.0f))).rotateYDegrees(alongFirst ^ facing.getAxis() == Direction.Axis.X ? 0.0f : 90.0f)).translate(0.0f, -0.5625f, 0.0f)).rotateXDegrees(-angleForBE)).translate(0.0f, 0.5625f, 0.0f)).uncenter();
        cogs.light(light).renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

    public static float getAngleForBE(KineticBlockEntity be, BlockPos pos, Direction.Axis axis) {
        float time = AnimationTickHolder.getRenderTime((LevelAccessor)be.getLevel());
        float offset = GantryCarriageRenderer.getRotationOffsetForPosition(be, pos, axis);
        return (time * be.getSpeed() * 3.0f / 20.0f + offset) % 360.0f;
    }

    @Override
    protected BlockState getRenderedBlockState(GantryCarriageBlockEntity be) {
        return GantryCarriageRenderer.shaft(GantryCarriageRenderer.getRotationAxisOf(be));
    }
}
