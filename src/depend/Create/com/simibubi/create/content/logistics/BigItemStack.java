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
 *  net.minecraft.util.ExtraCodecs
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.logistics;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

public class BigItemStack {
    public static final Codec<BigItemStack> CODEC = RecordCodecBuilder.create(i -> i.group((App)ItemStack.OPTIONAL_CODEC.fieldOf("item_stack").forGetter(s -> s.stack), (App)ExtraCodecs.NON_NEGATIVE_INT.fieldOf("count").forGetter(s -> s.count)).apply((Applicative)i, BigItemStack::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, BigItemStack> STREAM_CODEC = StreamCodec.composite((StreamCodec)ItemStack.OPTIONAL_STREAM_CODEC, s -> s.stack, (StreamCodec)ByteBufCodecs.VAR_INT, s -> s.count, BigItemStack::new);
    public static final int INF = 1000000000;
    public ItemStack stack;
    public int count;

    public BigItemStack(ItemStack stack) {
        this(stack, 1);
    }

    public BigItemStack(ItemStack stack, int count) {
        this.stack = stack;
        this.count = count;
    }

    public boolean isInfinite() {
        return this.count >= 1000000000;
    }

    public static BigItemStack receive(RegistryFriendlyByteBuf buffer) {
        return new BigItemStack((ItemStack)ItemStack.STREAM_CODEC.decode((Object)buffer), buffer.readVarInt());
    }

    public static Comparator<? super BigItemStack> comparator() {
        return (i1, i2) -> Integer.compare(i2.count, i1.count);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof BigItemStack) {
            BigItemStack other = (BigItemStack)obj;
            return ItemStack.isSameItemSameComponents((ItemStack)this.stack, (ItemStack)other.stack) && this.count == other.count;
        }
        return false;
    }

    public int hashCode() {
        return this.nullHash(this.stack) * 31 ^ Integer.hashCode(this.count);
    }

    int nullHash(Object o) {
        return o == null ? 0 : o.hashCode();
    }

    public String toString() {
        return "(" + this.stack.getHoverName().getString() + " x" + this.count + ")";
    }

    public static List<BigItemStack> duplicateWrappers(List<BigItemStack> list) {
        ArrayList<BigItemStack> copy = new ArrayList<BigItemStack>();
        for (BigItemStack bigItemStack : list) {
            copy.add(new BigItemStack(bigItemStack.stack, bigItemStack.count));
        }
        return copy;
    }
}
