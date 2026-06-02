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
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.crafter;

import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.crafter.CrafterHelper;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CrafterCTBehaviour
extends ConnectedTextureBehaviour.Base {
    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
        if (state.getBlock() != other.getBlock()) {
            return false;
        }
        if (state.getValue(HorizontalKineticBlock.HORIZONTAL_FACING) != other.getValue(HorizontalKineticBlock.HORIZONTAL_FACING)) {
            return false;
        }
        return CrafterHelper.areCraftersConnected(reader, pos, otherPos);
    }

    @Override
    protected boolean reverseUVs(BlockState state, Direction direction) {
        boolean isNegative;
        if (!direction.getAxis().isVertical()) {
            return false;
        }
        Direction facing = (Direction)state.getValue(HorizontalKineticBlock.HORIZONTAL_FACING);
        if (facing.getAxis() == direction.getAxis()) {
            return false;
        }
        boolean bl = isNegative = facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE;
        if (direction == Direction.DOWN && facing.getAxis() == Direction.Axis.Z) {
            return !isNegative;
        }
        return isNegative;
    }

    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        boolean facingX;
        Direction facing = (Direction)state.getValue(HorizontalKineticBlock.HORIZONTAL_FACING);
        boolean isFront = facing.getAxis() == direction.getAxis();
        boolean isVertical = direction.getAxis().isVertical();
        boolean bl = facingX = facing.getAxis() == Direction.Axis.X;
        return isFront ? AllSpriteShifts.BRASS_CASING : (isVertical && !facingX ? AllSpriteShifts.CRAFTER_OTHERSIDE : AllSpriteShifts.CRAFTER_SIDE);
    }
}
