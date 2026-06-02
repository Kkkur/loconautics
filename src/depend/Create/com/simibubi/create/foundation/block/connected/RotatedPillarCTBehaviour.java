/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.foundation.block.connected;

import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import com.simibubi.create.content.decoration.palettes.ConnectedPillarBlock;
import com.simibubi.create.content.decoration.palettes.LayeredBlock;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.HorizontalCTBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class RotatedPillarCTBehaviour
extends HorizontalCTBehaviour {
    public RotatedPillarCTBehaviour(CTSpriteShiftEntry layerShift, CTSpriteShiftEntry topShift) {
        super(layerShift, topShift);
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face, Direction primaryOffset, Direction secondaryOffset) {
        if (other.getBlock() != state.getBlock()) {
            return false;
        }
        Direction.Axis stateAxis = (Direction.Axis)state.getValue((Property)LayeredBlock.AXIS);
        if (other.getValue((Property)LayeredBlock.AXIS) != stateAxis) {
            return false;
        }
        if (this.isBeingBlocked(state, reader, pos, otherPos, face)) {
            return false;
        }
        if (reader.getBlockState(pos).getBlock() instanceof CopycatBlock) {
            return true;
        }
        if (reader.getBlockState(otherPos).getBlock() instanceof CopycatBlock) {
            return true;
        }
        if (primaryOffset != null && primaryOffset.getAxis() != stateAxis && !ConnectedPillarBlock.getConnection(state, primaryOffset)) {
            return false;
        }
        if (secondaryOffset != null && secondaryOffset.getAxis() != stateAxis) {
            if (!ConnectedPillarBlock.getConnection(state, secondaryOffset)) {
                return false;
            }
            if (!ConnectedPillarBlock.getConnection(other, secondaryOffset.getOpposite())) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean isBeingBlocked(BlockState state, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
        return state.getValue((Property)LayeredBlock.AXIS) == face.getAxis() && super.isBeingBlocked(state, reader, pos, otherPos, face);
    }

    @Override
    protected boolean reverseUVs(BlockState state, Direction face) {
        Direction.Axis axis = (Direction.Axis)state.getValue((Property)LayeredBlock.AXIS);
        if (axis == Direction.Axis.X) {
            return face.getAxisDirection() == Direction.AxisDirection.NEGATIVE && face.getAxis() != Direction.Axis.X;
        }
        if (axis == Direction.Axis.Z) {
            return face != Direction.NORTH && face.getAxisDirection() != Direction.AxisDirection.POSITIVE;
        }
        return super.reverseUVs(state, face);
    }

    @Override
    protected boolean reverseUVsHorizontally(BlockState state, Direction face) {
        return super.reverseUVsHorizontally(state, face);
    }

    @Override
    protected boolean reverseUVsVertically(BlockState state, Direction face) {
        Direction.Axis axis = (Direction.Axis)state.getValue((Property)LayeredBlock.AXIS);
        if (axis == Direction.Axis.X && face == Direction.NORTH) {
            return false;
        }
        if (axis == Direction.Axis.Z && face == Direction.WEST) {
            return false;
        }
        return super.reverseUVsVertically(state, face);
    }

    @Override
    protected Direction getUpDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
        boolean alongX;
        Direction.Axis axis = (Direction.Axis)state.getValue((Property)LayeredBlock.AXIS);
        if (axis == Direction.Axis.Y) {
            return super.getUpDirection(reader, pos, state, face);
        }
        boolean bl = alongX = axis == Direction.Axis.X;
        if (face.getAxis().isVertical() && alongX) {
            return super.getUpDirection(reader, pos, state, face).getClockWise();
        }
        if (face.getAxis() == axis || face.getAxis().isVertical()) {
            return super.getUpDirection(reader, pos, state, face);
        }
        return Direction.fromAxisAndDirection((Direction.Axis)axis, (Direction.AxisDirection)(alongX ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE));
    }

    @Override
    protected Direction getRightDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
        Direction.Axis axis = (Direction.Axis)state.getValue((Property)LayeredBlock.AXIS);
        if (axis == Direction.Axis.Y) {
            return super.getRightDirection(reader, pos, state, face);
        }
        if (face.getAxis().isVertical() && axis == Direction.Axis.X) {
            return super.getRightDirection(reader, pos, state, face).getClockWise();
        }
        if (face.getAxis() == axis || face.getAxis().isVertical()) {
            return super.getRightDirection(reader, pos, state, face);
        }
        return Direction.fromAxisAndDirection((Direction.Axis)Direction.Axis.Y, (Direction.AxisDirection)face.getAxisDirection());
    }

    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, TextureAtlasSprite sprite) {
        return super.getShift(state, direction.getAxis() == state.getValue((Property)LayeredBlock.AXIS) ? Direction.UP : Direction.SOUTH, sprite);
    }
}
