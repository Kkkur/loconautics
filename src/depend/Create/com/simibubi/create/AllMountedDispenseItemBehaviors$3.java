/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.projectile.FireworkRocketEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create;

import com.simibubi.create.api.contraption.dispenser.DefaultMountedDispenseBehavior;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

class AllMountedDispenseItemBehaviors.3
extends DefaultMountedDispenseBehavior {
    AllMountedDispenseItemBehaviors.3() {
    }

    @Override
    protected ItemStack execute(ItemStack stack, MovementContext context, BlockPos pos, Vec3 facing) {
        double x = (double)pos.getX() + facing.x * 0.7 + 0.5;
        double y = (double)pos.getY() + facing.y * 0.7 + 0.5;
        double z = (double)pos.getZ() + facing.z * 0.7 + 0.5;
        FireworkRocketEntity firework = new FireworkRocketEntity(context.world, stack, x, y, z, true);
        firework.shoot(facing.x, facing.y, facing.z, 0.5f, 1.0f);
        context.world.addFreshEntity((Entity)firework);
        stack.shrink(1);
        return stack;
    }

    @Override
    protected void playSound(LevelAccessor level, BlockPos pos) {
        level.levelEvent(1004, pos, 0);
    }
}
