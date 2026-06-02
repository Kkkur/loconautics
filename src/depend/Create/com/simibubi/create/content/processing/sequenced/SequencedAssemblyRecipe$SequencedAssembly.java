/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create.content.processing.sequenced;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record SequencedAssemblyRecipe.SequencedAssembly(ResourceLocation id, int step, float progress) {
    public static final Codec<SequencedAssemblyRecipe.SequencedAssembly> CODEC = RecordCodecBuilder.create(i -> i.group((App)ResourceLocation.CODEC.fieldOf("id").forGetter(SequencedAssemblyRecipe.SequencedAssembly::id), (App)Codec.INT.fieldOf("step").forGetter(SequencedAssemblyRecipe.SequencedAssembly::step), (App)Codec.FLOAT.fieldOf("progress").forGetter(SequencedAssemblyRecipe.SequencedAssembly::progress)).apply((Applicative)i, SequencedAssemblyRecipe.SequencedAssembly::new));
    public static final StreamCodec<ByteBuf, SequencedAssemblyRecipe.SequencedAssembly> STREAM_CODEC = StreamCodec.composite((StreamCodec)ResourceLocation.STREAM_CODEC, SequencedAssemblyRecipe.SequencedAssembly::id, (StreamCodec)ByteBufCodecs.INT, SequencedAssemblyRecipe.SequencedAssembly::step, (StreamCodec)ByteBufCodecs.FLOAT, SequencedAssemblyRecipe.SequencedAssembly::progress, SequencedAssemblyRecipe.SequencedAssembly::new);
}
