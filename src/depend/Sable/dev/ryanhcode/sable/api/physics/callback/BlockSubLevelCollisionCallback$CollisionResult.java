/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api.physics.callback;

import dev.ryanhcode.sable.companion.math.JOMLConversion;
import org.joml.Vector3dc;

public record BlockSubLevelCollisionCallback.CollisionResult(Vector3dc tangentMotion, boolean removeCollision) {
    public static final BlockSubLevelCollisionCallback.CollisionResult NONE = new BlockSubLevelCollisionCallback.CollisionResult(JOMLConversion.ZERO, false);
}
