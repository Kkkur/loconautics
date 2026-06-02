/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.IdMap
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.Biomes
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.chunk.PalettedContainer
 *  net.minecraft.world.level.chunk.PalettedContainer$Strategy
 *  net.minecraft.world.level.chunk.PalettedContainerRO
 *  net.minecraft.world.level.chunk.UpgradeData
 *  net.minecraft.world.level.lighting.LevelLightEngine
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.ticks.LevelChunkTicks
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.sublevel.plot;

import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelReactionWheel;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.EmbeddedPlotLevelAccessor;
import dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.IdMap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.LevelChunkTicks;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3dc;

public abstract class LevelPlot {
    public final ChunkPos plotPos;
    protected final SubLevelContainer container;
    protected final int logSize;
    private final PlotChunkHolder[] chunks;
    @NotNull
    private final SubLevel subLevel;
    private final List<PlotChunkHolder> loadedChunks = new ObjectArrayList();
    protected final Object2ObjectOpenHashMap<BlockPos, BlockEntitySubLevelActor> blockEntityActors = new Object2ObjectOpenHashMap();
    private final Object2ObjectOpenHashMap<BlockPos, BlockEntitySubLevelReactionWheel> blockEntityReactionWheels = new Object2ObjectOpenHashMap();
    protected boolean expandPlotIfNecessary = true;
    @Nullable
    protected BoundingBox3i localBounds = null;
    protected ResourceKey<Biome> biome = Biomes.PLAINS;

    public LevelPlot(SubLevelContainer container, int x, int z, int logSize, SubLevel subLevel) {
        this.container = container;
        this.plotPos = new ChunkPos(x, z);
        this.logSize = logSize;
        this.chunks = new PlotChunkHolder[(1 << logSize) * (1 << logSize)];
        this.subLevel = subLevel;
    }

    public void tick() {
    }

    public EmbeddedPlotLevelAccessor getEmbeddedLevelAccessor() {
        return new EmbeddedPlotLevelAccessor(this);
    }

    public BlockPos getCenterBlock() {
        ChunkPos centerChunk = this.getCenterChunk();
        return new BlockPos(centerChunk.getMinBlockX() + 8, 128, centerChunk.getMinBlockZ() + 8);
    }

    protected void newChunk(ChunkPos pos, LevelChunk chunk, boolean initializeLighting) {
        ChunkPos local = this.toLocal(pos);
        if (this.getChunkHolder(local) != null) {
            throw new IllegalStateException("Chunk already exists at %s".formatted(pos));
        }
        PlotChunkHolder holder = PlotChunkHolder.create(chunk.getLevel(), pos, this.getLightEngine(), chunk);
        this.addChunkHolder(local, holder, initializeLighting);
    }

    public abstract LevelLightEngine getLightEngine();

    public void newEmptyChunk(ChunkPos pos) {
        Level level = this.container.getLevel();
        int sectionCount = level.getSectionsCount();
        LevelChunkSection[] sections = new LevelChunkSection[sectionCount];
        for (int i = 0; i < sectionCount; ++i) {
            Registry biomeRegistry = level.registryAccess().registryOrThrow(Registries.BIOME);
            PalettedContainer states = new PalettedContainer((IdMap)Block.BLOCK_STATE_REGISTRY, (Object)Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES);
            PalettedContainer biomes = new PalettedContainer(biomeRegistry.asHolderIdMap(), (Object)biomeRegistry.getHolderOrThrow(this.biome), PalettedContainer.Strategy.SECTION_BIOMES);
            sections[i] = new LevelChunkSection(states, (PalettedContainerRO)biomes);
        }
        LevelChunk chunk = new LevelChunk(level, pos, UpgradeData.EMPTY, new LevelChunkTicks(), new LevelChunkTicks(), 0L, sections, null, null);
        this.newChunk(pos, chunk, true);
    }

    public SubLevel getSubLevel() {
        return this.subLevel;
    }

    public boolean contains(double x, double z) {
        int logBlockSize = this.logSize + 4;
        return x >= (double)(this.plotPos.x << logBlockSize) && x < (double)(this.plotPos.x + 1 << logBlockSize) && z >= (double)(this.plotPos.z << logBlockSize) && z < (double)(this.plotPos.z + 1 << logBlockSize);
    }

    public boolean contains(Vec3 point) {
        return this.contains(point.x(), point.z());
    }

    public boolean contains(Vector3dc point) {
        return this.contains(point.x(), point.z());
    }

    public ChunkPos getChunkMin() {
        return new ChunkPos(this.plotPos.x << this.logSize, this.plotPos.z << this.logSize);
    }

    public ChunkPos getChunkMax() {
        return new ChunkPos((this.plotPos.x + 1 << this.logSize) - 1, (this.plotPos.z + 1 << this.logSize) - 1);
    }

    public boolean contains(ChunkPos chunk) {
        return chunk.x >> this.logSize == this.plotPos.x && chunk.z >> this.logSize == this.plotPos.z;
    }

    public ChunkPos toLocal(ChunkPos global) {
        return new ChunkPos(global.x - (this.plotPos.x << this.logSize), global.z - (this.plotPos.z << this.logSize));
    }

    public ChunkPos toGlobal(ChunkPos local) {
        return new ChunkPos(local.x + (this.plotPos.x << this.logSize), local.z + (this.plotPos.z << this.logSize));
    }

    @Nullable
    public PlotChunkHolder getChunkHolder(ChunkPos local) {
        if (local.x < 0 || local.x >= 1 << this.logSize || local.z < 0 || local.z >= 1 << this.logSize) {
            return null;
        }
        return this.chunks[local.z << this.logSize | local.x];
    }

    @ApiStatus.Internal
    public void addChunkHolder(ChunkPos localChunkPos, PlotChunkHolder holder, boolean initializeLighting) {
        if (holder == null) {
            throw new IllegalArgumentException("Chunk cannot be null");
        }
        this.loadedChunks.add(holder);
        this.chunks[localChunkPos.z << this.logSize | localChunkPos.x] = holder;
        this.updateBoundingBox();
    }

    public LevelChunk getChunk(ChunkPos local) {
        PlotChunkHolder holder = this.getChunkHolder(local);
        return holder == null ? null : holder.getChunk();
    }

    public ChunkPos getCenterChunk() {
        return new ChunkPos((this.plotPos.x << this.logSize) + (1 << this.logSize - 1), (this.plotPos.z << this.logSize) + (1 << this.logSize - 1));
    }

    public Collection<PlotChunkHolder> getLoadedChunks() {
        return this.loadedChunks;
    }

    public void updateBoundingBox() {
        if (this.subLevel.getLevel().isClientSide) {
            return;
        }
        BoundingBox3i previousBounds = this.localBounds;
        this.localBounds = null;
        BoundingBox3i temp = new BoundingBox3i(0, 0, 0, 0, 0, 0);
        for (PlotChunkHolder chunk : this.loadedChunks) {
            BoundingBox3i chunkBounds;
            ChunkPos pos = chunk.getPos();
            BoundingBox3ic chunkLocalBounds = chunk.getBoundingBox();
            if (chunkLocalBounds == null || (chunkBounds = chunkLocalBounds.move(pos.getMinBlockX(), 0, pos.getMinBlockZ(), temp)) == null) continue;
            if (this.localBounds == null) {
                this.localBounds = new BoundingBox3i((BoundingBox3ic)chunkBounds);
                continue;
            }
            this.localBounds = this.localBounds.expandTo((BoundingBox3ic)chunkBounds, this.localBounds);
        }
        if (!Objects.equals(previousBounds, this.localBounds)) {
            this.subLevel.onPlotBoundsChanged();
        }
    }

    public BoundingBox3ic getBoundingBox() {
        return this.localBounds != null ? this.localBounds : BoundingBox3i.EMPTY;
    }

    public void setBoundingBox(BoundingBox3ic bounds) {
        if (this.localBounds == null) {
            this.localBounds = new BoundingBox3i(bounds);
        } else {
            this.localBounds.set(bounds);
        }
    }

    public void onRemove() {
        for (PlotChunkHolder chunk : this.loadedChunks) {
            LevelChunk levelChunk = chunk.getChunk();
            assert (levelChunk != null);
            levelChunk.setLoaded(false);
            this.onRemoveChunkHolder(levelChunk);
        }
        this.loadedChunks.clear();
        this.localBounds = null;
    }

    protected abstract void onRemoveChunkHolder(LevelChunk var1);

    public void expandIfNecessary(BlockPos blockPos) {
        if (!this.expandPlotIfNecessary) {
            return;
        }
        for (Direction direction : Direction.values()) {
            BlockPos offsetPos = blockPos.relative(direction, 2);
            ChunkPos globalChunk = new ChunkPos(offsetPos);
            if (this.getChunk(this.toLocal(globalChunk)) != null) continue;
            this.newEmptyChunk(globalChunk);
        }
    }

    public void onBlockChange(BlockPos pos, BlockState state) {
        BlockEntitySubLevelActor actor;
        Level level = this.subLevel.getLevel();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        BlockEntitySubLevelActor blockEntitySubLevelActor = actor = blockEntity instanceof BlockEntitySubLevelActor ? (BlockEntitySubLevelActor)blockEntity : null;
        if (actor != null) {
            this.blockEntityActors.put((Object)pos, (Object)actor);
        } else {
            this.blockEntityActors.remove((Object)pos);
        }
        if (blockEntity instanceof BlockEntitySubLevelReactionWheel) {
            BlockEntitySubLevelReactionWheel reactionWheel = (BlockEntitySubLevelReactionWheel)blockEntity;
            this.blockEntityReactionWheels.put((Object)pos, (Object)reactionWheel);
            SubLevel subLevel = this.subLevel;
            if (subLevel instanceof ServerSubLevel) {
                ServerSubLevel serverSubLevel = (ServerSubLevel)subLevel;
                serverSubLevel.getReactionWheelManager().wheelChanged(pos, reactionWheel, true);
            }
        } else {
            SubLevel subLevel;
            BlockEntitySubLevelReactionWheel reactionWheel = (BlockEntitySubLevelReactionWheel)this.blockEntityReactionWheels.remove((Object)pos);
            if (reactionWheel != null && (subLevel = this.subLevel) instanceof ServerSubLevel) {
                ServerSubLevel serverSubLevel = (ServerSubLevel)subLevel;
                serverSubLevel.getReactionWheelManager().wheelChanged(pos, reactionWheel, false);
            }
        }
    }

    public Iterable<BlockEntitySubLevelActor> getBlockEntityActors() {
        return this.blockEntityActors.values();
    }

    public Collection<BlockEntitySubLevelReactionWheel> getBlockEntityReactionWheels() {
        return this.blockEntityReactionWheels.values();
    }

    public Set<Map.Entry<BlockPos, BlockEntitySubLevelReactionWheel>> getBlockEntityReactionWheelMap() {
        return this.blockEntityReactionWheels.entrySet();
    }
}
