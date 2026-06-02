/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 */
package dev.eriksonn.aeronautics.neoforge.data.recipe;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;

static class AeroProcessingRecipeGen.1
implements DataProvider {
    AeroProcessingRecipeGen.1() {
    }

    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        return CompletableFuture.allOf((CompletableFuture[])GENERATORS.stream().map(gen -> gen.run(cachedOutput)).toArray(CompletableFuture[]::new));
    }

    public String getName() {
        return "Aero's Perfect Processing Recipes";
    }
}
