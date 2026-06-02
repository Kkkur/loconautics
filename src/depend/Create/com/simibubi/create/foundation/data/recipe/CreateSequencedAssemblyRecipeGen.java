/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Ingredient$TagValue
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.material.Fluids
 */
package com.simibubi.create.foundation.data.recipe;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.SequencedAssemblyRecipeGen;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;

public final class CreateSequencedAssemblyRecipeGen
extends SequencedAssemblyRecipeGen {
    BaseRecipeProvider.GeneratedRecipe PRECISION_MECHANISM = this.create("precision_mechanism", b -> b.require(CreateRecipeProvider.I.goldSheet()).transitionTo((ItemLike)AllItems.INCOMPLETE_PRECISION_MECHANISM.get()).addOutput((ItemLike)AllItems.PRECISION_MECHANISM.get(), 120.0f).addOutput((ItemLike)AllItems.GOLDEN_SHEET.get(), 8.0f).addOutput((ItemLike)AllItems.ANDESITE_ALLOY.get(), 8.0f).addOutput((ItemLike)AllBlocks.COGWHEEL.get(), 5.0f).addOutput((ItemLike)Items.GOLD_NUGGET, 3.0f).addOutput((ItemLike)AllBlocks.SHAFT.get(), 2.0f).addOutput((ItemLike)AllItems.CRUSHED_GOLD.get(), 2.0f).addOutput((ItemLike)Items.IRON_INGOT, 1.0f).addOutput((ItemLike)Items.CLOCK, 1.0f).loops(5).addStep(DeployerApplicationRecipe::new, rb -> (ItemApplicationRecipe.Builder)rb.require(CreateRecipeProvider.I.cog())).addStep(DeployerApplicationRecipe::new, rb -> (ItemApplicationRecipe.Builder)rb.require(CreateRecipeProvider.I.largeCog())).addStep(DeployerApplicationRecipe::new, rb -> (ItemApplicationRecipe.Builder)rb.require(CreateRecipeProvider.I.ironNugget())));
    BaseRecipeProvider.GeneratedRecipe REINFORCED_SHEET = this.create("sturdy_sheet", b -> b.require(AllTags.AllItemTags.OBSIDIAN_DUST.tag).transitionTo((ItemLike)AllItems.INCOMPLETE_REINFORCED_SHEET.get()).addOutput((ItemLike)AllItems.STURDY_SHEET.get(), 1.0f).loops(1).addStep(FillingRecipe::new, rb -> (StandardProcessingRecipe.Builder)rb.require(Fluids.LAVA, 500)).addStep(PressingRecipe::new, rb -> rb).addStep(PressingRecipe::new, rb -> rb));
    BaseRecipeProvider.GeneratedRecipe TRACK = this.create("track", b -> b.require(AllTags.AllItemTags.SLEEPERS.tag).transitionTo((ItemLike)AllItems.INCOMPLETE_TRACK.get()).addOutput((ItemLike)AllBlocks.TRACK.get(), 1.0f).loops(1).addStep(DeployerApplicationRecipe::new, rb -> (ItemApplicationRecipe.Builder)rb.require(Ingredient.fromValues(Stream.of(new Ingredient.TagValue(CreateRecipeProvider.I.ironNugget()), new Ingredient.TagValue(CreateRecipeProvider.I.zincNugget()))))).addStep(DeployerApplicationRecipe::new, rb -> (ItemApplicationRecipe.Builder)rb.require(Ingredient.fromValues(Stream.of(new Ingredient.TagValue(CreateRecipeProvider.I.ironNugget()), new Ingredient.TagValue(CreateRecipeProvider.I.zincNugget()))))).addStep(PressingRecipe::new, rb -> rb));

    public CreateSequencedAssemblyRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, "create");
    }
}
