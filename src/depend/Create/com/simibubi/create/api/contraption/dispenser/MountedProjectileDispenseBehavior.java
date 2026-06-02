/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.dispenser.ProjectileDispenseBehavior
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.contraption.dispenser;

import com.simibubi.create.api.contraption.dispenser.DefaultMountedDispenseBehavior;
import com.simibubi.create.api.contraption.dispenser.MountedDispenseBehavior;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.mixin.accessor.ProjectileDispenseBehaviorAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class MountedProjectileDispenseBehavior
extends DefaultMountedDispenseBehavior {
    @Override
    protected ItemStack execute(ItemStack stack, MovementContext context, BlockPos pos, Vec3 facing) {
        double z;
        double y;
        double x = (double)pos.getX() + facing.x * 0.7 + 0.5;
        Projectile projectile = this.getProjectile(context.world, x, y = (double)pos.getY() + facing.y * 0.7 + 0.5, z = (double)pos.getZ() + facing.z * 0.7 + 0.5, stack.copy(), MountedDispenseBehavior.getClosestFacingDirection(facing));
        if (projectile == null) {
            return stack;
        }
        Vec3 motion = facing.scale((double)this.getPower()).add(context.motion);
        projectile.shoot(motion.x, motion.y, motion.z, (float)motion.length(), this.getUncertainty());
        context.world.addFreshEntity((Entity)projectile);
        stack.shrink(1);
        return stack;
    }

    @Override
    protected void playSound(LevelAccessor level, BlockPos pos) {
        level.levelEvent(1002, pos, 0);
    }

    @Nullable
    protected abstract Projectile getProjectile(Level var1, double var2, double var4, double var6, ItemStack var8, Direction var9);

    protected float getUncertainty() {
        return 6.0f;
    }

    protected float getPower() {
        return 1.1f;
    }

    public static MountedDispenseBehavior of(ProjectileDispenseBehavior vanillaBehaviour) {
        final ProjectileDispenseBehaviorAccessor accessor = (ProjectileDispenseBehaviorAccessor)vanillaBehaviour;
        return new MountedProjectileDispenseBehavior(){

            @Override
            protected Projectile getProjectile(Level level, double x, double y, double z, ItemStack stack, Direction facing) {
                return accessor.create$getProjectileItem().asProjectile(level, (Position)new Vec3(x, y, z), stack, facing);
            }

            @Override
            protected float getUncertainty() {
                return accessor.create$getDispenseConfig().uncertainty();
            }

            @Override
            protected float getPower() {
                return accessor.create$getDispenseConfig().power();
            }
        };
    }
}
