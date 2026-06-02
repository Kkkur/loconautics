/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.kinetics.base;

import com.simibubi.create.api.contraption.transformable.TransformableBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class DirectionalAxisKineticBlock
extends DirectionalKineticBlock
implements TransformableBlock {
    public static final BooleanProperty AXIS_ALONG_FIRST_COORDINATE = BooleanProperty.create((String)"axis_along_first");

    public DirectionalAxisKineticBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AXIS_ALONG_FIRST_COORDINATE});
        super.createBlockStateDefinition(builder);
    }

    protected Direction getFacingForPlacement(BlockPlaceContext context) {
        Direction facing = context.getNearestLookingDirection().getOpposite();
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            facing = facing.getOpposite();
        }
        return facing;
    }

    protected boolean getAxisAlignmentForPlacement(BlockPlaceContext context) {
        return context.getHorizontalDirection().getAxis() == Direction.Axis.X;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = this.getFacingForPlacement(context);
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        boolean alongFirst = false;
        Direction.Axis faceAxis = facing.getAxis();
        if (faceAxis.isHorizontal()) {
            alongFirst = faceAxis == Direction.Axis.Z;
            Direction positivePerpendicular = faceAxis == Direction.Axis.X ? Direction.SOUTH : Direction.EAST;
            boolean shaftAbove = this.prefersConnectionTo((LevelReader)world, pos, Direction.UP, true);
            boolean shaftBelow = this.prefersConnectionTo((LevelReader)world, pos, Direction.DOWN, true);
            boolean preferLeft = this.prefersConnectionTo((LevelReader)world, pos, positivePerpendicular, false);
            boolean preferRight = this.prefersConnectionTo((LevelReader)world, pos, positivePerpendicular.getOpposite(), false);
            if (shaftAbove || shaftBelow || preferLeft || preferRight) {
                boolean bl = alongFirst = faceAxis == Direction.Axis.X;
            }
        }
        if (faceAxis.isVertical()) {
            alongFirst = this.getAxisAlignmentForPlacement(context);
            Direction prefferedSide = null;
            for (Direction side : Iterate.horizontalDirections) {
                if (!this.prefersConnectionTo((LevelReader)world, pos, side, true) && !this.prefersConnectionTo((LevelReader)world, pos, side.getClockWise(), false)) continue;
                if (prefferedSide != null && prefferedSide.getAxis() != side.getAxis()) {
                    prefferedSide = null;
                    break;
                }
                prefferedSide = side;
            }
            if (prefferedSide != null) {
                alongFirst = prefferedSide.getAxis() == Direction.Axis.X;
            }
        }
        return (BlockState)((BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)facing)).setValue((Property)AXIS_ALONG_FIRST_COORDINATE, (Comparable)Boolean.valueOf(alongFirst));
    }

    protected boolean prefersConnectionTo(LevelReader reader, BlockPos pos, Direction facing, boolean shaftAxis) {
        if (!shaftAxis) {
            return false;
        }
        BlockPos neighbourPos = pos.relative(facing);
        BlockState blockState = reader.getBlockState(neighbourPos);
        Block block = blockState.getBlock();
        return block instanceof IRotate && ((IRotate)block).hasShaftTowards(reader, neighbourPos, blockState, facing.getOpposite());
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        Direction.Axis pistonAxis = ((Direction)state.getValue((Property)FACING)).getAxis();
        boolean alongFirst = (Boolean)state.getValue((Property)AXIS_ALONG_FIRST_COORDINATE);
        if (pistonAxis == Direction.Axis.X) {
            return alongFirst ? Direction.Axis.Y : Direction.Axis.Z;
        }
        if (pistonAxis == Direction.Axis.Y) {
            return alongFirst ? Direction.Axis.X : Direction.Axis.Z;
        }
        if (pistonAxis == Direction.Axis.Z) {
            return alongFirst ? Direction.Axis.X : Direction.Axis.Y;
        }
        throw new IllegalStateException("Unknown axis??");
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        if (rot.ordinal() % 2 == 1) {
            state = (BlockState)state.cycle((Property)AXIS_ALONG_FIRST_COORDINATE);
        }
        return super.rotate(state, rot);
    }

    @Override
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

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == this.getRotationAxis(state);
    }
}
