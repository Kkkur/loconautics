/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.advancements.critereon.ItemPredicate
 *  net.minecraft.data.recipes.RecipeCategory
 *  net.minecraft.data.recipes.RecipeOutput
 *  net.minecraft.data.recipes.SimpleCookingRecipeBuilder
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.AbstractCookingRecipe
 *  net.minecraft.world.item.crafting.AbstractCookingRecipe$Factory
 *  net.minecraft.world.item.crafting.BlastingRecipe
 *  net.minecraft.world.item.crafting.CampfireCookingRecipe
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.item.crafting.SmeltingRecipe
 *  net.minecraft.world.item.crafting.SmokingRecipe
 *  net.minecraft.world.level.ItemLike
 *  net.neoforged.neoforge.common.conditions.ICondition
 */
package com.simibubi.create.foundation.data.recipe;

import com.google.common.base.Supplier;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.foundation.data.recipe.CreateStandardRecipeGen;
import java.util.function.UnaryOperator;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;

class CreateStandardRecipeGen.GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder {
    private Supplier<Ingredient> ingredient;
    private float exp;
    private int cookingTime;

    CreateStandardRecipeGen.GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder(Supplier<Ingredient> ingredient) {
        this.ingredient = ingredient;
        this.cookingTime = 200;
        this.exp = 0.0f;
    }

    CreateStandardRecipeGen.GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder forDuration(int duration) {
        this.cookingTime = duration;
        return this;
    }

    CreateStandardRecipeGen.GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder rewardXP(float xp) {
        this.exp = xp;
        return this;
    }

    BaseRecipeProvider.GeneratedRecipe inFurnace() {
        return this.inFurnace(b -> b);
    }

    BaseRecipeProvider.GeneratedRecipe inFurnace(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
        return this.create(RecipeSerializer.SMELTING_RECIPE, builder, SmeltingRecipe::new, 1.0f);
    }

    BaseRecipeProvider.GeneratedRecipe inSmoker() {
        return this.inSmoker(b -> b);
    }

    BaseRecipeProvider.GeneratedRecipe inSmoker(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
        this.create(RecipeSerializer.SMELTING_RECIPE, builder, SmeltingRecipe::new, 1.0f);
        this.create(RecipeSerializer.CAMPFIRE_COOKING_RECIPE, builder, CampfireCookingRecipe::new, 3.0f);
        return this.create(RecipeSerializer.SMOKING_RECIPE, builder, SmokingRecipe::new, 0.5f);
    }

    BaseRecipeProvider.GeneratedRecipe inBlastFurnace() {
        return this.inBlastFurnace(b -> b);
    }

    BaseRecipeProvider.GeneratedRecipe inBlastFurnace(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
        this.create(RecipeSerializer.SMELTING_RECIPE, builder, SmeltingRecipe::new, 1.0f);
        return this.create(RecipeSerializer.BLASTING_RECIPE, builder, BlastingRecipe::new, 0.5f);
    }

    private <T extends AbstractCookingRecipe> BaseRecipeProvider.GeneratedRecipe create(RecipeSerializer<T> serializer, UnaryOperator<SimpleCookingRecipeBuilder> builder, AbstractCookingRecipe.Factory<T> factory, float cookingTimeModifier) {
        return GeneratedRecipeBuilder.this.this$0.register(recipeOutput -> {
            boolean isOtherMod = GeneratedRecipeBuilder.this.compatDatagenOutput != null;
            SimpleCookingRecipeBuilder b = (SimpleCookingRecipeBuilder)builder.apply(SimpleCookingRecipeBuilder.generic((Ingredient)((Ingredient)this.ingredient.get()), (RecipeCategory)RecipeCategory.MISC, (ItemLike)(isOtherMod ? Items.DIRT : (ItemLike)GeneratedRecipeBuilder.this.result.get()), (float)this.exp, (int)((int)((float)this.cookingTime * cookingTimeModifier)), (RecipeSerializer)serializer, (AbstractCookingRecipe.Factory)factory));
            if (GeneratedRecipeBuilder.this.unlockedBy != null) {
                b.unlockedBy("has_item", CreateStandardRecipeGen.inventoryTrigger((ItemPredicate[])new ItemPredicate[]{(ItemPredicate)GeneratedRecipeBuilder.this.unlockedBy.get()}));
            }
            RecipeOutput conditionalOutput = recipeOutput.withConditions(GeneratedRecipeBuilder.this.recipeConditions.toArray(new ICondition[0]));
            b.save((RecipeOutput)(isOtherMod ? new CreateStandardRecipeGen.ModdedCookingRecipeOutput(conditionalOutput, GeneratedRecipeBuilder.this.compatDatagenOutput) : conditionalOutput), GeneratedRecipeBuilder.this.createSimpleLocation(RegisteredObjectsHelper.getKeyOrThrow((RecipeSerializer)serializer).getPath()));
        });
    }
}
