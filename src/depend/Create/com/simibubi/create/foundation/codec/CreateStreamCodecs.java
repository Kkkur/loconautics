/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.codec.StreamCodec$CodecOperation
 */
package com.simibubi.create.foundation.codec;

import io.netty.buffer.ByteBuf;
import java.util.Vector;
import java.util.function.BiFunction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface CreateStreamCodecs {
    @Deprecated(forRemoval=true)
    public static <B extends ByteBuf, V> StreamCodec.CodecOperation<B, V, Vector<V>> vector() {
        return codec -> ByteBufCodecs.collection(Vector::new, (StreamCodec)codec);
    }

    @Deprecated(forRemoval=true)
    public static <C> StreamCodec<RegistryFriendlyByteBuf, C> ofLegacyNbtWithRegistries(final BiFunction<C, HolderLookup.Provider, CompoundTag> writer, final BiFunction<HolderLookup.Provider, CompoundTag, C> reader) {
        return new StreamCodec<RegistryFriendlyByteBuf, C>(){

            public C decode(RegistryFriendlyByteBuf buffer) {
                return reader.apply(buffer.registryAccess(), buffer.readNbt());
            }

            public void encode(RegistryFriendlyByteBuf buffer, C value) {
                buffer.writeNbt((Tag)writer.apply(value, buffer.registryAccess()));
            }
        };
    }
}
