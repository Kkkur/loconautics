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
 */
package com.simibubi.create.content.schematics.cannon;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SchematicannonBlockEntity.SchematicannonOptions(int replaceMode, boolean skipMissing, boolean replaceBlockEntities) {
    public static final Codec<SchematicannonBlockEntity.SchematicannonOptions> CODEC = RecordCodecBuilder.create(i -> i.group((App)Codec.INT.fieldOf("replace_mode").forGetter(SchematicannonBlockEntity.SchematicannonOptions::replaceMode), (App)Codec.BOOL.fieldOf("skip_missing").forGetter(SchematicannonBlockEntity.SchematicannonOptions::skipMissing), (App)Codec.BOOL.fieldOf("replace_block_entities").forGetter(SchematicannonBlockEntity.SchematicannonOptions::replaceBlockEntities)).apply((Applicative)i, SchematicannonBlockEntity.SchematicannonOptions::new));
    public static final StreamCodec<ByteBuf, SchematicannonBlockEntity.SchematicannonOptions> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, SchematicannonBlockEntity.SchematicannonOptions::replaceMode, (StreamCodec)ByteBufCodecs.BOOL, SchematicannonBlockEntity.SchematicannonOptions::skipMissing, (StreamCodec)ByteBufCodecs.BOOL, SchematicannonBlockEntity.SchematicannonOptions::replaceBlockEntities, SchematicannonBlockEntity.SchematicannonOptions::new);
}
