/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.crafting.RecipeSerializer
 */
package com.simibubi.create.content.processing.recipe;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public static class StandardProcessingRecipe.Serializer<R extends StandardProcessingRecipe<?>>
implements RecipeSerializer<R> {
    private final StandardProcessingRecipe.Factory<R> factory;
    private final MapCodec<R> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

    public StandardProcessingRecipe.Serializer(StandardProcessingRecipe.Factory<R> factory) {
        this.factory = factory;
        this.codec = ProcessingRecipe.codec(factory, ProcessingRecipeParams.CODEC);
        this.streamCodec = ProcessingRecipe.streamCodec(factory, ProcessingRecipeParams.STREAM_CODEC);
    }

    public MapCodec<R> codec() {
        return this.codec;
    }

    public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
        return this.streamCodec;
    }

    public StandardProcessingRecipe.Factory<R> factory() {
        return this.factory;
    }
}
