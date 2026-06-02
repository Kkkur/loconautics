/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.item.crafting.ShapedRecipe$Serializer
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.kinetics.crafter;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

public static class MechanicalCraftingRecipe.Serializer
implements RecipeSerializer<MechanicalCraftingRecipe> {
    public static final MapCodec<MechanicalCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)RecipeSerializer.SHAPED_RECIPE.codec().forGetter(t -> t), (App)Codec.BOOL.fieldOf("accept_mirrored").forGetter(MechanicalCraftingRecipe::acceptsMirrored)).apply((Applicative)instance, MechanicalCraftingRecipe::fromShaped));
    public static final StreamCodec<RegistryFriendlyByteBuf, MechanicalCraftingRecipe> STREAM_CODEC = StreamCodec.composite((StreamCodec)ShapedRecipe.Serializer.STREAM_CODEC, i -> i, (StreamCodec)ByteBufCodecs.BOOL, i -> i.acceptMirrored, MechanicalCraftingRecipe::fromShaped);

    @NotNull
    public MapCodec<MechanicalCraftingRecipe> codec() {
        return CODEC;
    }

    @NotNull
    public StreamCodec<RegistryFriendlyByteBuf, MechanicalCraftingRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
