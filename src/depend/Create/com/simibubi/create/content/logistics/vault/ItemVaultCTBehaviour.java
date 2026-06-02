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
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.vault;

import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.logistics.vault.ItemVaultBlock;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ItemVaultCTBehaviour
extends ConnectedTextureBehaviour.Base {
    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        boolean small;
        Direction.Axis vaultBlockAxis = ItemVaultBlock.getVaultBlockAxis(state);
        boolean bl = small = !ItemVaultBlock.isLarge(state);
        if (vaultBlockAxis == null) {
            return null;
        }
        if (direction.getAxis() == vaultBlockAxis) {
            return (CTSpriteShiftEntry)((Object)AllSpriteShifts.VAULT_FRONT.get(small));
        }
        if (direction == Direction.UP) {
            return (CTSpriteShiftEntry)((Object)AllSpriteShifts.VAULT_TOP.get(small));
        }
        if (direction == Direction.DOWN) {
            return (CTSpriteShiftEntry)((Object)AllSpriteShifts.VAULT_BOTTOM.get(small));
        }
        return (CTSpriteShiftEntry)((Object)AllSpriteShifts.VAULT_SIDE.get(small));
    }

    @Override
    protected Direction getUpDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
        boolean alongX;
        Direction.Axis vaultBlockAxis = ItemVaultBlock.getVaultBlockAxis(state);
        boolean bl = alongX = vaultBlockAxis == Direction.Axis.X;
        if (face.getAxis().isVertical() && alongX) {
            return super.getUpDirection(reader, pos, state, face).getClockWise();
        }
        if (face.getAxis() == vaultBlockAxis || face.getAxis().isVertical()) {
            return super.getUpDirection(reader, pos, state, face);
        }
        return Direction.fromAxisAndDirection((Direction.Axis)vaultBlockAxis, (Direction.AxisDirection)(alongX ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE));
    }

    @Override
    protected Direction getRightDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
        Direction.Axis vaultBlockAxis = ItemVaultBlock.getVaultBlockAxis(state);
        if (face.getAxis().isVertical() && vaultBlockAxis == Direction.Axis.X) {
            return super.getRightDirection(reader, pos, state, face).getClockWise();
        }
        if (face.getAxis() == vaultBlockAxis || face.getAxis().isVertical()) {
            return super.getRightDirection(reader, pos, state, face);
        }
        return Direction.fromAxisAndDirection((Direction.Axis)Direction.Axis.Y, (Direction.AxisDirection)face.getAxisDirection());
    }

    @Override
    public boolean buildContextForOccludedDirections() {
        return super.buildContextForOccludedDirections();
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
        return state == other && ConnectivityHandler.isConnected((BlockGetter)reader, pos, otherPos);
    }
}
