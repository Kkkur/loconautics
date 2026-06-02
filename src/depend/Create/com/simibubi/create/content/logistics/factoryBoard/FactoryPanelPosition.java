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
 */
package com.simibubi.create.content.logistics.factoryBoard;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;

public record FactoryPanelPosition(BlockPos pos, FactoryPanelBlock.PanelSlot slot) {
    public static final Codec<FactoryPanelPosition> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockPos.CODEC.fieldOf("pos").forGetter(FactoryPanelPosition::pos), (App)FactoryPanelBlock.PanelSlot.CODEC.fieldOf("slot").forGetter(FactoryPanelPosition::slot)).apply((Applicative)instance, FactoryPanelPosition::new));
    public static final StreamCodec<ByteBuf, FactoryPanelPosition> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, FactoryPanelPosition::pos, FactoryPanelBlock.PanelSlot.STREAM_CODEC, FactoryPanelPosition::slot, FactoryPanelPosition::new);
}
