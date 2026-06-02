/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.crafting.RecipeSerializer
 */
package com.simibubi.create.content.kinetics.deployer;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipeParams;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public static class ItemApplicationRecipe.Serializer<R extends ItemApplicationRecipe>
implements RecipeSerializer<R> {
    private final MapCodec<R> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

    public ItemApplicationRecipe.Serializer(ProcessingRecipe.Factory<ItemApplicationRecipeParams, R> factory) {
        this.codec = ProcessingRecipe.codec(factory, ItemApplicationRecipeParams.CODEC);
        this.streamCodec = ProcessingRecipe.streamCodec(factory, ItemApplicationRecipeParams.STREAM_CODEC);
    }

    public MapCodec<R> codec() {
        return this.codec;
    }

    public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
        return this.streamCodec;
    }
}
