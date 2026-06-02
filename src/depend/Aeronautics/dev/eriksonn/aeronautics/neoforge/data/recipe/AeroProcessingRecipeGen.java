/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.data.recipe.BaseRecipeProvider
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 */
package dev.eriksonn.aeronautics.neoforge.data.recipe;

import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import dev.eriksonn.aeronautics.neoforge.data.recipe.AeroCrushingRecipes;
import dev.eriksonn.aeronautics.neoforge.data.recipe.AeroDeployingRecipes;
import dev.eriksonn.aeronautics.neoforge.data.recipe.AeroMechanicalCraftingRecipes;
import dev.eriksonn.aeronautics.neoforge.data.recipe.AeroMixingRecipes;
import dev.eriksonn.aeronautics.neoforge.data.recipe.AeroWashingRecipes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

public class AeroProcessingRecipeGen {
    protected static List<BaseRecipeProvider> GENERATORS = new ArrayList<BaseRecipeProvider>();

    public static DataProvider registerAll(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        GENERATORS.add((BaseRecipeProvider)new AeroMixingRecipes(output, lookupProvider));
        GENERATORS.add((BaseRecipeProvider)new AeroCrushingRecipes(output, lookupProvider));
        GENERATORS.add((BaseRecipeProvider)new AeroMechanicalCraftingRecipes(output, lookupProvider));
        GENERATORS.add((BaseRecipeProvider)new AeroWashingRecipes(output, lookupProvider));
        GENERATORS.add((BaseRecipeProvider)new AeroDeployingRecipes(output, lookupProvider));
        return new DataProvider(){

            public CompletableFuture<?> run(CachedOutput cachedOutput) {
                return CompletableFuture.allOf((CompletableFuture[])GENERATORS.stream().map(gen -> gen.run(cachedOutput)).toArray(CompletableFuture[]::new));
            }

            public String getName() {
                return "Aero's Perfect Processing Recipes";
            }
        };
    }
}
