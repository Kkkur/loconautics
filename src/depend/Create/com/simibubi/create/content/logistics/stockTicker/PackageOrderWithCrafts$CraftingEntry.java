/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 */
package com.simibubi.create.content.logistics.stockTicker;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.logistics.stockTicker.PackageOrder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PackageOrderWithCrafts.CraftingEntry(PackageOrder pattern, int count) {
    public static final Codec<PackageOrderWithCrafts.CraftingEntry> CODEC = RecordCodecBuilder.create(i -> i.group((App)PackageOrder.CODEC.fieldOf("pattern").forGetter(PackageOrderWithCrafts.CraftingEntry::pattern), (App)Codec.INT.fieldOf("count").forGetter(PackageOrderWithCrafts.CraftingEntry::count)).apply((Applicative)i, PackageOrderWithCrafts.CraftingEntry::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, PackageOrderWithCrafts.CraftingEntry> STREAM_CODEC = StreamCodec.composite(PackageOrder.STREAM_CODEC, s -> s.pattern, (StreamCodec)ByteBufCodecs.VAR_INT, s -> s.count, PackageOrderWithCrafts.CraftingEntry::new);
}
