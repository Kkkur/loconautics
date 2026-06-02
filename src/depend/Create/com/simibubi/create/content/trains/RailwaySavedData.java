/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.world.level.saveddata.SavedData
 *  net.minecraft.world.level.saveddata.SavedData$Factory
 */
package com.simibubi.create.content.trains;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

public class RailwaySavedData
extends SavedData {
    private Map<UUID, TrackGraph> trackNetworks = new HashMap<UUID, TrackGraph>();
    private Map<UUID, SignalEdgeGroup> signalEdgeGroups = new HashMap<UUID, SignalEdgeGroup>();
    private Map<UUID, Train> trains = new HashMap<UUID, Train>();

    public static SavedData.Factory<RailwaySavedData> factory() {
        return new SavedData.Factory(RailwaySavedData::new, RailwaySavedData::load);
    }

    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registries) {
        GlobalRailwayManager railways = Create.RAILWAYS;
        DimensionPalette dimensions = new DimensionPalette();
        nbt.put("RailGraphs", (Tag)NBTHelper.writeCompoundList(railways.trackNetworks.values(), tg -> tg.write(registries, dimensions)));
        nbt.put("SignalBlocks", (Tag)NBTHelper.writeCompoundList(railways.signalEdgeGroups.values(), seg -> {
            if (seg.fallbackGroup && !railways.trackNetworks.containsKey(seg.id)) {
                return null;
            }
            return seg.write();
        }));
        nbt.put("Trains", (Tag)NBTHelper.writeCompoundList(railways.trains.values(), t -> t.write(dimensions, registries)));
        dimensions.write(nbt);
        return nbt;
    }

    private static RailwaySavedData load(CompoundTag nbt, HolderLookup.Provider registries) {
        RailwaySavedData sd = new RailwaySavedData();
        sd.trackNetworks = new HashMap<UUID, TrackGraph>();
        sd.signalEdgeGroups = new HashMap<UUID, SignalEdgeGroup>();
        sd.trains = new HashMap<UUID, Train>();
        DimensionPalette dimensions = DimensionPalette.read(nbt);
        NBTHelper.iterateCompoundList((ListTag)nbt.getList("RailGraphs", 10), c -> {
            TrackGraph graph = TrackGraph.read(c, registries, dimensions);
            sd.trackNetworks.put(graph.id, graph);
        });
        NBTHelper.iterateCompoundList((ListTag)nbt.getList("SignalBlocks", 10), c -> {
            SignalEdgeGroup group = SignalEdgeGroup.read(c);
            sd.signalEdgeGroups.put(group.id, group);
        });
        NBTHelper.iterateCompoundList((ListTag)nbt.getList("Trains", 10), c -> {
            Train train = Train.read(c, registries, sd.trackNetworks, dimensions);
            sd.trains.put(train.id, train);
        });
        for (TrackGraph graph : sd.trackNetworks.values()) {
            for (SignalBoundary signal : graph.getPoints(EdgePointType.SIGNAL)) {
                UUID groupId = (UUID)signal.groups.getFirst();
                UUID otherGroupId = (UUID)signal.groups.getSecond();
                if (groupId == null || otherGroupId == null) continue;
                SignalEdgeGroup group = sd.signalEdgeGroups.get(groupId);
                SignalEdgeGroup otherGroup = sd.signalEdgeGroups.get(otherGroupId);
                if (group == null || otherGroup == null) continue;
                group.putAdjacent(otherGroupId);
                otherGroup.putAdjacent(groupId);
            }
        }
        return sd;
    }

    public Map<UUID, TrackGraph> getTrackNetworks() {
        return this.trackNetworks;
    }

    public Map<UUID, Train> getTrains() {
        return this.trains;
    }

    public Map<UUID, SignalEdgeGroup> getSignalBlocks() {
        return this.signalEdgeGroups;
    }

    private RailwaySavedData() {
    }

    public static RailwaySavedData load(MinecraftServer server) {
        return (RailwaySavedData)server.overworld().getDataStorage().computeIfAbsent(RailwaySavedData.factory(), "create_tracks");
    }
}
