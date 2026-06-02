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
import com.simibubi.create.content.equipment.zapper.terrainzapper.Brush;
import com.simibubi.create.content.equipment.zapper.terrainzapper.CuboidBrush;
import com.simibubi.create.content.equipment.zapper.terrainzapper.CylinderBrush;
import com.simibubi.create.content.equipment.zapper.terrainzapper.DynamicBrush;
import com.simibubi.create.content.equipment.zapper.terrainzapper.SphereBrush;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum TerrainBrushes implements StringRepresentable
{
    Cuboid(new CuboidBrush()),
    Sphere(new SphereBrush()),
    Cylinder(new CylinderBrush()),
    Surface(new DynamicBrush(true)),
    Cluster(new DynamicBrush(false));

    public static final Codec<TerrainBrushes> CODEC;
    public static final StreamCodec<ByteBuf, TerrainBrushes> STREAM_CODEC;
    private Brush brush;

    private TerrainBrushes(Brush brush) {
        this.brush = brush;
    }

    public Brush get() {
        return this.brush;
    }

    @NotNull
    public String getSerializedName() {
        return Lang.asId((String)this.name());
    }

    static {
        CODEC = StringRepresentable.fromValues(TerrainBrushes::values);
        STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(TerrainBrushes.class);
    }
}
