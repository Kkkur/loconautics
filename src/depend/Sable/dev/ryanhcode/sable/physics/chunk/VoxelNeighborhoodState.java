/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.block.KelpBlock
 *  net.minecraft.world.level.block.KelpPlantBlock
 *  net.minecraft.world.level.block.piston.MovingPistonBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.LevelChunk
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.physics.chunk;

import dev.ryanhcode.sable.api.block.BlockWithSubLevelCollisionCallback;
import dev.ryanhcode.sable.util.LevelAccelerator;
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.KelpBlock;
import net.minecraft.world.level.block.KelpPlantBlock;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

public enum VoxelNeighborhoodState {
    EMPTY(0),
    FACE(9163292),
    EDGE(15330059),
    CORNER(15428619),
    INTERIOR(0);

    public static BiFunction<BlockGetter, BlockState, Boolean> IS_SOLID_MEMOIZED;
    public static BiFunction<BlockGetter, BlockState, Boolean> IS_FULL_BLOCK;
    private final int debugColor;

    private VoxelNeighborhoodState(int debugColor) {
        this.debugColor = debugColor;
    }

    public static boolean isSolid(BlockGetter blockGetter, BlockPos pos, BlockState state) {
        return IS_SOLID_MEMOIZED.apply(blockGetter, state);
    }

    public static boolean isFullBlock(BlockGetter blockGetter, BlockPos pos, BlockState state) {
        return IS_FULL_BLOCK.apply(blockGetter, state);
    }

    public static boolean isLiquid(BlockState state) {
        return state.liquid() || state.getBlock() instanceof KelpPlantBlock || state.getBlock() instanceof KelpBlock;
    }

    public static VoxelNeighborhoodState getState(LevelAccelerator level, BlockPos pos, @Nullable LevelChunk chunk) {
        BlockState state;
        ChunkPos initialPos = new ChunkPos(pos);
        BlockState blockState = state = chunk != null ? level.getBlockState(chunk, pos) : level.getBlockState(pos);
        if (VoxelNeighborhoodState.isLiquid(state) || BlockWithSubLevelCollisionCallback.hasCallback(state)) {
            return CORNER;
        }
        if (!VoxelNeighborhoodState.isSolid(level, pos, state)) {
            return EMPTY;
        }
        if (!VoxelNeighborhoodState.isFullBlock(level, pos, state)) {
            return CORNER;
        }
        boolean allSolid = true;
        boolean cornerSolid = true;
        int bothSidesCount = 0;
        for (Direction.Axis axis : Direction.Axis.VALUES) {
            boolean positiveSolid;
            BlockPos nPos = pos.relative(Direction.get((Direction.AxisDirection)Direction.AxisDirection.NEGATIVE, (Direction.Axis)axis));
            BlockPos pPos = pos.relative(Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis));
            BlockState nState = chunk != null && new ChunkPos(nPos).equals((Object)initialPos) ? level.getBlockState(chunk, nPos) : level.getBlockState(nPos);
            BlockState pState = chunk != null && new ChunkPos(pPos).equals((Object)initialPos) ? level.getBlockState(chunk, pPos) : level.getBlockState(pPos);
            boolean negativeSolid = VoxelNeighborhoodState.isSolid(level, nPos, nState) && VoxelNeighborhoodState.isFullBlock(level, nPos, nState);
            boolean bl = positiveSolid = VoxelNeighborhoodState.isSolid(level, pPos, pState) && VoxelNeighborhoodState.isFullBlock(level, pPos, pState);
            if (!negativeSolid || !positiveSolid) {
                allSolid = false;
            }
            if (!negativeSolid || !positiveSolid) continue;
            cornerSolid = false;
            ++bothSidesCount;
        }
        if (allSolid) {
            return INTERIOR;
        }
        if (bothSidesCount == 1) {
            return EDGE;
        }
        if (cornerSolid) {
            return CORNER;
        }
        return FACE;
    }

    public int getDebugColor() {
        return this.debugColor;
    }

    public byte byteRepresentation() {
        return (byte)this.ordinal();
    }

    static {
        IS_SOLID_MEMOIZED = new BiFunction<BlockGetter, BlockState, Boolean>(){
            private final Int2BooleanOpenHashMap cache = new Int2BooleanOpenHashMap();

            @Override
            public Boolean apply(BlockGetter blockGetter, BlockState state) {
                return this.cache.computeIfAbsent(state.hashCode(), x -> {
                    if (state.isAir()) {
                        return false;
                    }
                    if (state.getBlock() instanceof MovingPistonBlock) {
                        return true;
                    }
                    return !state.getCollisionShape(blockGetter, BlockPos.ZERO).isEmpty();
                });
            }
        };
        IS_FULL_BLOCK = new BiFunction<BlockGetter, BlockState, Boolean>(){
            private final Int2BooleanOpenHashMap cache = new Int2BooleanOpenHashMap();

            @Override
            public Boolean apply(BlockGetter blockGetter, BlockState state) {
                return this.cache.computeIfAbsent(state.hashCode(), x -> {
                    if (state.isAir()) {
                        return false;
                    }
                    return state.isCollisionShapeFullBlock(blockGetter, BlockPos.ZERO);
                });
            }
        };
    }
}
