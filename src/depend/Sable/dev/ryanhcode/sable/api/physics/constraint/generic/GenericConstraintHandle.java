/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Quaterniondc
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api.physics.constraint.generic;

import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle;
import org.joml.Quaterniondc;
import org.joml.Vector3dc;

public interface GenericConstraintHandle
extends PhysicsConstraintHandle {
    public void setFrame1(Vector3dc var1, Quaterniondc var2);

    public void setFrame2(Vector3dc var1, Quaterniondc var2);
}
