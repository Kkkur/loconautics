/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  org.joml.Vector3fc
 */
package dev.ryanhcode.sable.network.packets;

import dev.ryanhcode.sable.companion.math.Pose3d;
import org.joml.Vector3fc;

public record ClientboundSableSnapshotDualPacket.Entry(long plotCoordinate, Pose3d pose, Vector3fc linearVelocity, Vector3fc angularVelocity) {
}
