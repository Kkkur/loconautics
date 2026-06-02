/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.phys.AABB
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.ryanhcode.sable.mixin.prevent_freezing;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={Biome.class})
public class BiomeMixin {
    @WrapMethod(method={"shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z"})
    public boolean sable$preventFreezing(LevelReader levelReader, BlockPos blockPos, boolean bl, Operation<Boolean> original) {
        if (!((Boolean)original.call(new Object[]{levelReader, blockPos, bl})).booleanValue()) {
            return false;
        }
        if (levelReader instanceof Level) {
            Level level = (Level)levelReader;
            ActiveSableCompanion helper = Sable.HELPER;
            SubLevel parent = helper.getContaining(level, (Vec3i)blockPos);
            BlockPos projectedPos = blockPos;
            if (parent != null) {
                projectedPos = BlockPos.containing((Position)parent.logicalPose().transformPosition(projectedPos.getCenter()));
            }
            BoundingBox3d bb3d = new BoundingBox3d(projectedPos);
            Iterable<SubLevel> allIntersecting = helper.getAllIntersecting(level, (BoundingBox3dc)bb3d);
            for (SubLevel subLevel : allIntersecting) {
                if (subLevel == parent) continue;
                bb3d.set((double)projectedPos.getX(), (double)projectedPos.getY(), (double)projectedPos.getZ(), (double)(projectedPos.getX() + 1), (double)(projectedPos.getY() + 1), (double)(projectedPos.getZ() + 1));
                bb3d.transformInverse((Pose3dc)subLevel.logicalPose());
                if (!BlockPos.betweenClosedStream((AABB)bb3d.toMojang()).anyMatch(p -> !level.getBlockState(p).canBeReplaced())) continue;
                return false;
            }
        }
        return true;
    }
}
