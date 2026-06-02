/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.objects.Object2IntArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.HoverEvent
 *  net.minecraft.network.chat.HoverEvent$Action
 *  net.minecraft.network.chat.HoverEvent$ItemStackInfo
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.server.network.Filterable
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.component.WrittenBookContent
 *  net.minecraft.world.level.ItemLike
 */
package com.simibubi.create.content.schematics.cannon;

import com.google.common.collect.Sets;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
import com.simibubi.create.content.equipment.clipboard.ClipboardEntry;
import com.simibubi.create.content.equipment.clipboard.ClipboardOverrides;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.utility.CreateLang;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.ItemLike;

public class MaterialChecklist {
    public static final int MAX_ENTRIES_PER_PAGE = 5;
    public static final int MAX_ENTRIES_PER_CLIPBOARD_PAGE = 7;
    public Object2IntMap<Item> gathered = new Object2IntArrayMap();
    public Object2IntMap<Item> required = new Object2IntArrayMap();
    public Object2IntMap<Item> damageRequired = new Object2IntArrayMap();
    public boolean blocksNotLoaded;

    public void warnBlockNotLoaded() {
        this.blocksNotLoaded = true;
    }

    public void require(ItemRequirement requirement) {
        if (requirement.isEmpty()) {
            return;
        }
        if (requirement.isInvalid()) {
            return;
        }
        for (ItemRequirement.StackRequirement stack : requirement.getRequiredItems()) {
            if (stack.usage == ItemRequirement.ItemUseType.DAMAGE) {
                this.putOrIncrement(this.damageRequired, stack.stack);
            }
            if (stack.usage != ItemRequirement.ItemUseType.CONSUME) continue;
            this.putOrIncrement(this.required, stack.stack);
        }
    }

    private void putOrIncrement(Object2IntMap<Item> map, ItemStack stack) {
        Item item = stack.getItem();
        if (item == Items.AIR) {
            return;
        }
        if (map.containsKey((Object)item)) {
            map.put((Object)item, map.getInt((Object)item) + stack.getCount());
        } else {
            map.put((Object)item, stack.getCount());
        }
    }

    public void collect(ItemStack stack) {
        Item item = stack.getItem();
        if (this.required.containsKey((Object)item) || this.damageRequired.containsKey((Object)item)) {
            if (this.gathered.containsKey((Object)item)) {
                this.gathered.put((Object)item, this.gathered.getInt((Object)item) + stack.getCount());
            } else {
                this.gathered.put((Object)item, stack.getCount());
            }
        }
    }

    public ItemStack createWrittenBook() {
        MutableComponent textComponent;
        ItemStack book = new ItemStack((ItemLike)Items.WRITTEN_BOOK);
        ArrayList<Filterable> pages = new ArrayList<Filterable>();
        int itemsWritten = 0;
        if (this.blocksNotLoaded) {
            textComponent = Component.literal((String)("\n" + String.valueOf(ChatFormatting.RED)));
            textComponent = textComponent.append((Component)CreateLang.translateDirect("materialChecklist.blocksNotLoaded", new Object[0]));
            pages.add(Filterable.passThrough((Object)textComponent));
        }
        ArrayList keys = new ArrayList(Sets.union((Set)this.required.keySet(), (Set)this.damageRequired.keySet()));
        Collections.sort(keys, (item1, item2) -> {
            Locale locale = Locale.ENGLISH;
            String name1 = item1.getDescription().getString().toLowerCase(locale);
            String name2 = item2.getDescription().getString().toLowerCase(locale);
            return name1.compareTo(name2);
        });
        textComponent = Component.empty();
        ArrayList<Item> completed = new ArrayList<Item>();
        for (Item item : keys) {
            int amount = this.getRequiredAmount(item);
            if (this.gathered.containsKey((Object)item)) {
                amount -= this.gathered.getInt((Object)item);
            }
            if (amount <= 0) {
                completed.add(item);
                continue;
            }
            if (itemsWritten == 5) {
                itemsWritten = 0;
                textComponent.append((Component)Component.literal((String)"\n >>>").withStyle(ChatFormatting.BLUE));
                pages.add(Filterable.passThrough((Object)textComponent));
                textComponent = Component.empty();
            }
            ++itemsWritten;
            textComponent.append((Component)this.entry(new ItemStack((ItemLike)item), amount, true, true));
        }
        for (Item item : completed) {
            if (itemsWritten == 5) {
                itemsWritten = 0;
                textComponent.append((Component)Component.literal((String)"\n >>>").withStyle(ChatFormatting.DARK_GREEN));
                pages.add(Filterable.passThrough((Object)textComponent));
                textComponent = Component.empty();
            }
            ++itemsWritten;
            textComponent.append((Component)this.entry(new ItemStack((ItemLike)item), this.getRequiredAmount(item), false, true));
        }
        pages.add(Filterable.passThrough((Object)textComponent));
        WrittenBookContent contents = new WrittenBookContent(Filterable.passThrough((Object)(String.valueOf(ChatFormatting.BLUE) + "Material Checklist")), "Schematicannon", 0, pages, true);
        book.set(DataComponents.WRITTEN_BOOK_CONTENT, (Object)contents);
        textComponent = CreateLang.translateDirect("materialChecklist", new Object[0]).setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE).withItalic(Boolean.FALSE));
        book.set(DataComponents.CUSTOM_NAME, (Object)textComponent);
        return book;
    }

    public ItemStack createWrittenClipboard() {
        int itemsWritten = 0;
        ArrayList<List<ClipboardEntry>> pages = new ArrayList<List<ClipboardEntry>>();
        ArrayList<ClipboardEntry> currentPage = new ArrayList<ClipboardEntry>();
        if (this.blocksNotLoaded) {
            currentPage.add(new ClipboardEntry(false, CreateLang.translateDirect("materialChecklist.blocksNotLoaded", new Object[0]).withStyle(ChatFormatting.RED)));
        }
        ArrayList keys = new ArrayList(Sets.union((Set)this.required.keySet(), (Set)this.damageRequired.keySet()));
        Collections.sort(keys, (item1, item2) -> {
            Locale locale = Locale.ENGLISH;
            String name1 = item1.getDescription().getString().toLowerCase(locale);
            String name2 = item2.getDescription().getString().toLowerCase(locale);
            return name1.compareTo(name2);
        });
        ArrayList<Item> completed = new ArrayList<Item>();
        for (Item item : keys) {
            int amount = this.getRequiredAmount(item);
            if (this.gathered.containsKey((Object)item)) {
                amount -= this.gathered.getInt((Object)item);
            }
            if (amount <= 0) {
                completed.add(item);
                continue;
            }
            if (itemsWritten == 7) {
                itemsWritten = 0;
                currentPage.add(new ClipboardEntry(false, Component.literal((String)">>>").withStyle(ChatFormatting.DARK_GRAY)));
                pages.add(currentPage);
                currentPage = new ArrayList();
            }
            ++itemsWritten;
            currentPage.add(new ClipboardEntry(false, this.entry(new ItemStack((ItemLike)item), amount, true, false)).displayItem(new ItemStack((ItemLike)item), amount));
        }
        for (Item item : completed) {
            if (itemsWritten == 7) {
                itemsWritten = 0;
                currentPage.add(new ClipboardEntry(true, Component.literal((String)">>>").withStyle(ChatFormatting.DARK_GREEN)));
                pages.add(currentPage);
                currentPage = new ArrayList();
            }
            ++itemsWritten;
            currentPage.add(new ClipboardEntry(true, this.entry(new ItemStack((ItemLike)item), this.getRequiredAmount(item), false, false)).displayItem(new ItemStack((ItemLike)item), 0));
        }
        pages.add(currentPage);
        ItemStack clipboard = AllBlocks.CLIPBOARD.asStack();
        clipboard.set(AllDataComponents.CLIPBOARD_CONTENT, (Object)new ClipboardContent(ClipboardOverrides.ClipboardType.WRITTEN, pages, true));
        clipboard.set(DataComponents.CUSTOM_NAME, (Object)CreateLang.translateDirect("materialChecklist", new Object[0]).setStyle(Style.EMPTY.withItalic(Boolean.valueOf(false))));
        return clipboard;
    }

    public int getRequiredAmount(Item item) {
        int amount = this.required.getOrDefault((Object)item, 0);
        if (this.damageRequired.containsKey((Object)item)) {
            amount += (int)Math.ceil((float)this.damageRequired.getInt((Object)item) / (float)new ItemStack((ItemLike)item).getMaxDamage());
        }
        return amount;
    }

    private MutableComponent entry(ItemStack item, int amount, boolean unfinished, boolean forBook) {
        int stacks = amount / 64;
        int remainder = amount % 64;
        MutableComponent tc = Component.empty();
        tc.append((Component)Component.translatable((String)item.getDescriptionId()).setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, (Object)new HoverEvent.ItemStackInfo(item)))));
        if (!unfinished && forBook) {
            tc.append(" \u2714");
        }
        if (!unfinished || forBook) {
            tc.withStyle(unfinished ? ChatFormatting.BLUE : ChatFormatting.DARK_GREEN);
        }
        return tc.append((Component)Component.literal((String)("\n x" + amount)).withStyle(ChatFormatting.BLACK)).append((Component)Component.literal((String)(" | " + stacks + "\u25a4 +" + remainder + (forBook ? "\n" : ""))).withStyle(ChatFormatting.GRAY));
    }
}
