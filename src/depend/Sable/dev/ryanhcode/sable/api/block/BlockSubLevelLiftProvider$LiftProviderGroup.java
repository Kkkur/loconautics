/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  org.joml.Vector3d
 */
package dev.ryanhcode.sable.api.block;

import java.util.Set;
import net.minecraft.core.BlockPos;
import org.joml.Vector3d;

public static final class BlockSubLevelLiftProvider.LiftProviderGroup {
    private final Set<BlockPos> positions;
    private final Vector3d totalLift = new Vector3d();
    private final Vector3d liftCenter = new Vector3d();
    private final Vector3d totalDrag = new Vector3d();
    private final Vector3d dragCenter = new Vector3d();
    public double totalLiftStrength;
    public double totalDragStrength;

    public BlockSubLevelLiftProvider.LiftProviderGroup(Set<BlockPos> positions) {
        this.positions = positions;
    }

    public Set<BlockPos> positions() {
        return this.positions;
    }

    public Vector3d totalLift() {
        return this.totalLift;
    }

    public Vector3d liftCenter() {
        return this.liftCenter;
    }

    public Vector3d totalDrag() {
        return this.totalDrag;
    }

    public Vector3d dragCenter() {
        return this.dragCenter;
    }
}
