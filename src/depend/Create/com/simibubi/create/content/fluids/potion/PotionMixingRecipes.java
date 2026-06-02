/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.alchemy.Potion
 *  net.minecraft.world.item.alchemy.PotionBrewing
 *  net.minecraft.world.item.alchemy.PotionBrewing$Mix
 *  net.minecraft.world.item.alchemy.PotionContents
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.common.brewing.BrewingRecipe
 *  net.neoforged.neoforge.common.brewing.IBrewingRecipe
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.crafting.DataComponentFluidIngredient
 *  net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
 */
package com.simibubi.create.content.fluids.potion;

import com.simibubi.create.Create;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.mixin.accessor.PotionBrewingAccessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.DataComponentFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class PotionMixingRecipes {
    public static final List<Item> SUPPORTED_CONTAINERS = List.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
    private static List<RecipeHolder<MixingRecipe>> RECIPES;
    private static Map<Item, List<MixingRecipe>> SORTED;
    private static boolean alreadyGenerated;
    private static boolean alreadySorted;

    public static List<RecipeHolder<MixingRecipe>> createRecipes(Level level) {
        if (!alreadyGenerated) {
            RECIPES = PotionMixingRecipes.createRecipesImpl(level);
            alreadyGenerated = true;
        }
        return RECIPES;
    }

    public static Map<Item, List<MixingRecipe>> sortRecipesByItem(Level level) {
        if (!alreadySorted) {
            SORTED = PotionMixingRecipes.sortRecipesByItem(PotionMixingRecipes.createRecipes(level));
            alreadySorted = true;
        }
        return SORTED;
    }

    private static List<RecipeHolder<MixingRecipe>> createRecipesImpl(Level level) {
        PotionBrewing potionBrewing = level.potionBrewing();
        ArrayList<RecipeHolder<MixingRecipe>> mixingRecipes = new ArrayList<RecipeHolder<MixingRecipe>>();
        int recipeIndex = 0;
        ArrayList<Item> allowedSupportedContainers = new ArrayList<Item>();
        ArrayList<ItemStack> supportedContainerStacks = new ArrayList<ItemStack>();
        for (Item item : SUPPORTED_CONTAINERS) {
            ItemStack stack = new ItemStack((ItemLike)item);
            supportedContainerStacks.add(stack);
            if (!((PotionBrewingAccessor)potionBrewing).create$isContainer(stack)) continue;
            allowedSupportedContainers.add(item);
        }
        for (Item item : allowedSupportedContainers) {
            PotionFluid.BottleType bottleType = PotionFluidHandler.bottleTypeFromItem(item);
            for (PotionBrewing.Mix<Potion> mix : ((PotionBrewingAccessor)potionBrewing).create$getPotionMixes()) {
                FluidStack fromFluid = PotionFluidHandler.getFluidFromPotion(new PotionContents(mix.from()), bottleType, 1000);
                FluidStack toFluid = PotionFluidHandler.getFluidFromPotion(new PotionContents(mix.to()), bottleType, 1000);
                mixingRecipes.add(PotionMixingRecipes.createRecipe("potion_mixing_vanilla_" + recipeIndex++, mix.ingredient(), fromFluid, toFluid));
            }
        }
        for (PotionBrewing.Mix mix : ((PotionBrewingAccessor)potionBrewing).create$getContainerMixes()) {
            Item to;
            Item from = (Item)mix.from().value();
            if (!allowedSupportedContainers.contains(from) || !allowedSupportedContainers.contains(to = (Item)mix.to().value())) continue;
            PotionFluid.BottleType fromBottleType = PotionFluidHandler.bottleTypeFromItem(from);
            PotionFluid.BottleType toBottleType = PotionFluidHandler.bottleTypeFromItem(to);
            Ingredient ingredient = mix.ingredient();
            List potions = level.registryAccess().lookupOrThrow(Registries.POTION).listElements().toList();
            for (Holder.Reference potion : potions) {
                FluidStack fromFluid = PotionFluidHandler.getFluidFromPotion(new PotionContents((Holder)potion), fromBottleType, 1000);
                FluidStack toFluid = PotionFluidHandler.getFluidFromPotion(new PotionContents((Holder)potion), toBottleType, 1000);
                mixingRecipes.add(PotionMixingRecipes.createRecipe("potion_mixing_vanilla_" + recipeIndex++, ingredient, fromFluid, toFluid));
            }
        }
        recipeIndex = 0;
        for (IBrewingRecipe iBrewingRecipe : potionBrewing.getRecipes()) {
            BrewingRecipe recipeImpl;
            ItemStack output;
            if (!(iBrewingRecipe instanceof BrewingRecipe) || !SUPPORTED_CONTAINERS.contains((output = (recipeImpl = (BrewingRecipe)iBrewingRecipe).getOutput()).getItem())) continue;
            Ingredient input = recipeImpl.getInput();
            Ingredient ingredient = recipeImpl.getIngredient();
            FluidStack outputFluid = null;
            for (ItemStack stack : supportedContainerStacks) {
                ItemStack[] stacks;
                if (!input.test(stack) || (stacks = input.getItems()).length == 0) continue;
                FluidStack inputFluid = PotionFluidHandler.getFluidFromPotionItem(stacks[0]);
                inputFluid.setAmount(1000);
                if (outputFluid == null) {
                    outputFluid = PotionFluidHandler.getFluidFromPotionItem(output);
                }
                outputFluid.setAmount(1000);
                mixingRecipes.add(PotionMixingRecipes.createRecipe("potion_mixing_modded_" + recipeIndex++, ingredient, inputFluid, outputFluid));
            }
        }
        return mixingRecipes;
    }

    private static RecipeHolder<MixingRecipe> createRecipe(String id, Ingredient ingredient, FluidStack fromFluid, FluidStack toFluid) {
        ResourceLocation recipeId = Create.asResource(id);
        MixingRecipe recipe = (MixingRecipe)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)new StandardProcessingRecipe.Builder<MixingRecipe>(MixingRecipe::new, recipeId).require(ingredient)).require(new SizedFluidIngredient(DataComponentFluidIngredient.of((boolean)false, (FluidStack)fromFluid), fromFluid.getAmount()))).output(toFluid)).requiresHeat(HeatCondition.HEATED)).build();
        return new RecipeHolder(recipeId, (Recipe)recipe);
    }

    private static Map<Item, List<MixingRecipe>> sortRecipesByItem(List<RecipeHolder<MixingRecipe>> all) {
        HashMap<Item, List<MixingRecipe>> byItem = new HashMap<Item, List<MixingRecipe>>();
        HashSet<Item> processedItems = new HashSet<Item>();
        for (RecipeHolder<MixingRecipe> recipe : all) {
            for (Ingredient ingredient : ((MixingRecipe)recipe.value()).getIngredients()) {
                for (ItemStack itemStack : ingredient.getItems()) {
                    Item item = itemStack.getItem();
                    if (!processedItems.add(item)) continue;
                    byItem.computeIfAbsent(item, i -> new ArrayList()).add((MixingRecipe)recipe.value());
                }
            }
            processedItems.clear();
        }
        return byItem;
    }

    static {
        alreadyGenerated = false;
        alreadySorted = false;
    }
}
