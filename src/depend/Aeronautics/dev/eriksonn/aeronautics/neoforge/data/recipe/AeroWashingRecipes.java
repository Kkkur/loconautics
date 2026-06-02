/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.data.recipe.BaseRecipeProvider$GeneratedRecipe
 *  com.simibubi.create.api.data.recipe.WashingRecipeGen
 *  com.simibubi.create.content.processing.recipe.StandardProcessingRecipe$Builder
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.world.level.ItemLike
 *  org.jetbrains.annotations.NotNull
 */
package dev.eriksonn.aeronautics.neoforge.data.recipe;

import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.WashingRecipeGen;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import dev.eriksonn.aeronautics.index.AeroBlocks;
import dev.eriksonn.aeronautics.index.AeroTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

public class AeroWashingRecipes
extends WashingRecipeGen {
    BaseRecipeProvider.GeneratedRecipe ENVELOPE_WASHING = this.create("envelope_washing", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require(AeroTags.ItemTags.SHAFTLESS_ENVELOPE)).output((ItemLike)AeroBlocks.WHITE_ENVELOPE_BLOCK.asItem()));

    public AeroWashingRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, "aeronautics");
    }

    @NotNull
    public String getName() {
        return "Aero's Whimsical Washing Recipes";
    }
}
