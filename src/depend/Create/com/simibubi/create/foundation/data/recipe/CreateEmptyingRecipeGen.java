/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  net.neoforged.neoforge.common.NeoForgeMod
 */
package com.simibubi.create.foundation.data.recipe;

import com.simibubi.create.AllFluids;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.EmptyingRecipeGen;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.data.recipe.Mods;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.NeoForgeMod;

public final class CreateEmptyingRecipeGen
extends EmptyingRecipeGen {
    BaseRecipeProvider.GeneratedRecipe HONEY_BOTTLE = this.create("honey_bottle", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require((ItemLike)Items.HONEY_BOTTLE)).output((Fluid)AllFluids.HONEY.get(), 250)).output((ItemLike)Items.GLASS_BOTTLE));
    BaseRecipeProvider.GeneratedRecipe BUILDERS_TEA = this.create("builders_tea", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require((ItemLike)AllItems.BUILDERS_TEA.get())).output((Fluid)AllFluids.TEA.get(), 250)).output((ItemLike)Items.GLASS_BOTTLE));
    BaseRecipeProvider.GeneratedRecipe FD_MILK = this.create(Mods.FD.recipeId("milk_bottle"), b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require(Mods.FD, "milk_bottle")).output((Fluid)NeoForgeMod.MILK.get(), 250)).output((ItemLike)Items.GLASS_BOTTLE)).whenModLoaded(Mods.FD.getId()));
    BaseRecipeProvider.GeneratedRecipe AM_LAVA = this.create(Mods.AM.recipeId("lava_bottle"), b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require(Mods.AM, "lava_bottle")).output((ItemLike)Items.GLASS_BOTTLE)).output((Fluid)Fluids.LAVA, 250)).whenModLoaded(Mods.AM.getId()));
    BaseRecipeProvider.GeneratedRecipe NEO_MILK = this.create(Mods.NEA.recipeId("milk_bottle"), b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require(Mods.FD, "milk_bottle")).output((Fluid)NeoForgeMod.MILK.get(), 250)).output((ItemLike)Items.GLASS_BOTTLE)).whenModLoaded(Mods.NEA.getId()));

    public CreateEmptyingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, "create");
    }
}
