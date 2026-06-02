/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.entity.EntitySubLevelUtil
 *  dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis
 *  dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration
 *  dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle
 *  dev.ryanhcode.sable.api.physics.constraint.free.FreeConstraintConfiguration
 *  dev.ryanhcode.sable.api.physics.force.ForceTotal
 *  dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle
 *  dev.ryanhcode.sable.companion.SubLevelAccess
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.commands.arguments.EntityAnchorArgument$Anchor
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.network.syncher.SynchedEntityData$Builder
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.entity.projectile.ProjectileDeflection
 *  net.minecraft.world.entity.projectile.ThrowableProjectile
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.entities.launched_plunger;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle;
import dev.ryanhcode.sable.api.physics.constraint.free.FreeConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.force.ForceTotal;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.simulated_team.simulated.content.entities.launched_plunger.LaunchedPlungerServerHandler;
import dev.simulated_team.simulated.index.SimEntityDataSerializers;
import dev.simulated_team.simulated.index.SimItems;
import dev.simulated_team.simulated.index.SimSoundEvents;
import dev.simulated_team.simulated.mixin_interface.PlayerLaunchedPlungerExtension;
import dev.simulated_team.simulated.service.SimConfigService;
import java.util.Optional;
import java.util.UUID;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class LaunchedPlungerEntity
extends ThrowableProjectile {
    public static final EntityDataAccessor<Optional<UUID>> OTHER_PLUNGER = SynchedEntityData.defineId(LaunchedPlungerEntity.class, (EntityDataSerializer)EntityDataSerializers.OPTIONAL_UUID);
    public static final EntityDataAccessor<Integer> OTHER_PLUNGER_ID = SynchedEntityData.defineId(LaunchedPlungerEntity.class, (EntityDataSerializer)EntityDataSerializers.INT);
    public static final EntityDataAccessor<Direction> PLUNGED_DIRECTION = SynchedEntityData.defineId(LaunchedPlungerEntity.class, (EntityDataSerializer)EntityDataSerializers.DIRECTION);
    public static final EntityDataAccessor<BlockPos> PLUNGED_BLOCK_POS = SynchedEntityData.defineId(LaunchedPlungerEntity.class, (EntityDataSerializer)EntityDataSerializers.BLOCK_POS);
    public static final EntityDataAccessor<Boolean> IS_PLUNGED = SynchedEntityData.defineId(LaunchedPlungerEntity.class, (EntityDataSerializer)EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> IS_FIRST = SynchedEntityData.defineId(LaunchedPlungerEntity.class, (EntityDataSerializer)EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Vec3> TARGET_POS = SynchedEntityData.defineId(LaunchedPlungerEntity.class, SimEntityDataSerializers.VEC3);
    private static final double CLIENT_VELOCITY_SMOOTHING_ALPHA = 0.7;
    private final float animationOffset = (float)(Math.random() * 50.0);
    private final ForceTotal forceTotal = new ForceTotal();
    private final ForceTotal otherForceTotal = new ForceTotal();
    private Vec3 previousClientPosition = Vec3.ZERO;
    private Vec3 previousClientSmoothedVelocity = Vec3.ZERO;
    private Vec3 clientSmoothedVelocity = Vec3.ZERO;
    private Vec3 prevTargetPos = Vec3.ZERO;
    private LaunchedPlungerEntity cachedOtherPlunger;
    private int plungedTime = 0;
    private boolean addedToPlungerHandler = false;
    private PhysicsConstraintHandle constraint;
    private static final ProjectileDeflection DEFLECTION = (projectile, entity, randomSource) -> {
        Vec3 target = Vec3.ZERO;
        if (entity instanceof LaunchedPlungerEntity) {
            LaunchedPlungerEntity launchedPlungerEntity = (LaunchedPlungerEntity)entity;
            target = launchedPlungerEntity.getData(TARGET_POS);
            target = target.subtract(entity.position());
        }
        projectile.setDeltaMovement(projectile.getDeltaMovement().scale(0.8).add(target.normalize().scale(0.5)));
    };

    public LaunchedPlungerEntity(EntityType<? extends LaunchedPlungerEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static LaunchedPlungerEntity create(EntityType<? extends LaunchedPlungerEntity> entityType, Level world) {
        return new LaunchedPlungerEntity(entityType, world);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OTHER_PLUNGER_ID, (Object)-1);
        builder.define(TARGET_POS, (Object)Vec3.ZERO);
        builder.define(OTHER_PLUNGER, Optional.empty());
        builder.define(PLUNGED_BLOCK_POS, (Object)BlockPos.ZERO);
        builder.define(IS_FIRST, (Object)Boolean.FALSE);
        builder.define(PLUNGED_DIRECTION, (Object)Direction.UP);
        builder.define(IS_PLUNGED, (Object)false);
    }

    public void tick() {
        Level level = this.level();
        if (!level.isClientSide && !this.addedToPlungerHandler) {
            LaunchedPlungerServerHandler.addLaunchedPlunger(level, this);
            this.addedToPlungerHandler = true;
        }
        super.tick();
        Entity owner = this.getOwner();
        if (owner == null) {
            this.discard();
            return;
        }
        LaunchedPlungerEntity other = this.getOther();
        if (!level.isClientSide && other != null) {
            this.setData(TARGET_POS, other.position());
            double distance = Math.sqrt(Sable.HELPER.distanceSquaredWithSubLevels(level, (Position)this.position(), (Position)other.position()));
            if (distance > (double)((Integer)SimConfigService.INSTANCE.server().equipment.maxPlungerLauncherRange.get()).intValue()) {
                this.discard();
            }
        } else {
            this.setData(TARGET_POS, Vec3.ZERO);
            if (!level.isClientSide) {
                double distance;
                Player player;
                Player player2;
                if (((Optional)this.getEntityData().get(OTHER_PLUNGER)).isPresent() || ((Optional)this.getEntityData().get(OTHER_PLUNGER)).isEmpty() && owner instanceof Player && !(player2 = (Player)owner).isHolding((Item)SimItems.PLUNGER_LAUNCHER.get())) {
                    this.discard();
                } else if (((Optional)this.getEntityData().get(OTHER_PLUNGER)).isEmpty() && owner instanceof Player && (player = (Player)owner).isHolding((Item)SimItems.PLUNGER_LAUNCHER.get()) && (distance = Math.sqrt(Sable.HELPER.distanceSquaredWithSubLevels(this.level(), (Position)this.position(), (Position)player.position()))) > (double)((Integer)SimConfigService.INSTANCE.server().equipment.maxPlungerLauncherRange.get()).intValue()) {
                    this.discard();
                }
            } else if (owner instanceof Player) {
                Player player = (Player)owner;
                PlayerLaunchedPlungerExtension duck = (PlayerLaunchedPlungerExtension)player;
                duck.simulated$setLaunchedPlunger(this);
            }
        }
        if (this.getData(IS_PLUNGED).booleanValue()) {
            this.setDeltaMovement(Vec3.ZERO);
            this.lookAt(EntityAnchorArgument.Anchor.FEET, this.position().add(Vec3.atLowerCornerOf((Vec3i)this.getData(PLUNGED_DIRECTION).getNormal()).scale((double)0.05f)));
            if (this.firstTick) {
                this.plungedTime = 20;
            } else if (this.plungedTime < 20) {
                ++this.plungedTime;
            }
            BlockPos plungedPos = this.getData(PLUNGED_BLOCK_POS);
            if (level.isLoaded(plungedPos) && level.getBlockState(plungedPos).isAir()) {
                SubLevel containing = Sable.HELPER.getContaining((Entity)this);
                if (containing != null) {
                    EntitySubLevelUtil.kickEntity((SubLevel)containing, (Entity)this);
                }
                this.resetPlunged();
                level.playSound(null, this.getX(), this.getY(), this.getZ(), SimSoundEvents.PLUNGER_RELEASE.event(), SoundSource.PLAYERS, 1.0f, 1.0f);
            }
        }
        if (level.isClientSide) {
            this.tickSmoothing();
        }
    }

    private void tickSmoothing() {
        Vec3 movement = this.position().subtract(this.previousClientPosition);
        this.previousClientPosition = this.position();
        this.previousClientSmoothedVelocity = this.clientSmoothedVelocity;
        this.clientSmoothedVelocity = this.clientSmoothedVelocity.add(movement.subtract(this.clientSmoothedVelocity).scale(0.7));
    }

    public Vec3 getAttachmentPos(float partialTick) {
        Vec3 pos = this.getPosition(partialTick);
        if (this.isPlunged()) {
            pos = pos.add(Vec3.atLowerCornerOf((Vec3i)this.getData(PLUNGED_DIRECTION).getNormal()).scale(0.6));
        }
        return pos;
    }

    public Vec3 getAttachmentPos() {
        Vec3 pos = this.position();
        if (this.isPlunged()) {
            pos = pos.add(Vec3.atLowerCornerOf((Vec3i)this.getData(PLUNGED_DIRECTION).getNormal()).scale(0.6));
        }
        return pos;
    }

    public void physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
        LaunchedPlungerEntity otherPlunger = this.getOther();
        if (otherPlunger != null && this.isPlunged() && otherPlunger.isPlunged()) {
            RigidBodyHandle otherHandle;
            ServerSubLevel otherSubLevel = (ServerSubLevel)Sable.HELPER.getContaining((Entity)otherPlunger);
            if (subLevel == otherSubLevel) {
                return;
            }
            RigidBodyHandle rigidBodyHandle = otherHandle = otherSubLevel != null ? RigidBodyHandle.of((ServerSubLevel)otherSubLevel) : null;
            if (otherSubLevel == null || this.getId() > otherPlunger.getId()) {
                Vec3 attachmentPos = subLevel.logicalPose().transformPosition(this.getAttachmentPos());
                Vec3 otherAttachmentPos = otherPlunger.getAttachmentPos();
                if (otherSubLevel != null) {
                    otherAttachmentPos = otherSubLevel.logicalPose().transformPosition(otherAttachmentPos);
                }
                Vector3d force = JOMLConversion.toJOML((Position)otherAttachmentPos.subtract(attachmentPos));
                double maxLength = 12.0;
                if (force.lengthSquared() > 144.0) {
                    force.normalize(12.0);
                }
                force.mul(40.0);
                if (force.lengthSquared() < 1.0E-6) {
                    return;
                }
                Vector3d localAttachmentPos1 = JOMLConversion.toJOML((Position)this.getAttachmentPos());
                Vector3d velocity = Sable.HELPER.getVelocity(this.level(), (SubLevelAccess)subLevel, (Vector3dc)localAttachmentPos1, new Vector3d());
                if (otherHandle != null) {
                    Vector3d localAttachmentPos2 = JOMLConversion.toJOML((Position)otherPlunger.getAttachmentPos());
                    velocity.add((Vector3dc)Sable.HELPER.getVelocity(this.level(), (SubLevelAccess)otherSubLevel, localAttachmentPos2));
                }
                Vector3d localForce1 = subLevel.logicalPose().transformNormalInverse((Vector3dc)force, new Vector3d());
                double inverseNormalMass = subLevel.getMassTracker().getInverseNormalMass((Vector3dc)localAttachmentPos1, (Vector3dc)localForce1);
                if (otherHandle != null) {
                    Vector3d localAttachmentPos2 = JOMLConversion.toJOML((Position)otherPlunger.getAttachmentPos());
                    Vector3d localForce2 = otherSubLevel.logicalPose().transformNormalInverse((Vector3dc)force, new Vector3d());
                    inverseNormalMass = Math.max(inverseNormalMass, otherSubLevel.getMassTracker().getInverseNormalMass((Vector3dc)localAttachmentPos2, (Vector3dc)localForce2));
                }
                double scaleFactor = 1.0 / inverseNormalMass * 0.07 * timeStep;
                this.forceTotal.applyImpulseAtPoint(subLevel, (Vector3dc)localAttachmentPos1, (Vector3dc)localForce1.mul(scaleFactor));
                handle.applyForcesAndReset(this.forceTotal);
                if (otherHandle != null) {
                    Vector3d localForce2 = otherSubLevel.logicalPose().transformNormalInverse((Vector3dc)force, new Vector3d()).mul(-scaleFactor);
                    this.otherForceTotal.applyImpulseAtPoint(otherSubLevel, (Vector3dc)JOMLConversion.toJOML((Position)otherPlunger.getAttachmentPos()), (Vector3dc)localForce2);
                    otherHandle.applyForcesAndReset(this.otherForceTotal);
                }
            }
            if (this.constraint == null && otherPlunger.constraint == null) {
                FreeConstraintConfiguration constraintConfig = new FreeConstraintConfiguration((Vector3dc)JOMLConversion.toJOML((Position)this.getAttachmentPos()), (Vector3dc)JOMLConversion.toJOML((Position)otherPlunger.getAttachmentPos()), (Quaterniondc)new Quaterniond());
                SubLevelPhysicsSystem physicsSystem = SubLevelPhysicsSystem.get((Level)subLevel.getLevel());
                this.constraint = physicsSystem.getPipeline().addConstraint(subLevel, (ServerSubLevel)Sable.HELPER.getContaining((Entity)otherPlunger), (PhysicsConstraintConfiguration)constraintConfig);
                for (ConstraintJointAxis axis : ConstraintJointAxis.LINEAR) {
                    this.constraint.setMotor(axis, 0.0, 0.0, 1.0, false, 50.0);
                }
                for (ConstraintJointAxis axis : ConstraintJointAxis.ANGULAR) {
                    this.constraint.setMotor(axis, 0.0, 0.0, 0.25, false, 50.0);
                }
            }
        }
    }

    public Vec3 getLightProbePosition(float partialTicks) {
        return this.getAttachmentPos(partialTicks);
    }

    protected AABB makeBoundingBox() {
        AABB bb = this.getDimensions(this.getPose()).makeBoundingBox(this.position());
        return bb.move(0.0, -bb.getYsize() / 2.0, 0.0);
    }

    private void removeConstraint() {
        if (this.constraint != null) {
            this.constraint.remove();
        }
    }

    public int getPlungedTime() {
        return this.plungedTime;
    }

    public float getAnimationOffset() {
        return this.animationOffset;
    }

    protected boolean canHitEntity(Entity entity) {
        return false;
    }

    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        this.removeConstraint();
        this.noPhysics = true;
        SubLevel subLevel = Sable.HELPER.getContaining(this.level(), (Position)blockHitResult.getLocation());
        Vec3 selfPos = this.position();
        Vec3 diff = blockHitResult.getLocation().subtract(this.position());
        if (subLevel != null) {
            selfPos = subLevel.logicalPose().transformPositionInverse(selfPos);
            diff = blockHitResult.getLocation().subtract(selfPos);
        }
        this.setDeltaMovement(diff);
        Vec3 nudge = diff.normalize().scale((double)0.05f);
        this.setPosRaw(selfPos.x() - nudge.x(), selfPos.y() - nudge.y(), selfPos.z() - nudge.z());
        this.setData(IS_PLUNGED, true);
        this.setData(PLUNGED_DIRECTION, blockHitResult.getDirection());
        this.setData(PLUNGED_BLOCK_POS, blockHitResult.getBlockPos());
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SimSoundEvents.PLUNGER_PLACE.event(), SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    public void remove(Entity.RemovalReason removalReason) {
        this.removeConstraint();
        if (!this.level().isClientSide) {
            this.playSound(SimSoundEvents.PLUNGER_RELEASE.event(), 1.0f, 0.9f + 0.2f * this.level().random.nextFloat());
            LaunchedPlungerServerHandler.removeLaunchedPlunger(this.level(), this);
        }
        super.remove(removalReason);
        LaunchedPlungerEntity other = this.getOther();
        if (other != null && !other.isRemoved()) {
            other.discard();
            this.setOther(null);
        }
    }

    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        Optional<UUID> other = this.getData(OTHER_PLUNGER);
        other.ifPresent(value -> compoundTag.putUUID("OtherPlunger", value));
        compoundTag.put("PlungedBlockPos", NbtUtils.writeBlockPos((BlockPos)this.getData(PLUNGED_BLOCK_POS)));
        compoundTag.put("TargetPos", (Tag)VecHelper.writeNBT((Vec3)this.getData(TARGET_POS)));
        NBTHelper.writeEnum((CompoundTag)compoundTag, (String)"PlungedDir", (Enum)this.getData(PLUNGED_DIRECTION));
        compoundTag.putBoolean("IsPlunged", this.getData(IS_PLUNGED).booleanValue());
        compoundTag.putBoolean("IsFirst", this.getData(IS_FIRST).booleanValue());
        super.addAdditionalSaveData(compoundTag);
    }

    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.setData(IS_PLUNGED, compoundTag.getBoolean("IsPlunged"));
        this.setData(PLUNGED_DIRECTION, (Direction)NBTHelper.readEnum((CompoundTag)compoundTag, (String)"PlungedDir", Direction.class));
        this.setData(PLUNGED_BLOCK_POS, (BlockPos)NbtUtils.readBlockPos((CompoundTag)compoundTag, (String)"PlungedBlockPos").get());
        this.setData(TARGET_POS, VecHelper.readNBT((ListTag)((ListTag)compoundTag.get("TargetPos"))));
        this.setData(IS_FIRST, compoundTag.getBoolean("IsFirst"));
        if (compoundTag.contains("OtherPlunger")) {
            this.setData(OTHER_PLUNGER, Optional.of(compoundTag.getUUID("OtherPlunger")));
        }
        super.readAdditionalSaveData(compoundTag);
    }

    public void resetPlunged() {
        this.setData(IS_PLUNGED, false);
        this.setData(PLUNGED_DIRECTION, Direction.UP);
        this.setData(PLUNGED_BLOCK_POS, BlockPos.ZERO);
        this.noPhysics = false;
    }

    public ProjectileDeflection deflection(Projectile projectile) {
        this.discard();
        return DEFLECTION;
    }

    public boolean skipAttackInteraction(Entity entity) {
        if (entity instanceof Player) {
            this.discard();
        }
        return true;
    }

    public boolean isPickable() {
        return true;
    }

    public float getPickRadius() {
        return 0.0f;
    }

    @Nullable
    public ItemStack getPickResult() {
        return null;
    }

    public boolean isShiftKeyDown() {
        return true;
    }

    public LaunchedPlungerEntity getOther() {
        if (this.cachedOtherPlunger != null && this.cachedOtherPlunger.isAlive()) {
            return this.cachedOtherPlunger;
        }
        if (!this.level().isClientSide) {
            Optional<UUID> otherID = this.getData(OTHER_PLUNGER);
            if (otherID.isPresent()) {
                Entity entity = ((ServerLevel)this.level()).getEntity(otherID.get());
                if (entity instanceof LaunchedPlungerEntity) {
                    this.setData(OTHER_PLUNGER_ID, entity.getId());
                    this.cachedOtherPlunger = (LaunchedPlungerEntity)entity;
                } else {
                    this.setData(OTHER_PLUNGER_ID, -1);
                }
            }
        } else {
            Entity entity;
            int otherID = this.getData(OTHER_PLUNGER_ID);
            if (otherID != -1 && (entity = this.level().getEntity(otherID)) instanceof LaunchedPlungerEntity) {
                this.cachedOtherPlunger = (LaunchedPlungerEntity)entity;
            }
        }
        return this.cachedOtherPlunger;
    }

    public void setOther(LaunchedPlungerEntity other) {
        this.cachedOtherPlunger = other;
        this.setData(OTHER_PLUNGER, Optional.ofNullable(other == null ? null : other.getUUID()));
        this.setData(OTHER_PLUNGER_ID, other == null ? -1 : other.getId());
    }

    @NotNull
    public Vec3 getTarget() {
        return this.getClientTarget(1.0f);
    }

    @NotNull
    public Vec3 getClientTarget(float pt) {
        if (this.level().isClientSide()) {
            if (this.firstTick) {
                this.prevTargetPos = this.getData(TARGET_POS);
            }
            this.prevTargetPos = VecHelper.lerp((float)pt, (Vec3)this.prevTargetPos, (Vec3)this.getData(TARGET_POS));
            return this.prevTargetPos;
        }
        return this.getData(TARGET_POS);
    }

    public void load(CompoundTag compound) {
        super.load(compound);
        this.setOwner(null);
        this.ownerUUID = null;
    }

    public boolean isPlunged() {
        return this.getData(IS_PLUNGED);
    }

    public <T> T getData(EntityDataAccessor<T> accessor) {
        return (T)this.entityData.get(accessor);
    }

    public <T> void setData(EntityDataAccessor<T> accessor, T value) {
        this.entityData.set(accessor, value);
    }

    public Vec3 getClientSmoothedVelocity(float partialTicks) {
        return this.previousClientSmoothedVelocity.lerp(this.clientSmoothedVelocity, (double)partialTicks);
    }
}
