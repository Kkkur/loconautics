/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.EdgeData;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TravellingPoint {
    public TrackNode node1;
    public TrackNode node2;
    public TrackEdge edge;
    public double position;
    public boolean blocked;
    public boolean upsideDown;

    public TravellingPoint() {
    }

    public TravellingPoint(TrackNode node1, TrackNode node2, TrackEdge edge, double position, boolean upsideDown) {
        this.node1 = node1;
        this.node2 = node2;
        this.edge = edge;
        this.position = position;
        this.upsideDown = upsideDown;
    }

    public IEdgePointListener ignoreEdgePoints() {
        return (d, c) -> false;
    }

    public ITurnListener ignoreTurns() {
        return (d, c) -> {};
    }

    public IPortalListener ignorePortals() {
        return $ -> false;
    }

    public ITrackSelector random() {
        return (graph, pair) -> (Map.Entry)((List)pair.getSecond()).get(Create.RANDOM.nextInt(((List)pair.getSecond()).size()));
    }

    public ITrackSelector follow(TravellingPoint other) {
        return this.follow(other, null);
    }

    public ITrackSelector follow(TravellingPoint other, @Nullable Consumer<Boolean> success) {
        return (graph, pair) -> {
            List validTargets = (List)pair.getSecond();
            boolean forward = (Boolean)pair.getFirst();
            TrackNode target = forward ? other.node1 : other.node2;
            TrackNode secondary = forward ? other.node2 : other.node1;
            for (Map.Entry entry : validTargets) {
                if (entry.getKey() != target && entry.getKey() != secondary) continue;
                if (success != null) {
                    success.accept(true);
                }
                return entry;
            }
            ArrayList frontiers = new ArrayList(validTargets.size());
            ArrayList visiteds = new ArrayList(validTargets.size());
            for (Map.Entry validTarget : validTargets) {
                ArrayList<Map.Entry> e = new ArrayList<Map.Entry>();
                e.add(validTarget);
                frontiers.add(e);
                HashSet<TrackEdge> e2 = new HashSet<TrackEdge>();
                e2.add((TrackEdge)validTarget.getValue());
                visiteds.add(e2);
            }
            for (int i = 0; i < 20; ++i) {
                for (int j = 0; j < validTargets.size(); ++j) {
                    Map.Entry entry = (Map.Entry)validTargets.get(j);
                    List frontier = (List)frontiers.get(j);
                    if (frontier.isEmpty()) continue;
                    Map.Entry currentEntry = (Map.Entry)frontier.remove(0);
                    for (Map.Entry<TrackNode, TrackEdge> nextEntry : graph.getConnectionsFrom((TrackNode)currentEntry.getKey()).entrySet()) {
                        TrackEdge nextEdge = nextEntry.getValue();
                        if (!((Set)visiteds.get(j)).add(nextEdge) || !((TrackEdge)currentEntry.getValue()).canTravelTo(nextEdge)) continue;
                        TrackNode nextNode = nextEntry.getKey();
                        if (nextNode == target) {
                            if (success != null) {
                                success.accept(true);
                            }
                            return entry;
                        }
                        frontier.add(nextEntry);
                    }
                }
            }
            if (success != null) {
                success.accept(false);
            }
            return (Map.Entry)validTargets.get(0);
        };
    }

    public ITrackSelector steer(SteerDirection direction, Vec3 upNormal) {
        return (graph, pair) -> {
            List validTargets = (List)pair.getSecond();
            double closest = Double.MAX_VALUE;
            Map.Entry best = null;
            for (Map.Entry entry : validTargets) {
                Vec3 trajectory = this.edge.getDirection(false);
                Vec3 entryTrajectory = ((TrackEdge)entry.getValue()).getDirection(true);
                Vec3 normal = trajectory.cross(upNormal);
                double dot = normal.dot(entryTrajectory);
                double diff = Math.abs((double)direction.targetDot - dot);
                if (diff > closest) continue;
                closest = diff;
                best = entry;
            }
            if (best == null) {
                Create.LOGGER.warn("Couldn't find steer target, choosing first");
                return (Map.Entry)validTargets.get(0);
            }
            return best;
        };
    }

    public double travel(TrackGraph graph, double distance, ITrackSelector trackSelector) {
        return this.travel(graph, distance, trackSelector, this.ignoreEdgePoints());
    }

    public double travel(TrackGraph graph, double distance, ITrackSelector trackSelector, IEdgePointListener signalListener) {
        return this.travel(graph, distance, trackSelector, signalListener, this.ignoreTurns());
    }

    public double travel(TrackGraph graph, double distance, ITrackSelector trackSelector, IEdgePointListener signalListener, ITurnListener turnListener) {
        return this.travel(graph, distance, trackSelector, signalListener, turnListener, this.ignorePortals());
    }

    public double travel(TrackGraph graph, double distance, ITrackSelector trackSelector, IEdgePointListener signalListener, ITurnListener turnListener, IPortalListener portalListener) {
        double collectedDistance;
        this.blocked = false;
        if (this.edge == null) {
            return 0.0;
        }
        double edgeLength = this.edge.getLength();
        if (Mth.equal((double)distance, (double)0.0)) {
            return 0.0;
        }
        double prevPos = this.position;
        double traveled = distance;
        double currentT = edgeLength == 0.0 ? 0.0 : this.position / edgeLength;
        double incrementT = this.edge.incrementT(currentT, distance);
        this.position = incrementT * edgeLength;
        ArrayList<Map.Entry<TrackNode, TrackEdge>> validTargets = new ArrayList<Map.Entry<TrackNode, TrackEdge>>();
        boolean forward = distance > 0.0;
        Double blockedLocation = this.edgeTraversedFrom(graph, forward, signalListener, turnListener, prevPos, collectedDistance = forward ? -prevPos : -edgeLength + prevPos);
        if (blockedLocation != null) {
            this.position = blockedLocation;
            traveled = this.position - prevPos;
            return traveled;
        }
        if (forward) {
            while (this.position > edgeLength) {
                Map.Entry entry;
                validTargets.clear();
                for (Map.Entry<TrackNode, TrackEdge> entry2 : graph.getConnectionsFrom(this.node2).entrySet()) {
                    TrackEdge newEdge;
                    TrackNode newNode = entry2.getKey();
                    if (newNode == this.node1 || !this.edge.canTravelTo(newEdge = entry2.getValue())) continue;
                    validTargets.add(entry2);
                }
                if (validTargets.isEmpty()) {
                    traveled -= this.position - edgeLength;
                    this.position = edgeLength;
                    this.blocked = true;
                    break;
                }
                Map.Entry entry3 = entry = validTargets.size() == 1 ? (Map.Entry)validTargets.get(0) : (Map.Entry)trackSelector.apply(graph, Pair.of((Object)true, validTargets));
                if (((TrackEdge)entry.getValue()).getLength() == 0.0 && portalListener.test(Couple.create((Object)((Object)this.node2.getLocation()), (Object)((Object)((TrackNode)entry.getKey()).getLocation())))) {
                    traveled -= this.position - edgeLength;
                    this.position = edgeLength;
                    this.blocked = true;
                    break;
                }
                this.node1 = this.node2;
                this.node2 = (TrackNode)entry.getKey();
                this.edge = (TrackEdge)entry.getValue();
                this.position -= edgeLength;
                collectedDistance += edgeLength;
                if (this.edge.isTurn()) {
                    turnListener.accept(collectedDistance, this.edge);
                }
                if ((blockedLocation = this.edgeTraversedFrom(graph, forward, signalListener, turnListener, 0.0, collectedDistance)) != null) {
                    traveled -= this.position;
                    this.position = blockedLocation;
                    traveled += this.position;
                    break;
                }
                prevPos = 0.0;
                edgeLength = this.edge.getLength();
            }
        } else {
            while (this.position < 0.0) {
                validTargets.clear();
                Object entry = graph.getConnectionsFrom(this.node1).entrySet().iterator();
                while (entry.hasNext()) {
                    Map.Entry<TrackNode, TrackEdge> entry4 = entry.next();
                    TrackNode newNode = entry4.getKey();
                    if (newNode == this.node2 || !graph.getConnectionsFrom(newNode).get(this.node1).canTravelTo(this.edge)) continue;
                    validTargets.add(entry4);
                }
                if (validTargets.isEmpty()) {
                    traveled -= this.position;
                    this.position = 0.0;
                    this.blocked = true;
                } else {
                    Object object = entry = validTargets.size() == 1 ? (Map.Entry)validTargets.get(0) : (Map.Entry)trackSelector.apply(graph, Pair.of((Object)false, validTargets));
                    if (((TrackEdge)entry.getValue()).getLength() == 0.0 && portalListener.test(Couple.create((Object)((Object)((TrackNode)entry.getKey()).getLocation()), (Object)((Object)this.node1.getLocation())))) {
                        traveled -= this.position;
                        this.position = 0.0;
                        this.blocked = true;
                    } else {
                        this.node2 = this.node1;
                        this.node1 = (TrackNode)entry.getKey();
                        this.edge = graph.getConnectionsFrom(this.node1).get(this.node2);
                        edgeLength = this.edge.getLength();
                        this.position += edgeLength;
                        blockedLocation = this.edgeTraversedFrom(graph, forward, signalListener, turnListener, edgeLength, collectedDistance += edgeLength);
                        if (blockedLocation == null) continue;
                        traveled -= this.position;
                        this.position = blockedLocation;
                        traveled += this.position;
                    }
                }
                break;
            }
        }
        return traveled;
    }

    protected Double edgeTraversedFrom(TrackGraph graph, boolean forward, IEdgePointListener edgePointListener, ITurnListener turnListener, double prevPos, double totalDistance) {
        if (this.edge.isTurn()) {
            turnListener.accept(Math.max(0.0, totalDistance), this.edge);
        }
        double from = forward ? prevPos : this.position;
        double to = forward ? this.position : prevPos;
        EdgeData edgeData = this.edge.getEdgeData();
        List<TrackEdgePoint> edgePoints = edgeData.getPoints();
        double length = this.edge.getLength();
        for (int i = 0; i < edgePoints.size(); ++i) {
            double distance;
            int index = forward ? i : edgePoints.size() - i - 1;
            TrackEdgePoint nextBoundary = edgePoints.get(index);
            double locationOn = nextBoundary.getLocationOn(this.edge);
            double d = distance = forward ? locationOn : length - locationOn;
            if (forward ? locationOn < from || locationOn >= to : locationOn <= from || locationOn > to) continue;
            Couple nodes = Couple.create((Object)this.node1, (Object)this.node2);
            if (!edgePointListener.test(totalDistance + distance, Pair.of((Object)nextBoundary, (Object)(forward ? nodes : nodes.swap())))) continue;
            return locationOn;
        }
        return null;
    }

    public void reverse(TrackGraph graph) {
        TrackNode n = this.node1;
        this.node1 = this.node2;
        this.node2 = n;
        this.position = this.edge.getLength() - this.position;
        this.edge = graph.getConnectionsFrom(this.node1).get(this.node2);
    }

    public Vec3 getPosition(@Nullable TrackGraph trackGraph) {
        return this.getPosition(trackGraph, false);
    }

    public Vec3 getPosition(@Nullable TrackGraph trackGraph, boolean flipUpsideDown) {
        return this.getPositionWithOffset(trackGraph, 0.0, flipUpsideDown);
    }

    public Vec3 getPositionWithOffset(@Nullable TrackGraph trackGraph, double offset, boolean flipUpsideDown) {
        double t = (this.position + offset) / this.edge.getLength();
        return this.edge.getPosition(trackGraph, t).add(this.edge.getNormal(trackGraph, t).scale(this.upsideDown ^ flipUpsideDown ? -1.0 : 1.0));
    }

    public void migrateTo(List<TrackGraphLocation> locations) {
        TrackGraphLocation location = locations.remove(0);
        TrackGraph graph = location.graph;
        this.node1 = graph.locateNode((TrackNodeLocation)((Object)location.edge.getFirst()));
        this.node2 = graph.locateNode((TrackNodeLocation)((Object)location.edge.getSecond()));
        this.position = location.position;
        this.edge = graph.getConnectionsFrom(this.node1).get(this.node2);
    }

    public CompoundTag write(DimensionPalette dimensions) {
        CompoundTag tag = new CompoundTag();
        Couple nodes = Couple.create((Object)this.node1, (Object)this.node2);
        if (nodes.either(Objects::isNull)) {
            return tag;
        }
        tag.put("Nodes", (Tag)nodes.map(TrackNode::getLocation).serializeEach(loc -> loc.write(dimensions)));
        tag.putDouble("Position", this.position);
        tag.putBoolean("UpsideDown", this.upsideDown);
        return tag;
    }

    public static TravellingPoint read(CompoundTag tag, TrackGraph graph, DimensionPalette dimensions) {
        Couple locs;
        if (graph == null) {
            return new TravellingPoint(null, null, null, 0.0, false);
        }
        Couple couple = tag.contains("Nodes") ? Couple.deserializeEach((ListTag)tag.getList("Nodes", 10), c -> TrackNodeLocation.read(c, dimensions)).map(graph::locateNode) : (locs = Couple.create(null, null));
        if (locs.either(Objects::isNull)) {
            return new TravellingPoint(null, null, null, 0.0, false);
        }
        double position = tag.getDouble("Position");
        return new TravellingPoint((TrackNode)locs.getFirst(), (TrackNode)locs.getSecond(), graph.getConnectionsFrom((TrackNode)locs.getFirst()).get(locs.getSecond()), position, tag.getBoolean("UpsideDown"));
    }

    public static interface IEdgePointListener
    extends BiPredicate<Double, Pair<TrackEdgePoint, Couple<TrackNode>>> {
    }

    public static interface ITurnListener
    extends BiConsumer<Double, TrackEdge> {
    }

    public static interface IPortalListener
    extends Predicate<Couple<TrackNodeLocation>> {
    }

    public static interface ITrackSelector
    extends BiFunction<TrackGraph, Pair<Boolean, List<Map.Entry<TrackNode, TrackEdge>>>, Map.Entry<TrackNode, TrackEdge>> {
    }

    public static enum SteerDirection {
        NONE(0.0f),
        LEFT(-1.0f),
        RIGHT(1.0f);

        final float targetDot;

        private SteerDirection(float targetDot) {
            this.targetDot = targetDot;
        }
    }
}
