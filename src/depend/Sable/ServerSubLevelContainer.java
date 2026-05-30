/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.biome.Biome
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 */
package dev.ryanhcode.sable.api.sublevel;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.storage.SubLevelOccupancySavedData;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunkMap;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.ryanhcode.sable.sublevel.system.SubLevelTrackingSystem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class ServerSubLevelContainer
extends SubLevelContainer {
    @Nullable
    private SubLevelPhysicsSystem physics;
    @Nullable
    private SubLevelTrackingSystem tracking;
    private SubLevelHoldingChunkMap holdingChunkMap;

    public ServerSubLevelContainer(Level level, int logSideLength, int logPlotSize, int originX, int originZ) {
        super(level, logSideLength, logPlotSize, originX, originZ);
    }

    public void initialize() {
        this.holdingChunkMap = new SubLevelHoldingChunkMap(this.getLevel(), this);
    }

    @Override
    public void tick() {
        super.tick();
        this.holdingChunkMap.processChanges();
    }

    @ApiStatus.Internal
    public void takePhysicsSystem(SubLevelPhysicsSystem physics) {
        this.physics = physics;
    }

    @ApiStatus.Internal
    public void takeTrackingSystem(SubLevelTrackingSystem tracking) {
        this.tracking = tracking;
    }

    @NotNull
    public SubLevelPhysicsSystem physicsSystem() {
        assert (this.physics != null);
        return this.physics;
    }

    @NotNull
    public SubLevelTrackingSystem trackingSystem() {
        assert (this.tracking != null);
        return this.tracking;
    }

    @Override
    public void removeSubLevel(int x, int z, SubLevelRemovalReason reason) {
        ServerSubLevel subLevel = (ServerSubLevel)this.getSubLevel(x, z);
        if (subLevel == null) {
            throw new IllegalStateException("No sub-level at " + x + ", " + z);
        }
        if (reason == SubLevelRemovalReason.REMOVED) {
            subLevel.deleteAllEntities();
        }
        super.removeSubLevel(x, z, reason);
        if (reason == SubLevelRemovalReason.REMOVED) {
            ServerLevel level = this.getLevel();
            SubLevelOccupancySavedData.getOrLoad(level).setDirty();
            this.holdingChunkMap.queueDeletion(subLevel);
        }
    }

    @Override
    protected SubLevel createSubLevel(int globalPlotX, int globalPlotZ, Pose3d pose, UUID uuid) {
        Holder holder;
        Optional key;
        ServerLevel level = this.getLevel();
        ServerSubLevel subLevel = new ServerSubLevel(level, globalPlotX, globalPlotZ, pose);
        subLevel.setUniqueId(uuid);
        Vector3d position = pose.position();
        BlockPos blockPos = BlockPos.containing((double)position.x, (double)position.y, (double)position.z);
        if (level.isLoaded(blockPos) && (key = (holder = level.getBiome(blockPos)).unwrapKey()).isPresent()) {
            subLevel.getPlot().setBiome((ResourceKey<Biome>)((ResourceKey)key.get()));
        }
        return subLevel;
    }

    public SubLevelHoldingChunkMap getHoldingChunkMap() {
        return this.holdingChunkMap;
    }

    public List<ServerSubLevel> getAllSubLevels() {
        return super.getAllSubLevels();
    }

    public ServerLevel getLevel() {
        return (ServerLevel)super.getLevel();
    }

    public void close() {
        try {
            this.holdingChunkMap.close();
        }
        catch (Exception e) {
            Sable.LOGGER.error("Failed closing sub-level holding chunk map", (Throwable)e);
        }
    }
}
