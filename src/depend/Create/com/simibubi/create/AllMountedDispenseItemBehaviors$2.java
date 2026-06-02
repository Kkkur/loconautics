/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.PrimedTnt
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create;

import com.simibubi.create.api.contraption.dispenser.DefaultMountedDispenseBehavior;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

class AllMountedDispenseItemBehaviors.2
extends DefaultMountedDispenseBehavior {
    AllMountedDispenseItemBehaviors.2() {
    }

    @Override
    protected ItemStack execute(ItemStack stack, MovementContext context, BlockPos pos, Vec3 facing) {
        double x = (double)pos.getX() + facing.x * 0.7 + 0.5;
        double y = (double)pos.getY() + facing.y * 0.7 + 0.5;
        double z = (double)pos.getZ() + facing.z * 0.7 + 0.5;
        PrimedTnt tnt = new PrimedTnt(context.world, x, y, z, null);
        tnt.push(context.motion.x, context.motion.y, context.motion.z);
        context.world.addFreshEntity((Entity)tnt);
        context.world.playSound(null, tnt.getX(), tnt.getY(), tnt.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.0f);
        stack.shrink(1);
        return stack;
    }
}
