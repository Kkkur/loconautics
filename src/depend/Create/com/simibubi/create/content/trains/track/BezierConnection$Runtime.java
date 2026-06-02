/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.track;

import net.createmod.catnip.data.Couple;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

private static class BezierConnection.Runtime {
    private final Vec3 finish1;
    private final Vec3 finish2;
    private final double length;
    private final float[] stepLUT;
    private final int segments;
    private double radius;
    private double handleLength;
    private final AABB bounds;

    private BezierConnection.Runtime(Couple<Vec3> starts, Couple<Vec3> axes) {
        Vec3 end1 = (Vec3)starts.getFirst();
        Vec3 end2 = (Vec3)starts.getSecond();
        Vec3 axis1 = ((Vec3)axes.getFirst()).normalize();
        Vec3 axis2 = ((Vec3)axes.getSecond()).normalize();
        this.determineHandles(end1, end2, axis1, axis2);
        this.finish1 = axis1.scale(this.handleLength).add(end1);
        this.finish2 = axis2.scale(this.handleLength).add(end2);
        int scanCount = 16;
        this.length = BezierConnection.Runtime.computeLength(this.finish1, this.finish2, end1, end2, scanCount);
        this.segments = (int)(this.length * 2.0);
        this.stepLUT = new float[this.segments + 1];
        this.stepLUT[0] = 1.0f;
        float combinedDistance = 0.0f;
        AABB bounds = new AABB(end1, end2);
        Vec3 previous = end1;
        for (int i = 0; i <= this.segments; ++i) {
            float t = (float)i / (float)this.segments;
            Vec3 result = VecHelper.bezier((Vec3)end1, (Vec3)end2, (Vec3)this.finish1, (Vec3)this.finish2, (float)t);
            bounds = bounds.minmax(new AABB(result, result));
            if (i > 0) {
                combinedDistance = (float)((double)combinedDistance + result.distanceTo(previous) / this.length);
                this.stepLUT[i] = t / combinedDistance;
            }
            previous = result;
        }
        this.bounds = bounds.inflate(1.375);
    }

    private static double computeLength(Vec3 finish1, Vec3 finish2, Vec3 end1, Vec3 end2, int scanCount) {
        double length = 0.0;
        Vec3 previous = end1;
        for (int i = 0; i <= scanCount; ++i) {
            float t = (float)i / (float)scanCount;
            Vec3 result = VecHelper.bezier((Vec3)end1, (Vec3)end2, (Vec3)finish1, (Vec3)finish2, (float)t);
            if (previous != null) {
                length += result.distanceTo(previous);
            }
            previous = result;
        }
        return length;
    }

    public float getSegmentT(int index) {
        return index == this.segments ? 1.0f : (float)index * this.stepLUT[index] / (float)this.segments;
    }

    private void determineHandles(Vec3 end1, Vec3 end2, Vec3 axis1, Vec3 axis2) {
        Vec3 cross1 = axis1.cross(new Vec3(0.0, 1.0, 0.0));
        Vec3 cross2 = axis2.cross(new Vec3(0.0, 1.0, 0.0));
        this.radius = 0.0;
        double a1 = Mth.atan2((double)(-axis2.z), (double)(-axis2.x));
        double a2 = Mth.atan2((double)axis1.z, (double)axis1.x);
        double angle = a1 - a2;
        float circle = (float)Math.PI * 2;
        if (Math.abs((double)circle - (angle = (angle + (double)circle) % (double)circle)) < Math.abs(angle)) {
            angle = (double)circle - angle;
        }
        if (Mth.equal((double)angle, (double)0.0)) {
            double[] intersect = VecHelper.intersect((Vec3)end1, (Vec3)end2, (Vec3)axis1, (Vec3)cross2, (Direction.Axis)Direction.Axis.Y);
            if (intersect != null) {
                double t = Math.abs(intersect[0]);
                double u = Math.abs(intersect[1]);
                double min = Math.min(t, u);
                double max = Math.max(t, u);
                if (min > 1.2 && max / min > 1.0 && max / min < 3.0) {
                    this.handleLength = max - min;
                    return;
                }
            }
            this.handleLength = end2.distanceTo(end1) / 3.0;
            return;
        }
        double n = (double)circle / angle;
        double factor = 1.3333333333333333 * Math.tan(Math.PI / (2.0 * n));
        double[] intersect = VecHelper.intersect((Vec3)end1, (Vec3)end2, (Vec3)cross1, (Vec3)cross2, (Direction.Axis)Direction.Axis.Y);
        if (intersect == null) {
            this.handleLength = end2.distanceTo(end1) / 3.0;
            return;
        }
        this.radius = Math.abs(intersect[1]);
        this.handleLength = this.radius * factor;
        if (Mth.equal((double)this.handleLength, (double)0.0)) {
            this.handleLength = 1.0;
        }
    }
}
