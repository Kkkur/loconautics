/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.core.BlockPos
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api.physics.callback;

import dev.ryanhcode.sable.companion.math.JOMLConversion;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public interface BlockSubLevelCollisionCallback {
    @ApiStatus.Internal
    default public double[] onCollision(int x, int y, int z, double x1, double y1, double z1, double impactVelocity) {
        CollisionResult result = this.sable$onCollision(new BlockPos(x, y, z), new Vector3d(x1, y1, z1), impactVelocity);
        Vector3dc motion = result.tangentMotion;
        return new double[]{motion.x(), motion.y(), motion.z(), result.removeCollision ? 1.0 : 0.0};
    }

    public CollisionResult sable$onCollision(BlockPos var1, Vector3d var2, double var3);

    public record CollisionResult(Vector3dc tangentMotion, boolean removeCollision) {
        public static final CollisionResult NONE = new CollisionResult(JOMLConversion.ZERO, false);
    }
}
