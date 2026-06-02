/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 */
package dev.ryanhcode.sable.sublevel.entity_collision;

import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.entity_collision.SubLevelEntityCollision;
import java.util.Map;
import net.minecraft.world.phys.Vec3;

public static class SubLevelEntityCollision.CollisionInfo {
    public SubLevel preTrackingSubLevel;
    public Vec3 preDeltaMovement;
    public boolean subLevelHorizontalCollision;
    public boolean horizontalCollision;
    public boolean verticalCollision;
    public boolean verticalCollisionBelow;
    public boolean minorHorizontalCollision;
    public Vec3 inheritedMotion;
    public Vec3 motion;
    public SubLevel trackingSubLevel;
    public Map<SubLevel, SubLevelEntityCollision.FirstCollisionInfo> firstCollisions;
}
