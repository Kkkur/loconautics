/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.lighting.LevelLightEngine
 */
package dev.ryanhcode.sable.sublevel.plot;

import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ClientLevelPlot
extends LevelPlot {
    public ClientLevelPlot(SubLevelContainer plotContainer, int x, int z, int logSize, ClientSubLevel subLevel) {
        super(plotContainer, x, z, logSize, subLevel);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return this.getSubLevel().getLevel().getLightEngine();
    }

    @Override
    public ClientSubLevel getSubLevel() {
        return (ClientSubLevel)super.getSubLevel();
    }

    @Override
    protected void onRemoveChunkHolder(LevelChunk levelChunk) {
        ((ClientLevel)levelChunk.getLevel()).unload(levelChunk);
    }

    @Override
    public void addChunkHolder(ChunkPos localChunkPos, PlotChunkHolder holder, boolean initializeLighting) {
        super.addChunkHolder(localChunkPos, holder, initializeLighting);
        for (BlockEntity blockEntity : holder.getChunk().getBlockEntities().values()) {
            BlockEntitySubLevelActor actor = blockEntity instanceof BlockEntitySubLevelActor ? (BlockEntitySubLevelActor)blockEntity : null;
            if (actor == null) continue;
            this.blockEntityActors.put((Object)blockEntity.getBlockPos(), (Object)actor);
        }
    }
}
