/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.neoforged.bus.api.Event
 *  net.neoforged.neoforge.common.NeoForge
 *  net.neoforged.neoforge.event.level.ChunkEvent$Load
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package dev.ryanhcode.sable.neoforge.platform;

import dev.ryanhcode.sable.platform.SableChunkEventPlatform;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SableChunkEventPlatformImpl
implements SableChunkEventPlatform {
    @Override
    public void onChunkPacketReplaced(LevelChunk chunk) {
        NeoForge.EVENT_BUS.post((Event)new ChunkEvent.Load((ChunkAccess)chunk, false));
    }

    @Override
    public void onOldChunkInvalid(LevelChunk chunk) {
    }
}
