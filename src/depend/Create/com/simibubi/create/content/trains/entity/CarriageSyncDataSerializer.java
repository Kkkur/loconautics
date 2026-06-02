/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.syncher.EntityDataSerializer
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.content.trains.entity.CarriageSyncData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;

public class CarriageSyncDataSerializer
implements EntityDataSerializer<CarriageSyncData> {
    private static final StreamCodec<RegistryFriendlyByteBuf, CarriageSyncData> STREAM_CODEC = StreamCodec.of((buf, data) -> data.write((FriendlyByteBuf)buf), CarriageSyncData::new);

    public StreamCodec<? super RegistryFriendlyByteBuf, CarriageSyncData> codec() {
        return STREAM_CODEC;
    }

    public CarriageSyncData copy(CarriageSyncData data) {
        return data.copy();
    }
}
