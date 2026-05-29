/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Quaterniondc
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api.physics.constraint.fixed;

import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.fixed.FixedConstraintHandle;
import org.joml.Quaterniondc;
import org.joml.Vector3dc;

public record FixedConstraintConfiguration(Vector3dc pos1, Vector3dc pos2, Quaterniondc orientation) implements PhysicsConstraintConfiguration<FixedConstraintHandle>
{
}
