/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.physics.impl.rapier.collider;

import dev.ryanhcode.sable.api.physics.collider.VoxelColliderData;
import dev.ryanhcode.sable.physics.impl.rapier.Rapier3D;
import org.joml.Vector3dc;

public record RapierVoxelColliderData(int handle) implements VoxelColliderData
{
    public static final RapierVoxelColliderData EMPTY = new RapierVoxelColliderData(-1);

    @Override
    public void addBox(Vector3dc min, Vector3dc max) {
        Rapier3D.addVoxelColliderBox(this.handle, new double[]{min.x(), min.y(), min.z(), max.x(), max.y(), max.z()});
    }

    @Override
    public void clearBoxes() {
        Rapier3D.clearVoxelColliderBoxes(this.handle);
    }
}
