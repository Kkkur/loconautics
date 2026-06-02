/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllFluids
 *  com.simibubi.create.AllItems
 *  com.simibubi.create.api.data.recipe.BaseRecipeProvider$GeneratedRecipe
 *  com.simibubi.create.api.data.recipe.FillingRecipeGen
 *  com.simibubi.create.content.processing.recipe.StandardProcessingRecipe$Builder
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.material.FlowingFluid
 */
package dev.simulated_team.simulated.data.neoforge;

import com.simibubi.create.AllFluids;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.FillingRecipeGen;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import dev.simulated_team.simulated.index.SimItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.FlowingFluid;

public class SimFillingRecipes
extends FillingRecipeGen {
    private final BaseRecipeProvider.GeneratedRecipe HONEY_GLUE = this.create("honey_glue", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require((FlowingFluid)AllFluids.HONEY.get(), 500)).require((ItemLike)AllItems.IRON_SHEET)).output(SimItems.HONEY_GLUE));

    public SimFillingRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, "simulated");
    }

    public String getName() {
        return "Simulated's Fantastic Filling Recipes";
    }
}
