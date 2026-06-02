/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.Pair
 */
package com.simibubi.create.content.trains.signal;

import com.google.common.base.Predicates;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.EdgeData;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackGraphSync;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;

public class SignalPropagator {
    public static void onSignalRemoved(TrackGraph graph, SignalBoundary signal) {
        signal.sidesToUpdate.map($ -> false);
        for (boolean front : Iterate.trueAndFalse) {
            if (((Boolean)signal.sidesToUpdate.get(front)).booleanValue()) continue;
            UUID id = (UUID)signal.groups.get(front);
            if (Create.RAILWAYS.signalEdgeGroups.remove(id) != null) {
                Create.RAILWAYS.sync.edgeGroupRemoved(id);
            }
            SignalPropagator.walkSignals(graph, signal, front, pair -> {
                TrackNode node1 = (TrackNode)pair.getFirst();
                SignalBoundary boundary = (SignalBoundary)pair.getSecond();
                boundary.queueUpdate(node1);
                return false;
            }, signalData -> {
                if (!signalData.hasSignalBoundaries()) {
                    signalData.setSingleSignalGroup(graph, EdgeData.passiveGroup);
                    return true;
                }
                return false;
            }, false);
        }
    }

    public static void notifySignalsOfNewNode(TrackGraph graph, TrackNode node) {
        ArrayList<Couple<TrackNode>> frontier = new ArrayList<Couple<TrackNode>>();
        frontier.add(Couple.create((Object)node, null));
        SignalPropagator.walkSignals(graph, frontier, pair -> {
            TrackNode node1 = (TrackNode)pair.getFirst();
            SignalBoundary boundary = (SignalBoundary)pair.getSecond();
            boundary.queueUpdate(node1);
            return false;
        }, signalData -> {
            if (!signalData.hasSignalBoundaries()) {
                signalData.setSingleSignalGroup(graph, EdgeData.passiveGroup);
                return true;
            }
            return false;
        }, false);
    }

    public static void propagateSignalGroup(TrackGraph graph, SignalBoundary signal, boolean front) {
        Map<UUID, SignalEdgeGroup> globalGroups = Create.RAILWAYS.signalEdgeGroups;
        TrackGraphSync sync = Create.RAILWAYS.sync;
        SignalEdgeGroup group = new SignalEdgeGroup(UUID.randomUUID());
        UUID groupId = group.id;
        globalGroups.put(groupId, group);
        signal.setGroup(front, groupId);
        sync.pointAdded(graph, signal);
        SignalPropagator.walkSignals(graph, signal, front, pair -> {
            TrackNode node1 = (TrackNode)pair.getFirst();
            SignalBoundary boundary = (SignalBoundary)pair.getSecond();
            UUID currentGroup = boundary.getGroup(node1);
            if (currentGroup != null && globalGroups.remove(currentGroup) != null) {
                sync.edgeGroupRemoved(currentGroup);
            }
            boundary.setGroupAndUpdate(node1, groupId);
            sync.pointAdded(graph, boundary);
            return true;
        }, signalData -> {
            UUID singleSignalGroup = signalData.getSingleSignalGroup();
            if (singleSignalGroup != null && globalGroups.remove(singleSignalGroup) != null) {
                sync.edgeGroupRemoved(singleSignalGroup);
            }
            signalData.setSingleSignalGroup(graph, groupId);
            return true;
        }, false);
        group.resolveColor();
        sync.edgeGroupCreated(groupId, group.color);
    }

    public static Map<UUID, Boolean> collectChainedSignals(TrackGraph graph, SignalBoundary signal, boolean front) {
        HashMap<UUID, Boolean> map = new HashMap<UUID, Boolean>();
        SignalPropagator.walkSignals(graph, signal, front, pair -> {
            SignalBoundary boundary = (SignalBoundary)pair.getSecond();
            map.put(boundary.id, !boundary.isPrimary((TrackNode)pair.getFirst()));
            return false;
        }, (Predicate<EdgeData>)Predicates.alwaysFalse(), true);
        return map;
    }

    public static void walkSignals(TrackGraph graph, SignalBoundary signal, boolean front, Predicate<Pair<TrackNode, SignalBoundary>> boundaryCallback, Predicate<EdgeData> nonBoundaryCallback, boolean forCollection) {
        SignalBoundary immediateBoundary;
        Couple edgeLocation = signal.edgeLocation;
        Couple startNodes = edgeLocation.map(graph::locateNode);
        Couple startEdges = startNodes.mapWithParams((l1, l2) -> graph.getConnectionsFrom((TrackNode)l1).get(l2), startNodes.swap());
        TrackNode node1 = (TrackNode)startNodes.get(front);
        TrackNode node2 = (TrackNode)startNodes.get(!front);
        TrackEdge startEdge = (TrackEdge)startEdges.get(front);
        TrackEdge oppositeEdge = (TrackEdge)startEdges.get(!front);
        if (startEdge == null) {
            return;
        }
        if (!forCollection) {
            SignalPropagator.notifyTrains(graph, startEdge, oppositeEdge);
            startEdge.getEdgeData().refreshIntersectingSignalGroups(graph);
            Create.RAILWAYS.sync.edgeDataChanged(graph, node1, node2, startEdge, oppositeEdge);
        }
        if ((immediateBoundary = startEdge.getEdgeData().next(EdgePointType.SIGNAL, signal.getLocationOn(startEdge))) != null) {
            if (boundaryCallback.test((Pair<TrackNode, SignalBoundary>)Pair.of((Object)node1, (Object)immediateBoundary))) {
                startEdge.getEdgeData().refreshIntersectingSignalGroups(graph);
            }
            return;
        }
        ArrayList<Couple<TrackNode>> frontier = new ArrayList<Couple<TrackNode>>();
        frontier.add(Couple.create((Object)node2, (Object)node1));
        SignalPropagator.walkSignals(graph, frontier, boundaryCallback, nonBoundaryCallback, forCollection);
    }

    private static void walkSignals(TrackGraph graph, List<Couple<TrackNode>> frontier, Predicate<Pair<TrackNode, SignalBoundary>> boundaryCallback, Predicate<EdgeData> nonBoundaryCallback, boolean forCollection) {
        HashSet<TrackEdge> visited = new HashSet<TrackEdge>();
        while (!frontier.isEmpty()) {
            Couple<TrackNode> couple = frontier.remove(0);
            TrackNode currentNode = (TrackNode)couple.getFirst();
            TrackNode prevNode = (TrackNode)couple.getSecond();
            block1: for (Map.Entry<TrackNode, TrackEdge> entry : graph.getConnectionsFrom(currentNode).entrySet()) {
                TrackNode nextNode = entry.getKey();
                TrackEdge edge = entry.getValue();
                if (nextNode == prevNode || !visited.add(edge) || forCollection && !graph.getConnectionsFrom(prevNode).get(currentNode).canTravelTo(edge)) continue;
                TrackEdge oppositeEdge = graph.getConnectionsFrom(nextNode).get(currentNode);
                visited.add(oppositeEdge);
                for (boolean flip : Iterate.falseAndTrue) {
                    TrackEdge currentEdge = flip ? oppositeEdge : edge;
                    EdgeData signalData = currentEdge.getEdgeData();
                    if (!signalData.hasSignalBoundaries()) {
                        if (!nonBoundaryCallback.test(signalData)) continue;
                        SignalPropagator.notifyTrains(graph, currentEdge);
                        Create.RAILWAYS.sync.edgeDataChanged(graph, currentNode, nextNode, edge, oppositeEdge);
                        continue;
                    }
                    SignalBoundary nextBoundary = signalData.next(EdgePointType.SIGNAL, 0.0);
                    if (nextBoundary == null) continue;
                    if (!boundaryCallback.test((Pair<TrackNode, SignalBoundary>)Pair.of((Object)currentNode, (Object)nextBoundary))) continue block1;
                    SignalPropagator.notifyTrains(graph, edge, oppositeEdge);
                    currentEdge.getEdgeData().refreshIntersectingSignalGroups(graph);
                    Create.RAILWAYS.sync.edgeDataChanged(graph, currentNode, nextNode, edge, oppositeEdge);
                    continue block1;
                }
                frontier.add((Couple<TrackNode>)Couple.create((Object)nextNode, (Object)currentNode));
            }
        }
    }

    public static void notifyTrains(TrackGraph graph, TrackEdge ... edges) {
        for (TrackEdge trackEdge : edges) {
            for (Train train : Create.RAILWAYS.trains.values()) {
                if (train.graph != graph || train.updateSignalBlocks) continue;
                train.forEachTravellingPoint(tp -> {
                    if (tp.edge == trackEdge) {
                        train.updateSignalBlocks = true;
                    }
                });
            }
        }
    }
}
