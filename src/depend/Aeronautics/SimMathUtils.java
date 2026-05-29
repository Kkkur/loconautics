/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Axis
 *  dev.ryanhcode.sable.api.math.OrientedBoundingBox3d
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectIterable
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.core.Direction
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.util;

import com.mojang.math.Axis;
import dev.ryanhcode.sable.api.math.OrientedBoundingBox3d;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SimMathUtils {
    private static final Quaterniondc[] ALL_QUATS = new Quaterniondc[]{new Quaterniond(0.0, 0.0, 0.0, 1.0), new Quaterniond(1.0, 0.0, 0.0, 0.0), new Quaterniond(0.0, 1.0, 0.0, 0.0), new Quaterniond(0.0, 0.0, 1.0, 0.0), new Quaterniond(1.0, 0.0, 0.0, 1.0).normalize(), new Quaterniond(0.0, 1.0, 0.0, 1.0).normalize(), new Quaterniond(0.0, 0.0, 1.0, 1.0).normalize(), new Quaterniond(0.0, 1.0, 1.0, 0.0).normalize(), new Quaterniond(1.0, 0.0, 1.0, 0.0).normalize(), new Quaterniond(1.0, 1.0, 0.0, 0.0).normalize(), new Quaterniond(1.0, 1.0, 1.0, 1.0).normalize()};

    public static Vec3 rotateQuat(Vec3 V, Quaterniond Q) {
        Quaterniond q = new Quaterniond((double)((float)V.x), (double)((float)V.y), (double)((float)V.z), 0.0);
        Quaterniond Q2 = new Quaterniond((Quaterniondc)Q);
        q.mul((Quaterniondc)Q2);
        Q2.conjugate();
        Q2.mul((Quaterniondc)q);
        return new Vec3(Q2.x(), Q2.y(), Q2.z());
    }

    public static Vec3 rotateQuat(Vec3 V, Quaternionf Q) {
        Quaternionf q = new Quaternionf((float)V.x, (float)V.y, (float)V.z, 0.0f);
        Quaternionf Q2 = new Quaternionf((Quaternionfc)Q);
        q.mul((Quaternionfc)Q2);
        Q2.conjugate();
        Q2.mul((Quaternionfc)q);
        return new Vec3((double)Q2.x(), (double)Q2.y(), (double)Q2.z());
    }

    public static Vec3 rotateQuatReverse(Vec3 V, Quaterniond Q) {
        Quaterniond q = new Quaterniond((double)((float)V.x), (double)((float)V.y), (double)((float)V.z), 0.0);
        Quaterniond Q2 = new Quaterniond((Quaterniondc)Q);
        Q2.conjugate();
        q.mul((Quaterniondc)Q2);
        Q2.conjugate();
        Q2.mul((Quaterniondc)q);
        return new Vec3(Q2.x(), Q2.y(), Q2.z());
    }

    public static Vec3 rotateQuatReverse(Vec3 V, Quaternionf Q) {
        Quaternionf q = new Quaternionf((float)V.x, (float)V.y, (float)V.z, 0.0f);
        Quaternionf Q2 = new Quaternionf((Quaternionfc)Q);
        Q2.conjugate();
        q.mul((Quaternionfc)Q2);
        Q2.conjugate();
        Q2.mul((Quaternionfc)q);
        return new Vec3((double)Q2.x(), (double)Q2.y(), (double)Q2.z());
    }

    public static Vec3 clampIntoCone(Vec3 v, Vec3 coneAxis, double coneAngle) {
        double nn;
        double disc;
        double vv = v.dot(v);
        double vn = v.dot(coneAxis);
        double offsetDistance = (-vn + Math.sqrt(disc = (nn = coneAxis.dot(coneAxis)) * vv * 1.01 - vn * vn) / Math.tan(coneAngle)) / nn;
        if (offsetDistance < 0.0 ^ coneAngle < 0.0) {
            return v;
        }
        return v.add(coneAxis.scale(offsetDistance)).normalize();
    }

    public static void clampIntoCone(Vector3d v, Vector3d coneAxis, double coneAngle) {
        double nn;
        double disc;
        double vv = v.dot((Vector3dc)v);
        double vn = v.dot((Vector3dc)coneAxis);
        double offsetDistance = (-vn + Math.sqrt(disc = (nn = coneAxis.dot((Vector3dc)coneAxis)) * vv * 1.01 - vn * vn) / Math.tan(coneAngle)) / nn;
        if (offsetDistance < 0.0 ^ coneAngle < 0.0) {
            return;
        }
        v.add((Vector3dc)new Vector3d((Vector3dc)coneAxis).mul(offsetDistance)).normalize();
    }

    public static boolean isInCylinder(Vector3dc axisVector, Vector3d relativePosition, double cylinderLength, double cylinderRadius) {
        double distance = axisVector.dot((Vector3dc)relativePosition);
        if (distance < 0.0 || distance > cylinderLength) {
            return false;
        }
        Vector3d scaledAxis = axisVector.mul(distance, new Vector3d());
        return (relativePosition = relativePosition.sub((Vector3dc)scaledAxis, scaledAxis)).lengthSquared() <= cylinderRadius * cylinderRadius;
    }

    public static Quaternionf getBlockStateOrientation(Direction facing) {
        Quaternionf orientation = facing.getAxis().isHorizontal() ? Axis.YP.rotationDegrees(AngleHelper.horizontalAngle((Direction)facing.getOpposite())) : new Quaternionf();
        orientation.rotateX((-90.0f - AngleHelper.verticalAngle((Direction)facing)) * ((float)Math.PI / 180));
        return orientation;
    }

    public static Quaternionf getQuaternionfFromVectorRotation(Vector3dc start, Vector3dc end) {
        Vector3d cross = new Vector3d();
        start.cross(end, cross);
        Quaternionf Q = new Quaternionf((float)cross.x(), (float)cross.y(), (float)cross.z(), 1.0f + (float)start.dot(end));
        Q.normalize();
        return Q;
    }

    public static Quaterniond clampQuaternionToGrid(Quaterniond q, Iterable<Quaterniondc> gridQuats) {
        return SimMathUtils.clampQuaternionToGrid((Quaterniondc)q, gridQuats, q);
    }

    public static Quaterniond clampQuaternionToGrid(Quaterniondc q, Iterable<Quaterniondc> gridQuats, Quaterniond dest) {
        int signX = q.x() < 0.0 ? -1 : 1;
        int signY = q.y() < 0.0 ? -1 : 1;
        int signZ = q.z() < 0.0 ? -1 : 1;
        int signW = q.w() < 0.0 ? -1 : 1;
        dest.set(q);
        dest.x *= (double)(-signX);
        dest.y *= (double)(-signY);
        dest.z *= (double)(-signZ);
        dest.w *= (double)(-signW);
        Quaterniond temp = new Quaterniond();
        Quaterniond best = new Quaterniond();
        double distance = 10.0;
        for (Quaterniondc gq : gridQuats) {
            double currentDist = dest.add(gq, temp).lengthSquared();
            if (!(currentDist < distance)) continue;
            distance = currentDist;
            best.set(gq);
        }
        dest.set((Quaterniondc)best);
        dest.x *= (double)signX;
        dest.y *= (double)signY;
        dest.z *= (double)signZ;
        dest.w *= (double)signW;
        return dest;
    }

    public static float smoothStep(float t) {
        return t * t * (3.0f - 2.0f * t);
    }

    public static double getClosestYaw(Quaterniond orientation) {
        double d = OrientedBoundingBox3d.UP.dot((Vector3dc)new Vector3d(orientation.x(), orientation.y(), orientation.z()));
        return 2.0 * Math.atan2(-d, orientation.w());
    }

    public static enum GridQuats implements ObjectIterable<Quaterniondc>
    {
        ALL(2047),
        X_AXIS(19),
        Y_AXIS(37),
        Z_AXIS(73),
        REAL(1137);

        private final ObjectList<Quaterniondc> currentQuats = new ObjectArrayList(ALL_QUATS.length);
        private final ObjectList<Quaterniondc> oppositeQuats = new ObjectArrayList(ALL_QUATS.length);

        private GridQuats(int bitPattern) {
            for (Quaterniondc q : ALL_QUATS) {
                ((bitPattern & 1) > 0 ? this.currentQuats : this.oppositeQuats).add((Object)q);
                bitPattern >>= 1;
            }
        }

        public ObjectIterable<Quaterniondc> opposite() {
            return () -> this.oppositeQuats.iterator();
        }

        @NotNull
        public ObjectIterator<Quaterniondc> iterator() {
            return this.currentQuats.iterator();
        }
    }
}
