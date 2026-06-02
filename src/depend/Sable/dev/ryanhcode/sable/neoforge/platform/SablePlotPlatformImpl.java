/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.status.ChunkType
 *  net.neoforged.bus.api.Event
 *  net.neoforged.neoforge.common.NeoForge
 *  net.neoforged.neoforge.event.level.ChunkDataEvent$Load
 *  org.slf4j.Logger
 */
package dev.ryanhcode.sable.neoforge.platform;

import com.mojang.logging.LogUtils;
import dev.ryanhcode.sable.platform.SablePlotPlatform;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkType;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkDataEvent;
import org.slf4j.Logger;

public class SablePlotPlatformImpl
implements SablePlotPlatform {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void readLightData(CompoundTag tag, RegistryAccess registryAccess, LevelChunk chunk) {
        if (tag.contains("neoforge:aux_lights", 9)) {
            chunk.getAuxLightManager(chunk.getPos()).deserializeNBT((HolderLookup.Provider)registryAccess, tag.getList("neoforge:aux_lights", 10));
        }
    }

    @Override
    public void readChunkAttachments(CompoundTag tag, RegistryAccess registryAccess, LevelChunk chunk) {
        if (tag.contains("neoforge:attachments", 10)) {
            chunk.readAttachmentsFromNBT((HolderLookup.Provider)registryAccess, tag.getCompound("neoforge:attachments"));
        }
    }

    @Override
    public void postLoad(CompoundTag tag, LevelChunk chunk) {
        NeoForge.EVENT_BUS.post((Event)new ChunkDataEvent.Load((ChunkAccess)chunk, tag, ChunkType.LEVELCHUNK));
    }

    @Override
    public void writeLightData(CompoundTag tag, RegistryAccess registryAccess, LevelChunk chunk) {
        ListTag lightTag = chunk.getAuxLightManager(chunk.getPos()).serializeNBT((HolderLookup.Provider)registryAccess);
        if (lightTag != null) {
            tag.put("neoforge:aux_lights", (Tag)lightTag);
        }
    }

    @Override
    public void writeChunkAttachments(CompoundTag tag, RegistryAccess registryAccess, LevelChunk chunk) {
        try {
            CompoundTag capTag = chunk.writeAttachmentsToNBT((HolderLookup.Provider)registryAccess);
            if (capTag != null) {
                tag.put("neoforge:attachments", (Tag)capTag);
            }
        }
        catch (Exception e) {
            LOGGER.error("Failed to write chunk attachments. An attachment has likely thrown an exception trying to write state. It will not persist. Report this to the mod author", (Throwable)e);
        }
    }
}
