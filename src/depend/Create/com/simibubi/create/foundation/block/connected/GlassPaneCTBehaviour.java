/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.foundation.block.connected;

import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.SimpleCTBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class GlassPaneCTBehaviour
extends SimpleCTBehaviour {
    public GlassPaneCTBehaviour(CTSpriteShiftEntry shift) {
        super(shift);
    }

    @Override
    public boolean buildContextForOccludedDirections() {
        return true;
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
        return state.getBlock() == other.getBlock();
    }

    @Override
    protected boolean reverseUVsHorizontally(BlockState state, Direction face) {
        if (face.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
            return true;
        }
        return super.reverseUVsHorizontally(state, face);
    }
}
