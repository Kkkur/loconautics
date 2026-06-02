/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 *  net.neoforged.neoforge.common.Tags$Items
 */
package com.simibubi.create.foundation.data.recipe;

import com.google.common.base.Supplier;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeGen;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

public final class CreateMechanicalCraftingRecipeGen
extends MechanicalCraftingRecipeGen {
    BaseRecipeProvider.GeneratedRecipe CRUSHING_WHEEL = this.create((Supplier<ItemLike>)((Supplier)() -> AllBlocks.CRUSHING_WHEEL.get())).returns(2).recipe(b -> b.key(Character.valueOf('P'), Ingredient.of((TagKey)ItemTags.PLANKS)).key(Character.valueOf('S'), Ingredient.of(CreateRecipeProvider.I.stone())).key(Character.valueOf('A'), CreateRecipeProvider.I.andesiteAlloy()).patternLine(" AAA ").patternLine("AAPAA").patternLine("APSPA").patternLine("AAPAA").patternLine(" AAA ").disallowMirrored());
    BaseRecipeProvider.GeneratedRecipe WAND_OF_SYMMETRY = this.create((Supplier<ItemLike>)((Supplier)() -> AllItems.WAND_OF_SYMMETRY.get())).recipe(b -> b.key(Character.valueOf('E'), Ingredient.of((TagKey)Tags.Items.ENDER_PEARLS)).key(Character.valueOf('G'), Ingredient.of((TagKey)Tags.Items.GLASS_BLOCKS)).key(Character.valueOf('P'), CreateRecipeProvider.I.precisionMechanism()).key(Character.valueOf('O'), Ingredient.of((TagKey)Tags.Items.OBSIDIANS)).key(Character.valueOf('B'), Ingredient.of(CreateRecipeProvider.I.brass())).patternLine(" G ").patternLine("GEG").patternLine(" P ").patternLine(" B ").patternLine(" O "));
    BaseRecipeProvider.GeneratedRecipe EXTENDO_GRIP = this.create((Supplier<ItemLike>)((Supplier)() -> AllItems.EXTENDO_GRIP.get())).returns(1).recipe(b -> b.key(Character.valueOf('L'), Ingredient.of(CreateRecipeProvider.I.brass())).key(Character.valueOf('R'), CreateRecipeProvider.I.precisionMechanism()).key(Character.valueOf('H'), (ItemLike)AllItems.BRASS_HAND.get()).key(Character.valueOf('S'), Ingredient.of((TagKey)Tags.Items.RODS_WOODEN)).patternLine(" L ").patternLine(" R ").patternLine("SSS").patternLine("SSS").patternLine(" H ").disallowMirrored());
    BaseRecipeProvider.GeneratedRecipe POTATO_CANNON = this.create((Supplier<ItemLike>)((Supplier)() -> AllItems.POTATO_CANNON.get())).returns(1).recipe(b -> b.key(Character.valueOf('L'), CreateRecipeProvider.I.andesiteAlloy()).key(Character.valueOf('R'), CreateRecipeProvider.I.precisionMechanism()).key(Character.valueOf('S'), (ItemLike)AllBlocks.FLUID_PIPE.get()).key(Character.valueOf('C'), Ingredient.of(CreateRecipeProvider.I.copper())).patternLine("LRSSS").patternLine("CC   "));

    public CreateMechanicalCraftingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, "create");
    }
}
