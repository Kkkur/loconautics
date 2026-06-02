/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.core.Holder
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.util.ExtraCodecs
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.EntityHitResult
 */
package com.simibubi.create.content.equipment.potatoCannon;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileEntityHitAction;
import com.simibubi.create.content.equipment.potatoCannon.AllPotatoProjectileEntityHitActions;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;

public record AllPotatoProjectileEntityHitActions.PotionEffect(Holder<MobEffect> effect, int level, int ticks, boolean recoverable) implements PotatoProjectileEntityHitAction
{
    public static final MapCodec<AllPotatoProjectileEntityHitActions.PotionEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(AllPotatoProjectileEntityHitActions.PotionEffect::effect), (App)ExtraCodecs.POSITIVE_INT.fieldOf("level").forGetter(AllPotatoProjectileEntityHitActions.PotionEffect::level), (App)ExtraCodecs.POSITIVE_INT.fieldOf("ticks").forGetter(AllPotatoProjectileEntityHitActions.PotionEffect::ticks), (App)Codec.BOOL.fieldOf("recoverable").forGetter(AllPotatoProjectileEntityHitActions.PotionEffect::recoverable)).apply((Applicative)instance, AllPotatoProjectileEntityHitActions.PotionEffect::new));

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
