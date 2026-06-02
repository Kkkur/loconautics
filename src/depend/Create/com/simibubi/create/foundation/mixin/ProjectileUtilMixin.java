/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.projectile.ProjectileUtil
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package com.simibubi.create.foundation.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={ProjectileUtil.class})
public class ProjectileUtilMixin {
    @WrapOperation(method={"getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;canRiderInteract()Z")})
    private static boolean create$interactWithEntitiesOnContraptions(Entity instance, Operation<Boolean> original) {
        return (Boolean)original.call(new Object[]{instance}) != false || instance.getRootVehicle() instanceof AbstractContraptionEntity;
    }
}
