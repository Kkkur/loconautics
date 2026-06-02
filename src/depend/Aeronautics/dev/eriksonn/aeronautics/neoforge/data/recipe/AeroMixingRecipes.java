/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllItems
 *  com.simibubi.create.api.data.recipe.BaseRecipeProvider$GeneratedRecipe
 *  com.simibubi.create.api.data.recipe.MixingRecipeGen
 *  com.simibubi.create.content.processing.recipe.HeatCondition
 *  com.simibubi.create.content.processing.recipe.StandardProcessingRecipe$Builder
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.material.Fluid
 *  net.neoforged.neoforge.common.Tags$Fluids
 *  org.jetbrains.annotations.NotNull
 */
package dev.eriksonn.aeronautics.neoforge.data.recipe;

import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.MixingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import dev.eriksonn.aeronautics.index.AeroItems;
import dev.eriksonn.aeronautics.neoforge.index.AeroFluidsNeoForge;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class AeroMixingRecipes
extends MixingRecipeGen {
    BaseRecipeProvider.GeneratedRecipe LEVITITE_BLEND = this.create("levitite_blend", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require(AeroItems.ENDSTONE_POWDER)).require(AeroItems.ENDSTONE_POWDER)).require(AeroItems.ENDSTONE_POWDER)).require(AeroItems.ENDSTONE_POWDER)).require((ItemLike)AllItems.ZINC_NUGGET)).require((ItemLike)AllItems.ZINC_NUGGET)).require(Tags.Fluids.WATER, 500)).output((Fluid)AeroFluidsNeoForge.LEVITITE_BLEND.get(), 500)).requiresHeat(HeatCondition.HEATED));

    public AeroMixingRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, "aeronautics");
    }

    @NotNull
    public String getName() {
        return "Aero's Miraculous Mixing Recipes";
    }
}
