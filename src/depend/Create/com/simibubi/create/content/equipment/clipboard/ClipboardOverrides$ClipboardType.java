/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.StringRepresentable
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.equipment.clipboard;

import com.mojang.serialization.Codec;
import com.simibubi.create.Create;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public static enum ClipboardOverrides.ClipboardType implements StringRepresentable
{
    EMPTY("empty_clipboard"),
    WRITTEN("clipboard"),
    EDITING("clipboard_and_quill");

    public static final Codec<ClipboardOverrides.ClipboardType> CODEC;
    public static final StreamCodec<ByteBuf, ClipboardOverrides.ClipboardType> STREAM_CODEC;
    public final String file;
    public static ResourceLocation ID;

    private ClipboardOverrides.ClipboardType(String file) {
        this.file = file;
    }

    @NotNull
    public String getSerializedName() {
        return Lang.asId((String)this.name());
    }

    static {
        CODEC = StringRepresentable.fromValues(ClipboardOverrides.ClipboardType::values);
        STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(ClipboardOverrides.ClipboardType.class);
        ID = Create.asResource("clipboard_type");
    }
}
