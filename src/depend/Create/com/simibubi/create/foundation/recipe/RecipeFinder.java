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
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.recipe;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.simibubi.create.Create;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class RecipeFinder {
    private static final Cache<Object, List<RecipeHolder<? extends Recipe<?>>>> CACHED_SEARCHES = CacheBuilder.newBuilder().build();
    public static final ResourceManagerReloadListener LISTENER = resourceManager -> CACHED_SEARCHES.invalidateAll();

    public static List<RecipeHolder<? extends Recipe<?>>> get(@Nullable Object cacheKey, Level level, Predicate<RecipeHolder<? extends Recipe<?>>> conditions) {
        if (cacheKey == null) {
            return RecipeFinder.startSearch(level, conditions);
        }
        try {
            return (List)CACHED_SEARCHES.get(cacheKey, () -> RecipeFinder.startSearch(level, conditions));
        }
        catch (ExecutionException e) {
            Create.LOGGER.error("Encountered a exception while searching for recipes", (Throwable)e);
            return Collections.emptyList();
        }
    }

    private static List<RecipeHolder<? extends Recipe<?>>> startSearch(Level level, Predicate<? super RecipeHolder<? extends Recipe<?>>> conditions) {
        ArrayList recipes = new ArrayList();
        for (RecipeHolder r : level.getRecipeManager().getRecipes()) {
            if (!conditions.test(r)) continue;
            recipes.add(r);
        }
        return recipes;
    }
}
