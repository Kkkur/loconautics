/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api.particle;

import dev.ryanhcode.sable.api.math.OrientedBoundingBox3d;
import org.joml.Vector3dc;

public interface ParticleSubLevelKickable {
    default public boolean sable$shouldCareAboutIntersectingSubLevels() {
        return true;
    }

    public boolean sable$shouldKickFromTracking();

    public boolean sable$shouldCollideWithTrackingSubLevel();

    default public Vector3dc sable$getUpDirection() {
        return OrientedBoundingBox3d.UP;
    }
}
