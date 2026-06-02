/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.data.recipe.BaseRecipeProvider
 *  com.simibubi.create.api.data.recipe.BaseRecipeProvider$GeneratedRecipe
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.recipes.SpecialRecipeBuilder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.crafting.CraftingBookCategory
 *  net.minecraft.world.item.crafting.Recipe
 */
package dev.simulated_team.simulated.data.neoforge;

import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.data.neoforge.PortableEngineDyeingRecipe;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Recipe;

public class SimStandardRecipeGen
extends BaseRecipeProvider {
    BaseRecipeProvider.GeneratedRecipe PORTABLE_ENGINE_DYEING = this.createSpecial(PortableEngineDyeingRecipe::new, "crafting", "portable_engine_dyeing");

    public SimStandardRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, "simulated");
    }

    public String getName() {
        return "Simulated's Surprisingly Standard Recipes";
    }

    private BaseRecipeProvider.GeneratedRecipe createSpecial(Function<CraftingBookCategory, Recipe<?>> builder, String recipeType, String path) {
        ResourceLocation location = Simulated.path(recipeType + "/" + path);
        return this.register(consumer -> {
            SpecialRecipeBuilder b = SpecialRecipeBuilder.special((Function)builder);
            b.save(consumer, location.toString());
        });
    }
}
