/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 *  net.neoforged.neoforge.items.wrapper.CombinedInvWrapper
 *  net.neoforged.neoforge.server.ServerLifecycleHooks
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.station;

import com.simibubi.create.Create;
import com.simibubi.create.compat.computercraft.events.PackageEvent;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.packagePort.postbox.PostboxBlockEntity;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.SingleBlockEntityEdgePoint;
import com.simibubi.create.content.trains.station.GlobalPackagePort;
import com.simibubi.create.content.trains.station.StationBlock;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

public class GlobalStation
extends SingleBlockEntityEdgePoint {
    public String name = "Track Station";
    public WeakReference<Train> nearestTrain = new WeakReference<Object>(null);
    public boolean assembling;
    public Map<BlockPos, GlobalPackagePort> connectedPorts = new HashMap<BlockPos, GlobalPackagePort>();

    @Override
    public void blockEntityAdded(BlockEntity blockEntity, boolean front) {
        super.blockEntityAdded(blockEntity, front);
        BlockState state = blockEntity.getBlockState();
        this.assembling = state != null && state.hasProperty((Property)StationBlock.ASSEMBLING) && (Boolean)state.getValue((Property)StationBlock.ASSEMBLING) != false;
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean migration, DimensionPalette dimensions) {
        super.read(nbt, registries, migration, dimensions);
        this.name = nbt.getString("Name");
        this.assembling = nbt.getBoolean("Assembling");
        this.nearestTrain = new WeakReference<Object>(null);
        this.connectedPorts.clear();
        ListTag portList = nbt.getList("Ports", 10);
        NBTHelper.iterateCompoundList((ListTag)portList, c -> {
            GlobalPackagePort port = new GlobalPackagePort();
            port.address = c.getString("Address");
            port.offlineBuffer.deserializeNBT(registries, c.getCompound("OfflineBuffer"));
            port.primed = c.getBoolean("Primed");
            this.connectedPorts.put(NBTHelper.readBlockPos((CompoundTag)c, (String)"Pos"), port);
        });
    }

    @Override
    public void read(FriendlyByteBuf buffer, DimensionPalette dimensions) {
        super.read(buffer, dimensions);
        this.name = buffer.readUtf();
        this.assembling = buffer.readBoolean();
        if (buffer.readBoolean()) {
            this.blockEntityPos = buffer.readBlockPos();
        }
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, DimensionPalette dimensions) {
        super.write(nbt, registries, dimensions);
        nbt.putString("Name", this.name);
        nbt.putBoolean("Assembling", this.assembling);
        nbt.put("Ports", (Tag)NBTHelper.writeCompoundList(this.connectedPorts.entrySet(), e -> {
            CompoundTag c = new CompoundTag();
            c.putString("Address", ((GlobalPackagePort)e.getValue()).address);
            c.put("OfflineBuffer", (Tag)((GlobalPackagePort)e.getValue()).offlineBuffer.serializeNBT(registries));
            c.putBoolean("Primed", ((GlobalPackagePort)e.getValue()).primed);
            c.put("Pos", NbtUtils.writeBlockPos((BlockPos)((BlockPos)e.getKey())));
            return c;
        }));
    }

    @Override
    public void write(FriendlyByteBuf buffer, DimensionPalette dimensions) {
        super.write(buffer, dimensions);
        buffer.writeUtf(this.name);
        buffer.writeBoolean(this.assembling);
        buffer.writeBoolean(this.blockEntityPos != null);
        if (this.blockEntityPos != null) {
            buffer.writeBlockPos(this.blockEntityPos);
        }
    }

    public boolean canApproachFrom(TrackNode side) {
        return this.isPrimary(side) && !this.assembling;
    }

    @Override
    public boolean canNavigateVia(TrackNode side) {
        return super.canNavigateVia(side) && !this.assembling;
    }

    public void reserveFor(Train train) {
        Train nearestTrain = this.getNearestTrain();
        if (nearestTrain == null || nearestTrain.navigation.distanceToDestination > train.navigation.distanceToDestination) {
            this.nearestTrain = new WeakReference<Train>(train);
        }
    }

    public void cancelReservation(Train train) {
        if (this.nearestTrain.get() == train) {
            this.nearestTrain = new WeakReference<Object>(null);
        }
    }

    public void trainDeparted(Train train) {
        this.cancelReservation(train);
    }

    @Nullable
    public Train getPresentTrain() {
        Train nearestTrain = this.getNearestTrain();
        if (nearestTrain == null || nearestTrain.getCurrentStation() != this) {
            return null;
        }
        return nearestTrain;
    }

    @Nullable
    public Train getImminentTrain() {
        Train nearestTrain = this.getNearestTrain();
        if (nearestTrain == null) {
            return nearestTrain;
        }
        if (nearestTrain.getCurrentStation() == this) {
            return nearestTrain;
        }
        if (!nearestTrain.navigation.isActive()) {
            return null;
        }
        if (nearestTrain.navigation.distanceToDestination > 30.0) {
            return null;
        }
        return nearestTrain;
    }

    @Nullable
    public Train getNearestTrain() {
        return (Train)this.nearestTrain.get();
    }

    public void runMailTransfer() {
        Train train = this.getPresentTrain();
        if (train == null || this.connectedPorts.isEmpty()) {
            return;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerLevel level = server.getLevel(this.getBlockEntityDimension());
        for (Carriage carriage : train.carriages) {
            ItemStack result;
            CombinedInvWrapper carriageInventory = carriage.storage.getAllItems();
            if (carriageInventory == null) continue;
            for (Map.Entry<BlockPos, GlobalPackagePort> entry : this.connectedPorts.entrySet()) {
                BlockEntity blockEntity;
                GlobalPackagePort port = entry.getValue();
                BlockPos pos = entry.getKey();
                PostboxBlockEntity box = null;
                Object postboxInventory = port.offlineBuffer;
                if (level != null && level.isLoaded(pos) && (blockEntity = level.getBlockEntity(pos)) instanceof PostboxBlockEntity) {
                    PostboxBlockEntity ppbe = (PostboxBlockEntity)blockEntity;
                    postboxInventory = ppbe.inventory;
                    box = ppbe;
                }
                for (int slot = 0; slot < postboxInventory.getSlots(); ++slot) {
                    ItemStack stack = postboxInventory.getStackInSlot(slot);
                    if (!PackageItem.isPackage(stack) || PackageItem.matchAddress(stack, port.address)) continue;
                    result = ItemHandlerHelper.insertItemStacked((IItemHandler)carriageInventory, (ItemStack)stack, (boolean)false);
                    if (box != null) {
                        box.computerBehaviour.prepareComputerEvent(new PackageEvent(stack, "package_sent"));
                    }
                    if (!result.isEmpty()) continue;
                    postboxInventory.setStackInSlot(slot, ItemStack.EMPTY);
                    if (box == null) {
                        port.primed = true;
                    } else {
                        box.spawnParticles();
                    }
                    Create.RAILWAYS.markTracksDirty();
                }
            }
            block3: for (int slot = 0; slot < carriageInventory.getSlots(); ++slot) {
                ItemStack stack = carriageInventory.getStackInSlot(slot);
                if (!PackageItem.isPackage(stack)) continue;
                for (Map.Entry<BlockPos, GlobalPackagePort> entry : this.connectedPorts.entrySet()) {
                    BlockEntity blockEntity;
                    GlobalPackagePort port = entry.getValue();
                    BlockPos pos = entry.getKey();
                    PostboxBlockEntity box = null;
                    if (!PackageItem.matchAddress(stack, port.address)) continue;
                    Object postboxInventory = port.offlineBuffer;
                    if (level != null && level.isLoaded(pos) && (blockEntity = level.getBlockEntity(pos)) instanceof PostboxBlockEntity) {
                        PostboxBlockEntity ppbe = (PostboxBlockEntity)blockEntity;
                        postboxInventory = ppbe.inventory;
                        box = ppbe;
                    }
                    result = ItemHandlerHelper.insertItemStacked((IItemHandler)postboxInventory, (ItemStack)stack, (boolean)false);
                    if (box != null) {
                        box.computerBehaviour.prepareComputerEvent(new PackageEvent(stack, "package_received"));
                    }
                    if (!result.isEmpty()) continue;
                    carriageInventory.setStackInSlot(slot, ItemStack.EMPTY);
                    if (box == null) {
                        port.primed = true;
                    } else {
                        box.spawnParticles();
                    }
                    Create.RAILWAYS.markTracksDirty();
                    continue block3;
                }
            }
        }
    }
}
