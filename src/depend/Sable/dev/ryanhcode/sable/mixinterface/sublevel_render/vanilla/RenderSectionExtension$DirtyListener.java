/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.chunk.SectionRenderDispatcher$RenderSection
 */
package dev.ryanhcode.sable.mixinterface.sublevel_render.vanilla;

import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;

@FunctionalInterface
public static interface RenderSectionExtension.DirtyListener {
    public void markDirty(SectionRenderDispatcher.RenderSection var1);
}
