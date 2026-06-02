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
package com.simibubi.create.content.processing.recipe;

import com.mojang.serialization.Codec;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum HeatCondition implements StringRepresentable
{
    NONE(0xFFFFFF),
    HEATED(15237888),
    SUPERHEATED(6067176);

    private int color;
    public static final Codec<HeatCondition> CODEC;
    public static final StreamCodec<ByteBuf, HeatCondition> STREAM_CODEC;

    private HeatCondition(int color) {
        this.color = color;
    }

    public boolean testBlazeBurner(BlazeBurnerBlock.HeatLevel level) {
        if (this == SUPERHEATED) {
            return level == BlazeBurnerBlock.HeatLevel.SEETHING;
        }
        if (this == HEATED) {
            return level != BlazeBurnerBlock.HeatLevel.NONE && level != BlazeBurnerBlock.HeatLevel.SMOULDERING;
        }
        return true;
    }

    public BlazeBurnerBlock.HeatLevel visualizeAsBlazeBurner() {
        if (this == SUPERHEATED) {
            return BlazeBurnerBlock.HeatLevel.SEETHING;
        }
        if (this == HEATED) {
            return BlazeBurnerBlock.HeatLevel.KINDLED;
        }
        return BlazeBurnerBlock.HeatLevel.NONE;
    }

    @NotNull
    public String getSerializedName() {
        return Lang.asId((String)this.name());
    }

    public String getTranslationKey() {
        return "recipe.heat_requirement." + this.getSerializedName();
    }

    public int getColor() {
        return this.color;
    }

    static {
        CODEC = StringRepresentable.fromEnum(HeatCondition::values);
        STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(HeatCondition.class);
    }
}
