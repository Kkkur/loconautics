/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.apache.commons.lang3.mutable.MutableDouble
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.graph.EdgeData;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.signal.SignalBlock;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackMaterial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.UUID;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

public class Navigation {
    public Train train;
    public GlobalStation destination;
    public double distanceToDestination;
    public double distanceStartedAt;
    public boolean destinationBehindTrain;
    public boolean announceArrival;
    List<Couple<TrackNode>> currentPath;
    private TravellingPoint signalScout;
    public Pair<UUID, Boolean> waitingForSignal;
    private Map<UUID, Pair<SignalBoundary, Boolean>> waitingForChainedGroups;
    public double distanceToSignal;
    public int ticksWaitingForSignal;

    public Navigation(Train train) {
        this.train = train;
        this.currentPath = new ArrayList<Couple<TrackNode>>();
        this.signalScout = new TravellingPoint();
        this.waitingForChainedGroups = new HashMap<UUID, Pair<SignalBoundary, Boolean>>();
    }

    public void tick(Level level) {
        double targetSpeed;
        double maxApproachSpeed;
        double speedRelativeToStation;
        if (this.destination == null) {
            return;
        }
        if (!this.train.runtime.paused) {
            boolean frontDriver = this.train.hasForwardConductor();
            boolean backDriver = this.train.hasBackwardConductor();
            if (this.destinationBehindTrain && !backDriver) {
                if (frontDriver) {
                    this.train.status.missingCorrectConductor();
                } else {
                    this.train.status.missingConductor();
                }
                this.cancelNavigation();
                return;
            }
            if (!this.destinationBehindTrain && !frontDriver) {
                this.train.status.missingConductor();
                this.cancelNavigation();
                return;
            }
            this.train.status.foundConductor();
        }
        this.destination.reserveFor(this.train);
        double acceleration = this.train.acceleration();
        double brakingDistance = this.train.speed * this.train.speed / (2.0 * acceleration);
        double speedMod = this.destinationBehindTrain ? -1.0 : 1.0;
        double preDepartureLookAhead = this.train.getCurrentStation() != null ? 4.5 : 0.0;
        double distanceToNextCurve = -1.0;
        if (this.train.graph != null) {
            TravellingPoint leadingPoint;
            if (this.waitingForSignal != null && this.currentSignalResolved()) {
                UUID signalId = (UUID)this.waitingForSignal.getFirst();
                SignalBoundary signal = this.train.graph.getPoint(EdgePointType.SIGNAL, signalId);
                if (signal != null && signal.types.get(((Boolean)this.waitingForSignal.getSecond()).booleanValue()) == SignalBlock.SignalType.CROSS_SIGNAL) {
                    this.waitingForChainedGroups.clear();
                }
                this.waitingForSignal = null;
            }
            TravellingPoint travellingPoint = leadingPoint = !this.destinationBehindTrain ? this.train.carriages.get(0).getLeadingPoint() : this.train.carriages.get(this.train.carriages.size() - 1).getTrailingPoint();
            if (this.waitingForSignal == null) {
                this.distanceToSignal = Double.MAX_VALUE;
                this.ticksWaitingForSignal = 0;
            }
            if (this.distanceToSignal > 0.0625) {
                MutableDouble curveDistanceTracker = new MutableDouble(-1.0);
                this.signalScout.node1 = leadingPoint.node1;
                this.signalScout.node2 = leadingPoint.node2;
                this.signalScout.edge = leadingPoint.edge;
                this.signalScout.position = leadingPoint.position;
                double brakingDistanceNoFlicker = brakingDistance + 3.0 - brakingDistance % 3.0;
                double scanDistance = Mth.clamp((double)brakingDistanceNoFlicker, (double)preDepartureLookAhead, (double)this.distanceToDestination);
                MutableDouble crossSignalDistanceTracker = new MutableDouble(-1.0);
                MutableObject trackingCrossSignal = new MutableObject(null);
                this.waitingForChainedGroups.clear();
                this.signalScout.travel(this.train.graph, (this.distanceToDestination + 50.0) * speedMod, this.controlSignalScout(), (distance, couple) -> {
                    boolean occupied;
                    boolean crossSignalTracked;
                    boolean bl = crossSignalTracked = trackingCrossSignal.getValue() != null;
                    if (!crossSignalTracked && distance > scanDistance) {
                        return true;
                    }
                    Couple nodes = (Couple)couple.getSecond();
                    TrackEdgePoint boundary = (TrackEdgePoint)couple.getFirst();
                    if (boundary == this.destination && ((GlobalStation)boundary).canApproachFrom((TrackNode)nodes.getSecond())) {
                        return true;
                    }
                    if (!(boundary instanceof SignalBoundary)) {
                        return false;
                    }
                    SignalBoundary signal = (SignalBoundary)boundary;
                    UUID entering = signal.getGroup((TrackNode)nodes.getSecond());
                    SignalEdgeGroup signalEdgeGroup = Create.RAILWAYS.signalEdgeGroups.get(entering);
                    if (signalEdgeGroup == null) {
                        return false;
                    }
                    boolean primary = entering.equals(signal.groups.getFirst());
                    boolean crossSignal = signal.types.get(primary) == SignalBlock.SignalType.CROSS_SIGNAL;
                    boolean bl2 = occupied = !this.train.manualTick && (signal.isForcedRed((TrackNode)nodes.getSecond()) || signalEdgeGroup.isOccupiedUnless(this.train));
                    if (!crossSignalTracked) {
                        if (crossSignal) {
                            trackingCrossSignal.setValue((Object)Pair.of((Object)boundary.id, (Object)primary));
                            crossSignalDistanceTracker.setValue((Number)distance);
                            this.waitingForChainedGroups.put(entering, (Pair<SignalBoundary, Boolean>)Pair.of((Object)signal, (Object)primary));
                        }
                        if (occupied) {
                            this.waitingForSignal = Pair.of((Object)boundary.id, (Object)primary);
                            this.distanceToSignal = distance;
                            if (!crossSignal) {
                                return true;
                            }
                        }
                        if (!occupied && !crossSignal && distance < this.distanceToSignal + 0.25 && distance < brakingDistanceNoFlicker) {
                            signalEdgeGroup.reserved = signal;
                        }
                        return false;
                    }
                    if (crossSignalTracked) {
                        this.waitingForChainedGroups.put(entering, (Pair<SignalBoundary, Boolean>)Pair.of((Object)signal, (Object)primary));
                        if (occupied) {
                            this.waitingForSignal = (Pair)trackingCrossSignal.getValue();
                            this.distanceToSignal = crossSignalDistanceTracker.doubleValue();
                            if (!crossSignal) {
                                return true;
                            }
                        }
                        if (!crossSignal) {
                            if (distance < this.distanceToSignal + 0.25) {
                                trackingCrossSignal.setValue(null);
                                this.reserveChain();
                                return false;
                            }
                            return true;
                        }
                    }
                    return false;
                }, (distance, edge) -> {
                    BezierConnection turn = edge.getTurn();
                    double vDistance = Math.abs(((Vec3)turn.starts.getFirst()).y - ((Vec3)turn.starts.getSecond()).y);
                    if (turn != null && vDistance > 0.0625 && ((Vec3)turn.axes.getFirst()).multiply(1.0, 0.0, 1.0).distanceTo(((Vec3)turn.axes.getSecond()).multiply(1.0, 0.0, 1.0).scale(-1.0)) < 0.015625 && vDistance / turn.getLength() < (double)0.225f) {
                        return;
                    }
                    float current = curveDistanceTracker.floatValue();
                    if (current == -1.0f || distance < (double)current) {
                        curveDistanceTracker.setValue((Number)distance);
                    }
                });
                if (trackingCrossSignal.getValue() != null && this.waitingForSignal == null) {
                    this.reserveChain();
                }
                distanceToNextCurve = curveDistanceTracker.floatValue();
            } else {
                ++this.ticksWaitingForSignal;
            }
        }
        double targetDistance = this.waitingForSignal != null ? this.distanceToSignal : this.distanceToDestination;
        if ((targetDistance += 0.25) > 0.03125 && this.train.getCurrentStation() != null) {
            if (this.waitingForSignal != null && this.distanceToSignal < preDepartureLookAhead) {
                ++this.ticksWaitingForSignal;
                return;
            }
            this.train.leaveStation();
        }
        this.train.currentlyBackwards = this.destinationBehindTrain;
        if (targetDistance < -10.0) {
            this.cancelNavigation();
            return;
        }
        if (targetDistance - Math.abs(this.train.speed) < 0.03125) {
            this.train.speed = Math.max(targetDistance, 0.03125) * speedMod;
            return;
        }
        this.train.burnFuel();
        double topSpeed = this.train.maxSpeed();
        if (targetDistance < 10.0 && (speedRelativeToStation = this.train.speed * speedMod) > (maxApproachSpeed = topSpeed * (targetDistance / 10.0))) {
            this.train.speed += (maxApproachSpeed - Math.abs(this.train.speed)) * 0.5 * speedMod;
            return;
        }
        double turnTopSpeed = Math.min(topSpeed *= this.train.throttle, (double)this.train.maxTurnSpeed());
        double d = targetSpeed = targetDistance > brakingDistance ? topSpeed * speedMod : 0.0;
        if (distanceToNextCurve != -1.0) {
            double targetTurnSpeed;
            double slowingDistance = brakingDistance - turnTopSpeed * turnTopSpeed / (2.0 * acceleration);
            double d2 = targetTurnSpeed = distanceToNextCurve > slowingDistance ? topSpeed * speedMod : turnTopSpeed * speedMod;
            if (Math.abs(targetTurnSpeed) < Math.abs(targetSpeed)) {
                targetSpeed = targetTurnSpeed;
            }
        }
        this.train.targetSpeed = targetSpeed;
        this.train.approachTargetSpeed(1.0f);
    }

    private void reserveChain() {
        this.train.reservedSignalBlocks.addAll(this.waitingForChainedGroups.keySet());
        this.waitingForChainedGroups.forEach((groupId, boundary) -> {
            SignalEdgeGroup signalEdgeGroup = Create.RAILWAYS.signalEdgeGroups.get(groupId);
            if (signalEdgeGroup != null) {
                signalEdgeGroup.reserved = (SignalBoundary)boundary.getFirst();
            }
        });
        this.waitingForChainedGroups.clear();
    }

    private boolean currentSignalResolved() {
        if (this.train.manualTick) {
            return true;
        }
        if (this.distanceToDestination < 0.5) {
            return true;
        }
        SignalBoundary signal = this.train.graph.getPoint(EdgePointType.SIGNAL, (UUID)this.waitingForSignal.getFirst());
        if (signal == null) {
            return true;
        }
        if (signal.types.get(((Boolean)this.waitingForSignal.getSecond()).booleanValue()) == SignalBlock.SignalType.CROSS_SIGNAL) {
            for (Map.Entry<UUID, Pair<SignalBoundary, Boolean>> entry : this.waitingForChainedGroups.entrySet()) {
                Pair<SignalBoundary, Boolean> boundary = entry.getValue();
                SignalEdgeGroup signalEdgeGroup = Create.RAILWAYS.signalEdgeGroups.get(entry.getKey());
                if (signalEdgeGroup == null) {
                    this.waitingForSignal.setFirst(null);
                    return true;
                }
                if (((SignalBoundary)boundary.getFirst()).isForcedRed((Boolean)boundary.getSecond())) {
                    this.train.reservedSignalBlocks.clear();
                    return false;
                }
                if (!signalEdgeGroup.isOccupiedUnless(this.train)) continue;
                return false;
            }
            return true;
        }
        UUID groupId = (UUID)signal.groups.get(((Boolean)this.waitingForSignal.getSecond()).booleanValue());
        if (groupId == null) {
            return true;
        }
        SignalEdgeGroup signalEdgeGroup = Create.RAILWAYS.signalEdgeGroups.get(groupId);
        if (signalEdgeGroup == null) {
            return true;
        }
        return !signalEdgeGroup.isOccupiedUnless(this.train);
    }

    public boolean isActive() {
        return this.destination != null;
    }

    public TravellingPoint.ITrackSelector control(TravellingPoint mp) {
        if (this.destination == null) {
            return mp.steer(this.train.manualSteer, new Vec3(0.0, 1.0, 0.0));
        }
        return (graph, pair) -> this.navigateOptions(this.currentPath, (TrackGraph)graph, (List)pair.getSecond());
    }

    public TravellingPoint.ITrackSelector controlSignalScout() {
        if (this.destination == null) {
            return this.signalScout.steer(this.train.manualSteer, new Vec3(0.0, 1.0, 0.0));
        }
        ArrayList<Couple<TrackNode>> pathCopy = new ArrayList<Couple<TrackNode>>(this.currentPath);
        return (graph, pair) -> this.navigateOptions((List<Couple<TrackNode>>)pathCopy, (TrackGraph)graph, (List)pair.getSecond());
    }

    private Map.Entry<TrackNode, TrackEdge> navigateOptions(List<Couple<TrackNode>> path, TrackGraph graph, List<Map.Entry<TrackNode, TrackEdge>> options) {
        if (path.isEmpty()) {
            return options.get(0);
        }
        Couple<TrackNode> nodes = path.get(0);
        TrackEdge targetEdge = graph.getConnection(nodes);
        for (Map.Entry<TrackNode, TrackEdge> entry : options) {
            if (entry.getValue() != targetEdge) continue;
            path.remove(0);
            return entry;
        }
        return options.get(0);
    }

    public void cancelNavigation() {
        this.distanceToDestination = 0.0;
        this.currentPath.clear();
        if (this.destination == null) {
            return;
        }
        this.destination.cancelReservation(this.train);
        this.destination = null;
        this.train.runtime.transitInterrupted();
        this.train.reservedSignalBlocks.clear();
    }

    public double startNavigation(DiscoveredPath pathTo) {
        boolean noneFound = pathTo == null;
        double distance = noneFound ? -1.0 : Math.abs(pathTo.distance);
        double cost = noneFound ? -1.0 : pathTo.cost;
        this.distanceToDestination = distance;
        if (noneFound) {
            this.distanceStartedAt = 0.0;
            this.distanceToDestination = 0.0;
            this.currentPath = new ArrayList<Couple<TrackNode>>();
            if (this.destination != null) {
                this.cancelNavigation();
            }
            return -1.0;
        }
        if (Math.abs(this.distanceToDestination) > 100.0) {
            this.announceArrival = true;
        }
        this.currentPath = pathTo.path;
        this.destinationBehindTrain = pathTo.distance < 0.0;
        this.train.reservedSignalBlocks.clear();
        this.train.navigation.waitingForSignal = null;
        if (this.destination == null) {
            this.distanceStartedAt = distance;
        }
        if (this.destination == pathTo.destination) {
            return 0.0;
        }
        if (!this.train.runtime.paused) {
            boolean frontDriver = this.train.hasForwardConductor();
            boolean backDriver = this.train.hasBackwardConductor();
            if (this.destinationBehindTrain && !backDriver) {
                if (frontDriver) {
                    this.train.status.missingCorrectConductor();
                } else {
                    this.train.status.missingConductor();
                }
                return -1.0;
            }
            if (!this.destinationBehindTrain && !frontDriver) {
                if (backDriver) {
                    this.train.status.missingCorrectConductor();
                } else {
                    this.train.status.missingConductor();
                }
                return -1.0;
            }
            this.train.status.foundConductor();
        }
        this.destination = pathTo.destination;
        return cost;
    }

    @Nullable
    public DiscoveredPath findPathTo(GlobalStation destination, double maxCost) {
        ArrayList<GlobalStation> destinations = new ArrayList<GlobalStation>();
        destinations.add(destination);
        return this.findPathTo(destinations, maxCost);
    }

    @Nullable
    public DiscoveredPath findPathTo(ArrayList<GlobalStation> destinations, double maxCost) {
        boolean canDriveBackward;
        TrackGraph graph = this.train.graph;
        if (graph == null) {
            return null;
        }
        Couple results = Couple.create(null, null);
        for (boolean forward : Iterate.trueAndFalse) {
            if (this.destination != null && this.destinationBehindTrain == forward) continue;
            TravellingPoint initialPoint = forward ? this.train.carriages.get(0).getLeadingPoint() : this.train.carriages.get(this.train.carriages.size() - 1).getTrailingPoint();
            TrackEdge initialEdge = forward ? initialPoint.edge : graph.getConnectionsFrom(initialPoint.node2).get(initialPoint.node1);
            this.search(Double.MAX_VALUE, maxCost, forward, destinations, (distance, cost, reachedVia, currentEntry, globalStation) -> {
                for (GlobalStation destination : destinations) {
                    if (globalStation != destination) continue;
                    TrackEdge edge = (TrackEdge)currentEntry.getSecond();
                    TrackNode node1 = (TrackNode)((Couple)currentEntry.getFirst()).getFirst();
                    TrackNode node2 = (TrackNode)((Couple)currentEntry.getFirst()).getSecond();
                    ArrayList<Couple<TrackNode>> currentPath = new ArrayList<Couple<TrackNode>>();
                    Pair backTrack = (Pair)reachedVia.get(edge);
                    Couple toReach = Couple.create((Object)node1, (Object)node2);
                    TrackEdge edgeReached = edge;
                    while (backTrack != null && edgeReached != initialEdge) {
                        if (((Boolean)backTrack.getFirst()).booleanValue()) {
                            currentPath.add(0, toReach);
                        }
                        toReach = (Couple)backTrack.getSecond();
                        edgeReached = graph.getConnection((Couple<TrackNode>)toReach);
                        backTrack = (Pair)reachedVia.get(edgeReached);
                    }
                    double position = edge.getLength() - destination.getLocationOn(edge);
                    double distanceToDestination = distance - position;
                    results.set(forward, (Object)new DiscoveredPath((double)(forward ? 1 : -1) * distanceToDestination, cost, currentPath, destination));
                    return true;
                }
                return false;
            });
        }
        DiscoveredPath front = (DiscoveredPath)results.getFirst();
        DiscoveredPath back = (DiscoveredPath)results.getSecond();
        boolean frontEmpty = front == null;
        boolean backEmpty = back == null;
        boolean canDriveForward = this.train.hasForwardConductor() || this.train.runtime.paused;
        boolean bl = canDriveBackward = this.train.doubleEnded && this.train.hasBackwardConductor() || this.train.runtime.paused;
        if (backEmpty || !canDriveBackward) {
            return canDriveForward ? front : null;
        }
        if (frontEmpty || !canDriveForward) {
            return canDriveBackward ? back : null;
        }
        boolean frontBetter = maxCost == -1.0 ? -back.distance > front.distance : back.cost > front.cost;
        return frontBetter ? front : back;
    }

    public GlobalStation findNearestApproachable(boolean forward) {
        TrackGraph graph = this.train.graph;
        if (graph == null) {
            return null;
        }
        MutableObject result = new MutableObject(null);
        double acceleration = this.train.acceleration();
        double minDistance = 0.75 * (this.train.speed * this.train.speed) / (2.0 * acceleration);
        double maxDistance = Math.max(32.0, 1.5 * (this.train.speed * this.train.speed) / (2.0 * acceleration));
        this.search(maxDistance, forward, null, (distance, cost, reachedVia, currentEntry, globalStation) -> {
            if (distance < minDistance) {
                return false;
            }
            TrackEdge edge = (TrackEdge)currentEntry.getSecond();
            double position = edge.getLength() - globalStation.getLocationOn(edge);
            if (distance - position < minDistance) {
                return false;
            }
            Train presentTrain = globalStation.getPresentTrain();
            if (presentTrain != null && presentTrain != this.train) {
                return false;
            }
            result.setValue((Object)globalStation);
            return true;
        });
        return (GlobalStation)result.getValue();
    }

    public void search(double maxDistance, boolean forward, ArrayList<GlobalStation> destinations, StationTest stationTest) {
        this.search(maxDistance, -1.0, forward, destinations, stationTest);
    }

    public void search(double maxDistance, double maxCost, boolean forward, ArrayList<GlobalStation> destinations, StationTest stationTest) {
        EdgeData initialSignalData;
        boolean costRelevant;
        TrackGraph graph = this.train.graph;
        if (graph == null) {
            return;
        }
        HashSet<TrackMaterial.TrackType> validTypes = new HashSet<TrackMaterial.TrackType>();
        for (int i = 0; i < this.train.carriages.size(); ++i) {
            Carriage carriage = this.train.carriages.get(i);
            if (i == 0) {
                validTypes.addAll(carriage.leadingBogey().type.getValidPathfindingTypes(carriage.leadingBogey().getStyle()));
            } else {
                validTypes.retainAll(carriage.leadingBogey().type.getValidPathfindingTypes(carriage.leadingBogey().getStyle()));
            }
            if (!carriage.isOnTwoBogeys()) continue;
            validTypes.retainAll(carriage.trailingBogey().type.getValidPathfindingTypes(carriage.trailingBogey().getStyle()));
        }
        if (validTypes.isEmpty()) {
            return;
        }
        IdentityHashMap penalties = new IdentityHashMap();
        boolean bl = costRelevant = maxCost >= 0.0;
        if (costRelevant) {
            for (Train otherTrain : Create.RAILWAYS.trains.values()) {
                if (otherTrain.graph != graph || otherTrain == this.train) continue;
                int navigationPenalty = otherTrain.getNavigationPenalty();
                otherTrain.getEndpointEdges().forEach(nodes -> {
                    if (nodes.either(Objects::isNull)) {
                        return;
                    }
                    for (boolean flip : Iterate.trueAndFalse) {
                        TrackEdge e = graph.getConnection((Couple<TrackNode>)(flip ? nodes.swap() : nodes));
                        if (e == null) continue;
                        int existing = penalties.getOrDefault(e, 0);
                        penalties.put(e, existing + navigationPenalty / 2);
                    }
                });
            }
        }
        TravellingPoint startingPoint = forward ? this.train.carriages.get(0).getLeadingPoint() : this.train.carriages.get(this.train.carriages.size() - 1).getTrailingPoint();
        HashSet<TrackEdge> visited = new HashSet<TrackEdge>();
        IdentityHashMap<TrackEdge, Pair<Boolean, Couple<TrackNode>>> reachedVia = new IdentityHashMap<TrackEdge, Pair<Boolean, Couple<TrackNode>>>();
        PriorityQueue<FrontierEntry> frontier = new PriorityQueue<FrontierEntry>();
        TrackNode initialNode1 = forward ? startingPoint.node1 : startingPoint.node2;
        TrackNode initialNode2 = forward ? startingPoint.node2 : startingPoint.node1;
        TrackEdge initialEdge = graph.getConnectionsFrom(initialNode1).get(initialNode2);
        if (initialEdge == null) {
            return;
        }
        double distanceToNode2 = forward ? initialEdge.getLength() - startingPoint.position : startingPoint.position;
        int signalWeight = Mth.clamp((int)(this.ticksWaitingForSignal * 2), (int)25, (int)200);
        int initialPenalty = 0;
        if (costRelevant) {
            initialPenalty += penalties.getOrDefault(initialEdge, 0).intValue();
        }
        if ((initialSignalData = initialEdge.getEdgeData()).hasPoints()) {
            for (TrackEdgePoint point : initialSignalData.getPoints()) {
                boolean isOwnStation;
                if (point.getLocationOn(initialEdge) < initialEdge.getLength() - distanceToNode2) continue;
                if (costRelevant && distanceToNode2 + (double)initialPenalty > maxCost) {
                    return;
                }
                if (!point.canNavigateVia(initialNode2)) {
                    return;
                }
                if (point instanceof SignalBoundary) {
                    SignalEdgeGroup signalEdgeGroup;
                    SignalBoundary signal = (SignalBoundary)point;
                    if (signal.isForcedRed(initialNode2)) {
                        initialPenalty += 400;
                        continue;
                    }
                    UUID group = signal.getGroup(initialNode2);
                    if (group == null || (signalEdgeGroup = Create.RAILWAYS.signalEdgeGroups.get(group)) == null) continue;
                    if (signalEdgeGroup.isOccupiedUnless(signal)) {
                        initialPenalty += signalWeight;
                        signalWeight /= 2;
                    }
                }
                if (!(point instanceof GlobalStation)) continue;
                GlobalStation station = (GlobalStation)point;
                Train presentTrain = station.getPresentTrain();
                boolean bl2 = isOwnStation = presentTrain == this.train;
                if (presentTrain != null && !isOwnStation) {
                    initialPenalty += 300;
                }
                if (station.canApproachFrom(initialNode2) && stationTest.test(distanceToNode2, distanceToNode2 + (double)initialPenalty, reachedVia, (Pair<Couple<TrackNode>, TrackEdge>)Pair.of((Object)Couple.create((Object)initialNode1, (Object)initialNode2), (Object)initialEdge), station)) {
                    return;
                }
                if (isOwnStation) continue;
                initialPenalty += 50;
            }
        }
        if (costRelevant && distanceToNode2 + (double)initialPenalty > maxCost) {
            return;
        }
        frontier.add(new FrontierEntry(this, distanceToNode2, initialPenalty, initialNode1, initialNode2, initialEdge));
        while (!frontier.isEmpty()) {
            TrackNode newNode;
            EdgeData signalData;
            FrontierEntry entry = (FrontierEntry)frontier.poll();
            if (!visited.add(entry.edge)) continue;
            double distance = entry.distance;
            int penalty = entry.penalty;
            if (distance > maxDistance) continue;
            TrackEdge edge = entry.edge;
            TrackNode node1 = entry.node1;
            TrackNode node2 = entry.node2;
            if (entry.hasDestination && (signalData = edge.getEdgeData()).hasPoints()) {
                for (TrackEdgePoint point : signalData.getPoints()) {
                    GlobalStation station;
                    if (!(point instanceof GlobalStation) || !(station = (GlobalStation)point).canApproachFrom(node2) || !stationTest.test(distance, penalty, reachedVia, (Pair<Couple<TrackNode>, TrackEdge>)Pair.of((Object)Couple.create((Object)node1, (Object)node2), (Object)edge), station)) continue;
                    return;
                }
            }
            ArrayList<Map.Entry<TrackNode, TrackEdge>> validTargets = new ArrayList<Map.Entry<TrackNode, TrackEdge>>();
            Map<TrackNode, TrackEdge> connectionsFrom = graph.getConnectionsFrom(node2);
            for (Map.Entry<TrackNode, TrackEdge> connection : connectionsFrom.entrySet()) {
                newNode = connection.getKey();
                if (newNode == node1 || !edge.canTravelTo(connection.getValue())) continue;
                validTargets.add(connection);
            }
            if (validTargets.isEmpty()) continue;
            block6: for (Map.Entry<TrackNode, TrackEdge> target : validTargets) {
                if (!validTypes.contains(target.getValue().getTrackMaterial().trackType)) continue;
                newNode = target.getKey();
                TrackEdge newEdge = target.getValue();
                int newPenalty = penalty;
                double edgeLength = newEdge.getLength();
                double newDistance = distance + edgeLength;
                if (costRelevant) {
                    newPenalty += penalties.getOrDefault(newEdge, 0).intValue();
                }
                boolean hasDestination = false;
                EdgeData signalData2 = newEdge.getEdgeData();
                if (signalData2.hasPoints()) {
                    for (TrackEdgePoint point : signalData2.getPoints()) {
                        boolean isOwnStation;
                        if (node2 == initialNode1 && point.getLocationOn(newEdge) < edgeLength - distanceToNode2) continue;
                        if (costRelevant && newDistance + (double)newPenalty > maxCost || !point.canNavigateVia(newNode)) continue block6;
                        if (point instanceof SignalBoundary) {
                            SignalEdgeGroup signalEdgeGroup;
                            SignalBoundary signal = (SignalBoundary)point;
                            if (signal.isForcedRed(newNode)) {
                                newPenalty += 400;
                                continue;
                            }
                            UUID group = signal.getGroup(newNode);
                            if (group == null || (signalEdgeGroup = Create.RAILWAYS.signalEdgeGroups.get(group)) == null) continue;
                            if (signalEdgeGroup.isOccupiedUnless(signal)) {
                                newPenalty += signalWeight;
                                signalWeight /= 2;
                            }
                        }
                        if (!(point instanceof GlobalStation)) continue;
                        GlobalStation station = (GlobalStation)point;
                        Train presentTrain = station.getPresentTrain();
                        boolean bl3 = isOwnStation = presentTrain == this.train;
                        if (presentTrain != null && !isOwnStation) {
                            newPenalty += 300;
                        }
                        if (station.canApproachFrom(newNode) && stationTest.test(newDistance, newDistance + (double)newPenalty, reachedVia, (Pair<Couple<TrackNode>, TrackEdge>)Pair.of((Object)Couple.create((Object)node2, (Object)newNode), (Object)newEdge), station)) {
                            hasDestination = true;
                            break;
                        }
                        if (isOwnStation) continue;
                        newPenalty += 50;
                    }
                }
                if (costRelevant && newDistance + (double)newPenalty > maxCost) continue;
                double remainingDist = 0.0;
                if (destinations != null && !destinations.isEmpty()) {
                    remainingDist = Double.MAX_VALUE;
                    Vec3 newNodePosition = newNode.getLocation().getLocation();
                    for (GlobalStation destination : destinations) {
                        double temp;
                        TrackNodeLocation destinationNode = (TrackNodeLocation)((Object)destination.edgeLocation.getFirst());
                        double dMin = Math.abs(newNodePosition.x - destinationNode.getLocation().x);
                        double dMid = Math.abs(newNodePosition.y - destinationNode.getLocation().y);
                        double dMax = Math.abs(newNodePosition.z - destinationNode.getLocation().z);
                        if (dMin > dMid) {
                            temp = dMid;
                            dMid = dMin;
                            dMin = temp;
                        }
                        if (dMin > dMax) {
                            temp = dMax;
                            dMax = dMin;
                            dMin = temp;
                        }
                        if (dMid > dMax) {
                            temp = dMax;
                            dMax = dMid;
                            dMid = temp;
                        }
                        double currentRemaining = 0.317837245195782 * dMin + 0.414213562373095 * dMid + dMax + destination.position;
                        if (node2.getLocation().equals((Object)destinationNode)) {
                            currentRemaining -= newEdge.getLength() * 2.0;
                        }
                        remainingDist = Math.min(remainingDist, currentRemaining);
                    }
                }
                reachedVia.putIfAbsent(newEdge, (Pair<Boolean, Couple<TrackNode>>)Pair.of((Object)(validTargets.size() > 1 ? 1 : 0), (Object)Couple.create((Object)node1, (Object)node2)));
                frontier.add(new FrontierEntry(this, newDistance, newPenalty, remainingDist, hasDestination, node2, newNode, newEdge));
            }
        }
    }

    public CompoundTag write(DimensionPalette dimensions) {
        CompoundTag tag = new CompoundTag();
        if (this.destination == null) {
            return tag;
        }
        this.removeBrokenPathEntries();
        tag.putUUID("Destination", this.destination.id);
        tag.putDouble("DistanceToDestination", this.distanceToDestination);
        tag.putDouble("DistanceStartedAt", this.distanceStartedAt);
        tag.putBoolean("BehindTrain", this.destinationBehindTrain);
        tag.putBoolean("AnnounceArrival", this.announceArrival);
        tag.put("Path", (Tag)NBTHelper.writeCompoundList(this.currentPath, c -> {
            CompoundTag nbt = new CompoundTag();
            nbt.put("Nodes", (Tag)c.map(TrackNode::getLocation).serializeEach(loc -> loc.write(dimensions)));
            return nbt;
        }));
        if (this.waitingForSignal == null) {
            return tag;
        }
        tag.putUUID("BlockingSignal", (UUID)this.waitingForSignal.getFirst());
        tag.putBoolean("BlockingSignalSide", ((Boolean)this.waitingForSignal.getSecond()).booleanValue());
        tag.putDouble("DistanceToSignal", this.distanceToSignal);
        tag.putInt("TicksWaitingForSignal", this.ticksWaitingForSignal);
        return tag;
    }

    public void read(CompoundTag tag, TrackGraph graph, DimensionPalette dimensions) {
        GlobalStation globalStation = this.destination = graph != null && tag.contains("Destination") ? graph.getPoint(EdgePointType.STATION, tag.getUUID("Destination")) : null;
        if (this.destination == null) {
            return;
        }
        this.distanceToDestination = tag.getDouble("DistanceToDestination");
        this.distanceStartedAt = tag.getDouble("DistanceStartedAt");
        this.destinationBehindTrain = tag.getBoolean("BehindTrain");
        this.announceArrival = tag.getBoolean("AnnounceArrival");
        this.currentPath.clear();
        NBTHelper.iterateCompoundList((ListTag)tag.getList("Path", 10), c -> this.currentPath.add((Couple<TrackNode>)Couple.deserializeEach((ListTag)c.getList("Nodes", 10), c2 -> TrackNodeLocation.read(c2, dimensions)).map(graph::locateNode)));
        this.removeBrokenPathEntries();
        Pair<UUID, Boolean> pair = this.waitingForSignal = tag.contains("BlockingSignal") ? Pair.of((Object)tag.getUUID("BlockingSignal"), (Object)tag.getBoolean("BlockingSignalSide")) : null;
        if (this.waitingForSignal == null) {
            return;
        }
        this.distanceToSignal = tag.getDouble("DistanceToSignal");
        this.ticksWaitingForSignal = tag.getInt("TicksWaitingForSignal");
    }

    private void removeBrokenPathEntries() {
        boolean nullEntriesPresent = false;
        Iterator<Couple<TrackNode>> iterator = this.currentPath.iterator();
        while (iterator.hasNext()) {
            Couple<TrackNode> couple = iterator.next();
            if (couple != null && couple.getFirst() != null && couple.getSecond() != null) continue;
            iterator.remove();
            nullEntriesPresent = true;
        }
        if (nullEntriesPresent) {
            Create.LOGGER.error("Found null values in path of train with name: " + this.train.name.getString() + ", id: " + this.train.id.toString());
        }
    }

    @FunctionalInterface
    public static interface StationTest {
        public boolean test(double var1, double var3, Map<TrackEdge, Pair<Boolean, Couple<TrackNode>>> var5, Pair<Couple<TrackNode>, TrackEdge> var6, GlobalStation var7);
    }

    private class FrontierEntry
    implements Comparable<FrontierEntry> {
        double distance;
        int penalty;
        double remaining;
        boolean hasDestination;
        TrackNode node1;
        TrackNode node2;
        TrackEdge edge;

        public FrontierEntry(Navigation navigation, double distance, int penalty, TrackNode node1, TrackNode node2, TrackEdge edge) {
            this.distance = distance;
            this.penalty = penalty;
            this.remaining = 0.0;
            this.hasDestination = false;
            this.node1 = node1;
            this.node2 = node2;
            this.edge = edge;
        }

        public FrontierEntry(Navigation navigation, double distance, int penalty, double remaining, boolean hasDestination, TrackNode node1, TrackNode node2, TrackEdge edge) {
            this.distance = distance;
            this.penalty = penalty;
            this.remaining = remaining;
            this.hasDestination = hasDestination;
            this.node1 = node1;
            this.node2 = node2;
            this.edge = edge;
        }

        @Override
        public int compareTo(FrontierEntry o) {
            return Double.compare(this.distance + (double)this.penalty + this.remaining, o.distance + (double)o.penalty + o.remaining);
        }
    }
}
