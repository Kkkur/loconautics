/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.createmod.catnip.codecs.CatnipCodecUtils
 *  net.createmod.catnip.nbt.NBTProcessors
 *  net.minecraft.core.component.DataComponentMap
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.network.Filterable
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.component.WrittenBookContent
 *  net.minecraft.world.level.block.entity.BlockEntityType
 */
package com.simibubi.create.foundation;

import com.mojang.serialization.Codec;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
import com.simibubi.create.content.equipment.clipboard.ClipboardEntry;
import java.util.List;
import java.util.function.UnaryOperator;
import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.createmod.catnip.nbt.NBTProcessors;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CreateNBTProcessors {
    public static void register() {
        NBTProcessors.addProcessor((BlockEntityType)BlockEntityType.LECTERN, data -> {
            if (!data.contains("Book", 10)) {
                return data;
            }
            CompoundTag book = data.getCompound("Book");
            ResourceLocation writableBookResource = BuiltInRegistries.ITEM.getKey((Object)Items.WRITABLE_BOOK);
            if (writableBookResource != BuiltInRegistries.ITEM.getDefaultKey() && book.getString("id").equals(writableBookResource.toString())) {
                return data;
            }
            WrittenBookContent bookContent = (WrittenBookContent)CatnipCodecUtils.decodeOrNull((Codec)WrittenBookContent.CODEC, (Tag)book);
            if (bookContent == null) {
                return data;
            }
            for (Filterable page : bookContent.pages()) {
                if (!NBTProcessors.textComponentHasClickEvent((Component)((Component)page.get(false)))) continue;
                return null;
            }
            return data;
        });
        NBTProcessors.addProcessor((BlockEntityType)((BlockEntityType)AllBlockEntityTypes.CLIPBOARD.get()), CreateNBTProcessors::clipboardProcessor);
        NBTProcessors.addProcessor((BlockEntityType)((BlockEntityType)AllBlockEntityTypes.CREATIVE_CRATE.get()), (UnaryOperator)NBTProcessors.itemProcessor((String)"Filter"));
    }

    public static CompoundTag clipboardProcessor(CompoundTag data) {
        DataComponentMap components = (DataComponentMap)CatnipCodecUtils.decodeOrNull((Codec)DataComponentMap.CODEC, (Tag)data.getCompound("components"));
        if (components == null) {
            return data;
        }
        ClipboardContent content = (ClipboardContent)components.get(AllDataComponents.CLIPBOARD_CONTENT);
        if (content == null) {
            return data;
        }
        for (List<ClipboardEntry> entries : content.pages()) {
            for (ClipboardEntry entry : entries) {
                if (!NBTProcessors.textComponentHasClickEvent((Component)entry.text)) continue;
                return null;
            }
        }
        return data;
    }
}
