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
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.box;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import java.util.Optional;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

public record PackageItem.PackageOrderData(int orderId, int linkIndex, boolean isFinalLink, int fragmentIndex, boolean isFinal, @Nullable PackageOrderWithCrafts orderContext) {
    public static final Codec<PackageItem.PackageOrderData> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("order_id").forGetter(PackageItem.PackageOrderData::orderId), (App)Codec.INT.fieldOf("link_index").forGetter(PackageItem.PackageOrderData::linkIndex), (App)Codec.BOOL.fieldOf("is_final_link").forGetter(PackageItem.PackageOrderData::isFinalLink), (App)Codec.INT.fieldOf("fragment_index").forGetter(PackageItem.PackageOrderData::fragmentIndex), (App)Codec.BOOL.fieldOf("is_final").forGetter(PackageItem.PackageOrderData::isFinal), (App)PackageOrderWithCrafts.CODEC.optionalFieldOf("order_context").forGetter(i -> Optional.ofNullable(i.orderContext))).apply((Applicative)instance, PackageItem.PackageOrderData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, PackageItem.PackageOrderData> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, PackageItem.PackageOrderData::orderId, (StreamCodec)ByteBufCodecs.INT, PackageItem.PackageOrderData::linkIndex, (StreamCodec)ByteBufCodecs.BOOL, PackageItem.PackageOrderData::isFinalLink, (StreamCodec)ByteBufCodecs.INT, PackageItem.PackageOrderData::fragmentIndex, (StreamCodec)ByteBufCodecs.BOOL, PackageItem.PackageOrderData::isFinal, (StreamCodec)CatnipStreamCodecBuilders.nullable(PackageOrderWithCrafts.STREAM_CODEC), PackageItem.PackageOrderData::orderContext, PackageItem.PackageOrderData::new);

    public PackageItem.PackageOrderData(int orderId, int linkIndex, boolean isFinalLink, int fragmentIndex, boolean isFinal, Optional<PackageOrderWithCrafts> orderContext) {
        this(orderId, linkIndex, isFinalLink, fragmentIndex, isFinal, (PackageOrderWithCrafts)orderContext.orElse(null));
    }
}
