/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderer
 *  net.caffeinemc.mods.sodium.client.render.chunk.RenderSection
 *  net.caffeinemc.mods.sodium.client.render.chunk.occlusion.OcclusionCuller
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.mixinterface.sublevel_render.sodium;

import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.occlusion.OcclusionCuller;
import org.jetbrains.annotations.Nullable;

public interface RenderSectionManagerExtension {
    @Nullable
    public RenderSection sable$getRenderSection(int var1, int var2, int var3);

    public void sable$setRenderSectionDirty(int var1, int var2, int var3, boolean var4);

    public OcclusionCuller sable$getOcclusionCuller();

    public ChunkRenderer sable$getChunkRenderer();
}
