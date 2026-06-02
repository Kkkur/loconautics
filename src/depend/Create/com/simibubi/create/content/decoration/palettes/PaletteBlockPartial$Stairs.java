/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  com.tterrag.registrate.providers.RegistrateRecipeProvider
 *  com.tterrag.registrate.util.DataIngredient
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  net.minecraft.data.recipes.RecipeCategory
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.StairBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 */
package com.simibubi.create.content.decoration.palettes;

import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import com.simibubi.create.content.decoration.palettes.PaletteBlockPartial;
import com.simibubi.create.content.decoration.palettes.PaletteBlockPattern;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import java.util.Arrays;
import java.util.function.Supplier;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

private static class PaletteBlockPartial.Stairs
extends PaletteBlockPartial<StairBlock> {
    public PaletteBlockPartial.Stairs() {
        super("stairs");
    }

    @Override
    protected StairBlock createBlock(Supplier<? extends Block> block) {
        return new StairBlock(block.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy((BlockBehaviour)((BlockBehaviour)block.get())));
    }

    @Override
    protected void generateBlockState(DataGenContext<Block, StairBlock> ctx, RegistrateBlockstateProvider prov, String variantName, PaletteBlockPattern pattern, Supplier<? extends Block> block) {
        prov.stairsBlock((StairBlock)ctx.get(), this.getTexture(variantName, pattern, 0));
    }

    @Override
    protected Iterable<TagKey<Block>> getBlockTags() {
        return Arrays.asList(BlockTags.STAIRS);
    }

    @Override
    protected Iterable<TagKey<Item>> getItemTags() {
        return Arrays.asList(ItemTags.STAIRS);
    }

    @Override
    protected void createRecipes(AllPaletteStoneTypes type, BlockEntry<? extends Block> patternBlock, DataGenContext<Block, ? extends Block> c, RegistrateRecipeProvider p) {
        RecipeCategory category = RecipeCategory.BUILDING_BLOCKS;
        p.stairs(DataIngredient.items((ItemLike)((Block)patternBlock.get()), (ItemLike[])new Block[0]), category, () -> c.get(), c.getName(), false);
        p.stonecutting(DataIngredient.tag(type.materialTag), category, () -> c.get(), 1);
    }
}
