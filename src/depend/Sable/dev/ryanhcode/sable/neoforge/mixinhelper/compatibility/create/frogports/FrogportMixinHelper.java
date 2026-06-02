/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.simibubi.create.content.logistics.packagePort.PackagePortBlockEntity
 *  com.simibubi.create.content.logistics.packagePort.PackagePortTarget
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.Vec3
 */
package dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.frogports;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.content.logistics.packagePort.PackagePortBlockEntity;
import com.simibubi.create.content.logistics.packagePort.PackagePortTarget;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public class FrogportMixinHelper {
    private FrogportMixinHelper() {
    }

    public static Vec3 getExactTargetLocation(PackagePortTarget instance, PackagePortBlockEntity packagePortBlockEntity, LevelAccessor levelAccessor, BlockPos blockPos, Operation<Vec3> original) {
        ActiveSableCompanion helper = Sable.HELPER;
        Vec3 originalTarget = (Vec3)original.call(new Object[]{instance, packagePortBlockEntity, levelAccessor, blockPos});
        Level level = packagePortBlockEntity.getLevel();
        Vec3 globalTarget = helper.projectOutOfSubLevel(level, originalTarget);
        SubLevel subLevel = helper.getContaining(level, (Vec3i)packagePortBlockEntity.getBlockPos());
        Vec3 localTarget = globalTarget;
        if (subLevel != null) {
            localTarget = subLevel.logicalPose().transformPositionInverse(localTarget);
        }
        return localTarget;
    }
}
