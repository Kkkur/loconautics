/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.minecraft.network.codec.StreamCodec
 */
package com.simibubi.create.compat.trainmap;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.network.codec.StreamCodec;

public static enum TrainMapSync.SignalState {
    NOT_WAITING,
    WAITING_FOR_REDSTONE,
    BLOCK_SIGNAL,
    CHAIN_SIGNAL;

    public static final StreamCodec<ByteBuf, TrainMapSync.SignalState> STREAM_CODEC;

    static {
        STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(TrainMapSync.SignalState.class);
    }
}
