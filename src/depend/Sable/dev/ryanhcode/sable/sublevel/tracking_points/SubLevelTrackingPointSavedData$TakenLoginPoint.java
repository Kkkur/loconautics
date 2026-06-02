/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.sublevel.tracking_points;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public record SubLevelTrackingPointSavedData.TakenLoginPoint(Vector3dc position, @Nullable UUID subLevelId, @Nullable Vector3d localAnchor) {
}
