/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.util.entry.ItemProviderEntry
 *  net.minecraft.advancements.Criterion
 *  net.minecraft.advancements.CriterionTriggerInstance
 *  net.minecraft.advancements.critereon.InventoryChangeTrigger$TriggerInstance
 *  net.minecraft.advancements.critereon.ItemPredicate
 *  net.minecraft.advancements.critereon.ItemPredicate$Builder
 *  net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger$TriggerInstance
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 */
package dev.simulated_team.simulated.data.advancements;

import com.tterrag.registrate.util.entry.ItemProviderEntry;
import dev.simulated_team.simulated.data.advancements.SimulatedAdvancement;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class SimulatedAdvancement.Builder {
    private SimulatedAdvancement.TaskType type = SimulatedAdvancement.TaskType.NORMAL;
    private boolean externalTrigger;
    private int keyIndex;
    private ItemStack icon;

    public SimulatedAdvancement.Builder special(SimulatedAdvancement.TaskType type) {
        this.type = type;
        return this;
    }

    public SimulatedAdvancement.Builder after(SimulatedAdvancement other) {
        SimulatedAdvancement.this.parent = other;
        return this;
    }

    public SimulatedAdvancement.Builder icon(ItemProviderEntry<?, ?> item) {
        return this.icon(item.asStack());
    }

    public SimulatedAdvancement.Builder icon(ItemLike item) {
        return this.icon(new ItemStack(item));
    }

    public SimulatedAdvancement.Builder icon(ItemStack stack) {
        this.icon = stack;
        return this;
    }

    public SimulatedAdvancement.Builder title(String title) {
        SimulatedAdvancement.this.title = title;
        return this;
    }

    public SimulatedAdvancement.Builder description(String description) {
        SimulatedAdvancement.this.description = description;
        return this;
    }

    public SimulatedAdvancement.Builder whenBlockPlaced(Block block) {
        return this.externalTrigger((Criterion<? extends CriterionTriggerInstance>)ItemUsedOnLocationTrigger.TriggerInstance.placedBlock((Block)block));
    }

    public SimulatedAdvancement.Builder whenIconCollected() {
        return this.externalTrigger((Criterion<? extends CriterionTriggerInstance>)InventoryChangeTrigger.TriggerInstance.hasItems((ItemLike[])new ItemLike[]{this.icon.getItem()}));
    }

    public SimulatedAdvancement.Builder whenIconPlaced() {
        Item item = this.icon.getItem();
        if (item instanceof BlockItem) {
            BlockItem blockItem = (BlockItem)item;
            return this.externalTrigger((Criterion<? extends CriterionTriggerInstance>)ItemUsedOnLocationTrigger.TriggerInstance.placedBlock((Block)blockItem.getBlock()));
        }
        return this.whenIconCollected();
    }

    public SimulatedAdvancement.Builder whenItemCollected(ItemProviderEntry<?, ?> item) {
        return this.whenItemCollected((ItemLike)item.asStack().getItem());
    }

    public SimulatedAdvancement.Builder whenItemCollected(ItemLike itemProvider) {
        return this.externalTrigger((Criterion<? extends CriterionTriggerInstance>)InventoryChangeTrigger.TriggerInstance.hasItems((ItemLike[])new ItemLike[]{itemProvider}));
    }

    public SimulatedAdvancement.Builder whenItemCollected(TagKey<Item> tag) {
        return this.externalTrigger((Criterion<? extends CriterionTriggerInstance>)InventoryChangeTrigger.TriggerInstance.hasItems((ItemPredicate[])new ItemPredicate[]{ItemPredicate.Builder.item().of(tag).build()}));
    }

    public SimulatedAdvancement.Builder awardedForFree() {
        return this.externalTrigger((Criterion<? extends CriterionTriggerInstance>)InventoryChangeTrigger.TriggerInstance.hasItems((ItemLike[])new ItemLike[0]));
    }

    public SimulatedAdvancement.Builder externalTrigger(Criterion<? extends CriterionTriggerInstance> trigger) {
        SimulatedAdvancement.this.builder.addCriterion(String.valueOf(this.keyIndex), trigger);
        this.externalTrigger = true;
        ++this.keyIndex;
        return this;
    }
}
