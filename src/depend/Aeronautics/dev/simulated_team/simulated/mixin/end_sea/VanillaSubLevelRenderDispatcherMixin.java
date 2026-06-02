/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.shaders.Uniform
 *  dev.ryanhcode.sable.sublevel.render.dispatcher.VanillaSubLevelRenderDispatcher
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.simulated_team.simulated.mixin.end_sea;

import com.mojang.blaze3d.shaders.Uniform;
import dev.ryanhcode.sable.sublevel.render.dispatcher.VanillaSubLevelRenderDispatcher;
import dev.simulated_team.simulated.content.end_sea.EndSeaPhysics;
import dev.simulated_team.simulated.content.end_sea.EndSeaPhysicsData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={VanillaSubLevelRenderDispatcher.class})
public class VanillaSubLevelRenderDispatcherMixin {
    @Inject(method={"setupDynamicEffects"}, at={@At(value="TAIL")})
    private static void setupDynamicEffects(ShaderInstance shader, boolean onSubLevel, boolean upload, CallbackInfo ci) {
        Uniform cameraY = shader.getUniform("EndSeaCameraY");
        if (cameraY != null) {
            Minecraft minecraft = Minecraft.getInstance();
            EndSeaPhysics physics = EndSeaPhysicsData.of((Level)minecraft.level);
            if (onSubLevel && physics != null) {
                float y = (float)(minecraft.gameRenderer.getMainCamera().getPosition().y - physics.startY());
                cameraY.set(y);
                if (upload) {
                    cameraY.upload();
                }
            } else {
                cameraY.set(0.0f);
                if (upload) {
                    cameraY.upload();
                }
            }
        }
    }
}
