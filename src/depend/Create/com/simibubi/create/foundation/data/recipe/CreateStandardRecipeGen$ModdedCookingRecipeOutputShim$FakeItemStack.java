/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create.foundation.data.recipe;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

private record CreateStandardRecipeGen.ModdedCookingRecipeOutputShim.FakeItemStack(ResourceLocation id) {
    public static Codec<CreateStandardRecipeGen.ModdedCookingRecipeOutputShim.FakeItemStack> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ResourceLocation.CODEC.fieldOf("id").forGetter(CreateStandardRecipeGen.ModdedCookingRecipeOutputShim.FakeItemStack::id)).apply((Applicative)instance, CreateStandardRecipeGen.ModdedCookingRecipeOutputShim.FakeItemStack::new));
}
