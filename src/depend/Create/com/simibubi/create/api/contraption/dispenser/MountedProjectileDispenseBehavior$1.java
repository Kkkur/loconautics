/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.api.contraption.dispenser;

import com.simibubi.create.api.contraption.dispenser.MountedProjectileDispenseBehavior;
import com.simibubi.create.foundation.mixin.accessor.ProjectileDispenseBehaviorAccessor;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

static class MountedProjectileDispenseBehavior.1
extends MountedProjectileDispenseBehavior {
    final /* synthetic */ ProjectileDispenseBehaviorAccessor val$accessor;

    MountedProjectileDispenseBehavior.1(ProjectileDispenseBehaviorAccessor projectileDispenseBehaviorAccessor) {
        this.val$accessor = projectileDispenseBehaviorAccessor;
    }

    @Override
    protected Projectile getProjectile(Level level, double x, double y, double z, ItemStack stack, Direction facing) {
        return this.val$accessor.create$getProjectileItem().asProjectile(level, (Position)new Vec3(x, y, z), stack, facing);
    }

    @Override
    protected float getUncertainty() {
        return this.val$accessor.create$getDispenseConfig().uncertainty();
    }

    @Override
    protected float getPower() {
        return this.val$accessor.create$getDispenseConfig().power();
    }
}
