/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.graph;

import com.simibubi.create.AllPackets;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.EdgeData;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackGraphPacket;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackMaterial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public class TrackGraphSyncPacket
extends TrackGraphPacket {
    public static final StreamCodec<FriendlyByteBuf, TrackGraphSyncPacket> STREAM_CODEC = StreamCodec.of((b, v) -> v.write((FriendlyByteBuf)b), TrackGraphSyncPacket::new);
    Map<Integer, Pair<TrackNodeLocation, Vec3>> addedNodes;
    List<Pair<Pair<Couple<Integer>, TrackMaterial>, BezierConnection>> addedEdges;
    List<Integer> removedNodes;
    List<TrackEdgePoint> addedEdgePoints;
    List<UUID> removedEdgePoints;
    Map<Integer, Pair<Integer, UUID>> splitSubGraphs;
    Map<Couple<Integer>, Pair<Integer, List<UUID>>> updatedEdgeData;
    boolean fullWipe;
    static final int NULL_GROUP = 0;
    static final int PASSIVE_GROUP = 1;
    static final int GROUP = 2;

    public TrackGraphSyncPacket(UUID graphId, int netId) {
        this.graphId = graphId;
        this.netId = netId;
        this.addedNodes = new HashMap<Integer, Pair<TrackNodeLocation, Vec3>>();
        this.addedEdges = new ArrayList<Pair<Pair<Couple<Integer>, TrackMaterial>, BezierConnection>>();
        this.removedNodes = new ArrayList<Integer>();
        this.addedEdgePoints = new ArrayList<TrackEdgePoint>();
        this.removedEdgePoints = new ArrayList<UUID>();
        this.updatedEdgeData = new HashMap<Couple<Integer>, Pair<Integer, List<UUID>>>();
        this.splitSubGraphs = new HashMap<Integer, Pair<Integer, UUID>>();
        this.packetDeletesGraph = false;
    }

    public TrackGraphSyncPacket(FriendlyByteBuf buffer) {
        int i;
        this.graphId = buffer.readUUID();
        this.netId = buffer.readInt();
        this.packetDeletesGraph = buffer.readBoolean();
        this.fullWipe = buffer.readBoolean();
        if (this.packetDeletesGraph) {
            return;
        }
        DimensionPalette dimensions = DimensionPalette.receive(buffer);
        this.addedNodes = new HashMap<Integer, Pair<TrackNodeLocation, Vec3>>();
        this.addedEdges = new ArrayList<Pair<Pair<Couple<Integer>, TrackMaterial>, BezierConnection>>();
        this.addedEdgePoints = new ArrayList<TrackEdgePoint>();
        this.removedEdgePoints = new ArrayList<UUID>();
        this.removedNodes = new ArrayList<Integer>();
        this.splitSubGraphs = new HashMap<Integer, Pair<Integer, UUID>>();
        this.updatedEdgeData = new HashMap<Couple<Integer>, Pair<Integer, List<UUID>>>();
        int size = buffer.readVarInt();
        for (i = 0; i < size; ++i) {
            this.removedNodes.add(buffer.readVarInt());
        }
        size = buffer.readVarInt();
        for (i = 0; i < size; ++i) {
            this.addedNodes.put(buffer.readVarInt(), (Pair<TrackNodeLocation, Vec3>)Pair.of((Object)((Object)TrackNodeLocation.receive(buffer, dimensions)), (Object)VecHelper.read((FriendlyByteBuf)buffer)));
        }
        size = buffer.readVarInt();
        for (i = 0; i < size; ++i) {
            this.addedEdges.add((Pair<Pair<Couple<Integer>, TrackMaterial>, BezierConnection>)Pair.of((Object)Pair.of((Object)Couple.create(() -> ((FriendlyByteBuf)buffer).readVarInt()), (Object)TrackMaterial.deserialize(buffer.readUtf())), (Object)(buffer.readBoolean() ? new BezierConnection(buffer) : null)));
        }
        size = buffer.readVarInt();
        for (i = 0; i < size; ++i) {
            this.addedEdgePoints.add(EdgePointType.read(buffer, dimensions));
        }
        size = buffer.readVarInt();
        for (i = 0; i < size; ++i) {
            this.removedEdgePoints.add(buffer.readUUID());
        }
        size = buffer.readVarInt();
        for (i = 0; i < size; ++i) {
            ArrayList<UUID> list = new ArrayList<UUID>();
            Couple key = Couple.create(() -> ((FriendlyByteBuf)buffer).readInt());
            Pair entry = Pair.of((Object)buffer.readVarInt(), list);
            int size2 = buffer.readVarInt();
            for (int j = 0; j < size2; ++j) {
                list.add(buffer.readUUID());
            }
            this.updatedEdgeData.put((Couple<Integer>)key, (Pair<Integer, List<UUID>>)entry);
        }
        size = buffer.readVarInt();
        for (i = 0; i < size; ++i) {
            this.splitSubGraphs.put(buffer.readVarInt(), (Pair<Integer, UUID>)Pair.of((Object)buffer.readInt(), (Object)buffer.readUUID()));
        }
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.graphId);
        buffer.writeInt(this.netId);
        buffer.writeBoolean(this.packetDeletesGraph);
        buffer.writeBoolean(this.fullWipe);
        if (this.packetDeletesGraph) {
            return;
        }
        DimensionPalette dimensions = new DimensionPalette();
        this.addedNodes.forEach((node, loc) -> dimensions.encode(((TrackNodeLocation)((Object)((Object)loc.getFirst()))).dimension));
        this.addedEdgePoints.forEach(ep -> ep.edgeLocation.forEach(loc -> dimensions.encode(loc.dimension)));
        dimensions.send(buffer);
        buffer.writeVarInt(this.removedNodes.size());
        this.removedNodes.forEach(arg_0 -> ((FriendlyByteBuf)buffer).writeVarInt(arg_0));
        buffer.writeVarInt(this.addedNodes.size());
        this.addedNodes.forEach((node, loc) -> {
            buffer.writeVarInt(node.intValue());
            ((TrackNodeLocation)((Object)((Object)loc.getFirst()))).send(buffer, dimensions);
            VecHelper.write((Vec3)((Vec3)loc.getSecond()), (FriendlyByteBuf)buffer);
        });
        buffer.writeVarInt(this.addedEdges.size());
        this.addedEdges.forEach(pair -> {
            ((Couple)((Pair)pair.getFirst()).getFirst()).forEach(arg_0 -> ((FriendlyByteBuf)buffer).writeVarInt(arg_0));
            buffer.writeUtf(((TrackMaterial)((Pair)pair.getFirst()).getSecond()).id.toString());
            BezierConnection turn = (BezierConnection)pair.getSecond();
            buffer.writeBoolean(turn != null);
            if (turn != null) {
                turn.write(buffer);
            }
        });
        buffer.writeVarInt(this.addedEdgePoints.size());
        this.addedEdgePoints.forEach(ep -> ep.write(buffer, dimensions));
        buffer.writeVarInt(this.removedEdgePoints.size());
        this.removedEdgePoints.forEach(arg_0 -> ((FriendlyByteBuf)buffer).writeUUID(arg_0));
        buffer.writeVarInt(this.updatedEdgeData.size());
        for (Map.Entry<Couple<Integer>, Pair<Integer, List<UUID>>> entry : this.updatedEdgeData.entrySet()) {
            entry.getKey().forEach(arg_0 -> ((FriendlyByteBuf)buffer).writeInt(arg_0));
            Pair<Integer, List<UUID>> pair2 = entry.getValue();
            buffer.writeVarInt(((Integer)pair2.getFirst()).intValue());
            List list = (List)pair2.getSecond();
            buffer.writeVarInt(list.size());
            list.forEach(arg_0 -> ((FriendlyByteBuf)buffer).writeUUID(arg_0));
        }
        buffer.writeVarInt(this.splitSubGraphs.size());
        this.splitSubGraphs.forEach((node, p) -> {
            buffer.writeVarInt(node.intValue());
            buffer.writeInt(((Integer)p.getFirst()).intValue());
            buffer.writeUUID((UUID)p.getSecond());
        });
    }

    @Override
    protected void handle(GlobalRailwayManager manager, TrackGraph graph) {
        ++manager.version;
        if (this.packetDeletesGraph) {
            manager.removeGraph(graph);
            return;
        }
        if (this.fullWipe) {
            manager.removeGraph(graph);
            graph = Create.RAILWAYS.sided(null).getOrCreateGraph(this.graphId, this.netId);
        }
        Iterator<Object> iterator = this.removedNodes.iterator();
        while (iterator.hasNext()) {
            int n = iterator.next();
            TrackNode node = graph.getNode(n);
            if (node == null) continue;
            graph.removeNode(null, node.getLocation());
        }
        for (Map.Entry entry : this.addedNodes.entrySet()) {
            Integer nodeId = (Integer)entry.getKey();
            Pair nodeLocation = (Pair)entry.getValue();
            graph.loadNode((TrackNodeLocation)((Object)nodeLocation.getFirst()), nodeId, (Vec3)nodeLocation.getSecond());
        }
        for (Pair pair : this.addedEdges) {
            Couple nodes = ((Couple)((Pair)pair.getFirst()).getFirst()).map(graph::getNode);
            TrackNode node1 = (TrackNode)nodes.getFirst();
            TrackNode node2 = (TrackNode)nodes.getSecond();
            if (node1 == null || node2 == null) continue;
            graph.putConnection(node1, node2, new TrackEdge(node1, node2, (BezierConnection)pair.getSecond(), (TrackMaterial)((Pair)pair.getFirst()).getSecond()));
        }
        for (TrackEdgePoint trackEdgePoint : this.addedEdgePoints) {
            graph.edgePoints.put(trackEdgePoint.getType(), trackEdgePoint);
        }
        for (UUID uUID : this.removedEdgePoints) {
            for (EdgePointType<?> type : EdgePointType.TYPES.values()) {
                graph.edgePoints.remove(type, uUID);
            }
        }
        this.handleEdgeData(manager, graph);
        if (!this.splitSubGraphs.isEmpty()) {
            graph.findDisconnectedGraphs(null, this.splitSubGraphs).forEach(manager::putGraph);
        }
    }

    protected void handleEdgeData(GlobalRailwayManager manager, TrackGraph graph) {
        for (Map.Entry<Couple<Integer>, Pair<Integer, List<UUID>>> entry : this.updatedEdgeData.entrySet()) {
            int i;
            TrackEdge edge;
            List idList = (List)entry.getValue().getSecond();
            int groupType = (Integer)entry.getValue().getFirst();
            Couple nodes = entry.getKey().map(graph::getNode);
            if (nodes.either(Objects::isNull) || (edge = graph.getConnectionsFrom((TrackNode)nodes.getFirst()).get(nodes.getSecond())) == null) continue;
            EdgeData edgeData = new EdgeData(edge);
            if (groupType == 0) {
                edgeData.setSingleSignalGroup(null, null);
            } else if (groupType == 1) {
                edgeData.setSingleSignalGroup(null, EdgeData.passiveGroup);
            } else {
                edgeData.setSingleSignalGroup(null, (UUID)idList.get(0));
            }
            List<TrackEdgePoint> points = edgeData.getPoints();
            edge.edgeData = edgeData;
            int n = i = groupType == 2 ? 1 : 0;
            while (i < idList.size()) {
                UUID uuid = (UUID)idList.get(i);
                for (EdgePointType<?> type : EdgePointType.TYPES.values()) {
                    Object point = graph.edgePoints.get(type, uuid);
                    if (point == null) continue;
                    points.add((TrackEdgePoint)point);
                    break;
                }
                ++i;
            }
        }
    }

    public void syncEdgeData(TrackNode node1, TrackNode node2, TrackEdge edge) {
        int groupType;
        Couple key = Couple.create((Object)node1.getNetId(), (Object)node2.getNetId());
        ArrayList<UUID> list = new ArrayList<UUID>();
        EdgeData edgeData = edge.getEdgeData();
        int n = edgeData.hasSignalBoundaries() ? 0 : (groupType = EdgeData.passiveGroup.equals(edgeData.getSingleSignalGroup()) ? 1 : 2);
        if (groupType == 2) {
            list.add(edgeData.getSingleSignalGroup());
        }
        for (TrackEdgePoint point : edgeData.getPoints()) {
            list.add(point.getId());
        }
        this.updatedEdgeData.put((Couple<Integer>)key, (Pair<Integer, List<UUID>>)Pair.of((Object)groupType, list));
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.SYNC_RAIL_GRAPH;
    }
}
