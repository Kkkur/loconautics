/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.api.physics.PhysicsPipeline
 *  dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis
 *  dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration
 *  dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle
 *  dev.ryanhcode.sable.api.physics.constraint.free.FreeConstraintConfiguration
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Player
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.physics_staff;

import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle;
import dev.ryanhcode.sable.api.physics.constraint.free.FreeConstraintConfiguration;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.simulated_team.simulated.config.server.physics.SimPhysics;
import dev.simulated_team.simulated.service.SimConfigService;
import java.util.UUID;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

private static class PhysicsStaffServerHandler.DragSession {
    private final UUID playerUUID;
    private final Vector3d plotAnchor = new Vector3d();
    private final Vector3d playerRelativeGoal = new Vector3d();
    private final Vector3d localGoal = new Vector3d();
    private final Quaterniond orientation = new Quaterniond();
    private final ServerSubLevel subLevel;
    private boolean markedForRemoval = false;
    private PhysicsConstraintHandle constraint = null;

    private PhysicsStaffServerHandler.DragSession(UUID playerUUID, ServerSubLevel subLevel) {
        this.playerUUID = playerUUID;
        this.subLevel = subLevel;
    }

    private void tick() {
        if (this.subLevel.isRemoved()) {
            this.markForRemoval();
        }
    }

    private void physicsTick(SubLevelPhysicsSystem physicsSystem) {
        if (this.subLevel.isRemoved()) {
            return;
        }
        if (this.constraint != null) {
            this.constraint.remove();
            this.constraint = null;
        }
        this.attachConstraint(physicsSystem);
        Player player = this.subLevel.getLevel().getPlayerByUUID(this.playerUUID);
        SimPhysics config = SimConfigService.INSTANCE.server().physics;
        if (player != null && this.constraint != null) {
            float angularStiffness = config.physicsStaffAngularStiffness.getF();
            float angularDamping = config.physicsStaffAngularDamping.getF();
            float linearStiffness = config.physicsStaffLinearStiffness.getF();
            float linearDamping = config.physicsStaffLinearDamping.getF();
            for (ConstraintJointAxis angularAxis : ConstraintJointAxis.ANGULAR) {
                this.constraint.setMotor(angularAxis, 0.0, (double)angularStiffness, (double)angularDamping, false, 0.0);
            }
            double partialTick = physicsSystem.getPartialPhysicsTick();
            double eyePosX = Mth.lerp((double)partialTick, (double)player.xOld, (double)player.getX());
            double eyePosY = Mth.lerp((double)partialTick, (double)player.yOld, (double)player.getY()) + (double)player.getEyeHeight();
            double eyePosZ = Mth.lerp((double)partialTick, (double)player.zOld, (double)player.getZ());
            this.localGoal.set((Vector3dc)this.playerRelativeGoal).add(eyePosX, eyePosY, eyePosZ);
            this.orientation.transformInverse(this.localGoal);
            this.constraint.setMotor(ConstraintJointAxis.LINEAR_X, this.localGoal.x(), (double)linearStiffness, (double)linearDamping, false, 0.0);
            this.constraint.setMotor(ConstraintJointAxis.LINEAR_Y, this.localGoal.y(), (double)linearStiffness, (double)linearDamping, false, 0.0);
            this.constraint.setMotor(ConstraintJointAxis.LINEAR_Z, this.localGoal.z(), (double)linearStiffness, (double)linearDamping, false, 0.0);
        }
    }

    private void attachConstraint(SubLevelPhysicsSystem physicsSystem) {
        PhysicsPipeline pipeline = physicsSystem.getPipeline();
        FreeConstraintConfiguration config = new FreeConstraintConfiguration(JOMLConversion.ZERO, (Vector3dc)this.plotAnchor, (Quaterniondc)this.orientation);
        this.constraint = pipeline.addConstraint(null, this.subLevel, (PhysicsConstraintConfiguration)config);
    }

    public boolean isMarkedForRemoval() {
        return this.markedForRemoval;
    }

    public void markForRemoval() {
        this.markedForRemoval = true;
    }

    public void onRemoved() {
        if (this.constraint != null) {
            this.constraint.remove();
        }
        this.constraint = null;
    }
}
