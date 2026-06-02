/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  mezz.jei.api.gui.drawable.IDrawable
 *  mezz.jei.api.recipe.RecipeType
 *  net.createmod.catnip.config.ConfigBase$ConfigBool
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.level.ItemLike
 */
package com.simibubi.create.compat.jei.category;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.Create;
import com.simibubi.create.compat.jei.CreateJEI;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.ItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.RecipeType;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.ItemLike;

public static class CreateRecipeCategory.Builder<T extends Recipe<? extends RecipeInput>> {
    private final Class<? extends T> recipeClass;
    private Supplier<Boolean> config = () -> true;
    private IDrawable background;
    private IDrawable icon;
    private final List<Consumer<List<RecipeHolder<T>>>> recipeListConsumers = new ArrayList<Consumer<List<RecipeHolder<T>>>>();
    private final List<Supplier<? extends ItemStack>> catalysts = new ArrayList<Supplier<? extends ItemStack>>();

    public CreateRecipeCategory.Builder(Class<? extends T> recipeClass) {
        this.recipeClass = recipeClass;
    }

    public CreateRecipeCategory.Builder<T> enableWhen(Supplier<Boolean> predicate) {
        this.config = predicate;
        return this;
    }

    public CreateRecipeCategory.Builder<T> enableWhen(ConfigBase.ConfigBool configValue) {
        this.config = () -> ((ConfigBase.ConfigBool)configValue).get();
        return this;
    }

    public CreateRecipeCategory.Builder<T> addRecipeListConsumer(Consumer<List<RecipeHolder<T>>> consumer) {
        this.recipeListConsumers.add(consumer);
        return this;
    }

    public CreateRecipeCategory.Builder<T> addRecipes(Supplier<Collection<? extends RecipeHolder<T>>> collection) {
        return this.addRecipeListConsumer(recipes -> recipes.addAll((Collection)collection.get()));
    }

    public CreateRecipeCategory.Builder<T> addAllRecipesIf(Predicate<RecipeHolder<T>> pred) {
        return this.addRecipeListConsumer(recipes -> this.consumeAllRecipesOfType(recipe -> {
            if (pred.test((RecipeHolder)recipe)) {
                recipes.add(recipe);
            }
        }));
    }

    public CreateRecipeCategory.Builder<T> addAllRecipesIf(Predicate<RecipeHolder<?>> pred, Function<RecipeHolder<?>, RecipeHolder<T>> converter) {
        return this.addRecipeListConsumer(recipes -> CreateJEI.consumeAllRecipes(recipe -> {
            if (pred.test((RecipeHolder<?>)recipe)) {
                recipes.add((RecipeHolder)converter.apply((RecipeHolder<?>)recipe));
            }
        }));
    }

    public CreateRecipeCategory.Builder<T> addTypedRecipes(IRecipeTypeInfo recipeTypeEntry) {
        return this.addTypedRecipes(recipeTypeEntry::getType);
    }

    public <I extends RecipeInput, R extends Recipe<I>> CreateRecipeCategory.Builder<T> addTypedRecipes(Supplier<net.minecraft.world.item.crafting.RecipeType<R>> recipeType) {
        return this.addRecipeListConsumer(recipes -> CreateJEI.consumeTypedRecipes(recipe -> {
            if (this.recipeClass.isInstance(recipe.value())) {
                recipes.add(recipe);
            }
        }, (net.minecraft.world.item.crafting.RecipeType)recipeType.get()));
    }

    public CreateRecipeCategory.Builder<T> addTypedRecipes(Supplier<net.minecraft.world.item.crafting.RecipeType<T>> recipeType, Function<RecipeHolder<?>, RecipeHolder<T>> converter) {
        return this.addRecipeListConsumer(recipes -> CreateJEI.consumeTypedRecipes(recipe -> recipes.add((RecipeHolder)converter.apply((RecipeHolder<?>)recipe)), (net.minecraft.world.item.crafting.RecipeType)recipeType.get()));
    }

    public CreateRecipeCategory.Builder<T> addTypedRecipesIf(Supplier<net.minecraft.world.item.crafting.RecipeType<? extends T>> recipeType, Predicate<RecipeHolder<?>> pred) {
        return this.addRecipeListConsumer(recipes -> this.consumeTypedRecipesTyped(recipe -> {
            if (pred.test((RecipeHolder<?>)recipe)) {
                recipes.add(recipe);
            }
        }, (net.minecraft.world.item.crafting.RecipeType)recipeType.get()));
    }

    public CreateRecipeCategory.Builder<T> addTypedRecipesExcluding(Supplier<net.minecraft.world.item.crafting.RecipeType<? extends T>> recipeType, Supplier<net.minecraft.world.item.crafting.RecipeType<? extends T>> excluded) {
        return this.addRecipeListConsumer(recipes -> {
            List<RecipeHolder<?>> excludedRecipes = CreateJEI.getTypedRecipes((net.minecraft.world.item.crafting.RecipeType)excluded.get());
            this.consumeTypedRecipesTyped(recipe -> {
                for (RecipeHolder excludedRecipe : excludedRecipes) {
                    if (!CreateJEI.doInputsMatch(recipe.value(), excludedRecipe.value())) continue;
                    return;
                }
                recipes.add(recipe);
            }, (net.minecraft.world.item.crafting.RecipeType)recipeType.get());
        });
    }

    public CreateRecipeCategory.Builder<T> removeRecipes(Supplier<net.minecraft.world.item.crafting.RecipeType<? extends T>> recipeType) {
        return this.addRecipeListConsumer(recipes -> {
            List<RecipeHolder<?>> excludedRecipes = CreateJEI.getTypedRecipes((net.minecraft.world.item.crafting.RecipeType)recipeType.get());
            recipes.removeIf(recipe -> {
                for (RecipeHolder excludedRecipe : excludedRecipes) {
                    if (!CreateJEI.doInputsMatch(recipe.value(), excludedRecipe.value()) || !CreateJEI.doOutputsMatch(recipe.value(), excludedRecipe.value())) continue;
                    return true;
                }
                return false;
            });
        });
    }

    public CreateRecipeCategory.Builder<T> removeNonAutomation() {
        return this.addRecipeListConsumer(recipes -> recipes.removeIf(AllRecipeTypes.CAN_BE_AUTOMATED.negate()));
    }

    public CreateRecipeCategory.Builder<T> catalystStack(Supplier<ItemStack> supplier) {
        this.catalysts.add(supplier);
        return this;
    }

    public CreateRecipeCategory.Builder<T> catalyst(Supplier<ItemLike> supplier) {
        return this.catalystStack(() -> new ItemStack((ItemLike)((ItemLike)supplier.get()).asItem()));
    }

    public CreateRecipeCategory.Builder<T> icon(IDrawable icon) {
        this.icon = icon;
        return this;
    }

    public CreateRecipeCategory.Builder<T> itemIcon(ItemLike item) {
        this.icon(new ItemIcon(() -> new ItemStack(item)));
        return this;
    }

    public CreateRecipeCategory.Builder<T> doubleItemIcon(ItemLike item1, ItemLike item2) {
        this.icon(new DoubleItemIcon(() -> new ItemStack(item1), () -> new ItemStack(item2)));
        return this;
    }

    public CreateRecipeCategory.Builder<T> background(IDrawable background) {
        this.background = background;
        return this;
    }

    public CreateRecipeCategory.Builder<T> emptyBackground(int width, int height) {
        this.background(new EmptyBackground(width, height));
        return this;
    }

    public CreateRecipeCategory<T> build(String name, CreateRecipeCategory.Factory<T> factory) {
        return this.build(Create.asResource(name), factory);
    }

    public CreateRecipeCategory<T> build(ResourceLocation id, CreateRecipeCategory.Factory<T> factory) {
        Supplier<List<Object>> recipesSupplier = this.config.get() != false ? () -> {
            ArrayList recipes = new ArrayList();
            for (Consumer consumer : this.recipeListConsumers) {
                consumer.accept(recipes);
            }
            return recipes;
        } : Collections::emptyList;
        CreateRecipeCategory.Info info = new CreateRecipeCategory.Info(RecipeType.createRecipeHolderType((ResourceLocation)id), (Component)Component.translatable((String)(id.getNamespace() + ".recipe." + id.getPath())), this.background, this.icon, recipesSupplier, this.catalysts);
        return factory.create(info);
    }

    private void consumeAllRecipesOfType(Consumer<RecipeHolder<T>> consumer) {
        CreateJEI.consumeAllRecipes(recipeHolder -> {
            if (this.recipeClass.isInstance(recipeHolder.value())) {
                consumer.accept((RecipeHolder<T>)recipeHolder);
            }
        });
    }

    private void consumeTypedRecipesTyped(Consumer<RecipeHolder<T>> consumer, net.minecraft.world.item.crafting.RecipeType<?> type) {
        CreateJEI.consumeTypedRecipes(recipeHolder -> {
            if (this.recipeClass.isInstance(recipeHolder.value())) {
                consumer.accept((RecipeHolder<T>)recipeHolder);
            }
        }, type);
    }
}
