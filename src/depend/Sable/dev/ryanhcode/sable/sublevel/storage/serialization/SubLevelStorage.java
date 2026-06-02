/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap
 *  net.minecraft.FileUtil
 *  net.minecraft.core.SectionPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.ExceptionCollector
 *  net.minecraft.world.level.ChunkPos
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package dev.ryanhcode.sable.sublevel.storage.serialization;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.storage.holding.GlobalSavedSubLevelPointer;
import dev.ryanhcode.sable.sublevel.storage.holding.SavedSubLevelPointer;
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunk;
import dev.ryanhcode.sable.sublevel.storage.region.SubLevelRegionFile;
import dev.ryanhcode.sable.sublevel.storage.region.SubLevelStorageFile;
import dev.ryanhcode.sable.sublevel.storage.serialization.SubLevelData;
import dev.ryanhcode.sable.sublevel.storage.serialization.SubLevelSerializer;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.FileUtil;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ExceptionCollector;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class SubLevelStorage
implements AutoCloseable {
    public static int MAX_CACHE_SIZE = 128;
    private final Long2ObjectLinkedOpenHashMap<SubLevelRegionFile> regionCache = new Long2ObjectLinkedOpenHashMap();
    private final Long2ObjectLinkedOpenHashMap<SubLevelStorageFile> storageCache = new Long2ObjectLinkedOpenHashMap();
    private final Path folder;

    public SubLevelStorage(Path folder) {
        this.folder = folder;
    }

    @NotNull
    private static String getFileName(ChunkPos chunkPos) {
        return "r." + chunkPos.getRegionX() + "." + chunkPos.getRegionZ();
    }

    @NotNull
    private static String getFileName(ChunkPos chunkPos, int index) {
        return "r." + chunkPos.getRegionX() + "." + chunkPos.getRegionZ() + "." + index;
    }

    private SubLevelRegionFile getRegionFile(ChunkPos chunkPos) throws IOException {
        long longKey = ChunkPos.asLong((int)chunkPos.getRegionX(), (int)chunkPos.getRegionZ());
        SubLevelRegionFile existingFile = (SubLevelRegionFile)this.regionCache.getAndMoveToFirst(longKey);
        if (existingFile != null) {
            return existingFile;
        }
        if (this.regionCache.size() >= MAX_CACHE_SIZE) {
            ((SubLevelRegionFile)this.regionCache.removeLast()).close();
        }
        Path path = this.getPath(chunkPos);
        Path externalPath = this.getExternalPath(chunkPos);
        SubLevelRegionFile loadedRegion = new SubLevelRegionFile(path, externalPath);
        this.regionCache.putAndMoveToFirst(longKey, (Object)loadedRegion);
        return loadedRegion;
    }

    private SubLevelStorageFile getRegionStorageFile(ChunkPos chunkPos, int index) throws IOException {
        long longKey = SectionPos.asLong((int)chunkPos.getRegionX(), (int)index, (int)chunkPos.getRegionZ());
        SubLevelStorageFile existingFile = (SubLevelStorageFile)this.storageCache.getAndMoveToFirst(longKey);
        if (existingFile != null) {
            return existingFile;
        }
        if (this.storageCache.size() >= MAX_CACHE_SIZE) {
            ((SubLevelStorageFile)this.storageCache.removeLast()).close();
        }
        FileUtil.createDirectoriesSafe((Path)this.folder);
        Path path = this.getPath(chunkPos, index);
        Path externalPath = this.getExternalPath(chunkPos);
        FileUtil.createDirectoriesSafe((Path)externalPath);
        SubLevelStorageFile loadedRegion = new SubLevelStorageFile(path, externalPath);
        this.storageCache.putAndMoveToFirst(longKey, (Object)loadedRegion);
        return loadedRegion;
    }

    public SubLevelHoldingChunk attemptLoadHoldingChunk(ChunkPos chunkPos) {
        try {
            SubLevelRegionFile regionFile = this.getRegionFile(chunkPos);
            return regionFile.read(chunkPos);
        }
        catch (IOException e) {
            Sable.LOGGER.error("Failed to load holding chunk for {}", (Object)chunkPos, (Object)e);
            return null;
        }
    }

    public void attemptSaveHoldingChunk(ChunkPos chunkPos, SubLevelHoldingChunk holdingChunk) {
        try {
            SubLevelRegionFile regionFile = this.getRegionFile(chunkPos);
            regionFile.trySave(chunkPos.getRegionLocalX(), chunkPos.getRegionLocalZ(), holdingChunk);
        }
        catch (IOException e) {
            Sable.LOGGER.error("Failed to save holding chunk for {}", (Object)chunkPos, (Object)e);
        }
    }

    public SubLevelData attemptLoadSubLevel(ChunkPos chunkPos, SavedSubLevelPointer pointer) {
        try {
            SubLevelStorageFile storageFile = this.getRegionStorageFile(chunkPos, pointer.storageIndex());
            CompoundTag tag = storageFile.read(pointer.subLevelIndex());
            if (tag == null) {
                Sable.LOGGER.error("Couldn't find sub-level at index {} in storage file for chunk {}", (Object)pointer.subLevelIndex(), (Object)chunkPos);
                return null;
            }
            SubLevelData subLevel = SubLevelSerializer.fromData(tag);
            if (subLevel != null) {
                subLevel.setOriginLoadedChunk(chunkPos);
            } else {
                Sable.LOGGER.error("Failed to load sub-level at index {} in storage file for chunk {}", (Object)pointer.subLevelIndex(), (Object)chunkPos);
            }
            return subLevel;
        }
        catch (IOException e) {
            Sable.LOGGER.error("Failed to load sub-level for {}", (Object)chunkPos, (Object)e);
            return null;
        }
    }

    public GlobalSavedSubLevelPointer attemptSaveSubLevel(ChunkPos chunkPos, SubLevelData subLevel) {
        try {
            int storageIndex = 0;
            while (true) {
                SubLevelStorageFile storageFile;
                int subLevelIndex;
                if ((subLevelIndex = (storageFile = this.getRegionStorageFile(chunkPos, storageIndex)).findFreeIndex()) != -1 && subLevelIndex < storageFile.getTotalIndexCapacity()) {
                    storageFile.write(subLevelIndex, subLevel.fullTag());
                    return new GlobalSavedSubLevelPointer(chunkPos, (short)storageIndex, (short)subLevelIndex);
                }
                ++storageIndex;
            }
        }
        catch (IOException e) {
            Sable.LOGGER.error("Failed to save sub-level for {}", (Object)chunkPos, (Object)e);
            return null;
        }
    }

    public void attemptSaveSubLevel(GlobalSavedSubLevelPointer pointer, SubLevelData subLevel) {
        try {
            SubLevelStorageFile storageFile = this.getRegionStorageFile(pointer.chunkPos(), pointer.storageIndex());
            storageFile.write((int)pointer.subLevelIndex(), subLevel != null ? subLevel.fullTag() : null);
        }
        catch (IOException e) {
            Sable.LOGGER.error("Failed to save sub-level for {}", (Object)pointer.chunkPos(), (Object)e);
        }
    }

    @NotNull
    private Path getExternalPath(ChunkPos chunkPos) {
        return this.folder.resolve(SubLevelStorage.getFileName(chunkPos) + ".r");
    }

    @NotNull
    private Path getExternalPath(ChunkPos chunkPos, int index) {
        return this.folder.resolve(SubLevelStorage.getFileName(chunkPos, index) + ".s");
    }

    @NotNull
    private Path getPath(ChunkPos chunkPos) {
        return this.folder.resolve(SubLevelStorage.getFileName(chunkPos) + ".slvlr");
    }

    @NotNull
    private Path getPath(ChunkPos chunkPos, int index) {
        return this.folder.resolve(SubLevelStorage.getFileName(chunkPos, index) + ".slvls");
    }

    @Override
    public void close() throws IOException {
        ExceptionCollector exceptionCollector = new ExceptionCollector();
        for (SubLevelStorageFile storageFile : this.storageCache.values()) {
            try {
                storageFile.close();
            }
            catch (IOException e) {
                exceptionCollector.add((Throwable)e);
            }
        }
        for (SubLevelRegionFile regionFile : this.regionCache.values()) {
            try {
                regionFile.close();
            }
            catch (IOException e) {
                exceptionCollector.add((Throwable)e);
            }
        }
        exceptionCollector.throwIfPresent();
    }

    @NotNull
    @ApiStatus.Internal
    public Path getFolder() {
        return this.folder;
    }

    public void flush() throws IOException {
        for (SubLevelStorageFile regionFile : this.regionCache.values()) {
            regionFile.flush();
        }
        for (SubLevelStorageFile regionFile : this.storageCache.values()) {
            regionFile.flush();
        }
    }
}
