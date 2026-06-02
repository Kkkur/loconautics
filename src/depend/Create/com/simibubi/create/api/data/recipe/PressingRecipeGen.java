/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 */
package com.simibubi.create.api.data.recipe;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.DatagenMod;
import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

public abstract class PressingRecipeGen
extends StandardProcessingRecipeGen<PressingRecipe> {
    protected BaseRecipeProvider.GeneratedRecipe moddedCompacting(DatagenMod mod, String input, String output) {
        return this.create("compat/" + mod.getId() + "/" + output, b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require(mod, input)).output(mod, output)).whenModLoaded(mod.getId()));
    }

    protected BaseRecipeProvider.GeneratedRecipe moddedPaths(DatagenMod mod, String ... blocks) {
        for (String block : blocks) {
            this.moddedCompacting(mod, block, block + "_path");
        }
        return null;
    }

    public PressingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.PRESSING;
    }
}
