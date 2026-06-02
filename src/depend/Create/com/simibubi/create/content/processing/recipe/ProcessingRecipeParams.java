/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.minecraft.core.NonNullList
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
 */
package com.simibubi.create.content.processing.recipe;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.codec.CreateCodecs;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class ProcessingRecipeParams {
    public static MapCodec<ProcessingRecipeParams> CODEC = ProcessingRecipeParams.codec(ProcessingRecipeParams::new);
    public static StreamCodec<RegistryFriendlyByteBuf, ProcessingRecipeParams> STREAM_CODEC = ProcessingRecipeParams.streamCodec(ProcessingRecipeParams::new);
    protected NonNullList<Ingredient> ingredients = NonNullList.create();
    protected NonNullList<ProcessingOutput> results = NonNullList.create();
    protected NonNullList<SizedFluidIngredient> fluidIngredients = NonNullList.create();
    protected NonNullList<FluidStack> fluidResults = NonNullList.create();
    protected int processingDuration = 0;
    protected HeatCondition requiredHeat = HeatCondition.NONE;

    protected ProcessingRecipeParams() {
    }

    protected static <P extends ProcessingRecipeParams> MapCodec<P> codec(Supplier<P> factory) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.either(CreateCodecs.SIZED_FLUID_INGREDIENT, (Codec)Ingredient.CODEC).listOf().fieldOf("ingredients").forGetter(ProcessingRecipeParams::ingredients), (App)Codec.either((Codec)FluidStack.CODEC, ProcessingOutput.CODEC).listOf().fieldOf("results").forGetter(ProcessingRecipeParams::results), (App)Codec.INT.optionalFieldOf("processing_time", (Object)0).forGetter(ProcessingRecipeParams::processingDuration), (App)HeatCondition.CODEC.optionalFieldOf("heat_requirement", (Object)HeatCondition.NONE).forGetter(ProcessingRecipeParams::requiredHeat)).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3) -> ProcessingRecipeParams.lambda$codec$2((Supplier)factory, arg_0, arg_1, arg_2, arg_3)));
    }

    protected static <P extends ProcessingRecipeParams> StreamCodec<RegistryFriendlyByteBuf, P> streamCodec(Supplier<P> factory) {
        return StreamCodec.of((buffer, params) -> params.encode((RegistryFriendlyByteBuf)buffer), buffer -> {
            ProcessingRecipeParams params = (ProcessingRecipeParams)factory.get();
            params.decode((RegistryFriendlyByteBuf)buffer);
            return params;
        });
    }

    protected final List<Either<SizedFluidIngredient, Ingredient>> ingredients() {
        ArrayList<Either<SizedFluidIngredient, Ingredient>> ingredients = new ArrayList<Either<SizedFluidIngredient, Ingredient>>(this.ingredients.size() + this.fluidIngredients.size());
        this.ingredients.forEach(ingredient -> ingredients.add(Either.right((Object)ingredient)));
        this.fluidIngredients.forEach(ingredient -> ingredients.add(Either.left((Object)ingredient)));
        return ingredients;
    }

    protected final List<Either<FluidStack, ProcessingOutput>> results() {
        ArrayList<Either<FluidStack, ProcessingOutput>> results = new ArrayList<Either<FluidStack, ProcessingOutput>>(this.results.size() + this.fluidResults.size());
        this.results.forEach(result -> results.add(Either.right((Object)result)));
        this.fluidResults.forEach(result -> results.add(Either.left((Object)result)));
        return results;
    }

    protected final int processingDuration() {
        return this.processingDuration;
    }

    protected final HeatCondition requiredHeat() {
        return this.requiredHeat;
    }

    protected void encode(RegistryFriendlyByteBuf buffer) {
        CatnipStreamCodecBuilders.nonNullList((StreamCodec)Ingredient.CONTENTS_STREAM_CODEC).encode((Object)buffer, this.ingredients);
        CatnipStreamCodecBuilders.nonNullList((StreamCodec)SizedFluidIngredient.STREAM_CODEC).encode((Object)buffer, this.fluidIngredients);
        CatnipStreamCodecBuilders.nonNullList(ProcessingOutput.STREAM_CODEC).encode((Object)buffer, this.results);
        CatnipStreamCodecBuilders.nonNullList((StreamCodec)FluidStack.STREAM_CODEC).encode((Object)buffer, this.fluidResults);
        ByteBufCodecs.VAR_INT.encode((Object)buffer, (Object)this.processingDuration);
        HeatCondition.STREAM_CODEC.encode((Object)buffer, (Object)this.requiredHeat);
    }

    protected void decode(RegistryFriendlyByteBuf buffer) {
        this.ingredients = (NonNullList)CatnipStreamCodecBuilders.nonNullList((StreamCodec)Ingredient.CONTENTS_STREAM_CODEC).decode((Object)buffer);
        this.fluidIngredients = (NonNullList)CatnipStreamCodecBuilders.nonNullList((StreamCodec)SizedFluidIngredient.STREAM_CODEC).decode((Object)buffer);
        this.results = (NonNullList)CatnipStreamCodecBuilders.nonNullList(ProcessingOutput.STREAM_CODEC).decode((Object)buffer);
        this.fluidResults = (NonNullList)CatnipStreamCodecBuilders.nonNullList((StreamCodec)FluidStack.STREAM_CODEC).decode((Object)buffer);
        this.processingDuration = (Integer)ByteBufCodecs.VAR_INT.decode((Object)buffer);
        this.requiredHeat = (HeatCondition)((Object)HeatCondition.STREAM_CODEC.decode((Object)buffer));
    }

    private static /* synthetic */ ProcessingRecipeParams lambda$codec$2(Supplier factory, List ingredients, List results, Integer processingDuration, HeatCondition requiredHeat) {
        ProcessingRecipeParams params = (ProcessingRecipeParams)factory.get();
        ingredients.forEach(either -> either.ifRight(arg_0 -> params.ingredients.add(arg_0)).ifLeft(arg_0 -> params.fluidIngredients.add(arg_0)));
        results.forEach(either -> either.ifRight(arg_0 -> params.results.add(arg_0)).ifLeft(arg_0 -> params.fluidResults.add(arg_0)));
        params.processingDuration = processingDuration;
        params.requiredHeat = requiredHeat;
        return params;
    }
}
