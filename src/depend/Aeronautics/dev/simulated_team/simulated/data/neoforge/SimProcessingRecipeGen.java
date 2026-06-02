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
package dev.simulated_team.simulated.data.neoforge;

import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import dev.simulated_team.simulated.data.neoforge.SimFillingRecipes;
import dev.simulated_team.simulated.data.neoforge.SimMechanicalCraftingRecipes;
import dev.simulated_team.simulated.data.neoforge.SimSequencedAssemblyRecipes;
import dev.simulated_team.simulated.data.neoforge.SimStandardRecipeGen;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

public abstract class SimProcessingRecipeGen
extends BaseRecipeProvider {
    protected static final List<BaseRecipeProvider> GENERATORS = new ArrayList<BaseRecipeProvider>();

    public static DataProvider registerAll(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        GENERATORS.add((BaseRecipeProvider)new SimFillingRecipes(output, lookupProvider));
        GENERATORS.add((BaseRecipeProvider)new SimMechanicalCraftingRecipes(output, lookupProvider));
        GENERATORS.add((BaseRecipeProvider)new SimSequencedAssemblyRecipes(output, lookupProvider));
        GENERATORS.add(new SimStandardRecipeGen(output, lookupProvider));
        return new DataProvider(){

            public CompletableFuture<?> run(CachedOutput arg) {
                return CompletableFuture.allOf((CompletableFuture[])GENERATORS.stream().map(gen -> gen.run(arg)).toArray(CompletableFuture[]::new));
            }

            public String getName() {
                return "Simulated's Peculiar Processing Recipes";
            }
        };
    }

    public SimProcessingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, "simulated");
    }
}
