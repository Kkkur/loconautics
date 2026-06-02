/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.ChunkPos
 */
package dev.ryanhcode.sable.sublevel.storage.holding;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.sublevel.storage.HoldingSubLevel;
import dev.ryanhcode.sable.sublevel.storage.holding.SavedSubLevelPointer;
import dev.ryanhcode.sable.sublevel.storage.serialization.SubLevelData;
import dev.ryanhcode.sable.sublevel.system.ticket.PhysicsChunkTicketManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;

public class SubLevelHoldingChunk {
    private final ObjectList<HoldingSubLevel> alsoLoad = new ObjectArrayList();
    private final ObjectList<SavedSubLevelPointer> pointers = new ObjectArrayList();
    private final Object2ObjectMap<UUID, HoldingSubLevel> loadedHoldingSubLevels = new Object2ObjectOpenHashMap();
    private final ChunkPos pos;
    final ObjectOpenHashSet<UUID> visitedSet = new ObjectOpenHashSet();

    public SubLevelHoldingChunk(ChunkPos pos) {
        this.pos = pos;
    }

    public void acceptHoldingSubLevel(HoldingSubLevel subLevelData) {
        this.loadedHoldingSubLevels.put((Object)subLevelData.data().uuid(), (Object)subLevelData);
    }

    public Iterable<HoldingSubLevel> getLoadedHoldingSubLevels() {
        return this.loadedHoldingSubLevels.values();
    }

    public void collectReadySubLevels(ServerLevel level, Object2ObjectMap<UUID, HoldingSubLevel> readySubLevels) {
        if (this.loadedHoldingSubLevels.isEmpty()) {
            return;
        }
        this.visitedSet.clear();
        ObjectIterator iter = this.loadedHoldingSubLevels.entrySet().iterator();
        block0: while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            if (this.visitedSet.contains(entry.getKey())) continue;
            HoldingSubLevel holdingSubLevel = (HoldingSubLevel)entry.getValue();
            SubLevelData data = holdingSubLevel.data();
            List<UUID> relations = data.dependencies();
            this.visitedSet.add((Object)((UUID)entry.getKey()));
            this.visitedSet.addAll(relations);
            for (UUID uuid : relations) {
                HoldingSubLevel dependencySubLevel = (HoldingSubLevel)this.loadedHoldingSubLevels.get((Object)uuid);
                if (dependencySubLevel == null) {
                    Sable.LOGGER.error("Sub-level dependency does not exist in chunk. Something has gone terribly wrong.");
                    iter.remove();
                    continue block0;
                }
                if (SubLevelHoldingChunk.canLoadSubLevel(level, dependencySubLevel.data())) continue;
                continue block0;
            }
            boolean allChunksLoaded = SubLevelHoldingChunk.canLoadSubLevel(level, data);
            if (!allChunksLoaded) continue;
            readySubLevels.put((Object)data.uuid(), (Object)holdingSubLevel);
            iter.remove();
            for (UUID uuid : relations) {
                HoldingSubLevel dependencySubLevel = (HoldingSubLevel)this.loadedHoldingSubLevels.get((Object)uuid);
                if (dependencySubLevel == null) continue;
                this.alsoLoad.add((Object)dependencySubLevel);
            }
        }
        for (HoldingSubLevel holdingSubLevel : this.alsoLoad) {
            UUID uuid = holdingSubLevel.data().uuid();
            this.loadedHoldingSubLevels.remove((Object)uuid);
            readySubLevels.put((Object)uuid, (Object)holdingSubLevel);
        }
        this.alsoLoad.clear();
    }

    private static boolean canLoadSubLevel(ServerLevel level, SubLevelData data) {
        BoundingBox3d bounds = data.bounds();
        BoundingBox3i chunkBounds = new BoundingBox3i(Mth.floor((double)(bounds.minX() - 1.0)) >> 4, Mth.floor((double)(bounds.minY() - 1.0)) >> 4, Mth.floor((double)(bounds.minZ() - 1.0)) >> 4, Mth.floor((double)(bounds.maxX() + 1.0)) >> 4, Mth.floor((double)(bounds.maxY() + 1.0)) >> 4, Mth.floor((double)(bounds.maxZ() + 1.0)) >> 4);
        boolean allChunksLoaded = true;
        block0: for (int x = chunkBounds.minX(); x <= chunkBounds.maxX(); ++x) {
            for (int z = chunkBounds.minZ(); z <= chunkBounds.maxZ(); ++z) {
                if (PhysicsChunkTicketManager.isChunkLoadedEnough(level, x, z)) continue;
                allChunksLoaded = false;
                break block0;
            }
        }
        return allChunksLoaded;
    }

    public static SubLevelHoldingChunk from(ChunkPos pos, CompoundTag tag) {
        SubLevelHoldingChunk chunk = new SubLevelHoldingChunk(pos);
        int[] pointer = tag.getIntArray("pointers");
        chunk.pointers.addAll(Arrays.stream(pointer).mapToObj(SavedSubLevelPointer::unpack).toList());
        return chunk;
    }

    public void writeTo(CompoundTag tag) {
        int[] pointers = this.pointers.stream().mapToInt(SavedSubLevelPointer::packed).toArray();
        tag.putIntArray("pointers", pointers);
    }

    public ChunkPos getChunkPos() {
        return this.pos;
    }

    public List<SavedSubLevelPointer> getSubLevelPointers() {
        return this.pointers;
    }
}
