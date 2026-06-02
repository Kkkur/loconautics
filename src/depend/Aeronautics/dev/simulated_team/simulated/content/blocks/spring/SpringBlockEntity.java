/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor
 *  dev.ryanhcode.sable.api.physics.force.ForceTotal
 *  dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle
 *  dev.ryanhcode.sable.api.schematic.SubLevelSchematicSerializationContext
 *  dev.ryanhcode.sable.api.schematic.SubLevelSchematicSerializationContext$SchematicMapping
 *  dev.ryanhcode.sable.api.schematic.SubLevelSchematicSerializationContext$Type
 *  dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer
 *  dev.ryanhcode.sable.api.sublevel.SubLevelContainer
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.spring;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.physics.force.ForceTotal;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.schematic.SubLevelSchematicSerializationContext;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.simulated_team.simulated.content.blocks.spring.SpringBlock;
import dev.simulated_team.simulated.util.SimLevelUtil;
import java.util.List;
import java.util.UUID;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SpringBlockEntity
extends SmartBlockEntity
implements BlockEntitySubLevelActor {
    private static final Vector3d frictionForce = new Vector3d();
    private static final Vector3d frictionTorque = new Vector3d();
    private static final Vector3d localLinearVelocity = new Vector3d();
    private static final Vector3d localAngularVelocity = new Vector3d();
    private static final Vector3d expectedVelocity = new Vector3d();
    private static final Vector3d localDampingPointForce = new Vector3d();
    private static final double TIME_TO_SNAP = 0.75;
    protected LerpedFloat renderLength = LerpedFloat.linear();
    protected double desiredLength;
    protected boolean isController;
    protected boolean assembling;
    protected BlockPos partnerPos;
    @Nullable
    private UUID partnerSubLevel;
    private float ticksWithoutPartner = 0.0f;
    private ForceTotal forceTotal;
    private ForceTotal partnerForceTotal;
    private double snappingTime;

    public SpringBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.renderLength.chase(0.0, 0.2, LerpedFloat.Chaser.EXP);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    public Vector3d getCenter() {
        BlockState state = this.getBlockState();
        Direction facing = (Direction)state.getValue((Property)SpringBlock.FACING);
        Vec3i facingVec = facing.getNormal();
        double scale = 0.25;
        return JOMLConversion.atCenterOf((Vec3i)this.worldPosition).sub((double)facingVec.getX() * scale, (double)facingVec.getY() * scale, (double)facingVec.getZ() * scale);
    }

    @Nullable
    public String tryChangeLengthOrError(Level level, double delta) {
        if (delta > 0.0 && this.desiredLength >= 9.0) {
            return "max_length";
        }
        if (delta < 0.0 && this.desiredLength <= 1.0) {
            return "min_length";
        }
        double newDesiredLength = Math.clamp(this.desiredLength + delta, 1.0, 9.0);
        newDesiredLength = (double)Math.round(newDesiredLength / 0.25) * 0.25;
        double currentLength = Sable.HELPER.distanceSquaredWithSubLevels(level, (Position)this.worldPosition.getCenter(), (Position)this.partnerPos.getCenter()) + 1.0;
        if (currentLength > newDesiredLength * newDesiredLength * 4.0) {
            return "too_stretched";
        }
        if (currentLength < newDesiredLength * newDesiredLength / 4.0) {
            return "too_compressed";
        }
        this.desiredLength = newDesiredLength;
        this.setChanged();
        this.sendData();
        BlockEntity blockEntity = level.getBlockEntity(this.partnerPos);
        if (blockEntity instanceof SpringBlockEntity) {
            SpringBlockEntity partnerBE = (SpringBlockEntity)blockEntity;
            partnerBE.desiredLength = this.desiredLength;
            partnerBE.setChanged();
            partnerBE.sendData();
        }
        return null;
    }

    public double getRenderLength(float pt) {
        return this.renderLength.getValue(pt);
    }

    /*
     * Enabled aggressive block sorting
     */
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            this.renderLength.updateChaseTarget((float)this.desiredLength);
            this.renderLength.tickChaser();
            return;
        }
        if (this.snappingTime > 0.75) {
            this.level.destroyBlock(this.getBlockPos(), true);
        }
        if (this.partnerPos == null) {
            this.level.destroyBlock(this.getBlockPos(), true);
            return;
        }
        if (!SimLevelUtil.isAreaActuallyLoaded(this.getLevel(), this.partnerPos, 1)) return;
        if (this.getPairedSpring() == null) {
            float f = this.ticksWithoutPartner;
            this.ticksWithoutPartner = f + 1.0f;
            if (f > 20.0f) {
                this.level.destroyBlock(this.getBlockPos(), true);
                return;
            }
        }
        this.ticksWithoutPartner = 0.0f;
    }

    public void sable$physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
        ServerSubLevel partnerSubLevel;
        SpringBlockEntity partner = this.getPairedSpring();
        if (this.partnerPos == null || !SimLevelUtil.isAreaActuallyLoaded(this.getLevel(), this.partnerPos, 1) || partner == null || this.ticksWithoutPartner != 0.0f) {
            return;
        }
        ServerSubLevelContainer container = SubLevelContainer.getContainer((ServerLevel)subLevel.getLevel());
        SubLevelPhysicsSystem system = container.physicsSystem();
        system.updatePose(subLevel);
        ServerSubLevel serverSubLevel = partnerSubLevel = this.partnerSubLevel != null ? (ServerSubLevel)container.getSubLevel(this.partnerSubLevel) : null;
        if (this.partnerSubLevel != null && partnerSubLevel == null) {
            return;
        }
        if (partnerSubLevel != null && !this.isController) {
            return;
        }
        if (partnerSubLevel == subLevel) {
            return;
        }
        if (partnerSubLevel != null) {
            system.updatePose(partnerSubLevel);
        }
        BlockState state = this.getBlockState();
        SpringBlock.Size size = (SpringBlock.Size)((Object)state.getValue(SpringBlock.SIZE));
        Vector3d center = this.getCenter();
        Vector3d partnerCenter = partner.getCenter();
        Vector3d velo1 = Sable.HELPER.getVelocity(this.level, (Vector3dc)center, new Vector3d());
        Vector3d velo2 = Sable.HELPER.getVelocity(this.level, (Vector3dc)partnerCenter, new Vector3d());
        Vector3d positionA = subLevel.logicalPose().transformPosition((Vector3dc)center, new Vector3d());
        Vector3d positionB = partnerSubLevel != null ? partnerSubLevel.logicalPose().transformPosition(JOMLConversion.atCenterOf((Vec3i)this.partnerPos)) : JOMLConversion.atCenterOf((Vec3i)this.partnerPos);
        Vector3d relativeVelo = velo1.sub((Vector3dc)velo2);
        Vector3d dampingPointForce = new Vector3d((Vector3dc)relativeVelo);
        dampingPointForce.mul(-4.5);
        double desiredLength = (this.isController ? this.desiredLength : partner.desiredLength) - 0.75;
        this.snappingTime = positionA.distanceSquared((Vector3dc)positionB) > Mth.square((double)this.getSnappingDistance()) ? (this.snappingTime += timeStep) : 0.0;
        Vector3d globalNormalA = JOMLConversion.atLowerCornerOf((Vec3i)((Direction)state.getValue((Property)SpringBlock.FACING)).getNormal());
        Vector3d globalNormalB = JOMLConversion.atLowerCornerOf((Vec3i)((Direction)partner.getBlockState().getValue((Property)SpringBlock.FACING)).getNormal());
        subLevel.logicalPose().transformNormal(globalNormalA);
        if (partnerSubLevel != null) {
            partnerSubLevel.logicalPose().transformNormal(globalNormalB);
        }
        Vector3d torque = globalNormalA.cross((Vector3dc)globalNormalB.negate(), new Vector3d()).mul(20.0).mul(timeStep);
        Vector3d mediumNormal = globalNormalA.lerp((Vector3dc)globalNormalB, 0.5);
        Vector3d middle = new Vector3d(positionA.x, positionA.y, positionA.z).lerp((Vector3dc)positionB, 0.5);
        Vector3d desireA = middle.fma(-desiredLength / 2.0, (Vector3dc)mediumNormal, new Vector3d());
        Vector3d alignmentForce = desireA.sub(positionA.x, positionA.y, positionA.z);
        Vector3d hookesPointForce = alignmentForce.mul(145.0);
        Vector3d angVelo1 = new Vector3d();
        Vector3d angVelo2 = new Vector3d();
        handle.getAngularVelocity(angVelo1);
        if (partnerSubLevel != null) {
            RigidBodyHandle otherHandle = RigidBodyHandle.of((ServerSubLevel)partnerSubLevel);
            otherHandle.getAngularVelocity(angVelo2);
        }
        Vector3d relativeAngVelo = angVelo1.sub((Vector3dc)angVelo2);
        Vector3d dampingTorque = new Vector3d();
        if (mediumNormal.lengthSquared() > 0.0) {
            mediumNormal.normalize();
            double dot = mediumNormal.dot((Vector3dc)relativeAngVelo);
            relativeAngVelo.set((Vector3dc)mediumNormal).mul(dot);
            dampingTorque.fma(-2.0, (Vector3dc)relativeAngVelo);
        }
        double sizeScale = switch (size) {
            default -> throw new MatchException(null, null);
            case SpringBlock.Size.LARGE -> 8.0;
            case SpringBlock.Size.MEDIUM -> 1.0;
            case SpringBlock.Size.SMALL -> 0.5;
        };
        hookesPointForce.mul(sizeScale);
        torque.mul(sizeScale);
        dampingTorque.mul(sizeScale);
        dampingPointForce.mul(sizeScale);
        if (this.forceTotal == null || this.partnerForceTotal == null) {
            this.forceTotal = new ForceTotal();
            this.partnerForceTotal = new ForceTotal();
        }
        this.applyLocalDamping(subLevel, handle, this.forceTotal, (Vector3dc)center, (Vector3dc)dampingPointForce, (Vector3dc)dampingTorque, timeStep);
        this.forceTotal.applyImpulseAtPoint(subLevel, (Vector3dc)center, (Vector3dc)subLevel.logicalPose().transformNormalInverse(new Vector3d((Vector3dc)hookesPointForce)).mul(timeStep));
        this.forceTotal.applyLinearAndAngularImpulse(JOMLConversion.ZERO, (Vector3dc)subLevel.logicalPose().transformNormalInverse((Vector3dc)torque, new Vector3d()));
        handle.applyForcesAndReset(this.forceTotal);
        if (partnerSubLevel != null) {
            RigidBodyHandle partnerHandle = RigidBodyHandle.of((ServerSubLevel)partnerSubLevel);
            this.applyLocalDamping(partnerSubLevel, partnerHandle, this.partnerForceTotal, (Vector3dc)partnerCenter, (Vector3dc)dampingPointForce.negate(), (Vector3dc)dampingTorque.negate(), timeStep);
            this.partnerForceTotal.applyImpulseAtPoint(partnerSubLevel, (Vector3dc)partnerCenter, (Vector3dc)partnerSubLevel.logicalPose().transformNormalInverse(hookesPointForce).mul(-timeStep));
            this.partnerForceTotal.applyLinearAndAngularImpulse(JOMLConversion.ZERO, (Vector3dc)partnerSubLevel.logicalPose().transformNormalInverse(torque.negate()));
            partnerHandle.applyForcesAndReset(this.partnerForceTotal);
        }
    }

    private void applyLocalDamping(ServerSubLevel subLevel, RigidBodyHandle handle, ForceTotal forceTotal, Vector3dc worldSpringPos, Vector3dc dampingPointForce, Vector3dc dampingTorque, double timeStep) {
        Pose3d pose = subLevel.logicalPose();
        handle.getAngularVelocity(localAngularVelocity);
        handle.getLinearVelocity(localLinearVelocity);
        pose.orientation().transformInverse(localAngularVelocity);
        pose.orientation().transformInverse(localLinearVelocity);
        Vector3dc centerOfMass = subLevel.getMassTracker().getCenterOfMass();
        pose.orientation().transformInverse(dampingPointForce, localDampingPointForce);
        Vector3d angularDamping = new Vector3d();
        angularDamping.add(dampingTorque);
        pose.orientation().transformInverse(angularDamping);
        angularDamping.add((Vector3dc)worldSpringPos.sub(centerOfMass, new Vector3d()).cross((Vector3dc)localDampingPointForce));
        Vector3d linearDamping = new Vector3d();
        linearDamping.add((Vector3dc)localDampingPointForce);
        frictionForce.set((Vector3dc)linearDamping);
        frictionTorque.set((Vector3dc)angularDamping);
        expectedVelocity.set((Vector3dc)frictionForce);
        expectedVelocity.mul(subLevel.getMassTracker().getInverseMass());
        expectedVelocity.mul(timeStep);
        double forceScale = this.getClampingFactor((Vector3dc)localLinearVelocity, (Vector3dc)expectedVelocity);
        expectedVelocity.set((Vector3dc)frictionTorque);
        subLevel.getMassTracker().getInverseInertiaTensor().transform(expectedVelocity);
        expectedVelocity.mul(timeStep);
        double torqueScale = this.getClampingFactor((Vector3dc)localAngularVelocity, (Vector3dc)expectedVelocity);
        frictionForce.mul(forceScale * timeStep);
        frictionTorque.mul(torqueScale * timeStep);
        forceTotal.applyLinearAndAngularImpulse((Vector3dc)frictionForce, (Vector3dc)frictionTorque);
    }

    private double getClampingFactor(Vector3dc currentVelocity, Vector3dc expectedVelocityChange) {
        double k = -currentVelocity.dot(expectedVelocityChange);
        double v = currentVelocity.lengthSquared();
        if (k < 0.0) {
            return 0.0;
        }
        if (10.0 * k < v) {
            return 1.0;
        }
        if (v < 1.0E-10) {
            return v / (k + 1.0E-10);
        }
        return v * (1.0 - Math.exp(-k / v)) / k;
    }

    public @Nullable Iterable<@NotNull SubLevel> sable$getConnectionDependencies() {
        SubLevel subLevel;
        if (this.partnerSubLevel != null && (subLevel = SubLevelContainer.getContainer((Level)this.level).getSubLevel(this.partnerSubLevel)) != null) {
            return List.of(subLevel);
        }
        return List.of();
    }

    public void remove() {
        if (!this.level.isClientSide && this.partnerPos != null && !this.assembling) {
            this.level.destroyBlock(this.partnerPos, false);
        }
        this.partnerPos = null;
    }

    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putBoolean("Controller", this.isController);
        tag.putDouble("DesiredLength", this.desiredLength);
        if (this.partnerPos == null) {
            return;
        }
        SubLevelSchematicSerializationContext schematicContext = SubLevelSchematicSerializationContext.getCurrentContext();
        if (schematicContext == null || schematicContext.getType() == SubLevelSchematicSerializationContext.Type.PLACE) {
            BlockPos partnerPos;
            if (this.partnerSubLevel != null) {
                tag.putUUID("GoalSubLevel", this.partnerSubLevel);
            }
            if ((partnerPos = this.partnerPos) != null) {
                if (schematicContext != null && this.partnerSubLevel == null) {
                    partnerPos = (BlockPos)schematicContext.getSetupTransform().apply((Object)partnerPos);
                }
                tag.putLong("Goal", partnerPos.asLong());
            }
            return;
        }
        Object partnerPos = this.partnerPos;
        UUID id = this.partnerSubLevel;
        if (id != null) {
            SubLevelSchematicSerializationContext.SchematicMapping mapping = schematicContext.getMapping(id);
            if (mapping != null) {
                id = mapping.newUUID();
                partnerPos = (BlockPos)mapping.transform().apply(partnerPos);
            } else {
                id = null;
                partnerPos = null;
            }
        } else {
            partnerPos = schematicContext.getBoundingBox().contains(partnerPos.getX(), partnerPos.getY(), partnerPos.getZ()) ? (BlockPos)schematicContext.getPlaceTransform().apply(partnerPos) : null;
        }
        if (partnerPos != null) {
            tag.putLong("Goal", partnerPos.asLong());
        }
        if (id != null) {
            tag.putUUID("GoalSubLevel", id);
        }
    }

    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        SubLevelSchematicSerializationContext schematicContext;
        super.read(tag, registries, clientPacket);
        this.isController = tag.getBoolean("Controller");
        this.desiredLength = tag.getDouble("DesiredLength");
        if (this.renderLength.getValue() == 0.0f) {
            this.renderLength.setValue(this.desiredLength);
        }
        boolean isPlacingFromSchematic = (schematicContext = SubLevelSchematicSerializationContext.getCurrentContext()) != null && schematicContext.getType() == SubLevelSchematicSerializationContext.Type.PLACE;
        SubLevelSchematicSerializationContext.SchematicMapping mapping = null;
        if (tag.hasUUID("GoalSubLevel")) {
            UUID subLevelID = tag.getUUID("GoalSubLevel");
            if (isPlacingFromSchematic) {
                mapping = schematicContext.getMapping(subLevelID);
                if (mapping == null) {
                    this.partnerSubLevel = null;
                    this.partnerPos = null;
                    return;
                }
                subLevelID = mapping.newUUID();
            }
            this.partnerSubLevel = subLevelID;
        }
        if (tag.contains("Goal")) {
            BlockPos blockPos = BlockPos.of((long)tag.getLong("Goal"));
            if (isPlacingFromSchematic) {
                blockPos = mapping != null ? (BlockPos)mapping.transform().apply((Object)blockPos) : (BlockPos)schematicContext.getPlaceTransform().apply((Object)blockPos);
            }
            this.partnerPos = blockPos;
        }
    }

    public void setPartnerPos(BlockPos pos, UUID subLevel) {
        this.partnerPos = pos;
        this.partnerSubLevel = subLevel;
        this.sendData();
    }

    public boolean isController() {
        return this.isController;
    }

    public void setController(boolean b) {
        this.isController = b;
    }

    public double getSnappingDistance() {
        return this.desiredLength * 4.0 + 2.0;
    }

    public void lazyTick() {
        super.lazyTick();
        this.invalidateRenderBoundingBox();
    }

    public SpringBlockEntity getPairedSpring() {
        if (this.partnerPos == null) {
            return null;
        }
        BlockEntity be = this.level.getBlockEntity(this.partnerPos);
        if (be instanceof SpringBlockEntity) {
            return (SpringBlockEntity)be;
        }
        return null;
    }

    public AABB getRenderBoundingBox() {
        SpringBlockEntity goal = this.getPairedSpring();
        if (goal == null) {
            return new AABB(this.getBlockPos());
        }
        Vec3 center = this.getBlockPos().getCenter();
        Vec3 partnerPos = this.partnerPos.getCenter();
        SubLevel subLevel = Sable.HELPER.getContaining((BlockEntity)this);
        SubLevel partnerSubLevel = Sable.HELPER.getContaining(this.level, (Vec3i)this.partnerPos);
        if (partnerSubLevel != null) {
            partnerPos = partnerSubLevel.logicalPose().transformPosition(partnerPos);
        }
        if (subLevel != null) {
            partnerPos = subLevel.logicalPose().transformPositionInverse(partnerPos);
        }
        return new AABB(center, partnerPos).inflate(3.0);
    }

    public void setDesiredLength(double desiredLength) {
        this.desiredLength = desiredLength;
    }

    public UUID getPartnerSubLevelID() {
        return this.partnerSubLevel;
    }
}
