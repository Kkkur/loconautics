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
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.AABB
 */
package dev.simulated_team.simulated.data.advancements;

import com.tterrag.registrate.util.entry.ItemProviderEntry;
import dev.simulated_team.simulated.data.advancements.SimpleSimulatedTrigger;
import dev.simulated_team.simulated.util.SimColors;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

public class SimulatedAdvancement {
    static final String SECRET_SUFFIX = "\n\u00a77(Hidden Advancement)";
    private final Advancement.Builder builder = Advancement.Builder.advancement();
    private SimpleSimulatedTrigger builtinTrigger;
    private SimulatedAdvancement parent;
    AdvancementHolder datagenResult;
    private final ResourceLocation background;
    private final String lang;
    private final String id;
    private final String modid;
    private String title;
    private String description;

    public SimulatedAdvancement(String id, UnaryOperator<Builder> b, ResourceLocation background, String modid, BiFunction<String, String, SimpleSimulatedTrigger> triggerHandler) {
        this.id = id;
        this.modid = modid;
        this.background = background;
        this.lang = "advancement." + modid + ".";
        Builder t = new Builder();
        b.apply(t);
        if (!t.externalTrigger) {
            this.builtinTrigger = triggerHandler.apply(modid, id + "_builtin");
            this.builder.addCriterion("0", this.builtinTrigger.createCriterion(this.builtinTrigger.instance()));
        }
        this.builder.display(t.icon, (Component)Component.translatable((String)this.titleKey()), (Component)Component.translatable((String)this.descriptionKey()).withStyle(s -> s.withColor(SimColors.ADVANCABLE_GOLD)), id.equals("root") ? this.background : null, t.type.advancementType, t.type.toast, t.type.announce, t.type.hide);
        if (t.type == TaskType.SECRET) {
            this.description = this.description + SECRET_SUFFIX;
        }
    }

    private String titleKey() {
        return this.lang + this.id;
    }

    private String descriptionKey() {
        return this.titleKey() + ".desc";
    }

    public boolean isAlreadyAwardedTo(Player player) {
        if (!(player instanceof ServerPlayer)) {
            return true;
        }
        ServerPlayer sp = (ServerPlayer)player;
        AdvancementHolder advancement = sp.getServer().getAdvancements().get(ResourceLocation.fromNamespaceAndPath((String)this.modid, (String)this.id));
        if (advancement == null) {
            return true;
        }
        return sp.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    public void awardTo(Player player) {
        if (this.isAlreadyAwardedTo(player)) {
            return;
        }
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer sp = (ServerPlayer)player;
        if (this.builtinTrigger == null) {
            throw new UnsupportedOperationException("Advancement " + this.id + " uses external Triggers, it cannot be awarded directly");
        }
        this.builtinTrigger.trigger(sp);
    }

    public void awardToNearby(BlockPos pos, Level level, int ticks, double radius) {
        if (level.getGameTime() % (long)ticks == 0L) {
            this.awardToNearby(pos, level, radius);
        }
    }

    public void awardToNearby(BlockPos pos, Level level) {
        this.awardToNearby(pos, level, 10.0);
    }

    public void awardToNearby(BlockPos pos, Level level, double radius) {
        AABB aabb = new AABB(pos).inflate(radius);
        List nearbyPlayers = level.getEntitiesOfClass(Player.class, aabb);
        for (Player player : nearbyPlayers) {
            this.awardTo(player);
        }
    }

    public void save(Consumer<AdvancementHolder> t) {
        if (this.parent != null) {
            this.builder.parent(this.parent.datagenResult);
        }
        this.datagenResult = this.builder.save(t, ResourceLocation.fromNamespaceAndPath((String)this.modid, (String)this.id).toString());
    }

    public void provideLang(BiConsumer<String, String> consumer) {
        consumer.accept(this.titleKey(), this.title);
        consumer.accept(this.descriptionKey(), this.description);
    }

    public class Builder {
        private TaskType type = TaskType.NORMAL;
        private boolean externalTrigger;
        private int keyIndex;
        private ItemStack icon;

        public Builder special(TaskType type) {
            this.type = type;
            return this;
        }

        public Builder after(SimulatedAdvancement other) {
            SimulatedAdvancement.this.parent = other;
            return this;
        }

        public Builder icon(ItemProviderEntry<?, ?> item) {
            return this.icon(item.asStack());
        }

        public Builder icon(ItemLike item) {
            return this.icon(new ItemStack(item));
        }

        public Builder icon(ItemStack stack) {
            this.icon = stack;
            return this;
        }

        public Builder title(String title) {
            SimulatedAdvancement.this.title = title;
            return this;
        }

        public Builder description(String description) {
            SimulatedAdvancement.this.description = description;
            return this;
        }

        public Builder whenBlockPlaced(Block block) {
            return this.externalTrigger((Criterion<? extends CriterionTriggerInstance>)ItemUsedOnLocationTrigger.TriggerInstance.placedBlock((Block)block));
        }

        public Builder whenIconCollected() {
            return this.externalTrigger((Criterion<? extends CriterionTriggerInstance>)InventoryChangeTrigger.TriggerInstance.hasItems((ItemLike[])new ItemLike[]{this.icon.getItem()}));
        }

        public Builder whenIconPlaced() {
            Item item = this.icon.getItem();
            if (item instanceof BlockItem) {
                BlockItem blockItem = (BlockItem)item;
                return this.externalTrigger((Criterion<? extends CriterionTriggerInstance>)ItemUsedOnLocationTrigger.TriggerInstance.placedBlock((Block)blockItem.getBlock()));
            }
            return this.whenIconCollected();
        }

        public Builder whenItemCollected(ItemProviderEntry<?, ?> item) {
            return this.whenItemCollected((ItemLike)item.asStack().getItem());
        }

        public Builder whenItemCollected(ItemLike itemProvider) {
            return this.externalTrigger((Criterion<? extends CriterionTriggerInstance>)InventoryChangeTrigger.TriggerInstance.hasItems((ItemLike[])new ItemLike[]{itemProvider}));
        }

        public Builder whenItemCollected(TagKey<Item> tag) {
            return this.externalTrigger((Criterion<? extends CriterionTriggerInstance>)InventoryChangeTrigger.TriggerInstance.hasItems((ItemPredicate[])new ItemPredicate[]{ItemPredicate.Builder.item().of(tag).build()}));
        }

        public Builder awardedForFree() {
            return this.externalTrigger((Criterion<? extends CriterionTriggerInstance>)InventoryChangeTrigger.TriggerInstance.hasItems((ItemLike[])new ItemLike[0]));
        }

        public Builder externalTrigger(Criterion<? extends CriterionTriggerInstance> trigger) {
            SimulatedAdvancement.this.builder.addCriterion(String.valueOf(this.keyIndex), trigger);
            this.externalTrigger = true;
            ++this.keyIndex;
            return this;
        }
    }

    public static enum TaskType {
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
