/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.SingleRecipeInput
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.equipment.sandPaper;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@ParametersAreNonnullByDefault
public class SandPaperPolishingRecipe
extends StandardProcessingRecipe<SingleRecipeInput> {
    public SandPaperPolishingRecipe(ProcessingRecipeParams params) {
        super((IRecipeTypeInfo)AllRecipeTypes.SANDPAPER_POLISHING, params);
    }

    public boolean matches(SingleRecipeInput inv, Level worldIn) {
        return ((Ingredient)this.ingredients.get(0)).test(inv.getItem(0));
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
    }

    public static boolean canPolish(Level world, ItemStack stack) {
        return !SandPaperPolishingRecipe.getMatchingRecipes(world, stack).isEmpty();
    }

    public static ItemStack applyPolish(Level world, Vec3 position, ItemStack stack, ItemStack sandPaperStack) {
        List<RecipeHolder<Recipe<SingleRecipeInput>>> matchingRecipes = SandPaperPolishingRecipe.getMatchingRecipes(world, stack);
        if (!matchingRecipes.isEmpty()) {
            return matchingRecipes.get(0).value().assemble((RecipeInput)new SingleRecipeInput(stack), (HolderLookup.Provider)world.registryAccess()).copy();
        }
        return stack;
    }

    public static List<RecipeHolder<Recipe<SingleRecipeInput>>> getMatchingRecipes(Level world, ItemStack stack) {
        return world.getRecipeManager().getRecipesFor(AllRecipeTypes.SANDPAPER_POLISHING.getType(), (RecipeInput)new SingleRecipeInput(stack), world);
    }
}
