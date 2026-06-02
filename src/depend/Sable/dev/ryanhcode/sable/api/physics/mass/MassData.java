/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix3dc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api.physics.mass;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public interface MassData {
    public double getMass();

    public double getInverseMass();

    public Matrix3dc getInertiaTensor();

    public Matrix3dc getInverseInertiaTensor();

    @Nullable
    public Vector3dc getCenterOfMass();

    default public boolean isInvalid() {
        return this.getMass() <= 0.0;
    }

    default public double getInverseNormalMass(Vector3dc position, Vector3dc direction) {
        Vector3d comLocalPos = position.sub(this.getCenterOfMass(), new Vector3d());
        Vector3d normalizedDirection = direction.normalize(new Vector3d());
        Vector3d cross = comLocalPos.cross((Vector3dc)normalizedDirection, new Vector3d());
        return cross.dot((Vector3dc)this.getInverseInertiaTensor().transform((Vector3dc)cross, new Vector3d())) + this.getInverseMass();
    }
}
