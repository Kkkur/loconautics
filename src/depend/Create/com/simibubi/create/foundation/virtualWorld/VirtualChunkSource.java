/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.ChunkSource
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.status.ChunkStatus
 *  net.minecraft.world.level.lighting.LevelLightEngine
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.virtualWorld;

import com.simibubi.create.foundation.virtualWorld.VirtualChunk;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.function.BooleanSupplier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.jetbrains.annotations.Nullable;

public class VirtualChunkSource
extends ChunkSource {
    private final VirtualRenderWorld world;
    private final Long2ObjectMap<VirtualChunk> chunks = new Long2ObjectOpenHashMap();

    public VirtualChunkSource(VirtualRenderWorld world) {
        this.world = world;
    }

    public Level getLevel() {
        return this.world;
    }

    public ChunkAccess getChunk(int x, int z) {
        return (ChunkAccess)this.chunks.computeIfAbsent(ChunkPos.asLong((int)x, (int)z), packedPos -> new VirtualChunk(this.world, ChunkPos.getX((long)packedPos), ChunkPos.getZ((long)packedPos)));
    }

    @Nullable
    public LevelChunk getChunk(int x, int z, boolean load) {
        return null;
    }

    @Nullable
    public ChunkAccess getChunk(int x, int z, ChunkStatus status, boolean load) {
        return this.getChunk(x, z);
    }

    public void tick(BooleanSupplier hasTimeLeft, boolean tickChunks) {
    }

    public String gatherStats() {
        return "VirtualChunkSource";
    }

    public int getLoadedChunksCount() {
        return 0;
    }

    public LevelLightEngine getLightEngine() {
        return this.world.getLightEngine();
    }
}
