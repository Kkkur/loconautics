/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataGenerator
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.recipes.RecipeOutput
 *  net.minecraft.data.recipes.RecipeProvider
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
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.foundation.data.recipe.CommonMetal;
import com.simibubi.create.foundation.data.recipe.CreateCompactingRecipeGen;
import com.simibubi.create.foundation.data.recipe.CreateCrushingRecipeGen;
import com.simibubi.create.foundation.data.recipe.CreateCuttingRecipeGen;
import com.simibubi.create.foundation.data.recipe.CreateDeployingRecipeGen;
import com.simibubi.create.foundation.data.recipe.CreateEmptyingRecipeGen;
import com.simibubi.create.foundation.data.recipe.CreateFillingRecipeGen;
import com.simibubi.create.foundation.data.recipe.CreateHauntingRecipeGen;
import com.simibubi.create.foundation.data.recipe.CreateItemApplicationRecipeGen;
import com.simibubi.create.foundation.data.recipe.CreateMillingRecipeGen;
import com.simibubi.create.foundation.data.recipe.CreateMixingRecipeGen;
import com.simibubi.create.foundation.data.recipe.CreatePolishingRecipeGen;
import com.simibubi.create.foundation.data.recipe.CreatePressingRecipeGen;
import com.simibubi.create.foundation.data.recipe.CreateWashingRecipeGen;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

public final class CreateRecipeProvider
extends RecipeProvider {
    static final List<ProcessingRecipeGen<?, ?, ?>> GENERATORS = new ArrayList();
    static final int BUCKET = 1000;
    static final int BOTTLE = 250;

    public CreateRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    protected void buildRecipes(RecipeOutput recipeOutput) {
    }

    public static void registerAllProcessing(DataGenerator gen, PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        GENERATORS.add(new CreateCrushingRecipeGen(output, registries));
        GENERATORS.add(new CreateMillingRecipeGen(output, registries));
        GENERATORS.add(new CreateCuttingRecipeGen(output, registries));
        GENERATORS.add(new CreateWashingRecipeGen(output, registries));
        GENERATORS.add(new CreatePolishingRecipeGen(output, registries));
        GENERATORS.add(new CreateDeployingRecipeGen(output, registries));
        GENERATORS.add(new CreateMixingRecipeGen(output, registries));
        GENERATORS.add(new CreateCompactingRecipeGen(output, registries));
        GENERATORS.add(new CreatePressingRecipeGen(output, registries));
        GENERATORS.add(new CreateFillingRecipeGen(output, registries));
        GENERATORS.add(new CreateEmptyingRecipeGen(output, registries));
        GENERATORS.add(new CreateHauntingRecipeGen(output, registries));
        GENERATORS.add(new CreateItemApplicationRecipeGen(output, registries));
        gen.addProvider(true, new DataProvider(){

            public String getName() {
                return "Create's Processing Recipes";
            }

            public CompletableFuture<?> run(CachedOutput dc) {
                return CompletableFuture.allOf((CompletableFuture[])GENERATORS.stream().map(gen -> gen.run(dc)).toArray(CompletableFuture[]::new));
            }
        });
    }

    protected static class I {
        protected I() {
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
}
