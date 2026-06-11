package com.lycoris.loconautics.allsable;

import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.entity.TravellingPoint.ITrackSelector;
import com.simibubi.create.content.trains.entity.TravellingPoint.SteerDirection;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.graph.TrackNode;

import net.minecraft.world.phys.Vec3;

/**
 * Phase 1 of the "all-Sable" train: a single {@link TravellingPoint} that walks the Create track graph at a
 * constant speed, with no {@code CarriageContraptionEntity} involved. This is the proof-of-concept that the
 * rail-following math is reusable on its own — drive a debug marker along straight and curved track.
 *
 * <p>It mirrors how {@code TrainRelocator} probes the graph: build a point from a
 * {@link TrackGraphLocation}, then {@code travel()} each tick using a {@code steer(NONE)} selector so
 * junctions resolve deterministically (go straight). When the point runs into a dead end
 * ({@link TravellingPoint#blocked}) we {@link TravellingPoint#reverse(TrackGraph) reverse} it, so the demo
 * bounces back and forth along the line forever.
 */
public final class RailFollower {

    private final TrackGraph graph;
    private final TravellingPoint point;
    private final ITrackSelector selector;
    /** Blocks per tick (metres / tick). Always positive; direction is encoded in the point itself. */
    private double speed;
    /** Last valid world position, returned if the point falls off the graph (edge becomes null). */
    private Vec3 lastPos;

    private RailFollower(TrackGraph graph, TravellingPoint point, Vec3 upNormal, double speed) {
        this.graph = graph;
        this.point = point;
        this.speed = speed;
        // steer(NONE) keeps the point going straight through junctions; reads point.edge live each call.
        this.selector = point.steer(SteerDirection.NONE, upNormal);
        // Seed lastPos so position()/tick() never return null before the first successful advance.
        this.lastPos = point.edge != null ? point.getPosition(graph) : Vec3.ZERO;
    }

    /**
     * Builds a follower placed at {@code location} on the graph, or {@code null} if the edge can't be
     * resolved. {@code upNormal} is the track's up vector at the spawn block (world-up for flat track).
     */
    public static RailFollower at(TrackGraphLocation location, Vec3 upNormal, double speed) {
        TravellingPoint point = pointAt(location);
        if (point == null) {
            return null;
        }
        return new RailFollower(location.graph, point, upNormal, speed);
    }

    /**
     * Builds a fresh {@link TravellingPoint} sitting at {@code location} on its graph, or {@code null} if the
     * edge can't be resolved. Shared placement helper for the rail followers/carriages.
     */
    public static TravellingPoint pointAt(TrackGraphLocation location) {
        TrackGraph graph = location.graph;
        TrackNode node1 = graph.locateNode(location.edge.getFirst());
        TrackNode node2 = graph.locateNode(location.edge.getSecond());
        if (node1 == null || node2 == null) {
            return null;
        }
        TrackEdge edge = graph.getConnectionsFrom(node1).get(node2);
        if (edge == null) {
            return null;
        }
        return new TravellingPoint(node1, node2, edge, location.position, false);
    }

    /** Advances the point by {@code speed} and returns its new world position, reversing at dead ends. */
    public Vec3 tick() {
        // If the point ever loses its edge (e.g. the graph changed under it), don't crash — hold position.
        if (point.edge == null) {
            return lastPos;
        }
        point.travel(graph, speed, selector, point.ignoreEdgePoints(), point.ignoreTurns(), point.ignorePortals());
        if (point.blocked && point.edge != null) {
            point.reverse(graph);
        }
        if (point.edge == null) {
            return lastPos;
        }
        lastPos = point.getPosition(graph);
        return lastPos;
    }

    /** Current world position on the rail (no advance). */
    public Vec3 position() {
        return point.edge == null ? lastPos : point.getPosition(graph);
    }

    public double speed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
