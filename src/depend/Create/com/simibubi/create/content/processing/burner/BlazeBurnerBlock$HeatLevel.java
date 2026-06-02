/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.util.StringRepresentable
 */
package com.simibubi.create.content.processing.burner;

import com.mojang.serialization.Codec;
import net.createmod.catnip.lang.Lang;
import net.minecraft.util.StringRepresentable;

public static enum BlazeBurnerBlock.HeatLevel implements StringRepresentable
{
    NONE,
    SMOULDERING,
    FADING,
    KINDLED,
    SEETHING;

    public static final Codec<BlazeBurnerBlock.HeatLevel> CODEC;

    public static BlazeBurnerBlock.HeatLevel byIndex(int index) {
        return BlazeBurnerBlock.HeatLevel.values()[index];
    }

    public BlazeBurnerBlock.HeatLevel nextActiveLevel() {
        return BlazeBurnerBlock.HeatLevel.byIndex(this.ordinal() % (BlazeBurnerBlock.HeatLevel.values().length - 1) + 1);
    }

    public boolean isAtLeast(BlazeBurnerBlock.HeatLevel heatLevel) {
        return this.ordinal() >= heatLevel.ordinal();
    }

    public String getSerializedName() {
        return Lang.asId((String)this.name());
    }

    static {
        CODEC = StringRepresentable.fromEnum(BlazeBurnerBlock.HeatLevel::values);
    }
}
