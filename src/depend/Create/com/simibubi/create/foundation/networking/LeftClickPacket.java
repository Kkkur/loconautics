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
package com.simibubi.create.foundation.networking;

import com.simibubi.create.AllPackets;
import com.simibubi.create.foundation.events.CommonEvents;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public enum LeftClickPacket implements ServerboundPacketPayload
{
    INSTANCE;

    public static final StreamCodec<ByteBuf, LeftClickPacket> STREAM_CODEC;

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.LEFT_CLICK;
    }

    public void handle(ServerPlayer player) {
        CommonEvents.leftClickEmpty(player);
    }

    static {
        STREAM_CODEC = StreamCodec.unit((Object)((Object)INSTANCE));
    }
}
