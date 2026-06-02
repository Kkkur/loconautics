/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.crafting.RecipeSerializer
 */
package com.simibubi.create;

import com.simibubi.create.AllTags;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;

public static enum AllTags.AllRecipeSerializerTags {
    AUTOMATION_IGNORE;

    public final TagKey<RecipeSerializer<?>> tag;

    private AllTags.AllRecipeSerializerTags() {
        this(AllTags.NameSpace.MOD);
    }

    private AllTags.AllRecipeSerializerTags(AllTags.NameSpace namespace) {
        this(namespace, null);
    }

    private AllTags.AllRecipeSerializerTags(AllTags.NameSpace namespace, String pathOverride) {
        this.tag = TagKey.create((ResourceKey)Registries.RECIPE_SERIALIZER, (ResourceLocation)namespace.id(this, pathOverride));
    }

    public boolean matches(RecipeSerializer<?> recipeSerializer) {
        ResourceKey key = (ResourceKey)BuiltInRegistries.RECIPE_SERIALIZER.getResourceKey(recipeSerializer).orElseThrow();
        return ((Holder.Reference)BuiltInRegistries.RECIPE_SERIALIZER.getHolder(key).orElseThrow()).is(this.tag);
    }
}
