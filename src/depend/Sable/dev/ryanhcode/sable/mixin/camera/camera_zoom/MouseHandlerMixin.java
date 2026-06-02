/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.client.CameraType
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.MouseHandler
 *  net.minecraft.world.entity.player.Inventory
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.mixin.camera.camera_zoom;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.SableClientConfig;
import dev.ryanhcode.sable.mixinhelpers.camera.new_camera_types.SableCameraTypes;
import dev.ryanhcode.sable.mixinterface.camera.camera_zoom.CameraZoomExtension;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={MouseHandler.class})
public class MouseHandlerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @WrapOperation(method={"onScroll"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/player/Inventory;swapPaint(D)V")})
    private void sable$onScroll(Inventory instance, double d, Operation<Void> original) {
        CameraType cameraType = this.minecraft.options.getCameraType();
        if (cameraType == SableCameraTypes.SUB_LEVEL_VIEW || cameraType == SableCameraTypes.SUB_LEVEL_VIEW_UNLOCKED) {
            CameraZoomExtension extension = (CameraZoomExtension)this.minecraft.gameRenderer.getMainCamera();
            extension.sable$setZoomAmount((float)((double)extension.sable$getZoomAmount() - d * (Double)SableClientConfig.ZOOM_SENSITIVITY.get()));
            return;
        }
        original.call(new Object[]{instance, d});
    }
}
