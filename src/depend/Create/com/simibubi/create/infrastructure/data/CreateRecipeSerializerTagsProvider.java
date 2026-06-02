/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.tags.TagsProvider
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.neoforged.neoforge.common.data.ExistingFileHelper
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.infrastructure.data;

import com.simibubi.create.AllTags;
import com.simibubi.create.compat.Mods;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class CreateRecipeSerializerTagsProvider
extends TagsProvider<RecipeSerializer<?>> {
    public CreateRecipeSerializerTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.RECIPE_SERIALIZER, lookupProvider, "create", existingFileHelper);
    }

    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(AllTags.AllRecipeSerializerTags.AUTOMATION_IGNORE.tag).addOptional(Mods.OCCULTISM.rl("spirit_trade")).addOptional(Mods.OCCULTISM.rl("ritual"));
    }

    public String getName() {
        return "Create's Recipe Serializer Tags";
    }
}
