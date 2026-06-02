/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.client.render.VeilRenderBridge
 *  foundry.veil.api.client.render.VeilRenderSystem
 *  foundry.veil.api.client.render.shader.program.ShaderProgram
 *  foundry.veil.api.client.render.shader.uniform.ShaderUniform
 *  net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer
 *  net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.eriksonn.aeronautics.mixin.render.sodium;

import dev.eriksonn.aeronautics.content.blocks.levitite.LevititeShaderManager;
import dev.eriksonn.aeronautics.index.client.AeroRenderTypes;
import foundry.veil.api.client.render.VeilRenderBridge;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.render.shader.uniform.ShaderUniform;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={SodiumWorldRenderer.class}, priority=990)
public class SodiumWorldRendererMixin {
    @Inject(method={"drawChunkLayer"}, at={@At(value="HEAD")})
    public void aeronautics$setupLevititeShaders(RenderType renderType, ChunkRenderMatrices matrices, double x, double y, double z, CallbackInfo ci) {
        if (renderType == AeroRenderTypes.levitite()) {
            ShaderProgram shader = VeilRenderSystem.setShader((ResourceLocation)AeroRenderTypes.LEVITITE_SHADER);
            if (shader == null) {
                return;
            }
            ShaderUniform time = shader.getUniform((CharSequence)"time");
            if (time != null) {
                long ticks = Minecraft.getInstance().level.getGameTime();
                float pt = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
                time.setFloat((float)(ticks %= 100000L) + pt);
            }
            LevititeShaderManager.prepareShaderForWorld(VeilRenderBridge.toShaderInstance((ShaderProgram)shader), x, y, z);
        }
    }

    @Inject(method={"drawChunkLayer"}, at={@At(value="TAIL")})
    public void aeronautics$cleanupLevititeShaders(RenderType renderLayer, ChunkRenderMatrices matrices, double x, double y, double z, CallbackInfo ci) {
        if (renderLayer == AeroRenderTypes.levitite()) {
            ShaderProgram shader = VeilRenderSystem.setShader((ResourceLocation)AeroRenderTypes.LEVITITE_SHADER);
            if (shader == null) {
                return;
            }
            LevititeShaderManager.prepareShaderForWorld(VeilRenderBridge.toShaderInstance((ShaderProgram)shader), x, y, z);
        }
    }
}
