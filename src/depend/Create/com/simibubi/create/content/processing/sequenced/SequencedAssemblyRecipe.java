/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.level.Level
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.event.entity.player.ItemTooltipEvent
 *  net.neoforged.neoforge.items.wrapper.RecipeWrapper
 */
package com.simibubi.create.content.processing.sequenced;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeSerializer;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import com.simibubi.create.foundation.utility.CreateLang;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

public class SequencedAssemblyRecipe
implements Recipe<RecipeWrapper> {
    protected SequencedAssemblyRecipeSerializer serializer;
    protected Ingredient ingredient;
    protected List<SequencedRecipe<?>> sequence;
    protected int loops;
    protected ProcessingOutput transitionalItem;
    public final List<ProcessingOutput> resultPool;

    public SequencedAssemblyRecipe(SequencedAssemblyRecipeSerializer serializer) {
        this.serializer = serializer;
        this.sequence = new ArrayList();
        this.resultPool = new ArrayList<ProcessingOutput>();
        this.loops = 5;
    }

    public static <I extends RecipeInput, R extends ProcessingRecipe<I, ?>> Optional<RecipeHolder<R>> getRecipe(Level world, I inv, RecipeType<R> type, Class<R> recipeClass) {
        return SequencedAssemblyRecipe.getRecipe(world, inv, type, recipeClass, r -> ((ProcessingRecipe)r.value()).matches(inv, world));
    }

    public static <I extends RecipeInput, R extends ProcessingRecipe<I, ?>> Optional<RecipeHolder<R>> getRecipe(Level world, I inv, RecipeType<R> type, Class<R> recipeClass, Predicate<? super RecipeHolder<R>> recipeFilter) {
        List<RecipeHolder<R>> list = SequencedAssemblyRecipe.getRecipes(world, inv.getItem(0), type, recipeClass, recipeFilter);
        if (!list.isEmpty()) {
            return Optional.of(list.getFirst());
        }
        return Optional.empty();
    }

    public static <R extends ProcessingRecipe<?, ?>> Optional<RecipeHolder<R>> getRecipe(Level level, ItemStack item, RecipeType<R> type, Class<R> recipeClass) {
        List all = level.getRecipeManager().getAllRecipesFor(AllRecipeTypes.SEQUENCED_ASSEMBLY.getType());
        for (RecipeHolder sequencedAssemblyRecipe : all) {
            SequencedRecipe<?> nextRecipe;
            Object recipe;
            if (!((SequencedAssemblyRecipe)sequencedAssemblyRecipe.value()).appliesTo(sequencedAssemblyRecipe.id(), item) || ((ProcessingRecipe)(recipe = (nextRecipe = ((SequencedAssemblyRecipe)sequencedAssemblyRecipe.value()).getNextRecipe(item)).getRecipe())).getType() != type || !recipeClass.isInstance(recipe)) continue;
            ((ProcessingRecipe)recipe).enforceNextResult(() -> ((SequencedAssemblyRecipe)sequencedAssemblyRecipe.value()).advance(sequencedAssemblyRecipe.id(), item, level.random));
            return Optional.of(new RecipeHolder(sequencedAssemblyRecipe.id(), (Recipe)((ProcessingRecipe)recipeClass.cast(recipe))));
        }
        return Optional.empty();
    }

    public static <R extends ProcessingRecipe<?, ?>> List<RecipeHolder<R>> getRecipes(Level level, ItemStack item, RecipeType<R> type, Class<R> recipeClass, Predicate<? super RecipeHolder<R>> recipeFilter) {
        List all = level.getRecipeManager().getAllRecipesFor(AllRecipeTypes.SEQUENCED_ASSEMBLY.getType());
        ArrayList<RecipeHolder<R>> result = new ArrayList<RecipeHolder<R>>();
        for (RecipeHolder holder : all) {
            Object recipe;
            if (!((SequencedAssemblyRecipe)holder.value()).appliesTo(holder.id(), item) || ((ProcessingRecipe)(recipe = ((SequencedAssemblyRecipe)holder.value()).getNextRecipe(item).getRecipe())).getType() != type || !recipeClass.isInstance(recipe)) continue;
            ((ProcessingRecipe)recipe).enforceNextResult(() -> ((SequencedAssemblyRecipe)holder.value()).advance(holder.id(), item, level.random));
            ProcessingRecipe castedRecipe = (ProcessingRecipe)recipeClass.cast(recipe);
            RecipeHolder h = new RecipeHolder(holder.id(), (Recipe)castedRecipe);
            if (!recipeFilter.test(h)) continue;
            result.add(h);
        }
        return result;
    }

    private ItemStack advance(ResourceLocation id, ItemStack input, RandomSource random) {
        int step = this.getStep(input);
        if ((step + 1) / this.sequence.size() >= this.loops) {
            return this.rollResult(random);
        }
        ItemStack advancedItem = this.getTransitionalItem().copyWithCount(1);
        SequencedAssembly sequencedAssembly = new SequencedAssembly(id, step + 1, ((float)step + 1.0f) / (float)(this.sequence.size() * this.loops));
        advancedItem.set(AllDataComponents.SEQUENCED_ASSEMBLY, (Object)sequencedAssembly);
        return advancedItem;
    }

    public int getLoops() {
        return this.loops;
    }

    private ItemStack rollResult(RandomSource random) {
        float totalWeight = 0.0f;
        for (ProcessingOutput entry : this.resultPool) {
            totalWeight += entry.getChance();
        }
        float number = random.nextFloat() * totalWeight;
        for (ProcessingOutput entry : this.resultPool) {
            if (!((number -= entry.getChance()) < 0.0f)) continue;
            return entry.getStack().copy();
        }
        return ItemStack.EMPTY;
    }

    private boolean appliesTo(ResourceLocation id, ItemStack input) {
        if (input.has(AllDataComponents.SEQUENCED_ASSEMBLY)) {
            return this.getTransitionalItem().getItem() == input.getItem() && ((SequencedAssembly)input.get(AllDataComponents.SEQUENCED_ASSEMBLY)).id().equals((Object)id);
        }
        return this.ingredient.test(input);
    }

    private SequencedRecipe<?> getNextRecipe(ItemStack input) {
        return this.sequence.get(this.getStep(input) % this.sequence.size());
    }

    private int getStep(ItemStack input) {
        if (!input.has(AllDataComponents.SEQUENCED_ASSEMBLY)) {
            return 0;
        }
        return ((SequencedAssembly)input.get(AllDataComponents.SEQUENCED_ASSEMBLY)).step();
    }

    public boolean matches(RecipeWrapper inv, Level level) {
        return false;
    }

    public ItemStack assemble(RecipeWrapper input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.resultPool.getFirst().getStack();
    }

    public float getOutputChance() {
        float totalWeight = 0.0f;
        for (ProcessingOutput entry : this.resultPool) {
            totalWeight += entry.getChance();
        }
        return this.resultPool.getFirst().getChance() / totalWeight;
    }

    public RecipeSerializer<?> getSerializer() {
        return this.serializer;
    }

    public boolean isSpecial() {
        return true;
    }

    public RecipeType<?> getType() {
        return AllRecipeTypes.SEQUENCED_ASSEMBLY.getType();
    }

    @OnlyIn(value=Dist.CLIENT)
    public static void addToTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!stack.has(AllDataComponents.SEQUENCED_ASSEMBLY)) {
            return;
        }
        SequencedAssembly sequencedAssembly = (SequencedAssembly)stack.get(AllDataComponents.SEQUENCED_ASSEMBLY);
        Optional optionalRecipe = Minecraft.getInstance().level.getRecipeManager().byKey(sequencedAssembly.id());
        if (optionalRecipe.isEmpty()) {
            return;
        }
        Recipe recipe = ((RecipeHolder)optionalRecipe.get()).value();
        if (!(recipe instanceof SequencedAssemblyRecipe)) {
            return;
        }
        SequencedAssemblyRecipe sequencedAssemblyRecipe = (SequencedAssemblyRecipe)recipe;
        int length = sequencedAssemblyRecipe.sequence.size();
        int step = sequencedAssemblyRecipe.getStep(stack);
        int total = length * sequencedAssemblyRecipe.loops;
        List tooltip = event.getToolTip();
        tooltip.add(CommonComponents.EMPTY);
        tooltip.add(CreateLang.translateDirect("recipe.sequenced_assembly", new Object[0]).withStyle(ChatFormatting.GRAY));
        tooltip.add(CreateLang.translateDirect("recipe.assembly.progress", step, total).withStyle(ChatFormatting.DARK_GRAY));
        int remaining = total - step;
        for (int i = 0; i < length && i < remaining; ++i) {
            SequencedRecipe<?> sequencedRecipe = sequencedAssemblyRecipe.sequence.get((i + step) % length);
            Component textComponent = sequencedRecipe.getAsAssemblyRecipe().getDescriptionForAssembly();
            if (i == 0) {
                tooltip.add(CreateLang.translateDirect("recipe.assembly.next", textComponent).withStyle(ChatFormatting.AQUA));
                continue;
            }
            tooltip.add(Component.literal((String)"-> ").append(textComponent).withStyle(ChatFormatting.DARK_AQUA));
        }
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public List<SequencedRecipe<?>> getSequence() {
        return this.sequence;
    }

    public ItemStack getTransitionalItem() {
        return this.transitionalItem.getStack();
    }

    public record SequencedAssembly(ResourceLocation id, int step, float progress) {
        public static final Codec<SequencedAssembly> CODEC = RecordCodecBuilder.create(i -> i.group((App)ResourceLocation.CODEC.fieldOf("id").forGetter(SequencedAssembly::id), (App)Codec.INT.fieldOf("step").forGetter(SequencedAssembly::step), (App)Codec.FLOAT.fieldOf("progress").forGetter(SequencedAssembly::progress)).apply((Applicative)i, SequencedAssembly::new));
        public static final StreamCodec<ByteBuf, SequencedAssembly> STREAM_CODEC = StreamCodec.composite((StreamCodec)ResourceLocation.STREAM_CODEC, SequencedAssembly::id, (StreamCodec)ByteBufCodecs.INT, SequencedAssembly::step, (StreamCodec)ByteBufCodecs.FLOAT, SequencedAssembly::progress, SequencedAssembly::new);
    }
}
