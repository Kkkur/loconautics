/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.equipment.goggles.GoggleOverlayRenderer
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.util.Mth
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.simulated_team.simulated.mixin.hold_interaction;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.equipment.goggles.GoggleOverlayRenderer;
import dev.simulated_team.simulated.index.SimClickInteractions;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GoggleOverlayRenderer.class})
public class GoggleOverlayRendererMixin {
    @Shadow
    public static int hoverTicks;

    @Inject(method={"renderOverlay"}, at={@At(value="INVOKE", target="Lnet/minecraft/util/Mth;clamp(FFF)F", shift=At.Shift.BEFORE)}, remap=false)
    private static void decrementRenderTicks(CallbackInfo ci) {
        if (SimClickInteractions.STEERING_WHEEL_MANAGER.isActive()) {
            hoverTicks = Mth.clamp((int)(hoverTicks - 2), (int)0, (int)24);
        }
    }

    @WrapOperation(method={"renderOverlay"}, at={@At(value="INVOKE", target="Lnet/minecraft/util/Mth;clamp(FFF)F")}, remap=false)
    private static float fixPartialTicks(float value, float min, float max, Operation<Float> original, @Local(argsOnly=true) DeltaTracker deltaTracker) {
        if (SimClickInteractions.STEERING_WHEEL_MANAGER.isActive()) {
            return Mth.clamp((float)((float)hoverTicks - deltaTracker.getGameTimeDeltaTicks()), (float)0.0f, (float)24.0f) / 24.0f;
        }
        return ((Float)original.call(new Object[]{Float.valueOf(value), Float.valueOf(min), Float.valueOf(max)})).floatValue();
    }

    @Inject(method={"renderOverlay"}, at={@At(value="INVOKE", target="Lnet/minecraft/util/Mth;clamp(FFF)F")}, remap=false, cancellable=true)
    private static void dontRenderTheText(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if ((float)hoverTicks - deltaTracker.getGameTimeDeltaTicks() <= 0.0f) {
            ci.cancel();
        }
    }
}
