/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.monster.Phantom
 *  net.minecraft.world.phys.EntityHitResult
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.eriksonn.aeronautics.mixin.potato_cannon_projectiles;

import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity;
import dev.eriksonn.aeronautics.index.AeroAdvancements;
import dev.eriksonn.aeronautics.mixinterface.PotatoProjectileEntityExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={PotatoProjectileEntity.class})
public class PotatoProjectileEntityMixin
implements PotatoProjectileEntityExtension {
    @Shadow
    protected float additionalDamageMult;
    @Unique
    public boolean aeronautics$isFromMountedPotatoCannon = false;

    @Override
    public void aeronautics$setIsFromMountedPotatoCannon(boolean value) {
        this.aeronautics$isFromMountedPotatoCannon = value;
    }

    @Override
    public void aeronautics$setDamageMultiplier(float value) {
        this.additionalDamageMult = value;
    }

    @Inject(method={"onHitEntity"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", shift=At.Shift.AFTER)})
    private void onHitEntity(EntityHitResult ray, CallbackInfo ci) {
        Entity entity;
        if (this.aeronautics$isFromMountedPotatoCannon && !ray.getEntity().isAlive() && (entity = ray.getEntity()) instanceof Phantom) {
            Phantom phantom = (Phantom)entity;
            AeroAdvancements.GHOSTBUSTER.awardToNearby(BlockPos.containing((Position)phantom.position()), phantom.level(), 30.0);
        }
    }
}
