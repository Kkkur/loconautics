/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.culling.Frustum
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package com.simibubi.create.foundation.mixin.accessor;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={LevelRenderer.class})
public interface LevelRendererAccessor {
    @Accessor(value="cullingFrustum")
    public Frustum create$getCullingFrustum();

    @Accessor(value="capturedFrustum")
    @Nullable
    public Frustum create$getCapturedFrustum();
}
