/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 */
package dev.simulated_team.simulated.data.neoforge;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;

static class SimProcessingRecipeGen.1
implements DataProvider {
    SimProcessingRecipeGen.1() {
    }

    public CompletableFuture<?> run(CachedOutput arg) {
        return CompletableFuture.allOf((CompletableFuture[])GENERATORS.stream().map(gen -> gen.run(arg)).toArray(CompletableFuture[]::new));
    }

    public String getName() {
        return "Simulated's Peculiar Processing Recipes";
    }
}
