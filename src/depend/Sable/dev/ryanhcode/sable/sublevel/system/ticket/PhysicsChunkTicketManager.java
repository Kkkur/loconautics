/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.core.SectionPos
 *  net.minecraft.server.level.DistanceManager
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.sublevel.system.ticket;

import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.physics.object.ArbitraryPhysicsObject;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunkMap;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.ryanhcode.sable.sublevel.system.ticket.PhysicsChunkTicket;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class PhysicsChunkTicketManager {
    public static final double MAX_PREDICTION_DISTANCE = 20.0;
    private final Map<SectionPos, PhysicsChunkTicket> physicsChunks = new Object2ObjectOpenHashMap();

    public void update(ServerLevel level, ServerSubLevelContainer container, SubLevelPhysicsSystem system, PhysicsPipeline pipeline, double timeStep) {
        SubLevelHoldingChunkMap holdingChunkMap = container.getHoldingChunkMap();
        long gameTime = level.getGameTime();
        Iterator<Map.Entry<SectionPos, PhysicsChunkTicket>> chunkIter = this.physicsChunks.entrySet().iterator();
        while (chunkIter.hasNext()) {
            boolean noLongerExistent;
            Map.Entry<SectionPos, PhysicsChunkTicket> entry = chunkIter.next();
            SectionPos sectionPos = entry.getKey();
            PhysicsChunkTicket ticket = entry.getValue();
            LevelPlot plot = SubLevelContainer.getContainer(level).getPlot(sectionPos.chunk());
            boolean outdated = ticket.lastInhabitedTick() < gameTime - 20L && plot == null;
            boolean bl = noLongerExistent = !PhysicsChunkTicketManager.isChunkLoadedEnough(level, sectionPos.x(), sectionPos.z());
            if (!outdated && !noLongerExistent) continue;
            pipeline.handleChunkSectionRemoval(sectionPos.x(), sectionPos.y(), sectionPos.z());
            chunkIter.remove();
        }
        LongOpenHashSet unloadedChunks = new LongOpenHashSet();
        BoundingBox3d b = new BoundingBox3d();
        BoundingBox3d b2 = new BoundingBox3d();
        Vector3d velocity = new Vector3d();
        Iterator<ArbitraryPhysicsObject> objectIter = system.getArbitraryObjects().iterator();
        block1: while (objectIter.hasNext()) {
            int z;
            int x;
            ArbitraryPhysicsObject arbitraryObject = objectIter.next();
            arbitraryObject.getBoundingBox(b);
            b.expand(1.0, b);
            BoundingBox3i chunkBounds = new BoundingBox3i(Mth.floor((double)b.minX()) >> 4, Mth.floor((double)b.minY()) >> 4, Mth.floor((double)b.minZ()) >> 4, Mth.floor((double)b.maxX()) >> 4, Mth.floor((double)b.maxY()) >> 4, Mth.floor((double)b.maxZ()) >> 4);
            for (x = chunkBounds.minX(); x <= chunkBounds.maxX(); ++x) {
                for (z = chunkBounds.minZ(); z <= chunkBounds.maxZ(); ++z) {
                    long l = ChunkPos.asLong((int)x, (int)z);
                    if (PhysicsChunkTicketManager.isChunkLoadedEnough(level, x, z) && !unloadedChunks.contains(l)) continue;
                    arbitraryObject.onUnloaded(holdingChunkMap, new ChunkPos(x, z));
                    unloadedChunks.add(l);
                    objectIter.remove();
                    continue block1;
                }
            }
            for (x = chunkBounds.minX(); x <= chunkBounds.maxX(); ++x) {
                for (z = chunkBounds.minZ(); z <= chunkBounds.maxZ(); ++z) {
                    for (int y = chunkBounds.minY(); y <= chunkBounds.maxY(); ++y) {
                        SectionPos sectionPos = SectionPos.of((int)x, (int)y, (int)z);
                        int index = level.getSectionIndexFromSectionY(y);
                        if (index < 0 || index >= level.getSectionsCount()) continue;
                        this.addTicket((Level)level, pipeline, sectionPos, x, y, z, index, gameTime);
                    }
                }
            }
        }
        block7: for (int i = 0; i < container.getAllSubLevels().size(); ++i) {
            int x;
            ServerSubLevel subLevel = container.getAllSubLevels().get(i);
            if (subLevel.isRemoved()) continue;
            b.set(subLevel.boundingBox());
            b2.set((BoundingBox3dc)b);
            if (subLevel.lastPose().position().distanceSquared((Vector3dc)subLevel.logicalPose().position()) > 0.0025000000000000005) {
                system.getPipeline().getLinearVelocity(subLevel, velocity.zero()).mul(timeStep);
                b2.move(0.0, Mth.clamp((double)velocity.y, (double)-20.0, (double)20.0), 0.0);
                b.expandTo((BoundingBox3dc)b2);
            }
            b.expand(1.0, b);
            BoundingBox3i chunkBounds = b.chunkBoundsFrom();
            for (x = chunkBounds.minX(); x <= chunkBounds.maxX(); ++x) {
                for (int z = chunkBounds.minZ(); z <= chunkBounds.maxZ(); ++z) {
                    long l = ChunkPos.asLong((int)x, (int)z);
                    if (PhysicsChunkTicketManager.isChunkLoadedEnough(level, x, z) && !unloadedChunks.contains(l)) continue;
                    unloadedChunks.add(l);
                    holdingChunkMap.moveToUnloaded(subLevel, new ChunkPos(x, z));
                    --i;
                    continue block7;
                }
            }
            for (x = chunkBounds.minX(); x <= chunkBounds.maxX(); ++x) {
                for (int z = chunkBounds.minZ(); z <= chunkBounds.maxZ(); ++z) {
                    for (int y = chunkBounds.minY(); y <= chunkBounds.maxY(); ++y) {
                        SectionPos sectionPos = SectionPos.of((int)x, (int)y, (int)z);
                        int index = level.getSectionIndexFromSectionY(y);
                        if (index < 0 || index >= level.getSectionsCount()) continue;
                        PhysicsChunkTicket physicsChunkTicket = this.addTicket((Level)level, pipeline, sectionPos, x, y, z, index, gameTime);
                    }
                }
            }
        }
    }

    @NotNull
    private PhysicsChunkTicket addTicket(Level level, PhysicsPipeline pipeline, SectionPos sectionPos, int x, int y, int z, int index, long gameTime) {
        PhysicsChunkTicket existingTicket = this.physicsChunks.get(sectionPos);
        if (existingTicket == null) {
            LevelChunk chunk = level.getChunk(x, z);
            pipeline.handleChunkSectionAddition(chunk.getSection(index), x, y, z, false);
            Collection<SubLevel> residents = null;
            PhysicsChunkTicket newTicket = new PhysicsChunkTicket(sectionPos, gameTime, residents);
            this.physicsChunks.put(sectionPos, newTicket);
            existingTicket = newTicket;
        }
        existingTicket.setLastInhabitedTick(gameTime);
        return existingTicket;
    }

    public void addSectionIfNotTracked(ServerLevel level, LevelChunkSection section, SectionPos sectionPos, PhysicsPipeline pipeline) {
        if (!this.physicsChunks.containsKey(sectionPos)) {
            pipeline.handleChunkSectionAddition(section, sectionPos.x(), sectionPos.y(), sectionPos.z(), false);
            PhysicsChunkTicket ticket = new PhysicsChunkTicket(sectionPos, level.getGameTime(), null);
            this.physicsChunks.put(sectionPos, ticket);
        }
    }

    public void addTicketForSection(ServerLevel level, SectionPos sectionPos) {
        PhysicsChunkTicket ticket = new PhysicsChunkTicket(sectionPos, level.getGameTime(), null);
        this.physicsChunks.put(sectionPos, ticket);
    }

    public Iterable<SubLevel> queryIntersecting(BoundingBox3dc bounds) {
        throw new IllegalStateException("Cannot query intersecting sub-levels when tickets are not used for queries.");
    }

    public boolean wouldBeLoaded(Level level, ArbitraryPhysicsObject object) {
        BoundingBox3d b = new BoundingBox3d();
        object.getBoundingBox(b);
        b.expand(1.0, b);
        BoundingBox3i chunkBounds = b.chunkBoundsFrom();
        for (int x = chunkBounds.minX(); x <= chunkBounds.maxX(); ++x) {
            for (int z = chunkBounds.minZ(); z <= chunkBounds.maxZ(); ++z) {
                if (PhysicsChunkTicketManager.isChunkLoadedEnough((ServerLevel)level, x, z)) continue;
                return false;
            }
        }
        return true;
    }

    public static boolean isChunkLoadedEnough(ServerLevel level, int x, int z) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container != null && container.inBounds(x, z)) {
            return true;
        }
        DistanceManager distanceManager = level.getChunkSource().chunkMap.getDistanceManager();
        return distanceManager.inBlockTickingRange(ChunkPos.asLong((int)x, (int)z));
    }
}
