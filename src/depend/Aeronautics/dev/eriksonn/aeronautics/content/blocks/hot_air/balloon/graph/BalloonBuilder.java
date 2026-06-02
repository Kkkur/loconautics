/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.util.LevelAccelerator
 *  it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue
 *  it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.graph;

import dev.eriksonn.aeronautics.content.blocks.hot_air.BlockEntityLiftingGasProvider;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.Balloon;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.ClientBalloon;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.ServerBalloon;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.graph.BalloonLayerData;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.graph.BalloonLayerGraph;
import dev.eriksonn.aeronautics.index.AeroTags;
import dev.ryanhcode.sable.util.LevelAccelerator;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BalloonBuilder {
    protected static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

    public static boolean isCandidatePosition(BlockPos pos, BlockState state, BalloonLayerGraph graph, BalloonLayerGraph existingGraph, BalloonLayerGraph mainGraph) {
        if (BalloonBuilder.containsBlockAt(pos, graph)) {
            return false;
        }
        if (existingGraph != null && BalloonBuilder.containsBlockAt(pos, existingGraph)) {
            return false;
        }
        if (mainGraph != null && BalloonBuilder.containsBlockAt(pos, mainGraph)) {
            return false;
        }
        return state.isAir() || !state.is(AeroTags.BlockTags.AIRTIGHT);
    }

    public static boolean isCandidatePosition(LevelAccelerator accelerator, BlockPos pos, BalloonLayerGraph graph, BalloonLayerGraph existingGraph, BalloonLayerGraph mainGraph) {
        BlockState state = accelerator.getBlockState(pos);
        return BalloonBuilder.isCandidatePosition(pos, state, graph, existingGraph, mainGraph);
    }

    private static boolean containsBlockAt(BlockPos pos, BalloonLayerGraph graph) {
        List<BalloonLayerData> layers = graph.getLayersAtY(pos.getY());
        for (BalloonLayerData layer : layers) {
            if (!layer.getHotAirBlock(pos.getX(), pos.getZ())) continue;
            return true;
        }
        return false;
    }

    public static BalloonLayerGraph buildBalloon(Level level, BlockPos startPos, @Nullable BalloonLayerGraph mainGraph) {
        LevelAccelerator accelerator = new LevelAccelerator(level);
        BalloonLayerGraph graph = new BalloonLayerGraph(startPos.getY());
        boolean firstSafe = BalloonBuilder.upwardsBiasedFloodFill(accelerator, startPos, graph, null, mainGraph);
        if (!firstSafe) {
            return null;
        }
        BalloonBuilder.completeBalloon(accelerator, graph, mainGraph);
        graph.rebuildConnections(startPos);
        return graph;
    }

    public static void completeBalloon(LevelAccelerator accelerator, BalloonLayerGraph graph, @Nullable BalloonLayerGraph mainGraph) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        boolean progressMade = true;
        while (progressMade) {
            progressMade = false;
            List<BalloonLayerData>[] layerMap = graph.getAllLayers();
            int minY = graph.getMinY();
            int maxY = minY + layerMap.length;
            for (int layerY = minY; layerY < maxY; ++layerY) {
                List<BalloonLayerData> layersAtY = layerMap[layerY - minY];
                for (BalloonLayerData layer : new ObjectArrayList(layersAtY)) {
                    if (layer.getState() == BalloonLayerData.State.COMPLETE) continue;
                    LongLinkedOpenHashSet queue = new LongLinkedOpenHashSet();
                    Iterator<BlockPos> candidates = layer.blockIterator();
                    while (candidates.hasNext()) {
                        queue.add(mutableBlockPos.set((Vec3i)candidates.next()).move(0, -1, 0).asLong());
                    }
                    while (!queue.isEmpty()) {
                        BalloonLayerGraph newGraph;
                        long l = queue.removeFirstLong();
                        BlockPos.MutableBlockPos candidate = mutableBlockPos.set(l);
                        if (!BalloonBuilder.isCandidatePosition(accelerator, (BlockPos)candidate, newGraph = new BalloonLayerGraph(candidate.getY()), graph, mainGraph)) continue;
                        boolean safe = BalloonBuilder.upwardsBiasedFloodFill(accelerator, (BlockPos)candidate, newGraph, graph, mainGraph);
                        if (!safe) {
                            BalloonBuilder.removeReachable(accelerator, queue, (BlockPos)mutableBlockPos);
                            continue;
                        }
                        List<BalloonLayerData> newLayersAtY = newGraph.getLayersAtY(candidate.getY());
                        queue.removeIf(x -> {
                            for (BalloonLayerData layerData : newLayersAtY) {
                                if (!layerData.getHotAirBlock(BlockPos.getX((long)x), BlockPos.getZ((long)x))) continue;
                                return true;
                            }
                            return false;
                        });
                        graph.addAll(newGraph);
                        progressMade = true;
                    }
                    layer.setState(BalloonLayerData.State.COMPLETE);
                }
            }
        }
    }

    private static void removeReachable(LevelAccelerator accelerator, LongLinkedOpenHashSet queue, BlockPos floodfillOrigin) {
        LongLinkedOpenHashSet visited = new LongLinkedOpenHashSet();
        LongLinkedOpenHashSet frontier = new LongLinkedOpenHashSet();
        long startLong = floodfillOrigin.asLong();
        frontier.add(startLong);
        visited.add(startLong);
        queue.remove(startLong);
        BlockPos.MutableBlockPos currentPos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos adjacentPos = new BlockPos.MutableBlockPos();
        while (!frontier.isEmpty()) {
            long current = frontier.removeFirstLong();
            currentPos.set(current);
            for (Direction dir : HORIZONTAL_DIRECTIONS) {
                BlockState neighborState;
                adjacentPos.setWithOffset((Vec3i)currentPos, dir);
                long neighborLong = adjacentPos.asLong();
                if (!queue.contains(neighborLong) || visited.contains(neighborLong) || !(neighborState = accelerator.getBlockState((BlockPos)adjacentPos)).isAir() && neighborState.is(AeroTags.BlockTags.AIRTIGHT)) continue;
                queue.remove(neighborLong);
                frontier.add(neighborLong);
                visited.add(neighborLong);
            }
        }
    }

    public static boolean upwardsBiasedFloodFill(LevelAccelerator accelerator, BlockPos startPos, BalloonLayerGraph outputGraph, BalloonLayerGraph existingGraph, BalloonLayerGraph mainGraph) {
        if (accelerator.isOutsideBuildHeight(startPos)) {
            return false;
        }
        long startPosLong = startPos.asLong();
        if (!BalloonBuilder.isCandidatePosition(accelerator, startPos, outputGraph, existingGraph, mainGraph)) {
            return false;
        }
        LongArrayFIFOQueue queue = new LongArrayFIFOQueue();
        LongOpenHashSet visited = new LongOpenHashSet();
        queue.enqueue(startPosLong);
        visited.add(startPosLong);
        BlockPos.MutableBlockPos currentPos = new BlockPos.MutableBlockPos();
        BalloonLayerData newLayer = new BalloonLayerData(startPos.getY());
        int yLevel = startPos.getY();
        while (!queue.isEmpty()) {
            long currentPosLong = queue.dequeueLastLong();
            currentPos.set(currentPosLong);
            BlockState state = accelerator.getBlockState((BlockPos)currentPos);
            if (!BalloonBuilder.isCandidatePosition((BlockPos)currentPos, state, outputGraph, existingGraph, mainGraph)) continue;
            ++newLayer.hotAirCount;
            newLayer.addHotAirBlock(currentPos.getX(), currentPos.getZ());
            if (BalloonBuilder.isSolid(state)) {
                ++newLayer.solidCount;
                newLayer.addSolidBlock(currentPos.getX(), currentPos.getZ());
            }
            BlockPos posAbove = currentPos.above();
            long posAboveLong = currentPosLong + 1L;
            if (!visited.contains(posAboveLong) && BalloonBuilder.isCandidatePosition(accelerator, posAbove, outputGraph, existingGraph, mainGraph) && !BalloonBuilder.upwardsBiasedFloodFill(accelerator, posAbove, outputGraph, existingGraph, mainGraph)) {
                return false;
            }
            for (Direction direction : HORIZONTAL_DIRECTIONS) {
                BlockPos neighborPos = currentPos.relative(direction);
                long neighborPosLong = neighborPos.asLong();
                if (visited.contains(neighborPosLong)) continue;
                visited.add(neighborPosLong);
                queue.enqueue(neighborPosLong);
            }
        }
        outputGraph.addLayer(yLevel, newLayer);
        return true;
    }

    public static Balloon attemptBuildBalloon(BlockEntityLiftingGasProvider heater, BlockPos startPos) {
        Level level = heater.getLevel();
        LevelAccelerator accelerator = new LevelAccelerator(level);
        ObjectArrayList heaters = new ObjectArrayList();
        heaters.add((Object)heater);
        BalloonLayerGraph graph = BalloonBuilder.buildBalloon(level, startPos, null);
        if (graph == null) {
            return null;
        }
        if (level instanceof ServerLevel) {
            return new ServerBalloon(level, accelerator, startPos, graph, (ObjectArrayList<BlockEntityLiftingGasProvider>)heaters);
        }
        return new ClientBalloon(level, accelerator, startPos, graph, (ObjectArrayList<BlockEntityLiftingGasProvider>)heaters);
    }

    public static boolean isSolid(BlockState state) {
        return !state.isAir();
    }
}
