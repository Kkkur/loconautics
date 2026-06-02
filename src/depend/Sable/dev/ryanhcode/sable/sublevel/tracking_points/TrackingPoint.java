/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 */
package dev.ryanhcode.sable.sublevel.tracking_points;

import dev.ryanhcode.sable.sublevel.storage.holding.GlobalSavedSubLevelPointer;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public record TrackingPoint(boolean inSubLevel, @Nullable UUID subLevelID, @Nullable GlobalSavedSubLevelPointer lastSavedSubLevelPointer, Vector3d point, Vector3d globalPlaceholderPosition) {
}
