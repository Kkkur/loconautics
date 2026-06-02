/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.sublevel.storage.holding;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.SubLevelHelper;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.mixinterface.toast.SableToastableServer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.storage.HoldingSubLevel;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import dev.ryanhcode.sable.sublevel.storage.holding.GlobalSavedSubLevelPointer;
import dev.ryanhcode.sable.sublevel.storage.holding.SavedSubLevelPointer;
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunk;
import dev.ryanhcode.sable.sublevel.storage.serialization.SubLevelData;
import dev.ryanhcode.sable.sublevel.storage.serialization.SubLevelSerializer;
import dev.ryanhcode.sable.sublevel.storage.serialization.SubLevelStorage;
import dev.ryanhcode.sable.sublevel.tracking_points.SubLevelTrackingPointSavedData;
import dev.ryanhcode.sable.sublevel.tracking_points.TrackingPoint;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SubLevelHoldingChunkMap
implements AutoCloseable {
    public static boolean VERBOSE = false;
    private final ServerLevel level;
    private final ServerSubLevelContainer container;
    private final SubLevelStorage storage;
    private final Object2ObjectMap<UUID, HoldingSubLevel> allHoldingSubLevels = new Object2ObjectOpenHashMap();
    private final Long2ObjectMap<SubLevelHoldingChunk> loadedHoldingChunks = new Long2ObjectOpenHashMap();
    private final LongSet dirtyHoldingChunks = new LongOpenHashSet();
    private final ObjectSet<ChunkPos> queuedUnloads = new ObjectOpenHashSet();
    private final ObjectSet<GlobalSavedSubLevelPointer> queuedDeletion = new ObjectOpenHashSet();
    private final LongSet chunksToUnload = new LongOpenHashSet();
    private final LongSet chunksToLoad = new LongOpenHashSet();

    public SubLevelHoldingChunkMap(ServerLevel level, ServerSubLevelContainer container) {
        this.level = level;
        this.container = container;
        File worldFolder = level.getChunkSource().getDataStorage().dataFolder.getParentFile();
        File subLevelsFolder = new File(worldFolder, "sublevels");
        subLevelsFolder.mkdirs();
        this.storage = new SubLevelStorage(subLevelsFolder.toPath());
    }

    public void updateChunkStatus(ChunkPos chunkPos, boolean loaded) {
        long key = chunkPos.toLong();
        if (!loaded) {
            this.chunksToUnload.add(key);
            this.chunksToLoad.remove(key);
        } else {
            this.chunksToLoad.add(key);
            this.chunksToUnload.remove(key);
        }
    }

    private void processLoad(ChunkPos chunkPos) {
        if (VERBOSE) {
            Sable.LOGGER.info("Processing load of chunk at {}", (Object)chunkPos);
        }
        if (this.queuedUnloads.contains((Object)chunkPos)) {
            if (VERBOSE) {
                Sable.LOGGER.info("Removing chunk at {} from queued unloads", (Object)chunkPos);
            }
            this.queuedUnloads.remove((Object)chunkPos);
        }
        if (this.loadedHoldingChunks.containsKey(chunkPos.toLong())) {
            return;
        }
        this.getOrLoadHoldingChunk(chunkPos, false);
    }

    private void processUnload(ChunkPos chunkPos) {
        if (!this.loadedHoldingChunks.containsKey(chunkPos.toLong())) {
            return;
        }
        if (VERBOSE) {
            Sable.LOGGER.info("Processing unload for chunk {}", (Object)chunkPos);
        }
        BoundingBox3d bounds = new BoundingBox3d((double)(chunkPos.x << 4), -1.7976931348623157E308, (double)(chunkPos.z << 4), (double)((chunkPos.x << 4) + 16), Double.MAX_VALUE, (double)((chunkPos.z << 4) + 16));
        ServerSubLevelContainer container = SubLevelContainer.getContainer(this.level);
        assert (container != null) : "Sub-level container is null";
        Iterable<SubLevel> toUnloadIterator = container.queryIntersecting((BoundingBox3dc)bounds);
        ObjectOpenHashSet toUnload = new ObjectOpenHashSet();
        for (SubLevel subLevel : toUnloadIterator) {
            toUnload.add((Object)subLevel);
        }
        if (VERBOSE) {
            Sable.LOGGER.info("Adding chunk {} to queued unloads", (Object)chunkPos);
        }
        this.queuedUnloads.add((Object)chunkPos);
        if (toUnload.isEmpty()) {
            return;
        }
        SubLevelHoldingChunk holdingChunk = this.getOrLoadHoldingChunk(chunkPos, true);
        ObjectOpenHashSet visited = new ObjectOpenHashSet();
        for (SubLevel subLevel : toUnload) {
            if (visited.contains((Object)subLevel)) continue;
            ServerSubLevel serverSubLevel = (ServerSubLevel)subLevel;
            Collection<ServerSubLevel> chain = SubLevelHelper.getLoadingDependencyChain(serverSubLevel);
            visited.addAll(chain);
            List<UUID> uuids = chain.stream().map(SubLevel::getUniqueId).toList();
            for (ServerSubLevel chainedSubLevel : chain) {
                GlobalSavedSubLevelPointer pointer = chainedSubLevel.getLastSerializationPointer();
                if (VERBOSE) {
                    Sable.LOGGER.info("Unloading sub-level {} with pointer {} to chunk {} as holding sub-level", new Object[]{chainedSubLevel, pointer, chunkPos});
                }
                SubLevelData data = SubLevelSerializer.toData(chainedSubLevel, uuids);
                HoldingSubLevel holdingSubLevel = new HoldingSubLevel(data, pointer);
                holdingChunk.acceptHoldingSubLevel(holdingSubLevel);
                this.allHoldingSubLevels.put((Object)holdingSubLevel.data().uuid(), (Object)holdingSubLevel);
                container.removeSubLevel(chainedSubLevel, SubLevelRemovalReason.UNLOADED);
            }
        }
    }

    public void saveAll() {
        if (VERBOSE) {
            Sable.LOGGER.info("Saving holding chunk-map");
        }
        this.processChanges();
        for (GlobalSavedSubLevelPointer deletion : this.queuedDeletion) {
            if (VERBOSE) {
                Sable.LOGGER.info("Processing queued deletion & clearing data for {}", (Object)deletion);
            }
            this.storage.attemptSaveSubLevel(deletion, null);
        }
        this.queuedDeletion.clear();
        List<ServerSubLevel> subLevels = this.container.getAllSubLevels();
        ObjectArrayList toMove = new ObjectArrayList(subLevels);
        ObjectArraySet moved = new ObjectArraySet(toMove.size());
        for (ServerSubLevel subLevel : toMove) {
            if (moved.contains(subLevel)) continue;
            Vector3d currentPosition = subLevel.logicalPose().position();
            ChunkPos moveToChunk = new ChunkPos(BlockPos.containing((double)currentPosition.x, (double)currentPosition.y, (double)currentPosition.z));
            Collection<ServerSubLevel> chain = SubLevelHelper.getLoadingDependencyChain(subLevel);
            moved.addAll(chain);
            List<UUID> uuids = chain.stream().map(SubLevel::getUniqueId).toList();
            for (ServerSubLevel chainedSubLevel : chain) {
                if (VERBOSE) {
                    Sable.LOGGER.info("Moving sub-level {} with last pointer {}", (Object)chainedSubLevel, (Object)chainedSubLevel.getLastSerializationPointer());
                }
                this.moveAndSaveSubLevel(chainedSubLevel, moveToChunk, uuids);
            }
        }
        for (SubLevelHoldingChunk holdingChunk : this.loadedHoldingChunks.values()) {
            ChunkPos holdingChunkPos = holdingChunk.getChunkPos();
            for (HoldingSubLevel holdingSubLevel : holdingChunk.getLoadedHoldingSubLevels()) {
                if (VERBOSE) {
                    Sable.LOGGER.info("Processing holding sub-level {} stored in chunk {} with pointer {}", new Object[]{holdingSubLevel, holdingChunkPos, holdingSubLevel.pointer()});
                }
                if (holdingSubLevel.pointer() == null || !Objects.equals(holdingSubLevel.pointer().chunkPos(), holdingChunkPos)) {
                    if (VERBOSE) {
                        Sable.LOGGER.info("Chunk position of holding chunk and pointer mis-match. Moving");
                    }
                    GlobalSavedSubLevelPointer newPointer = this.moveAndSaveSubLevel(null, holdingSubLevel.data(), holdingSubLevel.pointer(), holdingChunkPos);
                    holdingSubLevel.setPointer(newPointer);
                    continue;
                }
                this.storage.attemptSaveSubLevel(holdingSubLevel.pointer(), holdingSubLevel.data());
            }
        }
        for (ChunkPos unload : this.queuedUnloads) {
            SubLevelHoldingChunk holdingChunk = (SubLevelHoldingChunk)this.loadedHoldingChunks.get(unload.toLong());
            if (VERBOSE) {
                Sable.LOGGER.info("Processing queued unload for chunk {} at position {}", (Object)holdingChunk, (Object)(holdingChunk != null ? holdingChunk.getChunkPos() : null));
            }
            if (holdingChunk == null) continue;
            for (HoldingSubLevel holdingSubLevel : holdingChunk.getLoadedHoldingSubLevels()) {
                this.allHoldingSubLevels.remove((Object)holdingSubLevel.data().uuid());
            }
            this.setDirty(unload);
        }
        ObjectIterator objectIterator = this.dirtyHoldingChunks.iterator();
        while (objectIterator.hasNext()) {
            long longKey = (Long)objectIterator.next();
            ChunkPos chunkPos = new ChunkPos(longKey);
            SubLevelHoldingChunk holdingChunk = (SubLevelHoldingChunk)this.loadedHoldingChunks.get(longKey);
            if (VERBOSE) {
                Sable.LOGGER.info("Saving holding chunk {} at {}", (Object)holdingChunk, (Object)chunkPos);
            }
            if (holdingChunk == null) continue;
            this.storage.attemptSaveHoldingChunk(chunkPos, holdingChunk);
        }
        for (ChunkPos unload : this.queuedUnloads) {
            this.loadedHoldingChunks.remove(unload.toLong());
        }
        this.queuedUnloads.clear();
        try {
            if (VERBOSE) {
                Sable.LOGGER.info("Flushing storage");
            }
            this.storage.flush();
        }
        catch (IOException e) {
            Sable.LOGGER.error("Failed to flush sub-level storage to disk", (Throwable)e);
        }
    }

    private void moveAndSaveSubLevel(ServerSubLevel subLevel, ChunkPos moveToChunk, List<UUID> uuids) {
        GlobalSavedSubLevelPointer lastPointer = subLevel.getLastSerializationPointer();
        SubLevelData data = SubLevelSerializer.toData(subLevel, uuids);
        subLevel.setLastSerializationPointer(this.moveAndSaveSubLevel(subLevel, data, lastPointer, moveToChunk));
        if (VERBOSE) {
            Sable.LOGGER.info("Moved sub-level {}. {} -> {}", new Object[]{subLevel, lastPointer, subLevel.getLastSerializationPointer()});
        }
    }

    private GlobalSavedSubLevelPointer moveAndSaveSubLevel(@Nullable ServerSubLevel subLevel, SubLevelData data, GlobalSavedSubLevelPointer lastPointer, ChunkPos moveToChunk) {
        GlobalSavedSubLevelPointer newPointer;
        ChunkPos oldChunkPos;
        ChunkPos chunkPos = oldChunkPos = lastPointer != null ? lastPointer.chunkPos() : null;
        if (Objects.equals(oldChunkPos, moveToChunk)) {
            if (this.getOrLoadHoldingChunk(moveToChunk, false) == null) {
                throw new IllegalStateException("this shouldn't be possible");
            }
            if (VERBOSE) {
                Sable.LOGGER.info("Old chunk is the same as the new chunk position ({}, {})", (Object)oldChunkPos, (Object)moveToChunk);
                Sable.LOGGER.info("Saving sub-level data to {}", (Object)lastPointer);
            }
            this.storage.attemptSaveSubLevel(lastPointer, data);
            this.setDirty(moveToChunk);
            return lastPointer;
        }
        if (VERBOSE) {
            Sable.LOGGER.info("Saving sub-level data to storage in new chunk, {}", (Object)moveToChunk);
        }
        if ((newPointer = this.storage.attemptSaveSubLevel(moveToChunk, data)) == null) {
            MinecraftServer server = this.level.getServer();
            if (server instanceof SableToastableServer) {
                SableToastableServer toastable = (SableToastableServer)server;
                toastable.sable$reportSubLevelSaveFailure(data);
            }
            return null;
        }
        if (VERBOSE) {
            Sable.LOGGER.info("New pointer {}", (Object)newPointer);
        }
        SubLevelTrackingPointSavedData trackingPoints = SubLevelTrackingPointSavedData.getOrLoad(this.level);
        for (Map.Entry<UUID, TrackingPoint> entry : trackingPoints.getAllTrackingPoints()) {
            boolean pointerInSubLevel;
            TrackingPoint point = entry.getValue();
            if (!point.inSubLevel()) continue;
            boolean movingPointers = point.lastSavedSubLevelPointer() != null && point.lastSavedSubLevelPointer().equals(lastPointer);
            boolean bl = pointerInSubLevel = subLevel != null && Sable.HELPER.getContaining((Level)this.level, (Vector3dc)point.point()) == subLevel;
            if (!movingPointers && !pointerInSubLevel) continue;
            trackingPoints.setTrackingPoint(entry.getKey(), new TrackingPoint(true, point.subLevelID(), newPointer, point.point(), null));
        }
        if (VERBOSE) {
            Sable.LOGGER.info("Clearing last pointer (if exists) {}", (Object)lastPointer);
        }
        if (lastPointer != null) {
            this.storage.attemptSaveSubLevel(lastPointer, null);
        }
        if (oldChunkPos != null) {
            SavedSubLevelPointer localPointer = lastPointer.local();
            SubLevelHoldingChunk oldHoldingChunk = this.getOrLoadHoldingChunk(oldChunkPos, false);
            if (VERBOSE) {
                Sable.LOGGER.info("Removing pointer from last holding chunk {}", (Object)oldHoldingChunk);
            }
            if (oldHoldingChunk != null) {
                oldHoldingChunk.getSubLevelPointers().remove(localPointer);
                this.setDirty(oldChunkPos);
            } else if (VERBOSE) {
                Sable.LOGGER.info("Old holding chunk doesn't exist at {}! This may be a problem", (Object)oldChunkPos);
            }
        }
        SubLevelHoldingChunk newHoldingChunk = this.getOrLoadHoldingChunk(moveToChunk, true);
        if (VERBOSE) {
            Sable.LOGGER.info("Adding pointer to new holding chunk.");
        }
        SavedSubLevelPointer newLocalPointer = newPointer.local();
        newHoldingChunk.getSubLevelPointers().add(newLocalPointer);
        this.setDirty(moveToChunk);
        return newPointer;
    }

    @Contract(value="_, true -> !null")
    @Nullable
    private SubLevelHoldingChunk getOrLoadHoldingChunk(ChunkPos chunkPos, boolean create) {
        long longKey = chunkPos.toLong();
        SubLevelHoldingChunk holdingChunk = (SubLevelHoldingChunk)this.loadedHoldingChunks.get(longKey);
        if (holdingChunk != null) {
            return holdingChunk;
        }
        SubLevelHoldingChunk loadedChunk = this.storage.attemptLoadHoldingChunk(chunkPos);
        if (loadedChunk != null) {
            if (VERBOSE) {
                Sable.LOGGER.info("Loaded chunk at {} from disk", (Object)chunkPos);
            }
            List<SavedSubLevelPointer> pointerQueue = loadedChunk.getSubLevelPointers();
            for (SavedSubLevelPointer pointer : pointerQueue) {
                SubLevelData subLevelData;
                if (VERBOSE) {
                    Sable.LOGGER.info("Attempting to read pointer at {} into sub-level data", (Object)pointer);
                }
                if ((subLevelData = this.storage.attemptLoadSubLevel(chunkPos, pointer)) == null) {
                    Sable.LOGGER.error("Due to a failed storage sub-level data load, we can't add a holding sub-level for pointer {}. This will cause issues later down the line.", (Object)pointer);
                    continue;
                }
                GlobalSavedSubLevelPointer globalPointer = new GlobalSavedSubLevelPointer(chunkPos, pointer.storageIndex(), pointer.subLevelIndex());
                HoldingSubLevel holdingSubLevel = new HoldingSubLevel(subLevelData, globalPointer);
                loadedChunk.acceptHoldingSubLevel(holdingSubLevel);
                this.allHoldingSubLevels.put((Object)holdingSubLevel.data().uuid(), (Object)holdingSubLevel);
            }
            this.loadedHoldingChunks.put(longKey, (Object)loadedChunk);
            return loadedChunk;
        }
        if (create) {
            SubLevelHoldingChunk newHoldingChunk = new SubLevelHoldingChunk(chunkPos);
            this.loadedHoldingChunks.put(longKey, (Object)newHoldingChunk);
            return newHoldingChunk;
        }
        return null;
    }

    private void setDirty(ChunkPos chunkPos) {
        if (VERBOSE) {
            Sable.LOGGER.info("Setting chunk at {} as dirty", (Object)chunkPos);
        }
        this.dirtyHoldingChunks.add(chunkPos.toLong());
    }

    public void processChanges() {
        this.processUnloads();
        Object2ObjectOpenHashMap readySubLevels = new Object2ObjectOpenHashMap();
        for (SubLevelHoldingChunk chunk : this.loadedHoldingChunks.values()) {
            if (this.queuedUnloads.contains((Object)chunk.getChunkPos())) continue;
            chunk.collectReadySubLevels(this.level, (Object2ObjectMap<UUID, HoldingSubLevel>)readySubLevels);
        }
        for (HoldingSubLevel holdingSubLevel : readySubLevels.values()) {
            ServerSubLevel subLevel;
            if (VERBOSE) {
                Sable.LOGGER.info("Holding sub-level {} with pointer {} reportedly ready to load", (Object)holdingSubLevel, (Object)holdingSubLevel.pointer());
            }
            if ((subLevel = SubLevelSerializer.fullyLoad(this.level, holdingSubLevel.data())) != null) {
                subLevel.setLastSerializationPointer(holdingSubLevel.pointer());
            } else {
                MinecraftServer server = this.level.getServer();
                if (server instanceof SableToastableServer) {
                    SableToastableServer toastable = (SableToastableServer)server;
                    toastable.sable$reportSubLevelLoadFailure(holdingSubLevel.pointer());
                }
                Sable.LOGGER.info("Failed to load holding sub-level {} with pointer {}. This is a problem.", (Object)holdingSubLevel, (Object)holdingSubLevel.pointer());
            }
            this.allHoldingSubLevels.remove((Object)holdingSubLevel.data().uuid());
        }
    }

    @Nullable
    public HoldingSubLevel getHoldingSubLevel(UUID uuid) {
        return (HoldingSubLevel)this.allHoldingSubLevels.get((Object)uuid);
    }

    private void processUnloads() {
        long l;
        LongIterator longIterator = this.chunksToUnload.iterator();
        while (longIterator.hasNext()) {
            l = (Long)longIterator.next();
            this.processUnload(new ChunkPos(l));
        }
        longIterator = this.chunksToLoad.iterator();
        while (longIterator.hasNext()) {
            l = (Long)longIterator.next();
            this.processLoad(new ChunkPos(l));
        }
        this.chunksToUnload.clear();
        this.chunksToLoad.clear();
    }

    public void moveToUnloaded(ServerSubLevel subLevel, ChunkPos pos) {
        if (VERBOSE) {
            Sable.LOGGER.info("Sub-level {} with pointer {} detected unloaded chunk, moving to {}", new Object[]{subLevel, subLevel.getLastSerializationPointer(), pos});
        }
        Collection<ServerSubLevel> chain = SubLevelHelper.getLoadingDependencyChain(subLevel);
        List<UUID> uuids = chain.stream().map(SubLevel::getUniqueId).toList();
        SubLevelHoldingChunk holdingChunk = this.getOrLoadHoldingChunk(pos, true);
        for (ServerSubLevel chainSubLevel : chain) {
            SubLevelData data = SubLevelSerializer.toData(chainSubLevel, uuids);
            HoldingSubLevel holdingSubLevel = new HoldingSubLevel(data, chainSubLevel.getLastSerializationPointer());
            holdingChunk.acceptHoldingSubLevel(holdingSubLevel);
            this.allHoldingSubLevels.put((Object)holdingSubLevel.data().uuid(), (Object)holdingSubLevel);
            if (VERBOSE) {
                Sable.LOGGER.info("Added {} to holding chunk {}", (Object)chainSubLevel, (Object)holdingChunk);
            }
            this.container.removeSubLevel(chainSubLevel, SubLevelRemovalReason.UNLOADED);
        }
        this.setDirty(pos);
    }

    public void queueDeletion(ServerSubLevel subLevel) {
        GlobalSavedSubLevelPointer pointer = subLevel.getLastSerializationPointer();
        if (VERBOSE) {
            Sable.LOGGER.info("Queuing sub-level {} with pointer {} for deletion", (Object)subLevel, (Object)pointer);
        }
        if (pointer != null) {
            ChunkPos chunkPos = pointer.chunkPos();
            SubLevelHoldingChunk holdingChunk = this.getOrLoadHoldingChunk(chunkPos, false);
            if (holdingChunk != null) {
                holdingChunk.getSubLevelPointers().remove(pointer.local());
                this.setDirty(chunkPos);
            }
            this.queuedDeletion.add((Object)pointer);
        }
    }

    public SubLevelStorage getStorage() {
        return this.storage;
    }

    @Override
    public void close() throws Exception {
        this.storage.close();
    }
}
