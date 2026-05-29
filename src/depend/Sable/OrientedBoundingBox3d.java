/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector2d
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api.math;

import dev.ryanhcode.sable.api.math.LevelReusedVectors;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class OrientedBoundingBox3d {
    public static final Vector3dc RIGHT = new Vector3d(1.0, 0.0, 0.0);
    public static final Vector3dc UP = new Vector3d(0.0, 1.0, 0.0);
    public static final Vector3dc FORWARD = new Vector3d(0.0, 0.0, 1.0);
    private final Vector3d position = new Vector3d();
    private final Vector3d dimensions = new Vector3d();
    private final Quaterniond orientation = new Quaterniond();
    private final LevelReusedVectors sink;

    public OrientedBoundingBox3d(@NotNull LevelReusedVectors sink) {
        this.sink = sink;
    }

    public OrientedBoundingBox3d(@NotNull Vector3dc position, @NotNull Vector3dc dimensions, @NotNull Quaterniondc orientation, @NotNull LevelReusedVectors sink) {
        this.position.set(position);
        this.dimensions.set(dimensions);
        this.orientation.set(orientation);
        this.sink = sink;
    }

    public OrientedBoundingBox3d(double x, double y, double z, double sizeX, double sizeY, double sizeZ, @NotNull Quaterniondc orientation, @NotNull LevelReusedVectors sink) {
        this.position.set(x, y, z);
        this.dimensions.set(sizeX, sizeY, sizeZ);
        this.orientation.set(orientation);
        this.sink = sink;
    }

    public void set(Vector3dc position, Vector3dc dimensions, Quaterniondc orientation) {
        this.position.set(position);
        this.dimensions.set(dimensions);
        this.orientation.set(orientation);
    }

    public OrientedBoundingBox3d setPosition(Vector3dc position) {
        this.position.set(position);
        return this;
    }

    public OrientedBoundingBox3d setDimensions(Vector3dc dimensions) {
        this.dimensions.set(dimensions);
        return this;
    }

    public OrientedBoundingBox3d setOrientation(Quaterniondc orientation) {
        this.orientation.set(orientation);
        return this;
    }

    public Quaterniond getOrientation() {
        return this.orientation;
    }

    public Vector3d getPosition() {
        return this.position;
    }

    public Vector3d getDimensions() {
        return this.dimensions;
    }

    public Vector3d @NotNull [] vertices(Vector3d[] result) {
        this.dimensions.mul(0.5, this.sink.tempmin);
        this.dimensions.mul(-0.5, this.sink.tempmax);
        this.orientation.transform((Vector3dc)this.sink.tempmin, result[0]).add((Vector3dc)this.position);
        this.orientation.transform((Vector3dc)this.sink.tempVert1.set(this.sink.tempmax.x, this.sink.tempmin.y, this.sink.tempmin.z), result[1]).add((Vector3dc)this.position);
        this.orientation.transform((Vector3dc)this.sink.tempVert4.set(this.sink.tempmin.x, this.sink.tempmin.y, this.sink.tempmax.z), result[4]).add((Vector3dc)this.position);
        this.orientation.transform((Vector3dc)this.sink.tempVert5.set(this.sink.tempmax.x, this.sink.tempmin.y, this.sink.tempmax.z), result[5]).add((Vector3dc)this.position);
        this.orientation.transform((Vector3dc)this.sink.tempVert3.set(this.sink.tempmax.x, this.sink.tempmax.y, this.sink.tempmin.z), result[3]).add((Vector3dc)this.position);
        this.orientation.transform((Vector3dc)this.sink.tempVert2.set(this.sink.tempmin.x, this.sink.tempmax.y, this.sink.tempmin.z), result[2]).add((Vector3dc)this.position);
        this.orientation.transform((Vector3dc)this.sink.tempVert6.set(this.sink.tempmin.x, this.sink.tempmax.y, this.sink.tempmax.z), result[6]).add((Vector3dc)this.position);
        this.orientation.transform((Vector3dc)this.sink.tempmax, result[7]).add((Vector3dc)this.position);
        return result;
    }

    public Vector3d rotate(@NotNull Vector3d vec) {
        return this.orientation.transform(vec);
    }

    private static boolean doesOverlap(@NotNull Vector2d a, @NotNull Vector2d b) {
        return a.x <= b.y && a.y >= b.x;
    }

    public static double getOverlap(@NotNull Vector2d a, @NotNull Vector2d b) {
        if (!OrientedBoundingBox3d.doesOverlap(a, b)) {
            return 0.0;
        }
        return Math.min(a.y, b.y) - Math.max(a.x, b.x);
    }

    @NotNull
    public static Vector3d sat(@NotNull OrientedBoundingBox3d obbA, @NotNull OrientedBoundingBox3d obbB) {
        return OrientedBoundingBox3d.sat(obbA, obbB, new Vector3d());
    }

    @NotNull
    public static Vector3d sat(@NotNull OrientedBoundingBox3d obbA, @NotNull OrientedBoundingBox3d obbB, @NotNull Vector3d dest) {
        boolean facingOpposite;
        Objects.requireNonNull(obbA, "obbA");
        Objects.requireNonNull(obbB, "obbB");
        Objects.requireNonNull(dest, "dest");
        LevelReusedVectors context = obbA.sink;
        Vector3d[] verticesA = obbA.vertices(context.a);
        Vector3d[] verticesB = obbB.vertices(context.b);
        Vector3d checker = obbA.position.sub((Vector3dc)obbB.position, obbA.sink.checker).normalize();
        Vector3d aRight = obbA.rotate(context.obbARight.set(RIGHT));
        Vector3d aUp = obbA.rotate(context.obbAUp.set(UP));
        Vector3d aForward = obbA.rotate(context.obbAForward.set(FORWARD));
        Vector3d bRight = obbB.rotate(context.obbBRight.set(RIGHT));
        Vector3d bUp = obbB.rotate(context.obbBUp.set(UP));
        Vector3d bForward = obbB.rotate(context.obbBForward.set(FORWARD));
        Vector3d mtv = dest.set(Double.MAX_VALUE);
        OrientedBoundingBox3d.genChecks(aRight, aUp, aForward, bRight, bUp, bForward, context.checks);
        double minOverlap = Double.MAX_VALUE;
        for (Vector3d check : context.checks) {
            double overlap;
            if (check.lengthSquared() <= 0.0) continue;
            check.normalize();
            OrientedBoundingBox3d.checkSeparation(verticesA, check, context.proj1);
            OrientedBoundingBox3d.checkSeparation(verticesB, check, context.proj2);
            if (check.dot((Vector3dc)checker) > 0.0) {
                check.mul(-1.0);
            }
            if ((overlap = OrientedBoundingBox3d.getOverlap(context.proj1, context.proj2)) == 0.0) {
                return dest.zero();
            }
            if (!(overlap < minOverlap)) continue;
            minOverlap = overlap;
            mtv.set((Vector3dc)check.mul(minOverlap));
        }
        boolean bl = facingOpposite = obbA.position.sub((Vector3dc)obbB.position, context.oppo).dot((Vector3dc)mtv) < 0.0;
        if (facingOpposite) {
            mtv.mul(-1.0);
        }
        return mtv;
    }

    public static Vector3d[] genChecks(Vector3d aRight, Vector3d aUp, Vector3d aForward, Vector3d bRight, Vector3d bUp, Vector3d bForward, Vector3d[] checks) {
        checks[0].set((Vector3dc)aRight);
        checks[1].set((Vector3dc)aUp);
        checks[2].set((Vector3dc)aForward);
        checks[3].set((Vector3dc)bRight);
        checks[4].set((Vector3dc)bUp);
        checks[5].set((Vector3dc)bForward);
        aRight.cross((Vector3dc)bRight, checks[6]);
        aRight.cross((Vector3dc)bUp, checks[7]);
        aRight.cross((Vector3dc)bForward, checks[8]);
        aUp.cross((Vector3dc)bRight, checks[9]);
        aUp.cross((Vector3dc)bUp, checks[10]);
        aUp.cross((Vector3dc)bForward, checks[11]);
        aForward.cross((Vector3dc)bRight, checks[12]);
        aForward.cross((Vector3dc)bUp, checks[13]);
        aForward.cross((Vector3dc)bForward, checks[14]);
        return checks;
    }

    public static Vector3dc satToleranced(OrientedBoundingBox3d entityOBB, OrientedBoundingBox3d obbB, double tolerance) {
        boolean facingOpposite;
        Objects.requireNonNull(entityOBB, "entityOBB");
        Objects.requireNonNull(obbB, "obbB");
        LevelReusedVectors context = entityOBB.sink;
        Vector3d[] verticesA = entityOBB.vertices(context.a);
        Vector3d[] verticesB = obbB.vertices(context.b);
        Vector3d checker = entityOBB.position.sub((Vector3dc)obbB.position, new Vector3d()).normalize();
        Vector3d aRight = entityOBB.rotate(context.obbARight.set(RIGHT));
        Vector3d aUp = entityOBB.rotate(context.obbAUp.set(UP));
        Vector3d aForward = entityOBB.rotate(context.obbAForward.set(FORWARD));
        Vector3d bRight = obbB.rotate(context.obbBRight.set(RIGHT));
        Vector3d bUp = obbB.rotate(context.obbBUp.set(UP));
        Vector3d bForward = obbB.rotate(context.obbBForward.set(FORWARD));
        Vector3d mtv = new Vector3d(Double.MAX_VALUE);
        OrientedBoundingBox3d.genChecks(aRight, aUp, aForward, bRight, bUp, bForward, context.checks);
        double minOverlap = Double.MAX_VALUE;
        int i = 0;
        for (Vector3d check : context.checks) {
            double overlap;
            if (check.lengthSquared() <= 0.0) continue;
            check.normalize();
            OrientedBoundingBox3d.checkSeparation(verticesA, check, context.proj1);
            OrientedBoundingBox3d.checkSeparation(verticesB, check, context.proj2);
            if (check.dot((Vector3dc)checker) > 0.0) {
                check.mul(-1.0);
            }
            if ((overlap = OrientedBoundingBox3d.getOverlap(context.proj1, context.proj2)) == 0.0) {
                return context.zero;
            }
            double d = i == 14 ? 0.1 : 0.0;
            if (overlap - d < minOverlap) {
                minOverlap = overlap;
                mtv = check.mul(minOverlap);
            }
            ++i;
        }
        boolean bl = facingOpposite = entityOBB.position.sub((Vector3dc)obbB.position, context.oppo).dot((Vector3dc)mtv) < 0.0;
        if (facingOpposite) {
            mtv.mul(-1.0);
        }
        return mtv;
    }

    @NotNull
    public static Vector2d checkSeparation(Vector3d @NotNull [] self, @NotNull Vector3d axis, Vector2d result) {
        if (axis.lengthSquared() <= 0.0) {
            return result.set(0.0, 0.0);
        }
        double min = Double.MAX_VALUE;
        double max = -1.7976931348623157E308;
        for (Vector3d vec : self) {
            double dot = vec.dot((Vector3dc)axis);
            min = Math.min(dot, min);
            max = Math.max(dot, max);
        }
        return result.set(min, max);
    }
}
