/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.graph;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.EdgeData;
import com.simibubi.create.content.trains.graph.EdgePointManager;
import com.simibubi.create.content.trains.graph.EdgePointStorage;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackEdgeIntersection;
import com.simibubi.create.content.trains.graph.TrackGraphBounds;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackMaterial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TrackGraph {
    public static final AtomicInteger graphNetIdGenerator = new AtomicInteger();
    public static final AtomicInteger nodeNetIdGenerator = new AtomicInteger();
    public UUID id;
    public Color color;
    Map<TrackNodeLocation, TrackNode> nodes;
    Map<Integer, TrackNode> nodesById;
    Map<TrackNode, Map<TrackNode, TrackEdge>> connectionsByNode;
    EdgePointStorage edgePoints;
    Map<ResourceKey<Level>, TrackGraphBounds> bounds;
    List<TrackEdge> deferredIntersectionUpdates;
    int netId;
    int checksum = 0;

    public TrackGraph() {
        this(UUID.randomUUID());
    }

    public TrackGraph(UUID graphID) {
        this.setId(graphID);
        this.nodes = new HashMap<TrackNodeLocation, TrackNode>();
        this.nodesById = new HashMap<Integer, TrackNode>();
        this.bounds = new HashMap<ResourceKey<Level>, TrackGraphBounds>();
        this.connectionsByNode = new IdentityHashMap<TrackNode, Map<TrackNode, TrackEdge>>();
        this.edgePoints = new EdgePointStorage();
        this.deferredIntersectionUpdates = new ArrayList<TrackEdge>();
        this.netId = TrackGraph.nextGraphId();
    }

    public <T extends TrackEdgePoint> void addPoint(EdgePointType<T> type, T point) {
        this.edgePoints.put(type, point);
        EdgePointManager.onEdgePointAdded(this, point, type);
        Create.RAILWAYS.sync.pointAdded(this, point);
        this.markDirty();
    }

    public <T extends TrackEdgePoint> T getPoint(EdgePointType<T> type, UUID id) {
        return this.edgePoints.get(type, id);
    }

    public <T extends TrackEdgePoint> Collection<T> getPoints(EdgePointType<T> type) {
        return this.edgePoints.values(type);
    }

    public <T extends TrackEdgePoint> T removePoint(EdgePointType<T> type, UUID id) {
        T removed = this.edgePoints.remove(type, id);
        if (removed == null) {
            return null;
        }
        EdgePointManager.onEdgePointRemoved(this, removed, type);
        Create.RAILWAYS.sync.pointRemoved(this, (TrackEdgePoint)removed);
        this.markDirty();
        return removed;
    }

    public void tickPoints(boolean preTrains) {
        this.edgePoints.tick(this, preTrains);
    }

    public TrackGraphBounds getBounds(Level level) {
        return this.bounds.computeIfAbsent((ResourceKey<Level>)level.dimension(), dim -> new TrackGraphBounds(this, (ResourceKey<Level>)dim));
    }

    public void invalidateBounds() {
        this.checksum = 0;
        this.bounds.clear();
    }

    public Set<TrackNodeLocation> getNodes() {
        return this.nodes.keySet();
    }

    public TrackNode locateNode(Level level, Vec3 position) {
        return this.locateNode(new TrackNodeLocation(position).in(level));
    }

    public TrackNode locateNode(TrackNodeLocation position) {
        return this.nodes.get((Object)position);
    }

    public TrackNode getNode(int netId) {
        return this.nodesById.get(netId);
    }

    public boolean createNodeIfAbsent(TrackNodeLocation.DiscoveredLocation location) {
        if (!this.addNodeIfAbsent(new TrackNode(location, TrackGraph.nextNodeId(), location.normal))) {
            return false;
        }
        TrackNode newNode = this.nodes.get((Object)location);
        Create.RAILWAYS.sync.nodeAdded(this, newNode);
        this.invalidateBounds();
        this.markDirty();
        return true;
    }

    public void loadNode(TrackNodeLocation location, int netId, Vec3 normal) {
        this.addNode(new TrackNode(location, netId, normal));
    }

    public void addNode(TrackNode node) {
        TrackNodeLocation location = node.getLocation();
        if (this.nodes.containsKey((Object)location)) {
            this.removeNode(null, location);
        }
        this.nodes.put(location, node);
        this.nodesById.put(node.getNetId(), node);
    }

    public boolean addNodeIfAbsent(TrackNode node) {
        if (this.nodes.putIfAbsent(node.getLocation(), node) != null) {
            return false;
        }
        this.nodesById.put(node.getNetId(), node);
        return true;
    }

    public boolean removeNode(@Nullable LevelAccessor level, TrackNodeLocation location) {
        TrackNode removed = this.nodes.remove((Object)location);
        if (removed == null) {
            return false;
        }
        Map<UUID, Train> trains = Create.RAILWAYS.trains;
        for (UUID uuid : trains.keySet()) {
            Train train = trains.get(uuid);
            if (train.graph != this || !train.isTravellingOn(removed)) continue;
            train.detachFromTracks();
        }
        this.nodesById.remove(removed.netId);
        this.invalidateBounds();
        if (!this.connectionsByNode.containsKey(removed)) {
            return true;
        }
        Map<TrackNode, TrackEdge> connections = this.connectionsByNode.remove(removed);
        for (Map.Entry<TrackNode, TrackEdge> entry : connections.entrySet()) {
            TrackEdge trackEdge = entry.getValue();
            EdgeData edgeData = trackEdge.getEdgeData();
            for (TrackEdgePoint point : edgeData.getPoints()) {
                if (level != null) {
                    point.invalidate(level);
                }
                this.edgePoints.remove(point.getType(), point.getId());
            }
            if (level == null) continue;
            TrackNode otherNode = entry.getKey();
            for (TrackEdgeIntersection intersection : edgeData.getIntersections()) {
                Couple<TrackNodeLocation> target = intersection.target;
                TrackGraph graph = Create.RAILWAYS.getGraph(level, (TrackNodeLocation)((Object)target.getFirst()));
                if (graph == null) continue;
                graph.removeIntersection(intersection, removed, otherNode);
            }
        }
        for (TrackNode railNode : connections.keySet()) {
            if (!this.connectionsByNode.containsKey(railNode)) continue;
            this.connectionsByNode.get(railNode).remove(removed);
        }
        return true;
    }

    private void removeIntersection(TrackEdgeIntersection intersection, TrackNode targetNode1, TrackNode targetNode2) {
        TrackEdge edge;
        Map<TrackNode, TrackEdge> from2;
        TrackEdge edge2;
        TrackNode node1 = this.locateNode((TrackNodeLocation)((Object)intersection.target.getFirst()));
        TrackNode node2 = this.locateNode((TrackNodeLocation)((Object)intersection.target.getSecond()));
        if (node1 == null || node2 == null) {
            return;
        }
        Map<TrackNode, TrackEdge> from1 = this.getConnectionsFrom(node1);
        if (from1 != null && (edge2 = from1.get(node2)) != null) {
            edge2.getEdgeData().removeIntersection(this, intersection.id);
        }
        if ((from2 = this.getConnectionsFrom(node2)) != null && (edge = from2.get(node1)) != null) {
            edge.getEdgeData().removeIntersection(this, intersection.id);
        }
    }

    public static int nextNodeId() {
        return nodeNetIdGenerator.incrementAndGet();
    }

    public static int nextGraphId() {
        return graphNetIdGenerator.incrementAndGet();
    }

    public void transferAll(TrackGraph toOther) {
        this.nodes.forEach((loc, node) -> {
            if (toOther.addNodeIfAbsent((TrackNode)node)) {
                Create.RAILWAYS.sync.nodeAdded(toOther, (TrackNode)node);
            }
        });
        this.connectionsByNode.forEach((node1, map) -> map.forEach((node2, edge) -> {
            TrackNode n1 = toOther.locateNode(node1.location);
            TrackNode n2 = toOther.locateNode(node2.location);
            if (n1 == null || n2 == null) {
                return;
            }
            if (toOther.putConnection(n1, n2, (TrackEdge)edge)) {
                Create.RAILWAYS.sync.edgeAdded(toOther, n1, n2, (TrackEdge)edge);
                Create.RAILWAYS.sync.edgeDataChanged(toOther, n1, n2, (TrackEdge)edge);
            }
        }));
        this.edgePoints.transferAll(toOther, toOther.edgePoints);
        this.nodes.clear();
        this.connectionsByNode.clear();
        toOther.invalidateBounds();
        Map<UUID, Train> trains = Create.RAILWAYS.trains;
        for (UUID uuid : trains.keySet()) {
            Train train = trains.get(uuid);
            if (train.graph != this) continue;
            train.graph = toOther;
        }
    }

    public Set<TrackGraph> findDisconnectedGraphs(@Nullable LevelAccessor level, @Nullable Map<Integer, Pair<Integer, UUID>> splitSubGraphs) {
        HashSet<TrackGraph> dicovered = new HashSet<TrackGraph>();
        HashSet<TrackNodeLocation> vertices = new HashSet<TrackNodeLocation>(this.nodes.keySet());
        ArrayList<TrackNodeLocation> frontier = new ArrayList<TrackNodeLocation>();
        TrackGraph target = null;
        while (!vertices.isEmpty()) {
            if (target != null) {
                dicovered.add(target);
            }
            TrackNodeLocation start = (TrackNodeLocation)((Object)vertices.stream().findFirst().get());
            frontier.add(start);
            vertices.remove((Object)start);
            while (!frontier.isEmpty()) {
                TrackNodeLocation current = (TrackNodeLocation)((Object)frontier.remove(0));
                TrackNode currentNode = this.locateNode(current);
                Map<TrackNode, TrackEdge> connections = this.getConnectionsFrom(currentNode);
                for (TrackNode connected : connections.keySet()) {
                    if (!vertices.remove((Object)connected.getLocation())) continue;
                    frontier.add(connected.getLocation());
                }
                if (target == null) continue;
                if (splitSubGraphs != null && splitSubGraphs.containsKey(currentNode.getNetId())) {
                    Pair<Integer, UUID> ids = splitSubGraphs.get(currentNode.getNetId());
                    target.setId((UUID)ids.getSecond());
                    target.netId = (Integer)ids.getFirst();
                }
                this.transfer(level, currentNode, target);
            }
            frontier.clear();
            target = new TrackGraph();
        }
        return dicovered;
    }

    public void setId(UUID id) {
        this.id = id;
        this.color = Color.rainbowColor((int)new Random(id.getLeastSignificantBits()).nextInt());
    }

    public void setNetId(int id) {
        this.netId = id;
    }

    public int getChecksum() {
        if (this.checksum == 0) {
            this.checksum = this.nodes.values().stream().collect(Collectors.summingInt(TrackNode::getNetId));
        }
        return this.checksum;
    }

    public void transfer(LevelAccessor level, TrackNode node, TrackGraph target) {
        target.addNode(node);
        target.invalidateBounds();
        TrackNodeLocation nodeLoc = node.getLocation();
        Map<TrackNode, TrackEdge> connections = this.getConnectionsFrom(node);
        Map<UUID, Train> trains = Create.RAILWAYS.sided((LevelAccessor)level).trains;
        if (!connections.isEmpty()) {
            target.connectionsByNode.put(node, connections);
            for (TrackEdge entry : connections.values()) {
                EdgeData edgeData = entry.getEdgeData();
                for (TrackEdgePoint trackEdgePoint : edgeData.getPoints()) {
                    target.edgePoints.put(trackEdgePoint.getType(), trackEdgePoint);
                    this.edgePoints.remove(trackEdgePoint.getType(), trackEdgePoint.getId());
                }
            }
        }
        if (level != null) {
            for (UUID uuid : trains.keySet()) {
                Train train = trains.get(uuid);
                if (train.graph != this || !train.isTravellingOn(node)) continue;
                train.graph = target;
            }
        }
        this.nodes.remove((Object)nodeLoc);
        this.nodesById.remove(node.getNetId());
        this.connectionsByNode.remove(node);
        this.invalidateBounds();
    }

    public boolean isEmpty() {
        return this.nodes.isEmpty();
    }

    public Map<TrackNode, TrackEdge> getConnectionsFrom(TrackNode node) {
        if (node == null) {
            return null;
        }
        return this.connectionsByNode.getOrDefault(node, new HashMap());
    }

    public TrackEdge getConnection(Couple<TrackNode> nodes) {
        Map<TrackNode, TrackEdge> connectionsFrom = this.getConnectionsFrom((TrackNode)nodes.getFirst());
        if (connectionsFrom == null) {
            return null;
        }
        return connectionsFrom.get(nodes.getSecond());
    }

    public void connectNodes(LevelAccessor reader, TrackNodeLocation.DiscoveredLocation location, TrackNodeLocation.DiscoveredLocation location2, @Nullable BezierConnection turn) {
        TrackNode node1 = this.nodes.get((Object)location);
        TrackNode node2 = this.nodes.get((Object)location2);
        boolean bezier = turn != null;
        TrackMaterial material = bezier ? turn.getMaterial() : location2.materialA;
        TrackEdge edge = new TrackEdge(node1, node2, turn, material);
        TrackEdge edge2 = new TrackEdge(node2, node1, bezier ? turn.secondary() : null, material);
        for (TrackGraph graph : Create.RAILWAYS.trackNetworks.values()) {
            for (TrackNode otherNode1 : graph.nodes.values()) {
                Map<TrackNode, TrackEdge> connections = graph.connectionsByNode.get(otherNode1);
                if (connections == null) continue;
                for (Map.Entry<TrackNode, TrackEdge> entry : connections.entrySet()) {
                    TrackNode otherNode2 = entry.getKey();
                    TrackEdge otherEdge = entry.getValue();
                    if (graph == this && (otherNode1 == node1 || otherNode2 == node1 || otherNode1 == node2 || otherNode2 == node2) || edge == otherEdge || otherEdge.isInterDimensional() || edge.isInterDimensional() || node1.location.dimension != otherNode1.location.dimension || !bezier && !otherEdge.isTurn() || otherEdge.isTurn() && otherEdge.turn.isPrimary()) continue;
                    Collection<double[]> intersections = edge.getIntersection(node1, node2, otherEdge, otherNode1, otherNode2);
                    UUID id = UUID.randomUUID();
                    for (double[] intersection : intersections) {
                        double s = intersection[0];
                        double t = intersection[1];
                        edge.edgeData.addIntersection(this, id, s, otherNode1, otherNode2, t);
                        edge2.edgeData.addIntersection(this, id, edge.getLength() - s, otherNode1, otherNode2, t);
                        otherEdge.edgeData.addIntersection(graph, id, t, node1, node2, s);
                        TrackEdge otherEdge2 = graph.getConnection((Couple<TrackNode>)Couple.create((Object)otherNode2, (Object)otherNode1));
                        if (otherEdge2 == null) continue;
                        otherEdge2.edgeData.addIntersection(graph, id, otherEdge.getLength() - t, node1, node2, s);
                    }
                }
            }
        }
        this.putConnection(node1, node2, edge);
        this.putConnection(node2, node1, edge2);
        Create.RAILWAYS.sync.edgeAdded(this, node1, node2, edge);
        Create.RAILWAYS.sync.edgeAdded(this, node2, node1, edge2);
        this.markDirty();
    }

    public void disconnectNodes(TrackNode node1, TrackNode node2) {
        Map<TrackNode, TrackEdge> map1 = this.connectionsByNode.get(node1);
        Map<TrackNode, TrackEdge> map2 = this.connectionsByNode.get(node2);
        if (map1 != null) {
            map1.remove(node2);
        }
        if (map2 != null) {
            map2.remove(node1);
        }
    }

    public boolean putConnection(TrackNode node1, TrackNode node2, TrackEdge edge) {
        Map connections = this.connectionsByNode.computeIfAbsent(node1, n -> new IdentityHashMap());
        if (connections.containsKey(node2) && ((TrackEdge)connections.get(node2)).getEdgeData().hasPoints()) {
            return false;
        }
        return connections.put(node2, edge) == null;
    }

    public float distanceToLocationSqr(Level level, Vec3 location) {
        float nearest = Float.MAX_VALUE;
        for (TrackNodeLocation tnl : this.nodes.keySet()) {
            if (!Objects.equals(tnl.dimension, level.dimension())) continue;
            nearest = Math.min(nearest, (float)tnl.getLocation().distanceToSqr(location));
        }
        return nearest;
    }

    public void deferIntersectionUpdate(TrackEdge edge) {
        this.deferredIntersectionUpdates.add(edge);
    }

    public void resolveIntersectingEdgeGroups(Level level) {
        for (TrackEdge edge : this.deferredIntersectionUpdates) {
            if (!this.connectionsByNode.containsKey(edge.node1) || edge != this.connectionsByNode.get(edge.node1).get(edge.node2)) continue;
            EdgeData edgeData = edge.getEdgeData();
            for (TrackEdgeIntersection intersection : edgeData.getIntersections()) {
                TrackEdge otherEdge;
                UUID groupId = edgeData.getGroupAtPosition(this, intersection.location);
                Couple<TrackNodeLocation> target = intersection.target;
                TrackGraph graph = Create.RAILWAYS.getGraph((LevelAccessor)level, (TrackNodeLocation)((Object)target.getFirst()));
                if (graph == null) continue;
                TrackNode node1 = graph.locateNode((TrackNodeLocation)((Object)target.getFirst()));
                TrackNode node2 = graph.locateNode((TrackNodeLocation)((Object)target.getSecond()));
                Map<TrackNode, TrackEdge> connectionsFrom = graph.getConnectionsFrom(node1);
                if (connectionsFrom == null || (otherEdge = connectionsFrom.get(node2)) == null) continue;
                UUID otherGroupId = otherEdge.getEdgeData().getGroupAtPosition(graph, intersection.targetLocation);
                SignalEdgeGroup group = Create.RAILWAYS.signalEdgeGroups.get(groupId);
                SignalEdgeGroup otherGroup = Create.RAILWAYS.signalEdgeGroups.get(otherGroupId);
                if (group == null || otherGroup == null || groupId == null || otherGroupId == null) continue;
                intersection.groupId = groupId;
                group.putIntersection(intersection.id, otherGroupId);
                otherGroup.putIntersection(intersection.id, groupId);
            }
        }
        this.deferredIntersectionUpdates.clear();
    }

    public void markDirty() {
        Create.RAILWAYS.markTracksDirty();
    }

    public CompoundTag write(HolderLookup.Provider registries, DimensionPalette dimensions) {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("Id", this.id);
        tag.putInt("Color", this.color.getRGB());
        HashMap<TrackNode, Integer> indexTracker = new HashMap<TrackNode, Integer>();
        ListTag nodesList = new ListTag();
        int i = 0;
        for (TrackNode railNode : this.nodes.values()) {
            indexTracker.put(railNode, i);
            CompoundTag nodeTag = new CompoundTag();
            nodeTag.put("Location", (Tag)railNode.getLocation().write(dimensions));
            nodeTag.put("Normal", (Tag)VecHelper.writeNBT((Vec3)railNode.getNormal()));
            nodesList.add((Object)nodeTag);
            ++i;
        }
        this.connectionsByNode.forEach((node1, map) -> {
            Integer index1 = (Integer)indexTracker.get(node1);
            if (index1 == null) {
                return;
            }
            CompoundTag nodeTag = (CompoundTag)nodesList.get(index1.intValue());
            ListTag connectionsList = new ListTag();
            map.forEach((node2, edge) -> {
                CompoundTag connectionTag = new CompoundTag();
                Integer index2 = (Integer)indexTracker.get(node2);
                if (index2 == null) {
                    return;
                }
                connectionTag.putInt("To", index2.intValue());
                connectionTag.put("EdgeData", (Tag)edge.write(dimensions));
                connectionsList.add((Object)connectionTag);
            });
            nodeTag.put("Connections", (Tag)connectionsList);
        });
        tag.put("Nodes", (Tag)nodesList);
        tag.put("Points", (Tag)this.edgePoints.write(registries, dimensions));
        return tag;
    }

    public static TrackGraph read(CompoundTag tag, HolderLookup.Provider registries, DimensionPalette dimensions) {
        CompoundTag nodeTag;
        TrackGraph graph = new TrackGraph(tag.getUUID("Id"));
        graph.color = new Color(tag.getInt("Color"));
        graph.edgePoints.read(tag.getCompound("Points"), registries, dimensions);
        HashMap<Integer, TrackNode> indexTracker = new HashMap<Integer, TrackNode>();
        ListTag nodesList = tag.getList("Nodes", 10);
        int i = 0;
        for (Tag t : nodesList) {
            nodeTag = (CompoundTag)t;
            TrackNodeLocation location = TrackNodeLocation.read(nodeTag.getCompound("Location"), dimensions);
            Vec3 normal = VecHelper.readNBT((ListTag)nodeTag.getList("Normal", 6));
            graph.loadNode(location, TrackGraph.nextNodeId(), normal);
            indexTracker.put(i, graph.locateNode(location));
            ++i;
        }
        i = 0;
        for (Tag t : nodesList) {
            nodeTag = (CompoundTag)t;
            TrackNode node1 = (TrackNode)indexTracker.get(i);
            ++i;
            if (!nodeTag.contains("Connections")) continue;
            NBTHelper.iterateCompoundList((ListTag)nodeTag.getList("Connections", 10), c -> {
                TrackNode node2 = (TrackNode)indexTracker.get(c.getInt("To"));
                TrackEdge edge = TrackEdge.read(node1, node2, c.getCompound("EdgeData"), graph, dimensions);
                graph.putConnection(node1, node2, edge);
            });
        }
        return graph;
    }
}
