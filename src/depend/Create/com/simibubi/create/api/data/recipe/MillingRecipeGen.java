/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.util.entry.ItemEntry
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.ItemLike
 *  net.neoforged.neoforge.common.conditions.ICondition
 *  net.neoforged.neoforge.common.conditions.NotCondition
 *  net.neoforged.neoforge.common.conditions.TagEmptyCondition
 *  org.jetbrains.annotations.ApiStatus$ScheduledForRemoval
 */
package com.simibubi.create.api.data.recipe;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.DatagenMod;
import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.tterrag.registrate.util.entry.ItemEntry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;
import org.jetbrains.annotations.ApiStatus;

public abstract class MillingRecipeGen
extends StandardProcessingRecipeGen<MillingRecipe> {
    @Deprecated(since="6.0.7", forRemoval=true)
    @ApiStatus.ScheduledForRemoval(inVersion="1.21.1+ Port")
    protected BaseRecipeProvider.GeneratedRecipe metalOre(String name, ItemEntry<? extends Item> crushed, int duration) {
        return this.create(name + "_ore", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.duration(duration)).withCondition((ICondition)new NotCondition((ICondition)new TagEmptyCondition("c", "ores/" + name)))).require(AllTags.commonItemTag("ores/" + name))).output((ItemLike)crushed.get()));
    }

    protected BaseRecipeProvider.GeneratedRecipe moddedSandstone(DatagenMod mod, String name) {
        String sandstone = name + "_sandstone";
        return this.create(mod.recipeId(sandstone), b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.duration(150)).require(mod, sandstone)).output(mod, name + "_sand")).whenModLoaded(mod.getId()));
    }

    public MillingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.MILLING;
    }
}
