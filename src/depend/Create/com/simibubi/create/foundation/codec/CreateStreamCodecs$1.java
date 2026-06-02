/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 */
package com.simibubi.create.foundation.codec;

import java.util.function.BiFunction;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

static class CreateStreamCodecs.1
implements StreamCodec<RegistryFriendlyByteBuf, C> {
    final /* synthetic */ BiFunction val$reader;
    final /* synthetic */ BiFunction val$writer;

    CreateStreamCodecs.1(BiFunction biFunction, BiFunction biFunction2) {
        this.val$reader = biFunction;
        this.val$writer = biFunction2;
    }

    public C decode(RegistryFriendlyByteBuf buffer) {
        return this.val$reader.apply(buffer.registryAccess(), buffer.readNbt());
    }

    public void encode(RegistryFriendlyByteBuf buffer, C value) {
        buffer.writeNbt((Tag)this.val$writer.apply(value, buffer.registryAccess()));
    }
}
