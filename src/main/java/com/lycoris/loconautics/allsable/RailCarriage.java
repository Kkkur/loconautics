package com.lycoris.loconautics.allsable;

import java.util.UUID;

import org.joml.Matrix3d;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.entity.TravellingPoint.ITrackSelector;
import com.simibubi.create.content.trains.entity.TravellingPoint.SteerDirection;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;

import net.minecraft.nbt.CompoundTag;
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
    /** Track up-normal used to build the steering selectors (kept so the carriage can be re-serialised). */
    private final Vec3 upNormal;
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
        this.upNormal = upNormal;
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

    /** The leading bogey's {@link TravellingPoint} (read-only): its current edge + along-edge position, used by
     *  {@link SableTrainDriver} to scan the rail ahead for stations without touching the carriage internals. */
    public TravellingPoint leading() {
        return leading;
    }

    /** The Create track graph this carriage rides (read-only) — needed to resolve edge-point geometry. */
    public TrackGraph graph() {
        return graph;
    }

    /** Track up-normal used to build the steering selectors — needed to scout the rail ahead across edges. */
    public Vec3 upNormal() {
        return upNormal;
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
        return orientationTo(forwardVec());
    }

    /**
     * Absolute orientation that maps this carriage's spawn reference frame (the grid axis it was built along)
     * to an arbitrary {@code forward} direction. Used both for this carriage's own rail tangent
     * ({@link #orientation()}) and to orient a BODY to the chord between its loose bogeys (so the body follows
     * wherever its bogeys are, instead of travelling independently and drifting out of sync).
     */
    public Quaterniond orientationTo(Vector3d forward) {
        if (forward0 == null) {
            captureReference();
            if (forward0 == null) {
                return new Quaterniond();
            }
        }
        if (forward == null || forward.lengthSquared() < 1.0e-9) {
            return new Quaterniond();
        }
        Vector3d f = new Vector3d(forward).normalize();
        Vector3d up = perpendicularUp(f);
        Matrix3d ref = basis(forward0, up0);
        Matrix3d now = basis(f, up);
        Matrix3d delta = now.mul(ref.transpose(new Matrix3d()), new Matrix3d());
        return delta.getNormalizedRotation(new Quaterniond()).normalize();
    }

    /**
     * Captures the spawn reference frame. The car's blocks are built on the world grid (axis-aligned), so the
     * reference forward is the dominant horizontal GRID axis the car is longest along — NOT the raw spawn rail
     * chord. This makes {@link #orientation()} an ABSOLUTE map (grid → current rail), so the body follows the
     * rail like Create even when spawned/parked on a curve. (Using the raw spawn chord made orientation relative
     * to wherever it spawned, so a car parked on a curve stayed grid-aligned instead of turning with the track.)
     */
    private void captureReference() {
        Vector3d f = forwardVec();
        if (f != null) {
            this.forward0 = Math.abs(f.x) >= Math.abs(f.z)
                    ? new Vector3d(Math.signum(f.x), 0, 0)
                    : new Vector3d(0, 0, Math.signum(f.z));
            this.up0 = perpendicularUp(this.forward0);
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

    /** Clears a dead-end park. Bogey reference rails must keep following their car (the BODY carriage is
     *  what governs actually stopping the train), so the driver unsticks them before each advance. */
    public void unstick() {
        stopped = false;
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

    /** The id of the Create track graph this carriage rides (used to re-resolve the graph after a restart). */
    public UUID graphId() {
        return graph.id;
    }

    // ---------------------------------------------------------------------------------------------
    // Persistence: serialise the two bogey TravellingPoints + the spawn reference frame, so the carriage
    // can be rebuilt at exactly the same rail position/orientation after a server restart.
    // ---------------------------------------------------------------------------------------------

    /** Serialises this carriage to NBT (bogey points via {@code dims}, spacing, up-normal, spawn frame). */
    public CompoundTag writeNbt(DimensionPalette dims) {
        if (forward0 == null) {
            captureReference();
        }
        CompoundTag tag = new CompoundTag();
        tag.put("Leading", leading.write(dims));
        tag.put("Trailing", trailing.write(dims));
        tag.putDouble("Spacing", bogeySpacing);
        tag.putBoolean("Stopped", stopped);
        tag.putDouble("UpX", upNormal.x);
        tag.putDouble("UpY", upNormal.y);
        tag.putDouble("UpZ", upNormal.z);
        if (forward0 != null) {
            tag.putDouble("FwdX", forward0.x);
            tag.putDouble("FwdY", forward0.y);
            tag.putDouble("FwdZ", forward0.z);
        }
        return tag;
    }

    /**
     * Rebuilds a carriage from {@link #writeNbt} data on the given (already-resolved) graph. Returns
     * {@code null} if either bogey's track segment no longer exists in the graph (rail was changed while the
     * server was off). The saved spawn reference frame is restored verbatim so the orientation continues from
     * exactly where it left off (re-capturing it here would wrongly reset the car to axis-aligned).
     */
    public static RailCarriage restore(TrackGraph graph, CompoundTag tag, DimensionPalette dims) {
        TravellingPoint leading = TravellingPoint.read(tag.getCompound("Leading"), graph, dims);
        TravellingPoint trailing = TravellingPoint.read(tag.getCompound("Trailing"), graph, dims);
        if (leading.edge == null || trailing.edge == null) {
            return null;
        }
        Vec3 up = new Vec3(tag.getDouble("UpX"), tag.getDouble("UpY"), tag.getDouble("UpZ"));
        double spacing = tag.getDouble("Spacing");
        RailCarriage c = new RailCarriage(graph, leading, trailing, up, spacing, 0.0);
        if (tag.contains("FwdX")) {
            c.forward0 = new Vector3d(tag.getDouble("FwdX"), tag.getDouble("FwdY"), tag.getDouble("FwdZ"));
            c.up0 = perpendicularUp(c.forward0);
        }
        c.stopped = tag.getBoolean("Stopped");
        return c;
    }
}
