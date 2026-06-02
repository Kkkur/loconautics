/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$OverrideOnly
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api.physics.object.rope;

import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public interface RopeHandle {
    @ApiStatus.OverrideOnly
    public void readPose(List<Vector3d> var1);

    public void remove();

    public void setFirstSegmentLength(double var1);

    public void removeFirstPoint();

    public void addPoint(Vector3dc var1);

    public void setAttachment(AttachmentPoint var1, Vector3dc var2, ServerSubLevel var3);

    public void wakeUp();

    public static enum AttachmentPoint {
        START,
        END;

    }
}
