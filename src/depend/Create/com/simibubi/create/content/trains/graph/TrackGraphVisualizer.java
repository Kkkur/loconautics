/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.graph;

import com.simibubi.create.AllKeys;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.graph.EdgeData;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.track.BezierConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TrackGraphVisualizer {
    public static void visualiseSignalEdgeGroups(TrackGraph graph) {
        Minecraft mc = Minecraft.getInstance();
        Entity cameraEntity = mc.cameraEntity;
        if (cameraEntity == null) {
            return;
        }
        AABB box = graph.getBounds((Level)mc.level).box;
        if (box == null || !box.intersects(cameraEntity.getBoundingBox().inflate(50.0))) {
            return;
        }
        Vec3 camera = cameraEntity.getEyePosition();
        Outliner outliner = Outliner.getInstance();
        Map<UUID, SignalEdgeGroup> allGroups = Create.RAILWAYS.sided(null).signalEdgeGroups;
        float width = 0.125f;
        for (Map.Entry<TrackNodeLocation, TrackNode> nodeEntry : graph.nodes.entrySet()) {
            Map<TrackNode, TrackEdge> map;
            Vec3 location;
            TrackNodeLocation nodeLocation = nodeEntry.getKey();
            TrackNode node = nodeEntry.getValue();
            if (nodeLocation == null || (location = nodeLocation.getLocation()).distanceTo(camera) > 50.0 || !mc.level.dimension().equals(nodeLocation.dimension) || (map = graph.connectionsByNode.get(node)) == null) continue;
            int hashCode = node.hashCode();
            for (Map.Entry<TrackNode, TrackEdge> entry : map.entrySet()) {
                UUID singleGroup;
                SignalEdgeGroup singleEdgeGroup;
                TrackNode other = entry.getKey();
                TrackEdge edge = entry.getValue();
                EdgeData signalData = edge.getEdgeData();
                if (!edge.node1.location.dimension.equals(edge.node2.location.dimension) || other.hashCode() > hashCode && other.location.getLocation().distanceTo(camera) <= 50.0) continue;
                Vec3 yOffset = new Vec3(0.0, (double)((float)(other.hashCode() > hashCode ? 6 : 5) / 64.0f), 0.0);
                Vec3 startPoint = edge.getPosition(graph, 0.0);
                Vec3 endPoint = edge.getPosition(graph, 1.0);
                if (!edge.isTurn()) {
                    if (signalData.hasSignalBoundaries()) {
                        double prev = 0.0;
                        double length = edge.getLength();
                        SignalBoundary prevBoundary = null;
                        SignalEdgeGroup group = null;
                        for (TrackEdgePoint trackEdgePoint : signalData.getPoints()) {
                            SignalBoundary boundary;
                            if (!(trackEdgePoint instanceof SignalBoundary)) continue;
                            prevBoundary = boundary = (SignalBoundary)trackEdgePoint;
                            group = allGroups.get(boundary.getGroup(node));
                            if (group == null) continue;
                            Vec3 vec3 = edge.getPosition(graph, prev + (prev == 0.0 ? 0.0 : 0.0625 / length)).add(yOffset);
                            prev = boundary.getLocationOn(edge) / length;
                            outliner.showLine((Object)Pair.of((Object)boundary, (Object)edge), vec3, edge.getPosition(graph, prev - 0.0625 / length).add(yOffset)).colored(group.color.get()).lineWidth(width);
                        }
                        if (prevBoundary != null) {
                            group = allGroups.get(prevBoundary.getGroup(other));
                            if (group == null) continue;
                            outliner.showLine((Object)edge, edge.getPosition(graph, prev + 0.0625 / length).add(yOffset), endPoint.add(yOffset)).colored(group.color.get()).lineWidth(width);
                            continue;
                        }
                    }
                    SignalEdgeGroup signalEdgeGroup = singleEdgeGroup = (singleGroup = signalData.getEffectiveEdgeGroupId(graph)) == null ? null : allGroups.get(singleGroup);
                    if (singleEdgeGroup == null) continue;
                    outliner.showLine((Object)edge, startPoint.add(yOffset), endPoint.add(yOffset)).colored(singleEdgeGroup.color.get()).lineWidth(width);
                    continue;
                }
                if (signalData.hasSignalBoundaries()) {
                    SignalEdgeGroup initialGroup;
                    UUID initialGroupId;
                    Iterator<TrackEdgePoint> points = signalData.getPoints().iterator();
                    SignalBoundary currentBoundary = null;
                    double currentBoundaryPosition = 0.0;
                    while (points.hasNext()) {
                        SignalBoundary signal;
                        TrackEdgePoint next = points.next();
                        if (!(next instanceof SignalBoundary)) continue;
                        currentBoundary = signal = (SignalBoundary)next;
                        currentBoundaryPosition = signal.getLocationOn(edge);
                        break;
                    }
                    if (currentBoundary == null || (initialGroupId = currentBoundary.getGroup(node)) == null || (initialGroup = allGroups.get(initialGroupId)) == null) continue;
                    Color currentColour = initialGroup.color.get();
                    Vec3 previous = null;
                    BezierConnection turn = edge.getTurn();
                    for (int i = 0; i <= turn.getSegmentCount(); ++i) {
                        double f = (float)i * 1.0f / (float)turn.getSegmentCount();
                        double position = f * turn.getLength();
                        Vec3 current = edge.getPosition(graph, f);
                        if (previous != null) {
                            if (currentBoundary != null && position > currentBoundaryPosition) {
                                current = edge.getPosition(graph, (currentBoundaryPosition - (double)width) / turn.getLength());
                                outliner.showLine((Object)Pair.of((Object)edge, (Object)previous), previous.add(yOffset), current.add(yOffset)).colored(currentColour).lineWidth(width);
                                previous = current = edge.getPosition(graph, (currentBoundaryPosition + (double)width) / turn.getLength());
                                UUID newId = currentBoundary.getGroup(other);
                                if (newId != null && allGroups.containsKey(newId)) {
                                    currentColour = allGroups.get((Object)newId).color.get();
                                }
                                currentBoundary = null;
                                while (points.hasNext()) {
                                    SignalBoundary signal;
                                    TrackEdgePoint next = points.next();
                                    if (!(next instanceof SignalBoundary)) continue;
                                    currentBoundary = signal = (SignalBoundary)next;
                                    currentBoundaryPosition = signal.getLocationOn(edge);
                                    break;
                                }
                            }
                            outliner.showLine((Object)Pair.of((Object)edge, (Object)previous), previous.add(yOffset), current.add(yOffset)).colored(currentColour).lineWidth(width);
                        }
                        previous = current;
                    }
                }
                SignalEdgeGroup signalEdgeGroup = singleEdgeGroup = (singleGroup = signalData.getEffectiveEdgeGroupId(graph)) == null ? null : allGroups.get(singleGroup);
                if (singleEdgeGroup == null) continue;
                Vec3 previous = null;
                BezierConnection turn = edge.getTurn();
                for (int i = 0; i <= turn.getSegmentCount(); ++i) {
                    Vec3 current = edge.getPosition(graph, (float)i * 1.0f / (float)turn.getSegmentCount());
                    if (previous != null) {
                        outliner.showLine((Object)Pair.of((Object)edge, (Object)previous), previous.add(yOffset), current.add(yOffset)).colored(singleEdgeGroup.color.get()).lineWidth(width);
                    }
                    previous = current;
                }
            }
        }
    }

    public static void debugViewGraph(TrackGraph graph, boolean extended) {
        Minecraft mc = Minecraft.getInstance();
        Entity cameraEntity = mc.cameraEntity;
        if (cameraEntity == null) {
            return;
        }
        AABB box = graph.getBounds((Level)mc.level).box;
        if (box == null || !box.intersects(cameraEntity.getBoundingBox().inflate(50.0))) {
            return;
        }
        Vec3 camera = cameraEntity.getEyePosition();
        for (Map.Entry<TrackNodeLocation, TrackNode> nodeEntry : graph.nodes.entrySet()) {
            Vec3 location;
            TrackNodeLocation nodeLocation = nodeEntry.getKey();
            TrackNode node = nodeEntry.getValue();
            if (nodeLocation == null || (location = nodeLocation.getLocation()).distanceTo(camera) > 50.0 || !mc.level.dimension().equals(nodeLocation.dimension)) continue;
            Vec3 yOffset = new Vec3(0.0, 0.1875, 0.0);
            Vec3 v1 = location.add(yOffset);
            Vec3 v2 = v1.add(node.normal.scale(0.1875));
            Outliner.getInstance().showLine((Object)node.netId, v1, v2).colored(Color.mixColors((Color)Color.WHITE, (Color)graph.color, (float)1.0f)).lineWidth(0.125f);
            Map<TrackNode, TrackEdge> map = graph.connectionsByNode.get(node);
            if (map == null) continue;
            int hashCode = node.hashCode();
            for (Map.Entry<TrackNode, TrackEdge> entry : map.entrySet()) {
                TrackNode other = entry.getKey();
                TrackEdge edge = entry.getValue();
                if (!edge.node1.location.dimension.equals(edge.node2.location.dimension)) {
                    v1 = location.add(yOffset);
                    v2 = v1.add(node.normal.scale(0.1875));
                    Outliner.getInstance().showLine((Object)node.netId, v1, v2).colored(Color.mixColors((Color)Color.WHITE, (Color)graph.color, (float)1.0f)).lineWidth(0.25f);
                    continue;
                }
                if (other.hashCode() > hashCode && !AllKeys.isKeyDown(341)) continue;
                yOffset = new Vec3(0.0, (double)((float)(other.hashCode() > hashCode ? 6 : 4) / 16.0f), 0.0);
                if (!edge.isTurn()) {
                    if (extended) {
                        Vec3 materialPos = edge.getPosition(graph, 0.5).add(0.0, 1.0, 0.0);
                        Outliner.getInstance().showItem((Object)Pair.of((Object)edge, (Object)edge.edgeData), materialPos, edge.getTrackMaterial().asStack());
                        Outliner.getInstance().showAABB((Object)edge.edgeData, AABB.ofSize((Vec3)materialPos, (double)0.25, (double)0.0, (double)0.25).move(0.0, -0.5, 0.0)).lineWidth(0.0625f).colored(graph.color);
                    }
                    Outliner.getInstance().showLine((Object)edge, edge.getPosition(graph, 0.0).add(yOffset), edge.getPosition(graph, 1.0).add(yOffset)).colored(graph.color).lineWidth(0.0625f);
                    continue;
                }
                Vec3 previous = null;
                BezierConnection turn = edge.getTurn();
                if (extended) {
                    Vec3 materialPos = edge.getPosition(graph, 0.5).add(0.0, 1.0, 0.0);
                    Outliner.getInstance().showItem((Object)Pair.of((Object)edge, (Object)edge.edgeData), materialPos, edge.getTrackMaterial().asStack());
                    Outliner.getInstance().showAABB((Object)edge.edgeData, AABB.ofSize((Vec3)materialPos, (double)0.25, (double)0.0, (double)0.25).move(0.0, -0.5, 0.0)).lineWidth(0.0625f).colored(graph.color);
                }
                for (int i = 0; i <= turn.getSegmentCount(); ++i) {
                    Vec3 current = edge.getPosition(graph, (float)i * 1.0f / (float)turn.getSegmentCount());
                    if (previous != null) {
                        Outliner.getInstance().showLine((Object)Pair.of((Object)edge, (Object)previous), previous.add(yOffset), current.add(yOffset)).colored(graph.color).lineWidth(0.0625f);
                    }
                    previous = current;
                }
            }
        }
    }
}
