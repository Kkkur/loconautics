/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderStateShard
 *  net.minecraft.client.renderer.RenderStateShard$ShaderStateShard
 */
package dev.eriksonn.aeronautics.index.client;

import dev.eriksonn.aeronautics.content.blocks.levitite.LevititeShaderManager;
import net.minecraft.client.renderer.RenderStateShard;

private static class AeroRenderTypes.LevititeShaderState
extends RenderStateShard.ShaderStateShard {
    private final RenderStateShard enabled;
    private final RenderStateShard disabled;

    public AeroRenderTypes.LevititeShaderState(RenderStateShard enabled, RenderStateShard disabled) {
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
