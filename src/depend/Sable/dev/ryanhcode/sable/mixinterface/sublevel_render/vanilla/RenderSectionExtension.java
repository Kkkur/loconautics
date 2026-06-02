/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.chunk.SectionRenderDispatcher$RenderSection
 */
package dev.ryanhcode.sable.mixinterface.sublevel_render.vanilla;

import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;

public interface RenderSectionExtension {
    public void sable$addDirtyListener(DirtyListener var1);

    public void sable$setListening(boolean var1);

    @FunctionalInterface
    public static interface DirtyListener {
        public void markDirty(SectionRenderDispatcher.RenderSection var1);
    }
}
