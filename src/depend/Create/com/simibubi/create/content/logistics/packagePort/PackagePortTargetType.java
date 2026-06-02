/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 */
package com.simibubi.create.content.logistics.packagePort;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.logistics.packagePort.PackagePortTarget;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface PackagePortTargetType {
    public MapCodec<? extends PackagePortTarget> codec();

    public StreamCodec<? super RegistryFriendlyByteBuf, ? extends PackagePortTarget> streamCodec();
}
