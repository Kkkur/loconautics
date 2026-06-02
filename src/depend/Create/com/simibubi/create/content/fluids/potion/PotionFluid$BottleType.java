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
package com.simibubi.create.content.fluids.potion;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public static enum PotionFluid.BottleType implements StringRepresentable
{
    REGULAR,
    SPLASH,
    LINGERING;

    public static final Codec<PotionFluid.BottleType> CODEC;
    public static final StreamCodec<ByteBuf, PotionFluid.BottleType> STREAM_CODEC;

    @NotNull
    public String getSerializedName() {
        return Lang.asId((String)this.name());
    }

    static {
        CODEC = StringRepresentable.fromEnum(PotionFluid.BottleType::values);
        STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(PotionFluid.BottleType.class);
    }
}
