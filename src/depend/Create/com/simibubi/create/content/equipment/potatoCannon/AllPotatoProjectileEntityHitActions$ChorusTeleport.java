/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.animal.Fox
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.event.EventHooks
 *  net.neoforged.neoforge.event.entity.EntityTeleportEvent$ChorusFruit
 */
package com.simibubi.create.content.equipment.potatoCannon;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileEntityHitAction;
import com.simibubi.create.foundation.codec.CreateCodecs;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

public record AllPotatoProjectileEntityHitActions.ChorusTeleport(double teleportDiameter) implements PotatoProjectileEntityHitAction
{
    public static final MapCodec<AllPotatoProjectileEntityHitActions.ChorusTeleport> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)CreateCodecs.POSITIVE_DOUBLE.fieldOf("teleport_diameter").forGetter(AllPotatoProjectileEntityHitActions.ChorusTeleport::teleportDiameter)).apply((Applicative)instance, AllPotatoProjectileEntityHitActions.ChorusTeleport::new));

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
