/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.bus.api.Event
 *  net.neoforged.neoforge.common.NeoForge
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.Create;
import com.simibubi.create.api.event.TrackGraphMergeEvent;
import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackGraphSync;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.signal.SignalPropagator;
import com.simibubi.create.content.trains.track.ITrackBlock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

public class TrackPropagator {
    public static void onRailRemoved(LevelAccessor reader, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (!(block instanceof ITrackBlock)) {
            return;
        }
        ITrackBlock track = (ITrackBlock)block;
        Collection<TrackNodeLocation.DiscoveredLocation> ends = track.getConnected((BlockGetter)reader, pos, state, false, null);
        GlobalRailwayManager manager = Create.RAILWAYS;
        TrackGraphSync sync = manager.sync;
        for (TrackNodeLocation.DiscoveredLocation discoveredLocation : ends) {
            List<TrackGraph> intersecting = manager.getGraphs(reader, discoveredLocation);
            for (TrackGraph foundGraph : intersecting) {
                TrackNode removedNode = foundGraph.locateNode(discoveredLocation);
                if (removedNode == null) continue;
                foundGraph.removeNode(reader, discoveredLocation);
                sync.nodeRemoved(foundGraph, removedNode);
                if (!foundGraph.isEmpty()) continue;
                manager.removeGraphAndGroup(foundGraph);
                sync.graphRemoved(foundGraph);
            }
        }
        HashSet<BlockPos> positionsToUpdate = new HashSet<BlockPos>();
        for (TrackNodeLocation.DiscoveredLocation removedEnd : ends) {
            positionsToUpdate.addAll(removedEnd.allAdjacent());
        }
        HashSet<TrackGraph> hashSet = new HashSet<TrackGraph>();
        for (BlockPos blockPos : positionsToUpdate) {
            TrackGraph onRailAdded;
            if (blockPos.equals((Object)pos) || (onRailAdded = TrackPropagator.onRailAdded(reader, blockPos, reader.getBlockState(blockPos))) == null) continue;
            hashSet.add(onRailAdded);
        }
        for (TrackGraph railGraph : hashSet) {
            manager.updateSplitGraph(reader, railGraph);
        }
        manager.markTracksDirty();
    }

    public static TrackGraph onRailAdded(LevelAccessor reader, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (!(block instanceof ITrackBlock)) {
            return null;
        }
        ITrackBlock track = (ITrackBlock)block;
        GlobalRailwayManager manager = Create.RAILWAYS;
        TrackGraphSync sync = manager.sync;
        ArrayList<FrontierEntry> frontier = new ArrayList<FrontierEntry>();
        HashSet<TrackNodeLocation.DiscoveredLocation> visited = new HashSet<TrackNodeLocation.DiscoveredLocation>();
        HashSet<TrackGraph> connectedGraphs = new HashSet<TrackGraph>();
        TrackPropagator.addInitialEndsOf(reader, pos, state, track, frontier, false);
        int emergencyExit = 1000;
        while (!frontier.isEmpty() && emergencyExit-- != 0) {
            FrontierEntry entry = (FrontierEntry)frontier.remove(0);
            List<TrackGraph> intersecting = manager.getGraphs(reader, entry.currentNode);
            for (TrackGraph graph : intersecting) {
                TrackNode node = graph.locateNode(entry.currentNode);
                graph.removeNode(reader, entry.currentNode);
                sync.nodeRemoved(graph, node);
                connectedGraphs.add(graph);
            }
            if (!intersecting.isEmpty()) continue;
            Collection<TrackNodeLocation.DiscoveredLocation> ends = ITrackBlock.walkConnectedTracks((BlockGetter)reader, entry.currentNode, false);
            if (entry.prevNode != null) {
                ends.remove((Object)entry.prevNode);
            }
            TrackPropagator.continueSearch(frontier, visited, entry, ends);
        }
        frontier.clear();
        visited.clear();
        TrackGraph graph = null;
        Iterator iterator = connectedGraphs.iterator();
        while (iterator.hasNext()) {
            TrackGraph railGraph = (TrackGraph)iterator.next();
            if (!railGraph.isEmpty() || connectedGraphs.size() == 1) continue;
            manager.removeGraphAndGroup(railGraph);
            sync.graphRemoved(railGraph);
            iterator.remove();
        }
        if (connectedGraphs.size() > 1) {
            for (TrackGraph other : connectedGraphs) {
                if (graph == null) {
                    graph = other;
                    continue;
                }
                NeoForge.EVENT_BUS.post((Event)new TrackGraphMergeEvent(other, graph));
                other.transferAll(graph);
                manager.removeGraphAndGroup(other);
                sync.graphRemoved(other);
            }
        } else if (connectedGraphs.size() == 1) {
            graph = (TrackGraph)connectedGraphs.stream().findFirst().get();
        } else {
            graph = new TrackGraph();
            manager.putGraphWithDefaultGroup(graph);
        }
        TrackNodeLocation.DiscoveredLocation startNode = null;
        TrackPropagator.addInitialEndsOf(reader, pos, state, track, frontier, true);
        emergencyExit = 1000;
        while (!frontier.isEmpty() && emergencyExit-- != 0) {
            boolean first;
            FrontierEntry entry = (FrontierEntry)frontier.remove(0);
            Collection<TrackNodeLocation.DiscoveredLocation> ends = ITrackBlock.walkConnectedTracks((BlockGetter)reader, entry.currentNode, false);
            boolean bl = first = entry.prevNode == null;
            if (!first) {
                ends.remove((Object)entry.prevNode);
            }
            if (TrackPropagator.isValidGraphNodeLocation(entry.currentNode, ends, first)) {
                startNode = entry.currentNode;
                break;
            }
            TrackPropagator.continueSearch(frontier, visited, entry, ends);
        }
        frontier.clear();
        HashSet<TrackNode> addedNodes = new HashSet<TrackNode>();
        graph.createNodeIfAbsent(startNode);
        frontier.add(new FrontierEntry(startNode, null, startNode));
        emergencyExit = 1000;
        while (!frontier.isEmpty() && emergencyExit-- != 0) {
            boolean first;
            FrontierEntry entry = (FrontierEntry)frontier.remove(0);
            TrackNodeLocation.DiscoveredLocation parentNode = entry.parentNode;
            Collection<TrackNodeLocation.DiscoveredLocation> ends = ITrackBlock.walkConnectedTracks((BlockGetter)reader, entry.currentNode, false);
            boolean bl = first = entry.prevNode == null;
            if (!first) {
                ends.remove((Object)entry.prevNode);
            }
            if (TrackPropagator.isValidGraphNodeLocation(entry.currentNode, ends, first) && entry.currentNode != startNode) {
                boolean nodeIsNew = graph.createNodeIfAbsent(entry.currentNode);
                graph.connectNodes(reader, parentNode, entry.currentNode, entry.currentNode.getTurn());
                addedNodes.add(graph.locateNode(entry.currentNode));
                parentNode = entry.currentNode;
                if (!nodeIsNew) continue;
            }
            TrackPropagator.continueSearchWithParent(frontier, entry, parentNode, ends);
        }
        manager.markTracksDirty();
        for (TrackNode trackNode : addedNodes) {
            SignalPropagator.notifySignalsOfNewNode(graph, trackNode);
        }
        return graph;
    }

    private static void addInitialEndsOf(LevelAccessor reader, BlockPos pos, BlockState state, ITrackBlock track, List<FrontierEntry> frontier, boolean ignoreTurns) {
        for (TrackNodeLocation.DiscoveredLocation initial : track.getConnected((BlockGetter)reader, pos, state, ignoreTurns, null)) {
            frontier.add(new FrontierEntry(null, null, initial));
        }
    }

    private static void continueSearch(List<FrontierEntry> frontier, Set<TrackNodeLocation.DiscoveredLocation> visited, FrontierEntry entry, Collection<TrackNodeLocation.DiscoveredLocation> ends) {
        for (TrackNodeLocation.DiscoveredLocation location : ends) {
            if (!visited.add(location)) continue;
            frontier.add(new FrontierEntry(null, entry.currentNode, location));
        }
    }

    private static void continueSearchWithParent(List<FrontierEntry> frontier, FrontierEntry entry, TrackNodeLocation.DiscoveredLocation parentNode, Collection<TrackNodeLocation.DiscoveredLocation> ends) {
        for (TrackNodeLocation.DiscoveredLocation location : ends) {
            frontier.add(new FrontierEntry(parentNode, entry.currentNode, location));
        }
    }

    public static boolean isValidGraphNodeLocation(TrackNodeLocation.DiscoveredLocation location, Collection<TrackNodeLocation.DiscoveredLocation> next, boolean first) {
        boolean centeredZ;
        int size = next.size() - (first ? 1 : 0);
        if (size != 1) {
            return true;
        }
        if (location.shouldForceNode()) {
            return true;
        }
        if (location.differentMaterials()) {
            return true;
        }
        if (next.stream().anyMatch(TrackNodeLocation.DiscoveredLocation::shouldForceNode)) {
            return true;
        }
        Vec3 direction = location.getDirection();
        if (direction != null && next.stream().anyMatch(dl -> dl.notInLineWith(direction))) {
            return true;
        }
        Vec3 vec = location.getLocation();
        boolean centeredX = !Mth.equal((double)vec.x, (double)Math.round(vec.x));
        boolean bl = centeredZ = !Mth.equal((double)vec.z, (double)Math.round(vec.z));
        if (centeredX && !centeredZ) {
            return (int)Math.round(vec.z) % 16 == 0;
        }
        return (int)Math.round(vec.x) % 16 == 0;
    }

    static class FrontierEntry {
        TrackNodeLocation.DiscoveredLocation prevNode;
        TrackNodeLocation.DiscoveredLocation currentNode;
        TrackNodeLocation.DiscoveredLocation parentNode;

        public FrontierEntry(TrackNodeLocation.DiscoveredLocation parent, TrackNodeLocation.DiscoveredLocation previousNode, TrackNodeLocation.DiscoveredLocation location) {
            this.parentNode = parent;
            this.prevNode = previousNode;
            this.currentNode = location;
        }
    }
}
