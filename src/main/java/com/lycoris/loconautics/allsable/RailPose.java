package com.lycoris.loconautics.allsable;

import org.joml.Quaterniond;
import org.joml.Vector3d;

import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.TrackGraph;

import net.minecraft.world.phys.Vec3;

/**
 * Core of the "all-Sable" train (Phase 1+): turns a position on the Create track graph into a world pose
 * for a Sable sub-level — reusing Create's rail math ({@link TravellingPoint}/{@link TrackGraph}) without
 * any {@code CarriageContraptionEntity}.
 *
 * <p>A carriage rides on two bogey points (leading + trailing). Its world position is the midpoint and its
 * orientation comes from the vector between them — the same construction Create uses in
 * {@code Carriage$DimensionalCarriageEntity.alignEntity} ({@code yaw = atan2(dz,dx)+180},
 * {@code pitch = -atan2(dy, |dxz|)}). The "up" axis can later be refined from {@code TrackEdge.getNormal}
 * for banked/upside-down track; for now we keep it world-up (flat/curve correct, banking TODO).
 *
 * <p>This class is intentionally pure (no entity, no mixins) so Phase 1 can move a debug body along a rail,
 * and Phase 2 can drive the real sub-level pose from it.
 */
public final class RailPose {

    /** World position of a single point on the graph, or {@code null} if it isn't on an edge yet. */
    public static Vector3d position(TravellingPoint point, TrackGraph graph) {
        if (point == null || point.edge == null) {
            return null;
        }
        Vec3 p = point.getPosition(graph);
        return new Vector3d(p.x, p.y, p.z);
    }

    /** Midpoint world position of a two-bogey carriage, or {@code null} if either point isn't placed. */
    public static Vector3d carriagePosition(TravellingPoint leading, TravellingPoint trailing, TrackGraph graph) {
        Vector3d a = position(leading, graph);
        Vector3d b = position(trailing, graph);
        if (a == null || b == null) {
            return null;
        }
        return a.add(b).mul(0.5);
    }

    /**
     * Carriage orientation from the leading->trailing vector, matching Create's alignEntity yaw/pitch.
     * Returns identity if either point isn't placed.
     */
    public static Quaterniond carriageOrientation(TravellingPoint leading, TravellingPoint trailing, TrackGraph graph) {
        Vector3d a = position(leading, graph);
        Vector3d b = position(trailing, graph);
        if (a == null || b == null) {
            return new Quaterniond();
        }
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        double dz = a.z - b.z;
        double yawDeg = Math.toDegrees(Math.atan2(dz, dx)) + 180.0;
        double pitchDeg = -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
        return new Quaterniond()
                .rotateY(Math.toRadians(-yawDeg))
                .rotateX(Math.toRadians(pitchDeg))
                .normalize();
    }

    private RailPose() {
    }
}
