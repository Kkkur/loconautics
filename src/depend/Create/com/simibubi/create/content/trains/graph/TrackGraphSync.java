/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.graph;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackGraphPacket;
import com.simibubi.create.content.trains.graph.TrackGraphRollCallPacket;
import com.simibubi.create.content.trains.graph.TrackGraphSyncPacket;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.signal.EdgeGroupColor;
import com.simibubi.create.content.trains.signal.SignalEdgeGroupPacket;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackMaterial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TrackGraphSync {
    List<TrackGraphPacket> queuedPackets = new ArrayList<TrackGraphPacket>();
    int rollCallIn;
    private TrackGraphSyncPacket currentGraphSyncPacket;
    private int currentPayload;

    public void serverTick() {
        this.flushGraphPacket();
        if (!this.queuedPackets.isEmpty()) {
            for (TrackGraphPacket packet : this.queuedPackets) {
                if (!packet.packetDeletesGraph && !Create.RAILWAYS.trackNetworks.containsKey(packet.graphId)) continue;
                CatnipServices.NETWORK.sendToAllClients((CustomPacketPayload)packet);
                this.rollCallIn = 3;
            }
            this.queuedPackets.clear();
        }
        if (this.rollCallIn <= 0) {
            return;
        }
        --this.rollCallIn;
        if (this.rollCallIn > 0) {
            return;
        }
        this.sendRollCall();
    }

    public void nodeAdded(TrackGraph graph, TrackNode node) {
        this.flushGraphPacket(graph);
        this.currentGraphSyncPacket.addedNodes.put(node.getNetId(), (Pair<TrackNodeLocation, Vec3>)Pair.of((Object)((Object)node.getLocation()), (Object)node.getNormal()));
        ++this.currentPayload;
    }

    public void edgeAdded(TrackGraph graph, TrackNode node1, TrackNode node2, TrackEdge edge) {
        this.flushGraphPacket(graph);
        this.currentGraphSyncPacket.addedEdges.add((Pair<Pair<Couple<Integer>, TrackMaterial>, BezierConnection>)Pair.of((Object)Pair.of((Object)Couple.create((Object)node1.getNetId(), (Object)node2.getNetId()), (Object)edge.getTrackMaterial()), (Object)edge.getTurn()));
        ++this.currentPayload;
    }

    public void pointAdded(TrackGraph graph, TrackEdgePoint point) {
        this.flushGraphPacket(graph);
        this.currentGraphSyncPacket.addedEdgePoints.add(point);
        ++this.currentPayload;
    }

    public void pointRemoved(TrackGraph graph, TrackEdgePoint point) {
        this.flushGraphPacket(graph);
        this.currentGraphSyncPacket.removedEdgePoints.add(point.getId());
        ++this.currentPayload;
    }

    public void nodeRemoved(TrackGraph graph, TrackNode node) {
        this.flushGraphPacket(graph);
        int nodeId = node.getNetId();
        if (this.currentGraphSyncPacket.addedNodes.remove(nodeId) == null) {
            this.currentGraphSyncPacket.removedNodes.add(nodeId);
        }
        this.currentGraphSyncPacket.addedEdges.removeIf(pair -> {
            Couple ids = (Couple)((Pair)pair.getFirst()).getFirst();
            return (Integer)ids.getFirst() == nodeId || (Integer)ids.getSecond() == nodeId;
        });
    }

    public void graphSplit(TrackGraph graph, Set<TrackGraph> additional) {
        this.flushGraphPacket(graph);
        additional.forEach(rg -> this.currentGraphSyncPacket.splitSubGraphs.put((Integer)rg.nodesById.keySet().stream().findFirst().get(), (Pair<Integer, UUID>)Pair.of((Object)rg.netId, (Object)rg.id)));
    }

    public void graphRemoved(TrackGraph graph) {
        this.flushGraphPacket(graph);
        this.currentGraphSyncPacket.packetDeletesGraph = true;
    }

    public void sendEdgeGroups(List<UUID> ids, List<EdgeGroupColor> colors, ServerPlayer player) {
        CatnipServices.NETWORK.sendToClient(player, (CustomPacketPayload)new SignalEdgeGroupPacket(ids, colors, true));
    }

    public void edgeGroupCreated(UUID id, EdgeGroupColor color) {
        CatnipServices.NETWORK.sendToAllClients((CustomPacketPayload)new SignalEdgeGroupPacket(id, color));
    }

    public void edgeGroupRemoved(UUID id) {
        CatnipServices.NETWORK.sendToAllClients((CustomPacketPayload)new SignalEdgeGroupPacket((List<UUID>)ImmutableList.of((Object)id), Collections.emptyList(), false));
    }

    public void edgeDataChanged(TrackGraph graph, TrackNode node1, TrackNode node2, TrackEdge edge) {
        this.flushGraphPacket(graph);
        this.currentGraphSyncPacket.syncEdgeData(node1, node2, edge);
        ++this.currentPayload;
    }

    public void edgeDataChanged(TrackGraph graph, TrackNode node1, TrackNode node2, TrackEdge edge, TrackEdge edge2) {
        this.flushGraphPacket(graph);
        this.currentGraphSyncPacket.syncEdgeData(node1, node2, edge);
        this.currentGraphSyncPacket.syncEdgeData(node2, node1, edge2);
        ++this.currentPayload;
    }

    public void sendFullGraphTo(TrackGraph graph, ServerPlayer player) {
        TrackGraphSyncPacket currentPacket;
        TrackGraphSyncPacket packet = new TrackGraphSyncPacket(graph.id, graph.netId);
        packet.fullWipe = true;
        int sent = 0;
        HashSet<TrackEdgePoint> sentPoints = new HashSet<TrackEdgePoint>();
        for (TrackNode trackNode : graph.nodes.values()) {
            currentPacket = packet;
            currentPacket.addedNodes.put(trackNode.getNetId(), (Pair<TrackNodeLocation, Vec3>)Pair.of((Object)((Object)trackNode.getLocation()), (Object)trackNode.getNormal()));
            if (sent++ < 1000) continue;
            sent = 0;
            packet = this.flushAndCreateNew(graph, player, packet);
        }
        for (TrackNode trackNode : graph.nodes.values()) {
            currentPacket = packet;
            if (!graph.connectionsByNode.containsKey(trackNode)) continue;
            for (Map.Entry<TrackNode, TrackEdge> entry : graph.connectionsByNode.get(trackNode).entrySet()) {
                TrackNode node2 = entry.getKey();
                TrackEdge edge = entry.getValue();
                Couple key = Couple.create((Object)trackNode.getNetId(), (Object)node2.getNetId());
                currentPacket.addedEdges.add((Pair<Pair<Couple<Integer>, TrackMaterial>, BezierConnection>)Pair.of((Object)Pair.of((Object)key, (Object)edge.getTrackMaterial()), (Object)edge.getTurn()));
                currentPacket.syncEdgeData(trackNode, node2, edge);
                for (TrackEdgePoint point : edge.edgeData.getPoints()) {
                    if (sentPoints.contains(point)) continue;
                    sentPoints.add(point);
                    currentPacket.addedEdgePoints.add(point);
                    ++sent;
                }
            }
            if (sent++ < 1000) continue;
            sent = 0;
            packet = this.flushAndCreateNew(graph, player, packet);
        }
        for (EdgePointType edgePointType : EdgePointType.TYPES.values()) {
            for (TrackEdgePoint point : graph.getPoints(edgePointType)) {
                if (sentPoints.contains(point)) continue;
                sentPoints.add(point);
                packet.addedEdgePoints.add(point);
                if (sent++ < 1000) continue;
                sent = 0;
                packet = this.flushAndCreateNew(graph, player, packet);
            }
        }
        if (sent > 0) {
            this.flushAndCreateNew(graph, player, packet);
        }
    }

    private void sendRollCall() {
        CatnipServices.NETWORK.sendToAllClients((CustomPacketPayload)TrackGraphRollCallPacket.ofServer());
    }

    private TrackGraphSyncPacket flushAndCreateNew(TrackGraph graph, ServerPlayer player, TrackGraphSyncPacket packet) {
        CatnipServices.NETWORK.sendToClient(player, (CustomPacketPayload)packet);
        packet = new TrackGraphSyncPacket(graph.id, graph.netId);
        return packet;
    }

    private void flushGraphPacket() {
        this.flushGraphPacket(null, 0);
    }

    private void flushGraphPacket(TrackGraph graph) {
        this.flushGraphPacket(graph.id, graph.netId);
    }

    private void flushGraphPacket(@Nullable UUID graphId, int netId) {
        if (this.currentGraphSyncPacket != null) {
            if (this.currentGraphSyncPacket.graphId.equals(graphId) && this.currentPayload < 1000) {
                return;
            }
            this.queuedPackets.add(this.currentGraphSyncPacket);
            this.currentGraphSyncPacket = null;
            this.currentPayload = 0;
        }
        if (graphId != null) {
            this.currentGraphSyncPacket = new TrackGraphSyncPacket(graphId, netId);
            this.currentPayload = 0;
        }
    }
}
