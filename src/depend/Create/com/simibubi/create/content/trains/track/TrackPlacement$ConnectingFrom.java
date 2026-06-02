/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.track;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public record TrackPlacement.ConnectingFrom(BlockPos pos, Vec3 axis, Vec3 normal, Vec3 end) {
    public static final Codec<TrackPlacement.ConnectingFrom> CODEC = RecordCodecBuilder.create(i -> i.group((App)BlockPos.CODEC.fieldOf("pos").forGetter(TrackPlacement.ConnectingFrom::pos), (App)Vec3.CODEC.fieldOf("axis").forGetter(TrackPlacement.ConnectingFrom::axis), (App)Vec3.CODEC.fieldOf("normal").forGetter(TrackPlacement.ConnectingFrom::normal), (App)Vec3.CODEC.fieldOf("end").forGetter(TrackPlacement.ConnectingFrom::end)).apply((Applicative)i, TrackPlacement.ConnectingFrom::new));
    public static final StreamCodec<ByteBuf, TrackPlacement.ConnectingFrom> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, TrackPlacement.ConnectingFrom::pos, (StreamCodec)CatnipStreamCodecs.VEC3, TrackPlacement.ConnectingFrom::axis, (StreamCodec)CatnipStreamCodecs.VEC3, TrackPlacement.ConnectingFrom::normal, (StreamCodec)CatnipStreamCodecs.VEC3, TrackPlacement.ConnectingFrom::end, TrackPlacement.ConnectingFrom::new);
}
