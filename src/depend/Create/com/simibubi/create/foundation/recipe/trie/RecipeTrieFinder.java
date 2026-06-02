/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  net.minecraft.server.packs.resources.ResourceManagerReloadListener
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.foundation.recipe.trie;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.recipe.trie.RecipeTrie;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class RecipeTrieFinder {
    private static final Cache<Object, RecipeTrie<?>> CACHED_TRIES = CacheBuilder.newBuilder().build();
    public static final ResourceManagerReloadListener LISTENER = resourceManager -> CACHED_TRIES.invalidateAll();

    public static RecipeTrie<?> get(@NotNull Object cacheKey, Level world, Predicate<RecipeHolder<? extends Recipe<?>>> conditions) throws ExecutionException {
        return (RecipeTrie)CACHED_TRIES.get(cacheKey, () -> {
            List<RecipeHolder<Recipe<?>>> list = RecipeFinder.get(cacheKey, world, conditions);
            RecipeTrie.Builder builder = RecipeTrie.builder();
            for (RecipeHolder<? extends Recipe<?>> recipeHolder : list) {
                builder.insert(recipeHolder.value());
            }
            return builder.build();
        });
    }
}
