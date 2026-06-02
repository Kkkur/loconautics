/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 */
package com.simibubi.create.compat.trainmap;

import com.simibubi.create.AllPackets;
import com.simibubi.create.compat.trainmap.TrainMapSync;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class TrainMapSyncRequestPacket
implements ServerboundPacketPayload {
    public static final TrainMapSyncRequestPacket INSTANCE = new TrainMapSyncRequestPacket();
    public static final StreamCodec<ByteBuf, TrainMapSyncRequestPacket> STREAM_CODEC = StreamCodec.unit((Object)INSTANCE);

    public void handle(ServerPlayer player) {
        TrainMapSync.requestReceived(player);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.TRAIN_MAP_REQUEST;
    }
}
