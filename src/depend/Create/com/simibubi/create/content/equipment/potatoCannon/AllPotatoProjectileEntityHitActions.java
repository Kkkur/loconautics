/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.createmod.catnip.data.WorldAttached
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Registry
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.ExtraCodecs
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.animal.Fox
 *  net.minecraft.world.entity.monster.ZombieVillager
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.food.FoodProperties
 *  net.minecraft.world.food.FoodProperties$PossibleEffect
 *  net.minecraft.world.food.Foods
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.component.SuspiciousStewEffects
 *  net.minecraft.world.item.component.SuspiciousStewEffects$Entry
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.common.util.FakePlayer
 *  net.neoforged.neoforge.event.EventHooks
 *  net.neoforged.neoforge.event.entity.EntityTeleportEvent$ChorusFruit
 */
package com.simibubi.create.content.equipment.potatoCannon;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.Create;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileEntityHitAction;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.foundation.codec.CreateCodecs;
import java.util.UUID;
import net.createmod.catnip.data.WorldAttached;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

public class AllPotatoProjectileEntityHitActions {
    public static void init() {
    }

    private static void register(String name, MapCodec<? extends PotatoProjectileEntityHitAction> codec) {
        Registry.register(CreateBuiltInRegistries.POTATO_PROJECTILE_ENTITY_HIT_ACTION, (ResourceLocation)Create.asResource(name), codec);
    }

    private static void applyEffect(LivingEntity entity, MobEffectInstance effect) {
        if (((MobEffect)effect.getEffect().value()).isInstantenous()) {
            ((MobEffect)effect.getEffect().value()).applyInstantenousEffect(null, null, entity, effect.getDuration(), 1.0);
        } else {
            entity.addEffect(effect);
        }
    }

    static {
        AllPotatoProjectileEntityHitActions.register("set_on_fire", SetOnFire.CODEC);
        AllPotatoProjectileEntityHitActions.register("potion_effect", PotionEffect.CODEC);
        AllPotatoProjectileEntityHitActions.register("food_effects", FoodEffects.CODEC);
        AllPotatoProjectileEntityHitActions.register("chorus_teleport", ChorusTeleport.CODEC);
        AllPotatoProjectileEntityHitActions.register("cure_zombie_villager", CureZombieVillager.CODEC);
        AllPotatoProjectileEntityHitActions.register("suspicious_stew", SuspiciousStew.CODEC);
    }

    public record SetOnFire(int ticks) implements PotatoProjectileEntityHitAction
    {
        public static final MapCodec<SetOnFire> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)ExtraCodecs.POSITIVE_INT.fieldOf("ticks").forGetter(SetOnFire::ticks)).apply((Applicative)instance, SetOnFire::new));

        public static SetOnFire seconds(int seconds) {
            return new SetOnFire(seconds * 20);
        }

        @Override
        public boolean execute(ItemStack projectile, EntityHitResult ray, PotatoProjectileEntityHitAction.Type type) {
            ray.getEntity().setRemainingFireTicks(this.ticks);
            return false;
        }

        @Override
        public MapCodec<? extends PotatoProjectileEntityHitAction> codec() {
            return CODEC;
        }
    }

    public record PotionEffect(Holder<MobEffect> effect, int level, int ticks, boolean recoverable) implements PotatoProjectileEntityHitAction
    {
        public static final MapCodec<PotionEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(PotionEffect::effect), (App)ExtraCodecs.POSITIVE_INT.fieldOf("level").forGetter(PotionEffect::level), (App)ExtraCodecs.POSITIVE_INT.fieldOf("ticks").forGetter(PotionEffect::ticks), (App)Codec.BOOL.fieldOf("recoverable").forGetter(PotionEffect::recoverable)).apply((Applicative)instance, PotionEffect::new));

        @Override
        public boolean execute(ItemStack projectile, EntityHitResult ray, PotatoProjectileEntityHitAction.Type type) {
            Entity entity = ray.getEntity();
            if (entity.level().isClientSide) {
                return true;
            }
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                AllPotatoProjectileEntityHitActions.applyEffect(livingEntity, new MobEffectInstance(this.effect, this.ticks, this.level - 1));
            }
            return !this.recoverable;
        }

        @Override
        public MapCodec<? extends PotatoProjectileEntityHitAction> codec() {
            return CODEC;
        }
    }

    public record FoodEffects(FoodProperties foodProperty, boolean recoverable) implements PotatoProjectileEntityHitAction
    {
        public static final MapCodec<FoodEffects> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)FoodProperties.DIRECT_CODEC.fieldOf("food_property").forGetter(FoodEffects::foodProperty), (App)Codec.BOOL.fieldOf("recoverable").forGetter(FoodEffects::recoverable)).apply((Applicative)instance, FoodEffects::new));

        @Override
        public boolean execute(ItemStack projectile, EntityHitResult ray, PotatoProjectileEntityHitAction.Type type) {
            Entity entity = ray.getEntity();
            if (entity.level().isClientSide) {
                return true;
            }
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                for (FoodProperties.PossibleEffect effect : this.foodProperty.effects()) {
                    if (!(livingEntity.getRandom().nextFloat() < effect.probability())) continue;
                    AllPotatoProjectileEntityHitActions.applyEffect(livingEntity, effect.effect());
                }
            }
            return !this.recoverable;
        }

        @Override
        public MapCodec<? extends PotatoProjectileEntityHitAction> codec() {
            return CODEC;
        }
    }

    public record ChorusTeleport(double teleportDiameter) implements PotatoProjectileEntityHitAction
    {
        public static final MapCodec<ChorusTeleport> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)CreateCodecs.POSITIVE_DOUBLE.fieldOf("teleport_diameter").forGetter(ChorusTeleport::teleportDiameter)).apply((Applicative)instance, ChorusTeleport::new));

        @Override
        public boolean execute(ItemStack projectile, EntityHitResult ray, PotatoProjectileEntityHitAction.Type type) {
            Entity entity = ray.getEntity();
            Level level = entity.getCommandSenderWorld();
            if (level.isClientSide) {
                return true;
            }
            if (!(entity instanceof LivingEntity)) {
                return false;
            }
            LivingEntity livingEntity = (LivingEntity)entity;
            double entityX = livingEntity.getX();
            double entityY = livingEntity.getY();
            double entityZ = livingEntity.getZ();
            for (int teleportTry = 0; teleportTry < 16; ++teleportTry) {
                double teleportZ;
                double teleportY;
                double teleportX = entityX + (livingEntity.getRandom().nextDouble() - 0.5) * this.teleportDiameter;
                EntityTeleportEvent.ChorusFruit event = EventHooks.onChorusFruitTeleport((LivingEntity)livingEntity, (double)teleportX, (double)(teleportY = Mth.clamp((double)(entityY + (double)(livingEntity.getRandom().nextInt((int)this.teleportDiameter) - (int)(this.teleportDiameter / 2.0))), (double)0.0, (double)(level.getHeight() - 1))), (double)(teleportZ = entityZ + (livingEntity.getRandom().nextDouble() - 0.5) * this.teleportDiameter));
                if (event.isCanceled()) {
                    return false;
                }
                if (!livingEntity.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) continue;
                if (livingEntity.isPassenger()) {
                    livingEntity.stopRiding();
                }
                SoundEvent soundevent = livingEntity instanceof Fox ? SoundEvents.FOX_TELEPORT : SoundEvents.CHORUS_FRUIT_TELEPORT;
                level.playSound(null, entityX, entityY, entityZ, soundevent, SoundSource.PLAYERS, 1.0f, 1.0f);
                livingEntity.playSound(soundevent, 1.0f, 1.0f);
                livingEntity.setDeltaMovement(Vec3.ZERO);
                return true;
            }
            return false;
        }

        @Override
        public MapCodec<? extends PotatoProjectileEntityHitAction> codec() {
            return CODEC;
        }
    }

    public static enum CureZombieVillager implements PotatoProjectileEntityHitAction
    {
        INSTANCE;

        private static final FoodEffects EFFECT;
        private static final GameProfile ZOMBIE_CONVERTER_NAME;
        private static final WorldAttached<FakePlayer> ZOMBIE_CONVERTERS;
        public static final MapCodec<CureZombieVillager> CODEC;

        @Override
        public boolean execute(ItemStack projectile, EntityHitResult ray, PotatoProjectileEntityHitAction.Type type) {
            ZombieVillager zombieVillager;
            Entity entity = ray.getEntity();
            Level world = entity.level();
            if (!(entity instanceof ZombieVillager) || !(zombieVillager = (ZombieVillager)entity).hasEffect(MobEffects.WEAKNESS)) {
                return EFFECT.execute(projectile, ray, type);
            }
            if (world.isClientSide) {
                return false;
            }
            FakePlayer dummy = (FakePlayer)ZOMBIE_CONVERTERS.get((LevelAccessor)world);
            dummy.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack((ItemLike)Items.GOLDEN_APPLE, 1));
            zombieVillager.mobInteract((Player)dummy, InteractionHand.MAIN_HAND);
            return true;
        }

        @Override
        public MapCodec<? extends PotatoProjectileEntityHitAction> codec() {
            return CODEC;
        }

        static {
            EFFECT = new FoodEffects(Foods.GOLDEN_APPLE, false);
            ZOMBIE_CONVERTER_NAME = new GameProfile(UUID.fromString("be12d3dc-27d3-4992-8c97-66be53fd49c5"), "Converter");
            ZOMBIE_CONVERTERS = new WorldAttached(w -> new FakePlayer((ServerLevel)w, ZOMBIE_CONVERTER_NAME));
            CODEC = MapCodec.unit((Object)INSTANCE);
        }
    }

    public static enum SuspiciousStew implements PotatoProjectileEntityHitAction
    {
        INSTANCE;

        public static final MapCodec<SuspiciousStew> CODEC;

        @Override
        public boolean execute(ItemStack projectile, EntityHitResult ray, PotatoProjectileEntityHitAction.Type type) {
            Entity entity = ray.getEntity();
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                SuspiciousStewEffects stew = (SuspiciousStewEffects)projectile.getOrDefault(DataComponents.SUSPICIOUS_STEW_EFFECTS, (Object)SuspiciousStewEffects.EMPTY);
                for (SuspiciousStewEffects.Entry effect : stew.effects()) {
                    livingEntity.addEffect(effect.createEffectInstance());
                }
            }
            return false;
        }

        @Override
        public MapCodec<? extends PotatoProjectileEntityHitAction> codec() {
            return CODEC;
        }

        static {
            CODEC = MapCodec.unit((Object)INSTANCE);
        }
    }
}
