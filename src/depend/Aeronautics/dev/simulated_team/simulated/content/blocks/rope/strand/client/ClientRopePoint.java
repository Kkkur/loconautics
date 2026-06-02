/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.rope.strand.client;

import it.unimi.dsi.fastutil.objects.ObjectList;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public record ClientRopePoint(Vector3d position, Vector3d previousPosition, ObjectList<Snapshot> snapshots) {
    public Vector3d renderPos(float partialTicks, Vector3d dest) {
        return dest.set((Vector3dc)this.previousPosition).lerp((Vector3dc)this.position, (double)partialTicks);
    }

    public record Snapshot(double interpolationTick, Vector3d position) {
    }
}
