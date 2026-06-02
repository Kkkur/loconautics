/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DynamicOps
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtOps
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.resources.RegistryOps
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 *  net.neoforged.neoforge.items.ItemStackHandler
 *  net.neoforged.neoforge.items.wrapper.CombinedInvWrapper
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.api.contraption.storage.SyncedMountedStorage;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorage;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageWrapper;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorageSyncPacket;
import com.simibubi.create.content.equipment.toolbox.ToolboxMountedStorage;
import com.simibubi.create.content.fluids.tank.storage.FluidTankMountedStorage;
import com.simibubi.create.content.fluids.tank.storage.creative.CreativeFluidTankMountedStorage;
import com.simibubi.create.content.logistics.crate.CreativeCrateMountedStorage;
import com.simibubi.create.content.logistics.depot.storage.DepotMountedStorage;
import com.simibubi.create.content.logistics.vault.ItemVaultMountedStorage;
import com.simibubi.create.impl.contraption.storage.FallbackMountedStorage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

public class MountedStorageManager {
    private Map<BlockPos, MountedItemStorage> itemsBuilder;
    private Map<BlockPos, MountedFluidStorage> fluidsBuilder;
    private Map<BlockPos, SyncedMountedStorage> syncedItemsBuilder;
    private Map<BlockPos, SyncedMountedStorage> syncedFluidsBuilder;
    private ImmutableMap<BlockPos, MountedItemStorage> allItemStorages;
    protected MountedItemStorageWrapper items;
    @Nullable
    protected MountedItemStorageWrapper fuelItems;
    protected MountedFluidStorageWrapper fluids;
    private ImmutableMap<BlockPos, SyncedMountedStorage> syncedItems;
    private ImmutableMap<BlockPos, SyncedMountedStorage> syncedFluids;
    private List<IItemHandlerModifiable> externalHandlers;
    protected CombinedInvWrapper allItems;
    private int syncCooldown;
    private Set<BlockPos> interactablePositions;

    public MountedStorageManager() {
        this.reset();
    }

    public void initialize() {
        if (this.isInitialized()) {
            return;
        }
        this.allItemStorages = ImmutableMap.copyOf(this.itemsBuilder);
        this.items = new MountedItemStorageWrapper(MountedStorageManager.subMap(this.allItemStorages, this::isExposed));
        this.allItems = this.items;
        this.itemsBuilder = null;
        ImmutableMap<BlockPos, MountedItemStorage> fuelMap = MountedStorageManager.subMap(this.allItemStorages, this::canUseForFuel);
        this.fuelItems = fuelMap.isEmpty() ? null : new MountedItemStorageWrapper(fuelMap);
        ImmutableMap fluids = ImmutableMap.copyOf(this.fluidsBuilder);
        this.fluids = new MountedFluidStorageWrapper((ImmutableMap<BlockPos, MountedFluidStorage>)fluids);
        this.fluidsBuilder = null;
        this.syncedItems = ImmutableMap.copyOf(this.syncedItemsBuilder);
        this.syncedItemsBuilder = null;
        this.syncedFluids = ImmutableMap.copyOf(this.syncedFluidsBuilder);
        this.syncedFluidsBuilder = null;
    }

    private boolean isExposed(MountedItemStorage storage) {
        return !AllTags.AllMountedItemStorageTypeTags.INTERNAL.matches(storage);
    }

    private boolean canUseForFuel(MountedItemStorage storage) {
        return this.isExposed(storage) && !AllTags.AllMountedItemStorageTypeTags.FUEL_BLACKLIST.matches(storage);
    }

    private boolean isInitialized() {
        return this.itemsBuilder == null;
    }

    private void assertInitialized() {
        if (!this.isInitialized()) {
            throw new IllegalStateException("MountedStorageManager is uninitialized");
        }
    }

    protected void reset() {
        this.allItemStorages = null;
        this.items = null;
        this.fuelItems = null;
        this.fluids = null;
        this.externalHandlers = new ArrayList<IItemHandlerModifiable>();
        this.allItems = null;
        this.itemsBuilder = new HashMap<BlockPos, MountedItemStorage>();
        this.fluidsBuilder = new HashMap<BlockPos, MountedFluidStorage>();
        this.syncedItemsBuilder = new HashMap<BlockPos, SyncedMountedStorage>();
        this.syncedFluidsBuilder = new HashMap<BlockPos, SyncedMountedStorage>();
    }

    public void addBlock(Level level, BlockState state, BlockPos globalPos, BlockPos localPos, @Nullable BlockEntity be) {
        Object storage;
        MountedFluidStorageType<?> fluidType;
        Object storage2;
        MountedItemStorageType<?> itemType = MountedItemStorageType.REGISTRY.get(state.getBlock());
        if (itemType != null && (storage2 = itemType.mount(level, state, globalPos, be)) != null) {
            this.addStorage((MountedItemStorage)storage2, localPos);
        }
        if ((fluidType = MountedFluidStorageType.REGISTRY.get(state.getBlock())) != null && (storage = fluidType.mount(level, state, globalPos, be)) != null) {
            this.addStorage((MountedFluidStorage)storage, localPos);
        }
    }

    public void unmount(Level level, StructureTemplate.StructureBlockInfo info, BlockPos globalPos, @Nullable BlockEntity be) {
        MountedFluidStorageType<?> expectedType;
        MountedFluidStorage fluidStorage;
        MountedItemStorageType<?> expectedType2;
        BlockPos localPos = info.pos();
        BlockState state = info.state();
        MountedItemStorage itemStorage = (MountedItemStorage)this.getAllItemStorages().get((Object)localPos);
        if (itemStorage != null && itemStorage.type == (expectedType2 = MountedItemStorageType.REGISTRY.get(state.getBlock()))) {
            itemStorage.unmount(level, state, globalPos, be);
        }
        if ((fluidStorage = (MountedFluidStorage)this.getFluids().storages.get((Object)localPos)) != null && fluidStorage.type == (expectedType = MountedFluidStorageType.REGISTRY.get(state.getBlock()))) {
            fluidStorage.unmount(level, state, globalPos, be);
        }
    }

    public void tick(AbstractContraptionEntity entity) {
        if (this.syncCooldown > 0) {
            --this.syncCooldown;
            return;
        }
        HashMap<BlockPos, MountedItemStorage> items = new HashMap<BlockPos, MountedItemStorage>();
        HashMap<BlockPos, MountedFluidStorage> fluids = new HashMap<BlockPos, MountedFluidStorage>();
        this.syncedItems.forEach((pos, storage) -> {
            if (storage.isDirty()) {
                items.put((BlockPos)pos, (MountedItemStorage)((Object)storage));
                storage.markClean();
            }
        });
        this.syncedFluids.forEach((pos, storage) -> {
            if (storage.isDirty()) {
                fluids.put((BlockPos)pos, (MountedFluidStorage)((Object)storage));
                storage.markClean();
            }
        });
        if (!items.isEmpty() || !fluids.isEmpty()) {
            MountedStorageSyncPacket packet = new MountedStorageSyncPacket(entity.getId(), items, fluids);
            CatnipServices.NETWORK.sendToClientsTrackingEntity((Entity)entity, (CustomPacketPayload)packet);
            this.syncCooldown = 8;
        }
    }

    public void handleSync(MountedStorageSyncPacket packet, AbstractContraptionEntity entity) {
        ImmutableMap<BlockPos, MountedItemStorage> items = this.getAllItemStorages();
        MountedFluidStorageWrapper fluids = this.getFluids();
        this.reset();
        IdentityHashMap<SyncedMountedStorage, BlockPos> syncedStorages = new IdentityHashMap<SyncedMountedStorage, BlockPos>();
        try {
            this.itemsBuilder.putAll((Map<BlockPos, MountedItemStorage>)items);
            this.fluidsBuilder.putAll((Map<BlockPos, MountedFluidStorage>)fluids.storages);
            packet.items().forEach((pos, storage) -> {
                this.itemsBuilder.put((BlockPos)pos, (MountedItemStorage)storage);
                syncedStorages.put((SyncedMountedStorage)((Object)storage), (BlockPos)pos);
            });
            packet.fluids().forEach((pos, storage) -> {
                this.fluidsBuilder.put((BlockPos)pos, (MountedFluidStorage)storage);
                syncedStorages.put((SyncedMountedStorage)((Object)storage), (BlockPos)pos);
            });
        }
        catch (Throwable t) {
            Create.LOGGER.error("An error occurred while syncing a MountedStorageManager", t);
        }
        this.initialize();
        Contraption contraption = entity.getContraption();
        syncedStorages.forEach((storage, pos) -> storage.afterSync(contraption, (BlockPos)pos));
    }

    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket, @Nullable Contraption contraption) {
        RegistryOps registryOps = registries.createSerializationContext((DynamicOps)NbtOps.INSTANCE);
        this.reset();
        try {
            NBTHelper.iterateCompoundList((ListTag)nbt.getList("items", 10), tag -> {
                BlockPos pos = NBTHelper.readBlockPos((CompoundTag)tag, (String)"pos");
                CompoundTag data = tag.getCompound("storage");
                MountedItemStorage.CODEC.decode((DynamicOps)registryOps, (Object)data).resultOrPartial(err -> Create.LOGGER.error("Failed to deserialize mounted item storage: {}", err)).map(Pair::getFirst).ifPresent(storage -> this.addStorage((MountedItemStorage)storage, pos));
            });
            NBTHelper.iterateCompoundList((ListTag)nbt.getList("fluids", 10), tag -> {
                BlockPos pos = NBTHelper.readBlockPos((CompoundTag)tag, (String)"pos");
                CompoundTag data = tag.getCompound("storage");
                MountedFluidStorage.CODEC.decode((DynamicOps)registryOps, (Object)data).resultOrPartial(err -> Create.LOGGER.error("Failed to deserialize mounted fluid storage: {}", err)).map(Pair::getFirst).ifPresent(storage -> this.addStorage((MountedFluidStorage)storage, pos));
            });
            this.readLegacy(registries, nbt);
            if (nbt.contains("interactable_positions")) {
                this.interactablePositions = new HashSet<BlockPos>();
                NBTHelper.iterateCompoundList((ListTag)nbt.getList("interactable_positions", 10), tag -> {
                    BlockPos pos = new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
                    this.interactablePositions.add(pos);
                });
            }
        }
        catch (Throwable t) {
            Create.LOGGER.error("Error deserializing mounted storage", t);
        }
        this.initialize();
        if (!clientPacket || contraption == null) {
            return;
        }
        this.getAllItemStorages().forEach((pos, storage) -> {
            if (storage instanceof SyncedMountedStorage) {
                SyncedMountedStorage synced = (SyncedMountedStorage)((Object)storage);
                synced.afterSync(contraption, (BlockPos)pos);
            }
        });
        this.getFluids().storages.forEach((pos, storage) -> {
            if (storage instanceof SyncedMountedStorage) {
                SyncedMountedStorage synced = (SyncedMountedStorage)((Object)storage);
                synced.afterSync(contraption, (BlockPos)pos);
            }
        });
    }

    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        RegistryOps registryOps = registries.createSerializationContext((DynamicOps)NbtOps.INSTANCE);
        ListTag items = new ListTag();
        this.getAllItemStorages().forEach((pos, storage) -> {
            if (!clientPacket || storage instanceof SyncedMountedStorage) {
                MountedItemStorage.CODEC.encodeStart((DynamicOps)registryOps, storage).resultOrPartial(err -> Create.LOGGER.error("Failed to serialize mounted item storage: {}", err)).ifPresent(encoded -> {
                    CompoundTag tag = new CompoundTag();
                    tag.put("pos", NbtUtils.writeBlockPos((BlockPos)pos));
                    tag.put("storage", encoded);
                    items.add((Object)tag);
                });
            }
        });
        if (!items.isEmpty()) {
            nbt.put("items", (Tag)items);
        }
        ListTag fluids = new ListTag();
        this.getFluids().storages.forEach((pos, storage) -> {
            if (!clientPacket || storage instanceof SyncedMountedStorage) {
                MountedFluidStorage.CODEC.encodeStart((DynamicOps)registryOps, storage).resultOrPartial(err -> Create.LOGGER.error("Failed to serialize mounted fluid storage: {}", err)).ifPresent(encoded -> {
                    CompoundTag tag = new CompoundTag();
                    tag.put("pos", NbtUtils.writeBlockPos((BlockPos)pos));
                    tag.put("storage", encoded);
                    fluids.add((Object)tag);
                });
            }
        });
        if (!fluids.isEmpty()) {
            nbt.put("fluids", (Tag)fluids);
        }
        if (clientPacket) {
            Sets.SetView positions = Sets.union((Set)this.getAllItemStorages().keySet(), (Set)this.getFluids().storages.keySet());
            ListTag list = new ListTag();
            for (BlockPos pos2 : positions) {
                CompoundTag tag = new CompoundTag();
                tag.putInt("X", pos2.getX());
                tag.putInt("Y", pos2.getY());
                tag.putInt("Z", pos2.getZ());
                list.add((Object)tag);
            }
            nbt.put("interactable_positions", (Tag)list);
        }
    }

    public void attachExternal(IItemHandlerModifiable externalStorage) {
        this.externalHandlers.add(externalStorage);
        IItemHandlerModifiable[] all = new IItemHandlerModifiable[this.externalHandlers.size() + 1];
        all[0] = this.items;
        for (int i = 0; i < this.externalHandlers.size(); ++i) {
            all[i + 1] = this.externalHandlers.get(i);
        }
        this.allItems = new CombinedInvWrapper(all);
    }

    public CombinedInvWrapper getAllItems() {
        this.assertInitialized();
        return this.allItems;
    }

    public ImmutableMap<BlockPos, MountedItemStorage> getAllItemStorages() {
        this.assertInitialized();
        return this.allItemStorages;
    }

    public MountedItemStorageWrapper getMountedItems() {
        this.assertInitialized();
        return this.items;
    }

    @Nullable
    public MountedItemStorageWrapper getFuelItems() {
        this.assertInitialized();
        return this.fuelItems;
    }

    public MountedFluidStorageWrapper getFluids() {
        this.assertInitialized();
        return this.fluids;
    }

    public boolean handlePlayerStorageInteraction(Contraption contraption, Player player, BlockPos localPos) {
        if (!(player instanceof ServerPlayer)) {
            return this.interactablePositions != null && this.interactablePositions.contains(localPos);
        }
        ServerPlayer serverPlayer = (ServerPlayer)player;
        StructureTemplate.StructureBlockInfo info = contraption.getBlocks().get(localPos);
        if (info == null) {
            return false;
        }
        MountedStorageManager storageManager = contraption.getStorage();
        MountedItemStorage storage = (MountedItemStorage)storageManager.getAllItemStorages().get((Object)localPos);
        if (storage != null) {
            return storage.handleInteraction(serverPlayer, contraption, info);
        }
        return false;
    }

    private void readLegacy(HolderLookup.Provider registries, CompoundTag nbt) {
        NBTHelper.iterateCompoundList((ListTag)nbt.getList("Storage", 10), tag -> {
            BlockPos pos = NBTHelper.readBlockPos((CompoundTag)tag, (String)"Pos");
            CompoundTag data = tag.getCompound("Data");
            if (data.contains("Toolbox")) {
                this.addStorage(ToolboxMountedStorage.fromLegacy(registries, data), pos);
            } else if (data.contains("NoFuel")) {
                this.addStorage(ItemVaultMountedStorage.fromLegacy(registries, data), pos);
            } else if (data.contains("Bottomless")) {
                ItemStack supplied = ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)data.getCompound("ProvidedStack"));
                this.addStorage(new CreativeCrateMountedStorage(supplied), pos);
            } else if (data.contains("Synced")) {
                this.addStorage(DepotMountedStorage.fromLegacy(registries, data), pos);
            } else {
                ItemStackHandler handler = new ItemStackHandler();
                handler.deserializeNBT(registries, data);
                this.addStorage(new FallbackMountedStorage((IItemHandler)handler), pos);
            }
        });
        NBTHelper.iterateCompoundList((ListTag)nbt.getList("FluidStorage", 10), tag -> {
            BlockPos pos = NBTHelper.readBlockPos((CompoundTag)tag, (String)"Pos");
            CompoundTag data = tag.getCompound("Data");
            if (data.contains("Bottomless")) {
                this.addStorage(CreativeFluidTankMountedStorage.fromLegacy(registries, data), pos);
            } else {
                this.addStorage(FluidTankMountedStorage.fromLegacy(registries, data), pos);
            }
        });
    }

    private void addStorage(MountedItemStorage storage, BlockPos pos) {
        this.itemsBuilder.put(pos, storage);
        if (storage instanceof SyncedMountedStorage) {
            SyncedMountedStorage synced = (SyncedMountedStorage)((Object)storage);
            this.syncedItemsBuilder.put(pos, synced);
        }
    }

    private void addStorage(MountedFluidStorage storage, BlockPos pos) {
        this.fluidsBuilder.put(pos, storage);
        if (storage instanceof SyncedMountedStorage) {
            SyncedMountedStorage synced = (SyncedMountedStorage)((Object)storage);
            this.syncedFluidsBuilder.put(pos, synced);
        }
    }

    private static <K, V> ImmutableMap<K, V> subMap(Map<K, V> map, Predicate<V> predicate) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        map.forEach((key, value) -> {
            if (predicate.test(value)) {
                builder.put(key, value);
            }
        });
        return builder.build();
    }
}
