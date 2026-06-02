/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Registry
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ItemParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundGameEventPacket
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.tags.DamageTypeTags
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EntityType$Builder
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.entity.boss.wither.WitherBoss
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.AbstractHurtingProjectile
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.entity.IEntityWithComplexSpawn
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.equipment.potatoCannon;

import com.simibubi.create.AllEnchantments;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileRenderMode;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.equipment.potatoCannon.AllPotatoProjectileRenderModes;
import com.simibubi.create.content.equipment.potatoCannon.AllPotatoProjectileTypes;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.damageTypes.CreateDamageSources;
import com.simibubi.create.foundation.particle.AirParticleData;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PotatoProjectileEntity
extends AbstractHurtingProjectile
implements IEntityWithComplexSpawn {
    protected PotatoCannonProjectileType type;
    protected ItemStack stack = ItemStack.EMPTY;
    protected Entity stuckEntity;
    protected Vec3 stuckOffset;
    protected PotatoProjectileRenderMode stuckRenderer;
    protected double stuckFallSpeed;
    protected float additionalDamageMult = 1.0f;
    protected float additionalKnockback = 0.0f;
    protected float recoveryChance = 0.0f;

    public PotatoProjectileEntity(EntityType<? extends AbstractHurtingProjectile> type, Level level) {
        super(type, level);
    }

    public void setItem(ItemStack stack) {
        this.stack = stack;
        this.type = (PotatoCannonProjectileType)PotatoCannonProjectileType.getTypeForItem(this.level().registryAccess(), stack.getItem()).orElseGet(() -> this.level().registryAccess().registryOrThrow(CreateRegistries.POTATO_PROJECTILE_TYPE).getHolderOrThrow(AllPotatoProjectileTypes.FALLBACK)).value();
    }

    public void setEnchantmentEffectsFromCannon(ItemStack cannon) {
        Registry enchantmentRegistry = this.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        int recovery = cannon.getEnchantmentLevel((Holder)enchantmentRegistry.getHolderOrThrow(AllEnchantments.POTATO_RECOVERY));
        if (recovery > 0) {
            this.recoveryChance = 0.125f + (float)recovery * 0.125f;
        }
    }

    public ItemStack getItem() {
        return this.stack;
    }

    @Nullable
    public PotatoCannonProjectileType getProjectileType() {
        return this.type;
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        this.setItem(ItemStack.parseOptional((HolderLookup.Provider)this.registryAccess(), (CompoundTag)nbt.getCompound("Item")));
        this.additionalDamageMult = nbt.getFloat("AdditionalDamage");
        this.additionalKnockback = nbt.getFloat("AdditionalKnockback");
        this.recoveryChance = nbt.getFloat("Recovery");
        super.readAdditionalSaveData(nbt);
    }

    public void addAdditionalSaveData(CompoundTag nbt) {
        nbt.put("Item", this.stack.saveOptional((HolderLookup.Provider)this.registryAccess()));
        nbt.putFloat("AdditionalDamage", this.additionalDamageMult);
        nbt.putFloat("AdditionalKnockback", this.additionalKnockback);
        nbt.putFloat("Recovery", this.recoveryChance);
        super.addAdditionalSaveData(nbt);
    }

    @Nullable
    public Entity getStuckEntity() {
        if (this.stuckEntity == null) {
            return null;
        }
        if (!this.stuckEntity.isAlive()) {
            return null;
        }
        return this.stuckEntity;
    }

    public void setStuckEntity(Entity stuckEntity) {
        this.stuckEntity = stuckEntity;
        this.stuckOffset = this.position().subtract(stuckEntity.position());
        this.stuckRenderer = new AllPotatoProjectileRenderModes.StuckToEntity(this.stuckOffset);
        this.stuckFallSpeed = 0.0;
        this.setDeltaMovement(Vec3.ZERO);
    }

    public PotatoProjectileRenderMode getRenderMode() {
        if (this.getStuckEntity() != null) {
            return this.stuckRenderer;
        }
        return this.type.renderMode();
    }

    public void tick() {
        Entity stuckEntity = this.getStuckEntity();
        if (stuckEntity != null) {
            if (this.getY() < stuckEntity.getY() - 0.1) {
                this.pop(this.position());
                this.kill();
            } else {
                this.stuckFallSpeed += 0.007 * (double)this.type.gravityMultiplier();
                this.stuckOffset = this.stuckOffset.add(0.0, -this.stuckFallSpeed, 0.0);
                Vec3 pos = stuckEntity.position().add(this.stuckOffset);
                this.setPos(pos.x, pos.y, pos.z);
            }
        } else {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.05 * (double)this.type.gravityMultiplier(), 0.0).scale((double)this.type.drag()));
        }
        super.tick();
    }

    protected float getInertia() {
        return 1.0f;
    }

    protected ParticleOptions getTrailParticle() {
        return new AirParticleData(1.0f, 10.0f);
    }

    protected boolean shouldBurn() {
        return false;
    }

    protected void onHitEntity(EntityHitResult ray) {
        Vec3 appliedMotion;
        super.onHitEntity(ray);
        if (this.getStuckEntity() != null) {
            return;
        }
        Vec3 hit = ray.getLocation();
        Entity target = ray.getEntity();
        float damage = (float)this.type.damage() * this.additionalDamageMult;
        float knockback = this.type.knockback() + this.additionalKnockback;
        Entity owner = this.getOwner();
        if (!target.isAlive()) {
            return;
        }
        if (owner instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)owner;
            livingEntity.setLastHurtMob(target);
        }
        if (target instanceof PotatoProjectileEntity) {
            PotatoProjectileEntity ppe = (PotatoProjectileEntity)target;
            if (this.tickCount < 10 && target.tickCount < 10) {
                return;
            }
            if (ppe.getProjectileType() != this.getProjectileType()) {
                Entity entity;
                Player p;
                if (owner instanceof Player) {
                    p = (Player)owner;
                    AllAdvancements.POTATO_CANNON_COLLIDE.awardTo(p);
                }
                if ((entity = ppe.getOwner()) instanceof Player) {
                    p = (Player)entity;
                    AllAdvancements.POTATO_CANNON_COLLIDE.awardTo(p);
                }
            }
        }
        this.pop(hit);
        if (target instanceof WitherBoss && ((WitherBoss)target).isPowered()) {
            return;
        }
        if (this.type.preEntityHit(this.stack, ray)) {
            return;
        }
        boolean targetIsEnderman = target.getType() == EntityType.ENDERMAN;
        int k = target.getRemainingFireTicks();
        if (this.isOnFire() && !targetIsEnderman) {
            target.igniteForSeconds(5.0f);
        }
        boolean onServer = !this.level().isClientSide;
        DamageSource damageSource = this.causePotatoDamage();
        if (onServer && !target.hurt(damageSource, damage)) {
            target.setRemainingFireTicks(k);
            this.kill();
            return;
        }
        if (targetIsEnderman) {
            return;
        }
        if (!this.type.onEntityHit(this.stack, ray) && onServer) {
            if (this.random.nextDouble() <= (double)this.recoveryChance) {
                this.recoverItem();
            } else {
                this.spawnAtLocation(this.type.dropStack());
            }
        }
        if (!(target instanceof LivingEntity)) {
            PotatoProjectileEntity.playHitSound(this.level(), this.position());
            this.kill();
            return;
        }
        LivingEntity livingentity = (LivingEntity)target;
        if (this.type.reloadTicks() < 10) {
            livingentity.invulnerableTime = this.type.reloadTicks() + 10;
        }
        if (onServer && knockback > 0.0f && (appliedMotion = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize()).lengthSqr() > 0.0) {
            livingentity.knockback((double)knockback * 0.6, -appliedMotion.x, -appliedMotion.z);
        }
        if (onServer && owner instanceof LivingEntity) {
            EnchantmentHelper.doPostAttackEffects((ServerLevel)((ServerLevel)this.level()), (Entity)livingentity, (DamageSource)damageSource);
        }
        if (livingentity != owner && livingentity instanceof Player && owner instanceof ServerPlayer && !this.isSilent()) {
            ((ServerPlayer)owner).connection.send((Packet)new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0f));
        }
        if (onServer && owner instanceof ServerPlayer) {
            ServerPlayer serverplayerentity = (ServerPlayer)owner;
            if (!target.isAlive() && target.getType().getCategory() == MobCategory.MONSTER || target instanceof Player && target != owner) {
                AllAdvancements.POTATO_CANNON.awardTo((Player)serverplayerentity);
            }
        }
        if (this.type.sticky() && target.isAlive()) {
            this.setStuckEntity(target);
        } else {
            this.kill();
        }
    }

    private void recoverItem() {
        if (!this.stack.isEmpty()) {
            this.spawnAtLocation(this.stack.copyWithCount(1));
        }
    }

    public static void playHitSound(Level world, Vec3 location) {
        AllSoundEvents.POTATO_HIT.playOnServer(world, (Vec3i)BlockPos.containing((Position)location));
    }

    public static void playLaunchSound(Level world, Vec3 location, float pitch) {
        AllSoundEvents.FWOOMP.playAt(world, location, 1.0f, pitch, true);
    }

    protected void onHitBlock(BlockHitResult ray) {
        Vec3 hit = ray.getLocation();
        this.pop(hit);
        if (!this.type.onBlockHit((LevelAccessor)this.level(), this.stack, ray) && !this.level().isClientSide) {
            if (this.random.nextDouble() <= (double)this.recoveryChance) {
                this.recoverItem();
            } else {
                this.spawnAtLocation(this.getProjectileType().dropStack());
            }
        }
        super.onHitBlock(ray);
        this.kill();
    }

    public boolean hurt(@NotNull DamageSource source, float amt) {
        if (source.is(DamageTypeTags.IS_FIRE)) {
            return false;
        }
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        this.pop(this.position());
        this.kill();
        return true;
    }

    private void pop(Vec3 hit) {
        if (!this.stack.isEmpty()) {
            for (int i = 0; i < 7; ++i) {
                Vec3 m = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)this.random, (float)0.25f);
                this.level().addParticle((ParticleOptions)new ItemParticleOption(ParticleTypes.ITEM, this.stack), hit.x, hit.y, hit.z, m.x, m.y, m.z);
            }
        }
        if (!this.level().isClientSide) {
            PotatoProjectileEntity.playHitSound(this.level(), this.position());
        }
    }

    private DamageSource causePotatoDamage() {
        return CreateDamageSources.potatoCannon(this.level(), (Entity)this, this.getOwner());
    }

    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        EntityType.Builder<?> entityBuilder = builder;
        return entityBuilder.sized(0.25f, 0.25f);
    }

    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        CompoundTag compound = new CompoundTag();
        this.addAdditionalSaveData(compound);
        buffer.writeNbt((Tag)compound);
    }

    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        this.readAdditionalSaveData(additionalData.readNbt());
    }
}
