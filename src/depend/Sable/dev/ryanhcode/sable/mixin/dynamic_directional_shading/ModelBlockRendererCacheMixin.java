/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.block.ModelBlockRenderer$Cache
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 */
package dev.ryanhcode.sable.mixin.dynamic_directional_shading;

import dev.ryanhcode.sable.mixinterface.dynamic_directional_shading.ModelBlockRendererCacheExtension;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={ModelBlockRenderer.Cache.class})
public class ModelBlockRendererCacheMixin
implements ModelBlockRendererCacheExtension {
    @Unique
    private boolean sable$onSubLevel;

    @Override
    public void sable$setOnSubLevel(boolean onSubLevel) {
        this.sable$onSubLevel = onSubLevel;
    }

    @Override
    public boolean sable$getOnSubLevel() {
        return this.sable$onSubLevel;
    }
}
