/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.util.StringRepresentable
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.logistics.filter;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum AttributeFilterWhitelistMode implements StringRepresentable
{
    WHITELIST_DISJ,
    WHITELIST_CONJ,
    BLACKLIST;

    public static final Codec<AttributeFilterWhitelistMode> CODEC;
    public static final StreamCodec<ByteBuf, AttributeFilterWhitelistMode> STREAM_CODEC;

    @NotNull
    public String getSerializedName() {
        return Lang.asId((String)this.name());
    }

    static {
        CODEC = StringRepresentable.fromValues(AttributeFilterWhitelistMode::values);
        STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(AttributeFilterWhitelistMode.class);
    }
}
