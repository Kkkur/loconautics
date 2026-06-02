/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Quaterniond
 *  org.joml.Vector3d
 */
package dev.ryanhcode.sable.physics.impl.rapier;

import org.joml.Quaterniond;
import org.joml.Vector3d;

private record RapierPhysicsPipeline.TrackedKinematicContraption(Vector3d lastUploadedPosition, Quaterniond lastUploadedOrientation, Vector3d lastUploadedLinVel, Vector3d lastUploadedAngVel, int id) {
}
