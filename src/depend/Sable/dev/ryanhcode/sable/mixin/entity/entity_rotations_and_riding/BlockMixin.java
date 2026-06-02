/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.mixin.entity.entity_rotations_and_riding;

import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.api.math.OrientedBoundingBox3d;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={Block.class})
public class BlockMixin {
    @Redirect(method={"updateEntityAfterFallOn"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 sable$rotateWithEntity(Vec3 instance, double x, double y, double z, @Local(argsOnly=true) Entity entity) {
        Quaterniondc orientation = EntitySubLevelUtil.getCustomEntityOrientation(entity, 1.0f);
        if (orientation == null) {
            return instance.multiply(x, y, z);
        }
        Vector3d up = orientation.transform(OrientedBoundingBox3d.UP, new Vector3d());
        double dot = up.dot(instance.x, instance.y, instance.z);
        return instance.subtract(up.x * dot, up.y * dot, up.z * dot);
    }
}
