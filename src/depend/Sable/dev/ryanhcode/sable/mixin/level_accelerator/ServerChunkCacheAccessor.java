/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ChunkHolder
 *  net.minecraft.server.level.ServerChunkCache
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package dev.ryanhcode.sable.mixin.level_accelerator;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={ServerChunkCache.class})
public interface ServerChunkCacheAccessor {
    @Invoker
    public ChunkHolder invokeGetVisibleChunkIfPresent(long var1);
}
