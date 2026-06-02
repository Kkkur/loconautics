/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.data.recipes.RecipeOutput
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingBookCategory
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.ShapedRecipePattern
 *  net.minecraft.world.level.ItemLike
 *  net.neoforged.neoforge.common.conditions.ICondition
 *  net.neoforged.neoforge.common.conditions.ModLoadedCondition
 *  net.neoforged.neoforge.common.conditions.NotCondition
 */
package com.simibubi.create.api.data.recipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;

public class MechanicalCraftingRecipeBuilder {
    private final Item result;
    private final int count;
    private final List<String> pattern = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private boolean acceptMirrored;
    private final List<ICondition> recipeConditions;

    public MechanicalCraftingRecipeBuilder(ItemLike result, int resultCount) {
        this.result = result.asItem();
        this.count = resultCount;
        this.acceptMirrored = true;
        this.recipeConditions = new ArrayList<ICondition>();
    }

    public static MechanicalCraftingRecipeBuilder shapedRecipe(ItemLike result) {
        return MechanicalCraftingRecipeBuilder.shapedRecipe(result, 1);
    }

    public static MechanicalCraftingRecipeBuilder shapedRecipe(ItemLike result, int resultCount) {
        return new MechanicalCraftingRecipeBuilder(result, resultCount);
    }

    public MechanicalCraftingRecipeBuilder key(Character c, TagKey<Item> tag) {
        return this.key(c, Ingredient.of(tag));
    }

    public MechanicalCraftingRecipeBuilder key(Character c, ItemLike item) {
        return this.key(c, Ingredient.of((ItemLike[])new ItemLike[]{item}));
    }

    public MechanicalCraftingRecipeBuilder key(Character c, Ingredient ingredient) {
        if (this.key.containsKey(c)) {
            throw new IllegalArgumentException("Symbol '" + c + "' is already defined!");
        }
        if (c.charValue() == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        }
        this.key.put(c, ingredient);
        return this;
    }

    public MechanicalCraftingRecipeBuilder patternLine(String line) {
        if (!this.pattern.isEmpty() && line.length() != this.pattern.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        }
        this.pattern.add(line);
        return this;
    }

    public MechanicalCraftingRecipeBuilder disallowMirrored() {
        this.acceptMirrored = false;
        return this;
    }

    public void build(RecipeOutput output) {
        this.build(output, RegisteredObjectsHelper.getKeyOrThrow((Item)this.result));
    }

    public void build(RecipeOutput output, String id) {
        ResourceLocation resourcelocation = RegisteredObjectsHelper.getKeyOrThrow((Item)this.result);
        ResourceLocation idRs = ResourceLocation.parse((String)id);
        if (idRs.equals((Object)resourcelocation)) {
            throw new IllegalStateException("Shaped Recipe " + id + " should remove its 'id' argument");
        }
        this.build(output, idRs);
    }

    public void build(RecipeOutput output, ResourceLocation id) {
        this.validate(id);
        MechanicalCraftingRecipe recipe = new MechanicalCraftingRecipe("", CraftingBookCategory.MISC, ShapedRecipePattern.of(this.key, this.pattern), new ItemStack((ItemLike)this.result, this.count), this.acceptMirrored);
        output.accept(id, (Recipe)recipe, null, (ICondition[])this.recipeConditions.toArray(ICondition[]::new));
    }

    private void validate(ResourceLocation recipeId) {
        if (this.pattern.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + String.valueOf(recipeId) + "!");
        }
        HashSet set = Sets.newHashSet(this.key.keySet());
        set.remove(Character.valueOf(' '));
        for (String s : this.pattern) {
            for (int i = 0; i < s.length(); ++i) {
                char c0 = s.charAt(i);
                if (!this.key.containsKey(Character.valueOf(c0)) && c0 != ' ') {
                    throw new IllegalStateException("Pattern in recipe " + String.valueOf(recipeId) + " uses undefined symbol '" + c0 + "'");
                }
                set.remove(Character.valueOf(c0));
            }
        }
        if (!set.isEmpty()) {
            throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + String.valueOf(recipeId));
        }
    }

    public MechanicalCraftingRecipeBuilder whenModLoaded(String modid) {
        return this.withCondition((ICondition)new ModLoadedCondition(modid));
    }

    public MechanicalCraftingRecipeBuilder whenModMissing(String modid) {
        return this.withCondition((ICondition)new NotCondition((ICondition)new ModLoadedCondition(modid)));
    }

    public MechanicalCraftingRecipeBuilder withCondition(ICondition condition) {
        this.recipeConditions.add(condition);
        return this;
    }
}
