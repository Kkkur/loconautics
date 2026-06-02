/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.createmod.catnip.codecs.CatnipCodecUtils
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.item.filter.attribute;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public interface ItemAttribute {
    public static final Codec<ItemAttribute> CODEC = CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE.byNameCodec().dispatch(ItemAttribute::getType, ItemAttributeType::codec);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemAttribute> STREAM_CODEC = ByteBufCodecs.registry(CreateRegistries.ITEM_ATTRIBUTE_TYPE).dispatch(ItemAttribute::getType, ItemAttributeType::streamCodec);

    public static CompoundTag saveStatic(ItemAttribute attribute, HolderLookup.Provider registries) {
        CompoundTag nbt = new CompoundTag();
        nbt.put("attribute", (Tag)CatnipCodecUtils.encode(CODEC, (HolderLookup.Provider)registries, (Object)attribute).orElseThrow());
        return nbt;
    }

    @Nullable
    public static ItemAttribute loadStatic(CompoundTag nbt, HolderLookup.Provider registries) {
        return (ItemAttribute)CatnipCodecUtils.decodeOrNull(CODEC, (HolderLookup.Provider)registries, (Tag)nbt.get("attribute"));
    }

    public static List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
        ArrayList<ItemAttribute> attributes = new ArrayList<ItemAttribute>();
        for (ItemAttributeType type : CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE) {
            attributes.addAll(type.getAllAttributes(stack, level));
        }
        return attributes;
    }

    public boolean appliesTo(ItemStack var1, Level var2);

    public ItemAttributeType getType();

    @OnlyIn(value=Dist.CLIENT)
    default public MutableComponent format(boolean inverted) {
        return CreateLang.translateDirect("item_attributes." + this.getTranslationKey() + (inverted ? ".inverted" : ""), this.getTranslationParameters());
    }

    public String getTranslationKey();

    default public Object[] getTranslationParameters() {
        return new String[0];
    }

    public record ItemAttributeEntry(ItemAttribute attribute, boolean inverted) {
        public static final Codec<ItemAttributeEntry> CODEC = RecordCodecBuilder.create(i -> i.group((App)CODEC.fieldOf("attribute").forGetter(ItemAttributeEntry::attribute), (App)Codec.BOOL.fieldOf("inverted").forGetter(ItemAttributeEntry::inverted)).apply((Applicative)i, ItemAttributeEntry::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, ItemAttributeEntry> STREAM_CODEC = StreamCodec.composite(STREAM_CODEC, ItemAttributeEntry::attribute, (StreamCodec)ByteBufCodecs.BOOL, ItemAttributeEntry::inverted, ItemAttributeEntry::new);
    }
}
