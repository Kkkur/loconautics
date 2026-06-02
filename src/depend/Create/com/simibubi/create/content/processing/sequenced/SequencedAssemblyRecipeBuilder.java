/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.data.recipes.RecipeOutput
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.level.ItemLike
 *  net.neoforged.neoforge.common.conditions.ICondition
 */
package com.simibubi.create.content.processing.sequenced;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeSerializer;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;

public class SequencedAssemblyRecipeBuilder {
    private ResourceLocation id;
    private SequencedAssemblyRecipe recipe;
    protected List<ICondition> recipeConditions;

    public SequencedAssemblyRecipeBuilder(ResourceLocation id) {
        this.id = id;
        this.recipeConditions = new ArrayList<ICondition>();
        this.recipe = new SequencedAssemblyRecipe((SequencedAssemblyRecipeSerializer)AllRecipeTypes.SEQUENCED_ASSEMBLY.getSerializer());
    }

    public <R extends StandardProcessingRecipe<?>> SequencedAssemblyRecipeBuilder addStep(StandardProcessingRecipe.Factory<R> factory, UnaryOperator<StandardProcessingRecipe.Builder<R>> builder) {
        return this.addStep((ResourceLocation id) -> new StandardProcessingRecipe.Builder(factory, (ResourceLocation)id), builder);
    }

    public <R extends ItemApplicationRecipe> SequencedAssemblyRecipeBuilder addStep(ItemApplicationRecipe.Factory<R> factory, UnaryOperator<ItemApplicationRecipe.Builder<R>> builder) {
        return this.addStep((ResourceLocation id) -> new ItemApplicationRecipe.Builder(factory, (ResourceLocation)id), builder);
    }

    public <B extends ProcessingRecipeBuilder<?, ?, B>> SequencedAssemblyRecipeBuilder addStep(Function<ResourceLocation, B> factory, UnaryOperator<B> builder) {
        ProcessingRecipeBuilder recipeBuilder = (ProcessingRecipeBuilder)factory.apply(ResourceLocation.withDefaultNamespace((String)"dummy"));
        Item placeHolder = this.recipe.getTransitionalItem().getItem();
        this.recipe.getSequence().add(new SequencedRecipe(((ProcessingRecipeBuilder)builder.apply(((ProcessingRecipeBuilder)recipeBuilder.require((ItemLike)placeHolder)).output((ItemLike)placeHolder))).build()));
        return this;
    }

    public SequencedAssemblyRecipeBuilder require(ItemLike ingredient) {
        return this.require(Ingredient.of((ItemLike[])new ItemLike[]{ingredient}));
    }

    public SequencedAssemblyRecipeBuilder require(TagKey<Item> tag) {
        return this.require(Ingredient.of(tag));
    }

    public SequencedAssemblyRecipeBuilder require(Ingredient ingredient) {
        this.recipe.ingredient = ingredient;
        return this;
    }

    public SequencedAssemblyRecipeBuilder transitionTo(ItemLike item) {
        this.recipe.transitionalItem = new ProcessingOutput(item.asItem(), 1, 1.0f);
        return this;
    }

    public SequencedAssemblyRecipeBuilder loops(int loops) {
        this.recipe.loops = loops;
        return this;
    }

    public SequencedAssemblyRecipeBuilder addOutput(ItemLike item, float weight) {
        return this.addOutput(new ItemStack(item), weight);
    }

    public SequencedAssemblyRecipeBuilder addOutput(ItemStack item, float weight) {
        this.recipe.resultPool.add(new ProcessingOutput(item.getItem(), item.getCount(), item.getComponentsPatch(), weight));
        return this;
    }

    public RecipeHolder<SequencedAssemblyRecipe> build() {
        return new RecipeHolder(this.id, (Recipe)this.recipe);
    }

    public void build(RecipeOutput consumer) {
        RecipeHolder<SequencedAssemblyRecipe> holder = this.build();
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath((String)holder.id().getNamespace(), (String)(AllRecipeTypes.SEQUENCED_ASSEMBLY.getId().getPath() + "/" + holder.id().getPath()));
        consumer.accept(id, holder.value(), null, this.recipeConditions.toArray(new ICondition[0]));
    }
}
