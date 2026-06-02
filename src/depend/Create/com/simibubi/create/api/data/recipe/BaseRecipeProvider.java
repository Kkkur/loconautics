/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.recipes.RecipeOutput
 *  net.minecraft.data.recipes.RecipeProvider
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create.api.data.recipe;

import com.simibubi.create.Create;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;

public abstract class BaseRecipeProvider
extends RecipeProvider {
    protected final String modid;
    protected final List<GeneratedRecipe> all = new ArrayList<GeneratedRecipe>();

    public BaseRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries);
        this.modid = defaultNamespace;
    }

    protected ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath((String)this.modid, (String)path);
    }

    protected GeneratedRecipe register(GeneratedRecipe recipe) {
        this.all.add(recipe);
        return recipe;
    }

    public void buildRecipes(RecipeOutput recipeOutput) {
        this.all.forEach(c -> c.register(recipeOutput));
        Create.LOGGER.info("{} registered {} recipe{}", new Object[]{this.getName(), this.all.size(), this.all.size() == 1 ? "" : "s"});
    }

    @FunctionalInterface
    public static interface GeneratedRecipe {
        public void register(RecipeOutput var1);
    }
}
