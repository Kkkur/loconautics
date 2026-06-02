/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntityDimensions
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.entity.monster.Creeper
 *  net.minecraft.world.food.FoodProperties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.bus.api.EventPriority
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.common.UsernameCache
 *  net.neoforged.neoforge.common.util.FakePlayer
 *  net.neoforged.neoforge.event.entity.EntityEvent$Size
 *  net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent
 *  net.neoforged.neoforge.event.entity.living.LivingDropsEvent
 *  net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent
 *  org.apache.commons.lang3.tuple.Pair
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.deployer;

import com.mojang.authlib.GameProfile;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CKinetics;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.UsernameCache;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber
public class DeployerFakePlayer
extends FakePlayer {
    public static final UUID fallbackID = UUID.fromString("9e2faded-cafe-4ec2-c314-dad129ae971d");
    Pair<BlockPos, Float> blockBreakingProgress;
    ItemStack spawnedItemEffects;
    public boolean placedTracks;
    public boolean onMinecartContraption;
    private UUID owner;

    public DeployerFakePlayer(ServerLevel world, @Nullable UUID owner) {
        super(world, (GameProfile)new DeployerGameProfile(fallbackID, "Deployer", owner));
        this.owner = owner;
    }

    public OptionalInt openMenu(MenuProvider menuProvider) {
        return OptionalInt.empty();
    }

    public Component getDisplayName() {
        return CreateLang.translateDirect("block.deployer.damage_source_name", new Object[0]);
    }

    @OnlyIn(value=Dist.CLIENT)
    public EntityDimensions getDefaultDimensions(Pose pose) {
        return super.getDefaultDimensions(pose).withEyeHeight(0.0f);
    }

    public Vec3 position() {
        return new Vec3(this.getX(), this.getY(), this.getZ());
    }

    public float getCurrentItemAttackStrengthDelay() {
        return 0.015625f;
    }

    public boolean canEat(boolean ignoreHunger) {
        return false;
    }

    public ItemStack eat(Level level, ItemStack food, FoodProperties foodProperties) {
        food.shrink(1);
        return food;
    }

    public boolean canBeAffected(MobEffectInstance pEffectInstance) {
        return false;
    }

    public UUID getUUID() {
        return this.owner == null ? super.getUUID() : this.owner;
    }

    @SubscribeEvent
    public static void deployerHasEyesOnHisFeet(EntityEvent.Size event) {
        if (event.getEntity() instanceof DeployerFakePlayer) {
            event.setNewSize(event.getNewSize().withEyeHeight(0.0f));
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void deployerCollectsDropsFromKilledEntities(LivingDropsEvent event) {
        DamageSource source = event.getSource();
        Entity trueSource = source.getEntity();
        if (trueSource != null && trueSource instanceof DeployerFakePlayer) {
            DeployerFakePlayer fakePlayer = (DeployerFakePlayer)trueSource;
            event.getDrops().forEach(stack -> fakePlayer.getInventory().placeItemBackInInventory(stack.getItem()));
            event.setCanceled(true);
        }
    }

    protected boolean doesEmitEquipEvent(EquipmentSlot p_217035_) {
        return false;
    }

    public void remove(Entity.RemovalReason p_150097_) {
        if (this.blockBreakingProgress != null && !this.level().isClientSide) {
            this.level().destroyBlockProgress(this.getId(), (BlockPos)this.blockBreakingProgress.getKey(), -1);
        }
        super.remove(p_150097_);
    }

    @SubscribeEvent
    public static void deployerKillsDoNotSpawnXP(LivingExperienceDropEvent event) {
        if (event.getAttackingPlayer() instanceof DeployerFakePlayer) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void entitiesDontRetaliate(LivingChangeTargetEvent event) {
        if (!(event.getOriginalAboutToBeSetTarget() instanceof DeployerFakePlayer)) {
            return;
        }
        LivingEntity entityLiving = event.getEntity();
        if (!(entityLiving instanceof Mob)) {
            return;
        }
        Mob mob = (Mob)entityLiving;
        CKinetics.DeployerAggroSetting setting = (CKinetics.DeployerAggroSetting)((Object)AllConfigs.server().kinetics.ignoreDeployerAttacks.get());
        switch (setting) {
            case ALL: {
                event.setCanceled(true);
                break;
            }
            case CREEPERS: {
                if (!(mob instanceof Creeper)) break;
                event.setCanceled(true);
            }
        }
    }

    private static class DeployerGameProfile
    extends GameProfile {
        private UUID owner;

        public DeployerGameProfile(UUID id, String name, UUID owner) {
            super(id, name);
            this.owner = owner;
        }

        public UUID getId() {
            return this.owner == null ? super.getId() : this.owner;
        }

        public String getName() {
            if (this.owner == null) {
                return super.getName();
            }
            String lastKnownUsername = UsernameCache.getLastKnownUsername((UUID)this.owner);
            return lastKnownUsername == null ? super.getName() : lastKnownUsername;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof GameProfile)) {
                return false;
            }
            GameProfile otherProfile = (GameProfile)o;
            return Objects.equals(this.getId(), otherProfile.getId()) && Objects.equals(this.getName(), otherProfile.getName());
        }

        public int hashCode() {
            UUID id = this.getId();
            String name = this.getName();
            int result = id == null ? 0 : id.hashCode();
            result = 31 * result + (name == null ? 0 : name.hashCode());
            return result;
        }
    }
}
