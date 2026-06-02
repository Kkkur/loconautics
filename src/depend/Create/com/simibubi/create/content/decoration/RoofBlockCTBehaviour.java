/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.SlabBlock
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.StairBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Half
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.block.state.properties.SlabType
 *  net.minecraft.world.level.block.state.properties.StairsShape
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.decoration;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTType;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import org.jetbrains.annotations.Nullable;

public class RoofBlockCTBehaviour
extends ConnectedTextureBehaviour.Base {
    private CTSpriteShiftEntry shift;

    public RoofBlockCTBehaviour(CTSpriteShiftEntry shift) {
        this.shift = shift;
    }

    @Override
    @Nullable
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        if (direction == Direction.UP) {
            return this.shift;
        }
        return null;
    }

    @Override
    public boolean buildContextForOccludedDirections() {
        return true;
    }

    @Override
    public ConnectedTextureBehaviour.CTContext buildContext(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face, ConnectedTextureBehaviour.ContextRequirement requirement) {
        if (this.isUprightStair(state)) {
            return this.getStairMapping(state);
        }
        return super.buildContext(reader, pos, state, face, requirement);
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face, Direction primaryOffset, Direction secondaryOffset) {
        if (this.connects(reader, pos, state, other) || this.connectsHigh(reader, pos, state, other, reader.getBlockState(otherPos.above()))) {
            return true;
        }
        if (primaryOffset != null && secondaryOffset != null) {
            return false;
        }
        for (boolean p : Iterate.trueAndFalse) {
            Direction offset;
            Direction direction = offset = p ? primaryOffset : secondaryOffset;
            if (offset == null || offset.getAxis().isVertical() || !this.connectsHigh(reader, pos, state, reader.getBlockState(pos.relative(offset.getClockWise())), reader.getBlockState(pos.relative(offset.getClockWise()).above())) && !this.connectsHigh(reader, pos, state, reader.getBlockState(pos.relative(offset.getCounterClockWise())), reader.getBlockState(pos.relative(offset.getCounterClockWise()).above()))) continue;
            return true;
        }
        return false;
    }

    public boolean isUprightStair(BlockState state) {
        return state.hasProperty((Property)StairBlock.SHAPE) && state.getOptionalValue((Property)StairBlock.HALF).orElse(Half.TOP) == Half.BOTTOM;
    }

    public ConnectedTextureBehaviour.CTContext getStairMapping(BlockState state) {
        ConnectedTextureBehaviour.CTContext context = new ConnectedTextureBehaviour.CTContext();
        StairsShape shape = (StairsShape)state.getValue((Property)StairBlock.SHAPE);
        Direction facing = (Direction)state.getValue((Property)StairBlock.FACING);
        if (shape == StairsShape.OUTER_LEFT) {
            facing = facing.getCounterClockWise();
        }
        if (shape == StairsShape.INNER_LEFT) {
            facing = facing.getCounterClockWise();
        }
        int type = shape == StairsShape.STRAIGHT ? 0 : (shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? 1 : 2);
        int rot = facing.get2DDataValue();
        context.up = type >= 2;
        context.right = type % 2 == 1;
        context.left = rot >= 2;
        context.down = rot % 2 == 1;
        return context;
    }

    protected boolean connects(BlockAndTintGetter reader, BlockPos pos, BlockState state, BlockState other) {
        double top = state.getCollisionShape((BlockGetter)reader, pos).max(Direction.Axis.Y);
        double topOther = other.getSoundType() != SoundType.COPPER ? 0.0 : other.getCollisionShape((BlockGetter)reader, pos).max(Direction.Axis.Y);
        return Mth.equal((double)top, (double)topOther);
    }

    protected boolean connectsHigh(BlockAndTintGetter reader, BlockPos pos, BlockState state, BlockState other, BlockState aboveOther) {
        if (state.getBlock() instanceof SlabBlock && other.getBlock() instanceof SlabBlock && state.getValue((Property)SlabBlock.TYPE) == SlabType.BOTTOM && other.getValue((Property)SlabBlock.TYPE) != SlabType.BOTTOM) {
            return true;
        }
        if (state.getBlock() instanceof SlabBlock && state.getValue((Property)SlabBlock.TYPE) == SlabType.BOTTOM) {
            double topOther;
            double top = state.getCollisionShape((BlockGetter)reader, pos).max(Direction.Axis.Y);
            return !Mth.equal((double)top, (double)(topOther = other.getCollisionShape((BlockGetter)reader, pos).max(Direction.Axis.Y))) && topOther > top;
        }
        double topAboveOther = aboveOther.getCollisionShape((BlockGetter)reader, pos).max(Direction.Axis.Y);
        return topAboveOther > 0.0;
    }

    @Override
    @Nullable
    public CTType getDataType(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction direction) {
        return this.isUprightStair(state) ? AllCTTypes.ROOF_STAIR : AllCTTypes.ROOF;
    }
}
