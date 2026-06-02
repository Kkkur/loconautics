/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 */
package com.simibubi.create.api.data.recipe;

import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

public abstract class SequencedAssemblyRecipeGen
extends BaseRecipeProvider {
    public SequencedAssemblyRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    public String getName() {
        return this.modid + "'s sequenced assembly recipes";
    }

    protected BaseRecipeProvider.GeneratedRecipe create(String name, UnaryOperator<SequencedAssemblyRecipeBuilder> transform) {
        BaseRecipeProvider.GeneratedRecipe generatedRecipe = c -> ((SequencedAssemblyRecipeBuilder)transform.apply(new SequencedAssemblyRecipeBuilder(this.asResource(name)))).build(c);
        this.all.add(generatedRecipe);
        return generatedRecipe;
    }
}
