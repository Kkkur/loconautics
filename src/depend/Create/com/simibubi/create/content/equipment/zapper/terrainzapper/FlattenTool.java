/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.simibubi.create.content.equipment.zapper.terrainzapper;

import com.simibubi.create.content.equipment.zapper.terrainzapper.TerrainTools;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

public class FlattenTool {
    static float[][] kernel = new float[][]{{0.003765f, 0.015019f, 0.023792f, 0.015019f, 0.003765f}, {0.015019f, 0.059912f, 0.094907f, 0.059912f, 0.015019f}, {0.023792f, 0.094907f, 0.150342f, 0.094907f, 0.023792f}, {0.015019f, 0.059912f, 0.094907f, 0.059912f, 0.015019f}, {0.003765f, 0.015019f, 0.023792f, 0.015019f, 0.003765f}};

    private static int[][] applyKernel(int[][] values) {
        int[][] result = new int[values.length][values[0].length];
        for (int i = 0; i < values.length; ++i) {
            for (int j = 0; j < values[i].length; ++j) {
                int value = values[i][j];
                float newValue = 0.0f;
                for (int iOffset = -2; iOffset <= 2; ++iOffset) {
                    for (int jOffset = -2; jOffset <= 2; ++jOffset) {
                        int iTarget = i + iOffset;
                        int jTarget = j + jOffset;
                        int ref = 0;
                        ref = iTarget < 0 || iTarget >= values.length || jTarget < 0 || jTarget >= values[0].length ? value : values[iTarget][jTarget];
                        if (ref == Integer.MIN_VALUE) {
                            ref = value;
                        }
                        newValue += kernel[iOffset + 2][jOffset + 2] * (float)ref;
                    }
                }
                result[i][j] = Mth.floor((float)(newValue + 0.5f));
            }
        }
        return result;
    }

    public static void apply(Level world, List<BlockPos> targetPositions, Direction facing) {
        ArrayList<BlockPos> surfaces = new ArrayList<BlockPos>();
        HashMap<Pair<Integer, Integer>, Integer> heightMap = new HashMap<Pair<Integer, Integer>, Integer>();
        int offset = facing.getAxisDirection().getStep();
        int minEntry = Integer.MAX_VALUE;
        int minCoord1 = Integer.MAX_VALUE;
        int minCoord2 = Integer.MAX_VALUE;
        int maxEntry = Integer.MIN_VALUE;
        int maxCoord1 = Integer.MIN_VALUE;
        int maxCoord2 = Integer.MIN_VALUE;
        for (BlockPos p : targetPositions) {
            Pair<Integer, Integer> coords = FlattenTool.getCoords(p, facing);
            BlockState belowSurface = world.getBlockState(p);
            minCoord1 = Math.min(minCoord1, (Integer)coords.getKey());
            minCoord2 = Math.min(minCoord2, (Integer)coords.getValue());
            maxCoord1 = Math.max(maxCoord1, (Integer)coords.getKey());
            maxCoord2 = Math.max(maxCoord2, (Integer)coords.getValue());
            if (TerrainTools.isReplaceable(belowSurface)) {
                if (heightMap.containsKey(coords)) continue;
                heightMap.put(coords, Integer.MIN_VALUE);
                continue;
            }
            BlockState surface = world.getBlockState(p = p.relative(facing));
            if (!TerrainTools.isReplaceable(surface)) {
                if (heightMap.containsKey(coords) && !((Integer)heightMap.get(coords)).equals(Integer.MIN_VALUE)) continue;
                heightMap.put(coords, Integer.MAX_VALUE);
                continue;
            }
            surfaces.add(p);
            int coordinate = facing.getAxis().choose(p.getX(), p.getY(), p.getZ());
            if (heightMap.containsKey(coords) && !((Integer)heightMap.get(coords)).equals(Integer.MAX_VALUE) && !((Integer)heightMap.get(coords)).equals(Integer.MIN_VALUE) && (Integer)heightMap.get(coords) * offset >= coordinate * offset) continue;
            heightMap.put(coords, coordinate);
            maxEntry = Math.max(maxEntry, coordinate);
            minEntry = Math.min(minEntry, coordinate);
        }
        if (surfaces.isEmpty()) {
            return;
        }
        int[][] heightMapArray = new int[maxCoord1 - minCoord1 + 1][maxCoord2 - minCoord2 + 1];
        for (int i = 0; i < heightMapArray.length; ++i) {
            for (int j = 0; j < heightMapArray[i].length; ++j) {
                Integer height;
                Pair pair = Pair.of((Object)(minCoord1 + i), (Object)(minCoord2 + j));
                heightMapArray[i][j] = !heightMap.containsKey(pair) ? Integer.MIN_VALUE : ((height = (Integer)heightMap.get(pair)).equals(Integer.MAX_VALUE) ? (offset == 1 ? maxEntry + 2 : minEntry - 2) : (height.equals(Integer.MIN_VALUE) ? (offset == 1 ? minEntry - 2 : maxEntry + 2) : height));
            }
        }
        heightMapArray = FlattenTool.applyKernel(heightMapArray);
        block3: for (BlockPos p : surfaces) {
            int targetCoord;
            Pair<Integer, Integer> coords = FlattenTool.getCoords(p, facing);
            int surfaceCoord = facing.getAxis().choose(p.getX(), p.getY(), p.getZ()) * offset;
            if (surfaceCoord == (targetCoord = heightMapArray[(Integer)coords.getKey() - minCoord1][(Integer)coords.getValue() - minCoord2] * offset)) continue;
            BlockState blockState = world.getBlockState(p);
            int timeOut = 1000;
            while (surfaceCoord > targetCoord) {
                BlockPos below = p.relative(facing.getOpposite());
                world.setBlockAndUpdate(below, blockState);
                world.setBlockAndUpdate(p, blockState.getFluidState().createLegacyBlock());
                p = p.relative(facing.getOpposite());
                --surfaceCoord;
                if (timeOut-- > 0) continue;
                break;
            }
            while (surfaceCoord < targetCoord) {
                BlockPos above = p.relative(facing);
                if (!(blockState.getBlock() instanceof LiquidBlock)) {
                    world.setBlockAndUpdate(above, blockState);
                }
                world.setBlockAndUpdate(p, world.getBlockState(p.relative(facing.getOpposite())));
                p = p.relative(facing);
                ++surfaceCoord;
                if (timeOut-- > 0) continue;
                continue block3;
            }
        }
    }

    private static Pair<Integer, Integer> getCoords(BlockPos pos, Direction facing) {
        switch (facing.getAxis()) {
            case X: {
                return Pair.of((Object)pos.getZ(), (Object)pos.getY());
            }
            case Y: {
                return Pair.of((Object)pos.getX(), (Object)pos.getZ());
            }
            case Z: {
                return Pair.of((Object)pos.getX(), (Object)pos.getY());
            }
        }
        return null;
    }
}
