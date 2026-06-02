/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  foundry.veil.api.client.render.VeilRenderBridge
 *  net.minecraft.client.renderer.RenderStateShard
 *  net.minecraft.client.renderer.RenderStateShard$EmptyTextureStateShard
 *  net.minecraft.client.renderer.RenderStateShard$LightmapStateShard
 *  net.minecraft.client.renderer.RenderStateShard$OutputStateShard
 *  net.minecraft.client.renderer.RenderStateShard$ShaderStateShard
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.RenderType$CompositeState
 *  net.minecraft.client.renderer.RenderType$CompositeState$CompositeStateBuilder
 *  net.minecraft.resources.ResourceLocation
 */
package dev.eriksonn.aeronautics.index.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.content.blocks.levitite.LevititeShaderManager;
import foundry.veil.api.client.render.VeilRenderBridge;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class AeroRenderTypes
extends RenderType {
    public static final ResourceLocation LEVITITE_SHADER = Aeronautics.path("levitite/levitite");
    private static final RenderStateShard.ShaderStateShard LEVITITE_SHADER_SHARD = new LevititeShaderState((RenderStateShard)VeilRenderBridge.shaderState((ResourceLocation)LEVITITE_SHADER), (RenderStateShard)new RenderStateShard.OutputStateShard("disabled", () -> {
        RENDERTYPE_SOLID_SHADER.setupRenderState();
        RenderSystem.colorMask((boolean)false, (boolean)false, (boolean)false, (boolean)false);
        RenderSystem.depthMask((boolean)false);
    }, () -> {
        RENDERTYPE_SOLID_SHADER.clearRenderState();
        RenderSystem.colorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
        RenderSystem.depthMask((boolean)true);
    }));
    private static final RenderType LEVITITE = RenderType.create((String)"aeronautics:levitite", (VertexFormat)DefaultVertexFormat.BLOCK, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)1536, (boolean)false, (boolean)true, (RenderType.CompositeState)VeilRenderBridge.create((RenderType.CompositeState.CompositeStateBuilder)RenderType.CompositeState.builder().setShaderState(LEVITITE_SHADER_SHARD).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(CULL).setTextureState((RenderStateShard.EmptyTextureStateShard)RenderStateShard.BLOCK_SHEET).setLightmapState(RenderStateShard.LightmapStateShard.LIGHTMAP)).addLayer(VeilRenderBridge.patchState((int)4)).create(false));
    private static final RenderType LEVITITE_GHOSTS = RenderType.create((String)"aeronautics:levitite_ghosts", (VertexFormat)DefaultVertexFormat.BLOCK, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)1536, (boolean)false, (boolean)true, (RenderType.CompositeState)VeilRenderBridge.create((RenderType.CompositeState.CompositeStateBuilder)RenderType.CompositeState.builder().setShaderState(LEVITITE_SHADER_SHARD).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setTextureState((RenderStateShard.EmptyTextureStateShard)RenderStateShard.BLOCK_SHEET).setLightmapState(RenderStateShard.LightmapStateShard.LIGHTMAP)).addLayer(VeilRenderBridge.patchState((int)4)).create(false));

    public AeroRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static RenderType levitite() {
        return LEVITITE;
    }

    public static RenderType levititeGhosts() {
        return LEVITITE_GHOSTS;
    }

    private static class LevititeShaderState
    extends RenderStateShard.ShaderStateShard {
        private final RenderStateShard enabled;
        private final RenderStateShard disabled;

        public LevititeShaderState(RenderStateShard enabled, RenderStateShard disabled) {
            this.enabled = enabled;
            this.disabled = disabled;
        }

        public void setupRenderState() {
            if (LevititeShaderManager.isEnabled()) {
                this.enabled.setupRenderState();
            } else {
                this.disabled.setupRenderState();
            }
        }

        public void clearRenderState() {
            if (LevititeShaderManager.isEnabled()) {
                this.enabled.clearRenderState();
            } else {
                this.disabled.clearRenderState();
            }
        }

        public String toString() {
            return LevititeShaderManager.isEnabled() ? this.enabled.toString() : this.disabled.toString();
        }
    }
}
