/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.contraption.transformable.TransformableBlock
 *  com.simibubi.create.content.contraptions.StructureTransform
 *  com.simibubi.create.content.equipment.wrench.IWrenchable
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DirectionalBlock
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.util;

import com.simibubi.create.api.contraption.transformable.TransformableBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractDirectionalAxisBlock
extends DirectionalBlock
implements TransformableBlock,
IWrenchable {
    public static final BooleanProperty AXIS_ALONG_FIRST_COORDINATE = BooleanProperty.create((String)"axis_along_first");

    public AbstractDirectionalAxisBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(new Property[]{AXIS_ALONG_FIRST_COORDINATE, FACING});
        super.createBlockStateDefinition(pBuilder);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean shift = context.isSecondaryUseActive();
        Direction facing = this.getFacingForPlacement(context);
        boolean alongFirst = false;
        Direction.Axis faceAxis = facing.getAxis();
        if (faceAxis.isHorizontal()) {
            boolean bl = alongFirst = faceAxis == Direction.Axis.Z;
        }
        if (faceAxis.isVertical()) {
            alongFirst = this.getAxisAlignmentForPlacement(context);
        }
        return (BlockState)((BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)facing)).setValue((Property)AXIS_ALONG_FIRST_COORDINATE, (Comparable)Boolean.valueOf(shift != alongFirst));
    }

    protected boolean getAxisAlignmentForPlacement(BlockPlaceContext context) {
        return context.getHorizontalDirection().getAxis() == Direction.Axis.Z;
    }

    public static Direction.Axis getAxis(BlockState state) {
        if (state.getBlock() instanceof AbstractDirectionalAxisBlock) {
            Direction.Axis gatheredAxis;
            Direction facing = (Direction)state.getValue((Property)FACING);
            if (facing.getAxis().isVertical()) {
                gatheredAxis = (Boolean)state.getValue((Property)AXIS_ALONG_FIRST_COORDINATE) != false ? Direction.Axis.X : Direction.Axis.Z;
            } else {
                boolean facingUp = (Boolean)state.getValue((Property)AXIS_ALONG_FIRST_COORDINATE) != (facing.getStepX() == 0);
                gatheredAxis = facingUp ? Direction.Axis.Y : facing.getClockWise().getAxis();
            }
            return gatheredAxis;
        }
        return Direction.Axis.Y;
    }

    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        if (targetedFace == originalState.getValue((Property)FACING)) {
            return (BlockState)super.getRotatedBlockState(originalState, targetedFace).setValue((Property)AXIS_ALONG_FIRST_COORDINATE, (Comparable)Boolean.valueOf((Boolean)originalState.getValue((Property)AXIS_ALONG_FIRST_COORDINATE) == false));
        }
        return super.getRotatedBlockState(originalState, targetedFace);
    }

    @Nullable
    public static Direction getDirectionOfAxis(BlockState state) {
        if (state.getBlock() instanceof AbstractDirectionalAxisBlock) {
            Direction.Axis axis = AbstractDirectionalAxisBlock.getAxis(state);
            return Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis);
        }
        return null;
    }

    protected Direction getFacingForPlacement(BlockPlaceContext context) {
        return context.getClickedFace();
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        if (rot.ordinal() % 2 == 1) {
            state = (BlockState)state.cycle((Property)AXIS_ALONG_FIRST_COORDINATE);
        }
        return (BlockState)state.setValue((Property)FACING, (Comparable)rot.rotate((Direction)state.getValue((Property)FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation((Direction)state.getValue((Property)FACING)));
    }

    public BlockState transform(BlockState state, StructureTransform transform) {
        if (transform.mirror != null) {
            state = this.mirror(state, transform.mirror);
        }
        if (transform.rotationAxis == Direction.Axis.Y) {
            return this.rotate(state, transform.rotation);
        }
        Direction newFacing = transform.rotateFacing((Direction)state.getValue((Property)FACING));
        if (transform.rotationAxis == newFacing.getAxis() && transform.rotation.ordinal() % 2 == 1) {
            state = (BlockState)state.cycle((Property)AXIS_ALONG_FIRST_COORDINATE);
        }
        return (BlockState)state.setValue((Property)FACING, (Comparable)newFacing);
    }
}
