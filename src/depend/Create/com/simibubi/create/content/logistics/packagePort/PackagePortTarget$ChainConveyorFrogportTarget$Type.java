/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.network.codec.StreamCodec
 */
package com.simibubi.create.content.logistics.packagePort;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.logistics.packagePort.PackagePortTarget;
import com.simibubi.create.content.logistics.packagePort.PackagePortTargetType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public static class PackagePortTarget.ChainConveyorFrogportTarget.Type
implements PackagePortTargetType {
    public MapCodec<PackagePortTarget.ChainConveyorFrogportTarget> codec() {
        return CODEC;
    }

    public StreamCodec<ByteBuf, PackagePortTarget.ChainConveyorFrogportTarget> streamCodec() {
        return STREAM_CODEC;
    }
}
