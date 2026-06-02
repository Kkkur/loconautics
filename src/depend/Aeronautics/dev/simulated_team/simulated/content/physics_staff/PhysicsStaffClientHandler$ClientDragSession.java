/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  org.joml.Quaterniond
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.physics_staff;

import dev.ryanhcode.sable.sublevel.SubLevel;
import org.joml.Quaterniond;
import org.joml.Vector3dc;

public static final class PhysicsStaffClientHandler.ClientDragSession {
    private final SubLevel dragSubLevel;
    private final Vector3dc dragLocalAnchor;
    private final Quaterniond dragOrientation;
    private double distance;

    public PhysicsStaffClientHandler.ClientDragSession(SubLevel dragSubLevel, Vector3dc dragLocalAnchor, Quaterniond dragOrientation, double distance) {
        this.dragSubLevel = dragSubLevel;
        this.dragLocalAnchor = dragLocalAnchor;
        this.dragOrientation = dragOrientation;
        this.distance = distance;
    }

    public SubLevel dragSubLevel() {
        return this.dragSubLevel;
    }

    public Vector3dc dragLocalAnchor() {
        return this.dragLocalAnchor;
    }

    public Quaterniond dragOrientation() {
        return this.dragOrientation;
    }

    public double distance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String toString() {
        return "ClientDragSession[dragSubLevel=" + String.valueOf(this.dragSubLevel) + ", dragLocalAnchor=" + String.valueOf(this.dragLocalAnchor) + ", dragOrientation=" + String.valueOf(this.dragOrientation) + ", distance=" + this.distance + "]";
    }
}
