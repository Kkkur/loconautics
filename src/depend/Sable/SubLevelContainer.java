/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.chunk.LevelChunk
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector2i
 */
package dev.ryanhcode.sable.api.sublevel;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelObserver;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.mixinterface.plot.SubLevelContainerHolder;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder;
import dev.ryanhcode.sable.sublevel.storage.SubLevelOccupancySavedData;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import dev.ryanhcode.sable.util.iterator.ListBackedFilterIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

public abstract class SubLevelContainer {
    public static int DEFAULT_LOG_SIZE_LENGTH = 7;
    public static int DEFAULT_LOG_PLOT_SIZE = 7;
    public static final int DEFAULT_ORIGIN = 10000;
    protected final SubLevel[] subLevels;
    private final List<SubLevel> allSubLevels = new ObjectArrayList();
    private final Map<UUID, SubLevel> subLevelsByUUID = new HashMap<UUID, SubLevel>();
    private final BitSet occupancy;
    private final List<SubLevelObserver> observers = new ObjectArrayList();
    private final Level level;
    private final int logSideLength;
    private final int logPlotSize;
    private final int originX;
    private final int originZ;

    @Nullable
    public static SubLevelContainer getContainer(Level level) {
        if (level instanceof SubLevelContainerHolder) {
            SubLevelContainerHolder holder = (SubLevelContainerHolder)level;
            return holder.sable$getPlotContainer();
        }
        return null;
    }

    @Nullable
    public static ServerSubLevelContainer getContainer(ServerLevel level) {
        if (level instanceof SubLevelContainerHolder) {
            SubLevelContainerHolder holder = (SubLevelContainerHolder)level;
            return (ServerSubLevelContainer)holder.sable$getPlotContainer();
        }
        return null;
    }

    @Nullable
    public static ClientSubLevelContainer getContainer(ClientLevel level) {
        if (level instanceof SubLevelContainerHolder) {
            SubLevelContainerHolder holder = (SubLevelContainerHolder)level;
            return (ClientSubLevelContainer)holder.sable$getPlotContainer();
        }
        return null;
    }

    public SubLevelContainer(Level level, int logSideLength, int logPlotSize, int originX, int originZ) {
        this.level = level;
        this.logSideLength = logSideLength;
        this.logPlotSize = logPlotSize;
        this.originX = originX;
        this.originZ = originZ;
        this.subLevels = new SubLevel[(1 << logSideLength) * (1 << logSideLength)];
        this.occupancy = new BitSet(this.subLevels.length);
    }

    public void tick() {
        this.allSubLevels.forEach(SubLevel::tick);
        this.processSubLevelRemovals();
        this.observers.forEach(observer -> observer.tick(this));
    }

    public void processSubLevelRemovals() {
        for (SubLevel subLevel : this.allSubLevels) {
            ServerSubLevel serverSubLevel;
            if (subLevel instanceof ServerSubLevel && !(serverSubLevel = (ServerSubLevel)subLevel).isRemoved() && serverSubLevel.getMassTracker().isInvalid()) {
                serverSubLevel.getPlot().destroyAllBlocks();
                serverSubLevel.markRemoved();
            }
            if (!subLevel.isRemoved()) continue;
            LevelPlot plot = subLevel.getPlot();
            ChunkPos plotPos = plot.plotPos;
            this.removeSubLevel(plotPos.x - this.originX, plotPos.z - this.originZ, SubLevelRemovalReason.REMOVED);
        }
    }

    public void addObserver(SubLevelObserver observer) {
        this.observers.add(observer);
    }

    private Vector2i getFirstEmptyPlot() {
        for (int x = 0; x < 1 << this.logSideLength; ++x) {
            for (int z = 0; z < 1 << this.logSideLength; ++z) {
                if (this.occupancy.get(this.getIndex(x, z))) continue;
                return new Vector2i(x, z);
            }
        }
        return null;
    }

    @ApiStatus.Internal
    public int getIndex(int x, int z) {
        return x + (z << this.logSideLength);
    }

    @Nullable
    private LevelPlot getLocalPlot(int x, int z) {
        if (x < 0 || x >= 1 << this.logSideLength || z < 0 || z >= 1 << this.logSideLength) {
            return null;
        }
        SubLevel subLevel = this.subLevels[this.getIndex(x, z)];
        if (subLevel == null) {
            return null;
        }
        return subLevel.getPlot();
    }

    @Nullable
    public SubLevel getSubLevel(int x, int z) {
        if (x < 0 || x >= 1 << this.logSideLength || z < 0 || z >= 1 << this.logSideLength) {
            return null;
        }
        return this.subLevels[this.getIndex(x, z)];
    }

    public SubLevel allocateNewSubLevel(Pose3d pose) {
        Vector2i firstEmptyPlot = this.getFirstEmptyPlot();
        if (firstEmptyPlot == null) {
            throw new IllegalStateException("No empty plots left in the plotgrid");
        }
        return this.allocateSubLevel(UUID.randomUUID(), firstEmptyPlot.x, firstEmptyPlot.y, pose);
    }

    public SubLevel allocateSubLevel(UUID uuid, int x, int z, Pose3d pose) {
        if (this.getLocalPlot(x, z) != null) {
            throw new IllegalArgumentException("Plot already exists at " + x + ", " + z);
        }
        if (x < 0 || x >= 1 << this.logSideLength || z < 0 || z >= 1 << this.logSideLength) {
            throw new IllegalArgumentException("Plot coordinates out of bounds: " + x + ", " + z);
        }
        SubLevel subLevel = this.createSubLevel(x + this.originX, z + this.originZ, pose, uuid);
        int index = this.getIndex(x, z);
        this.subLevels[index] = subLevel;
        this.getOccupancy().set(index);
        this.allSubLevels.add(subLevel);
        this.subLevelsByUUID.put(subLevel.getUniqueId(), subLevel);
        this.observers.forEach(observer -> observer.onSubLevelAdded(subLevel));
        Level level = this.level;
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            SubLevelOccupancySavedData.getOrLoad(serverLevel).setDirty();
        }
        return subLevel;
    }

    protected abstract SubLevel createSubLevel(int var1, int var2, Pose3d var3, UUID var4);

    @Nullable
    public LevelChunk getChunk(ChunkPos pos) {
        if (!this.inBounds(pos)) {
            return null;
        }
        LevelPlot plot = this.getPlot(pos);
        if (plot == null) {
            return null;
        }
        ChunkPos local = plot.toLocal(pos);
        return plot.getChunk(local);
    }

    @Nullable
    public PlotChunkHolder getChunkHolder(ChunkPos pos) {
        if (!this.inBounds(pos)) {
            return null;
        }
        LevelPlot plot = this.getPlot(pos);
        if (plot == null) {
            return null;
        }
        ChunkPos local = plot.toLocal(pos);
        return plot.getChunkHolder(local);
    }

    @Nullable
    public LevelPlot getPlot(int chunkX, int chunkZ) {
        int plotX = (chunkX >> this.logPlotSize) - this.originX;
        int plotZ = (chunkZ >> this.logPlotSize) - this.originZ;
        return this.getLocalPlot(plotX, plotZ);
    }

    @Nullable
    public LevelPlot getPlot(ChunkPos pos) {
        int plotX = (pos.x >> this.logPlotSize) - this.originX;
        int plotZ = (pos.z >> this.logPlotSize) - this.originZ;
        return this.getLocalPlot(plotX, plotZ);
    }

    public boolean inBounds(ChunkPos pos) {
        return this.inBounds(pos.x, pos.z);
    }

    public boolean inBounds(BlockPos pos) {
        return this.inBounds(pos.getX() >> 4, pos.getZ() >> 4);
    }

    public boolean inBounds(int x, int z) {
        int plotX = (x >> this.logPlotSize) - this.originX;
        int plotZ = (z >> this.logPlotSize) - this.originZ;
        int sideLength = 1 << this.logSideLength;
        return plotX >= 0 && plotX < sideLength && plotZ >= 0 && plotZ < sideLength;
    }

    public void newPopulatedChunk(ChunkPos pos, LevelChunk chunk) {
        if (!this.inBounds(pos)) {
            return;
        }
        int plotX = (pos.x >> this.logPlotSize) - this.originX;
        int plotZ = (pos.z >> this.logPlotSize) - this.originZ;
        LevelPlot plot = this.getLocalPlot(plotX, plotZ);
        if (plot == null) {
            Sable.LOGGER.error("Cannot add chunk at {}, {} in nonexistent sub-level plot", (Object)plotX, (Object)plotZ);
            return;
        }
        ChunkPos local = plot.toLocal(pos);
        if (plot.getChunkHolder(local) != null) {
            throw new IllegalStateException("Chunk already exists at " + String.valueOf(pos));
        }
        PlotChunkHolder holder = PlotChunkHolder.create(chunk.getLevel(), pos, plot.getLightEngine(), chunk);
        plot.addChunkHolder(local, holder, false);
    }

    public List<ServerPlayer> getPlayersTracking(ChunkPos chunkPos) {
        LevelPlot plot = this.getPlot(chunkPos);
        if (plot == null) {
            return List.of();
        }
        SubLevel subLevel = plot.getSubLevel();
        if (subLevel instanceof ServerSubLevel) {
            ServerSubLevel serverSubLevel = (ServerSubLevel)subLevel;
            Collection<UUID> trackingPlayers = serverSubLevel.getTrackingPlayers();
            ObjectArrayList players = new ObjectArrayList(trackingPlayers.size());
            for (UUID uuid : serverSubLevel.getTrackingPlayers()) {
                ServerPlayer player = this.level.getServer().getPlayerList().getPlayer(uuid);
                if (player == null) continue;
                players.add((Object)player);
            }
            return players;
        }
        return List.of();
    }

    public List<? extends SubLevel> getAllSubLevels() {
        return this.allSubLevels;
    }

    public Level getLevel() {
        return this.level;
    }

    public int getLogPlotSize() {
        return this.logPlotSize;
    }

    public int getLogSideLength() {
        return this.logSideLength;
    }

    public Vector2i getOrigin() {
        return new Vector2i(this.originX, this.originZ);
    }

    public void removeSubLevel(int x, int z, SubLevelRemovalReason reason) {
        SubLevel subLevel = this.getSubLevel(x, z);
        if (subLevel == null) {
            throw new IllegalStateException("No sub-level at " + x + ", " + z);
        }
        this.observers.forEach(observer -> observer.onSubLevelRemoved(subLevel, reason));
        subLevel.onRemove();
        int index = this.getIndex(x, z);
        this.subLevels[index] = null;
        this.allSubLevels.remove(subLevel);
        this.subLevelsByUUID.remove(subLevel.getUniqueId());
        if (reason == SubLevelRemovalReason.REMOVED) {
            this.getOccupancy().clear(index);
        }
    }

    public int getLoadedCount() {
        return this.allSubLevels.size();
    }

    public Iterable<SubLevel> queryIntersecting(BoundingBox3dc bounds) {
        return () -> new ListBackedFilterIterator<SubLevel>(subLevel -> subLevel.boundingBox().intersects(bounds), this.allSubLevels);
    }

    public void removeSubLevel(SubLevel subLevel, SubLevelRemovalReason reason) {
        int x = subLevel.getPlot().plotPos.x - this.originX;
        int z = subLevel.getPlot().plotPos.z - this.originZ;
        this.removeSubLevel(x, z, reason);
    }

    @Nullable
    public SubLevel getSubLevel(UUID uuid) {
        return this.subLevelsByUUID.get(uuid);
    }

    @ApiStatus.Internal
    public BitSet getOccupancy() {
        return this.occupancy;
    }
}
