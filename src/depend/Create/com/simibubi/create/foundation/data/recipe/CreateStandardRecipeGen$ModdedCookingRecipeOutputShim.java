/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.foundation.data.recipe;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.mixin.accessor.MappedRegistryAccessor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
private static class CreateStandardRecipeGen.ModdedCookingRecipeOutputShim
implements Recipe<RecipeInput> {
    private static final Map<RecipeType<?>, Serializer> serializers = new ConcurrentHashMap();
    private final Recipe<?> wrapped;
    private final ResourceLocation overrideID;

    private CreateStandardRecipeGen.ModdedCookingRecipeOutputShim(Recipe<?> wrapped, ResourceLocation overrideID) {
        this.wrapped = wrapped;
        this.overrideID = overrideID;
    }

    public boolean matches(RecipeInput recipeInput, Level level) {
        throw new AssertionError((Object)"Only for datagen output");
    }

    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
        throw new AssertionError((Object)"Only for datagen output");
    }

    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        throw new AssertionError((Object)"Only for datagen output");
    }

    public ItemStack getResultItem(HolderLookup.Provider registries) {
        throw new AssertionError((Object)"Only for datagen output");
    }

    public RecipeSerializer<?> getSerializer() {
        return serializers.computeIfAbsent(this.getType(), t -> Serializer.create(this.wrapped));
    }

    public RecipeType<?> getType() {
        return this.wrapped.getType();
    }

    private record Serializer(MapCodec<Recipe<?>> wrappedCodec) implements RecipeSerializer<CreateStandardRecipeGen.ModdedCookingRecipeOutputShim>
    {
        private static Serializer create(Recipe<?> wrapped) {
            MappedRegistryAccessor mra;
            RecipeSerializer wrappedSerializer = wrapped.getSerializer();
            Serializer serializer = new Serializer(wrappedSerializer.codec());
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
            return RecordCodecBuilder.mapCodec(instance -> instance.group((App)this.wrappedCodec.forGetter(i -> i.wrapped), (App)FakeItemStack.CODEC.fieldOf("result").forGetter(i -> new FakeItemStack(i.overrideID))).apply((Applicative)instance, (wrappedRecipe, fakeItemStack) -> {
                throw new AssertionError((Object)"Only for datagen output");
            }));
        }

        public StreamCodec<RegistryFriendlyByteBuf, CreateStandardRecipeGen.ModdedCookingRecipeOutputShim> streamCodec() {
            throw new AssertionError((Object)"Only for datagen output");
        }
    }

    private record FakeItemStack(ResourceLocation id) {
        public static Codec<FakeItemStack> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ResourceLocation.CODEC.fieldOf("id").forGetter(FakeItemStack::id)).apply((Applicative)instance, FakeItemStack::new));
    }
}
