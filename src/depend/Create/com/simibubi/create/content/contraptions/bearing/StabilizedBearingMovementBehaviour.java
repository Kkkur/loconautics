/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Axis
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package com.simibubi.create.content.contraptions.bearing;

import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.BearingVisual;
import com.simibubi.create.content.contraptions.bearing.StabilizedBearingVisual;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class StabilizedBearingMovementBehaviour
implements MovementBehaviour {
    @Override
    public ItemStack canBeDisabledVia(MovementContext context) {
        return null;
    }

    @Override
    public boolean disableBlockEntityRendering() {
        return true;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)context.world)) {
            return;
        }
        Direction facing = (Direction)context.state.getValue((Property)BlockStateProperties.FACING);
        PartialModel top = AllPartialModels.BEARING_TOP;
        SuperByteBuffer superBuffer = CachedBuffers.partial((PartialModel)top, (BlockState)context.state);
        float renderPartialTicks = AnimationTickHolder.getPartialTicks();
        Quaternionf orientation = BearingVisual.getBlockStateOrientation(facing);
        float angle = StabilizedBearingMovementBehaviour.getCounterRotationAngle(context, facing, renderPartialTicks) * (float)facing.getAxisDirection().getStep();
        Quaternionf rotation = Axis.of((Vector3f)facing.step()).rotationDegrees(angle);
        rotation.mul((Quaternionfc)orientation);
        orientation = rotation;
        superBuffer.transform(matrices.getModel());
        superBuffer.rotateCentered((Quaternionfc)orientation);
        superBuffer.light(LevelRenderer.getLightColor((BlockAndTintGetter)renderWorld, (BlockPos)context.localPos)).useLevelLight((BlockAndTintGetter)context.world, matrices.getWorld()).renderInto(matrices.getViewProjection(), buffer.getBuffer(RenderType.solid()));
    }

    @Override
    @Nullable
    public ActorVisual createVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld, MovementContext movementContext) {
        return new StabilizedBearingVisual(visualizationContext, simulationWorld, movementContext);
    }

    static float getCounterRotationAngle(MovementContext context, Direction facing, float renderPartialTicks) {
        if (!context.contraption.canBeStabilized(facing, context.localPos)) {
            return 0.0f;
        }
        float offset = 0.0f;
        Direction.Axis axis = facing.getAxis();
        AbstractContraptionEntity entity = context.contraption.entity;
        if (entity instanceof ControlledContraptionEntity) {
            ControlledContraptionEntity controlledCE = (ControlledContraptionEntity)entity;
            if (context.contraption.canBeStabilized(facing, context.localPos)) {
                offset = -controlledCE.getAngle(renderPartialTicks);
            }
        } else if (entity instanceof OrientedContraptionEntity) {
            OrientedContraptionEntity orientedCE = (OrientedContraptionEntity)entity;
            if (axis.isVertical()) {
                offset = -orientedCE.getViewYRot(renderPartialTicks);
            } else if (orientedCE.isInitialOrientationPresent() && orientedCE.getInitialOrientation().getAxis() == axis) {
                offset = -orientedCE.getViewXRot(renderPartialTicks);
            }
        }
        return offset;
    }
}
