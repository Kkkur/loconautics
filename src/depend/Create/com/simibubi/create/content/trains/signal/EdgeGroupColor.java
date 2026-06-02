/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.network.codec.StreamCodec
 */
package com.simibubi.create.content.trains.signal;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.theme.Color;
import net.minecraft.network.codec.StreamCodec;

public enum EdgeGroupColor {
    YELLOW(15450709),
    GREEN(5357652),
    BLUE(5476833),
    ORANGE(0xE36E36),
    LAVENDER(13341370),
    RED(10761528),
    CYAN(7264985),
    BROWN(10583128),
    WHITE(15065564);

    public static final StreamCodec<ByteBuf, EdgeGroupColor> STREAM_CODEC;
    private final Color color;
    private final int mask;

    private EdgeGroupColor(int color) {
        this.color = new Color(color);
        this.mask = 1 << this.ordinal();
    }

    public int strikeFrom(int mask) {
        if (this == WHITE) {
            return mask;
        }
        return mask | this.mask;
    }

    public Color get() {
        return this.color;
    }

    public static EdgeGroupColor getDefault() {
        return EdgeGroupColor.values()[0];
    }

    public static EdgeGroupColor findNextAvailable(int mask) {
        EdgeGroupColor[] values;
        for (EdgeGroupColor value : values = EdgeGroupColor.values()) {
            if ((mask & 1) == 0) {
                return value;
            }
            mask >>= 1;
        }
        return WHITE;
    }

    static {
        STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(EdgeGroupColor.class);
    }
}
