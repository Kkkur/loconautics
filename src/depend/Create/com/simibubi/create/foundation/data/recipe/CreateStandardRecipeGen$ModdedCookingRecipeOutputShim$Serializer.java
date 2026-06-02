/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeSerializer
 */
package com.simibubi.create.foundation.data.recipe;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.data.recipe.CreateStandardRecipeGen;
import com.simibubi.create.foundation.mixin.accessor.MappedRegistryAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

private record CreateStandardRecipeGen.ModdedCookingRecipeOutputShim.Serializer(MapCodec<Recipe<?>> wrappedCodec) implements RecipeSerializer<CreateStandardRecipeGen.ModdedCookingRecipeOutputShim>
{
    private static CreateStandardRecipeGen.ModdedCookingRecipeOutputShim.Serializer create(Recipe<?> wrapped) {
        MappedRegistryAccessor mra;
        RecipeSerializer wrappedSerializer = wrapped.getSerializer();
        CreateStandardRecipeGen.ModdedCookingRecipeOutputShim.Serializer serializer = new CreateStandardRecipeGen.ModdedCookingRecipeOutputShim.Serializer(wrappedSerializer.codec());
        Registry registry = BuiltInRegistries.RECIPE_SERIALIZER;
        if (!(registry instanceof MappedRegistryAccessor)) {
            throw new AssertionError((Object)("ModdedCookingRecipeOutputShim will not be able to serialize without injecting into a registry. Expected BuiltInRegistries.RECIPE_SERIALIZER to be of class MappedRegistry, is of class " + String.valueOf(BuiltInRegistries.RECIPE_SERIALIZER.getClass())));
        }
        MappedRegistryAccessor mra$ = mra = (MappedRegistryAccessor)registry;
        int wrappedId = mra$.getToId().getOrDefault((Object)wrappedSerializer, -1);
        ResourceKey wrappedKey = mra$.getByValue().get(wrappedSerializer).key();
        mra$.getToId().put((Object)serializer, wrappedId);
        mra$.getByValue().put(serializer, Holder.Reference.createStandAlone(null, (ResourceKey)wrappedKey));
        return serializer;
    }

    public MapCodec<CreateStandardRecipeGen.ModdedCookingRecipeOutputShim> codec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group((App)this.wrappedCodec.forGetter(i -> i.wrapped), (App)CreateStandardRecipeGen.ModdedCookingRecipeOutputShim.FakeItemStack.CODEC.fieldOf("result").forGetter(i -> new CreateStandardRecipeGen.ModdedCookingRecipeOutputShim.FakeItemStack(i.overrideID))).apply((Applicative)instance, (wrappedRecipe, fakeItemStack) -> {
            throw new AssertionError((Object)"Only for datagen output");
        }));
    }

    public StreamCodec<RegistryFriendlyByteBuf, CreateStandardRecipeGen.ModdedCookingRecipeOutputShim> streamCodec() {
        throw new AssertionError((Object)"Only for datagen output");
    }
}
