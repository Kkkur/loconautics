/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.LivingEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.mixin.entity.entities_stick_sublevels.effects;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.Sable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={LivingEntity.class})
public class LivingEntityMixin {
    @WrapOperation(method={"checkFallDamage"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/LivingEntity;blockPosition()Lnet/minecraft/core/BlockPos;")})
    private BlockPos sable$fallDamageParticlesPosition(LivingEntity instance, Operation<BlockPos> original, @Local(argsOnly=true) BlockPos blockPos) {
        if (Sable.HELPER.getContaining(instance.level(), (Vec3i)blockPos) != null) {
            return blockPos;
        }
        return (BlockPos)original.call(new Object[]{instance});
    }
}
