/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.CameraType
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.camera.new_camera_types;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.mixinhelpers.camera.new_camera_types.SableCameraTypes;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.CameraType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GameRenderer.class})
public class GameRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V", shift=At.Shift.BEFORE)})
    public void sable$setupCamera(DeltaTracker deltaTracker, CallbackInfo ci) {
        CameraType cameraType = this.minecraft.options.getCameraType();
        if (cameraType == SableCameraTypes.SUB_LEVEL_VIEW || cameraType == SableCameraTypes.SUB_LEVEL_VIEW_UNLOCKED) {
            SubLevel subLevel;
            Entity vehicle = this.minecraft.cameraEntity.getVehicle();
            if (vehicle != null && (subLevel = Sable.HELPER.getContaining((Level)this.minecraft.level, (Position)vehicle.position())) != null) {
                return;
            }
            this.minecraft.options.setCameraType(CameraType.FIRST_PERSON);
        }
    }
}
