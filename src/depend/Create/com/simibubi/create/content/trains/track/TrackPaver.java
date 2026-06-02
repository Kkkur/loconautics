/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.SlabBlock
 *  net.minecraft.world.level.block.WallBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.block.state.properties.SlabType
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.girder.GirderBlock;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.Vec3;

public class TrackPaver {
    public static int paveStraight(Level level, BlockPos startPos, Vec3 direction, int extent, Block block, boolean simulate, Set<BlockPos> visited) {
        int itemsNeeded = 0;
        BlockState defaultBlockState = block.defaultBlockState();
        boolean slabLike = defaultBlockState.hasProperty((Property)SlabBlock.TYPE);
        boolean wallLike = TrackPaver.isWallLike(defaultBlockState);
        if (slabLike) {
            defaultBlockState = (BlockState)defaultBlockState.setValue((Property)SlabBlock.TYPE, (Comparable)SlabType.DOUBLE);
        }
        if (defaultBlockState.getBlock() instanceof GirderBlock) {
            for (Direction d : Iterate.horizontalDirections) {
                if (!Vec3.atLowerCornerOf((Vec3i)d.getNormal()).equals((Object)direction)) continue;
                defaultBlockState = (BlockState)((BlockState)((BlockState)((BlockState)defaultBlockState.setValue((Property)GirderBlock.TOP, (Comparable)Boolean.valueOf(false))).setValue((Property)GirderBlock.BOTTOM, (Comparable)Boolean.valueOf(false))).setValue(GirderBlock.AXIS, (Comparable)d.getAxis())).setValue((Property)(d.getAxis() == Direction.Axis.X ? GirderBlock.X : GirderBlock.Z), (Comparable)Boolean.valueOf(true));
            }
        }
        HashSet<BlockPos> toPlaceOn = new HashSet<BlockPos>();
        Vec3 start = VecHelper.getCenterOf((Vec3i)startPos);
        Vec3 mainNormal = direction.cross(new Vec3(0.0, 1.0, 0.0));
        Vec3 normalizedNormal = mainNormal.normalize();
        Vec3 normalizedDirection = direction.normalize();
        float diagFiller = 0.45f;
        for (int i = 0; i < extent; ++i) {
            Vec3 offset = direction.scale((double)i);
            Vec3 mainPos = start.add(offset.x, offset.y, offset.z);
            toPlaceOn.add(BlockPos.containing((Position)mainPos.add(mainNormal)));
            toPlaceOn.add(BlockPos.containing((Position)mainPos.subtract(mainNormal)));
            if (wallLike) continue;
            toPlaceOn.add(BlockPos.containing((Position)mainPos));
            if (i < extent - 1) {
                for (int x : Iterate.positiveAndNegative) {
                    toPlaceOn.add(BlockPos.containing((Position)mainPos.add(normalizedNormal.scale((double)((float)x * diagFiller))).add(normalizedDirection.scale((double)diagFiller))));
                }
            }
            if (i <= 0) continue;
            for (int x : Iterate.positiveAndNegative) {
                toPlaceOn.add(BlockPos.containing((Position)mainPos.add(normalizedNormal.scale((double)((float)x * diagFiller))).add(normalizedDirection.scale((double)(-diagFiller)))));
            }
        }
        BlockState state = defaultBlockState;
        for (BlockPos p : toPlaceOn) {
            if (!visited.add(p) || !TrackPaver.placeBlockIfFree(level, p, state, simulate)) continue;
            itemsNeeded += slabLike ? 2 : 1;
        }
        visited.addAll(toPlaceOn);
        return itemsNeeded;
    }

    public static int paveCurve(Level level, BezierConnection bc, Block block, boolean simulate, Set<BlockPos> visited) {
        int itemsNeeded = 0;
        BlockState defaultBlockState = block.defaultBlockState();
        boolean slabLike = defaultBlockState.hasProperty((Property)SlabBlock.TYPE);
        if (slabLike) {
            defaultBlockState = (BlockState)defaultBlockState.setValue((Property)SlabBlock.TYPE, (Comparable)SlabType.DOUBLE);
        }
        if (TrackPaver.isWallLike(defaultBlockState)) {
            if (AllBlocks.METAL_GIRDER.has(defaultBlockState)) {
                return (bc.getSegmentCount() + 1) / 2 * 2;
            }
            return 0;
        }
        HashMap<Pair, Double> yLevels = new HashMap<Pair, Double>();
        BlockPos tePosition = (BlockPos)bc.bePositions.getFirst();
        Vec3 end1 = ((Vec3)bc.starts.getFirst()).subtract(Vec3.atLowerCornerOf((Vec3i)tePosition)).add(0.0, 0.1875, 0.0);
        Vec3 end2 = ((Vec3)bc.starts.getSecond()).subtract(Vec3.atLowerCornerOf((Vec3i)tePosition)).add(0.0, 0.1875, 0.0);
        Vec3 axis1 = (Vec3)bc.axes.getFirst();
        Vec3 axis2 = (Vec3)bc.axes.getSecond();
        double handleLength = bc.getHandleLength();
        Vec3 finish1 = axis1.scale(handleLength).add(end1);
        Vec3 finish2 = axis2.scale(handleLength).add(end2);
        Vec3 faceNormal1 = (Vec3)bc.normals.getFirst();
        Vec3 faceNormal2 = (Vec3)bc.normals.getSecond();
        int segCount = bc.getSegmentCount();
        float[] lut = bc.getStepLUT();
        for (int i = 0; i < segCount; ++i) {
            float t = i == segCount ? 1.0f : (float)i * lut[i] / (float)segCount;
            Vec3 result = VecHelper.bezier((Vec3)end1, (Vec3)end2, (Vec3)finish1, (Vec3)finish2, (float)(t += 0.5f / (float)segCount));
            Vec3 derivative = VecHelper.bezierDerivative((Vec3)end1, (Vec3)end2, (Vec3)finish1, (Vec3)finish2, (float)t).normalize();
            Vec3 faceNormal = faceNormal1.equals((Object)faceNormal2) ? faceNormal1 : VecHelper.slerp((float)t, (Vec3)faceNormal1, (Vec3)faceNormal2);
            Vec3 normal = faceNormal.cross(derivative).normalize();
            Vec3 below = result.add(faceNormal.scale(-1.125));
            Vec3 rail1 = below.add(normal.scale((double)0.97f));
            Vec3 rail2 = below.subtract(normal.scale((double)0.97f));
            Vec3 railMiddle = rail1.add(rail2).scale(0.5);
            for (Vec3 vec : new Vec3[]{rail1, rail2, railMiddle}) {
                BlockPos pos = BlockPos.containing((Position)vec);
                Pair key = Pair.of((Object)pos.getX(), (Object)pos.getZ());
                if (yLevels.containsKey(key) && !((Double)yLevels.get(key) > vec.y)) continue;
                yLevels.put(key, vec.y);
            }
        }
        for (Map.Entry entry : yLevels.entrySet()) {
            BlockState stateToPlace;
            double yValue = (Double)entry.getValue();
            int floor = Mth.floor((double)yValue);
            boolean placeSlab = slabLike && yValue - (double)floor >= 0.5;
            BlockPos targetPos = new BlockPos(((Integer)((Pair)entry.getKey()).getFirst()).intValue(), floor, ((Integer)((Pair)entry.getKey()).getSecond()).intValue());
            targetPos = targetPos.offset((Vec3i)tePosition).above(placeSlab ? 1 : 0);
            BlockState blockState = stateToPlace = placeSlab ? (BlockState)defaultBlockState.setValue((Property)SlabBlock.TYPE, (Comparable)SlabType.BOTTOM) : defaultBlockState;
            if (!visited.add(targetPos)) continue;
            if (TrackPaver.placeBlockIfFree(level, targetPos, stateToPlace, simulate)) {
                itemsNeeded += !placeSlab ? 2 : 1;
            }
            if (!placeSlab || !visited.add(targetPos.below())) continue;
            BlockState topSlab = (BlockState)stateToPlace.setValue((Property)SlabBlock.TYPE, (Comparable)SlabType.TOP);
            if (!TrackPaver.placeBlockIfFree(level, targetPos.below(), topSlab, simulate)) continue;
            ++itemsNeeded;
        }
        return itemsNeeded;
    }

    private static boolean isWallLike(BlockState defaultBlockState) {
        return defaultBlockState.getBlock() instanceof WallBlock || AllBlocks.METAL_GIRDER.has(defaultBlockState);
    }

    private static boolean placeBlockIfFree(Level level, BlockPos pos, BlockState state, boolean simulate) {
        BlockState stateAtPos = level.getBlockState(pos);
        if (stateAtPos.getBlock() != state.getBlock() && stateAtPos.canBeReplaced()) {
            if (!simulate) {
                level.setBlock(pos, ProperWaterloggedBlock.withWater((LevelAccessor)level, state, pos), 3);
            }
            return true;
        }
        return false;
    }
}
