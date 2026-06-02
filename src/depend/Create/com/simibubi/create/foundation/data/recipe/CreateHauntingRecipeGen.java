/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 *  net.neoforged.neoforge.common.Tags$Items
 */
package com.simibubi.create.foundation.data.recipe;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.HauntingRecipeGen;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.data.recipe.Mods;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

public final class CreateHauntingRecipeGen
extends HauntingRecipeGen {
    BaseRecipeProvider.GeneratedRecipe BRASS_BELL = this.convert(() -> Ingredient.of((ItemLike[])new ItemLike[]{(ItemLike)AllBlocks.PECULIAR_BELL.get()}), () -> AllBlocks.HAUNTED_BELL.get());
    BaseRecipeProvider.GeneratedRecipe HAUNT_STONE = this.convert((ItemLike)Items.STONE, (ItemLike)Items.INFESTED_STONE);
    BaseRecipeProvider.GeneratedRecipe HAUNT_DEEPSLATE = this.convert((ItemLike)Items.DEEPSLATE, (ItemLike)Items.INFESTED_DEEPSLATE);
    BaseRecipeProvider.GeneratedRecipe HAUNT_STONE_BRICKS = this.convert((ItemLike)Items.STONE_BRICKS, (ItemLike)Items.INFESTED_STONE_BRICKS);
    BaseRecipeProvider.GeneratedRecipe HAUNT_MOSSY_STONE_BRICKS = this.convert((ItemLike)Items.MOSSY_STONE_BRICKS, (ItemLike)Items.INFESTED_MOSSY_STONE_BRICKS);
    BaseRecipeProvider.GeneratedRecipe HAUNT_CRACKED_STONE_BRICKS = this.convert((ItemLike)Items.CRACKED_STONE_BRICKS, (ItemLike)Items.INFESTED_CRACKED_STONE_BRICKS);
    BaseRecipeProvider.GeneratedRecipe HAUNT_CHISELED_STONE_BRICKS = this.convert((ItemLike)Items.CHISELED_STONE_BRICKS, (ItemLike)Items.INFESTED_CHISELED_STONE_BRICKS);
    BaseRecipeProvider.GeneratedRecipe SOUL_TORCH = this.convert((ItemLike)Items.TORCH, (ItemLike)Items.SOUL_TORCH);
    BaseRecipeProvider.GeneratedRecipe SOUL_CAMPFIRE = this.convert((ItemLike)Items.CAMPFIRE, (ItemLike)Items.SOUL_CAMPFIRE);
    BaseRecipeProvider.GeneratedRecipe SOUL_LANTERN = this.convert((ItemLike)Items.LANTERN, (ItemLike)Items.SOUL_LANTERN);
    BaseRecipeProvider.GeneratedRecipe POISON_POTATO = this.convert((ItemLike)Items.POTATO, (ItemLike)Items.POISONOUS_POTATO);
    BaseRecipeProvider.GeneratedRecipe GLOW_INK = this.convert((ItemLike)Items.INK_SAC, (ItemLike)Items.GLOW_INK_SAC);
    BaseRecipeProvider.GeneratedRecipe GLOW_BERRIES = this.convert((ItemLike)Items.SWEET_BERRIES, (ItemLike)Items.GLOW_BERRIES);
    BaseRecipeProvider.GeneratedRecipe NETHER_BRICK = this.convert((ItemLike)Items.BRICK, (ItemLike)Items.NETHER_BRICK);
    BaseRecipeProvider.GeneratedRecipe PRISMARINE = this.create(Create.asResource("lapis_recycling"), b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require((TagKey<Item>)Tags.Items.GEMS_LAPIS)).output(0.75f, (ItemLike)Items.PRISMARINE_SHARD)).output(0.125f, (ItemLike)Items.PRISMARINE_CRYSTALS));
    BaseRecipeProvider.GeneratedRecipe SOUL_SAND = this.convert(() -> Ingredient.of((TagKey)ItemTags.SAND), () -> Blocks.SOUL_SAND);
    BaseRecipeProvider.GeneratedRecipe SOUL_DIRT = this.convert(() -> Ingredient.of((TagKey)ItemTags.DIRT), () -> Blocks.SOUL_SOIL);
    BaseRecipeProvider.GeneratedRecipe BLACK_STONE = this.convert(() -> Ingredient.of((TagKey)Tags.Items.COBBLESTONES), () -> Blocks.BLACKSTONE);
    BaseRecipeProvider.GeneratedRecipe CRIMSON_FUNGUS = this.convert((ItemLike)Items.RED_MUSHROOM, (ItemLike)Items.CRIMSON_FUNGUS);
    BaseRecipeProvider.GeneratedRecipe WARPED_FUNGUS = this.convert((ItemLike)Items.BROWN_MUSHROOM, (ItemLike)Items.WARPED_FUNGUS);
    BaseRecipeProvider.GeneratedRecipe FD = this.moddedConversion(Mods.FD, "tomato", "rotten_tomato");
    BaseRecipeProvider.GeneratedRecipe HH = this.create(Mods.HH.recipeId("rotten_apple"), b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.require((ItemLike)Items.APPLE)).output(Mods.HH, "rotten_apple")).whenModLoaded(Mods.HH.getId()));

    public CreateHauntingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, "create");
    }
}
