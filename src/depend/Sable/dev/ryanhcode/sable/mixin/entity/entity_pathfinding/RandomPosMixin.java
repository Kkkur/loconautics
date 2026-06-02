/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.ai.util.RandomPos
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 */
package dev.ryanhcode.sable.mixin.entity.entity_pathfinding;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value={RandomPos.class})
public class RandomPosMixin {
    @Overwrite
    public static BlockPos generateRandomPosTowardDirection(PathfinderMob mob, int someInteger, RandomSource random, BlockPos pos) {
        SubLevel trackingSubLevel = Sable.HELPER.getTrackingSubLevel((Entity)mob);
        Vec3 effectiveMobPos = mob.position();
        if (trackingSubLevel != null) {
            effectiveMobPos = trackingSubLevel.logicalPose().transformPositionInverse(effectiveMobPos);
        }
        int ox = pos.getX();
        int oz = pos.getZ();
        if (mob.hasRestriction() && someInteger > 1) {
            BlockPos blockPos = mob.getRestrictCenter();
            ox = effectiveMobPos.x() > (double)blockPos.getX() ? (ox -= random.nextInt(someInteger / 2)) : (ox += random.nextInt(someInteger / 2));
            oz = effectiveMobPos.z() > (double)blockPos.getZ() ? (oz -= random.nextInt(someInteger / 2)) : (oz += random.nextInt(someInteger / 2));
        }
        return BlockPos.containing((double)((double)ox + effectiveMobPos.x()), (double)((double)pos.getY() + effectiveMobPos.y()), (double)((double)oz + effectiveMobPos.z()));
    }
}
