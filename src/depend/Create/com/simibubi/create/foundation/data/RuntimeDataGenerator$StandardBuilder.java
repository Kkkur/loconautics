/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.createmod.catnip.codecs.CatnipCodecUtils
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.crafting.Recipe
 *  net.neoforged.neoforge.common.conditions.WithConditions
 */
package com.simibubi.create.foundation.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.simibubi.create.Create;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import java.util.Optional;
import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.WithConditions;

private static class RuntimeDataGenerator.StandardBuilder<T extends StandardProcessingRecipe<?>>
extends StandardProcessingRecipe.Builder<T> {
    public RuntimeDataGenerator.StandardBuilder(String modid, StandardProcessingRecipe.Factory<T> factory, String from, String to) {
        super(factory, Create.asResource("runtime_generated/compat/" + modid + "/" + from + "_to_" + to));
    }

    @Override
    public T build() {
        StandardProcessingRecipe recipe = (StandardProcessingRecipe)super.build();
        IRecipeTypeInfo recipeType = recipe.getTypeInfo();
        ResourceLocation typeId = recipeType.getId();
        if (!(recipeType.getSerializer() instanceof StandardProcessingRecipe.Serializer)) {
            throw new IllegalStateException("Cannot datagen ProcessingRecipe of type: " + String.valueOf(typeId));
        }
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath((String)this.recipeId.getNamespace(), (String)(typeId.getPath() + "/" + this.recipeId.getPath()));
        Optional serialized = CatnipCodecUtils.encode((Codec)Recipe.CONDITIONAL_CODEC, (DynamicOps)JsonOps.INSTANCE, Optional.of(new WithConditions((Object)recipe)));
        serialized.ifPresent(r -> JSON_FILES.put((Object)id.withPrefix("recipe/"), r));
        return (T)recipe;
    }
}
