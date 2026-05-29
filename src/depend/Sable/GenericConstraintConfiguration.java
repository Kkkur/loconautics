/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Quaterniondc
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api.physics.constraint.generic;

import dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.generic.GenericConstraintHandle;
import java.util.EnumSet;
import java.util.Set;
import org.joml.Quaterniondc;
import org.joml.Vector3dc;

public record GenericConstraintConfiguration(Vector3dc pos1, Vector3dc pos2, Quaterniondc orientation1, Quaterniondc orientation2, Set<ConstraintJointAxis> lockedAxes) implements PhysicsConstraintConfiguration<GenericConstraintHandle>
{
    public GenericConstraintConfiguration(Vector3dc pos1, Vector3dc pos2, Quaterniondc orientation1, Quaterniondc orientation2) {
        this(pos1, pos2, orientation1, orientation2, EnumSet.noneOf(ConstraintJointAxis.class));
    }
}
