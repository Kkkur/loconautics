/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.WeatheringCopper$WeatherState
 */
package com.simibubi.create.api.data.recipe;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipeParams;
import com.simibubi.create.foundation.block.CopperBlockSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.WeatheringCopper;

public abstract class DeployingRecipeGen
extends ProcessingRecipeGen<ItemApplicationRecipeParams, DeployerApplicationRecipe, ItemApplicationRecipe.Builder<DeployerApplicationRecipe>> {
    public BaseRecipeProvider.GeneratedRecipe copperChain(CopperBlockSet set) {
        for (CopperBlockSet.Variant<?> variant : set.getVariants()) {
            ArrayList<Supplier<ItemLike>> chain = new ArrayList<Supplier<ItemLike>>(4);
            ArrayList<Supplier<ItemLike>> waxedChain = new ArrayList<Supplier<ItemLike>>(4);
            for (WeatheringCopper.WeatherState state : WeatheringCopper.WeatherState.values()) {
                waxedChain.add(() -> set.get(variant, state, true).get());
                chain.add(() -> set.get(variant, state, false).get());
            }
            this.oxidizationChain(chain, waxedChain);
        }
        return null;
    }

    public BaseRecipeProvider.GeneratedRecipe addWax(Supplier<ItemLike> waxed, Supplier<ItemLike> nonWaxed) {
        this.createWithDeferredId(this.idWithSuffix(nonWaxed, "_from_removing_wax"), b -> (ItemApplicationRecipe.Builder)((ItemApplicationRecipe.Builder)((ItemApplicationRecipe.Builder)b.require((ItemLike)waxed.get())).require((TagKey<Item>)ItemTags.AXES)).toolNotConsumed().output((ItemLike)nonWaxed.get()));
        return this.createWithDeferredId(this.idWithSuffix(waxed, "_from_adding_wax"), b -> (ItemApplicationRecipe.Builder)((ItemApplicationRecipe.Builder)((ItemApplicationRecipe.Builder)b.require((ItemLike)nonWaxed.get())).require((ItemLike)Items.HONEYCOMB_BLOCK)).toolNotConsumed().output((ItemLike)waxed.get()));
    }

    public BaseRecipeProvider.GeneratedRecipe oxidizationChain(List<Supplier<ItemLike>> chain, List<Supplier<ItemLike>> waxedChain) {
        int i;
        for (i = 0; i < chain.size() - 1; ++i) {
            Supplier<ItemLike> to = chain.get(i);
            Supplier<ItemLike> from = chain.get(i + 1);
            this.createWithDeferredId(this.idWithSuffix(to, "_from_deoxidising"), b -> (ItemApplicationRecipe.Builder)((ItemApplicationRecipe.Builder)((ItemApplicationRecipe.Builder)b.require((ItemLike)from.get())).require((TagKey<Item>)ItemTags.AXES)).toolNotConsumed().output((ItemLike)to.get()));
        }
        for (i = 0; i < chain.size(); ++i) {
            this.addWax(waxedChain.get(i), chain.get(i));
        }
        return null;
    }

    public DeployingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.DEPLOYING;
    }

    @Override
    protected ItemApplicationRecipe.Builder<DeployerApplicationRecipe> getBuilder(ResourceLocation id) {
        return new ItemApplicationRecipe.Builder<DeployerApplicationRecipe>(DeployerApplicationRecipe::new, id);
    }
}
