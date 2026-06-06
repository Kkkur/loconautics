package com.lycoris.loconautics.allsable;

import org.joml.Quaterniond;
import org.joml.Vector3d;

import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.entity.TravellingPoint.ITrackSelector;
import com.simibubi.create.content.trains.entity.TravellingPoint.SteerDirection;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;

import net.minecraft.world.phys.Vec3;

/**
 * Phase 2a of the "all-Sable" train: a carriage riding on <b>two</b> bogey {@link TravellingPoint}s
 * (leading + trailing) a fixed distance apart, from which we derive the full carriage <i>pose</i>
 * (position + orientation) via {@link RailPose} — still with no sub-level/entity, so the pose can be
 * eyeballed in isolation before Phase 2b drives a real body with it.
 *
 * <p>Mirrors Create's {@code Carriage}: both bogeys advance the same arc-length each tick (rigid spacing).
 * The leading bogey steers straight through junctions ({@code steer(NONE)}); the trailing bogey
 * {@link TravellingPoint#follow(TravellingPoint) follows} the leading one so it takes the same path at
 * switches. On a curve the straight-line (chord) distance between the bogeys shrinks slightly — exactly what
 * a real two-bogey carriage body does — while the along-rail distance stays {@code bogeySpacing}.
 */
public final class RailCarriage {

    private final TrackGraph graph;
    private final TravellingPoint leading;
    private final TravellingPoint trailing;
    private final ITrackSelector leadingSelector;
    private final ITrackSelector trailingSelector;
    private final double bogeySpacing;
    private double speed;
    private boolean stopped;

    private RailCarriage(TrackGraph graph, TravellingPoint leading, TravellingPoint trailing, Vec3 upNormal,
                         double bogeySpacing, double speed) {
        this.graph = graph;
        this.leading = leading;
        this.trailing = trailing;
        this.bogeySpacing = bogeySpacing;
        this.speed = speed;
        this.leadingSelector = leading.steer(SteerDirection.NONE, upNormal);
        this.trailingSelector = trailing.follow(leading);
    }

    /**
     * Builds a carriage whose leading bogey sits at {@code location} and whose trailing bogey is placed
     * {@code bogeySpacing} blocks behind it along the rail. Returns {@code null} if the edge can't be
     * resolved.
     */
    public static RailCarriage at(TrackGraphLocation location, Vec3 upNormal, double bogeySpacing, double speed) {
        TravellingPoint leading = RailFollower.pointAt(location);
        if (leading == null) {
            return null;
        }
        // Clone the leading point and walk it backwards along the rail to seat the trailing bogey.
        TravellingPoint trailing = new TravellingPoint(leading.node1, leading.node2, leading.edge,
                leading.position, leading.upsideDown);
        ITrackSelector backSteer = trailing.steer(SteerDirection.NONE, upNormal);
        trailing.travel(location.graph, -bogeySpacing, backSteer,
                trailing.ignoreEdgePoints(), trailing.ignoreTurns(), trailing.ignorePortals());
        return new RailCarriage(location.graph, leading, trailing, upNormal, bogeySpacing, speed);
    }

    /** Advances both bogeys by {@code speed} (rigid), stopping at a dead end. Returns the carriage centre. */
    public Vec3 tick() {
        if (!stopped && speed != 0.0) {
            leading.travel(graph, speed, leadingSelector,
                    leading.ignoreEdgePoints(), leading.ignoreTurns(), leading.ignorePortals());
            trailing.travel(graph, speed, trailingSelector,
                    trailing.ignoreEdgePoints(), trailing.ignoreTurns(), trailing.ignorePortals());
            if (leading.blocked || trailing.blocked) {
                stopped = true; // reaching a dead end with two rigid bogeys: just park (no bounce in Phase 2a)
            }
        }
        return leadingPos().add(trailingPos()).scale(0.5);
    }

    public Vec3 leadingPos() {
        return leading.getPosition(graph);
    }

    public Vec3 trailingPos() {
        return trailing.getPosition(graph);
    }

    /** Carriage centre (midpoint of the two bogeys). */
    public Vector3d center() {
        return RailPose.carriagePosition(leading, trailing, graph);
    }

    /** Carriage orientation (yaw/pitch from leading->trailing), matching Create's alignEntity. */
    public Quaterniond orientation() {
        return RailPose.carriageOrientation(leading, trailing, graph);
    }

    /** Unit forward (tangent) vector: from the trailing bogey toward the leading one. */
    public Vec3 forward() {
        Vec3 f = leadingPos().subtract(trailingPos());
        double len = f.length();
        return len < 1.0e-6 ? new Vec3(1, 0, 0) : f.scale(1.0 / len);
    }

    public boolean stopped() {
        return stopped;
    }

    public double bogeySpacing() {
        return bogeySpacing;
    }

    public double speed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
