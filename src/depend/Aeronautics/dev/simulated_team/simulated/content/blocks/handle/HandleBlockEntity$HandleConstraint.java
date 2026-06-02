/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis
 *  dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration
 *  dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle
 *  dev.ryanhcode.sable.api.physics.constraint.free.FreeConstraintConfiguration
 *  dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle
 *  dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer
 *  dev.ryanhcode.sable.api.sublevel.SubLevelContainer
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem
 *  net.minecraft.core.Position
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.handle;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle;
import dev.ryanhcode.sable.api.physics.constraint.free.FreeConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.simulated_team.simulated.service.SimConfigService;
import java.util.UUID;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

private class HandleBlockEntity.HandleConstraint {
    private static final double CONSTRAINT_DAMPING = 30.0;
    private static final double CONSTRAINT_STIFFNESS = 240.0;
    private final UUID playerId;
    private float scrollDistance;
    @Nullable
    private PhysicsConstraintHandle constraintHandle;

    public HandleBlockEntity.HandleConstraint(UUID playerId, float scrollDistance, PhysicsConstraintHandle constraintHandle) {
        this.playerId = playerId;
        this.scrollDistance = scrollDistance;
        this.constraintHandle = constraintHandle;
    }

    public void physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle) {
        this.removeJoint();
        Player player = HandleBlockEntity.this.level.getPlayerByUUID(this.playerId);
        if (player == null) {
            return;
        }
        if (!(player.onGround() || player.isInWater() || player.getAbilities().flying || player.onClimbable())) {
            return;
        }
        SubLevel standingSubLevel = Sable.HELPER.getTrackingSubLevel((Entity)player);
        if (standingSubLevel == subLevel) {
            return;
        }
        Vector3d constraintGoal = JOMLConversion.toJOML((Position)player.getEyePosition().add(player.getLookAngle().scale(Math.max(2.0, (double)this.scrollDistance))));
        Vector3d constraintPosition = HandleBlockEntity.this.getGrabCenter();
        double validRange = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue() + 2.0;
        double currentDistance = Sable.HELPER.distanceSquaredWithSubLevels(HandleBlockEntity.this.level, (Vector3dc)constraintGoal, (Vector3dc)constraintPosition);
        if (Mth.equal((float)-1.0f, (float)this.scrollDistance) || currentDistance > validRange * validRange) {
            return;
        }
        ServerSubLevelContainer container = SubLevelContainer.getContainer((ServerLevel)subLevel.getLevel());
        assert (container != null);
        SubLevelPhysicsSystem physicsSystem = container.physicsSystem();
        this.constraintHandle = physicsSystem.getPipeline().addConstraint(null, subLevel, (PhysicsConstraintConfiguration)new FreeConstraintConfiguration((Vector3dc)constraintGoal, (Vector3dc)constraintPosition, (Quaterniondc)new Quaterniond()));
        double maxForce = SimConfigService.INSTANCE.server().physics.handleMaxForce.getF();
        for (ConstraintJointAxis axis : ConstraintJointAxis.LINEAR) {
            this.constraintHandle.setMotor(axis, 0.0, 240.0, 30.0, true, maxForce);
        }
        for (ConstraintJointAxis axis : ConstraintJointAxis.ANGULAR) {
            this.constraintHandle.setMotor(axis, 0.0, 0.0, 4.5, true, maxForce);
        }
    }

    public boolean hasJoint() {
        return this.constraintHandle != null;
    }

    public void removeJoint() {
        if (this.constraintHandle != null) {
            this.constraintHandle.remove();
            this.constraintHandle = null;
        }
    }

    public void setScrollDistance(float desiredRange) {
        this.scrollDistance = (float)Math.min((double)desiredRange, 2.5);
    }
}
