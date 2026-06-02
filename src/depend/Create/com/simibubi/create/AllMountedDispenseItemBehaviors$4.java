/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.projectile.SmallFireball
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create;

import com.simibubi.create.api.contraption.dispenser.DefaultMountedDispenseBehavior;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

class AllMountedDispenseItemBehaviors.4
extends DefaultMountedDispenseBehavior {
    AllMountedDispenseItemBehaviors.4() {
    }

    @Override
    protected ItemStack execute(ItemStack stack, MovementContext context, BlockPos pos, Vec3 facing) {
        RandomSource random = context.world.random;
        double x = (double)pos.getX() + facing.x * 0.7 + 0.5;
        double y = (double)pos.getY() + facing.y * 0.7 + 0.5;
        double z = (double)pos.getZ() + facing.z * 0.7 + 0.5;
        SmallFireball fireball = new SmallFireball(context.world, x, y, z, new Vec3(random.nextGaussian() * 0.05 + facing.x + context.motion.x, random.nextGaussian() * 0.05 + facing.y + context.motion.y, random.nextGaussian() * 0.05 + facing.z + context.motion.z).normalize());
        fireball.setItem(stack);
        context.world.addFreshEntity((Entity)fireball);
        stack.shrink(1);
        return stack;
    }

    @Override
    protected void playSound(LevelAccessor level, BlockPos pos) {
        level.levelEvent(1018, pos, 0);
    }
}
