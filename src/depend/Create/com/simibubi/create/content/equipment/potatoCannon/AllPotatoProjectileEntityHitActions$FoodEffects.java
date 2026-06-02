/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.food.FoodProperties
 *  net.minecraft.world.food.FoodProperties$PossibleEffect
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;

public record AllPotatoProjectileEntityHitActions.FoodEffects(FoodProperties foodProperty, boolean recoverable) implements PotatoProjectileEntityHitAction
{
    public static final MapCodec<AllPotatoProjectileEntityHitActions.FoodEffects> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)FoodProperties.DIRECT_CODEC.fieldOf("food_property").forGetter(AllPotatoProjectileEntityHitActions.FoodEffects::foodProperty), (App)Codec.BOOL.fieldOf("recoverable").forGetter(AllPotatoProjectileEntityHitActions.FoodEffects::recoverable)).apply((Applicative)instance, AllPotatoProjectileEntityHitActions.FoodEffects::new));

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
