/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.util.entry.ItemEntry
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 */
package com.simibubi.create.api.data.recipe;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.DatagenMod;
import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.tterrag.registrate.util.entry.ItemEntry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public abstract class WashingRecipeGen
extends StandardProcessingRecipeGen<SplashingRecipe> {
    public BaseRecipeProvider.GeneratedRecipe convert(Block block, Block result) {
        return this.create(() -> block, b -> (StandardProcessingRecipe.Builder)b.output((ItemLike)result));
    }

    public BaseRecipeProvider.GeneratedRecipe crushedOre(ItemEntry<Item> crushed, Supplier<ItemLike> nugget, Supplier<ItemLike> secondary, float secondaryChance) {
        return this.create(() -> crushed.get(), b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.output((ItemLike)nugget.get(), 9)).output(secondaryChance, (ItemLike)secondary.get(), 1));
    }

    protected BaseRecipeProvider.GeneratedRecipe simpleModded(DatagenMod mod, String input, String output) {
        return this.create(mod.getId() + "/" + output, b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require(mod, input)).output(mod, output)).whenModLoaded(mod.getId()));
    }

    public WashingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.SPLASHING;
    }
}
