/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.entity.projectile.ThrownPotion
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create;

import com.simibubi.create.api.contraption.dispenser.MountedProjectileDispenseBehavior;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

class AllMountedDispenseItemBehaviors.6
extends MountedProjectileDispenseBehavior {
    AllMountedDispenseItemBehaviors.6() {
    }

    @Override
    protected Projectile getProjectile(Level level, double x, double y, double z, ItemStack stack, Direction facing) {
        ThrownPotion potion = new ThrownPotion(level, x, y, z);
        potion.setItem(stack);
        return potion;
    }

    @Override
    protected float getUncertainty() {
        return super.getUncertainty() * 0.5f;
    }

    @Override
    protected float getPower() {
        return super.getPower() * 1.25f;
    }
}
