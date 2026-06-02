/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains;

import com.simibubi.create.CreateClient;
import com.simibubi.create.content.kinetics.KineticDebugger;
import com.simibubi.create.content.trains.RailwaySavedData;
import com.simibubi.create.content.trains.display.GlobalTrainDisplayData;
import com.simibubi.create.content.trains.entity.AddTrainPacket;
import com.simibubi.create.content.trains.entity.RemoveTrainPacket;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackGraphSync;
import com.simibubi.create.content.trains.graph.TrackGraphVisualizer;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.signal.EdgeGroupColor;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

public class GlobalRailwayManager {
    public Map<UUID, TrackGraph> trackNetworks;
    public Map<UUID, SignalEdgeGroup> signalEdgeGroups;
    public Map<UUID, Train> trains;
    public TrackGraphSync sync;
    private List<Train> movingTrains;
    private List<Train> waitingTrains;
    private RailwaySavedData savedData;
    public int version;

    public GlobalRailwayManager() {
        this.cleanUp();
    }

    public void playerLogin(Player player) {
        if (player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            this.loadTrackData(serverPlayer.getServer());
            for (TrackGraph g : this.trackNetworks.values()) {
                this.sync.sendFullGraphTo(g, serverPlayer);
            }
            ArrayList<UUID> ids = new ArrayList<UUID>(this.signalEdgeGroups.size());
            ArrayList<EdgeGroupColor> colors = new ArrayList<EdgeGroupColor>(this.signalEdgeGroups.size());
            for (SignalEdgeGroup group : this.signalEdgeGroups.values()) {
                ids.add(group.id);
                colors.add(group.color);
            }
            this.sync.sendEdgeGroups(ids, colors, serverPlayer);
            for (Train train : this.trains.values()) {
                CatnipServices.NETWORK.sendToClient(serverPlayer, (CustomPacketPayload)new AddTrainPacket(train));
            }
        }
    }

    public void playerLogout(Player player) {
    }

    public void levelLoaded(LevelAccessor level) {
        MinecraftServer server = level.getServer();
        if (server == null || server.overworld() != level) {
            return;
        }
        this.cleanUp();
        this.savedData = null;
        this.loadTrackData(server);
    }

    private void loadTrackData(MinecraftServer server) {
        if (this.savedData != null) {
            return;
        }
        this.savedData = RailwaySavedData.load(server);
        this.trains = this.savedData.getTrains();
        this.trackNetworks = this.savedData.getTrackNetworks();
        this.signalEdgeGroups = this.savedData.getSignalBlocks();
        this.movingTrains.addAll(this.trains.values());
    }

    public void cleanUp() {
        this.trackNetworks = new HashMap<UUID, TrackGraph>();
        this.signalEdgeGroups = new HashMap<UUID, SignalEdgeGroup>();
        this.trains = new HashMap<UUID, Train>();
        this.sync = new TrackGraphSync();
        this.movingTrains = new LinkedList<Train>();
        this.waitingTrains = new LinkedList<Train>();
        GlobalTrainDisplayData.statusByDestination.clear();
    }

    public void markTracksDirty() {
        if (this.savedData != null) {
            this.savedData.setDirty();
        }
    }

    public void addTrain(Train train) {
        this.trains.put(train.id, train);
        this.movingTrains.add(train);
    }

    public void removeTrain(UUID id) {
        Train removed = this.trains.remove(id);
        if (removed == null) {
            return;
        }
        this.movingTrains.remove(removed);
        this.waitingTrains.remove(removed);
    }

    public TrackGraph getOrCreateGraph(UUID graphID, int netId) {
        return this.trackNetworks.computeIfAbsent(graphID, uid -> {
            TrackGraph trackGraph = new TrackGraph(graphID);
            trackGraph.setNetId(netId);
            return trackGraph;
        });
    }

    public void putGraphWithDefaultGroup(TrackGraph graph) {
        SignalEdgeGroup group = new SignalEdgeGroup(graph.id);
        this.signalEdgeGroups.put(graph.id, group.asFallback());
        this.sync.edgeGroupCreated(graph.id, group.color);
        this.putGraph(graph);
    }

    public void putGraph(TrackGraph graph) {
        this.trackNetworks.put(graph.id, graph);
        this.markTracksDirty();
    }

    public void removeGraphAndGroup(TrackGraph graph) {
        this.signalEdgeGroups.remove(graph.id);
        this.sync.edgeGroupRemoved(graph.id);
        this.removeGraph(graph);
    }

    public void removeGraph(TrackGraph graph) {
        this.trackNetworks.remove(graph.id);
        this.markTracksDirty();
    }

    public void updateSplitGraph(LevelAccessor level, TrackGraph graph) {
        Set<TrackGraph> disconnected = graph.findDisconnectedGraphs(level, null);
        for (TrackGraph d : disconnected) {
            this.putGraphWithDefaultGroup(d);
        }
        if (!disconnected.isEmpty()) {
            this.sync.graphSplit(graph, disconnected);
            this.markTracksDirty();
        }
    }

    @Nullable
    public TrackGraph getGraph(LevelAccessor level, TrackNodeLocation vertex) {
        if (this.trackNetworks == null) {
            return null;
        }
        for (TrackGraph railGraph : this.trackNetworks.values()) {
            if (railGraph.locateNode(vertex) == null) continue;
            return railGraph;
        }
        return null;
    }

    public List<TrackGraph> getGraphs(LevelAccessor level, TrackNodeLocation vertex) {
        if (this.trackNetworks == null) {
            return Collections.emptyList();
        }
        ArrayList<TrackGraph> intersecting = new ArrayList<TrackGraph>();
        for (TrackGraph railGraph : this.trackNetworks.values()) {
            if (railGraph.locateNode(vertex) == null) continue;
            intersecting.add(railGraph);
        }
        return intersecting;
    }

    public void tick(Level level) {
        if (level.dimension() != Level.OVERWORLD) {
            return;
        }
        for (SignalEdgeGroup group : this.signalEdgeGroups.values()) {
            group.trains.clear();
            group.reserved = null;
        }
        for (TrackGraph graph : this.trackNetworks.values()) {
            graph.tickPoints(true);
            graph.resolveIntersectingEdgeGroups(level);
        }
        this.tickTrains(level);
        for (TrackGraph graph : this.trackNetworks.values()) {
            graph.tickPoints(false);
        }
        boolean bl = GlobalTrainDisplayData.updateTick = level.getGameTime() % 100L == 0L;
        if (GlobalTrainDisplayData.updateTick) {
            GlobalTrainDisplayData.refresh();
        }
    }

    private void tickTrains(Level level) {
        Train train2;
        for (Train train2 : this.waitingTrains) {
            train2.earlyTick(level);
        }
        for (Train train2 : this.movingTrains) {
            train2.earlyTick(level);
        }
        for (Train train2 : this.waitingTrains) {
            train2.tick(level);
        }
        for (Train train2 : this.movingTrains) {
            train2.tick(level);
        }
        Iterator<Train> iterator = this.waitingTrains.iterator();
        while (iterator.hasNext()) {
            train2 = iterator.next();
            if (train2.invalid) {
                iterator.remove();
                this.trains.remove(train2.id);
                CatnipServices.NETWORK.sendToAllClients((CustomPacketPayload)new RemoveTrainPacket(train2));
                continue;
            }
            if (train2.navigation.waitingForSignal != null) continue;
            this.movingTrains.add(train2);
            iterator.remove();
        }
        iterator = this.movingTrains.iterator();
        while (iterator.hasNext()) {
            train2 = iterator.next();
            if (train2.invalid) {
                iterator.remove();
                this.trains.remove(train2.id);
                CatnipServices.NETWORK.sendToAllClients((CustomPacketPayload)new RemoveTrainPacket(train2));
                continue;
            }
            if (train2.navigation.waitingForSignal == null) continue;
            this.waitingTrains.add(train2);
            iterator.remove();
        }
    }

    public void tickSignalOverlay() {
        if (!GlobalRailwayManager.isTrackGraphDebugActive()) {
            for (TrackGraph trackGraph : this.trackNetworks.values()) {
                TrackGraphVisualizer.visualiseSignalEdgeGroups(trackGraph);
            }
        }
    }

    public void clientTick() {
        if (GlobalRailwayManager.isTrackGraphDebugActive()) {
            for (TrackGraph trackGraph : this.trackNetworks.values()) {
                TrackGraphVisualizer.debugViewGraph(trackGraph, GlobalRailwayManager.isTrackGraphDebugExtended());
            }
        }
    }

    private static boolean isTrackGraphDebugActive() {
        return KineticDebugger.isF3DebugModeActive() && (Boolean)AllConfigs.client().showTrackGraphOnF3.get() != false;
    }

    private static boolean isTrackGraphDebugExtended() {
        return (Boolean)AllConfigs.client().showExtendedTrackGraphOnF3.get();
    }

    public GlobalRailwayManager sided(LevelAccessor level) {
        if (level != null && !level.isClientSide()) {
            return this;
        }
        MutableObject m = new MutableObject();
        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.clientManager((MutableObject<GlobalRailwayManager>)m));
        return (GlobalRailwayManager)m.getValue();
    }

    @OnlyIn(value=Dist.CLIENT)
    private void clientManager(MutableObject<GlobalRailwayManager> m) {
        m.setValue((Object)CreateClient.RAILWAYS);
    }
}
