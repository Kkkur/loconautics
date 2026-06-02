/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 */
package com.simibubi.create.foundation.data.recipe;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;

static class CreateRecipeProvider.1
implements DataProvider {
    CreateRecipeProvider.1() {
    }

    public String getName() {
        return "Create's Processing Recipes";
    }

    public CompletableFuture<?> run(CachedOutput dc) {
        return CompletableFuture.allOf((CompletableFuture[])GENERATORS.stream().map(gen -> gen.run(dc)).toArray(CompletableFuture[]::new));
    }
}
