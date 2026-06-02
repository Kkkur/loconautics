/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.equipment.sandPaper;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record SandPaperItemComponent(ItemStack item) {
    public static final Codec<SandPaperItemComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ItemStack.OPTIONAL_CODEC.fieldOf("item").forGetter(i -> i.item)).apply((Applicative)instance, SandPaperItemComponent::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, SandPaperItemComponent> STREAM_CODEC = StreamCodec.composite((StreamCodec)ItemStack.OPTIONAL_STREAM_CODEC, i -> i.item, SandPaperItemComponent::new);

    @Override
    public final boolean equals(Object arg0) {
        ItemStack otherItem;
        return arg0 instanceof ItemStack && ItemStack.isSameItemSameComponents((ItemStack)(otherItem = (ItemStack)arg0), (ItemStack)this.item);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(this.item.getItem(), this.item.getCount(), this.item.getComponents());
    }
}
