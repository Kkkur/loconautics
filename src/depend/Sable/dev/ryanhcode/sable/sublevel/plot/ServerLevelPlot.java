/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectCollection
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.IdMap
 *  net.minecraft.core.Registry
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.LongArrayTag
 *  net.minecraft.nbt.NbtOps
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.FullChunkStatus
 *  net.minecraft.server.level.ServerChunkCache
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.network.ServerGamePacketListenerImpl
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.ChunkAccess$TicksToSave
 *  net.minecraft.world.level.chunk.ChunkSource
 *  net.minecraft.world.level.chunk.DataLayer
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.chunk.LightChunkGetter
 *  net.minecraft.world.level.chunk.PalettedContainer
 *  net.minecraft.world.level.chunk.PalettedContainer$Strategy
 *  net.minecraft.world.level.chunk.PalettedContainerRO
 *  net.minecraft.world.level.chunk.UpgradeData
 *  net.minecraft.world.level.chunk.storage.ChunkSerializer$ChunkReadException
 *  net.minecraft.world.level.entity.EntityAccess
 *  net.minecraft.world.level.entity.EntitySection
 *  net.minecraft.world.level.entity.PersistentEntitySectionManager
 *  net.minecraft.world.level.levelgen.Heightmap
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.level.lighting.LevelLightEngine
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.ticks.LevelChunkTicks
 */
package dev.ryanhcode.sable.sublevel.plot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockSubLevelLiftProvider;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.api.sublevel.KinematicContraption;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.index.SableTags;
import dev.ryanhcode.sable.mixinterface.plot.serialization.LevelChunkTicksExtension;
import dev.ryanhcode.sable.platform.SablePlotPlatform;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder;
import dev.ryanhcode.sable.sublevel.plot.SubLevelPlayerChunkSender;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.IdMap;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.LevelChunkTicks;

public class ServerLevelPlot
extends LevelPlot {
    protected static final int DATA_VERSION = 1;
    private static final Codec<PalettedContainer<BlockState>> BLOCK_STATE_CODEC = PalettedContainer.codecRW((IdMap)Block.BLOCK_STATE_REGISTRY, (Codec)BlockState.CODEC, (PalettedContainer.Strategy)PalettedContainer.Strategy.SECTION_STATES, (Object)Blocks.AIR.defaultBlockState());
    protected final LevelLightEngine lightEngine;
    private final ObjectSet<KinematicContraption> contraptions = new ObjectOpenHashSet();
    private final Object2ObjectMap<BlockPos, BlockSubLevelLiftProvider.LiftProviderContext> liftProviders = new Object2ObjectOpenHashMap();

    public ServerLevelPlot(SubLevelContainer plotContainer, int x, int z, int logSize, ServerSubLevel subLevel) {
        super(plotContainer, x, z, logSize, subLevel);
        ServerLevel level = subLevel.getLevel();
        LevelLightEngine parentLightEngine = level.getLightEngine();
        ChunkSource chunkSource = level.getChunkSource();
        this.lightEngine = new LevelLightEngine((LightChunkGetter)chunkSource, parentLightEngine.blockEngine != null, parentLightEngine.skyEngine != null);
    }

    public void addContraption(KinematicContraption contraption) {
        this.contraptions.add((Object)contraption);
    }

    public void removeContraption(KinematicContraption contraption) {
        this.contraptions.remove((Object)contraption);
    }

    public ObjectCollection<KinematicContraption> getContraptions() {
        return this.contraptions;
    }

    private static void logLoadingErrors(ChunkPos chunkPos, int y, String errorText) {
        Sable.LOGGER.error("Recoverable errors when loading plot section [{}, {}, {}]: {}", new Object[]{chunkPos.x, y, chunkPos.z, errorText});
    }

    @Override
    public void tick() {
        do {
            this.lightEngine.runLightUpdates();
        } while (this.lightEngine.hasLightWork());
        this.contraptions.removeIf(contraption -> !contraption.sable$isValid());
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return this.lightEngine;
    }

    @Override
    protected void onRemoveChunkHolder(LevelChunk levelChunk) {
        int idx;
        ChunkPos pos = levelChunk.getPos();
        ServerLevel serverLevel = this.getSubLevel().getLevel();
        ServerChunkCache serverChunkCache = serverLevel.getChunkSource();
        if (serverChunkCache instanceof ServerChunkCache) {
            ServerChunkCache cache = serverChunkCache;
            cache.chunkMap.updatingChunkMap.remove(pos.toLong());
            cache.chunkMap.modified = true;
        }
        levelChunk.setLoaded(false);
        serverLevel.unload(levelChunk);
        this.lightEngine.retainData(pos, false);
        this.lightEngine.setLightEnabled(pos, false);
        for (idx = this.lightEngine.getMinLightSection(); idx < this.lightEngine.getMaxLightSection(); ++idx) {
            this.lightEngine.queueSectionData(LightLayer.BLOCK, SectionPos.of((ChunkPos)pos, (int)idx), null);
            this.lightEngine.queueSectionData(LightLayer.SKY, SectionPos.of((ChunkPos)pos, (int)idx), null);
        }
        for (idx = serverLevel.getMinSection(); idx < serverLevel.getMaxSection(); ++idx) {
            this.lightEngine.updateSectionStatus(SectionPos.of((ChunkPos)pos, (int)idx), true);
        }
        serverLevel.entityManager.updateChunkStatus(pos, FullChunkStatus.INACCESSIBLE);
    }

    public void setBiome(ResourceKey<Biome> biome) {
        this.biome = biome;
    }

    private void initializeLight(LevelChunk chunk) {
        LevelChunkSection[] alevelchunksection = chunk.getSections();
        Level level = chunk.getLevel();
        ChunkPos pos = chunk.getPos();
        LevelLightEngine lightEngine = this.lightEngine;
        for (int i = 0; i < chunk.getSectionsCount(); ++i) {
            LevelChunkSection levelchunksection = alevelchunksection[i];
            if (levelchunksection.hasOnlyAir()) continue;
            this.lightEngine.updateSectionStatus(SectionPos.of((ChunkPos)pos, (int)level.getSectionYFromSectionIndex(i)), false);
        }
        lightEngine.setLightEnabled(pos, chunk.isLightCorrect());
        lightEngine.retainData(pos, false);
    }

    private void correctLight(LevelChunk chunk) {
        if (chunk.isLightCorrect()) {
            return;
        }
        this.lightEngine.propagateLightSources(chunk.getPos());
        chunk.setLightCorrect(true);
    }

    private void lightChunk(LevelChunk chunk) {
        chunk.initializeLightSources();
        this.initializeLight(chunk);
        this.correctLight(chunk);
    }

    @Override
    public void addChunkHolder(ChunkPos localChunkPos, PlotChunkHolder holder, boolean initializeLighting) {
        ServerLevel level = this.getSubLevel().getLevel();
        ChunkPos globalChunkPos = this.toGlobal(localChunkPos);
        LevelChunk chunk = holder.getChunk();
        ServerChunkCache serverChunkCache = level.getChunkSource();
        if (serverChunkCache instanceof ServerChunkCache) {
            ServerChunkCache cache = serverChunkCache;
            cache.chunkMap.updatingChunkMap.put(globalChunkPos.toLong(), (Object)holder);
            cache.chunkMap.modified = true;
        }
        super.addChunkHolder(localChunkPos, holder, initializeLighting);
        chunk.setLightCorrect(false);
        if (initializeLighting) {
            this.lightChunk(chunk);
        }
        chunk.setFullStatus(() -> ((PlotChunkHolder)holder).getFullStatus());
        chunk.runPostLoad();
        chunk.setLoaded(true);
        chunk.registerAllBlockEntitiesAfterLevelLoad();
        chunk.registerTickContainerInLevel(level);
        level.entityManager.updateChunkStatus(chunk.getPos(), FullChunkStatus.ENTITY_TICKING);
        level.getChunkSource().chunkMap.onFullChunkStatusChange(globalChunkPos, FullChunkStatus.ENTITY_TICKING);
        do {
            this.lightEngine.runLightUpdates();
        } while (this.lightEngine.hasLightWork());
        List<ServerPlayer> players = this.container.getPlayersTracking(globalChunkPos);
        for (ServerPlayer player : players) {
            SubLevelPlayerChunkSender.sendChunk(arg_0 -> ((ServerGamePacketListenerImpl)player.connection).send(arg_0), this.lightEngine, chunk);
            SubLevelPlayerChunkSender.sendChunkPoiData(level, chunk);
        }
    }

    public void kickAllEntities() {
        ServerSubLevel subLevel = this.getSubLevel();
        PersistentEntitySectionManager manager = subLevel.getLevel().entityManager;
        for (PlotChunkHolder chunk : this.getLoadedChunks()) {
            Stream sections = manager.sectionStorage.getExistingSectionsInChunk(chunk.getPos().toLong());
            for (EntitySection section : sections.toList()) {
                List entities = section.getEntities().toList();
                for (Entity entity : entities) {
                    if (entity.getType().is(SableTags.DESTROY_WITH_SUB_LEVEL)) {
                        entity.remove(Entity.RemovalReason.KILLED);
                    } else {
                        EntitySubLevelUtil.kickEntity(subLevel, entity);
                        ServerLevel level = subLevel.getLevel();
                        entity.levelCallback.onRemove(Entity.RemovalReason.CHANGED_DIMENSION);
                        level.addDuringTeleport(entity);
                    }
                    section.remove((EntityAccess)entity);
                }
            }
        }
    }

    public void destroyAllBlocks() {
        if (this.localBounds == null || this.localBounds == BoundingBox3i.EMPTY) {
            return;
        }
        ServerLevel level = this.getSubLevel().getLevel();
        BoundingBox3i bounds = this.localBounds;
        for (int x = bounds.minX(); x <= bounds.maxX(); ++x) {
            for (int y = bounds.minY(); y <= bounds.maxY(); ++y) {
                for (int z = bounds.minZ(); z <= bounds.maxZ(); ++z) {
                    BlockPos pos = new BlockPos(x, y, z);
                    level.destroyBlock(pos, true);
                }
            }
        }
    }

    private void newNonLitChunk(ChunkPos pos) {
        Level level = this.container.getLevel();
        int sectionCount = level.getSectionsCount();
        LevelChunkSection[] sections = new LevelChunkSection[sectionCount];
        for (int i = 0; i < sectionCount; ++i) {
            sections[i] = new LevelChunkSection(level.registryAccess().registryOrThrow(Registries.BIOME));
        }
        LevelChunk chunk = new LevelChunk(level, pos, UpgradeData.EMPTY, new LevelChunkTicks(), new LevelChunkTicks(), 0L, sections, null, null);
        this.newChunk(pos, chunk, false);
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("plot_x", this.plotPos.x - this.container.getOrigin().x);
        tag.putInt("plot_z", this.plotPos.z - this.container.getOrigin().y);
        tag.putInt("log_size", this.logSize);
        tag.putString("biome", this.biome.location().toString());
        tag.putInt("data_version", 1);
        ServerLevel level = this.getSubLevel().getLevel();
        CompoundTag chunks = new CompoundTag();
        for (PlotChunkHolder chunkHolder : this.getLoadedChunks()) {
            ChunkPos global = chunkHolder.getPos();
            ChunkPos local = this.toLocal(global);
            LevelChunk chunk = chunkHolder.getChunk();
            CompoundTag chunkTag = new CompoundTag();
            CompoundTag sectionsTag = new CompoundTag();
            for (int idx = 0; idx < chunk.getSectionsCount(); ++idx) {
                LevelChunkSection section = chunk.getSection(idx);
                if (section.hasOnlyAir()) continue;
                CompoundTag sectionTag = new CompoundTag();
                sectionTag.put("block_states", (Tag)BLOCK_STATE_CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)section.getStates()).getOrThrow());
                SectionPos sectionPos = SectionPos.of((ChunkPos)global, (int)level.getSectionYFromSectionIndex(idx));
                DataLayer blockLight = this.lightEngine.getLayerListener(LightLayer.BLOCK).getDataLayerData(sectionPos);
                DataLayer skyLight = this.lightEngine.getLayerListener(LightLayer.SKY).getDataLayerData(sectionPos);
                if (blockLight != null && !blockLight.isEmpty()) {
                    sectionTag.putByteArray("BlockLight", blockLight.getData());
                }
                if (skyLight != null && !skyLight.isEmpty()) {
                    sectionTag.putByteArray("SkyLight", skyLight.getData());
                }
                sectionsTag.put(String.valueOf(idx), (Tag)sectionTag);
            }
            chunkTag.put("sections", (Tag)sectionsTag);
            tag.putBoolean("isLightOn", chunk.isLightCorrect());
            ListTag blockEntitiesTag = new ListTag();
            for (BlockPos blockPos : chunk.getBlockEntitiesPos()) {
                CompoundTag blockEntityNBT = chunk.getBlockEntityNbtForSaving(blockPos, (HolderLookup.Provider)level.registryAccess());
                if (blockEntityNBT == null) continue;
                blockEntitiesTag.add((Object)blockEntityNBT);
            }
            chunkTag.put("block_entities", (Tag)blockEntitiesTag);
            ChunkAccess.TicksToSave ticksToSave = chunk.getTicksForSerialization();
            long gameTime = level.getGameTime();
            chunkTag.put("block_ticks", ticksToSave.blocks().save(gameTime, block -> BuiltInRegistries.BLOCK.getKey(block).toString()));
            chunkTag.put("fluid_ticks", ticksToSave.fluids().save(gameTime, fluid -> BuiltInRegistries.FLUID.getKey(fluid).toString()));
            CompoundTag heightMapsTag = new CompoundTag();
            for (Map.Entry entry : chunk.getHeightmaps()) {
                if (!chunk.getPersistedStatus().heightmapsAfter().contains(entry.getKey())) continue;
                heightMapsTag.put(((Heightmap.Types)entry.getKey()).getSerializationKey(), (Tag)new LongArrayTag(((Heightmap)entry.getValue()).getRawData()));
            }
            chunkTag.put("heightmaps", (Tag)heightMapsTag);
            SablePlotPlatform.INSTANCE.writeLightData(tag, level.registryAccess(), chunk);
            SablePlotPlatform.INSTANCE.writeChunkAttachments(tag, level.registryAccess(), chunk);
            chunks.put(String.valueOf(ChunkPos.asLong((int)local.x, (int)local.z)), (Tag)chunkTag);
        }
        tag.put("chunks", (Tag)chunks);
        return tag;
    }

    public void load(CompoundTag tag) {
        ChunkPos global;
        ChunkPos local;
        LevelChunk chunk;
        ResourceLocation location;
        int dataVersion;
        int logSize = tag.getInt("log_size");
        if (logSize != this.logSize) {
            throw new IllegalArgumentException("Log size mismatch");
        }
        int n = dataVersion = tag.contains("data_version") ? tag.getInt("data_version") : 0;
        if (dataVersion < 0 || dataVersion > 1) {
            throw new IllegalArgumentException("Unsupported version: " + dataVersion);
        }
        ServerSubLevel subLevel = this.getSubLevel();
        ServerLevel level = subLevel.getLevel();
        if (tag.contains("biome") && (location = ResourceLocation.tryParse((String)tag.getString("biome"))) != null) {
            this.biome = ResourceKey.create((ResourceKey)Registries.BIOME, (ResourceLocation)location);
        }
        CompoundTag chunks = tag.getCompound("chunks");
        for (String key : chunks.getAllKeys()) {
            long chunkPos = Long.parseLong(key);
            int x = ChunkPos.getX((long)chunkPos);
            int z = ChunkPos.getZ((long)chunkPos);
            ChunkPos local2 = new ChunkPos(x, z);
            ChunkPos global2 = this.toGlobal(local2);
            CompoundTag chunkTag = chunks.getCompound(key);
            CompoundTag sectionsTag = chunkTag.getCompound("sections");
            this.newNonLitChunk(global2);
            chunk = this.getChunk(local2);
            boolean hasLit = false;
            for (String sectionKey : sectionsTag.getAllKeys()) {
                boolean hasSkyLight;
                int yIndex = Integer.parseInt(sectionKey);
                LevelChunkSection[] sections = chunk.getSections();
                CompoundTag sectionTag = sectionsTag.getCompound(sectionKey);
                PalettedContainer palettedContainer = (PalettedContainer)BLOCK_STATE_CODEC.parse((DynamicOps)NbtOps.INSTANCE, (Object)sectionTag.getCompound("block_states")).promotePartial(string -> ServerLevelPlot.logLoadingErrors(new ChunkPos(chunkPos), chunk.getSectionYFromSectionIndex(yIndex), string)).getOrThrow(ChunkSerializer.ChunkReadException::new);
                Registry biomeRegistry = level.registryAccess().registryOrThrow(Registries.BIOME);
                PalettedContainer biomeContainer = new PalettedContainer(biomeRegistry.asHolderIdMap(), (Object)biomeRegistry.getHolderOrThrow(this.biome), PalettedContainer.Strategy.SECTION_BIOMES);
                sections[yIndex] = new LevelChunkSection(palettedContainer, (PalettedContainerRO)biomeContainer);
                SectionPos sectionPos = SectionPos.of((ChunkPos)global2, (int)level.getSectionYFromSectionIndex(yIndex));
                boolean hasBlockLight = this.lightEngine.blockEngine != null && sectionTag.contains("BlockLight", 7);
                boolean bl = hasSkyLight = this.lightEngine.skyEngine != null && level.dimensionType().hasSkyLight() && sectionTag.contains("SkyLight", 7);
                if (!hasBlockLight && !hasSkyLight) continue;
                if (!hasLit) {
                    this.lightEngine.retainData(global2, true);
                    hasLit = true;
                }
                if (hasBlockLight) {
                    this.lightEngine.queueSectionData(LightLayer.BLOCK, sectionPos, new DataLayer(sectionTag.getByteArray("BlockLight")));
                }
                if (!hasSkyLight) continue;
                this.lightEngine.queueSectionData(LightLayer.SKY, sectionPos, new DataLayer(sectionTag.getByteArray("SkyLight")));
            }
            if (dataVersion >= 0) {
                LevelChunkTicks blockTicks = LevelChunkTicks.load((ListTag)chunkTag.getList("block_ticks", 10), id -> BuiltInRegistries.BLOCK.getOptional(ResourceLocation.tryParse((String)id)), (ChunkPos)global2);
                LevelChunkTicks fluidTicks = LevelChunkTicks.load((ListTag)chunkTag.getList("fluid_ticks", 10), id -> BuiltInRegistries.FLUID.getOptional(ResourceLocation.tryParse((String)id)), (ChunkPos)global2);
                ((LevelChunkTicksExtension)chunk.getBlockTicks()).sable$copy(blockTicks);
                ((LevelChunkTicksExtension)chunk.getFluidTicks()).sable$copy(fluidTicks);
                CompoundTag heightMapsTag = chunkTag.getCompound("heightmaps");
                EnumSet<Heightmap.Types> enumset = EnumSet.noneOf(Heightmap.Types.class);
                for (Heightmap.Types heightMapType : chunk.getPersistedStatus().heightmapsAfter()) {
                    String heightMapKey = heightMapType.getSerializationKey();
                    if (heightMapsTag.contains(heightMapKey, 12)) {
                        chunk.setHeightmap(heightMapType, heightMapsTag.getLongArray(heightMapKey));
                        continue;
                    }
                    enumset.add(heightMapType);
                }
                Heightmap.primeHeightmaps((ChunkAccess)chunk, enumset);
                SablePlotPlatform.INSTANCE.readLightData(chunkTag, level.registryAccess(), chunk);
                chunk.setLightCorrect(chunkTag.getBoolean("isLightOn"));
            }
            this.lightChunk(chunk);
            SablePlotPlatform.INSTANCE.readChunkAttachments(chunkTag, level.registryAccess(), chunk);
            ListTag blockEntitiesTag = chunkTag.getList("block_entities", 10);
            for (int i = 0; i < blockEntitiesTag.size(); ++i) {
                CompoundTag blockEntityTag = blockEntitiesTag.getCompound(i);
                boolean keepBlockEntityPacked = blockEntityTag.getBoolean("keepPacked");
                if (keepBlockEntityPacked) {
                    chunk.setBlockEntityNbt(blockEntityTag);
                    continue;
                }
                BlockPos blockPos = BlockEntity.getPosFromTag((CompoundTag)blockEntityTag);
                BlockEntity blockEntity = BlockEntity.loadStatic((BlockPos)blockPos, (BlockState)chunk.getBlockState(blockPos), (CompoundTag)blockEntityTag, (HolderLookup.Provider)level.registryAccess());
                if (blockEntity == null) continue;
                chunk.setBlockEntity(blockEntity);
            }
            chunk.registerAllBlockEntitiesAfterLevelLoad();
            level.startTickingChunk(chunk);
            SablePlotPlatform.INSTANCE.postLoad(chunkTag, chunk);
        }
        do {
            this.lightEngine.runLightUpdates();
        } while (this.lightEngine.hasLightWork());
        SubLevelPhysicsSystem physicsSystem = ((ServerSubLevelContainer)this.container).physicsSystem();
        BlockPos.MutableBlockPos globalBlockPos = new BlockPos.MutableBlockPos();
        for (String key : chunks.getAllKeys()) {
            long chunkPos = Long.parseLong(key);
            int x = ChunkPos.getX((long)chunkPos);
            int z = ChunkPos.getZ((long)chunkPos);
            local = new ChunkPos(x, z);
            global = this.toGlobal(local);
            PlotChunkHolder chunkHolder = this.getChunkHolder(local);
            LevelChunk chunk2 = this.getChunk(local);
            LevelChunkSection[] levelChunkSections = chunk2.getSections();
            List<ServerPlayer> players = this.container.getPlayersTracking(global);
            for (ServerPlayer player : players) {
                SubLevelPlayerChunkSender.sendChunk(arg_0 -> ((ServerGamePacketListenerImpl)player.connection).send(arg_0), this.lightEngine, chunk2);
                SubLevelPlayerChunkSender.sendChunkPoiData(level, chunk2);
            }
            for (int i = 0; i < chunk2.getSectionsCount(); ++i) {
                LevelChunkSection section = levelChunkSections[i];
                if (section.hasOnlyAir()) continue;
                int sectionY = chunk2.getSectionYFromSectionIndex(i);
                int chunkMinX = global.getMinBlockX();
                int chunkMinY = sectionY << 4;
                int chunkMinZ = global.getMinBlockZ();
                boolean expandPlotBackup = this.expandPlotIfNecessary;
                this.expandPlotIfNecessary = false;
                BlockState airState = Blocks.AIR.defaultBlockState();
                for (int xOff = 0; xOff < 16; ++xOff) {
                    for (int yOff = 0; yOff < 16; ++yOff) {
                        for (int zOff = 0; zOff < 16; ++zOff) {
                            BlockState state = section.getBlockState(xOff, yOff, zOff);
                            if (state.isAir()) continue;
                            globalBlockPos.set(xOff + chunkMinX, yOff + chunkMinY, zOff + chunkMinZ);
                            BlockPos immutable = globalBlockPos.immutable();
                            chunkHolder.handleBlockChange(xOff, chunkMinY + yOff, zOff, airState, state);
                            subLevel.getHeatMapManager().onSolidAdded(immutable);
                            subLevel.getFloatingBlockController().queueAddFloatingBlock(state, immutable);
                            physicsSystem.updateMassDataFromBlockChange(subLevel, (BlockPos)globalBlockPos, airState, state, false);
                            this.onBlockChange(immutable, state);
                        }
                    }
                }
                this.expandPlotIfNecessary = expandPlotBackup;
            }
        }
        this.updateBoundingBox();
        subLevel.updateMergedMassData(1.0f);
        physicsSystem.getPipeline().onStatsChanged(subLevel);
        for (String key : chunks.getAllKeys()) {
            long chunkPos = Long.parseLong(key);
            int x = ChunkPos.getX((long)chunkPos);
            int z = ChunkPos.getZ((long)chunkPos);
            local = new ChunkPos(x, z);
            global = this.toGlobal(local);
            chunk = this.getChunk(local);
            LevelChunkSection[] levelChunkSections = chunk.getSections();
            for (int i = 0; i < chunk.getSectionsCount(); ++i) {
                LevelChunkSection section = levelChunkSections[i];
                if (section.hasOnlyAir()) continue;
                int sectionY = chunk.getSectionYFromSectionIndex(i);
                physicsSystem.getTicketManager().addTicketForSection(level, SectionPos.of((int)global.x, (int)sectionY, (int)global.z));
                physicsSystem.getPipeline().handleChunkSectionAddition(section, global.x, sectionY, global.z, true);
            }
        }
        subLevel.updateMergedMassData(1.0f);
        physicsSystem.getPipeline().onStatsChanged(subLevel);
    }

    @Override
    public void onBlockChange(BlockPos pos, BlockState state) {
        super.onBlockChange(pos, state);
        this.liftProviders.remove((Object)pos);
        Block block = state.getBlock();
        if (block instanceof BlockSubLevelLiftProvider) {
            BlockSubLevelLiftProvider prov = (BlockSubLevelLiftProvider)block;
            this.liftProviders.put((Object)pos, (Object)new BlockSubLevelLiftProvider.LiftProviderContext(pos, state, Vec3.atLowerCornerOf((Vec3i)prov.sable$getNormal(state).getNormal())));
        }
    }

    public ObjectCollection<BlockSubLevelLiftProvider.LiftProviderContext> getLiftProviders() {
        return this.liftProviders.values();
    }

    @Override
    public ServerSubLevel getSubLevel() {
        return (ServerSubLevel)super.getSubLevel();
    }
}
