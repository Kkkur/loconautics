/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.kinetics.gearbox;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlockEntity;
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
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class GearboxRenderer
extends KineticBlockEntityRenderer<GearboxBlockEntity> {
    public GearboxRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(GearboxBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        Direction.Axis boxAxis = (Direction.Axis)be.getBlockState().getValue((Property)BlockStateProperties.AXIS);
        BlockPos pos = be.getBlockPos();
        float time = AnimationTickHolder.getRenderTime((LevelAccessor)be.getLevel());
        for (Direction direction : Iterate.directions) {
            Direction.Axis axis = direction.getAxis();
            if (boxAxis == axis) continue;
            SuperByteBuffer shaft = CachedBuffers.partialFacing((PartialModel)AllPartialModels.SHAFT_HALF, (BlockState)be.getBlockState(), (Direction)direction);
            float offset = GearboxRenderer.getRotationOffsetForPosition(be, pos, axis);
            float angle = time * be.getSpeed() * 3.0f / 10.0f % 360.0f;
            if (be.getSpeed() != 0.0f && be.hasSource()) {
                BlockPos source = be.source.subtract((Vec3i)be.getBlockPos());
                Direction sourceFacing = Direction.getNearest((float)source.getX(), (float)source.getY(), (float)source.getZ());
                if (sourceFacing.getAxis() == direction.getAxis()) {
                    angle *= sourceFacing == direction ? 1.0f : -1.0f;
                } else if (sourceFacing.getAxisDirection() == direction.getAxisDirection()) {
                    angle *= -1.0f;
                }
            }
            angle += offset;
            angle = angle / 180.0f * (float)Math.PI;
            GearboxRenderer.kineticRotationTransform(shaft, be, axis, angle, light);
            shaft.renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }
    }
}
