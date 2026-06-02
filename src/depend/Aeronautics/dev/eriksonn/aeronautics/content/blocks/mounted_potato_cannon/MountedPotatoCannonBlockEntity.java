/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllEntityTypes
 *  com.simibubi.create.AllSoundEvents
 *  com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation
 *  com.simibubi.create.api.equipment.potatoCannon.PotatoCannonProjectileType
 *  com.simibubi.create.content.equipment.potatoCannon.PotatoCannonItem$Ammo
 *  com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity
 *  com.simibubi.create.content.kinetics.base.DirectionalKineticBlock
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.foundation.particle.AirParticleData
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor
 *  dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.eriksonn.aeronautics.content.blocks.mounted_potato_cannon;

import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonItem;
import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.particle.AirParticleData;
import dev.eriksonn.aeronautics.config.AeroConfig;
import dev.eriksonn.aeronautics.content.blocks.mounted_potato_cannon.MountedPotatoCannonBlock;
import dev.eriksonn.aeronautics.content.blocks.mounted_potato_cannon.MountedPotatoCannonInventory;
import dev.eriksonn.aeronautics.data.AeroLang;
import dev.eriksonn.aeronautics.index.AeroAdvancements;
import dev.eriksonn.aeronautics.mixinterface.PotatoProjectileEntityExtension;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import java.util.List;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class MountedPotatoCannonBlockEntity
extends KineticBlockEntity
implements BlockEntitySubLevelActor,
IHaveGoggleInformation,
Clearable {
    private static final Vector3d RECOIL_DIR = new Vector3d();
    private static final Vector3d RECOIL_CENTER = new Vector3d();
    private State currentState = State.CHARGING;
    private final MountedPotatoCannonInventory inventory = new MountedPotatoCannonInventory(this);
    private float chargeTimer = 0.0f;
    private int initialAmmoReloadTicks = -1;
    private float recoilMagnitude = 0.0f;
    boolean needsClientUpdate = false;
    private boolean blocked;
    private double blockedLength;
    private int itemRotationId;
    private int barrelTimer = 100;
    private int itemTimer = 20;
    private float animationSpeed;
    private float angle;
    private float previousAngle;

    public MountedPotatoCannonBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public void initialize() {
        super.initialize();
        this.inventory.updateCachedType((HolderLookup.Provider)this.level.registryAccess(), this.inventory.slot.getStack());
        this.resetAndUpdate();
    }

    public void tick() {
        super.tick();
        if (this.recoilMagnitude > 0.0f) {
            this.recoilMagnitude *= 0.5f;
        }
        if (this.level.isClientSide()) {
            this.previousAngle = this.angle;
            float targetSpeed = Math.abs(this.speed);
            float maxTarget = 32.0f;
            targetSpeed = (float)(1.0 - Math.exp(-targetSpeed / maxTarget)) * maxTarget;
            this.animationSpeed += ((targetSpeed *= 0.3f) - this.animationSpeed) * 0.3f;
            this.angle += this.animationSpeed;
            if (this.angle > 360.0f) {
                this.angle -= 360.0f;
                this.previousAngle -= 360.0f;
            }
        }
        if (this.itemTimer < 20) {
            ++this.itemTimer;
        }
        if (this.barrelTimer < 100) {
            ++this.barrelTimer;
        }
        this.updateBlockedState();
        switch (this.currentState.ordinal()) {
            case 2: {
                if (this.initialAmmoReloadTicks == -1) break;
                if (this.chargeTimer < 1.0f) {
                    this.chargeTimer += this.getChargeUpSpeed();
                }
                if (!(this.chargeTimer >= 1.0f)) break;
                this.currentState = State.CHARGED;
                break;
            }
            case 0: {
                boolean internalBlocked = this.blocked;
                BlockState state = this.level.getBlockState(this.getBlockPos().relative((Direction)this.getBlockState().getValue((Property)DirectionalKineticBlock.FACING)));
                if (Blocks.MANGROVE_TRAPDOOR == state.getBlock()) {
                    internalBlocked = false;
                }
                if (internalBlocked || !((Boolean)this.getBlockState().getValue((Property)MountedPotatoCannonBlock.POWERED)).booleanValue()) break;
                this.currentState = State.FIRING;
                this.barrelTimer = 0;
                break;
            }
            case 1: {
                PotatoCannonItem.Ammo ammo;
                this.animationSpeed = this.barrelTimer * 75;
                if (this.barrelTimer <= 2) break;
                if (this.level.isClientSide) {
                    this.speed = 0.2f;
                }
                if ((ammo = this.getInventory().getAmmo()) != null) {
                    this.getInventory().extractSlot(0, 1, false);
                    Vec3 barrelPos = this.getBarrelPos();
                    BlockState state = this.level.getBlockState(this.getBlockPos().relative((Direction)this.getBlockState().getValue((Property)DirectionalKineticBlock.FACING)));
                    if (this.blocked && Blocks.MANGROVE_TRAPDOOR == state.getBlock()) {
                        barrelPos = new Vec3((double)this.getBlockPos().getX() + 0.5, (double)this.getBlockPos().getY() + 0.5, (double)this.getBlockPos().getZ() + 0.5).add(this.getAimingVector().scale(this.blockedLength / (double)1.725f));
                    }
                    if (!this.level.isClientSide) {
                        PotatoCannonProjectileType type = ammo.type();
                        Vec3 motion = this.getAimingVector().scale((double)type.velocityMultiplier() * 2.0);
                        Vec3 sprayBase = VecHelper.rotate((Vec3)new Vec3(0.0, 0.1, 0.0), (double)(360.0f * this.level.random.nextFloat()), (Direction.Axis)Direction.Axis.Z);
                        float sprayChange = 360.0f / (float)type.split();
                        for (int i = 0; i < type.split(); ++i) {
                            PotatoProjectileEntity shootyBoomBoom = (PotatoProjectileEntity)AllEntityTypes.POTATO_PROJECTILE.create(this.getLevel());
                            if (shootyBoomBoom == null) continue;
                            shootyBoomBoom.setItem(ammo.stack());
                            ((PotatoProjectileEntityExtension)shootyBoomBoom).aeronautics$setDamageMultiplier(2.0f);
                            ((PotatoProjectileEntityExtension)shootyBoomBoom).aeronautics$setIsFromMountedPotatoCannon(true);
                            Vec3 splitMotion = motion;
                            if (type.split() > 1) {
                                float imperfection = 40.0f * (this.level.random.nextFloat() - 0.5f);
                                Vec3 sprayOffset = VecHelper.rotate((Vec3)sprayBase, (double)((float)i * sprayChange + imperfection), (Direction.Axis)Direction.Axis.Z);
                                splitMotion = motion.add(VecHelper.lookAt((Vec3)sprayOffset, (Vec3)motion));
                            }
                            shootyBoomBoom.setPos(barrelPos.x, barrelPos.y, barrelPos.z);
                            shootyBoomBoom.setDeltaMovement(splitMotion);
                            this.level.addFreshEntity((Entity)shootyBoomBoom);
                        }
                        this.recoilMagnitude = (float)type.split() / 2.0f * AeroConfig.server().physics.mountedPotatoCannonMagnitude.getF();
                        AllSoundEvents.FWOOMP.playOnServer(this.level, (Vec3i)this.worldPosition, 1.0f, ammo.type().soundPitch() + this.level.random.nextFloat() * 0.2f);
                        AeroAdvancements.HEAVIER_ARTILLERY.awardToNearby(this.getBlockPos(), this.level);
                    } else {
                        for (int i = 0; i < 8; ++i) {
                            Vec3 vel = this.getAimingVector();
                            RandomSource rnd = this.level.getRandom();
                            vel = vel.add(new Vec3(rnd.nextDouble() - 0.5, rnd.nextDouble() - 0.5, rnd.nextDouble() - 0.5).scale(1.0));
                            vel = vel.scale(1.5);
                            this.level.addParticle((ParticleOptions)new AirParticleData(0.5f, 0.1f), barrelPos.x, barrelPos.y, barrelPos.z, vel.x, vel.y, vel.z);
                        }
                    }
                }
                this.resetToCharging();
            }
        }
    }

    private void updateBlockedState() {
        Vec3 pos = new Vec3((double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.5, (double)this.worldPosition.getZ() + 0.5);
        Vec3 beginning = pos.add(this.getAimingVector().scale((double)0.65f));
        Vec3 end = pos.add(this.getAimingVector().scale((double)1.15f));
        BlockHitResult ray = this.level.clip(new ClipContext(beginning, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
        Vector3d projected = Sable.HELPER.projectOutOfSubLevel(this.getLevel(), JOMLConversion.toJOML((Position)ray.getLocation()));
        this.blocked = ray.getType() != HitResult.Type.MISS;
        this.blockedLength = 1.0 - (projected.length() - beginning.length()) / (end.length() - beginning.length()) + 0.1875;
        if (this.blocked != (Boolean)this.getBlockState().getValue((Property)MountedPotatoCannonBlock.BLOCKED)) {
            this.level.setBlockAndUpdate(this.getBlockPos(), (BlockState)this.getBlockState().setValue((Property)MountedPotatoCannonBlock.BLOCKED, (Comparable)Boolean.valueOf(this.blocked)));
        }
    }

    public void sable$physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
        if (this.recoilMagnitude > 0.0f) {
            RECOIL_DIR.set((Vector3dc)JOMLConversion.toJOML((Position)Vec3.atLowerCornerOf((Vec3i)((Direction)this.getBlockState().getValue((Property)DirectionalKineticBlock.FACING)).getOpposite().getNormal())));
            RECOIL_CENTER.set((Vector3dc)JOMLConversion.toJOML((Position)Vec3.atCenterOf((Vec3i)this.getBlockPos())));
            RECOIL_DIR.mul((double)this.recoilMagnitude);
            handle.applyImpulseAtPoint((Vector3dc)RECOIL_CENTER, (Vector3dc)RECOIL_DIR);
        }
    }

    public void resetAndUpdate() {
        this.currentState = State.CHARGING;
        this.initialAmmoReloadTicks = -1;
        this.chargeTimer = 0.0f;
        this.itemTimer = 0;
        this.itemRotationId = -1;
        PotatoCannonItem.Ammo ammo = this.getInventory().getAmmo();
        if (ammo != null) {
            this.initialAmmoReloadTicks = ammo.type().reloadTicks();
            this.itemRotationId = this.level.getRandom().nextInt(10000);
        }
        if (!this.level.isClientSide()) {
            this.needsClientUpdate = true;
        }
    }

    private void resetToCharging() {
        this.currentState = State.CHARGING;
        this.itemTimer = 0;
        this.chargeTimer = 0.0f;
    }

    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.put("inventory", (Tag)this.inventory.write(registries));
        compound.putInt("ItemRotationID", this.itemRotationId);
        compound.putInt("ItemTimer", this.itemTimer);
        compound.putFloat("ChargeTimer", this.chargeTimer);
        NBTHelper.writeEnum((CompoundTag)compound, (String)"State", (Enum)this.currentState);
        compound.putInt("BarrelTimer", this.barrelTimer);
        if (clientPacket) {
            compound.putBoolean("NeedsUpdate", this.needsClientUpdate);
            this.needsClientUpdate = false;
        }
    }

    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.inventory.read(registries, compound.getCompound("inventory"));
        this.inventory.updateCachedType(registries, this.inventory.slot.getStack());
        if (clientPacket && compound.getBoolean("NeedsUpdate")) {
            this.resetAndUpdate();
        }
        this.chargeTimer = compound.getFloat("ChargeTimer");
        this.barrelTimer = compound.getInt("BarrelTimer");
        this.itemRotationId = compound.getInt("ItemRotationID");
        this.itemTimer = compound.getInt("ItemTimer");
        this.currentState = (State)NBTHelper.readEnum((CompoundTag)compound, (String)"State", State.class);
    }

    private float getChargeUpSpeed() {
        if (this.initialAmmoReloadTicks == -1 || this.getSpeed() == 0.0f) {
            return 0.0f;
        }
        return Math.abs(this.getSpeed()) / (float)(64 * this.initialAmmoReloadTicks);
    }

    public float getBarrelDistance(float partialTick) {
        float normalizedTimer = (float)(this.barrelTimer - 1) + partialTick;
        float x = Math.max(normalizedTimer - 0.5f, 0.0f);
        double recoilMultiplier = 0.75;
        float distance = (float)(Math.E * ((double)x * 0.75) * Math.exp(-x));
        return -0.5f * distance;
    }

    public float getBellowDistance(float partialTick) {
        float normalizedTimer = (float)(this.barrelTimer - 1) + partialTick;
        float distance = this.currentState == State.FIRING ? 1.0f - (1.0f - normalizedTimer) * 0.75f : 1.0f - Math.min(this.chargeTimer + this.getChargeUpSpeed() * partialTick, 1.0f);
        distance = Math.min(distance, 1.0f);
        distance = Math.max(distance, 0.0f);
        return distance *= 0.15f;
    }

    public float getCogwheelAngle(float partialTicks) {
        return Mth.lerp((float)partialTicks, (float)this.previousAngle, (float)this.angle);
    }

    public float getCogwheelSpeed() {
        return -Mth.clamp((float)this.getSpeed(), (float)-1.0f, (float)1.0f);
    }

    public float getItemTime(float partialTicks) {
        return (float)this.itemTimer + partialTicks;
    }

    public int getItemRotationId() {
        return this.itemRotationId;
    }

    public Vec3 getBarrelPos() {
        return new Vec3((double)this.getBlockPos().getX() + 0.5, (double)this.getBlockPos().getY() + 0.5, (double)this.getBlockPos().getZ() + 0.5).add(this.getAimingVector().scale(1.2));
    }

    public Vec3 getAimingVector() {
        return Vec3.atLowerCornerOf((Vec3i)((Direction)this.getBlockState().getValue((Property)DirectionalKineticBlock.FACING)).getNormal());
    }

    public MountedPotatoCannonInventory getInventory() {
        return this.inventory;
    }

    public boolean isBlocked() {
        return this.blocked;
    }

    public double getBlockedLength() {
        return this.blockedLength;
    }

    public AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate(1.0);
    }

    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        PotatoCannonProjectileType type;
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        AeroLang.emptyLine(tooltip);
        AeroLang.blockName(this.getBlockState()).text(":").forGoggles(tooltip);
        PotatoCannonItem.Ammo ammo = this.inventory.getAmmo();
        if (ammo != null) {
            type = ammo.type();
            ItemStack currentStack = this.inventory.slot.getStack();
            AeroLang.translate("potato_cannon.ammo", currentStack.getDisplayName(), currentStack.getCount()).style(ChatFormatting.GRAY).forGoggles(tooltip, 1);
            float damage = (float)type.damage() * 2.0f;
            AeroLang.translate("potato_cannon.attack_damage", Float.valueOf(damage)).style(ChatFormatting.DARK_GREEN).forGoggles(tooltip, 1);
            if (Math.abs(this.getSpeed()) > 0.0f) {
                AeroLang.translate("potato_cannon.reload_ticks", Math.round(1.0f / this.getChargeUpSpeed())).style(ChatFormatting.DARK_GREEN).forGoggles(tooltip, 1);
            }
        } else {
            return false;
        }
        AeroLang.translate("potato_cannon.knockback", Float.valueOf(type.knockback())).style(ChatFormatting.DARK_GREEN).forGoggles(tooltip, 1);
        return true;
    }

    public void clearContent() {
        this.inventory.clearContent();
    }

    public static enum State {
        CHARGED,
        FIRING,
        CHARGING;

    }
}
