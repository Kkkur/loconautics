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
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.RotatedPillarBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.chassis;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.contraptions.chassis.LinearChassisBlock;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public static class LinearChassisBlock.ChassisCTBehaviour
extends ConnectedTextureBehaviour.Base {
    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        Block block = state.getBlock();
        BooleanProperty glueableSide = ((LinearChassisBlock)block).getGlueableSide(state, direction);
        if (glueableSide == null) {
            return AllBlocks.LINEAR_CHASSIS.has(state) ? AllSpriteShifts.CHASSIS_SIDE : AllSpriteShifts.SECONDARY_CHASSIS_SIDE;
        }
        return (Boolean)state.getValue((Property)glueableSide) != false ? AllSpriteShifts.CHASSIS_STICKY : AllSpriteShifts.CHASSIS;
    }

    @Override
    protected Direction getUpDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
        Direction.Axis axis = (Direction.Axis)state.getValue((Property)RotatedPillarBlock.AXIS);
        if (face.getAxis() == axis) {
            return super.getUpDirection(reader, pos, state, face);
        }
        return Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis);
    }

    @Override
    protected Direction getRightDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
        Direction.Axis axis = (Direction.Axis)state.getValue((Property)RotatedPillarBlock.AXIS);
        return axis != face.getAxis() && axis.isHorizontal() ? (face.getAxis().isHorizontal() ? Direction.DOWN : (axis == Direction.Axis.X ? Direction.NORTH : Direction.EAST)) : super.getRightDirection(reader, pos, state, face);
    }

    @Override
    protected boolean reverseUVsHorizontally(BlockState state, Direction face) {
        boolean side;
        Direction.Axis axis = (Direction.Axis)state.getValue((Property)RotatedPillarBlock.AXIS);
        boolean bl = side = face.getAxis() != axis;
        if (side && axis == Direction.Axis.X && face.getAxis().isHorizontal()) {
            return true;
        }
        return super.reverseUVsHorizontally(state, face);
    }

    @Override
    protected boolean reverseUVsVertically(BlockState state, Direction face) {
        return super.reverseUVsVertically(state, face);
    }

    @Override
    public boolean reverseUVs(BlockState state, Direction face) {
        boolean end;
        Direction.Axis axis = (Direction.Axis)state.getValue((Property)RotatedPillarBlock.AXIS);
        boolean bl = end = face.getAxis() == axis;
        if (end && axis.isHorizontal() && face.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            return true;
        }
        if (!end && axis.isHorizontal() && face == Direction.DOWN) {
            return true;
        }
        return super.reverseUVs(state, face);
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
        Direction.Axis axis = (Direction.Axis)state.getValue((Property)RotatedPillarBlock.AXIS);
        boolean superConnect = face.getAxis() == axis ? super.connectsTo(state, other, reader, pos, otherPos, face) : LinearChassisBlock.sameKind(state, other);
        return superConnect && axis == other.getValue((Property)RotatedPillarBlock.AXIS);
    }
}
