/*
 * Decompiled with CFR 0.152.
 */
package dev.ryanhcode.sable.api.physics.constraint.rotary;

import dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle;

public interface RotaryConstraintHandle
extends PhysicsConstraintHandle {
    public static final ConstraintJointAxis DEFAULT_AXIS = ConstraintJointAxis.ANGULAR_X;

    @Deprecated(forRemoval=true)
    default public void setServoCoefficients(double angle, double stiffness, double damping) {
        this.setMotor(DEFAULT_AXIS, angle, stiffness, damping, false, 0.0);
    }
}
