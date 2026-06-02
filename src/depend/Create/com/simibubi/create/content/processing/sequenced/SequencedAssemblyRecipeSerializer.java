/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.util.ExtraCodecs
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.processing.sequenced;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import java.util.Collection;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public class SequencedAssemblyRecipeSerializer
implements RecipeSerializer<SequencedAssemblyRecipe> {
    private final MapCodec<SequencedAssemblyRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group((App)Ingredient.CODEC.fieldOf("ingredient").forGetter(SequencedAssemblyRecipe::getIngredient), (App)ProcessingOutput.CODEC.fieldOf("transitional_item").forGetter(r -> r.transitionalItem), (App)SequencedRecipe.CODEC.listOf().fieldOf("sequence").forGetter(SequencedAssemblyRecipe::getSequence), (App)ProcessingOutput.CODEC.listOf().fieldOf("results").forGetter(r -> r.resultPool), (App)ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("loops", (Object)1).forGetter(SequencedAssemblyRecipe::getLoops)).apply((Applicative)i, (ingredient, transitionalItem, sequence, results, loops) -> {
        SequencedAssemblyRecipe recipe = new SequencedAssemblyRecipe(this);
        recipe.ingredient = ingredient;
        recipe.transitionalItem = transitionalItem;
        recipe.sequence.addAll((Collection<SequencedRecipe<?>>)sequence);
        recipe.resultPool.addAll((Collection<ProcessingOutput>)results);
        recipe.loops = loops;
        for (int j = 0; j < recipe.sequence.size(); ++j) {
            ((SequencedRecipe)sequence.get(j)).initFromSequencedAssembly(recipe, j == 0);
        }
        return recipe;
    }));
    public final StreamCodec<RegistryFriendlyByteBuf, SequencedAssemblyRecipe> STREAM_CODEC = StreamCodec.composite((StreamCodec)Ingredient.CONTENTS_STREAM_CODEC, r -> r.ingredient, (StreamCodec)CatnipStreamCodecBuilders.list(SequencedRecipe.STREAM_CODEC), SequencedAssemblyRecipe::getSequence, (StreamCodec)CatnipStreamCodecBuilders.list(ProcessingOutput.STREAM_CODEC), r -> r.resultPool, ProcessingOutput.STREAM_CODEC, r -> r.transitionalItem, (StreamCodec)ByteBufCodecs.VAR_INT, r -> r.loops, (ingredient, transitionalItem, sequence, results, loops) -> {
        SequencedAssemblyRecipe recipe = new SequencedAssemblyRecipe(this);
        recipe.ingredient = ingredient;
        recipe.getSequence().addAll((Collection<SequencedRecipe<?>>)transitionalItem);
        recipe.resultPool.addAll((Collection<ProcessingOutput>)sequence);
        recipe.transitionalItem = results;
        recipe.loops = loops;
        return recipe;
    });

    @NotNull
    public MapCodec<SequencedAssemblyRecipe> codec() {
        return this.CODEC;
    }

    @NotNull
    public StreamCodec<RegistryFriendlyByteBuf, SequencedAssemblyRecipe> streamCodec() {
        return this.STREAM_CODEC;
    }
}
