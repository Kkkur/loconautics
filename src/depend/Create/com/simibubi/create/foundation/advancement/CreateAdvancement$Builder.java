/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.util.entry.ItemProviderEntry
 *  net.minecraft.advancements.Criterion
 *  net.minecraft.advancements.critereon.InventoryChangeTrigger$TriggerInstance
 *  net.minecraft.advancements.critereon.ItemPredicate
 *  net.minecraft.advancements.critereon.ItemPredicate$Builder
 *  net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger$TriggerInstance
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 */
package com.simibubi.create.foundation.advancement;

import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import java.util.function.Function;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

class CreateAdvancement.Builder {
    private CreateAdvancement.TaskType type = CreateAdvancement.TaskType.NORMAL;
    private boolean externalTrigger;
    private int keyIndex;
    private ItemStack icon;
    private Function<HolderLookup.Provider, ItemStack> func;

    CreateAdvancement.Builder() {
    }

    CreateAdvancement.Builder special(CreateAdvancement.TaskType type) {
        this.type = type;
        return this;
    }

    CreateAdvancement.Builder after(CreateAdvancement other) {
        CreateAdvancement.this.parent = other;
        return this;
    }

    CreateAdvancement.Builder icon(ItemProviderEntry<?, ?> item) {
        return this.icon(item.asStack());
    }

    CreateAdvancement.Builder icon(ItemLike item) {
        return this.icon(new ItemStack(item));
    }

    CreateAdvancement.Builder icon(ItemStack stack) {
        this.icon = stack;
        return this;
    }

    CreateAdvancement.Builder icon(Function<HolderLookup.Provider, ItemStack> func) {
        this.func = func;
        return this;
    }

    CreateAdvancement.Builder title(String title) {
        CreateAdvancement.this.title = title;
        return this;
    }

    CreateAdvancement.Builder description(String description) {
        CreateAdvancement.this.description = description;
        return this;
    }

    CreateAdvancement.Builder whenBlockPlaced(Block block) {
        return this.externalTrigger(ItemUsedOnLocationTrigger.TriggerInstance.placedBlock((Block)block));
    }

    CreateAdvancement.Builder whenIconCollected() {
        return this.externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems((ItemLike[])new ItemLike[]{this.icon.getItem()}));
    }

    CreateAdvancement.Builder whenItemCollected(ItemProviderEntry<?, ?> item) {
        return this.whenItemCollected((ItemLike)item.asStack().getItem());
    }

    CreateAdvancement.Builder whenItemCollected(ItemLike itemProvider) {
        return this.externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems((ItemLike[])new ItemLike[]{itemProvider}));
    }

    CreateAdvancement.Builder whenItemCollected(TagKey<Item> tag) {
        return this.externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems((ItemPredicate[])new ItemPredicate[]{ItemPredicate.Builder.item().of(tag).build()}));
    }

    CreateAdvancement.Builder awardedForFree() {
        return this.externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems((ItemLike[])new ItemLike[0]));
    }

    CreateAdvancement.Builder externalTrigger(Criterion<?> trigger) {
        CreateAdvancement.this.mcBuilder.addCriterion(String.valueOf(this.keyIndex), trigger);
        this.externalTrigger = true;
        ++this.keyIndex;
        return this;
    }
}
