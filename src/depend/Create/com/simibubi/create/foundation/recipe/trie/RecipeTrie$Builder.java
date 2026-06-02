/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.level.material.Fluid
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
 */
package com.simibubi.create.foundation.recipe.trie;

import com.simibubi.create.Create;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.recipe.trie.AbstractIngredient;
import com.simibubi.create.foundation.recipe.trie.AbstractRecipe;
import com.simibubi.create.foundation.recipe.trie.AbstractVariant;
import com.simibubi.create.foundation.recipe.trie.IntArrayTrie;
import com.simibubi.create.foundation.recipe.trie.RecipeTrie;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public static class RecipeTrie.Builder<R extends Recipe<?>> {
    private final IntArrayTrie<R> trie = new IntArrayTrie();
    private final Map<Object, AbstractVariant> variantCache = new HashMap<Object, AbstractVariant>();
    private final Object2IntOpenHashMap<AbstractVariant> variantToId = new Object2IntOpenHashMap();
    private int nextVariantId = 0;
    private final Object2IntMap<AbstractIngredient> ingredientToId = new Object2IntOpenHashMap();
    private int nextIngredientId = 0;
    private final int universalIngredientId;
    private final Int2ObjectOpenHashMap<IntSet> variantToIngredients = new Int2ObjectOpenHashMap();

    private RecipeTrie.Builder() {
        this.variantToId.defaultReturnValue(-1);
        this.ingredientToId.defaultReturnValue(-1);
        this.universalIngredientId = this.getOrAssignId(AbstractIngredient.Universal.INSTANCE);
    }

    private int getOrAssignId(AbstractIngredient ingredient) {
        return this.ingredientToId.computeIfAbsent((Object)ingredient, $ -> {
            int id = this.nextIngredientId++;
            for (AbstractVariant variant : ingredient.variants) {
                ((IntSet)this.variantToIngredients.computeIfAbsent(this.getOrAssignId(variant), $1 -> new IntOpenHashSet())).add(id);
            }
            return id;
        });
    }

    private int getOrAssignId(AbstractVariant variant) {
        return this.variantToId.computeIfAbsent((Object)variant, $ -> this.nextVariantId++);
    }

    private AbstractVariant getOrAssignVariant(Item item) {
        AbstractVariant variant = this.variantCache.computeIfAbsent(item, $ -> new AbstractVariant.AbstractItem(item));
        this.getOrAssignId(variant);
        return variant;
    }

    private AbstractVariant getOrAssignVariant(Fluid fluid) {
        AbstractVariant variant = this.variantCache.computeIfAbsent(fluid, $ -> new AbstractVariant.AbstractFluid(fluid));
        this.getOrAssignId(variant);
        return variant;
    }

    private void insert(AbstractRecipe<? extends R> recipe) {
        int[] key = new int[recipe.ingredients.size()];
        int i = 0;
        for (AbstractIngredient ingredient : recipe.ingredients) {
            key[i++] = this.getOrAssignId(ingredient);
        }
        Arrays.sort(key);
        this.trie.insert(key, recipe.recipe);
    }

    public <R1 extends R> void insert(R1 recipe) {
        this.insert((R1)this.createRecipe(recipe));
    }

    private <R1 extends R> AbstractRecipe<R1> createRecipe(R1 recipe) {
        HashSet<AbstractIngredient> ingredients = new HashSet<AbstractIngredient>();
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.isEmpty()) {
                ingredients.add(AbstractIngredient.Universal.INSTANCE);
                continue;
            }
            if (!ingredient.isSimple()) {
                ingredients.add(AbstractIngredient.Universal.INSTANCE);
                continue;
            }
            HashSet<AbstractVariant> variants = new HashSet<AbstractVariant>();
            for (ItemStack stack : ingredient.getItems()) {
                variants.add(this.getOrAssignVariant(stack.getItem()));
            }
            ingredients.add(new AbstractIngredient(variants));
        }
        if (recipe instanceof BasinRecipe) {
            BasinRecipe basinRecipe = (BasinRecipe)recipe;
            for (SizedFluidIngredient ingredient : basinRecipe.getFluidIngredients()) {
                if (ingredient.amount() == 0) {
                    ingredients.add(AbstractIngredient.Universal.INSTANCE);
                    continue;
                }
                HashSet<AbstractVariant> variants = new HashSet<AbstractVariant>();
                for (FluidStack stack : ingredient.getFluids()) {
                    variants.add(this.getOrAssignVariant(stack.getFluid()));
                }
                ingredients.add(new AbstractIngredient(variants));
            }
        }
        return new AbstractRecipe<R1>(recipe, ingredients);
    }

    public RecipeTrie<R> build() {
        this.variantToId.trim();
        this.variantToIngredients.trim();
        Create.LOGGER.info("RecipeTrie of depth {} with {} nodes built with {} variants, {} ingredients, and {} recipes", new Object[]{this.trie.getMaxDepth(), this.trie.getNodeCount(), this.variantToId.size(), this.ingredientToId.size(), this.trie.getValueCount()});
        return new RecipeTrie<R>(this.trie, (Object2IntMap<AbstractVariant>)this.variantToId, (Int2ObjectMap<IntSet>)this.variantToIngredients, this.universalIngredientId);
    }
}
