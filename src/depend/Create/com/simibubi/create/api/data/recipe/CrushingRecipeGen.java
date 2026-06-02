/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.tags.TagKey
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 *  net.neoforged.neoforge.common.conditions.ICondition
 *  net.neoforged.neoforge.common.conditions.NotCondition
 *  net.neoforged.neoforge.common.conditions.TagEmptyCondition
 */
package com.simibubi.create.api.data.recipe;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.data.recipe.CommonMetal;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;

public abstract class CrushingRecipeGen
extends StandardProcessingRecipeGen<CrushingRecipe> {
    protected BaseRecipeProvider.GeneratedRecipe mineralRecycling(AllPaletteStoneTypes type, Supplier<ItemLike> crushed, Supplier<ItemLike> nugget, float chance) {
        return this.mineralRecycling(type, b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.duration(250)).output(chance, (ItemLike)crushed.get(), 1)).output(chance, (ItemLike)nugget.get(), 1));
    }

    protected BaseRecipeProvider.GeneratedRecipe mineralRecycling(AllPaletteStoneTypes type, UnaryOperator<StandardProcessingRecipe.Builder<CrushingRecipe>> transform) {
        this.create(Lang.asId((String)type.name()) + "_recycling", b -> (StandardProcessingRecipe.Builder)transform.apply((StandardProcessingRecipe.Builder)b.require(type.materialTag)));
        return this.create(() -> type.getBaseBlock().get(), transform);
    }

    protected BaseRecipeProvider.GeneratedRecipe stoneOre(Supplier<ItemLike> ore, Supplier<ItemLike> raw, float expectedAmount, int duration) {
        return this.ore((ItemLike)Blocks.COBBLESTONE, ore, raw, expectedAmount, duration);
    }

    protected BaseRecipeProvider.GeneratedRecipe deepslateOre(Supplier<ItemLike> ore, Supplier<ItemLike> raw, float expectedAmount, int duration) {
        return this.ore((ItemLike)Blocks.COBBLED_DEEPSLATE, ore, raw, expectedAmount, duration);
    }

    protected BaseRecipeProvider.GeneratedRecipe netherOre(Supplier<ItemLike> ore, Supplier<ItemLike> raw, float expectedAmount, int duration) {
        return this.ore((ItemLike)Blocks.NETHERRACK, ore, raw, expectedAmount, duration);
    }

    protected BaseRecipeProvider.GeneratedRecipe ore(ItemLike stoneType, Supplier<ItemLike> ore, Supplier<ItemLike> raw, float expectedAmount, int duration) {
        return this.create(ore, b -> {
            ((StandardProcessingRecipe.Builder)b.duration(duration)).output((ItemLike)raw.get(), Mth.floor((float)expectedAmount));
            float extra = expectedAmount - (float)Mth.floor((float)expectedAmount);
            if (extra > 0.0f) {
                b.output(extra, (ItemLike)raw.get(), 1);
            }
            b.output(0.75f, (ItemLike)AllItems.EXP_NUGGET.get(), raw.get() == AllItems.CRUSHED_GOLD.get() ? 2 : 1);
            return (StandardProcessingRecipe.Builder)b.output(0.125f, stoneType);
        });
    }

    protected BaseRecipeProvider.GeneratedRecipe moddedOre(CommonMetal metal, Supplier<ItemLike> result) {
        TagKey<Item> tag = metal.ores.items();
        return this.create(String.valueOf((Object)metal) + "_ore", b -> (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.duration(400)).withCondition((ICondition)new NotCondition((ICondition)new TagEmptyCondition(tag.location())))).require(tag)).output((ItemLike)result.get(), 1)).output(0.75f, (ItemLike)result.get(), 1)).output(0.75f, (ItemLike)AllItems.EXP_NUGGET.get()));
    }

    protected BaseRecipeProvider.GeneratedRecipe rawOre(String metalName, Supplier<TagKey<Item>> input, Supplier<ItemLike> result, int xpMult) {
        return this.rawOre(metalName, input, result, false, xpMult);
    }

    protected BaseRecipeProvider.GeneratedRecipe rawOreBlock(String metalName, Supplier<TagKey<Item>> input, Supplier<ItemLike> result, int xpMult) {
        return this.rawOre(metalName, input, result, true, xpMult);
    }

    protected BaseRecipeProvider.GeneratedRecipe rawOre(String metalName, Supplier<TagKey<Item>> input, Supplier<ItemLike> result, boolean block, int xpMult) {
        return this.create("raw_" + metalName + (block ? "_block" : ""), b -> {
            int amount = block ? 9 : 1;
            return (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.duration(400)).require((TagKey<Item>)((TagKey)input.get()))).output((ItemLike)result.get(), amount)).output(0.75f, (ItemLike)AllItems.EXP_NUGGET.get(), amount * xpMult);
        });
    }

    protected BaseRecipeProvider.GeneratedRecipe moddedRawOre(CommonMetal metal, Supplier<ItemLike> result) {
        return this.moddedRawOre(metal, result, false);
    }

    protected BaseRecipeProvider.GeneratedRecipe moddedRawOreBlock(CommonMetal metal, Supplier<ItemLike> result) {
        return this.moddedRawOre(metal, result, true);
    }

    protected BaseRecipeProvider.GeneratedRecipe moddedRawOre(CommonMetal metal, Supplier<ItemLike> result, boolean block) {
        return this.create("raw_" + String.valueOf((Object)metal) + (block ? "_block" : ""), b -> {
            int amount = block ? 9 : 1;
            TagKey<Item> material = block ? metal.rawStorageBlocks.items() : metal.rawOres;
            return (StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)b.duration(400)).withCondition((ICondition)new NotCondition((ICondition)new TagEmptyCondition(material.location())))).require(material)).output((ItemLike)result.get(), amount)).output(0.75f, (ItemLike)AllItems.EXP_NUGGET.get(), amount);
        });
    }

    public CrushingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.CRUSHING;
    }
}
