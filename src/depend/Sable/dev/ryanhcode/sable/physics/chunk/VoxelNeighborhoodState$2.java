/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.ryanhcode.sable.physics.chunk;

import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

class VoxelNeighborhoodState.2
implements BiFunction<BlockGetter, BlockState, Boolean> {
    private final Int2BooleanOpenHashMap cache = new Int2BooleanOpenHashMap();

    VoxelNeighborhoodState.2() {
    }

    @Override
    public Boolean apply(BlockGetter blockGetter, BlockState state) {
        return this.cache.computeIfAbsent(state.hashCode(), x -> {
            if (state.isAir()) {
                return false;
            }
            return state.isCollisionShapeFullBlock(blockGetter, BlockPos.ZERO);
        });
    }
}
