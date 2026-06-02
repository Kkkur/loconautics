/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  foundry.veil.api.network.VeilPacketManager
 *  foundry.veil.api.network.VeilPacketManager$PacketSink
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.game.ClientboundBundlePacket
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.ChunkPos
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector2i
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package dev.ryanhcode.sable.sublevel.system;

import dev.ryanhcode.sable.SableConfig;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelObserver;
import dev.ryanhcode.sable.api.sublevel.SubLevelTrackingPlugin;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.network.packets.ClientboundSableSnapshotDualPacket;
import dev.ryanhcode.sable.network.packets.ClientboundSableSnapshotInfoDualPacket;
import dev.ryanhcode.sable.network.packets.tcp.ClientboundChangeBoundsSubLevelPacket;
import dev.ryanhcode.sable.network.packets.tcp.ClientboundFinalizeSubLevelPacket;
import dev.ryanhcode.sable.network.packets.tcp.ClientboundRecentlySplitSubLevelPacket;
import dev.ryanhcode.sable.network.packets.tcp.ClientboundStartTrackingSubLevelPacket;
import dev.ryanhcode.sable.network.packets.tcp.ClientboundStopMovingSubLevelPacket;
import dev.ryanhcode.sable.network.packets.tcp.ClientboundStopTrackingSubLevelPacket;
import dev.ryanhcode.sable.network.udp.SableUDPServer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder;
import dev.ryanhcode.sable.sublevel.plot.ServerLevelPlot;
import dev.ryanhcode.sable.sublevel.plot.SubLevelPlayerChunkSender;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import foundry.veil.api.network.VeilPacketManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class SubLevelTrackingSystem
implements SubLevelObserver {
    private final ServerLevel level;
    private final List<SubLevel> additionQueue = new ObjectArrayList();
    private final Set<UUID> currentlyUpdatingPlayers = new ObjectOpenHashSet();
    private final Set<UUID> pluginNeededPlayers = new ObjectOpenHashSet();
    private final List<SubLevelTrackingPlugin> plugins = new ObjectArrayList();
    private int interpolationTick;
    private long lastSendMs = -1L;

    public SubLevelTrackingSystem(ServerLevel level) {
        this.level = level;
    }

    private static long getSubLevelLong(ServerSubLevel subLevel, SubLevelContainer subLevels) {
        Vector2i origin = subLevels.getOrigin();
        ChunkPos plotPos = subLevel.getPlot().plotPos;
        return ChunkPos.asLong((int)(plotPos.x - origin.x), (int)(plotPos.z - origin.y));
    }

    private boolean shouldLoad(Player player, Vector3dc entityPosition) {
        double trackingRange = SableConfig.SUB_LEVEL_TRACKING_RANGE.getAsDouble();
        return entityPosition.distanceSquared(player.getX(), player.getY(), player.getZ()) < trackingRange * trackingRange;
    }

    @Override
    public void onSubLevelAdded(SubLevel subLevel) {
        this.additionQueue.add(subLevel);
    }

    @Override
    public void onSubLevelRemoved(SubLevel subLevel, SubLevelRemovalReason reason) {
        this.additionQueue.remove(subLevel);
        ServerSubLevel serverSubLevel = (ServerSubLevel)subLevel;
        this.sendRemoval(this.serverWidePlayerSink(serverSubLevel), serverSubLevel);
    }

    public VeilPacketManager.PacketSink serverWidePlayerSink(ServerSubLevel serverSubLevel) {
        return packet -> {
            for (UUID uuid : serverSubLevel.getTrackingPlayers()) {
                ServerPlayer player = this.level.getServer().getPlayerList().getPlayer(uuid);
                if (!(player instanceof ServerPlayer)) continue;
                player.connection.send(packet);
            }
        };
    }

    private void collectPlayers(Vector3d position, Collection<UUID> tracking) {
        for (ServerPlayer player : this.level.players()) {
            if (!this.shouldLoad((Player)player, (Vector3dc)position)) continue;
            tracking.add(player.getGameProfile().getId());
        }
    }

    private void sendFullSync(ServerPlayer player, ServerSubLevel subLevel, @Nullable CustomPacketPayload extraPacket) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(this.level);
        assert (container != null);
        long l = SubLevelTrackingSystem.getSubLevelLong(subLevel, container);
        ServerLevelPlot plot = subLevel.getPlot();
        Collection<PlotChunkHolder> chunks = plot.getLoadedChunks();
        ObjectArrayList packets = new ObjectArrayList(3 + chunks.size());
        packets.add((Object)new ClientboundCustomPayloadPacket((CustomPacketPayload)new ClientboundStartTrackingSubLevelPacket(l, subLevel.getUniqueId(), subLevel.lastPose(), subLevel.logicalPose(), plot.getBoundingBox(), subLevel.getName(), this.interpolationTick)));
        if (extraPacket != null) {
            packets.add((Object)new ClientboundCustomPayloadPacket(extraPacket));
        }
        for (PlotChunkHolder chunk : chunks) {
            SubLevelPlayerChunkSender.sendChunk(arg_0 -> ((ObjectList)packets).add(arg_0), ((LevelPlot)plot).getLightEngine(), chunk.getChunk());
        }
        packets.add((Object)new ClientboundCustomPayloadPacket((CustomPacketPayload)new ClientboundFinalizeSubLevelPacket(l)));
        player.connection.send((Packet)new ClientboundBundlePacket((Iterable)packets));
        for (PlotChunkHolder chunk : chunks) {
            SubLevelPlayerChunkSender.sendChunkPoiData(this.level, chunk.getChunk());
        }
    }

    private void sendRemoval(VeilPacketManager.PacketSink sink, ServerSubLevel subLevel) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(this.level);
        assert (container != null);
        long l = SubLevelTrackingSystem.getSubLevelLong(subLevel, container);
        sink.sendPacket(new CustomPacketPayload[]{new ClientboundStopTrackingSubLevelPacket(l)});
    }

    @Override
    public void tick(SubLevelContainer container) {
        Collection<UUID> tracking;
        ServerSubLevel serverSubLevel;
        for (SubLevel subLevel : this.additionQueue) {
            if (subLevel.isRemoved()) continue;
            serverSubLevel = (ServerSubLevel)subLevel;
            tracking = serverSubLevel.getTrackingPlayers();
            Vector3d position = subLevel.logicalPose().position();
            this.collectPlayers(position, tracking);
            UUID splitFromSubLevelID = serverSubLevel.getSplitFromSubLevel();
            SubLevel splitFromSubLevel = splitFromSubLevelID != null ? container.getSubLevel(splitFromSubLevelID) : null;
            for (UUID uuid : tracking) {
                ServerPlayer player = (ServerPlayer)this.level.getPlayerByUUID(uuid);
                if (player == null) {
                    throw new IllegalStateException("Player not found immediately after tracking initializes");
                }
                ClientboundRecentlySplitSubLevelPacket extraPacket = null;
                if (splitFromSubLevelID != null && splitFromSubLevel != null) {
                    extraPacket = new ClientboundRecentlySplitSubLevelPacket(serverSubLevel.getUniqueId(), splitFromSubLevel.getUniqueId(), serverSubLevel.getSplitFromPose());
                }
                this.sendFullSync(player, serverSubLevel, extraPacket);
            }
            serverSubLevel.clearSplitFrom();
        }
        this.additionQueue.clear();
        for (SubLevel subLevel : container.getAllSubLevels()) {
            if (subLevel.isRemoved()) continue;
            serverSubLevel = (ServerSubLevel)subLevel;
            tracking = serverSubLevel.getTrackingPlayers();
            Vector3d entityPos = subLevel.logicalPose().position();
            Iterator<UUID> iter = tracking.iterator();
            while (iter.hasNext()) {
                UUID uuid = iter.next();
                ServerPlayer player = (ServerPlayer)this.level.getPlayerByUUID(uuid);
                if (player == null) {
                    ServerPlayer serverWidePlayer = this.level.getServer().getPlayerList().getPlayer(uuid);
                    if (serverWidePlayer != null) {
                        this.sendRemoval(VeilPacketManager.player((ServerPlayer)serverWidePlayer), serverSubLevel);
                    }
                    iter.remove();
                    continue;
                }
                if (this.shouldLoad((Player)player, (Vector3dc)entityPos)) continue;
                this.sendRemoval(VeilPacketManager.player((ServerPlayer)player), serverSubLevel);
                iter.remove();
            }
            for (ServerPlayer player : this.level.players()) {
                UUID uuid;
                uuid = player.getGameProfile().getId();
                if (!this.shouldLoad((Player)player, (Vector3dc)entityPos) || tracking.contains(uuid)) continue;
                tracking.add(uuid);
                this.sendFullSync(player, serverSubLevel, null);
            }
        }
        this.sendBoundsUpdates(container);
        this.sendMovementUpdates(container);
    }

    private void sendBoundsUpdates(SubLevelContainer container) {
        for (SubLevel subLevel : container.getAllSubLevels()) {
            BoundingBox3i lastNetworkedBounds;
            ServerSubLevel serverSubLevel;
            BoundingBox3ic plotBounds;
            if (subLevel.isRemoved() || (plotBounds = (serverSubLevel = (ServerSubLevel)subLevel).getPlot().getBoundingBox()).equals((Object)(lastNetworkedBounds = serverSubLevel.lastNetworkedBoundingBox()))) continue;
            lastNetworkedBounds.set(plotBounds);
            long l = SubLevelTrackingSystem.getSubLevelLong(serverSubLevel, container);
            serverSubLevel.playerSink().sendPacket(new CustomPacketPayload[]{new ClientboundChangeBoundsSubLevelPacket(l, plotBounds)});
        }
    }

    public int getInterpolationTick() {
        return this.interpolationTick;
    }

    private void sendMovementUpdates(SubLevelContainer container) {
        ServerPlayer player;
        UUID uuid;
        Object2ObjectOpenHashMap movementUpdates = new Object2ObjectOpenHashMap();
        for (SubLevel subLevel : container.getAllSubLevels()) {
            if (subLevel.isRemoved()) continue;
            ServerSubLevel serverSubLevel = (ServerSubLevel)subLevel;
            Collection<UUID> tracking = serverSubLevel.getTrackingPlayers();
            SubLevelUpdateTicket.UpdateTicketType type = SubLevelUpdateTicket.UpdateTicketType.MOVE;
            if (!serverSubLevel.logicalPose().withinTolerance(serverSubLevel.lastNetworkedPose(), 9.375E-4, Math.toRadians(0.015))) {
                serverSubLevel.lastNetworkedPose().set((Pose3dc)serverSubLevel.logicalPose());
                serverSubLevel.setLastNetworkedStopped(false);
            } else {
                if (serverSubLevel.getLastNetworkedStopped()) continue;
                type = SubLevelUpdateTicket.UpdateTicketType.STOP;
                serverSubLevel.setLastNetworkedStopped(true);
            }
            Iterator iterator = tracking.iterator();
            while (iterator.hasNext()) {
                uuid = (UUID)iterator.next();
                player = (ServerPlayer)this.level.getPlayerByUUID(uuid);
                if (player == null) continue;
                List playerUpdates = movementUpdates.computeIfAbsent(uuid, p -> new ArrayList());
                playerUpdates.add(new SubLevelUpdateTicket(serverSubLevel, type));
            }
        }
        long ms = System.currentTimeMillis();
        int msSinceLastSend = this.lastSendMs == -1L ? (int)(1000.0 / (double)this.level.getServer().tickRateManager().tickrate()) : (int)(ms - this.lastSendMs);
        this.lastSendMs = ms;
        this.pluginNeededPlayers.clear();
        for (SubLevelTrackingPlugin plugin : this.plugins) {
            for (UUID neededPlayer : plugin.neededPlayers()) {
                this.pluginNeededPlayers.add(neededPlayer);
            }
        }
        this.currentlyUpdatingPlayers.addAll(movementUpdates.keySet());
        this.currentlyUpdatingPlayers.addAll(this.pluginNeededPlayers);
        Iterator<UUID> currentlyUpdatingIter = this.currentlyUpdatingPlayers.iterator();
        while (currentlyUpdatingIter.hasNext()) {
            UUID uuid2 = currentlyUpdatingIter.next();
            ServerPlayer serverPlayer = (ServerPlayer)this.level.getPlayerByUUID(uuid2);
            if (serverPlayer == null) {
                currentlyUpdatingIter.remove();
                continue;
            }
            if (movementUpdates.containsKey(uuid2)) continue;
            if (this.pluginNeededPlayers.contains(uuid2)) {
                serverPlayer.connection.send((Packet)new ClientboundCustomPayloadPacket((CustomPacketPayload)new ClientboundSableSnapshotInfoDualPacket(msSinceLastSend, this.interpolationTick, false)));
                continue;
            }
            serverPlayer.connection.send((Packet)new ClientboundCustomPayloadPacket((CustomPacketPayload)new ClientboundSableSnapshotInfoDualPacket(msSinceLastSend, this.interpolationTick, true)));
            currentlyUpdatingIter.remove();
        }
        for (SubLevelTrackingPlugin subLevelTrackingPlugin : this.plugins) {
            subLevelTrackingPlugin.sendTrackingData(this.interpolationTick);
        }
        for (Map.Entry entry : movementUpdates.entrySet()) {
            int i;
            Iterator iter;
            uuid = (UUID)entry.getKey();
            player = (ServerPlayer)this.level.getPlayerByUUID(uuid);
            List toUpdate = (List)entry.getValue();
            ObjectArrayList entries = new ObjectArrayList();
            for (SubLevelUpdateTicket ticket : toUpdate) {
                ServerSubLevel serverSubLevel = (ServerSubLevel)ticket.subLevels;
                long l = SubLevelTrackingSystem.getSubLevelLong(serverSubLevel, container);
                switch (ticket.type.ordinal()) {
                    case 0: {
                        player.connection.send((Packet)new ClientboundCustomPayloadPacket((CustomPacketPayload)new ClientboundStopMovingSubLevelPacket(l)));
                        break;
                    }
                    case 1: {
                        Vector3f linearVelocity = new Vector3f((float)serverSubLevel.latestLinearVelocity.x, (float)serverSubLevel.latestLinearVelocity.y, (float)serverSubLevel.latestLinearVelocity.z);
                        Vector3f angularVelocity = new Vector3f((float)serverSubLevel.latestAngularVelocity.x, (float)serverSubLevel.latestAngularVelocity.y, (float)serverSubLevel.latestAngularVelocity.z);
                        entries.add(new ClientboundSableSnapshotDualPacket.Entry(l, serverSubLevel.logicalPose(), (Vector3fc)linearVelocity, (Vector3fc)angularVelocity));
                    }
                }
            }
            int maxBatchSize = 16;
            SableUDPServer udpServer = SableUDPServer.getServer(this.level.getServer());
            if (udpServer != null && udpServer.isConnectedTo(player)) {
                iter = entries.iterator();
                udpServer.sendUDPPacket(player, new ClientboundSableSnapshotInfoDualPacket(msSinceLastSend, this.interpolationTick, false), true);
                while (iter.hasNext()) {
                    ObjectArrayList batch = new ObjectArrayList();
                    for (i = 0; i < 16 && iter.hasNext(); ++i) {
                        batch.add((ClientboundSableSnapshotDualPacket.Entry)iter.next());
                    }
                    udpServer.sendUDPPacket(player, new ClientboundSableSnapshotDualPacket(this.interpolationTick, (List<ClientboundSableSnapshotDualPacket.Entry>)batch), true);
                }
                continue;
            }
            iter = entries.iterator();
            while (iter.hasNext()) {
                ObjectArrayList batch = new ObjectArrayList();
                for (i = 0; i < 16 && iter.hasNext(); ++i) {
                    batch.add((ClientboundSableSnapshotDualPacket.Entry)iter.next());
                }
                player.connection.send((Packet)new ClientboundBundlePacket(List.of(new ClientboundCustomPayloadPacket((CustomPacketPayload)new ClientboundSableSnapshotInfoDualPacket(msSinceLastSend, this.interpolationTick, false)), new ClientboundCustomPayloadPacket((CustomPacketPayload)new ClientboundSableSnapshotDualPacket(this.interpolationTick, (List<ClientboundSableSnapshotDualPacket.Entry>)batch)))));
            }
        }
        ++this.interpolationTick;
    }

    public void addTrackingPlugin(SubLevelTrackingPlugin plugin) {
        if (this.plugins.contains(plugin)) {
            return;
        }
        this.plugins.add(plugin);
    }

    private record SubLevelUpdateTicket(SubLevel subLevels, UpdateTicketType type) {

        private static enum UpdateTicketType {
            STOP,
            MOVE;

        }
    }
}
