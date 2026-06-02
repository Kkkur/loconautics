/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  io.github.mortuusars.exposure.client.animation.CameraPoses
 *  io.github.mortuusars.exposure.world.entity.CameraStandEntity
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.mixin.compatibility.exposure;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.Sable;
import io.github.mortuusars.exposure.client.animation.CameraPoses;
import io.github.mortuusars.exposure.world.entity.CameraStandEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={CameraPoses.class})
public class CameraPosesMixin {
    @WrapOperation(method={"applyStand"}, at={@At(value="INVOKE", target="Lio/github/mortuusars/exposure/world/entity/CameraStandEntity;getEyePosition()Lnet/minecraft/world/phys/Vec3;")})
    private Vec3 sable$applyStand(CameraStandEntity instance, Operation<Vec3> original) {
        Vec3 pos = (Vec3)original.call(new Object[]{instance});
        return Sable.HELPER.projectOutOfSubLevel(instance.level(), pos);
    }
}
