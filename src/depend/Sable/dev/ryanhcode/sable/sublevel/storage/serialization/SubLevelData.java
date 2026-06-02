/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.ChunkPos
 *  org.jetbrains.annotations.NotNull
 */
package dev.ryanhcode.sable.sublevel.storage.serialization;

import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.Pose3d;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.NotNull;

public final class SubLevelData {
    @NotNull
    private final UUID uuid;
    @NotNull
    private final BoundingBox3d bounds;
    @NotNull
    private final Pose3d pose;
    @NotNull
    private final List<UUID> relations;
    @NotNull
    private final CompoundTag fullTag;
    private ChunkPos originLoadedChunk = null;

    public SubLevelData(@NotNull UUID uuid, @NotNull BoundingBox3d bounds, @NotNull Pose3d pose, @NotNull List<UUID> relations, @NotNull CompoundTag fullTag) {
        this.uuid = uuid;
        this.bounds = bounds;
        this.pose = pose;
        this.relations = relations;
        this.fullTag = fullTag;
    }

    public ChunkPos getOriginLoadedChunk() {
        return this.originLoadedChunk;
    }

    public void setOriginLoadedChunk(ChunkPos originLoadedChunk) {
        this.originLoadedChunk = originLoadedChunk;
    }

    @NotNull
    public UUID uuid() {
        return this.uuid;
    }

    @NotNull
    public BoundingBox3d bounds() {
        return this.bounds;
    }

    @NotNull
    public Pose3d pose() {
        return this.pose;
    }

    @NotNull
    public List<UUID> dependencies() {
        return this.relations;
    }

    @NotNull
    public CompoundTag fullTag() {
        return this.fullTag;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        SubLevelData that = (SubLevelData)obj;
        return Objects.equals(this.uuid, that.uuid) && Objects.equals(this.bounds, that.bounds) && Objects.equals(this.pose, that.pose) && Objects.equals(this.relations, that.relations) && Objects.equals(this.fullTag, that.fullTag);
    }

    public int hashCode() {
        return Objects.hash(this.uuid, this.bounds, this.pose, this.relations, this.fullTag);
    }

    public String toString() {
        return "HalfLoadedSublevel[uuid=" + String.valueOf(this.uuid) + ", bounds=" + String.valueOf(this.bounds) + ", pose=" + String.valueOf(this.pose) + ", relations=" + String.valueOf(this.relations) + "]";
    }
}
