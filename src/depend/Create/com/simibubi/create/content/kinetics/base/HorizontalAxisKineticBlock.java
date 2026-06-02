/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.kinetics.base;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class HorizontalAxisKineticBlock
extends KineticBlock {
    public static final Property<Direction.Axis> HORIZONTAL_AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    public HorizontalAxisKineticBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{HORIZONTAL_AXIS});
        super.createBlockStateDefinition(builder);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction.Axis preferredAxis = HorizontalAxisKineticBlock.getPreferredHorizontalAxis(context);
        if (preferredAxis != null) {
            return (BlockState)this.defaultBlockState().setValue(HORIZONTAL_AXIS, (Comparable)preferredAxis);
        }
        return (BlockState)this.defaultBlockState().setValue(HORIZONTAL_AXIS, (Comparable)context.getHorizontalDirection().getClockWise().getAxis());
    }

    public static Direction.Axis getPreferredHorizontalAxis(BlockPlaceContext context) {
        Direction prefferedSide = null;
        for (Direction side : Iterate.horizontalDirections) {
            BlockState blockState = context.getLevel().getBlockState(context.getClickedPos().relative(side));
            if (!(blockState.getBlock() instanceof IRotate) || !((IRotate)blockState.getBlock()).hasShaftTowards((LevelReader)context.getLevel(), context.getClickedPos().relative(side), blockState, side.getOpposite())) continue;
            if (prefferedSide != null && prefferedSide.getAxis() != side.getAxis()) {
                prefferedSide = null;
                break;
            }
            prefferedSide = side;
        }
        return prefferedSide == null ? null : prefferedSide.getAxis();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return (Direction.Axis)state.getValue(HORIZONTAL_AXIS);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(HORIZONTAL_AXIS);
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        Direction.Axis axis = (Direction.Axis)state.getValue(HORIZONTAL_AXIS);
        return (BlockState)state.setValue(HORIZONTAL_AXIS, (Comparable)rot.rotate(Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis)).getAxis());
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state;
    }
}
