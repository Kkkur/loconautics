/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.data.recipe.BaseRecipeProvider$GeneratedRecipe
 *  com.simibubi.create.api.data.recipe.CrushingRecipeGen
 *  com.simibubi.create.content.processing.recipe.StandardProcessingRecipe$Builder
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 *  org.jetbrains.annotations.NotNull
 */
package dev.eriksonn.aeronautics.neoforge.data.recipe;

import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.CrushingRecipeGen;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import dev.eriksonn.aeronautics.index.AeroItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class AeroCrushingRecipes
extends CrushingRecipeGen {
    BaseRecipeProvider.GeneratedRecipe END_STONE_POWDER = this.create("end_stone_powder", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.duration(250)).require((ItemLike)Blocks.END_STONE)).output(0.5f, (ItemLike)Blocks.END_STONE)).output(AeroItems.ENDSTONE_POWDER));

    public AeroCrushingRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, "aeronautics");
    }

    @NotNull
    public String getName() {
        return "Aero's Captivating Crushing Recipes";
    }
}
