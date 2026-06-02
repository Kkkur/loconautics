/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.minecraft.network.codec.StreamCodec
 */
package com.simibubi.create.content.schematics.cannon;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.network.codec.StreamCodec;

public static enum ConfigureSchematicannonPacket.Option {
    DONT_REPLACE,
    REPLACE_SOLID,
    REPLACE_ANY,
    REPLACE_EMPTY,
    SKIP_MISSING,
    SKIP_BLOCK_ENTITIES,
    PLAY,
    PAUSE,
    STOP;

    public static final StreamCodec<ByteBuf, ConfigureSchematicannonPacket.Option> STREAM_CODEC;

    static {
        STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(ConfigureSchematicannonPacket.Option.class);
    }
}
