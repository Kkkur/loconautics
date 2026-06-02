/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.minecraft.network.codec.StreamCodec
 */
package com.simibubi.create.content.logistics.filter;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.network.codec.StreamCodec;

public static enum FilterScreenPacket.Option {
    WHITELIST,
    WHITELIST2,
    BLACKLIST,
    RESPECT_DATA,
    IGNORE_DATA,
    UPDATE_FILTER_ITEM,
    ADD_TAG,
    ADD_INVERTED_TAG,
    UPDATE_ADDRESS;

    public static final StreamCodec<ByteBuf, FilterScreenPacket.Option> STREAM_CODEC;

    static {
        STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(FilterScreenPacket.Option.class);
    }
}
