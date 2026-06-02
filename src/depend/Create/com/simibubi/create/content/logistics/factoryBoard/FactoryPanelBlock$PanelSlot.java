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
package com.simibubi.create.content.logistics.factoryBoard;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public static enum FactoryPanelBlock.PanelSlot implements StringRepresentable
{
    TOP_LEFT(1, 1),
    TOP_RIGHT(0, 1),
    BOTTOM_LEFT(1, 0),
    BOTTOM_RIGHT(0, 0);

    public static final Codec<FactoryPanelBlock.PanelSlot> CODEC;
    public static final StreamCodec<ByteBuf, FactoryPanelBlock.PanelSlot> STREAM_CODEC;
    public final int xOffset;
    public final int yOffset;

    private FactoryPanelBlock.PanelSlot(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @NotNull
    public String getSerializedName() {
        return Lang.asId((String)this.name());
    }

    static {
        CODEC = StringRepresentable.fromValues(FactoryPanelBlock.PanelSlot::values);
        STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(FactoryPanelBlock.PanelSlot.class);
    }
}
