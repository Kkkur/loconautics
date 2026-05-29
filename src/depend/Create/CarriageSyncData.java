/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class CarriageSyncData {
    public List<Pair<Couple<Integer>, Float>> wheelLocations = new ArrayList<Pair<Couple<Integer>, Float>>(4);
    public Pair<Vec3, Couple<Vec3>> fallbackLocations = null;
    public float distanceToDestination;
    public boolean leadingCarriage = false;
    private Pair<Vec3, Couple<Vec3>> fallbackPointSnapshot = null;
    private TravellingPoint[] pointsToApproach;
    private float[] pointDistanceSnapshot = new float[4];
    private float destinationDistanceSnapshot = 0.0f;
    private int ticksSince = 0;
    private boolean isDirty;

    public CarriageSyncData() {
        this.pointsToApproach = new TravellingPoint[4];
        for (int i = 0; i < 4; ++i) {
            this.wheelLocations.add(null);
            this.pointsToApproach[i] = new TravellingPoint();
        }
    }

    public CarriageSyncData(FriendlyByteBuf buf) {
        this();
        this.read(buf);
    }

    public CarriageSyncData copy() {
        CarriageSyncData data = new CarriageSyncData();
        for (int i = 0; i < 4; ++i) {
            data.wheelLocations.set(i, this.wheelLocations.get(i));
        }
        if (this.fallbackLocations != null) {
            data.fallbackLocations = this.fallbackLocations.copy();
        }
        data.distanceToDestination = this.distanceToDestination;
        data.leadingCarriage = this.leadingCarriage;
        return data;
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.leadingCarriage);
        buffer.writeBoolean(this.fallbackLocations != null);
        if (this.fallbackLocations != null) {
            Vec3 contraptionAnchor = (Vec3)this.fallbackLocations.getFirst();
            Couple rotationAnchors = (Couple)this.fallbackLocations.getSecond();
            VecHelper.write((Vec3)contraptionAnchor, (FriendlyByteBuf)buffer);
            VecHelper.write((Vec3)((Vec3)rotationAnchors.getFirst()), (FriendlyByteBuf)buffer);
            VecHelper.write((Vec3)((Vec3)rotationAnchors.getSecond()), (FriendlyByteBuf)buffer);
            return;
        }
        for (Pair<Couple<Integer>, Float> pair : this.wheelLocations) {
            buffer.writeBoolean(pair == null);
            if (pair == null) break;
            ((Couple)pair.getFirst()).forEach(arg_0 -> ((FriendlyByteBuf)buffer).writeInt(arg_0));
            buffer.writeFloat(((Float)pair.getSecond()).floatValue());
        }
        buffer.writeFloat(this.distanceToDestination);
    }

    public void read(FriendlyByteBuf buffer) {
        this.leadingCarriage = buffer.readBoolean();
        boolean fallback = buffer.readBoolean();
        this.ticksSince = 0;
        if (fallback) {
            this.fallbackLocations = Pair.of((Object)VecHelper.read((FriendlyByteBuf)buffer), (Object)Couple.create((Object)VecHelper.read((FriendlyByteBuf)buffer), (Object)VecHelper.read((FriendlyByteBuf)buffer)));
            return;
        }
        this.fallbackLocations = null;
        for (int i = 0; i < 4 && !buffer.readBoolean(); ++i) {
            this.wheelLocations.set(i, (Pair<Couple<Integer>, Float>)Pair.of((Object)Couple.create(() -> ((FriendlyByteBuf)buffer).readInt()), (Object)Float.valueOf(buffer.readFloat())));
        }
        this.distanceToDestination = buffer.readFloat();
    }

    public void update(CarriageContraptionEntity entity, Carriage carriage) {
        Carriage.DimensionalCarriageEntity dce = carriage.getDimensional(entity.level());
        TrackGraph graph = carriage.train.graph;
        if (graph == null) {
            this.updateFallbackLocations(dce);
            return;
        }
        this.fallbackLocations = null;
        this.leadingCarriage = entity.carriageIndex == (carriage.train.speed >= 0.0 ? 0 : carriage.train.carriages.size() - 1);
        for (boolean first : Iterate.trueAndFalse) {
            if (!first && !carriage.isOnTwoBogeys()) break;
            CarriageBogey bogey = (CarriageBogey)carriage.bogeys.get(first);
            for (boolean firstPoint : Iterate.trueAndFalse) {
                TravellingPoint point = (TravellingPoint)bogey.points.get(firstPoint);
                int index = (first ? 0 : 2) + (firstPoint ? 0 : 1);
                Couple nodes = Couple.create((Object)point.node1, (Object)point.node2);
                if (nodes.either(Objects::isNull)) {
                    this.updateFallbackLocations(dce);
                    return;
                }
                this.wheelLocations.set(index, (Pair<Couple<Integer>, Float>)Pair.of((Object)nodes.map(TrackNode::getNetId), (Object)Float.valueOf((float)point.position)));
            }
        }
        this.distanceToDestination = (float)carriage.train.navigation.distanceToDestination;
        this.setDirty(true);
    }

    private void updateFallbackLocations(Carriage.DimensionalCarriageEntity dce) {
        this.fallbackLocations = Pair.of((Object)dce.positionAnchor, dce.rotationAnchors);
        dce.pointsInitialised = true;
        this.setDirty(true);
    }

    public void apply(CarriageContraptionEntity entity, Carriage carriage) {
        Pair<Couple<Integer>, Float> pair;
        Carriage.DimensionalCarriageEntity dce = carriage.getDimensional(entity.level());
        this.fallbackPointSnapshot = null;
        if (this.fallbackLocations != null) {
            this.fallbackPointSnapshot = Pair.of((Object)dce.positionAnchor, dce.rotationAnchors);
            dce.pointsInitialised = true;
            return;
        }
        TrackGraph graph = carriage.train.graph;
        if (graph == null) {
            return;
        }
        for (int i = 0; i < this.wheelLocations.size() && (pair = this.wheelLocations.get(i)) != null; ++i) {
            TrackEdge edge;
            CarriageBogey bogey = (CarriageBogey)carriage.bogeys.get(i / 2 == 0);
            TravellingPoint bogeyPoint = (TravellingPoint)bogey.points.get(i % 2 == 0);
            TravellingPoint point = dce.pointsInitialised ? this.pointsToApproach[i] : bogeyPoint;
            Couple nodes = ((Couple)pair.getFirst()).map(graph::getNode);
            if (nodes.either(Objects::isNull) || (edge = graph.getConnectionsFrom((TrackNode)nodes.getFirst()).get(nodes.getSecond())) == null) continue;
            point.node1 = (TrackNode)nodes.getFirst();
            point.node2 = (TrackNode)nodes.getSecond();
            point.edge = edge;
            point.position = ((Float)pair.getSecond()).floatValue();
            if (!dce.pointsInitialised) continue;
            float foundDistance = -1.0f;
            boolean direction = false;
            for (boolean forward : Iterate.trueAndFalse) {
                float distanceTo = this.getDistanceTo(graph, bogeyPoint, point, foundDistance, forward);
                if (!(distanceTo > 0.0f) || foundDistance != -1.0f && !(distanceTo < foundDistance)) continue;
                foundDistance = distanceTo;
                direction = forward;
            }
            if (foundDistance != -1.0f) {
                this.pointDistanceSnapshot[i] = (float)(direction ? 1 : -1) * foundDistance;
                continue;
            }
            bogeyPoint.node1 = point.node1;
            bogeyPoint.node2 = point.node2;
            bogeyPoint.edge = point.edge;
            bogeyPoint.position = point.position;
            this.pointDistanceSnapshot[i] = 0.0f;
        }
        if (!dce.pointsInitialised) {
            carriage.train.navigation.distanceToDestination = this.distanceToDestination;
            dce.pointsInitialised = true;
            return;
        }
        if (!this.leadingCarriage) {
            return;
        }
        this.destinationDistanceSnapshot = (float)((double)this.distanceToDestination - carriage.train.navigation.distanceToDestination);
    }

    public void approach(CarriageContraptionEntity entity, Carriage carriage, float partialIn) {
        Carriage.DimensionalCarriageEntity dce = carriage.getDimensional(entity.level());
        int updateInterval = entity.getType().updateInterval();
        if (this.ticksSince >= updateInterval * 2) {
            partialIn /= (float)(this.ticksSince - updateInterval * 2 + 1);
        }
        float partial = partialIn *= ServerSpeedProvider.get();
        ++this.ticksSince;
        if (this.fallbackLocations != null && this.fallbackPointSnapshot != null) {
            dce.positionAnchor = this.approachVector(partial, dce.positionAnchor, (Vec3)this.fallbackLocations.getFirst(), (Vec3)this.fallbackPointSnapshot.getFirst());
            dce.rotationAnchors.replaceWithContext((current, first) -> this.approachVector(partial, (Vec3)current, (Vec3)((Couple)this.fallbackLocations.getSecond()).get(first.booleanValue()), (Vec3)((Couple)this.fallbackPointSnapshot.getSecond()).get(first.booleanValue())));
            return;
        }
        TrackGraph graph = carriage.train.graph;
        if (graph == null) {
            return;
        }
        carriage.train.navigation.distanceToDestination += (double)(partial * this.destinationDistanceSnapshot);
        for (boolean first2 : Iterate.trueAndFalse) {
            if (!first2 && !carriage.isOnTwoBogeys()) break;
            CarriageBogey bogey = (CarriageBogey)carriage.bogeys.get(first2);
            boolean[] blArray = Iterate.trueAndFalse;
            int n = blArray.length;
            for (int i = 0; i < n; ++i) {
                boolean firstPoint;
                int index = (first2 ? 0 : 2) + ((firstPoint = blArray[i]) ? 0 : 1);
                float f = this.pointDistanceSnapshot[index];
                if (Mth.equal((float)f, (float)0.0f)) continue;
                TravellingPoint point = (TravellingPoint)bogey.points.get(firstPoint);
                MutableBoolean success = new MutableBoolean(true);
                TravellingPoint toApproach = this.pointsToApproach[index];
                TravellingPoint.ITrackSelector trackSelector = point.follow(toApproach, b -> success.setValue(success.booleanValue() && b != false));
                point.travel(graph, partial * f, trackSelector);
                if (success.booleanValue()) continue;
                point.node1 = toApproach.node1;
                point.node2 = toApproach.node2;
                point.edge = toApproach.edge;
                point.position = toApproach.position;
                this.pointDistanceSnapshot[index] = 0.0f;
            }
        }
    }

    private Vec3 approachVector(float partial, Vec3 current, Vec3 target, Vec3 snapshot) {
        if (current == null || snapshot == null) {
            return target;
        }
        return current.add(target.subtract(snapshot).scale((double)partial));
    }

    public float getDistanceTo(TrackGraph graph, TravellingPoint current, TravellingPoint target, float maxDistance, boolean forward) {
        if (maxDistance == -1.0f) {
            maxDistance = 32.0f;
        }
        HashSet<TrackEdge> visited = new HashSet<TrackEdge>();
        IdentityHashMap<TrackEdge, Pair> reachedVia = new IdentityHashMap<TrackEdge, Pair>();
        PriorityQueue<Pair> frontier = new PriorityQueue<Pair>((p1, p2) -> Double.compare((Double)p1.getFirst(), (Double)p2.getFirst()));
        TrackNode initialNode1 = forward ? current.node1 : current.node2;
        TrackNode initialNode2 = forward ? current.node2 : current.node1;
        Map<TrackNode, TrackEdge> connectionsFromInitial = graph.getConnectionsFrom(initialNode1);
        if (connectionsFromInitial == null) {
            return -1.0f;
        }
        TrackEdge initialEdge = connectionsFromInitial.get(initialNode2);
        if (initialEdge == null) {
            return -1.0f;
        }
        TrackNode targetNode1 = forward ? target.node1 : target.node2;
        TrackNode targetNode2 = forward ? target.node2 : target.node1;
        TrackEdge targetEdge = graph.getConnectionsFrom(targetNode1).get(targetNode2);
        double distanceToNode2 = forward ? initialEdge.getLength() - current.position : current.position;
        frontier.add(Pair.of((Object)distanceToNode2, (Object)Pair.of((Object)Couple.create((Object)initialNode1, (Object)initialNode2), (Object)initialEdge)));
        while (!frontier.isEmpty()) {
            Pair poll = (Pair)frontier.poll();
            double distance = (Double)poll.getFirst();
            Pair currentEntry = (Pair)poll.getSecond();
            TrackNode node2 = (TrackNode)((Couple)currentEntry.getFirst()).getSecond();
            TrackEdge edge = (TrackEdge)currentEntry.getSecond();
            if (edge == targetEdge) {
                return (float)(distance - (forward ? edge.getLength() - target.position : target.position));
            }
            if (distance > (double)maxDistance) continue;
            ArrayList<Map.Entry<TrackNode, TrackEdge>> validTargets = new ArrayList<Map.Entry<TrackNode, TrackEdge>>();
            Map<TrackNode, TrackEdge> connectionsFrom = graph.getConnectionsFrom(node2);
            for (Map.Entry<TrackNode, TrackEdge> entry : connectionsFrom.entrySet()) {
                Vec3 newDirection;
                TrackEdge newEdge = entry.getValue();
                Vec3 currentDirection = edge.getDirection(false);
                if (currentDirection.dot(newDirection = newEdge.getDirection(true)) < 0.875 || !visited.add(entry.getValue())) continue;
                validTargets.add(entry);
            }
            if (validTargets.isEmpty()) continue;
            for (Map.Entry<TrackNode, TrackEdge> entry : validTargets) {
                TrackNode newNode = entry.getKey();
                TrackEdge newEdge = entry.getValue();
                reachedVia.put(newEdge, Pair.of((Object)(validTargets.size() > 1 ? 1 : 0), (Object)edge));
                frontier.add(Pair.of((Object)(newEdge.getLength() + distance), (Object)Pair.of((Object)Couple.create((Object)node2, (Object)newNode), (Object)newEdge)));
            }
        }
        return -1.0f;
    }

    public void setDirty(boolean dirty) {
        this.isDirty = dirty;
    }

    public boolean isDirty() {
        return this.isDirty;
    }
}
