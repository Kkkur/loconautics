/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  org.jetbrains.annotations.ApiStatus$OverrideOnly
 */
package dev.ryanhcode.sable.api.physics.object.box;

import dev.ryanhcode.sable.companion.math.Pose3d;
import org.jetbrains.annotations.ApiStatus;

public interface BoxHandle {
    @ApiStatus.OverrideOnly
    public void readPose(Pose3d var1);

    public void remove();

    public void wakeUp();

    public int getRuntimeId();
}
