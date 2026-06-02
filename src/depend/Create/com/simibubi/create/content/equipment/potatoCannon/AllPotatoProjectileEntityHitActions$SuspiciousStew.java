/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.SuspiciousStewEffects
 *  net.minecraft.world.item.component.SuspiciousStewEffects$Entry
 *  net.minecraft.world.phys.EntityHitResult
 */
package com.simibubi.create.content.equipment.potatoCannon;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileEntityHitAction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.phys.EntityHitResult;

public static enum AllPotatoProjectileEntityHitActions.SuspiciousStew implements PotatoProjectileEntityHitAction
{
    INSTANCE;

    public static final MapCodec<AllPotatoProjectileEntityHitActions.SuspiciousStew> CODEC;

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
