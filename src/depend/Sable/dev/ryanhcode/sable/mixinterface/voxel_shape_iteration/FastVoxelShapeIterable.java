/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package dev.ryanhcode.sable.mixinterface.voxel_shape_iteration;

import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import java.util.Iterator;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface FastVoxelShapeIterable {
    public Iterator<BoundingBox3dc> sable$allBoxes();
}
