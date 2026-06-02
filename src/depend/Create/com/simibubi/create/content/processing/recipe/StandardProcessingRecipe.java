/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeSerializer
 */
package com.simibubi.create.content.processing.recipe;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class StandardProcessingRecipe<T extends RecipeInput>
extends ProcessingRecipe<T, ProcessingRecipeParams> {
    public StandardProcessingRecipe(IRecipeTypeInfo typeInfo, ProcessingRecipeParams params) {
        super(typeInfo, params);
    }

    public static class Serializer<R extends StandardProcessingRecipe<?>>
    implements RecipeSerializer<R> {
        private final Factory<R> factory;
        private final MapCodec<R> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

        public Serializer(Factory<R> factory) {
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

        public Factory<R> factory() {
            return this.factory;
        }
    }

    public static class Builder<R extends StandardProcessingRecipe<?>>
    extends ProcessingRecipeBuilder<ProcessingRecipeParams, R, Builder<R>> {
        public Builder(Factory<R> factory, ResourceLocation recipeId) {
            super(factory, recipeId);
        }

        @Override
        protected ProcessingRecipeParams createParams() {
            return new ProcessingRecipeParams();
        }

        @Override
        public Builder<R> self() {
            return this;
        }
    }

    @FunctionalInterface
    public static interface Factory<R extends StandardProcessingRecipe<?>>
    extends ProcessingRecipe.Factory<ProcessingRecipeParams, R> {
        @Override
        public R create(ProcessingRecipeParams var1);
    }
}
