/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  net.neoforged.neoforge.common.Tags$Fluids
 *  net.neoforged.neoforge.common.Tags$Items
 *  net.neoforged.neoforge.common.crafting.BlockTagIngredient
 *  net.neoforged.neoforge.common.crafting.ICustomIngredient
 */
package com.simibubi.create.foundation.data.recipe;

import com.simibubi.create.AllFluids;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.MixingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import com.simibubi.create.foundation.data.recipe.Mods;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.BlockTagIngredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;

public final class CreateMixingRecipeGen
extends MixingRecipeGen {
    BaseRecipeProvider.GeneratedRecipe TEMP_LAVA = this.create("lava_from_cobble", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require((TagKey<Item>)Tags.Items.COBBLESTONES)).output((Fluid)Fluids.LAVA, 50)).requiresHeat(HeatCondition.SUPERHEATED));
    BaseRecipeProvider.GeneratedRecipe TEA = this.create("tea", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require(Fluids.WATER, 250)).require((TagKey<Fluid>)Tags.Fluids.MILK, 250)).require((TagKey<Item>)ItemTags.LEAVES)).output((Fluid)AllFluids.TEA.get(), 500)).requiresHeat(HeatCondition.HEATED));
    BaseRecipeProvider.GeneratedRecipe CHOCOLATE = this.create("chocolate", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require((TagKey<Fluid>)Tags.Fluids.MILK, 250)).require((ItemLike)Items.SUGAR)).require((ItemLike)Items.COCOA_BEANS)).output((Fluid)AllFluids.CHOCOLATE.get(), 250)).requiresHeat(HeatCondition.HEATED));
    BaseRecipeProvider.GeneratedRecipe CHOCOLATE_MELTING = this.create("chocolate_melting", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require((ItemLike)AllItems.BAR_OF_CHOCOLATE.get())).output((Fluid)AllFluids.CHOCOLATE.get(), 250)).requiresHeat(HeatCondition.HEATED));
    BaseRecipeProvider.GeneratedRecipe HONEY = this.create("honey", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require((ItemLike)Items.HONEY_BLOCK)).output((Fluid)AllFluids.HONEY.get(), 1000)).requiresHeat(HeatCondition.HEATED));
    BaseRecipeProvider.GeneratedRecipe DOUGH = this.create("dough_by_mixing", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require(CreateRecipeProvider.I.wheatFlour())).require(Fluids.WATER, 1000)).output((ItemLike)AllItems.DOUGH.get(), 1));
    BaseRecipeProvider.GeneratedRecipe BRASS_INGOT = this.create("brass_ingot", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require(CreateRecipeProvider.I.copper())).require(CreateRecipeProvider.I.zinc())).output((ItemLike)AllItems.BRASS_INGOT.get(), 2)).requiresHeat(HeatCondition.HEATED));
    BaseRecipeProvider.GeneratedRecipe ANDESITE_ALLOY = this.create("andesite_alloy", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require((ItemLike)Blocks.ANDESITE)).require(CreateRecipeProvider.I.ironNugget())).output(CreateRecipeProvider.I.andesiteAlloy(), 1));
    BaseRecipeProvider.GeneratedRecipe ANDESITE_ALLOY_FROM_ZINC = this.create("andesite_alloy_from_zinc", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require((ItemLike)Blocks.ANDESITE)).require(CreateRecipeProvider.I.zincNugget())).output(CreateRecipeProvider.I.andesiteAlloy(), 1));
    BaseRecipeProvider.GeneratedRecipe MUD = this.create("mud_by_mixing", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require((ICustomIngredient)new BlockTagIngredient(BlockTags.CONVERTABLE_TO_MUD))).require(Fluids.WATER, 250)).output((ItemLike)Blocks.MUD, 1));
    BaseRecipeProvider.GeneratedRecipe PULP = this.create("cardboard_pulp", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require(AllTags.AllItemTags.PULPIFIABLE.tag)).require(AllTags.AllItemTags.PULPIFIABLE.tag)).require(AllTags.AllItemTags.PULPIFIABLE.tag)).require(AllTags.AllItemTags.PULPIFIABLE.tag)).require(Fluids.WATER, 250)).output((ItemLike)AllItems.PULP, 1));
    BaseRecipeProvider.GeneratedRecipe AE2_FLUIX = this.create(Mods.AE2.recipeId("fluix_crystal"), b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require((TagKey<Item>)Tags.Items.DUSTS_REDSTONE)).require(Fluids.WATER, 250)).require(Mods.AE2, "charged_certus_quartz_crystal")).require((TagKey<Item>)Tags.Items.GEMS_QUARTZ)).output(1.0f, Mods.AE2, "fluix_crystal", 2)).whenModLoaded(Mods.AE2.getId()));
    BaseRecipeProvider.GeneratedRecipe RU_PEAT_MUD = this.moddedMud(Mods.RU, "peat");
    BaseRecipeProvider.GeneratedRecipe RU_SILT_MUD = this.moddedMud(Mods.RU, "silt");

    public CreateMixingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, "create");
    }
}
