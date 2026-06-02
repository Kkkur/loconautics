/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.network.syncher.SynchedEntityData$Builder
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 *  net.minecraft.world.entity.vehicle.MinecartFurnace
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseRailBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.RailShape
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.items.IItemHandler
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllAttachmentTypes;
import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionRelocationPacket;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.contraptions.bearing.StabilizedContraption;
import com.simibubi.create.content.contraptions.minecart.MinecartSim2020;
import com.simibubi.create.content.contraptions.minecart.capability.CapabilityMinecartController;
import com.simibubi.create.content.contraptions.minecart.capability.MinecartController;
import com.simibubi.create.content.contraptions.mounted.CartAssemblerBlockEntity;
import com.simibubi.create.content.contraptions.mounted.MountedContraption;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.mixin.accessor.MinecartFurnaceAccessor;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class OrientedContraptionEntity
extends AbstractContraptionEntity {
    private static final Ingredient FUEL_ITEMS = Ingredient.of((ItemLike[])new ItemLike[]{Items.COAL, Items.CHARCOAL});
    private static final EntityDataAccessor<Optional<UUID>> COUPLING = SynchedEntityData.defineId(OrientedContraptionEntity.class, (EntityDataSerializer)EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Direction> INITIAL_ORIENTATION = SynchedEntityData.defineId(OrientedContraptionEntity.class, (EntityDataSerializer)EntityDataSerializers.DIRECTION);
    protected Vec3 motionBeforeStall = Vec3.ZERO;
    protected boolean forceAngle;
    private boolean attachedExtraInventories = false;
    private boolean manuallyPlaced;
    public float prevYaw;
    public float yaw;
    public float targetYaw;
    public float prevPitch;
    public float pitch;
    public int nonDamageTicks = 10;

    public OrientedContraptionEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public static OrientedContraptionEntity create(Level world, Contraption contraption, Direction initialOrientation) {
        OrientedContraptionEntity entity = new OrientedContraptionEntity((EntityType)AllEntityTypes.ORIENTED_CONTRAPTION.get(), world);
        entity.setContraption(contraption);
        entity.setInitialOrientation(initialOrientation);
        entity.startAtInitialYaw();
        return entity;
    }

    public static OrientedContraptionEntity createAtYaw(Level world, Contraption contraption, Direction initialOrientation, float initialYaw) {
        OrientedContraptionEntity entity = OrientedContraptionEntity.create(world, contraption, initialOrientation);
        entity.startAtYaw(initialYaw);
        entity.manuallyPlaced = true;
        return entity;
    }

    public void setInitialOrientation(Direction direction) {
        this.entityData.set(INITIAL_ORIENTATION, (Object)direction);
    }

    public Direction getInitialOrientation() {
        return (Direction)this.entityData.get(INITIAL_ORIENTATION);
    }

    @Override
    public float getYawOffset() {
        return this.getInitialYaw();
    }

    public float getInitialYaw() {
        return (this.isInitialOrientationPresent() ? (Direction)this.entityData.get(INITIAL_ORIENTATION) : Direction.SOUTH).toYRot();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(COUPLING, Optional.empty());
        builder.define(INITIAL_ORIENTATION, (Object)Direction.UP);
    }

    @Override
    public AbstractContraptionEntity.ContraptionRotationState getRotationState() {
        AbstractContraptionEntity.ContraptionRotationState crs = new AbstractContraptionEntity.ContraptionRotationState();
        float yawOffset = this.getYawOffset();
        crs.zRotation = this.pitch;
        crs.yRotation = -this.yaw + yawOffset;
        if (this.pitch != 0.0f && this.yaw != 0.0f) {
            crs.secondYRotation = -this.yaw;
            crs.yRotation = yawOffset;
        }
        return crs;
    }

    public void stopRiding() {
        if (!this.level().isClientSide && this.isAlive()) {
            this.disassemble();
        }
        super.stopRiding();
    }

    @Override
    protected void readAdditional(CompoundTag compound, boolean spawnPacket) {
        ListTag vecNBT;
        super.readAdditional(compound, spawnPacket);
        if (compound.contains("InitialOrientation")) {
            this.setInitialOrientation((Direction)NBTHelper.readEnum((CompoundTag)compound, (String)"InitialOrientation", Direction.class));
        }
        this.yaw = compound.getFloat("Yaw");
        this.pitch = compound.getFloat("Pitch");
        this.manuallyPlaced = compound.getBoolean("Placed");
        if (compound.contains("ForceYaw")) {
            this.startAtYaw(compound.getFloat("ForceYaw"));
        }
        if (!(vecNBT = compound.getList("CachedMotion", 6)).isEmpty()) {
            this.motionBeforeStall = new Vec3(vecNBT.getDouble(0), vecNBT.getDouble(1), vecNBT.getDouble(2));
            if (!this.motionBeforeStall.equals((Object)Vec3.ZERO)) {
                this.prevYaw = this.yaw += OrientedContraptionEntity.yawFromVector(this.motionBeforeStall);
                this.targetYaw = this.yaw;
            }
            this.setDeltaMovement(Vec3.ZERO);
        }
        this.setCouplingId(compound.contains("OnCoupling") ? compound.getUUID("OnCoupling") : null);
    }

    @Override
    protected void writeAdditional(CompoundTag compound, HolderLookup.Provider registries, boolean spawnPacket) {
        Direction optional;
        super.writeAdditional(compound, registries, spawnPacket);
        if (this.motionBeforeStall != null) {
            compound.put("CachedMotion", (Tag)this.newDoubleList(new double[]{this.motionBeforeStall.x, this.motionBeforeStall.y, this.motionBeforeStall.z}));
        }
        if ((optional = (Direction)this.entityData.get(INITIAL_ORIENTATION)).getAxis().isHorizontal()) {
            NBTHelper.writeEnum((CompoundTag)compound, (String)"InitialOrientation", (Enum)optional);
        }
        if (this.forceAngle) {
            compound.putFloat("ForceYaw", this.yaw);
            this.forceAngle = false;
        }
        compound.putBoolean("Placed", this.manuallyPlaced);
        compound.putFloat("Yaw", this.yaw);
        compound.putFloat("Pitch", this.pitch);
        if (this.getCouplingId() != null) {
            compound.putUUID("OnCoupling", this.getCouplingId());
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (INITIAL_ORIENTATION.equals(key) && this.isInitialOrientationPresent() && !this.manuallyPlaced) {
            this.startAtInitialYaw();
        }
    }

    public boolean isInitialOrientationPresent() {
        return ((Direction)this.entityData.get(INITIAL_ORIENTATION)).getAxis().isHorizontal();
    }

    public void startAtInitialYaw() {
        this.startAtYaw(this.getInitialYaw());
    }

    public void startAtYaw(float yaw) {
        this.yaw = this.prevYaw = yaw;
        this.targetYaw = this.prevYaw;
        this.forceAngle = true;
    }

    @Override
    public Vec3 applyRotation(Vec3 localPos, float partialTicks) {
        localPos = VecHelper.rotate((Vec3)localPos, (double)this.getInitialYaw(), (Direction.Axis)Direction.Axis.Y);
        localPos = VecHelper.rotate((Vec3)localPos, (double)this.getViewXRot(partialTicks), (Direction.Axis)Direction.Axis.Z);
        localPos = VecHelper.rotate((Vec3)localPos, (double)this.getViewYRot(partialTicks), (Direction.Axis)Direction.Axis.Y);
        return localPos;
    }

    @Override
    public Vec3 reverseRotation(Vec3 localPos, float partialTicks) {
        localPos = VecHelper.rotate((Vec3)localPos, (double)(-this.getViewYRot(partialTicks)), (Direction.Axis)Direction.Axis.Y);
        localPos = VecHelper.rotate((Vec3)localPos, (double)(-this.getViewXRot(partialTicks)), (Direction.Axis)Direction.Axis.Z);
        localPos = VecHelper.rotate((Vec3)localPos, (double)(-this.getInitialYaw()), (Direction.Axis)Direction.Axis.Y);
        return localPos;
    }

    public float getViewYRot(float partialTicks) {
        return -(partialTicks == 1.0f ? this.yaw : AngleHelper.angleLerp((double)partialTicks, (double)this.prevYaw, (double)this.yaw));
    }

    public float getViewXRot(float partialTicks) {
        return partialTicks == 1.0f ? this.pitch : AngleHelper.angleLerp((double)partialTicks, (double)this.prevPitch, (double)this.pitch);
    }

    @Override
    protected void tickContraption() {
        boolean rotating;
        Entity e;
        if (this.nonDamageTicks > 0) {
            --this.nonDamageTicks;
        }
        if ((e = this.getVehicle()) == null) {
            return;
        }
        boolean rotationLock = false;
        boolean pauseWhileRotating = false;
        boolean wasStalled = this.isStalled();
        Contraption contraption = this.contraption;
        if (contraption instanceof MountedContraption) {
            MountedContraption mountedContraption = (MountedContraption)contraption;
            rotationLock = mountedContraption.rotationMode == CartAssemblerBlockEntity.CartMovementMode.ROTATION_LOCKED;
            pauseWhileRotating = mountedContraption.rotationMode == CartAssemblerBlockEntity.CartMovementMode.ROTATE_PAUSED;
        }
        Entity riding = e;
        while (riding.getVehicle() != null && !(this.contraption instanceof StabilizedContraption)) {
            riding = riding.getVehicle();
        }
        boolean isOnCoupling = false;
        UUID couplingId = this.getCouplingId();
        boolean bl = isOnCoupling = couplingId != null && riding instanceof AbstractMinecart;
        if (!this.attachedExtraInventories) {
            this.attachInventoriesFromRidingCarts(riding, isOnCoupling, couplingId);
            this.attachedExtraInventories = true;
        }
        if (!(rotating = this.updateOrientation(rotationLock, wasStalled, riding, isOnCoupling)) || !pauseWhileRotating) {
            this.tickActors();
        }
        boolean isStalled = this.isStalled();
        MinecartController controller = (MinecartController)riding.getData(AllAttachmentTypes.MINECART_CONTROLLER);
        if (controller.isPresent()) {
            if (!this.level().isClientSide()) {
                controller.setStalledExternally(isStalled);
            }
        } else {
            if (isStalled) {
                if (!wasStalled) {
                    this.motionBeforeStall = riding.getDeltaMovement();
                }
                riding.setDeltaMovement(0.0, 0.0, 0.0);
            }
            if (wasStalled && !isStalled) {
                riding.setDeltaMovement(this.motionBeforeStall);
                this.motionBeforeStall = Vec3.ZERO;
            }
        }
        if (this.level().isClientSide) {
            return;
        }
        if (!this.isStalled()) {
            if (isOnCoupling) {
                Couple<MinecartController> coupledCarts = this.getCoupledCartsIfPresent();
                if (coupledCarts == null) {
                    return;
                }
                coupledCarts.map(MinecartController::cart).forEach(this::powerFurnaceCartWithFuelFromStorage);
                return;
            }
            this.powerFurnaceCartWithFuelFromStorage(riding);
        }
    }

    protected boolean updateOrientation(boolean rotationLock, boolean wasStalled, Entity riding, boolean isOnCoupling) {
        if (isOnCoupling) {
            Couple<MinecartController> coupledCarts = this.getCoupledCartsIfPresent();
            if (coupledCarts == null) {
                return false;
            }
            Vec3 positionVec = ((MinecartController)coupledCarts.getFirst()).cart().position();
            Vec3 coupledVec = ((MinecartController)coupledCarts.getSecond()).cart().position();
            double diffX = positionVec.x - coupledVec.x;
            double diffY = positionVec.y - coupledVec.y;
            double diffZ = positionVec.z - coupledVec.z;
            this.prevYaw = this.yaw;
            this.prevPitch = this.pitch;
            this.yaw = (float)(Mth.atan2((double)diffZ, (double)diffX) * 180.0 / Math.PI);
            this.pitch = (float)(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ)) * 180.0 / Math.PI);
            if (this.getCouplingId().equals(riding.getUUID())) {
                this.pitch *= -1.0f;
                this.yaw += 180.0f;
            }
            return false;
        }
        Contraption positionVec = this.contraption;
        if (positionVec instanceof StabilizedContraption) {
            StabilizedContraption stabilized = (StabilizedContraption)positionVec;
            if (!(riding instanceof OrientedContraptionEntity)) {
                return false;
            }
            OrientedContraptionEntity parent = (OrientedContraptionEntity)riding;
            Direction facing = stabilized.getFacing();
            if (facing.getAxis().isVertical()) {
                return false;
            }
            this.prevYaw = this.yaw;
            this.yaw = AngleHelper.wrapAngle180((float)(this.getInitialYaw() - parent.getInitialYaw())) - parent.getViewYRot(1.0f);
            return false;
        }
        this.prevYaw = this.yaw;
        if (wasStalled) {
            return false;
        }
        boolean rotating = false;
        Vec3 movementVector = riding.getDeltaMovement();
        Vec3 locationDiff = riding.position().subtract(riding.xo, riding.yo, riding.zo);
        if (!(riding instanceof AbstractMinecart)) {
            movementVector = locationDiff;
        }
        Vec3 motion = movementVector.normalize();
        if (!rotationLock) {
            if (riding instanceof AbstractMinecart) {
                AbstractMinecart minecartEntity = (AbstractMinecart)riding;
                BlockPos railPosition = minecartEntity.getCurrentRailPosition();
                BlockState blockState = this.level().getBlockState(railPosition);
                Block block = blockState.getBlock();
                if (block instanceof BaseRailBlock) {
                    BaseRailBlock abstractRailBlock = (BaseRailBlock)block;
                    RailShape railDirection = abstractRailBlock.getRailDirection(blockState, (BlockGetter)this.level(), railPosition, minecartEntity);
                    motion = VecHelper.project((Vec3)motion, (Vec3)MinecartSim2020.getRailVec(railDirection));
                }
            }
            if (motion.length() > 0.0) {
                this.targetYaw = OrientedContraptionEntity.yawFromVector(motion);
                if (this.targetYaw < 0.0f) {
                    this.targetYaw += 360.0f;
                }
                if (this.yaw < 0.0f) {
                    this.yaw += 360.0f;
                }
            }
            this.prevYaw = this.yaw;
            float maxApproachSpeed = (float)(motion.length() * 12.0 / Math.max(1.0, this.getBoundingBox().getXsize() / 6.0));
            float yawHint = AngleHelper.getShortestAngleDiff((double)this.yaw, (double)OrientedContraptionEntity.yawFromVector(locationDiff));
            float approach = AngleHelper.getShortestAngleDiff((double)this.yaw, (double)this.targetYaw, (float)yawHint);
            approach = Mth.clamp((float)approach, (float)(-maxApproachSpeed), (float)maxApproachSpeed);
            this.yaw += approach;
            if (Math.abs(AngleHelper.getShortestAngleDiff((double)this.yaw, (double)this.targetYaw)) < 1.0f) {
                this.yaw = this.targetYaw;
            } else {
                rotating = true;
            }
        }
        return rotating;
    }

    protected void powerFurnaceCartWithFuelFromStorage(Entity riding) {
        ItemStack coal;
        MountedItemStorageWrapper fuelItems;
        int fuel;
        if (!(riding instanceof MinecartFurnace)) {
            return;
        }
        MinecartFurnace furnaceCart = (MinecartFurnace)riding;
        if (!(riding instanceof MinecartFurnaceAccessor)) {
            return;
        }
        MinecartFurnaceAccessor furnaceCartAccessor = (MinecartFurnaceAccessor)riding;
        int fuelBefore = fuel = furnaceCartAccessor.create$getFuel();
        double pushX = furnaceCart.xPush;
        double pushZ = furnaceCart.zPush;
        int i = Mth.floor((double)furnaceCart.getX());
        int j = Mth.floor((double)furnaceCart.getY());
        int k = Mth.floor((double)furnaceCart.getZ());
        if (furnaceCart.level().getBlockState(new BlockPos(i, j - 1, k)).is(BlockTags.RAILS)) {
            --j;
        }
        BlockPos blockpos = new BlockPos(i, j, k);
        BlockState blockstate = this.level().getBlockState(blockpos);
        if (furnaceCart.canUseRail() && blockstate.is(BlockTags.RAILS) && fuel > 1) {
            riding.setDeltaMovement(riding.getDeltaMovement().normalize().scale(1.0));
        }
        if (fuel < 5 && this.contraption != null && (fuelItems = this.contraption.getStorage().getFuelItems()) != null && !(coal = ItemHelper.extract((IItemHandler)fuelItems, (Predicate<ItemStack>)FUEL_ITEMS, 1, false)).isEmpty()) {
            fuel += 3600;
        }
        if (fuel != fuelBefore || pushX != 0.0 || pushZ != 0.0) {
            furnaceCart.xPush = pushX;
            furnaceCart.zPush = pushZ;
            furnaceCartAccessor.create$setFuel(fuel);
        }
    }

    @Nullable
    public Couple<MinecartController> getCoupledCartsIfPresent() {
        UUID couplingId = this.getCouplingId();
        if (couplingId == null) {
            return null;
        }
        MinecartController controller = CapabilityMinecartController.getIfPresent(this.level(), couplingId);
        if (controller == null || !controller.isPresent()) {
            return null;
        }
        UUID coupledCart = controller.getCoupledCart(true);
        MinecartController coupledController = CapabilityMinecartController.getIfPresent(this.level(), coupledCart);
        if (coupledController == null || !coupledController.isPresent()) {
            return null;
        }
        return Couple.create((Object)controller, (Object)coupledController);
    }

    protected void attachInventoriesFromRidingCarts(Entity riding, boolean isOnCoupling, UUID couplingId) {
        Contraption contraption = this.contraption;
        if (!(contraption instanceof MountedContraption)) {
            return;
        }
        MountedContraption mc = (MountedContraption)contraption;
        if (!isOnCoupling) {
            mc.addExtraInventories(riding);
            return;
        }
        Couple<MinecartController> coupledCarts = this.getCoupledCartsIfPresent();
        if (coupledCarts == null) {
            return;
        }
        coupledCarts.map(MinecartController::cart).forEach(mc::addExtraInventories);
    }

    @Nullable
    public UUID getCouplingId() {
        Optional uuid = (Optional)this.entityData.get(COUPLING);
        return uuid == null ? null : (uuid.isPresent() ? (UUID)uuid.get() : null);
    }

    public void setCouplingId(UUID id) {
        this.entityData.set(COUPLING, Optional.ofNullable(id));
    }

    public Vec3 getVehicleAttachmentPoint(Entity entity) {
        return entity instanceof AbstractContraptionEntity ? Vec3.ZERO : new Vec3(0.0, 0.19, 0.0);
    }

    @Override
    public Vec3 getAnchorVec() {
        Vec3 anchorVec = super.getAnchorVec();
        return anchorVec.subtract(0.5, 0.0, 0.5);
    }

    @Override
    public Vec3 getPrevAnchorVec() {
        Vec3 prevAnchorVec = super.getPrevAnchorVec();
        return prevAnchorVec.subtract(0.5, 0.0, 0.5);
    }

    @Override
    protected StructureTransform makeStructureTransform() {
        BlockPos offset = BlockPos.containing((Position)this.getAnchorVec().add(0.5, 0.5, 0.5));
        return new StructureTransform(offset, 0.0f, -this.yaw + this.getInitialYaw(), 0.0f);
    }

    @Override
    protected float getStalledAngle() {
        return this.yaw;
    }

    @Override
    protected void handleStallInformation(double x, double y, double z, float angle) {
        this.yaw = angle;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void applyLocalTransforms(PoseStack matrixStack, float partialTicks) {
        float angleInitialYaw = this.getInitialYaw();
        float angleYaw = this.getViewYRot(partialTicks);
        float anglePitch = this.getViewXRot(partialTicks);
        matrixStack.translate(-0.5f, 0.0f, -0.5f);
        Entity ridingEntity = this.getVehicle();
        if (ridingEntity instanceof AbstractMinecart) {
            this.repositionOnCart(matrixStack, partialTicks, ridingEntity);
        } else if (ridingEntity instanceof AbstractContraptionEntity) {
            if (ridingEntity.getVehicle() instanceof AbstractMinecart) {
                this.repositionOnCart(matrixStack, partialTicks, ridingEntity.getVehicle());
            } else {
                this.repositionOnContraption(matrixStack, partialTicks, ridingEntity);
            }
        }
        ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)matrixStack).nudge(this.getId())).center()).rotateYDegrees(angleYaw)).rotateZDegrees(anglePitch)).rotateYDegrees(angleInitialYaw)).uncenter();
    }

    @OnlyIn(value=Dist.CLIENT)
    private void repositionOnContraption(PoseStack matrixStack, float partialTicks, Entity ridingEntity) {
        Vec3 pos = this.getContraptionOffset(partialTicks, ridingEntity);
        matrixStack.translate(pos.x, pos.y, pos.z);
    }

    @OnlyIn(value=Dist.CLIENT)
    private void repositionOnCart(PoseStack matrixStack, float partialTicks, Entity ridingEntity) {
        Vec3 cartPos = this.getCartOffset(partialTicks, ridingEntity);
        if (cartPos == Vec3.ZERO) {
            return;
        }
        matrixStack.translate(cartPos.x, cartPos.y, cartPos.z);
    }

    @OnlyIn(value=Dist.CLIENT)
    private Vec3 getContraptionOffset(float partialTicks, Entity ridingEntity) {
        AbstractContraptionEntity parent = (AbstractContraptionEntity)ridingEntity;
        Vec3 passengerPosition = parent.getPassengerPosition(this, partialTicks);
        if (passengerPosition == null) {
            return Vec3.ZERO;
        }
        double x = passengerPosition.x - Mth.lerp((double)partialTicks, (double)this.xOld, (double)this.getX());
        double y = passengerPosition.y - Mth.lerp((double)partialTicks, (double)this.yOld, (double)this.getY());
        double z = passengerPosition.z - Mth.lerp((double)partialTicks, (double)this.zOld, (double)this.getZ());
        return new Vec3(x, y, z);
    }

    @OnlyIn(value=Dist.CLIENT)
    private Vec3 getCartOffset(float partialTicks, Entity ridingEntity) {
        double cartZ;
        double cartY;
        AbstractMinecart cart = (AbstractMinecart)ridingEntity;
        double cartX = Mth.lerp((double)partialTicks, (double)cart.xOld, (double)cart.getX());
        Vec3 cartPos = cart.getPos(cartX, cartY = Mth.lerp((double)partialTicks, (double)cart.yOld, (double)cart.getY()), cartZ = Mth.lerp((double)partialTicks, (double)cart.zOld, (double)cart.getZ()));
        if (cartPos != null) {
            Vec3 cartPosFront = cart.getPosOffs(cartX, cartY, cartZ, (double)0.3f);
            Vec3 cartPosBack = cart.getPosOffs(cartX, cartY, cartZ, (double)-0.3f);
            if (cartPosFront == null) {
                cartPosFront = cartPos;
            }
            if (cartPosBack == null) {
                cartPosBack = cartPos;
            }
            cartX = cartPos.x - cartX;
            cartY = (cartPosFront.y + cartPosBack.y) / 2.0 - cartY;
            cartZ = cartPos.z - cartZ;
            return new Vec3(cartX, cartY, cartZ);
        }
        return Vec3.ZERO;
    }

    @OnlyIn(value=Dist.CLIENT)
    public static void handleRelocationPacket(ContraptionRelocationPacket packet) {
        Entity entity = Minecraft.getInstance().level.getEntity(packet.entityId());
        if (entity instanceof OrientedContraptionEntity) {
            OrientedContraptionEntity oce = (OrientedContraptionEntity)entity;
            oce.nonDamageTicks = 10;
        }
    }
}
