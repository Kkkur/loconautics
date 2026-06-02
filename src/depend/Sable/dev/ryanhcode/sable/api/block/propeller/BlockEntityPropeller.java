/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api.block.propeller;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public interface BlockEntityPropeller {
    public Direction getBlockDirection();

    public double getAirflow();

    public double getThrust();

    public boolean isActive();

    default public double getScaledThrust() {
        return -this.getThrust() * this.getAirflowScaling() * this.getCurrentAirPressure();
    }

    default public double getCurrentAirPressure() {
        Level level = this.getLevel();
        return DimensionPhysicsData.getAirPressure(level, (Vector3dc)Sable.HELPER.projectOutOfSubLevel(level, JOMLConversion.toJOML((Position)this.getBlockPos().getCenter())));
    }

    default public double getAirflowScaling() {
        double airflow = this.getAirflow();
        if (Math.abs(airflow) <= 0.001) {
            return 1.0;
        }
        Level level = this.getLevel();
        Vector3d pos = JOMLConversion.toJOML((Position)this.getBlockPos().getCenter());
        SubLevel subLevel = Sable.HELPER.getContaining(level, (Vec3i)this.getBlockPos());
        if (subLevel == null) {
            return 1.0;
        }
        Vector3d velocity = Sable.HELPER.getVelocity(level, subLevel, (Vector3dc)pos, new Vector3d());
        Vector3d thrustDirection = subLevel.logicalPose().transformNormal(JOMLConversion.atLowerCornerOf((Vec3i)this.getBlockDirection().getNormal()));
        return Math.clamp((airflow + velocity.dot(thrustDirection.x, thrustDirection.y, thrustDirection.z)) / airflow, 0.0, 1.0);
    }

    public Level getLevel();

    public BlockPos getBlockPos();
}
