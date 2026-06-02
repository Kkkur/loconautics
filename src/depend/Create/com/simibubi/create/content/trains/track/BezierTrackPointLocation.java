/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 */
package com.simibubi.create.content.trains.track;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record BezierTrackPointLocation(BlockPos curveTarget, int segment) {
    public static final Codec<BezierTrackPointLocation> CODEC = RecordCodecBuilder.create(i -> i.group((App)BlockPos.CODEC.fieldOf("curve_target").forGetter(BezierTrackPointLocation::curveTarget), (App)Codec.INT.fieldOf("segment").forGetter(BezierTrackPointLocation::segment)).apply((Applicative)i, BezierTrackPointLocation::new));
    public static final StreamCodec<ByteBuf, BezierTrackPointLocation> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, BezierTrackPointLocation::curveTarget, (StreamCodec)ByteBufCodecs.INT, BezierTrackPointLocation::segment, BezierTrackPointLocation::new);
}
