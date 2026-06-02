/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.data.Iterate
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
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.kinetics.simpleRelays.encased;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class EncasedCogRenderer
extends KineticBlockEntityRenderer<SimpleKineticBlockEntity> {
    private boolean large;

    public static EncasedCogRenderer small(BlockEntityRendererProvider.Context context) {
        return new EncasedCogRenderer(context, false);
    }

    public static EncasedCogRenderer large(BlockEntityRendererProvider.Context context) {
        return new EncasedCogRenderer(context, true);
    }

    public EncasedCogRenderer(BlockEntityRendererProvider.Context context, boolean large) {
        super(context);
        this.large = large;
    }

    @Override
    protected void renderSafe(SimpleKineticBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        BlockState blockState = be.getBlockState();
        Block block = blockState.getBlock();
        if (!(block instanceof IRotate)) {
            return;
        }
        IRotate def = (IRotate)block;
        Direction.Axis axis = EncasedCogRenderer.getRotationAxisOf(be);
        BlockPos pos = be.getBlockPos();
        float angle = this.large ? BracketedKineticBlockEntityRenderer.getAngleForLargeCogShaft(be, axis) : EncasedCogRenderer.getAngleForBe(be, pos, axis);
        for (Direction d : Iterate.directionsInAxis((Direction.Axis)EncasedCogRenderer.getRotationAxisOf(be))) {
            if (!def.hasShaftTowards((LevelReader)be.getLevel(), be.getBlockPos(), blockState, d)) continue;
            SuperByteBuffer shaft = CachedBuffers.partialFacing((PartialModel)AllPartialModels.SHAFT_HALF, (BlockState)be.getBlockState(), (Direction)d);
            EncasedCogRenderer.kineticRotationTransform(shaft, be, axis, angle, light);
            shaft.renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }
    }

    @Override
    protected SuperByteBuffer getRotatedModel(SimpleKineticBlockEntity be, BlockState state) {
        return CachedBuffers.partialFacingVertical((PartialModel)(this.large ? AllPartialModels.SHAFTLESS_LARGE_COGWHEEL : AllPartialModels.SHAFTLESS_COGWHEEL), (BlockState)state, (Direction)Direction.fromAxisAndDirection((Direction.Axis)((Direction.Axis)state.getValue((Property)EncasedCogwheelBlock.AXIS)), (Direction.AxisDirection)Direction.AxisDirection.POSITIVE));
    }
}
