/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.simibubi.create.content.trains.observer;

import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.signal.SignalPropagator;
import com.simibubi.create.content.trains.signal.SingleBlockEntityEdgePoint;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import java.util.UUID;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TrackObserver
extends SingleBlockEntityEdgePoint {
    private int activated = 0;
    private FilterItemStack filter = FilterItemStack.empty();
    private UUID currentTrain = null;

    @Override
    public void blockEntityAdded(BlockEntity blockEntity, boolean front) {
        super.blockEntityAdded(blockEntity, front);
        FilteringBehaviour filteringBehaviour = BlockEntityBehaviour.get(blockEntity, FilteringBehaviour.TYPE);
        if (filteringBehaviour != null) {
            this.setFilterAndNotify(blockEntity.getLevel(), filteringBehaviour.getFilter());
        }
    }

    @Override
    public void tick(TrackGraph graph, boolean preTrains) {
        super.tick(graph, preTrains);
        if (this.isActivated()) {
            --this.activated;
        }
        if (!this.isActivated()) {
            this.currentTrain = null;
        }
    }

    public void setFilterAndNotify(Level level, ItemStack filter) {
        this.filter = FilterItemStack.of(filter.copy());
        this.notifyTrains(level);
    }

    private void notifyTrains(Level level) {
        TrackGraph graph = Create.RAILWAYS.sided((LevelAccessor)level).getGraph((LevelAccessor)level, (TrackNodeLocation)((Object)this.edgeLocation.getFirst()));
        if (graph == null) {
            return;
        }
        TrackEdge edge = graph.getConnection((Couple<TrackNode>)this.edgeLocation.map(graph::locateNode));
        if (edge == null) {
            return;
        }
        SignalPropagator.notifyTrains(graph, edge);
    }

    public FilterItemStack getFilter() {
        return this.filter;
    }

    public UUID getCurrentTrain() {
        return this.currentTrain;
    }

    public boolean isActivated() {
        return this.activated > 0;
    }

    public void keepAlive(Train train) {
        this.activated = 8;
        this.currentTrain = train.id;
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean migration, DimensionPalette dimensions) {
        super.read(nbt, registries, migration, dimensions);
        this.activated = nbt.getInt("Activated");
        this.filter = FilterItemStack.of(registries, nbt.getCompound("Filter"));
        if (nbt.contains("TrainId")) {
            this.currentTrain = nbt.getUUID("TrainId");
        }
    }

    @Override
    public void read(FriendlyByteBuf buffer, DimensionPalette dimensions) {
        super.read(buffer, dimensions);
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, DimensionPalette dimensions) {
        super.write(nbt, registries, dimensions);
        nbt.putInt("Activated", this.activated);
        nbt.put("Filter", (Tag)this.filter.serializeNBT(registries));
        if (this.currentTrain != null) {
            nbt.putUUID("TrainId", this.currentTrain);
        }
    }

    @Override
    public void write(FriendlyByteBuf buffer, DimensionPalette dimensions) {
        super.write(buffer, dimensions);
    }
}
