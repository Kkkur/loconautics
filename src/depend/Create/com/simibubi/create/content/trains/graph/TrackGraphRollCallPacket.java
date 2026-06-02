/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.trains.graph;

import com.simibubi.create.AllPackets;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackGraphRequestPacket;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record TrackGraphRollCallPacket(List<Entry> entries) implements ClientboundPacketPayload
{
    public static final StreamCodec<ByteBuf, TrackGraphRollCallPacket> STREAM_CODEC = CatnipStreamCodecBuilders.list(Entry.STREAM_CODEC).map(TrackGraphRollCallPacket::new, TrackGraphRollCallPacket::entries);

    public static TrackGraphRollCallPacket ofServer() {
        ArrayList<Entry> entries = new ArrayList<Entry>();
        for (TrackGraph graph : Create.RAILWAYS.trackNetworks.values()) {
            entries.add(new Entry(graph.netId, graph.getChecksum()));
        }
        return new TrackGraphRollCallPacket(entries);
    }

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        GlobalRailwayManager manager = Create.RAILWAYS.sided(null);
        HashSet<UUID> unusedIds = new HashSet<UUID>(manager.trackNetworks.keySet());
        ArrayList<Integer> failedIds = new ArrayList<Integer>();
        HashMap idByNetId = new HashMap();
        manager.trackNetworks.forEach((uuid, g) -> idByNetId.put(g.netId, uuid));
        for (Entry entry : this.entries) {
            UUID uuid2 = (UUID)idByNetId.get(entry.netId);
            if (uuid2 == null) {
                failedIds.add(entry.netId);
                continue;
            }
            unusedIds.remove(uuid2);
            TrackGraph trackGraph = manager.trackNetworks.get(uuid2);
            if (trackGraph.getChecksum() == entry.checksum) continue;
            Create.LOGGER.warn("Track network: {} failed its checksum; Requesting refresh", (Object)uuid2.toString().substring(0, 6));
            failedIds.add(entry.netId);
        }
        for (Integer failed : failedIds) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new TrackGraphRequestPacket(failed));
        }
        for (UUID unused : unusedIds) {
            manager.trackNetworks.remove(unused);
        }
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.TRACK_GRAPH_ROLL_CALL;
    }

    public record Entry(int netId, int checksum) {
        public static final StreamCodec<ByteBuf, Entry> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.VAR_INT, Entry::netId, (StreamCodec)ByteBufCodecs.INT, Entry::checksum, Entry::new);
    }
}
