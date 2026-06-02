/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Camera
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.sublevel_render;

import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.mixinterface.plot.SubLevelContainerHolder;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.render.dispatcher.SubLevelRenderDispatcher;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class})
public class LevelRendererMixin {
    @Shadow
    @Nullable
    private ClientLevel level;

    @Inject(method={"allChanged"}, at={@At(value="TAIL")})
    private void sable$allChanged(CallbackInfo ci) {
        if (this.level == null) {
            return;
        }
        SubLevelRenderDispatcher.get().rebuild(((ClientSubLevelContainer)((SubLevelContainerHolder)this.level).sable$getPlotContainer()).getAllSubLevels());
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/DimensionSpecialEffects;constantAmbientLight()Z", ordinal=0, shift=At.Shift.BEFORE)})
    private void sable$renderSingleBlockSubLevels(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f modelView, Matrix4f projection, CallbackInfo ci) {
        List<ClientSubLevel> sublevels = ((ClientSubLevelContainer)((SubLevelContainerHolder)this.level).sable$getPlotContainer()).getAllSubLevels();
        Vec3 cameraPosition = camera.getPosition();
        SubLevelRenderDispatcher.get().renderAfterSections(sublevels, cameraPosition.x, cameraPosition.y, cameraPosition.z, modelView, projection, deltaTracker.getGameTimeDeltaPartialTick(false));
    }
}
