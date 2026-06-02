/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 *  net.neoforged.neoforge.common.Tags$Items
 */
package com.simibubi.create.foundation.data.recipe;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.recipe.CommonMetal;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

protected static class CreateRecipeProvider.I {
    protected CreateRecipeProvider.I() {
    }

    static TagKey<Item> redstone() {
        return Tags.Items.DUSTS_REDSTONE;
    }

    static TagKey<Item> planks() {
        return ItemTags.PLANKS;
    }

    static TagKey<Item> woodSlab() {
        return ItemTags.WOODEN_SLABS;
    }

    static TagKey<Item> gold() {
        return Tags.Items.INGOTS_GOLD;
    }

    static TagKey<Item> goldSheet() {
        return CommonMetal.GOLD.plates;
    }

    static TagKey<Item> stone() {
        return Tags.Items.STONES;
    }

    static ItemLike andesiteAlloy() {
        return (ItemLike)AllItems.ANDESITE_ALLOY.get();
    }

    static ItemLike shaft() {
        return (ItemLike)AllBlocks.SHAFT.get();
    }

    static ItemLike cog() {
        return (ItemLike)AllBlocks.COGWHEEL.get();
    }

    static ItemLike largeCog() {
        return (ItemLike)AllBlocks.LARGE_COGWHEEL.get();
    }

    static ItemLike andesiteCasing() {
        return (ItemLike)AllBlocks.ANDESITE_CASING.get();
    }

    static ItemLike vault() {
        return (ItemLike)AllBlocks.ITEM_VAULT.get();
    }

    static ItemLike stockLink() {
        return (ItemLike)AllBlocks.STOCK_LINK.get();
    }

    static TagKey<Item> brass() {
        return CommonMetal.BRASS.ingots;
    }

    static TagKey<Item> brassSheet() {
        return CommonMetal.BRASS.plates;
    }

    static TagKey<Item> iron() {
        return Tags.Items.INGOTS_IRON;
    }

    static TagKey<Item> ironNugget() {
        return Tags.Items.NUGGETS_IRON;
    }

    static TagKey<Item> zinc() {
        return CommonMetal.ZINC.ingots;
    }

    static TagKey<Item> ironSheet() {
        return CommonMetal.IRON.plates;
    }

    static TagKey<Item> sturdySheet() {
        return AllTags.AllItemTags.OBSIDIAN_PLATES.tag;
    }

    static ItemLike brassCasing() {
        return (ItemLike)AllBlocks.BRASS_CASING.get();
    }

    static ItemLike cardboard() {
        return (ItemLike)AllItems.CARDBOARD.get();
    }

    static ItemLike railwayCasing() {
        return (ItemLike)AllBlocks.RAILWAY_CASING.get();
    }

    static ItemLike electronTube() {
        return (ItemLike)AllItems.ELECTRON_TUBE.get();
    }

    static ItemLike precisionMechanism() {
        return (ItemLike)AllItems.PRECISION_MECHANISM.get();
    }

    static TagKey<Item> brassBlock() {
        return CommonMetal.BRASS.storageBlocks.items();
    }

    static TagKey<Item> zincBlock() {
        return CommonMetal.ZINC.storageBlocks.items();
    }

    static TagKey<Item> wheatFlour() {
        return AllTags.AllItemTags.WHEAT_FLOURS.tag;
    }

    static TagKey<Item> copper() {
        return Tags.Items.INGOTS_COPPER;
    }

    static TagKey<Item> copperNugget() {
        return CommonMetal.COPPER.nuggets;
    }

    static TagKey<Item> copperBlock() {
        return Tags.Items.STORAGE_BLOCKS_COPPER;
    }

    static TagKey<Item> copperSheet() {
        return CommonMetal.COPPER.plates;
    }

    static TagKey<Item> brassNugget() {
        return CommonMetal.BRASS.nuggets;
    }

    static TagKey<Item> zincNugget() {
        return CommonMetal.ZINC.nuggets;
    }

    static ItemLike copperCasing() {
        return (ItemLike)AllBlocks.COPPER_CASING.get();
    }

    static ItemLike refinedRadiance() {
        return (ItemLike)AllItems.REFINED_RADIANCE.get();
    }

    static ItemLike shadowSteel() {
        return (ItemLike)AllItems.SHADOW_STEEL.get();
    }

    static Ingredient netherite() {
        return Ingredient.of((TagKey)Tags.Items.INGOTS_NETHERITE);
    }
}
