/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.joml.Vector3d
 */
package dev.ryanhcode.sable.physics.impl.rapier.constraint;

import dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle;
import dev.ryanhcode.sable.physics.impl.rapier.Rapier3D;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3d;

@ApiStatus.Internal
public abstract class RapierConstraintHandle
implements PhysicsConstraintHandle {
    protected final long handle;
    protected final int sceneID;
    private final double[] impulseCache;

    protected RapierConstraintHandle(int sceneID, long handle) {
        this.sceneID = sceneID;
        this.handle = handle;
        this.impulseCache = new double[6];
    }

    @Override
    public void setContactsEnabled(boolean enabled) {
        Rapier3D.setConstraintContactsEnabled(this.sceneID, this.handle, enabled);
    }

    @Override
    public void getJointImpulses(Vector3d linearImpulseDest, Vector3d angularImpulseDest) {
        Rapier3D.getConstraintImpulses(this.sceneID, this.handle, this.impulseCache);
        linearImpulseDest.set(this.impulseCache[0], this.impulseCache[1], this.impulseCache[2]);
        angularImpulseDest.set(this.impulseCache[3], this.impulseCache[4], this.impulseCache[5]);
    }

    @Override
    public void setMotor(ConstraintJointAxis axis, double target, double stiffness, double damping, boolean hasForceLimit, double maxForce) {
        Rapier3D.setConstraintMotor(this.sceneID, this.handle, axis.ordinal(), target, stiffness, damping, hasForceLimit, maxForce);
    }

    @Override
    public void remove() {
        Rapier3D.removeConstraint(this.sceneID, this.handle);
    }

    @Override
    public boolean isValid() {
        return Rapier3D.isConstraintValid(this.sceneID, this.handle);
    }
}
