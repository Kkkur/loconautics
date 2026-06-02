/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 */
package com.simibubi.create.content.trains.graph;

import com.simibubi.create.AllPackets;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.graph.TrackGraph;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public record TrackGraphRequestPacket(int netId) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, TrackGraphRequestPacket> STREAM_CODEC = ByteBufCodecs.INT.map(TrackGraphRequestPacket::new, TrackGraphRequestPacket::netId);

    public void handle(ServerPlayer player) {
        for (TrackGraph trackGraph : Create.RAILWAYS.trackNetworks.values()) {
            if (trackGraph.netId != this.netId) continue;
            Create.RAILWAYS.sync.sendFullGraphTo(trackGraph, player);
            break;
        }
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.TRACK_GRAPH_REQUEST;
    }
}
