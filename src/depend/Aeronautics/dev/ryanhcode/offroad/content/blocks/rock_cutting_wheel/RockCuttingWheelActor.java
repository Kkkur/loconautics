/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.behaviour.movement.MovementBehaviour
 *  com.simibubi.create.content.contraptions.behaviour.MovementContext
 *  com.simibubi.create.content.contraptions.render.ActorVisual
 *  com.simibubi.create.content.contraptions.render.ContraptionMatrices
 *  com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.ryanhcode.sable.Sable
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.offroad.content.blocks.rock_cutting_wheel;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadBearingBlockEntity;
import dev.ryanhcode.offroad.content.blocks.rock_cutting_wheel.RockCuttingWheelActorVisual;
import dev.ryanhcode.offroad.content.blocks.rock_cutting_wheel.RockCuttingWheelBlock;
import dev.ryanhcode.offroad.content.blocks.rock_cutting_wheel.RockCuttingWheelRenderer;
import dev.ryanhcode.offroad.content.entities.BoreheadContraptionEntity;
import dev.ryanhcode.sable.Sable;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RockCuttingWheelActor
implements MovementBehaviour {
    public static final double SEARCH_ORIGIN_OFFSET = 1.0;

    private static boolean reverseRotation(Direction.Axis bearingAxis, float bearingSpeed, Direction localDir, boolean axisAlongFirst, BlockPos localPos) {
        Direction.Axis rotationAxis;
        if (localDir.getAxis().isVertical()) {
            rotationAxis = axisAlongFirst ? Direction.Axis.Z : Direction.Axis.X;
        } else {
            boolean facingUp = axisAlongFirst == (localDir.getStepX() == 0);
            Direction.Axis axis = rotationAxis = facingUp ? Direction.Axis.Y : localDir.getClockWise().getAxis();
        }
        if (localDir.getAxis() == bearingAxis) {
            return switch (rotationAxis) {
                default -> throw new MatchException(null, null);
                case Direction.Axis.Z -> {
                    if (localPos.getZ() == 0) {
                        if (localDir.getAxis() == Direction.Axis.X) {
                            if (localPos.getY() > 0 == bearingSpeed > 0.0f) {
                                yield true;
                            }
                            yield false;
                        }
                        if (localPos.getX() > 0 != bearingSpeed > 0.0f) {
                            yield true;
                        }
                        yield false;
                    }
                    if (localPos.getZ() <= 0) {
                        yield true;
                    }
                    yield false;
                }
                case Direction.Axis.Y -> {
                    if (localPos.getY() == 0) {
                        if (localDir.getAxis() == Direction.Axis.Z) {
                            if (localDir.getStepZ() > 0 == localPos.getX() > 0 != bearingSpeed > 0.0f) {
                                yield true;
                            }
                            yield false;
                        }
                        if (localDir.getStepX() > 0 == localPos.getZ() > 0 == bearingSpeed > 0.0f) {
                            yield true;
                        }
                        yield false;
                    }
                    if (localPos.getY() > 0 != (localDir.getStepX() < 0 || localDir.getStepZ() < 0)) {
                        yield true;
                    }
                    yield false;
                }
                case Direction.Axis.X -> {
                    if (localPos.getX() == 0) {
                        if (localDir.getAxis() == Direction.Axis.Y) {
                            if (localDir.getStepY() > 0 == localPos.getZ() > 0 != bearingSpeed > 0.0f) {
                                yield true;
                            }
                            yield false;
                        }
                        if (localPos.getY() > 0 == bearingSpeed > 0.0f) {
                            yield true;
                        }
                        yield false;
                    }
                    yield localPos.getX() > 0 != (localDir == Direction.DOWN);
                }
            };
        }
        if (bearingAxis == rotationAxis) {
            return false;
        }
        return false;
    }

    public void startMoving(MovementContext context) {
    }

    public void tick(MovementContext context) {
        BlockPos controllerPos;
        BlockEntity be;
        if (context.world.isClientSide && context.temporaryData == null) {
            context.temporaryData = LerpedFloat.angular();
        }
        if ((be = context.world.getBlockEntity(controllerPos = ((BoreheadContraptionEntity)context.contraption.entity).getControllerPos())) instanceof BoreheadBearingBlockEntity) {
            boolean meetsSpeed;
            BoreheadBearingBlockEntity bhbe = (BoreheadBearingBlockEntity)be;
            boolean bl = meetsSpeed = (double)Math.abs(bhbe.getSpeed()) > 0.1;
            if (!context.world.isClientSide) {
                if (!context.data.contains("Initialized")) {
                    int newIndex = bhbe.requestNewIndexAndIncrement(context);
                    if (newIndex != -1) {
                        context.data.putInt("Index", newIndex);
                    }
                    context.data.putBoolean("Initialized", true);
                }
                if (meetsSpeed && !bhbe.isStalled()) {
                    BlockPos pos = context.localPos;
                    BlockState state = context.state;
                    Vec3 centerPos = pos.getCenter();
                    Vec3i facingNormal = ((Direction)state.getValue((Property)BlockStateProperties.FACING)).getNormal();
                    centerPos = centerPos.add((double)facingNormal.getX() * 1.0, (double)facingNormal.getY() * 1.0, (double)facingNormal.getZ() * 1.0);
                    Vec3 contraptionProjectedPos = context.contraption.entity.toGlobalVector(centerPos, 1.0f);
                    Vec3 sublevelProjected = Sable.HELPER.projectOutOfSubLevel(context.world, contraptionProjectedPos);
                    bhbe.updatePosition(context.data.getInt("Index"), sublevelProjected);
                }
            } else {
                LerpedFloat lerpingObject = (LerpedFloat)context.temporaryData;
                if (lerpingObject != null) {
                    float clientRotationSpeed = bhbe.getRotationSpeed() * 8.0f;
                    BlockState state = context.state;
                    boolean reversed = RockCuttingWheelActor.reverseRotation(((BoreheadContraptionEntity)context.contraption.entity).getRotationAxis(), ((BoreheadContraptionEntity)context.contraption.entity).getAngleDelta(), (Direction)state.getValue((Property)BlockStateProperties.FACING), (Boolean)state.getValue((Property)RockCuttingWheelBlock.AXIS_ALONG_FIRST_COORDINATE), context.localPos);
                    if (reversed) {
                        clientRotationSpeed *= -1.0f;
                    }
                    double rate = 0.3;
                    if (!bhbe.isStalled() && meetsSpeed && !bhbe.isSlowingDown()) {
                        lerpingObject.chase((double)(lerpingObject.getValue() + clientRotationSpeed), 0.3, LerpedFloat.Chaser.EXP);
                    }
                    lerpingObject.tickChaser();
                }
            }
        }
    }

    public boolean disableBlockEntityRendering() {
        return true;
    }

    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        if (renderWorld.supportsVisualization()) {
            return;
        }
        if (context.temporaryData == null) {
            context.temporaryData = LerpedFloat.angular();
        }
        RockCuttingWheelRenderer.renderInContraption(context, renderWorld, matrices, buffer);
    }

    @Nullable
    public ActorVisual createVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld, MovementContext context) {
        if (context.temporaryData == null) {
            context.temporaryData = LerpedFloat.angular();
        }
        return new RockCuttingWheelActorVisual(visualizationContext, (BlockAndTintGetter)simulationWorld, context);
    }

    public boolean isActive(MovementContext context) {
        return context.contraption.entity instanceof BoreheadContraptionEntity;
    }
}
