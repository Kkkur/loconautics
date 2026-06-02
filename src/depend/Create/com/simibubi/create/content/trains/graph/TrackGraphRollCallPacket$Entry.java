/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 */
package com.simibubi.create.content.trains.graph;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TrackGraphRollCallPacket.Entry(int netId, int checksum) {
    public static final StreamCodec<ByteBuf, TrackGraphRollCallPacket.Entry> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.VAR_INT, TrackGraphRollCallPacket.Entry::netId, (StreamCodec)ByteBufCodecs.INT, TrackGraphRollCallPacket.Entry::checksum, TrackGraphRollCallPacket.Entry::new);
}
