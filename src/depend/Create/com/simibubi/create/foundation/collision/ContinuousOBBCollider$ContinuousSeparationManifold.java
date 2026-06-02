/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.foundation.collision;

import net.minecraft.world.phys.Vec3;

private static class ContinuousOBBCollider.ContinuousSeparationManifold {
    static final double UNDEFINED = -1.0;
    double latestCollisionEntryTime = -1.0;
    double earliestCollisionExitTime = Double.MAX_VALUE;
    boolean isDiscreteCollision = true;
    double collisionX;
    double collisionY;
    double collisionZ;
    final Vec3 stepSeparationAxis;
    double stepSeparation;
    Vec3 normalAxis;
    double normalSeparation;
    Vec3 axis;
    double separation;

    public ContinuousOBBCollider.ContinuousSeparationManifold(Vec3 stepSeparationAxis) {
        this.stepSeparationAxis = stepSeparationAxis;
    }

    boolean separate(Vec3 axis, double TL, double rA, double rB, double projectedMotion, boolean axisOfObjA) {
        double dot;
        boolean discreteCollision;
        ++checkCount;
        double distance = Math.abs(TL);
        double diff = distance - (rA + rB);
        boolean bl = discreteCollision = diff <= 0.0;
        if (!discreteCollision && Math.signum(projectedMotion) == Math.signum(TL)) {
            return true;
        }
        double sTL = Math.signum(TL);
        double separation = sTL * Math.abs(diff);
        if (!discreteCollision) {
            this.isDiscreteCollision = false;
            if (Math.abs(separation) > Math.abs(projectedMotion)) {
                return true;
            }
            double entryTime = Math.abs(separation) / Math.abs(projectedMotion);
            double exitTime = (diff + Math.abs(rA) + Math.abs(rB)) / Math.abs(projectedMotion);
            this.latestCollisionEntryTime = Math.max(entryTime, this.latestCollisionEntryTime);
            this.earliestCollisionExitTime = Math.min(exitTime, this.earliestCollisionExitTime);
        }
        if (axisOfObjA && distance != 0.0 && -diff <= Math.abs(this.normalSeparation)) {
            this.normalAxis = axis;
            this.normalSeparation = separation;
        }
        if ((dot = this.stepSeparationAxis.dot(axis)) != 0.0 && discreteCollision) {
            Vec3 cross = axis.cross(this.stepSeparationAxis);
            double dotSeparation = Math.signum(dot) * TL - (rA + rB);
            double stepSeparation = -dotSeparation;
            if (!cross.equals((Object)Vec3.ZERO)) {
                Vec3 sepVec = axis.scale(dotSeparation);
                Vec3 axisPlane = axis.cross(cross);
                Vec3 stepPlane = this.stepSeparationAxis.cross(cross);
                Vec3 stepSeparationVec = sepVec.subtract(axisPlane.scale(sepVec.dot(stepPlane) / axisPlane.dot(stepPlane)));
                stepSeparation = stepSeparationVec.length();
                if (Math.abs(this.stepSeparation) > Math.abs(stepSeparation) && stepSeparation != 0.0) {
                    this.stepSeparation = stepSeparation;
                }
            } else if (Math.abs(this.stepSeparation) > stepSeparation) {
                this.stepSeparation = stepSeparation;
            }
        }
        if (distance != 0.0 && -diff <= Math.abs(this.separation)) {
            this.axis = axis;
            this.separation = separation;
            double scale = Math.signum(TL) * (axisOfObjA ? -rA : -rB) - Math.signum(separation) * 0.125;
            this.collisionX = axis.x * scale;
            this.collisionY = axis.y * scale;
            this.collisionZ = axis.z * scale;
        }
        return false;
    }

    public double getTimeOfImpact() {
        if (this.latestCollisionEntryTime == -1.0) {
            return -1.0;
        }
        if (this.latestCollisionEntryTime > this.earliestCollisionExitTime) {
            return -1.0;
        }
        return this.latestCollisionEntryTime;
    }

    private static double withSignedEpsilon(double sep) {
        return sep + Math.signum(sep) * 1.0E-4;
    }

    public void reset() {
        this.axis = null;
        this.normalAxis = null;
        this.separation = Double.MAX_VALUE;
        this.stepSeparation = Double.MAX_VALUE;
        this.normalSeparation = Double.MAX_VALUE;
        this.latestCollisionEntryTime = -1.0;
        this.earliestCollisionExitTime = Double.MAX_VALUE;
        this.isDiscreteCollision = true;
    }
}
