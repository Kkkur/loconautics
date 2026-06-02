/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.TamableAnimal
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.mixin.entity.tamed_teleport;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.math.OrientedBoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={TamableAnimal.class})
public class TamableAnimalMixin {
    @Unique
    private static final BoundingBox3d sable$BOX = new BoundingBox3d();

    @WrapOperation(method={"maybeTeleportTo"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/TamableAnimal;canTeleportTo(Lnet/minecraft/core/BlockPos;)Z")})
    private static boolean sable$blockPosition(TamableAnimal instance, BlockPos blockPos, Operation<Boolean> original) {
        double dot;
        BlockPos pos;
        SubLevel subLevel = Sable.HELPER.getTrackingSubLevel((Entity)instance.getOwner());
        if (subLevel != null && ((Boolean)original.call(new Object[]{instance, pos = BlockPos.containing((Position)subLevel.logicalPose().transformPositionInverse(blockPos.getCenter()))})).booleanValue() && (dot = subLevel.logicalPose().transformNormal(new Vector3d(0.0, 1.0, 0.0)).dot(OrientedBoundingBox3d.UP)) > 0.85) {
            return true;
        }
        sable$BOX.set(instance.getBoundingBox().move(blockPos.subtract((Vec3i)instance.blockPosition())));
        Iterable<SubLevel> subLevels = Sable.HELPER.getAllIntersecting(instance.level(), (BoundingBox3dc)sable$BOX);
        for (SubLevel subLevel1 : subLevels) {
            Vector3d center = sable$BOX.center();
            BlockPos pos2 = BlockPos.containing((Position)subLevel1.logicalPose().transformPositionInverse(new Vec3(center.x(), center.y(), center.z())));
            if (instance.level().getBlockState(pos2).isAir()) continue;
            return false;
        }
        return (Boolean)original.call(new Object[]{instance, blockPos});
    }
}
