/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration
 *  dev.ryanhcode.sable.api.physics.constraint.fixed.FixedConstraintConfiguration
 *  dev.ryanhcode.sable.api.physics.constraint.fixed.FixedConstraintHandle
 *  dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer
 *  dev.ryanhcode.sable.api.sublevel.SubLevelContainer
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.docking_connector;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.fixed.FixedConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.fixed.FixedConstraintHandle;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

private record DockingConnectorBlockEntity.ConstraintSmoother(BlockPos otherConnectorPos, Quaterniond targetRelativeOrientation, Vector3d initialRelativePosition, Quaterniond initialRelativeOrientation) {
    private DockingConnectorBlockEntity.ConstraintSmoother(DockingConnectorBlockEntity otherConnectorPos, Quaterniondc targetRelativeOrientation, Vector3dc initialRelativePosition, Quaterniondc initialRelativeOrientation) {
        this(otherConnectorPos.getBlockPos(), new Quaterniond(targetRelativeOrientation), new Vector3d(initialRelativePosition), new Quaterniond(initialRelativeOrientation));
    }

    public void partialStep(DockingConnectorBlockEntity connector) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer((ServerLevel)((ServerLevel)connector.level));
        SubLevelPhysicsSystem physicsSystem = container.physicsSystem();
        double partialPhysicsTick = physicsSystem.getPartialPhysicsTick();
        double physicsTime = connector.feet.getValue((float)partialPhysicsTick);
        double lerpFactor = Mth.clamp((double)(physicsTime * physicsTime), (double)0.0, (double)1.0);
        this.step(container, connector, lerpFactor);
    }

    public void step(ServerSubLevelContainer container, DockingConnectorBlockEntity connector, double lerpFactor) {
        BlockPos pos = connector.getBlockPos();
        BlockEntity blockEntity = connector.level.getBlockEntity(this.otherConnectorPos);
        if (blockEntity instanceof DockingConnectorBlockEntity) {
            DockingConnectorBlockEntity other = (DockingConnectorBlockEntity)blockEntity;
            ServerSubLevel thisSubLevel = (ServerSubLevel)Sable.HELPER.getContaining(connector.level, (Vec3i)pos);
            ServerSubLevel otherSubLevel = (ServerSubLevel)Sable.HELPER.getContaining(connector.level, (Vec3i)this.otherConnectorPos);
            assert (thisSubLevel != null);
            Vector3d anchorPos = JOMLConversion.toJOML((Position)connector.getTipPosition());
            Vector3d otherAnchorPos = JOMLConversion.toJOML((Position)other.getTipPosition());
            double rotationLerpFactor = Mth.clamp((double)(lerpFactor * 2.0), (double)0.0, (double)1.0);
            if (connector.constraintHandle != null) {
                connector.constraintHandle.remove();
            }
            otherAnchorPos.fma(1.0 - lerpFactor, (Vector3dc)this.initialRelativePosition);
            FixedConstraintConfiguration constraint = new FixedConstraintConfiguration((Vector3dc)anchorPos, (Vector3dc)otherAnchorPos, (Quaterniondc)this.initialRelativeOrientation.slerp((Quaterniondc)this.targetRelativeOrientation, rotationLerpFactor, new Quaterniond()));
            connector.constraintHandle = (FixedConstraintHandle)container.physicsSystem().getPipeline().addConstraint(thisSubLevel, otherSubLevel, (PhysicsConstraintConfiguration)constraint);
        }
    }
}
