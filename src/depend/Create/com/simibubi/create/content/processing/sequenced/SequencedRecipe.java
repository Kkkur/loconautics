/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  io.netty.handler.codec.DecoderException
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.neoforged.neoforge.common.crafting.CompoundIngredient
 */
package com.simibubi.create.content.processing.sequenced;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;

public class SequencedRecipe<T extends ProcessingRecipe<?, ?>> {
    public static final Codec<SequencedRecipe<?>> CODEC = Recipe.CODEC.comapFlatMap(recipe -> {
        DataResult dataResult;
        if (recipe instanceof ProcessingRecipe) {
            ProcessingRecipe processing = (ProcessingRecipe)recipe;
            if (recipe instanceof IAssemblyRecipe) {
                dataResult = DataResult.success(new SequencedRecipe<ProcessingRecipe>(processing));
                return dataResult;
            }
        }
        dataResult = DataResult.error(() -> recipe.getClass().getSimpleName() + " is not supported in Sequenced Assembly");
        return dataResult;
    }, SequencedRecipe::getRecipe);
    public static final StreamCodec<RegistryFriendlyByteBuf, SequencedRecipe<?>> STREAM_CODEC = Recipe.STREAM_CODEC.map(recipe -> {
        if (recipe instanceof ProcessingRecipe) {
            ProcessingRecipe processing = (ProcessingRecipe)recipe;
            if (recipe instanceof IAssemblyRecipe) {
                return new SequencedRecipe<ProcessingRecipe>(processing);
            }
        }
        throw new DecoderException("Unexpected " + recipe.getClass().getSimpleName() + " not supported in Sequenced Assembly");
    }, SequencedRecipe::getRecipe);
    private final T wrapped;

    public SequencedRecipe(T wrapped) {
        this.wrapped = wrapped;
    }

    public IAssemblyRecipe getAsAssemblyRecipe() {
        return (IAssemblyRecipe)this.wrapped;
    }

    public T getRecipe() {
        return this.wrapped;
    }

    void initFromSequencedAssembly(SequencedAssemblyRecipe parent, boolean isFirst) {
        if (this.getAsAssemblyRecipe().supportsAssembly()) {
            Ingredient transit = Ingredient.of((ItemStack[])new ItemStack[]{parent.getTransitionalItem()});
            ((ProcessingRecipe)this.wrapped).getIngredients().set(0, (Object)(isFirst ? CompoundIngredient.of((Ingredient[])new Ingredient[]{transit, parent.getIngredient()}) : transit));
        }
    }
}
