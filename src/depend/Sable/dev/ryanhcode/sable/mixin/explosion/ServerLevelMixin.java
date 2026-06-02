/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  net.minecraft.core.Holder
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Explosion
 *  net.minecraft.world.level.ExplosionDamageCalculator
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.Level$ExplosionInteraction
 *  org.joml.Vector3d
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.ryanhcode.sable.mixin.explosion;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.ryanhcode.sable.Sable;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ServerLevel.class})
public abstract class ServerLevelMixin {
    @Shadow
    public abstract ServerLevel getLevel();

    @WrapMethod(method={"explode"})
    public Explosion sable$preExplode(Entity entity, DamageSource damageSource, ExplosionDamageCalculator explosionDamageCalculator, double d, double e, double f, float g, boolean bl, Level.ExplosionInteraction explosionInteraction, ParticleOptions particleOptions, ParticleOptions particleOptions2, Holder<SoundEvent> holder, Operation<Explosion> original) {
        Vector3d projectedPos = Sable.HELPER.projectOutOfSubLevel((Level)this.getLevel(), new Vector3d(d, e, f));
        return (Explosion)original.call(new Object[]{entity, damageSource, explosionDamageCalculator, projectedPos.x, projectedPos.y, projectedPos.z, Float.valueOf(g), bl, explosionInteraction, particleOptions, particleOptions2, holder});
    }
}
