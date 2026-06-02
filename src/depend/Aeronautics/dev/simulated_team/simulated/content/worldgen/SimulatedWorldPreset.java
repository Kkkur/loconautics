/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.GameRules
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.worldgen;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.Nullable;

public class SimulatedWorldPreset {
    private final ResourceLocation id;
    @Nullable
    private final Component description;

    public SimulatedWorldPreset(ResourceLocation id, @Nullable Component description) {
        this.id = id;
        this.description = description;
    }

    public void onPlayerJoin(ServerLevel level, ServerPlayer player) {
    }

    public void onChunkLoad(ServerLevel level, ChunkAccess chunkAccess, boolean newChunk) {
    }

    public void modifyGameRules(GameRules gameRules) {
    }

    public ResourceLocation id() {
        return this.id;
    }

    @Nullable
    public Component description() {
        return this.description;
    }
}
