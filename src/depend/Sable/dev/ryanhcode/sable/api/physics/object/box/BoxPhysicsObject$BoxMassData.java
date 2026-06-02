/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix3d
 *  org.joml.Matrix3dc
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api.physics.object.box;

import dev.ryanhcode.sable.api.physics.mass.MassData;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3d;
import org.joml.Matrix3dc;
import org.joml.Vector3dc;

private class BoxPhysicsObject.BoxMassData
implements MassData {
    private final Matrix3dc inertia;
    private final Matrix3dc inverseInertia;

    private BoxPhysicsObject.BoxMassData() {
        this.inertia = new Matrix3d().scale(BoxPhysicsObject.this.mass / 6.0);
        this.inverseInertia = this.inertia.invert(new Matrix3d());
    }

    @Override
    public double getMass() {
        return BoxPhysicsObject.this.mass;
    }

    @Override
    public double getInverseMass() {
        return 1.0 / BoxPhysicsObject.this.mass;
    }

    @Override
    public Matrix3dc getInertiaTensor() {
        return this.inertia;
    }

    @Override
    public Matrix3dc getInverseInertiaTensor() {
        return this.inverseInertia;
    }

    @Override
    @Nullable
    public Vector3dc getCenterOfMass() {
        return JOMLConversion.ZERO;
    }
}
