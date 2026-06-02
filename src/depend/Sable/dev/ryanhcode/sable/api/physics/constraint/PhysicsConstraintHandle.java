/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3d
 */
package dev.ryanhcode.sable.api.physics.constraint;

import dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis;
import org.joml.Vector3d;

public interface PhysicsConstraintHandle {
    public void getJointImpulses(Vector3d var1, Vector3d var2);

    public void setContactsEnabled(boolean var1);

    public void setMotor(ConstraintJointAxis var1, double var2, double var4, double var6, boolean var8, double var9);

    public void remove();

    public boolean isValid();
}
