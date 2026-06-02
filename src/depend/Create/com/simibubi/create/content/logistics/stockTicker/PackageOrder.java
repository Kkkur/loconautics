/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 */
package com.simibubi.create.content.logistics.stockTicker;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.logistics.BigItemStack;
import java.util.Collections;
import java.util.List;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record PackageOrder(List<BigItemStack> stacks) {
    public static final Codec<PackageOrder> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.list(BigItemStack.CODEC).fieldOf("entries").forGetter(PackageOrder::stacks)).apply((Applicative)instance, PackageOrder::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, PackageOrder> STREAM_CODEC = CatnipStreamCodecBuilders.list(BigItemStack.STREAM_CODEC).map(PackageOrder::new, PackageOrder::stacks);

    public static PackageOrder empty() {
        return new PackageOrder(Collections.emptyList());
    }

    public boolean isEmpty() {
        return this.stacks.isEmpty();
    }
}
