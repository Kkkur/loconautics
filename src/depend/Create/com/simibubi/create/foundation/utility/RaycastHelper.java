/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.foundation.utility;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class RaycastHelper {
    public static BlockHitResult rayTraceRange(Level level, Player player, double range) {
        Vec3 origin = player.getEyePosition();
        Vec3 target = RaycastHelper.getTraceTarget(player, range, origin);
        ClipContext context = new ClipContext(origin, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity)player);
        return level.clip(context);
    }

    public static PredicateTraceResult rayTraceUntil(Player player, double range, Predicate<BlockPos> predicate) {
        Vec3 origin = player.getEyePosition();
        Vec3 target = RaycastHelper.getTraceTarget(player, range, origin);
        return RaycastHelper.rayTraceUntil(origin, target, predicate);
    }

    public static Vec3 getTraceTarget(Player player, double range, Vec3 origin) {
        float f = player.getXRot();
        float f1 = player.getYRot();
        float f2 = Mth.cos((float)(-f1 * ((float)Math.PI / 180) - (float)Math.PI));
        float f3 = Mth.sin((float)(-f1 * ((float)Math.PI / 180) - (float)Math.PI));
        float f4 = -Mth.cos((float)(-f * ((float)Math.PI / 180)));
        float f5 = Mth.sin((float)(-f * ((float)Math.PI / 180)));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        return origin.add((double)f6 * range, (double)f5 * range, (double)f7 * range);
    }

    public static PredicateTraceResult rayTraceUntil(Vec3 start, Vec3 end, Predicate<BlockPos> predicate) {
        int z;
        int y;
        if (Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z)) {
            return null;
        }
        if (Double.isNaN(end.x) || Double.isNaN(end.y) || Double.isNaN(end.z)) {
            return null;
        }
        int dx = Mth.floor((double)end.x);
        int dy = Mth.floor((double)end.y);
        int dz = Mth.floor((double)end.z);
        int x = Mth.floor((double)start.x);
        BlockPos.MutableBlockPos currentPos = new BlockPos(x, y = Mth.floor((double)start.y), z = Mth.floor((double)start.z)).mutable();
        if (predicate.test((BlockPos)currentPos)) {
            return new PredicateTraceResult(currentPos.immutable(), Direction.getNearest((float)(dx - x), (float)(dy - y), (float)(dz - z)));
        }
        int remainingDistance = 200;
        while (remainingDistance-- >= 0) {
            Direction enumfacing;
            if (Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z)) {
                return null;
            }
            if (x == dx && y == dy && z == dz) {
                return new PredicateTraceResult();
            }
            boolean flag2 = true;
            boolean flag = true;
            boolean flag1 = true;
            double d0 = 999.0;
            double d1 = 999.0;
            double d2 = 999.0;
            if (dx > x) {
                d0 = (double)x + 1.0;
            } else if (dx < x) {
                d0 = (double)x + 0.0;
            } else {
                flag2 = false;
            }
            if (dy > y) {
                d1 = (double)y + 1.0;
            } else if (dy < y) {
                d1 = (double)y + 0.0;
            } else {
                flag = false;
            }
            if (dz > z) {
                d2 = (double)z + 1.0;
            } else if (dz < z) {
                d2 = (double)z + 0.0;
            } else {
                flag1 = false;
            }
            double d3 = 999.0;
            double d4 = 999.0;
            double d5 = 999.0;
            double d6 = end.x - start.x;
            double d7 = end.y - start.y;
            double d8 = end.z - start.z;
            if (flag2) {
                d3 = (d0 - start.x) / d6;
            }
            if (flag) {
                d4 = (d1 - start.y) / d7;
            }
            if (flag1) {
                d5 = (d2 - start.z) / d8;
            }
            if (d3 == -0.0) {
                d3 = -1.0E-4;
            }
            if (d4 == -0.0) {
                d4 = -1.0E-4;
            }
            if (d5 == -0.0) {
                d5 = -1.0E-4;
            }
            if (d3 < d4 && d3 < d5) {
                enumfacing = dx > x ? Direction.WEST : Direction.EAST;
                start = new Vec3(d0, start.y + d7 * d3, start.z + d8 * d3);
            } else if (d4 < d5) {
                enumfacing = dy > y ? Direction.DOWN : Direction.UP;
                start = new Vec3(start.x + d6 * d4, d1, start.z + d8 * d4);
            } else {
                enumfacing = dz > z ? Direction.NORTH : Direction.SOUTH;
                start = new Vec3(start.x + d6 * d5, start.y + d7 * d5, d2);
            }
            x = Mth.floor((double)start.x) - (enumfacing == Direction.EAST ? 1 : 0);
            y = Mth.floor((double)start.y) - (enumfacing == Direction.UP ? 1 : 0);
            z = Mth.floor((double)start.z) - (enumfacing == Direction.SOUTH ? 1 : 0);
            currentPos.set(x, y, z);
            if (!predicate.test((BlockPos)currentPos)) continue;
            return new PredicateTraceResult(currentPos.immutable(), enumfacing);
        }
        return new PredicateTraceResult();
    }

    public static class PredicateTraceResult {
        private BlockPos pos;
        private Direction facing;

        public PredicateTraceResult(BlockPos pos, Direction facing) {
            this.pos = pos;
            this.facing = facing;
        }

        public PredicateTraceResult() {
        }

        public Direction getFacing() {
            return this.facing;
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public boolean missed() {
            return this.pos == null;
        }
    }
}
