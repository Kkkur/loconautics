package com.lycoris.loconautics.allsable;

import org.joml.Matrix3d;
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

    /** Reference frame captured at spawn (rail tangent + up). Orientation is the delta from this to "now". */
    private Vector3d forward0;
    private Vector3d up0;

    private RailCarriage(TrackGraph graph, TravellingPoint leading, TravellingPoint trailing, Vec3 upNormal,
                         double bogeySpacing, double speed) {
        this.graph = graph;
        this.leading = leading;
        this.trailing = trailing;
        this.bogeySpacing = bogeySpacing;
        this.speed = speed;
        this.leadingSelector = leading.steer(SteerDirection.NONE, upNormal);
        this.trailingSelector = trailing.follow(leading);
        // Capture the reference frame now (bogeys are already placed). The car's blocks were assembled
        // world-aligned at this moment, so orientation must be identity here and a delta from now on.
        captureReference();
    }

    /**
     * Builds a carriage whose leading bogey sits at {@code location} and whose trailing bogey is placed
     * {@code bogeySpacing} blocks behind it along the rail. Returns {@code null} if the edge can't be
     * resolved.
     */
    public static RailCarriage at(TrackGraphLocation location, Vec3 upNormal, double bogeySpacing, double speed) {
        TravellingPoint seed = RailFollower.pointAt(location);
        if (seed == null) {
            return null;
        }
        // Place the two bogeys at the carriage ENDS, CENTRED on the spawn point: leading is half the span
        // ahead, trailing half behind. This mirrors Create (bogeys at the ends), so as the leading bogey
        // enters a curve the whole car yaws progressively instead of only pivoting once its centre arrives.
        double half = bogeySpacing / 2.0;
        TravellingPoint leading = new TravellingPoint(seed.node1, seed.node2, seed.edge, seed.position, seed.upsideDown);
        leading.travel(location.graph, half, leading.steer(SteerDirection.NONE, upNormal),
                leading.ignoreEdgePoints(), leading.ignoreTurns(), leading.ignorePortals());
        TravellingPoint trailing = new TravellingPoint(seed.node1, seed.node2, seed.edge, seed.position, seed.upsideDown);
        trailing.travel(location.graph, -half, trailing.steer(SteerDirection.NONE, upNormal),
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

    /**
     * Carriage orientation as the rotation from the spawn reference frame to the current rail frame.
     *
     * <p>We build an orthonormal basis {right, up, forward} both at spawn ({@link #forward0}/{@link #up0})
     * and now (forward = rail tangent, up = world-up projected perpendicular to forward), and return
     * {@code M_now · M_ref⁻¹}. At spawn this is identity (blocks stay world-aligned, as assembled); on a slope
     * it pitches, on a curve it yaws, with NO spurious roll — unlike the old Euler yaw/pitch which rolled the
     * car whenever it travelled along the world X axis (the ramp bug seen in-game).
     */
    public Quaterniond orientation() {
        if (forward0 == null) {
            captureReference();
            if (forward0 == null) {
                return new Quaterniond();
            }
        }
        Vector3d forward = forwardVec();
        if (forward == null) {
            return new Quaterniond();
        }
        Vector3d up = perpendicularUp(forward);

        Matrix3d ref = basis(forward0, up0);
        Matrix3d now = basis(forward, up);
        Matrix3d delta = now.mul(ref.transpose(new Matrix3d()), new Matrix3d());
        return delta.getNormalizedRotation(new Quaterniond()).normalize();
    }

    /** Captures the spawn reference frame from the current bogey positions (no-op if not yet placed). */
    private void captureReference() {
        Vector3d f = forwardVec();
        if (f != null) {
            this.forward0 = f;
            this.up0 = perpendicularUp(f);
        }
    }

    /** Unit rail tangent (trailing -> leading), or {@code null} if a bogey isn't placed. */
    private Vector3d forwardVec() {
        Vector3d a = RailPose.position(leading, graph);
        Vector3d b = RailPose.position(trailing, graph);
        if (a == null || b == null) {
            return null;
        }
        Vector3d f = a.sub(b, new Vector3d());
        if (f.lengthSquared() < 1.0e-9) {
            return new Vector3d(1, 0, 0);
        }
        return f.normalize();
    }

    /** World-up projected perpendicular to {@code forward} (Gram-Schmidt), with a fallback near vertical. */
    private static Vector3d perpendicularUp(Vector3d forward) {
        Vector3d worldUp = new Vector3d(0, 1, 0);
        Vector3d up = worldUp.sub(new Vector3d(forward).mul(worldUp.dot(forward)), new Vector3d());
        if (up.lengthSquared() < 1.0e-6) { // forward ~ vertical: use world +X as the reference instead
            Vector3d alt = new Vector3d(1, 0, 0);
            up = alt.sub(new Vector3d(forward).mul(alt.dot(forward)), new Vector3d());
        }
        return up.normalize();
    }

    /** Right-handed orthonormal basis matrix with columns (right, up, forward). */
    private static Matrix3d basis(Vector3d forward, Vector3d up) {
        Vector3d right = new Vector3d(up).cross(forward); // {right, up, forward} right-handed
        Matrix3d m = new Matrix3d();
        m.setColumn(0, right);
        m.setColumn(1, up);
        m.setColumn(2, forward);
        return m;
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
