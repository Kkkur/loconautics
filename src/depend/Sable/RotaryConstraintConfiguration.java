/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api.physics.constraint.rotary;

import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.rotary.RotaryConstraintHandle;
import org.joml.Vector3dc;

public record RotaryConstraintConfiguration(Vector3dc pos1, Vector3dc pos2, Vector3dc normal1, Vector3dc normal2) implements PhysicsConstraintConfiguration<RotaryConstraintHandle>
{
}
