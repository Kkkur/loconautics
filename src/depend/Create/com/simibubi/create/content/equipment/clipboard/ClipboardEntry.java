/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.core.component.DataComponentMap
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.chat.ComponentSerialization
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.equipment.clipboard;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ClipboardEntry {
    public static final Codec<ClipboardEntry> CODEC = RecordCodecBuilder.create(i -> i.group((App)Codec.BOOL.fieldOf("checked").forGetter(c -> c.checked), (App)ComponentSerialization.CODEC.fieldOf("text").forGetter(c -> c.text), (App)ItemStack.OPTIONAL_CODEC.fieldOf("icon").forGetter(c -> c.icon), (App)Codec.INT.fieldOf("item_amount").forGetter(c -> c.itemAmount)).apply((Applicative)i, (checked, text, icon, itemAmount) -> {
        ClipboardEntry entry = new ClipboardEntry((boolean)checked, text.copy());
        if (!icon.isEmpty()) {
            entry.displayItem((ItemStack)icon, (int)itemAmount);
        }
        return entry;
    }));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClipboardEntry> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.BOOL, c -> c.checked, (StreamCodec)ComponentSerialization.STREAM_CODEC, c -> c.text, (StreamCodec)ItemStack.OPTIONAL_STREAM_CODEC, c -> c.icon, (StreamCodec)ByteBufCodecs.INT, c -> c.itemAmount, (checked, text, icon, itemAmount) -> {
        ClipboardEntry entry = new ClipboardEntry((boolean)checked, text.copy());
        if (!icon.isEmpty()) {
            entry.displayItem((ItemStack)icon, (int)itemAmount);
        }
        return entry;
    });
    public boolean checked;
    public MutableComponent text;
    public ItemStack icon;
    public int itemAmount;

    public ClipboardEntry(boolean checked, MutableComponent text) {
        this.checked = checked;
        this.text = text;
        this.icon = ItemStack.EMPTY;
    }

    public ClipboardEntry displayItem(ItemStack icon, int amount) {
        this.icon = icon;
        this.itemAmount = amount;
        return this;
    }

    public static List<List<ClipboardEntry>> readAll(ItemStack clipboardItem) {
        return ClipboardEntry.readAll(clipboardItem.getComponents());
    }

    public static List<List<ClipboardEntry>> readAll(DataComponentMap components) {
        return ClipboardEntry.readAll((ClipboardContent)components.get(AllDataComponents.CLIPBOARD_CONTENT));
    }

    public static List<List<ClipboardEntry>> readAll(@Nullable ClipboardContent content) {
        if (content == null) {
            return new ArrayList<List<ClipboardEntry>>();
        }
        List<List<ClipboardEntry>> saved = content.pages();
        ArrayList<List<ClipboardEntry>> entries = new ArrayList<List<ClipboardEntry>>(saved.size());
        for (List<ClipboardEntry> inner : saved) {
            entries.add(new ArrayList<ClipboardEntry>(inner));
        }
        return entries;
    }

    public static List<ClipboardEntry> getLastViewedEntries(ItemStack heldItem) {
        List<List<ClipboardEntry>> pages = ClipboardEntry.readAll(heldItem);
        if (pages.isEmpty()) {
            return new ArrayList<ClipboardEntry>();
        }
        int previouslyOpenedPage = ((ClipboardContent)heldItem.getOrDefault(AllDataComponents.CLIPBOARD_CONTENT, (Object)ClipboardContent.EMPTY)).previouslyOpenedPage();
        int page = Math.min(previouslyOpenedPage, pages.size() - 1);
        return pages.get(page);
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClipboardEntry)) {
            return false;
        }
        ClipboardEntry that = (ClipboardEntry)o;
        return this.checked == that.checked && this.text.equals((Object)that.text) && ItemStack.isSameItemSameComponents((ItemStack)this.icon, (ItemStack)that.icon);
    }

    public int hashCode() {
        int result = Boolean.hashCode(this.checked);
        result = 31 * result + this.text.hashCode();
        result = 31 * result + ItemStack.hashItemAndComponents((ItemStack)this.icon);
        return result;
    }
}
