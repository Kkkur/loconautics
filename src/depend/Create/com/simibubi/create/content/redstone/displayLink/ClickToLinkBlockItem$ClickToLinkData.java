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
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create.content.redstone.displayLink;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ClickToLinkBlockItem.ClickToLinkData(BlockPos selectedPos, ResourceLocation selectedDim) {
    public static final Codec<ClickToLinkBlockItem.ClickToLinkData> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockPos.CODEC.fieldOf("selected_pos").forGetter(ClickToLinkBlockItem.ClickToLinkData::selectedPos), (App)ResourceLocation.CODEC.fieldOf("selected_dim").forGetter(ClickToLinkBlockItem.ClickToLinkData::selectedDim)).apply((Applicative)instance, ClickToLinkBlockItem.ClickToLinkData::new));
    public static final StreamCodec<ByteBuf, ClickToLinkBlockItem.ClickToLinkData> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, ClickToLinkBlockItem.ClickToLinkData::selectedPos, (StreamCodec)ResourceLocation.STREAM_CODEC, ClickToLinkBlockItem.ClickToLinkData::selectedDim, ClickToLinkBlockItem.ClickToLinkData::new);
}
