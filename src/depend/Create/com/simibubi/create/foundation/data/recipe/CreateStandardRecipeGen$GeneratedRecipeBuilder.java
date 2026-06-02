/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.advancements.critereon.ItemPredicate
 *  net.minecraft.advancements.critereon.ItemPredicate$Builder
 *  net.minecraft.data.recipes.RecipeCategory
 *  net.minecraft.data.recipes.RecipeOutput
 *  net.minecraft.data.recipes.ShapedRecipeBuilder
 *  net.minecraft.data.recipes.ShapelessRecipeBuilder
 *  net.minecraft.data.recipes.SimpleCookingRecipeBuilder
 *  net.minecraft.data.recipes.SmithingTransformRecipeBuilder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
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
 *  net.neoforged.neoforge.common.conditions.ModLoadedCondition
 *  net.neoforged.neoforge.common.conditions.NotCondition
 */
package com.simibubi.create.foundation.data.recipe;

import com.google.common.base.Supplier;
import com.simibubi.create.Create;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.foundation.data.recipe.CreateStandardRecipeGen;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
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
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;

class CreateStandardRecipeGen.GeneratedRecipeBuilder {
    private String path;
    private String suffix;
    private Supplier<? extends ItemLike> result;
    private ResourceLocation compatDatagenOutput;
    List<ICondition> recipeConditions;
    private Supplier<ItemPredicate> unlockedBy;
    private int amount;

    private CreateStandardRecipeGen.GeneratedRecipeBuilder(String path) {
        this.path = path;
        this.recipeConditions = new ArrayList<ICondition>();
        this.suffix = "";
        this.amount = 1;
    }

    public CreateStandardRecipeGen.GeneratedRecipeBuilder(String path, Supplier<? extends ItemLike> result) {
        this(path);
        this.result = result;
    }

    public CreateStandardRecipeGen.GeneratedRecipeBuilder(String path, ResourceLocation result) {
        this(path);
        this.compatDatagenOutput = result;
    }

    CreateStandardRecipeGen.GeneratedRecipeBuilder returns(int amount) {
        this.amount = amount;
        return this;
    }

    CreateStandardRecipeGen.GeneratedRecipeBuilder unlockedBy(Supplier<? extends ItemLike> item) {
        this.unlockedBy = () -> ItemPredicate.Builder.item().of(new ItemLike[]{(ItemLike)item.get()}).build();
        return this;
    }

    CreateStandardRecipeGen.GeneratedRecipeBuilder unlockedByTag(Supplier<TagKey<Item>> tag) {
        this.unlockedBy = () -> ItemPredicate.Builder.item().of((TagKey)tag.get()).build();
        return this;
    }

    CreateStandardRecipeGen.GeneratedRecipeBuilder whenModLoaded(String modid) {
        return this.withCondition((ICondition)new ModLoadedCondition(modid));
    }

    CreateStandardRecipeGen.GeneratedRecipeBuilder whenModMissing(String modid) {
        return this.withCondition((ICondition)new NotCondition((ICondition)new ModLoadedCondition(modid)));
    }

    CreateStandardRecipeGen.GeneratedRecipeBuilder withCondition(ICondition condition) {
        this.recipeConditions.add(condition);
        return this;
    }

    CreateStandardRecipeGen.GeneratedRecipeBuilder withSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    BaseRecipeProvider.GeneratedRecipe viaShaped(UnaryOperator<ShapedRecipeBuilder> builder) {
        return CreateStandardRecipeGen.this.register(consumer -> {
            ShapedRecipeBuilder b = (ShapedRecipeBuilder)builder.apply(ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)this.result.get()), (int)this.amount));
            if (this.unlockedBy != null) {
                b.unlockedBy("has_item", CreateStandardRecipeGen.inventoryTrigger((ItemPredicate[])new ItemPredicate[]{(ItemPredicate)this.unlockedBy.get()}));
            }
            b.save(consumer, this.createLocation("crafting"));
        });
    }

    BaseRecipeProvider.GeneratedRecipe viaShapeless(UnaryOperator<ShapelessRecipeBuilder> builder) {
        return CreateStandardRecipeGen.this.register(recipeOutput -> {
            ShapelessRecipeBuilder b = (ShapelessRecipeBuilder)builder.apply(ShapelessRecipeBuilder.shapeless((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)this.result.get()), (int)this.amount));
            if (this.unlockedBy != null) {
                b.unlockedBy("has_item", CreateStandardRecipeGen.inventoryTrigger((ItemPredicate[])new ItemPredicate[]{(ItemPredicate)this.unlockedBy.get()}));
            }
            RecipeOutput conditionalOutput = recipeOutput.withConditions(this.recipeConditions.toArray(new ICondition[0]));
            b.save(conditionalOutput, this.createLocation("crafting"));
        });
    }

    BaseRecipeProvider.GeneratedRecipe viaNetheriteSmithing(Supplier<? extends Item> base, Supplier<Ingredient> upgradeMaterial) {
        return CreateStandardRecipeGen.this.register(consumer -> {
            SmithingTransformRecipeBuilder b = SmithingTransformRecipeBuilder.smithing((Ingredient)Ingredient.of((ItemLike[])new ItemLike[]{Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE}), (Ingredient)Ingredient.of((ItemLike[])new ItemLike[]{(ItemLike)base.get()}), (Ingredient)((Ingredient)upgradeMaterial.get()), (RecipeCategory)RecipeCategory.COMBAT, (Item)((ItemLike)this.result.get()).asItem());
            b.unlocks("has_item", CreateStandardRecipeGen.inventoryTrigger((ItemPredicate[])new ItemPredicate[]{ItemPredicate.Builder.item().of(new ItemLike[]{(ItemLike)base.get()}).build()}));
            b.save(consumer, this.createLocation("crafting"));
        });
    }

    private ResourceLocation createSimpleLocation(String recipeType) {
        return Create.asResource(recipeType + "/" + this.getRegistryName().getPath() + this.suffix);
    }

    private ResourceLocation createLocation(String recipeType) {
        return Create.asResource(recipeType + "/" + this.path + "/" + this.getRegistryName().getPath() + this.suffix);
    }

    private ResourceLocation getRegistryName() {
        return this.compatDatagenOutput == null ? RegisteredObjectsHelper.getKeyOrThrow((Item)((ItemLike)this.result.get()).asItem()) : this.compatDatagenOutput;
    }

    GeneratedCookingRecipeBuilder viaCooking(Supplier<? extends ItemLike> item) {
        return this.unlockedBy(item).viaCookingIngredient((Supplier<Ingredient>)((Supplier)() -> Ingredient.of((ItemLike[])new ItemLike[]{(ItemLike)item.get()})));
    }

    GeneratedCookingRecipeBuilder viaCookingTag(Supplier<TagKey<Item>> tag) {
        return this.unlockedByTag(tag).viaCookingIngredient((Supplier<Ingredient>)((Supplier)() -> Ingredient.of((TagKey)((TagKey)tag.get()))));
    }

    GeneratedCookingRecipeBuilder viaCookingIngredient(Supplier<Ingredient> ingredient) {
        return new GeneratedCookingRecipeBuilder(ingredient);
    }

    class GeneratedCookingRecipeBuilder {
        private Supplier<Ingredient> ingredient;
        private float exp;
        private int cookingTime;

        GeneratedCookingRecipeBuilder(Supplier<Ingredient> ingredient) {
            this.ingredient = ingredient;
            this.cookingTime = 200;
            this.exp = 0.0f;
        }

        GeneratedCookingRecipeBuilder forDuration(int duration) {
            this.cookingTime = duration;
            return this;
        }

        GeneratedCookingRecipeBuilder rewardXP(float xp) {
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
            return CreateStandardRecipeGen.this.register(recipeOutput -> {
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
}
