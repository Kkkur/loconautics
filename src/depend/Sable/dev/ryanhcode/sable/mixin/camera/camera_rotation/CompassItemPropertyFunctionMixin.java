/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.item.CompassItemPropertyFunction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 */
package dev.ryanhcode.sable.mixin.camera.camera_rotation;

import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value={CompassItemPropertyFunction.class})
public abstract class CompassItemPropertyFunctionMixin {
    @Overwrite
    private double getAngleFromEntityToPos(Entity entity, BlockPos pos) {
        Entity vehicle;
        Vec3 localPos = Vec3.atCenterOf((Vec3i)pos);
        double entityX = entity.getX();
        double entityZ = entity.getZ();
        ActiveSableCompanion helper = Sable.HELPER;
        SubLevel subLevel = helper.getContaining(entity);
        if (subLevel == null && (vehicle = entity.getVehicle()) != null && (subLevel = helper.getContaining(vehicle)) != null) {
            Vec3 localEntityPos = subLevel.lastPose().transformPositionInverse(entity.position());
            entityX = localEntityPos.x;
            entityZ = localEntityPos.z;
        }
        if (subLevel != null) {
            localPos = subLevel.lastPose().transformPositionInverse(localPos);
        }
        return Math.atan2(localPos.z() - entityZ, localPos.x() - entityX) / 6.2831854820251465;
    }
}
