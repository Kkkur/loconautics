/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.util.entry.ItemProviderEntry
 *  net.minecraft.advancements.Advancement$Builder
 *  net.minecraft.advancements.AdvancementHolder
 *  net.minecraft.advancements.AdvancementType
 *  net.minecraft.advancements.Criterion
 *  net.minecraft.advancements.CriterionTriggerInstance
 *  net.minecraft.advancements.critereon.InventoryChangeTrigger$TriggerInstance
 *  net.minecraft.advancements.critereon.ItemPredicate
 *  net.minecraft.advancements.critereon.ItemPredicate$Builder
 *  net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger$TriggerInstance
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 */
package com.simibubi.create.foundation.advancement;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.advancement.AllTriggers;
import com.simibubi.create.foundation.advancement.SimpleCreateTrigger;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class CreateAdvancement {
    static final ResourceLocation BACKGROUND = Create.asResource("textures/gui/advancements.png");
    static final String LANG = "advancement.create.";
    static final String SECRET_SUFFIX = "\n\u00a77(Hidden Advancement)";
    private final Advancement.Builder mcBuilder = Advancement.Builder.advancement();
    private SimpleCreateTrigger builtinTrigger;
    private CreateAdvancement parent;
    private final Builder createBuilder = new Builder();
    AdvancementHolder datagenResult;
    private String id;
    private String title;
    private String description;

    public CreateAdvancement(String id, UnaryOperator<Builder> b) {
        this.id = id;
        b.apply(this.createBuilder);
        if (!this.createBuilder.externalTrigger) {
            this.builtinTrigger = AllTriggers.addSimple(id + "_builtin");
            this.mcBuilder.addCriterion("0", this.builtinTrigger.createCriterion((CriterionTriggerInstance)this.builtinTrigger.instance()));
        }
        if (this.createBuilder.type == TaskType.SECRET) {
            this.description = this.description + SECRET_SUFFIX;
        }
        AllAdvancements.ENTRIES.add(this);
    }

    private String titleKey() {
        return LANG + this.id;
    }

    private String descriptionKey() {
        return this.titleKey() + ".desc";
    }

    public boolean isAlreadyAwardedTo(Player player) {
        if (!(player instanceof ServerPlayer)) {
            return true;
        }
        ServerPlayer sp = (ServerPlayer)player;
        AdvancementHolder advancement = sp.getServer().getAdvancements().get(Create.asResource(this.id));
        if (advancement == null) {
            return true;
        }
        return sp.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    public void awardTo(Player player) {
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer sp = (ServerPlayer)player;
        if (this.builtinTrigger == null) {
            throw new UnsupportedOperationException("Advancement " + this.id + " uses external Triggers, it cannot be awarded directly");
        }
        this.builtinTrigger.trigger(sp);
    }

    void save(Consumer<AdvancementHolder> t, HolderLookup.Provider registries) {
        if (this.parent != null) {
            this.mcBuilder.parent(this.parent.datagenResult);
        }
        if (this.createBuilder.func != null) {
            this.createBuilder.icon(this.createBuilder.func.apply(registries));
        }
        this.mcBuilder.display(this.createBuilder.icon, (Component)Component.translatable((String)this.titleKey()), (Component)Component.translatable((String)this.descriptionKey()).withStyle(s -> s.withColor(14393875)), (ResourceLocation)(this.id.equals("root") ? BACKGROUND : null), this.createBuilder.type.advancementType, this.createBuilder.type.toast, this.createBuilder.type.announce, this.createBuilder.type.hide);
        this.datagenResult = this.mcBuilder.save(t, Create.asResource(this.id).toString());
    }

    void provideLang(BiConsumer<String, String> consumer) {
        consumer.accept(this.titleKey(), this.title);
        consumer.accept(this.descriptionKey(), this.description);
    }

    class Builder {
        private TaskType type = TaskType.NORMAL;
        private boolean externalTrigger;
        private int keyIndex;
        private ItemStack icon;
        private Function<HolderLookup.Provider, ItemStack> func;

        Builder() {
        }

        Builder special(TaskType type) {
            this.type = type;
            return this;
        }

        Builder after(CreateAdvancement other) {
            CreateAdvancement.this.parent = other;
            return this;
        }

        Builder icon(ItemProviderEntry<?, ?> item) {
            return this.icon(item.asStack());
        }

        Builder icon(ItemLike item) {
            return this.icon(new ItemStack(item));
        }

        Builder icon(ItemStack stack) {
            this.icon = stack;
            return this;
        }

        Builder icon(Function<HolderLookup.Provider, ItemStack> func) {
            this.func = func;
            return this;
        }

        Builder title(String title) {
            CreateAdvancement.this.title = title;
            return this;
        }

        Builder description(String description) {
            CreateAdvancement.this.description = description;
            return this;
        }

        Builder whenBlockPlaced(Block block) {
            return this.externalTrigger(ItemUsedOnLocationTrigger.TriggerInstance.placedBlock((Block)block));
        }

        Builder whenIconCollected() {
            return this.externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems((ItemLike[])new ItemLike[]{this.icon.getItem()}));
        }

        Builder whenItemCollected(ItemProviderEntry<?, ?> item) {
            return this.whenItemCollected((ItemLike)item.asStack().getItem());
        }

        Builder whenItemCollected(ItemLike itemProvider) {
            return this.externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems((ItemLike[])new ItemLike[]{itemProvider}));
        }

        Builder whenItemCollected(TagKey<Item> tag) {
            return this.externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems((ItemPredicate[])new ItemPredicate[]{ItemPredicate.Builder.item().of(tag).build()}));
        }

        Builder awardedForFree() {
            return this.externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems((ItemLike[])new ItemLike[0]));
        }

        Builder externalTrigger(Criterion<?> trigger) {
            CreateAdvancement.this.mcBuilder.addCriterion(String.valueOf(this.keyIndex), trigger);
            this.externalTrigger = true;
            ++this.keyIndex;
            return this;
        }
    }

    static enum TaskType {
        SILENT(AdvancementType.TASK, false, false, false),
        NORMAL(AdvancementType.TASK, true, false, false),
        NOISY(AdvancementType.TASK, true, true, false),
        EXPERT(AdvancementType.GOAL, true, true, false),
        SECRET(AdvancementType.GOAL, true, true, true);

        private final AdvancementType advancementType;
        private final boolean toast;
        private final boolean announce;
        private final boolean hide;

        private TaskType(AdvancementType advancementType, boolean toast, boolean announce, boolean hide) {
            this.advancementType = advancementType;
            this.toast = toast;
            this.announce = announce;
            this.hide = hide;
        }
    }
}
