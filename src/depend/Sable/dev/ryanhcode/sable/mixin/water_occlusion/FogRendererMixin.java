/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.FogRenderer
 *  net.minecraft.world.level.material.FogType
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.mixin.water_occlusion;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.mixinterface.water_occlusion.CameraWaterOcclusionExtension;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={FogRenderer.class})
public class FogRendererMixin {
    @WrapOperation(method={"*"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/Camera;getFluidInCamera()Lnet/minecraft/world/level/material/FogType;")})
    private static FogType sable$getFluidinCamera(Camera instance, Operation<FogType> original) {
        CameraWaterOcclusionExtension camera = (CameraWaterOcclusionExtension)Minecraft.getInstance().gameRenderer.getMainCamera();
        camera.sable$setIgnoreOcclusion(true);
        FogType type = (FogType)original.call(new Object[]{instance});
        camera.sable$setIgnoreOcclusion(false);
        return type;
    }
}
