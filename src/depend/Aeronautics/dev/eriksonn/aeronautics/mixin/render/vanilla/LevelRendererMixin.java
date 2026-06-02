/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef
 *  com.mojang.blaze3d.shaders.Uniform
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.ShaderInstance
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.eriksonn.aeronautics.mixin.render.vanilla;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.shaders.Uniform;
import dev.eriksonn.aeronautics.content.blocks.levitite.LevititeShaderManager;
import dev.eriksonn.aeronautics.index.client.AeroRenderTypes;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class}, priority=990)
public class LevelRendererMixin {
    @Shadow
    @Nullable
    private ClientLevel level;

    @Inject(method={"renderSectionLayer"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/ShaderInstance;apply()V", shift=At.Shift.AFTER)})
    public void aeronautics$setupLevititeShaders(RenderType renderType, double x, double y, double z, Matrix4f frustrumMatrix, Matrix4f projectionMatrix, CallbackInfo ci, @Local ShaderInstance shaderinstance, @Local LocalBooleanRef flag1) {
        if (renderType == AeroRenderTypes.levititeGhosts()) {
            flag1.set(false);
        } else if (renderType == AeroRenderTypes.levitite()) {
            Uniform time = shaderinstance.getUniform("time");
            if (time != null) {
                long ticks = this.level.getGameTime();
                float pt = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
                time.set((float)(ticks %= 100000L) + pt);
            }
            LevititeShaderManager.prepareShaderForWorld(shaderinstance, x, y, z);
        }
    }

    @Inject(method={"renderSectionLayer"}, at={@At(value="TAIL")})
    public void aeronautics$cleanupLevititeShaders(RenderType renderType, double x, double y, double z, Matrix4f frustrumMatrix, Matrix4f projectionMatrix, CallbackInfo ci, @Local ShaderInstance shaderinstance) {
        if (renderType == AeroRenderTypes.levitite()) {
            LevititeShaderManager.prepareShaderForWorld(shaderinstance, x, y, z);
        }
    }
}
