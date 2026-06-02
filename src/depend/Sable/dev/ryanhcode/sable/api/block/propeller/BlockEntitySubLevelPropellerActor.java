/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api.block.propeller;

import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.block.propeller.BlockEntityPropeller;
import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import dev.ryanhcode.sable.api.physics.force.ForceGroups;
import dev.ryanhcode.sable.api.physics.force.QueuedForceGroup;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public interface BlockEntitySubLevelPropellerActor
extends BlockEntitySubLevelActor {
    public static final Vector3d THRUST_VECTOR = new Vector3d();
    public static final Vector3d THRUST_POSITION = new Vector3d();

    public BlockEntityPropeller getPropeller();

    @Override
    default public void sable$physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
        BlockEntityPropeller prop = this.getPropeller();
        if (prop.isActive()) {
            Vec3 thrustDirection = Vec3.atLowerCornerOf((Vec3i)prop.getBlockDirection().getNormal());
            this.applyForces(subLevel, thrustDirection, timeStep);
        }
    }

    default public void applyForces(ServerSubLevel subLevel, Vec3 thrustDirection, double timeStep) {
        BlockEntityPropeller prop = this.getPropeller();
        Vec3 thrust = thrustDirection.scale(prop.getScaledThrust() * timeStep);
        THRUST_POSITION.set((Vector3dc)JOMLConversion.atCenterOf((Vec3i)prop.getBlockPos()));
        THRUST_VECTOR.set(thrust.x, thrust.y, thrust.z);
        QueuedForceGroup forceGroup = subLevel.getOrCreateQueuedForceGroup((ForceGroup)ForceGroups.PROPULSION.get());
        forceGroup.applyAndRecordPointForce((Vector3dc)new Vector3d((Vector3dc)THRUST_POSITION), (Vector3dc)new Vector3d((Vector3dc)THRUST_VECTOR));
    }
}
