/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.contraption.dispenser;

import com.simibubi.create.api.contraption.dispenser.DefaultMountedDispenseBehavior;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class OptionalMountedDispenseBehavior
extends DefaultMountedDispenseBehavior {
    private boolean success;

    @Override
    protected final ItemStack execute(ItemStack stack, MovementContext context, BlockPos pos, Vec3 facing) {
        ItemStack remainder = this.doExecute(stack, context, pos, facing);
        this.success = remainder != null;
        return remainder == null ? stack : remainder;
    }

    @Override
    protected void playSound(LevelAccessor level, BlockPos pos) {
        if (this.success) {
            super.playSound(level, pos);
        } else {
            level.levelEvent(1001, pos, 0);
        }
    }

    @Nullable
    protected ItemStack doExecute(ItemStack stack, MovementContext context, BlockPos pos, Vec3 facing) {
        return super.execute(stack, context, pos, facing);
    }
}
