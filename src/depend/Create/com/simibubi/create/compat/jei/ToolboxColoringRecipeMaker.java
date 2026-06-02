/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.NonNullList
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.DyeItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingBookCategory
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Ingredient$ItemValue
 *  net.minecraft.world.item.crafting.Ingredient$TagValue
 *  net.minecraft.world.item.crafting.Ingredient$Value
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.ShapelessRecipe
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 */
package com.simibubi.create.compat.jei;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import java.util.Arrays;
import java.util.stream.Stream;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public final class ToolboxColoringRecipeMaker {
    public static Stream<RecipeHolder<CraftingRecipe>> createRecipes() {
        String group = "create.toolbox.color";
        ItemStack baseShulkerStack = AllBlocks.TOOLBOXES.get(DyeColor.BROWN).asStack();
        Ingredient baseShulkerIngredient = Ingredient.of((ItemStack[])new ItemStack[]{baseShulkerStack});
        return Arrays.stream(DyeColor.values()).filter(dc -> dc != DyeColor.BROWN).map(color -> {
            DyeItem dye = DyeItem.byColor((DyeColor)color);
            ItemStack dyeStack = new ItemStack((ItemLike)dye);
            TagKey colorTag = color.getTag();
            Ingredient.ItemValue dyeList = new Ingredient.ItemValue(dyeStack);
            Ingredient.TagValue colorList = new Ingredient.TagValue(colorTag);
            Stream<Ingredient.Value> colorIngredientStream = Stream.of(dyeList, colorList);
            Ingredient colorIngredient = Ingredient.fromValues(colorIngredientStream);
            NonNullList inputs = NonNullList.of((Object)Ingredient.EMPTY, (Object[])new Ingredient[]{baseShulkerIngredient, colorIngredient});
            Block coloredShulkerBox = (Block)AllBlocks.TOOLBOXES.get((DyeColor)color).get();
            ItemStack output = new ItemStack((ItemLike)coloredShulkerBox);
            ShapelessRecipe recipe = new ShapelessRecipe(group, CraftingBookCategory.MISC, output, inputs);
            return new RecipeHolder(Create.asResource(group + "/" + String.valueOf(color)), (Recipe)recipe);
        });
    }

    private ToolboxColoringRecipeMaker() {
    }
}
