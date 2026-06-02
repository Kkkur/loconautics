/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.api.schematic.requirement.SpecialBlockEntityItemRequirement
 *  com.simibubi.create.content.contraptions.actors.roller.RollerBlock
 *  com.simibubi.create.content.equipment.clipboard.ClipboardCloneable
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.schematics.requirement.ItemRequirement
 *  com.simibubi.create.content.schematics.requirement.ItemRequirement$ItemUseType
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour$ValueSettings
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor
 *  dev.ryanhcode.sable.api.math.OrientedBoundingBox3d
 *  dev.ryanhcode.sable.api.physics.force.ForceTotal
 *  dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle
 *  dev.ryanhcode.sable.api.physics.mass.MassData
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  dev.ryanhcode.sable.mixinterface.clip_overwrite.ClipContextExtension
 *  dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyHelper
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  dev.simulated_team.simulated.multiloader.inventory.SingleSlotContainer
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.offroad.content.blocks.wheel_mount;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.api.schematic.requirement.SpecialBlockEntityItemRequirement;
import com.simibubi.create.content.contraptions.actors.roller.RollerBlock;
import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.ryanhcode.offroad.content.blocks.wheel_mount.WheelMountBlock;
import dev.ryanhcode.offroad.content.blocks.wheel_mount.WheelMountInventory;
import dev.ryanhcode.offroad.content.components.TireLike;
import dev.ryanhcode.offroad.data.OffroadLang;
import dev.ryanhcode.offroad.index.OffroadDataComponents;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.math.OrientedBoundingBox3d;
import dev.ryanhcode.sable.api.physics.force.ForceTotal;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.physics.mass.MassData;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.mixinterface.clip_overwrite.ClipContextExtension;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyHelper;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.multiloader.inventory.SingleSlotContainer;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.List;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class WheelMountBlockEntity
extends KineticBlockEntity
implements BlockEntitySubLevelActor,
Clearable,
ClipboardCloneable,
SpecialBlockEntityItemRequirement {
    private static final MutableComponent SCROLL_OPTION_TITLE = OffroadLang.translate("scroll_option.suspension_strength", new Object[0]).component();
    private static final double MAX_ALLOWED_EXTENSION = 0.65;
    private static final double NO_WHEEL_EXTENSION = 0.5;
    private static final Collection<WheelMountBlockEntity> queuedWheelMounts = new ObjectOpenHashSet();
    private final WheelMountInventory inventory;
    private SuspensionStrengthValueBehaviour strength;
    private int clientSteeringSignal;
    protected int clientSteeringSignalLeft;
    protected int clientSteeringSignalRight;
    private double extension;
    private double lastExtension;
    private double chasingYaw;
    private double lastChasingYaw;
    private double lastAngle;
    private double angle;
    private double angularVelocity;
    private double touchingFriction;
    private int lastServerSteeringSignal;
    private int lastServerSteeringSignalLeft;
    private int lastServerSteeringSignalRight;
    private boolean liftedUp;
    private final Vector3d queuedForcePos;
    private final Vector3d queuedForce;
    private final ForceTotal forceTotal;

    public WheelMountBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.lastExtension = this.extension = 0.5;
        this.angularVelocity = 0.0;
        this.touchingFriction = 1.0;
        this.liftedUp = false;
        this.queuedForcePos = new Vector3d();
        this.queuedForce = new Vector3d();
        this.forceTotal = new ForceTotal();
        this.inventory = new WheelMountInventory(this);
    }

    public static void applyAllBatchedForces(ServerLevel level, double timeStep) {
        for (WheelMountBlockEntity blockEntity : queuedWheelMounts) {
            if (blockEntity.isRemoved()) continue;
            blockEntity.applyBatchedForces();
        }
        queuedWheelMounts.clear();
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.strength = new SuspensionStrengthValueBehaviour((Component)SCROLL_OPTION_TITLE, (SmartBlockEntity)this, new SuspensionStrengthValueBox(0));
        behaviours.add((BlockEntityBehaviour)this.strength);
        this.strength.value = 10;
    }

    public ItemRequirement getRequiredItems(BlockState state) {
        ItemStack stack = this.inventory.slot.getStack();
        if (stack.isEmpty()) {
            return super.getRequiredItems(state);
        }
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, stack);
    }

    public static double fudgeFriction(double realValue) {
        if (realValue < 1.0) {
            return 0.1 + 0.9 * realValue;
        }
        return realValue;
    }

    public void sable$physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
        ItemStack item = this.getHeldItem();
        TireLike tire = (TireLike)item.get(OffroadDataComponents.TIRE);
        BlockPos blockPos = this.getBlockPos();
        if (tire == null) {
            return;
        }
        float radius = tire.radius();
        double suspensionRestDistance = 0.65;
        MassData massData = subLevel.getMassTracker();
        Direction facing = (Direction)this.getBlockState().getValue(WheelMountBlock.HORIZONTAL_FACING);
        Vec3 localPos = blockPos.relative(facing).getCenter();
        this.queuedForcePos.set(localPos.x, localPos.y, localPos.z);
        double normalMass = 1.0 / massData.getInverseNormalMass((Vector3dc)this.queuedForcePos, OrientedBoundingBox3d.UP);
        double effectiveStrength = this.strength.getValue();
        double normalMassScaling = Math.min(normalMass / effectiveStrength, 1.0) * 10.0;
        double strengthMul = effectiveStrength * normalMassScaling * 2.0;
        double springStrength = effectiveStrength * normalMassScaling * 40.0;
        double dampingStrength = effectiveStrength * normalMassScaling;
        Pose3d pose = subLevel.logicalPose();
        Direction.Axis axis = facing.getAxis();
        Vec3i normal = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis).getNormal();
        Vector3dc sideD = this.getRotatedWheelAxis(normal);
        normal = new Vec3i(normal.getZ(), 0, normal.getX());
        Vector3dc normalD = this.getRotatedWheelAxis(normal);
        TerrainCastResult extensionToTerrain = this.computeMaxExtensionToTerrain(normalD, (Pose3dc)pose);
        double maxExtension = extensionToTerrain.maxExtension();
        this.extension = Mth.lerp((double)1.0, (double)this.extension, (double)maxExtension);
        if (maxExtension > 0.65 + (double)radius + 0.25) {
            this.extension = 0.65;
            return;
        }
        double distance = 0.10833333333333334 + this.extension;
        double springLength = Mth.clamp((double)(distance - (double)radius), (double)0.0, (double)0.65);
        Vector3d velocity = Sable.HELPER.getVelocity(this.level, JOMLConversion.toJOML((Position)localPos));
        Vector3d localVelocity = pose.transformNormalInverse(velocity);
        double dampingForce = -localVelocity.y * dampingStrength;
        double springForce = ((0.65 - springLength) * springStrength + dampingForce) * timeStep;
        Vec3i rayHitNormal = extensionToTerrain.normal().getNormal();
        Vec3 localForce = new Vec3(springForce * (double)rayHitNormal.getX(), springForce * (double)rayHitNormal.getY(), springForce * (double)rayHitNormal.getZ());
        if (extensionToTerrain.subLevel() != null) {
            localForce = extensionToTerrain.subLevel().logicalPose().transformNormal(localForce);
        }
        localForce = pose.transformNormalInverse(localForce);
        this.queuedForce.set(localForce.x, localForce.y, localForce.z);
        this.touchingFriction = extensionToTerrain.minInteractingBlock() != null ? WheelMountBlockEntity.fudgeFriction(PhysicsBlockPropertyHelper.getFriction((BlockState)this.level.getBlockState(extensionToTerrain.minInteractingBlock()))) : 1.0;
        double brakeStrength = (double)this.level.getSignal(blockPos.above(), Direction.DOWN) / 15.0;
        double surfaceBraking = Math.min(this.touchingFriction, 1.0);
        double brakingFrictionStrength = (0.075 + brakeStrength * 0.3) * surfaceBraking;
        float kineticSpeed = facing.getAxis() == Direction.Axis.X ? this.getSpeed() : -this.getSpeed();
        this.queuedForce.fma(localVelocity.dot(normalD) * -brakingFrictionStrength * strengthMul * timeStep + (double)kineticSpeed * (1.0 - brakeStrength) * surfaceBraking * 1.75 * timeStep, normalD);
        this.queuedForce.fma(localVelocity.dot(sideD) * -0.6 * this.touchingFriction * strengthMul * timeStep, sideD);
        this.forceTotal.applyImpulseAtPoint(subLevel, (Vector3dc)this.queuedForcePos, (Vector3dc)this.queuedForce);
        queuedWheelMounts.add(this);
    }

    private void applyBatchedForces() {
        SubLevel subLevel = Sable.HELPER.getContaining((BlockEntity)this);
        if (subLevel == null) {
            return;
        }
        RigidBodyHandle handle = RigidBodyHandle.of((ServerSubLevel)((ServerSubLevel)subLevel));
        handle.applyForcesAndReset(this.forceTotal);
    }

    public void tick() {
        super.tick();
        ItemStack item = this.getHeldItem();
        TireLike tire = (TireLike)item.get(OffroadDataComponents.TIRE);
        this.lastChasingYaw = this.chasingYaw;
        this.chasingYaw = Mth.lerp((double)0.4, (double)this.chasingYaw, (double)this.computeYaw());
        if (!this.level.isClientSide) {
            return;
        }
        if (tire == null) {
            this.angle = 0.0;
            this.lastAngle = 0.0;
            this.lastExtension = this.extension;
            this.extension = Mth.lerp((double)0.6, (double)this.extension, (double)0.5);
            return;
        }
        float radius = tire.radius();
        SubLevel subLevel = Sable.HELPER.getContaining((BlockEntity)this);
        this.lastExtension = this.extension;
        this.extension = Mth.lerp((double)0.7, (double)this.extension, (double)this.computeMaxExtension(radius));
        Direction facing = (Direction)this.getBlockState().getValue(WheelMountBlock.HORIZONTAL_FACING);
        float speed = facing.getAxis() == Direction.Axis.X ? -this.getSpeed() : this.getSpeed();
        double rpt = (double)speed * Math.PI * 2.0 / 60.0 / 20.0 * (double)(15 - this.level.getSignal(this.getBlockPos().above(), Direction.DOWN)) / 15.0;
        double attemptedAngularVelocity = Mth.lerp((double)0.2, (double)this.angularVelocity, (double)rpt);
        if (subLevel == null || this.liftedUp) {
            this.angularVelocity = attemptedAngularVelocity;
            this.lastAngle = this.angle;
            this.angle += this.angularVelocity;
            return;
        }
        Vector3d velocity = Sable.HELPER.getVelocity(this.level, JOMLConversion.atCenterOf((Vec3i)this.getBlockPos().relative(facing)));
        Vector3d localVelocity = subLevel.logicalPose().transformNormalInverse(velocity).div(20.0);
        Direction.Axis axis = facing.getAxis();
        Vec3i normal = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis).getNormal();
        normal = new Vec3i(normal.getZ(), 0, normal.getX());
        Vector3dc normalD = this.getRotatedWheelAxis(normal);
        double translation = localVelocity.dot(normalD);
        double circumference = Math.PI * (double)radius * 2.0;
        double angularDelta = -translation / circumference * Math.PI * 2.0;
        if (this.touchingFriction < 1.0) {
            angularDelta = Mth.lerp((double)this.touchingFriction, (double)attemptedAngularVelocity, (double)angularDelta);
        }
        this.lastAngle = this.angle;
        this.angle += angularDelta;
        this.angularVelocity = angularDelta;
    }

    private double computeMaxExtension(float radius) {
        SubLevel subLevel = Sable.HELPER.getContaining((BlockEntity)this);
        if (subLevel == null) {
            return 0.65;
        }
        Direction facing = (Direction)this.getBlockState().getValue(WheelMountBlock.HORIZONTAL_FACING);
        Pose3d pose = subLevel.logicalPose();
        Direction.Axis axis = facing.getAxis();
        Vec3i normal = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis).getNormal();
        normal = new Vec3i(normal.getZ(), 0, normal.getX());
        Vector3dc rotatedAxis = this.getRotatedWheelAxis(normal);
        TerrainCastResult extensionToTerrain = this.computeMaxExtensionToTerrain(rotatedAxis, (Pose3dc)pose);
        double unclampedExtension = extensionToTerrain.maxExtension - (double)radius;
        this.liftedUp = unclampedExtension > 0.65;
        this.touchingFriction = extensionToTerrain.minInteractingBlock() == null ? 1.0 : WheelMountBlockEntity.fudgeFriction(PhysicsBlockPropertyHelper.getFriction((BlockState)this.level.getBlockState(extensionToTerrain.minInteractingBlock())));
        return Mth.clamp((double)unclampedExtension, (double)-0.45, (double)0.65);
    }

    public String getClipboardKey() {
        return "Wheel Mount";
    }

    public boolean writeToClipboard(// Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider registries, CompoundTag tag, Direction side) {
        return false;
    }

    public boolean readFromClipboard(// Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider registries, CompoundTag tag, Player player, Direction side, boolean simulate) {
        return false;
    }

    private TerrainCastResult computeMaxExtensionToTerrain(Vector3dc normalD, Pose3dc pose) {
        Direction facing = (Direction)this.getBlockState().getValue(WheelMountBlock.HORIZONTAL_FACING);
        Vec3 wheelPosCenter = this.getBlockPos().relative(facing).getCenter();
        double minExtension = 5.0;
        Direction minNormal = Direction.UP;
        SubLevel minHitSubLevel = null;
        BlockPos minInteractingBlock = null;
        for (int i = -1; i <= 1; ++i) {
            double dist;
            Vec3 localPosO = wheelPosCenter.add(JOMLConversion.toMojang((Vector3dc)normalD).scale((double)i));
            ClipContext clipContext = new ClipContext(localPosO, localPosO.subtract(0.0, 5.0, 0.0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
            ((ClipContextExtension)clipContext).sable$setIgnoredSubLevel(Sable.HELPER.getContaining((BlockEntity)this));
            BlockHitResult clipResult = this.level.clip(clipContext);
            if (clipResult.getType() == HitResult.Type.MISS) continue;
            SubLevel hitSubLevel = Sable.HELPER.getContaining(this.level, (Position)clipResult.getLocation());
            Vec3 localHitPos = pose.transformPositionInverse(hitSubLevel == null ? clipResult.getLocation() : hitSubLevel.logicalPose().transformPosition(clipResult.getLocation()));
            if (localHitPos.y > wheelPosCenter.y || localPosO.distanceTo(localHitPos) < 0.05 || (dist = wheelPosCenter.y - localHitPos.y) <= 1.0E-5) continue;
            Direction dir = clipResult.getDirection();
            Vector3d hitNormal = new Vector3d((double)dir.getStepX(), (double)dir.getStepY(), (double)dir.getStepZ());
            if (hitSubLevel != null) {
                hitSubLevel.logicalPose().transformNormal(hitNormal);
            }
            pose.transformNormalInverse(hitNormal);
            if (hitNormal.dot(0.0, 1.0, 0.0) < 0.5) continue;
            minExtension = Math.min(minExtension, dist);
            minNormal = clipResult.getDirection();
            minHitSubLevel = hitSubLevel;
            minInteractingBlock = clipResult.getBlockPos();
        }
        return new TerrainCastResult(minExtension, minNormal, minHitSubLevel, minInteractingBlock);
    }

    @NotNull
    private Vector3dc getRotatedWheelAxis(Vec3i normal) {
        Vector3d normalD = new Vector3d((double)normal.getX(), (double)normal.getY(), (double)normal.getZ());
        normalD.rotateY(this.getChasingYaw());
        return normalD;
    }

    protected double getChasingYaw() {
        return this.chasingYaw;
    }

    protected double getLerpedYaw(double partialTick) {
        return Mth.lerp((double)partialTick, (double)this.lastChasingYaw, (double)this.chasingYaw);
    }

    public float getLerpedAngle(float partialTicks) {
        return (float)Mth.lerp((double)partialTicks, (double)this.lastAngle, (double)this.angle);
    }

    public double getLerpedExtension(float partialTick) {
        return Mth.lerp((double)partialTick, (double)this.lastExtension, (double)this.extension);
    }

    protected double computeYaw() {
        int signal = this.getSteeringSignal();
        if (signal == 0) {
            return 0.0;
        }
        return (double)(-signal) / 15.0 * Math.PI / 4.0 * 0.6666666666666666;
    }

    protected int getSteeringSignal() {
        int signalRight;
        if (this.level.isClientSide) {
            return this.clientSteeringSignal;
        }
        BlockState state = this.getBlockState();
        Direction facing = (Direction)state.getValue(WheelMountBlock.HORIZONTAL_FACING);
        Direction d1 = facing.getClockWise();
        Direction d2 = facing.getCounterClockWise();
        BlockPos pos = this.getBlockPos();
        int signalLeft = this.level.getSignal(pos.relative(d1), d2);
        int signal = signalLeft - (signalRight = this.level.getSignal(pos.relative(d2), d1));
        boolean sendData = signal != this.lastServerSteeringSignal || signalLeft != this.lastServerSteeringSignalLeft || signalRight != this.lastServerSteeringSignalRight;
        this.lastServerSteeringSignal = signal;
        this.lastServerSteeringSignalLeft = signalLeft;
        this.lastServerSteeringSignalRight = signalRight;
        if (sendData) {
            this.sendData();
        }
        return signal;
    }

    public ItemStack getHeldItem() {
        return this.inventory.getItem(0);
    }

    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        tag.put("CurrentStack", this.getHeldItem().saveOptional(registries));
        if (clientPacket) {
            tag.putInt("SteeringSignalStrength", this.lastServerSteeringSignal);
            tag.putInt("SteeringSignalStrengthLeft", this.lastServerSteeringSignalLeft);
            tag.putInt("SteeringSignalStrengthRight", this.lastServerSteeringSignalRight);
        }
        super.write(tag, registries, clientPacket);
    }

    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        ItemStack stack = ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)tag.getCompound("CurrentStack"));
        this.inventory.suppressUpdate = true;
        this.inventory.slot.setStack(stack);
        this.inventory.suppressUpdate = false;
        if (clientPacket) {
            if (tag.contains("SteeringSignalStrength")) {
                this.clientSteeringSignal = tag.getInt("SteeringSignalStrength");
                this.clientSteeringSignalLeft = tag.getInt("SteeringSignalStrengthLeft");
                this.clientSteeringSignalRight = tag.getInt("SteeringSignalStrengthRight");
            }
            this.onStackChanged();
        }
        super.read(tag, registries, clientPacket);
    }

    public void clearContent() {
        this.inventory.clearContent();
    }

    public SingleSlotContainer getInventory() {
        return this.inventory;
    }

    public void onStackChanged() {
        this.invalidateRenderBoundingBox();
    }

    protected AABB createRenderBoundingBox() {
        AABB aabb = new AABB(this.getBlockPos());
        if (this.getHeldItem() != null && this.getHeldItem().has(OffroadDataComponents.TIRE)) {
            TireLike tire = (TireLike)this.getHeldItem().getComponents().get(OffroadDataComponents.TIRE);
            aabb = aabb.inflate((double)(tire.radius() + 1.0f));
        }
        return aabb;
    }

    private static class SuspensionStrengthValueBehaviour
    extends ScrollValueBehaviour {
        private static final int MAX_SUSPENSION_STRENGTH = 180;

        public SuspensionStrengthValueBehaviour(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
            super(label, be, slot);
            this.between(5, 180);
        }

        public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
            return new ValueSettingsBoard(this.label, 180, 20, (List)ImmutableList.of((Object)OffroadLang.translate("scroll_option.suspension_strength_label", new Object[0]).component()), new ValueSettingsFormatter(ValueSettingsBehaviour.ValueSettings::format));
        }
    }

    private static final class SuspensionStrengthValueBox
    extends ValueBoxTransform {
        private final int hOffset;

        public SuspensionStrengthValueBox(int hOffset) {
            this.hOffset = hOffset;
        }

        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            Direction facing = (Direction)state.getValue((Property)RollerBlock.FACING);
            float yRot = AngleHelper.horizontalAngle((Direction)facing) + 180.0f;
            ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(yRot)).rotateXDegrees(90.0f);
        }

        public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
            Vec3 offset = this.getLocalOffset(level, pos, state);
            if (offset == null) {
                return false;
            }
            return localHit.distanceTo(offset) < (double)(this.scale / 3.0f);
        }

        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            Direction facing = (Direction)state.getValue((Property)RollerBlock.FACING);
            float stateAngle = AngleHelper.horizontalAngle((Direction)facing) + 180.0f;
            return VecHelper.rotateCentered((Vec3)VecHelper.voxelSpace((double)(8 + this.hOffset), (double)15.5, (double)11.0), (double)stateAngle, (Direction.Axis)Direction.Axis.Y);
        }
    }

    private record TerrainCastResult(double maxExtension, @NotNull Direction normal, @Nullable SubLevel subLevel, @Nullable BlockPos minInteractingBlock) {
    }
}
