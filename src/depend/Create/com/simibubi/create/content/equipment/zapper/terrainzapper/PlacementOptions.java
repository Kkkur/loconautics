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
package com.simibubi.create.content.equipment.zapper.terrainzapper;

import com.mojang.serialization.Codec;
import com.simibubi.create.foundation.gui.AllIcons;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum PlacementOptions implements StringRepresentable
{
    Merged(AllIcons.I_CENTERED),
    Attached(AllIcons.I_ATTACHED),
    Inserted(AllIcons.I_INSERTED);

    public static final Codec<PlacementOptions> CODEC;
    public static final StreamCodec<ByteBuf, PlacementOptions> STREAM_CODEC;
    public final String translationKey = Lang.asId((String)this.name());
    public final AllIcons icon;

    private PlacementOptions(AllIcons icon) {
        this.icon = icon;
    }

    @NotNull
    public String getSerializedName() {
        return Lang.asId((String)this.name());
    }

    static {
        CODEC = StringRepresentable.fromValues(PlacementOptions::values);
        STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(PlacementOptions.class);
    }
}
