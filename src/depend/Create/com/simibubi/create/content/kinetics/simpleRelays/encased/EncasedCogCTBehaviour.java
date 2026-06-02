/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.simpleRelays.encased;

import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import net.createmod.catnip.data.Couple;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class EncasedCogCTBehaviour
extends EncasedCTBehaviour {
    private Couple<CTSpriteShiftEntry> sideShifts;
    private boolean large;

    public EncasedCogCTBehaviour(CTSpriteShiftEntry shift) {
        this(shift, null);
    }

    public EncasedCogCTBehaviour(CTSpriteShiftEntry shift, Couple<CTSpriteShiftEntry> sideShifts) {
        super(shift);
        this.large = sideShifts == null;
        this.sideShifts = sideShifts;
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
        Direction.Axis axis = (Direction.Axis)state.getValue(RotatedPillarKineticBlock.AXIS);
        if (this.large || axis == face.getAxis()) {
            return super.connectsTo(state, other, reader, pos, otherPos, face);
        }
        if (other.getBlock() == state.getBlock() && other.getValue(RotatedPillarKineticBlock.AXIS) == state.getValue(RotatedPillarKineticBlock.AXIS)) {
            return true;
        }
        BlockState blockState = reader.getBlockState(otherPos.relative(face));
        if (!ICogWheel.isLargeCog(blockState)) {
            return false;
        }
        return ((IRotate)blockState.getBlock()).getRotationAxis(blockState) == axis;
    }

    @Override
    protected boolean reverseUVs(BlockState state, Direction face) {
        return ((Direction.Axis)state.getValue(RotatedPillarKineticBlock.AXIS)).isHorizontal() && face.getAxis().isHorizontal() && face.getAxisDirection() == Direction.AxisDirection.POSITIVE;
    }

    @Override
    protected boolean reverseUVsVertically(BlockState state, Direction face) {
        if (!this.large && state.getValue(RotatedPillarKineticBlock.AXIS) == Direction.Axis.X && face.getAxis() == Direction.Axis.Z) {
            return face != Direction.SOUTH;
        }
        return super.reverseUVsVertically(state, face);
    }

    @Override
    protected boolean reverseUVsHorizontally(BlockState state, Direction face) {
        if (this.large) {
            return super.reverseUVsHorizontally(state, face);
        }
        if (((Direction.Axis)state.getValue(RotatedPillarKineticBlock.AXIS)).isVertical() && face.getAxis().isHorizontal()) {
            return true;
        }
        if (state.getValue(RotatedPillarKineticBlock.AXIS) == Direction.Axis.Z && face == Direction.DOWN) {
            return true;
        }
        return super.reverseUVsHorizontally(state, face);
    }

    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        Direction.Axis axis = (Direction.Axis)state.getValue(RotatedPillarKineticBlock.AXIS);
        if (this.large || axis == direction.getAxis()) {
            if (axis == direction.getAxis() && ((Boolean)state.getValue((Property)(direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? EncasedCogwheelBlock.TOP_SHAFT : EncasedCogwheelBlock.BOTTOM_SHAFT))).booleanValue()) {
                return null;
            }
            return super.getShift(state, direction, sprite);
        }
        return (CTSpriteShiftEntry)((Object)this.sideShifts.get(axis == Direction.Axis.X || axis == Direction.Axis.Z && direction.getAxis() == Direction.Axis.X));
    }
}
